package com.bigdata2017.smartcar.storm;

import java.util.Arrays;
import java.util.UUID;

import org.apache.storm.redis.common.config.JedisPoolConfig;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

public class SmartCarDrivingTopology {

	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
		// 환경 설정
		Config config = new Config();
		
		config.setDebug( true );
		config.put( Config.NIMBUS_HOST, "lx02.hadoop.com" );
		config.put( Config.NIMBUS_THRIFT_PORT, 6627 );
		config.put( Config.STORM_ZOOKEEPER_PORT, 2181 );
		config.put( Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList( "lx02.hadoop.com" ) );	

		// 토폴로지 등록
		StormSubmitter.submitTopology( args[0], config, makeTopology() );
	}
	
	private static StormTopology makeTopology() {
		TopologyBuilder topologyBuilder = new TopologyBuilder();
		
		// Spout(Kafka) 생성 및 등록
		BrokerHosts brokerHosts = new ZkHosts( "lx02.hadoop.com:2181" );
		String topicName = "SmartCar-Topic";
		String zookeeperPathName = "/SmartCar-Topic";

		SpoutConfig spoutConf = new SpoutConfig( brokerHosts, topicName, zookeeperPathName, UUID.randomUUID().toString() );
		spoutConf.scheme = new SchemeAsMultiScheme( new StringScheme() );
		KafkaSpout kafkaSpout = new KafkaSpout( spoutConf );
		
		topologyBuilder.setSpout( "kafkaSpout", kafkaSpout, 1 );
		
		// Grouping [kafkaSpout -> splitBolt] 
		topologyBuilder.setBolt( "splitBolt", new SplitBolt(), 1 ).allGrouping( "kafkaSpout" );
		// Subgrouping [splitBolt -> hbaseBolt]
		topologyBuilder.setBolt( "hbaseBolt", new HBaseBolt(), 1 ).shuffleGrouping( "splitBolt" );
		
		
		// Grouping [kafkaSpout -> esperBolt]
		topologyBuilder.setBolt( "esperBolt", new EsperBolt(), 1 ).allGrouping( "kafkaSpout" );		
		// Subgrouping [esperBolt -> redisBolt]
		JedisPoolConfig jedisPoolConfig =
			new JedisPoolConfig.
			Builder().
			setHost( "lx02.hadoop.com" ).
			setPort( 6379 ).
			build();
		topologyBuilder.setBolt( "redisBolt", new RedisBolt( jedisPoolConfig ), 1 ).shuffleGrouping( "esperBolt" );
		
		// 토폴로지 생성
		return topologyBuilder.createTopology();
	}
}
