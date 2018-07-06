package com.bigdata2017.smartcar.storm;

import java.util.Map;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class EsperBolt extends BaseBasicBolt {
	
	private static final long serialVersionUID = 1L;
	
	private static final int MAX_SPEED = 30;
	private static final int DURATION_ESTIMATE = 30;
	
	private EPServiceProvider espService;
	private boolean isOverSpeedEvent = false;

	@Override
	public void prepare(Map stormConf, TopologyContext context) {

		Configuration configuration = new Configuration();
		configuration.addEventType( "DrivingInfo", DrivingInfo.class.getName() );

		espService = EPServiceProviderManager.getDefaultProvider( configuration );
		espService.initialize();
		
		String eplOverSpeed =  
				"	SELECT	date,"			+ 
		        "			carNumber,"		+
		        "			speedPedal,"	+
		        "			breakPedal,"	+
				"			steerAngle,"	+
				"			directLight,"	+
				"			speed,"			+
				"			areaNumber"     +
				"     FROM	DrivingInfo.win:time_batch(" + DURATION_ESTIMATE + " sec) " +
				" GROUP BY	carNumber"      +
				"   HAVING  AVG(speed) > "  + MAX_SPEED;
		EPStatement stmtESP = espService.getEPAdministrator().createEPL( eplOverSpeed );
		stmtESP.addListener( new UpdateListener(){
			@Override
			public void update( EventBean[] newEvents, EventBean[] oldEvents ) {
				if( newEvents != null ) {
					isOverSpeedEvent = true;
				}
			}
		});
	}
	
	@Override
	public void execute( Tuple tuple, BasicOutputCollector collector ) {

		String tValue = tuple.getString(0); 

		//발생일시(14자리), 차량번호, 가속페달, 브레이크페달, 운전대회적각, 방향지시등, 주행속도, 주행지역
		String[] receiveData = tValue.split("\\,");

		DrivingInfo drivingInfo = new DrivingInfo();
		drivingInfo.setDate( receiveData[0] );
		drivingInfo.setCarNumber( receiveData[1] );
		drivingInfo.setSpeedPedal( receiveData[2] );
		drivingInfo.setBreakPedal( receiveData[3] );
		drivingInfo.setSteerAngle( receiveData[4] );
		drivingInfo.setDirectLight( receiveData[5] );
		drivingInfo.setSpeed( Integer.parseInt( receiveData[6] ) );
		drivingInfo.setAreaNumber( receiveData[7] );

		espService.getEPRuntime().sendEvent( drivingInfo ); 

		//LOGGER.error( "sendEvent:" + drivingInfo.toString() );
		
		if( isOverSpeedEvent ) {
			//발생일시(14자리), 차량번호
			collector.emit( new Values( drivingInfo.getDate().substring(0,8), 
										drivingInfo.getCarNumber() + "-" + drivingInfo.getDate() ) );
			isOverSpeedEvent = false;
		}		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare( new Fields( "date", "car_number" ) );	
	}
	
	public class DrivingInfo {
		private String date;
		private String carNumber;
		private String speedPedal;
		private String breakPedal;
		private String steerAngle;
		private String directLight;
		private int speed;
		private String areaNumber;
		
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getCarNumber() {
			return carNumber;
		}
		public void setCarNumber(String carNumber) {
			this.carNumber = carNumber;
		}
		public String getSpeedPedal() {
			return speedPedal;
		}
		public void setSpeedPedal(String speedPedal) {
			this.speedPedal = speedPedal;
		}
		public String getBreakPedal() {
			return breakPedal;
		}
		public void setBreakPedal(String breakPedal) {
			this.breakPedal = breakPedal;
		}
		public String getSteerAngle() {
			return steerAngle;
		}
		public void setSteerAngle(String steerAngle) {
			this.steerAngle = steerAngle;
		}
		public String getDirectLight() {
			return directLight;
		}
		public void setDirectLight(String directLight) {
			this.directLight = directLight;
		}
		public int getSpeed() {
			return speed;
		}
		public void setSpeed(int speed) {
			this.speed = speed;
		}
		public String getAreaNumber() {
			return areaNumber;
		}
		public void setAreaNumber(String areaNumber) {
			this.areaNumber = areaNumber;
		}
		@Override
		public String toString() {
			return "DrivingInfo [date=" + date + ", carNumber=" + carNumber + ", speedPedal=" + speedPedal
					+ ", breakPedal=" + breakPedal + ", steerAngle=" + steerAngle + ", directLight=" + directLight
					+ ", speed=" + speed + ", areaNumber=" + areaNumber + "]";
		}
	}
}
