package gsn.wrappers;

import java.io.File;
import java.io.Serializable;

import org.apache.log4j.Logger;

import gsn.beans.AddressBean;
import gsn.beans.DataField;
import gsn.beans.DataTypes;
import gsn.beans.StreamElement;
import gsn.http.rest.EventQueue;
import gsn.utils.ParamParser;
import gsn.wrappers.AbstractWrapper;

public class RemoteEventPushWrapper extends AbstractWrapper{
	
	private static final int            DEFAULT_SAMPLING_RATE       = 1000;
	private int                         samplingRate                = DEFAULT_SAMPLING_RATE;
	private final transient Logger      logger                      = Logger.getLogger(RemoteEventPushWrapper.class);
	private static int                  threadCounter               = 0;
	private final static String 		DF_NAME						= "Accelerometer";
	private final static String[]		FIELD_NAMES					= new String[] {DF_NAME};
	// TODO update me with all the phone sensors
	private transient DataField[]       outputStructureCache        = new DataField[]{new DataField(DF_NAME, "bigint", "Accelerometer coordinates")};
	
	@Override
	public DataField[] getOutputFormat() {
		return outputStructureCache;
	}

	@Override
	public boolean initialize() {
		
		setName("RemoteEventPushWrapper-Thread" + (++threadCounter));
		AddressBean addressBean = getActiveAddressBean();
		if(addressBean.getPredicateValue("sampling-rate") != null){
			samplingRate = ParamParser.getInteger(addressBean.getPredicateValue("sampling-rate"), DEFAULT_SAMPLING_RATE);
		}
		if(samplingRate <= 0){
			logger.warn("Sampling rate for RemoteEvent should be greather than 0.");
			samplingRate = DEFAULT_SAMPLING_RATE;
		}
		return true;
	}
	
	public void run(){
		String data = "no data";
		while(isActive()){
            try{
                //Thread.sleep(samplingRate);
            	Thread.sleep(30000);
            }catch (InterruptedException e){
                logger.error(e.getMessage(), e);
            }
            
            if(EventQueue.getInstance().isEmpty()){
            	// ...
            }else{
            	data = EventQueue.getInstance().removeData();
            }
         
            logger.error("data here in run of the wrapper" + data);
            
            StreamElement streamElement = new StreamElement(FIELD_NAMES, 
            												new Byte[]{DataTypes.BIGINT}, 
            												new Serializable[] {123},
            												System.currentTimeMillis());
            postStreamElement(streamElement);
        }
	}
	

	@Override
	public void dispose() {
		threadCounter--;		
	}

	@Override
	public String getWrapperName() {
		return "Remote Event Push Wrapper";
	}

}
