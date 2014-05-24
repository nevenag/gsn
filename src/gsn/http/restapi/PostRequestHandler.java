package gsn.http.restapi;

import gsn.Mappings;
import gsn.beans.DataField;
import gsn.beans.VSensorConfig;

import java.util.Iterator;

import org.apache.commons.collections.KeyValue;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/*
 * helper class for handling the post requests from the RestServlet.java
 * 
 * includes json handling that should be moved to util package
 * 
 * @author nevena
 * 
 * */

public class PostRequestHandler {
	
	// ideally get the sensors name from the request
	// grab the sensors wrapper
	// read its format and 
	// parse json
	
	
    public String postSensorsInfoAsJSON() {

        JSONArray sensorsInfo = new JSONArray();

        Iterator<VSensorConfig> vsIterator = Mappings.getAllVSensorConfigs();

        while (vsIterator.hasNext()) {

            JSONObject aSensor = new JSONObject();

            VSensorConfig sensorConfig = vsIterator.next();

            String vs_name = sensorConfig.getName();

            aSensor.put("name", vs_name);

            JSONArray listOfFields = new JSONArray();

            for (DataField df : sensorConfig.getOutputStructure()) {

                String field_name = df.getName().toLowerCase();
                String field_type = df.getType().toLowerCase();
                String field_unit = df.getUnit();
                if (field_unit == null || field_unit.trim().length() == 0)
                    field_unit = "";

                if (field_type.indexOf("double") >= 0) {
                    //listOfFields.add(field_name);
                    JSONObject field = new JSONObject();
                    field.put("name", field_name);
                    field.put("unit", field_unit);
                    listOfFields.add(field);
                }
            }

            aSensor.put("fields", listOfFields);

            Double alt = 0.0;
            Double lat = 0.0;
            Double lon = 0.0;

            for (KeyValue df : sensorConfig.getAddressing()) {

                String adressing_key = df.getKey().toString().toLowerCase().trim();
                String adressing_value = df.getValue().toString().toLowerCase().trim();

                if (adressing_key.indexOf("altitude") >= 0)
                    alt = Double.parseDouble(adressing_value);

                if (adressing_key.indexOf("longitude") >= 0)
                    lon = Double.parseDouble(adressing_value);

                if (adressing_key.indexOf("latitude") >= 0)
                    lat = Double.parseDouble(adressing_value);
            }

            aSensor.put("lat", lat);
            aSensor.put("lon", lon);
            aSensor.put("alt", alt);

            sensorsInfo.add(aSensor);

        }

        return sensorsInfo.toJSONString();
    }


}
