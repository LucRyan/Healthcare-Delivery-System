package Final.Ryan.Location;
import Final.Ryan.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.impl.io.ChunkedInputStream;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import Final.Ryan.Patient.Patient;
import Final.Ryan.Patient.PatientDbAdapter;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ListView;

public class ServiceHelper extends Service{  
	
	private static final String TAG = "ServiceHelper";
	private static final String DATABASE_TABLE_SEE = "ToBeSee";
	Patient patient;
	ArrayList<Patient> patients = new ArrayList<Patient>();
	
	public void onCreate() {  
        Log.d(TAG, "onCreate");
    }  
   
  
    @Override  
    public void onDestroy() {   
        Log.d(TAG, "onDestroy");  
        super.onDestroy();
    }  
      
    @Override  
    public void onStart(Intent intent, int startid) {  
        Log.d(TAG, "onStart"); 
    }
    
	Bundle nextMessage(DatagramSocket serverSocket, boolean socketOK, String username, String ipaddress) {
		byte[] receiveData = new byte[1024];
		Bundle info = null;
		DatagramPacket receivePacket = new DatagramPacket(receiveData,
				receiveData.length);
		try {
            serverSocket.receive(receivePacket);
            String output = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
            Log.i(TAG, "Receive packet: " + output + ". From: " + receivePacket.getAddress());
            /*
             * TODO add the database fill data function
             */
            //if (output == "done"){
            info = getXMLWebService(username, ipaddress);
            //}
            
		} catch (Exception e) {
			Log.e(TAG, "Problems receiving packet: " + e.getMessage());
			socketOK = false;
		} // end catch
		return info;
	}
	
	Intent broadcastMessages(Bundle info){
		Intent intent = new Intent();
		intent.putExtras(info);
		//Log.i(TAG, info.getString("pName"));
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.setAction("android.intent.action.receiveMessages");
		return intent;
	}
	
	
	boolean isTopActivity(Service service, String className){
		ActivityManager am = (ActivityManager)service.getSystemService(Activity.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		Log.d(TAG, "pkg:"+cn.getPackageName());
		Log.d(TAG, "cls:"+cn.getClassName());
		if(className.equals(cn.getClassName())){
			return true;
		}
		return false;
    }
	
	

	private Bundle getXMLWebService(String username, String ipaddress) {
		Bundle info = new Bundle();
		try {
			Log.i(TAG, "Start GET XML");
			
			URL searchUrl = new URL("http://"+ipaddress+":9090/clinic?chw="+ username);
			Log.i(TAG, searchUrl.toString());
			
			// TODO: Perform the http request (use HttpURLConnection)
			HttpURLConnection connection = null;
			
			// =========================================================
			// Set up the initial connection
	        connection = (HttpURLConnection)searchUrl.openConnection();
	        connection.setRequestMethod("GET");
	        connection.setDoOutput(true);
	        connection.setReadTimeout(10000);            
	        connection.connect();
	        
	        int responseCode = 0;
			responseCode = connection.getResponseCode(); // get the response code
			//Toast.makeText(this, responseCode + "", Toast.LENGTH_LONG).show();
			Log.i(TAG, String.valueOf(responseCode));
			
			if (responseCode == HttpURLConnection.HTTP_OK) {
				
				Log.i(TAG, "HTTP_OK");
				InputStream response = searchUrl.openStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(response));
				ArrayList <String> jsonTxt = new ArrayList<String>();
				for(String line;(line = reader.readLine()) != null;){
					jsonTxt.add(line);
				}
				reader.close();
                
				final int[] mPics = new int[]{
	                   	 R.drawable.pic1, R.drawable.pic2, R.drawable.pic3, R.drawable.pic4, R.drawable.pic5,
	                   	 R.drawable.pic6, R.drawable.pic7, R.drawable.pic8, R.drawable.pic9, R.drawable.pic10,
	                   	 R.drawable.pic11  	 
	                   };
                
				for(int i = 0; i< jsonTxt.size(); i++){
					System.out.println(jsonTxt.get(i));
					Log.i(TAG, i + ":" + jsonTxt.get(i));
					System.out.println(jsonTxt);
					
	                if(jsonTxt.get(i).length() > 10){
	            		
	                	Log.i(TAG, "line != null");
	                	Log.i(TAG, "Length:" + String.valueOf(jsonTxt.get(i).length()));
	                	
	                    JSONObject json = new JSONObject(jsonTxt.get(i));
	                    JSONObject patient = json.getJSONObject("patientRepresentation");
	                    int pId = patient.getInt("id");
	                    String pName = patient.getString("name");
	                    String pLongitude = patient.getString("longitude");
	                    String pLatitude = patient.getString("latitude");
	                    Log.i(TAG, "Id:"+pId +" Name:"+pName + " Long:"+pLongitude +" La:"+pLatitude);
	                    //patients.add(new Patient(pId, mPics[pId%11], pName, pLatitude, pLongitude));
	                    info.putString("pName", pName);
	                    info.putInt("pId", pId);
	                    info.putString("pLongitude", pLongitude);
	                    info.putString("pLatitude", pLatitude);
	                   
	                }
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		};
		 return info;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}  