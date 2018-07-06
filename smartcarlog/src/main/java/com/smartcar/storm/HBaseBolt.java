package com.bigdata2017.smartcar.storm;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

@SuppressWarnings("serial")
public class HBaseBolt implements IRichBolt {

	private static final String TABLE_NAME = "table_smartcar_driving";
	private static final String ZOOKEEPER_QUORUM = "lx02.hadoop.com";
	private static final String ZOOKEEPER_CLIENT_PORT = "2181";

	private static final String TUPLE_ROW_KEY_FIELD = "r_key";		
	private static final String COLUMN_FAMILY = "cf";
	private static final boolean IS_BATCH = false;
	
	private static final String[] COLUMN_NAMES = { "date", "car_number", "speed_pedal", "break_pedal", "steer_angle", "direct_light", "speed", "area_number" };
	
	private HTable hTable;
	private OutputCollector collector;

	@Override
	@SuppressWarnings( "rawtypes" )	
	public void prepare( Map map, TopologyContext context, OutputCollector collector ) {
		try {
			// 
			this.collector = collector;
			
			// HBase연결(HTable 연결) 
			Configuration config = HBaseConfiguration.create();
			config.set( "hbase.zookeeper.quorum", ZOOKEEPER_QUORUM );
			config.set( "hbase.zookeeper.property.clientPort", ZOOKEEPER_CLIENT_PORT );
			config.set( "hbase.cluster.distributed", "true" );
	
			hTable = new HTable( config, TABLE_NAME );
	
			if ( IS_BATCH ) {
				hTable.setAutoFlush( false, true );
			}
	
			// 컬럼 패밀리 존재 여부
			if( hTable.getTableDescriptor().hasFamily( Bytes.toBytes( COLUMN_FAMILY ) ) == false ) {
				throw new RuntimeException( String.format( "HBase table '%s' does not have column family '%s'", TABLE_NAME, COLUMN_FAMILY ) );
			}
			
		} catch( IOException e ) {
			throw new RuntimeException( e );
		}
	}
	
	@Override
	public void execute(Tuple tuple) {
		try {
			if( hTable != null ) {
				hTable.put( getPutFromTuple( tuple ) );
			}
			collector.ack( tuple );
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

	}

	@Override
	public void cleanup() {
		try {
			if( hTable != null ) {
				hTable.close();
			}
		} catch( IOException ex ) {
			ex.printStackTrace();
		}		
	}
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}
	
	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}
	
	private Put getPutFromTuple( final Tuple tuple ) {
		long ts = 0;
		byte[] rowKey = Bytes.toBytes( tuple.getStringByField( TUPLE_ROW_KEY_FIELD ) );

		Put put = new Put( rowKey );
		put.setWriteToWAL(true);

		byte[] cfBytes = Bytes.toBytes( COLUMN_FAMILY );
		
		for( String cq : COLUMN_NAMES ) {
			byte[] cqBytes = Bytes.toBytes( cq );
			byte[] val = Bytes.toBytes( tuple.getStringByField( cq ) );
			
			if (ts > 0) {
				put.add( cfBytes, cqBytes, ts, val );
			} else {
				put.add( cfBytes, cqBytes, val );
			}
		}

		return put;
	}
}