package gsn.wrappers;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
	private static final String FIELD_NAME_LONG = "Longitude";
	private static final String FIELD_NAME_LAT = "Latitude";
	private static final String[] FIELD_NAMES = new String[] { FIELD_NAME_LONG,			FIELD_NAME_LAT };
	private int rate;
	public boolean requestCame = false;
	@Override
	public DataField[] getOutputFormat() {
		// TODO return here json in the DataField[] format
		return null;
	}

	public void run(){
		synchronized(HttpPostAndroidWrapper.class){
			while(!requestCame){
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
