package gsn.wrappers;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import gsn.beans.AddressBean;
import gsn.beans.DataField;
import gsn.beans.DataTypes;
import gsn.beans.StreamElement;
import gsn.wrappers.AbstractWrapper;
import gsn.utils.ParamParser;

import org.apache.log4j.Logger;

import gsn.beans.AddressBean;
import gsn.beans.DataField;

public class HttpPostAndroidWrapper extends AbstractWrapper {
	private int DEFAULT_RATE = 2000;
	private static int threadCounter = 0;
	private final transient Logger logger = Logger.getLogger(HttpPostAndroidWrapper.class);
	private static String urlPath, longitude, latitude;
	private AddressBean addressBean;
	private String inputRate;
	//private static final String FIELD_NAME_LONG = "Longitude";
	//private static final String FIELD_NAME_LAT = "Latitude";
	// TODO add those tooprivate static final String[] FIELD_NAMES = new String[] {  };
	private static final String       POSITION_X                       = "POSITION_X";
	   
	private static final String       POSITION_Y                   = "POSITION_Y";
	   
	private static final String       POSITION_Z = "POSITION_Z";        

	private static final String [ ]   FIELD_NAMES                           = new String [ ] { POSITION_X, POSITION_Y, POSITION_Z};
	   


	
	private int rate;
	public boolean requestCame = false;
	@Override
	public DataField[] getOutputFormat() {
		// TODO return here json in the DataField[] format
		return null;
	}

	public void run(){
		while (isActive()){
			synchronized (HttpPostAndroidWrapper.class) {
				while (!requestCame) {
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// i got notified - i'm no longer waiting?
				// should this be out of synchronized?


//				 StreamElement streamElement = new StreamElement( 
//
//				FIELD_NAMES , 
//				new Byte [ ] { DataTypes.BIGINT , DataTypes.BIGINT , DataTypes.BIGINT } , 
//				new Serializable [ ] { putx ,  puty, putz} , System.currentTimeMillis( ) );
//
//
//				     postStreamElement( streamElement );
//				     }
			}		
		}
	}
	
	
	
	@Override
	public boolean initialize() {
	      this.addressBean =getActiveAddressBean( );
//	      urlPath = this.addressBean.getPredicateValue( "url" );
//	      try 
//		  {
//			url = new URL(urlPath);
//		  } 
//			catch (MalformedURLException e) 
//		  {
//			logger.error("Loading the http android wrapper failed : "+e.getMessage(),e);
//			return false;
//		  }
	      
		  inputRate = this.addressBean.getPredicateValue( "rate" );
	      if ( inputRate == null || inputRate.trim( ).length( ) == 0 ) rate = DEFAULT_RATE;
	      else
	         rate = Integer.parseInt( inputRate );
	      setName( "AndroidWrapper-Thread" + ( ++threadCounter ) );
	      if ( logger.isDebugEnabled( ) ) logger.debug( "HttpPostAndroidWrapper is now running @" + rate + " Rate." );
	      return true;
	}

	@Override
	public void dispose() {
		threadCounter--;
	}

	@Override
	public String getWrapperName() {
		return "Android Wrapper";
	}

}
