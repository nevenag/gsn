package org.androgsn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends Activity implements SensorEventListener {

	// place here the public IP from the server where the GSN is running
	private static String host = "http://ec2-54-187-208-40.us-west-2.compute.amazonaws.com:22001/rest/sensors";

	private SensorManager sensorManager;
	private long lastUpdate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		System.out.println("created");
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	}

	// Whenever any sensor changes its values within 
	// and after no less than 20 seconds 
	// send the update to GSN

	@Override
	public void onSensorChanged(SensorEvent event) {
		long actualTime = event.timestamp;
		long interval = 2000000000;
		// 20 seconds
		if ((actualTime - lastUpdate) / 10 < interval) {
			return;
		}
		lastUpdate = actualTime;
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			JSONObject jo = getAllSensorsData(event);
			sendPostRequest(jo);
		}
		/*
		 * if (event.sensor.getType() == Sensor.TYPE_LIGHT) { getLight(event); }
		 * if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
		 * getProximity(event); }
		 */
	}

	private JSONObject getAllSensorsData(SensorEvent event) {
		JSONObject object = new JSONObject();
		//JSONArray jaPressure = new JSONArray();
		//JSONArray jaLight = new JSONArray();
		JSONArray jaAccelerometer = getAccelerometerDataFields(event);
//		JSONArray fields = new JSONArray();
		try {
			object.put("vsname", "AndroidSensor");
			object.put("processing-class", "gsn.vsensor.BridgeVirtualSensor");
			object.put("description", "accelometer sensor");
			object.put("latitude", "-30.0");
			object.put("longitude", "150.0");
			//fields.put(jaPressure);
			//fields.put(jaLight);
			//fields.put(jaAccelerometer);
			object.put("fields", jaAccelerometer);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return object;
	}
	
	private JSONArray getLightDataFields(SensorEvent event) {
		float[] values = event.values;
		JSONArray ja = new JSONArray();
		try {
			JSONObject jox = new JSONObject();
			jox.put("name", "light_x");
			jox.put("type", "double");
			jox.put("value", Double.valueOf(values[0]));
			ja.put(jox);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ja;
	}
	
	private JSONArray getPressureDataFields(SensorEvent event) {
		float[] values = event.values;
		JSONArray ja = new JSONArray();
		try {
			JSONObject jox = new JSONObject();
			jox.put("name", "pressure_x");
			jox.put("type", "double");
			jox.put("value", Double.valueOf(values[0]));
			ja.put(jox);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ja;
	}
	
	private JSONArray getAccelerometerDataFields(SensorEvent event) {
		float[] values = event.values;
		// Accelerometer sensor has 3 values.
		JSONArray ja = new JSONArray();
		try {
			JSONObject jox = new JSONObject();
			jox.put("name", "accelerometer_x");
			jox.put("type", "double");
			jox.put("value", Double.valueOf(values[0]));

			JSONObject joy = new JSONObject();
			joy.put("name", "accelerometer_y");
			joy.put("type", "double");
			joy.put("value", Double.valueOf(values[1]));

			JSONObject joz = new JSONObject();
			joz.put("name", "accelerometer_z");
			joz.put("type", "double");
			joz.put("value", Double.valueOf(values[2]));

			ja.put(jox);
			ja.put(joy);
			ja.put(joz);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ja;
	}
	
	

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	protected void onResume() {
		super.onResume();
		// register this class as a listener for the 
		// pressure, light and accelerometer sensors
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		// unregister listener to save the battery
		super.onPause();
		sensorManager.unregisterListener(this);
	}

	// ----------------------

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
/*
 // use this method to let user upload the sensor data manually
	public void sendData(View view) {
		System.out.println("send");
		// crate new thread to async. wait for a response from the server
		new Thread(new Runnable() {
			public void run() {
				sendPostRequest();
				sendGetRequest();
			}
		}).start();
	}
*/
	private void sendPostRequest(JSONObject jo) {
		final HttpClient client = new DefaultHttpClient();
		final HttpPost post = new HttpPost(host);
		String message;

		try {
			message = jo.toString();
			post.setEntity(new StringEntity(message, "UTF8"));
			post.setHeader("Content-type", "application/json");
			System.out.println("request sent");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		new Thread(new Runnable() {
			public void run() {

		System.out.println("post response");
		try {
			client.execute(post);
		} catch (IOException e) {
			e.printStackTrace();
		}
			}
		}).start();

	}

	private void sendGetRequest() {
		HttpResponse response = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(host));
			response = client.execute(request);
		} catch (URISyntaxException e) {
			System.out.println("URISE" + e.getReason());
		} catch (ClientProtocolException e) {
			System.out.println("CPE" + e.getMessage());
		} catch (IOException e) {
			System.out.println("IOE" + e.getMessage());
		} catch (Exception e) {
			System.out.println("E" + e.getMessage());
		} finally {

			// free resources
		}
		System.out.println("get response is " + response.getStatusLine().getStatusCode());

	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
