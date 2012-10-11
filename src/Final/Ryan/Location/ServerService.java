package Final.Ryan.Location;
import Final.Ryan.R;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import Final.Ryan.Patient.PatientDbAdapter;
import Final.Ryan.Welcome.Welcome;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ServerService extends Service {  
	
    private static final String TAG = "ServerService";   
	DatagramSocket serverSocket; // Socket used both for sending and receiving
	//InetAddress serverAddr;
	//String name; // the Display name
	String serverPort; // server's port number
	boolean socketOK = true; // True as long as we don't get socket errors
	boolean isStop = false; // True as long as we don't get socket errors
	PatientDbAdapter ptDbAdapter;
	ServiceHelper shelper;
	Handler handler = new Handler();
	ReceiveMessages rm;
	String username, ipaddress;
    
    @Override  
    public IBinder onBind(Intent intent) {  
        return null;  
    }  
      
    @Override  
    public void onCreate() {  
        Toast.makeText(this, "Server Service Created", Toast.LENGTH_LONG).show();  
        Log.d(TAG, "onCreate");
    }  
   
  
    @Override  
    public void onDestroy() {  
        Toast.makeText(this, "Server Service Stopped", Toast.LENGTH_LONG).show();  
        Log.d(TAG, "onDestroy");  
        closeSocket();
        isStop = true;
        super.onDestroy();
    }  
      
    @Override  
    public void onStart(Intent intent, int startid) {  
        Toast.makeText(this, "Server Service Started", Toast.LENGTH_LONG).show();  
        Toast.makeText(this, getLocalIpAddress(), Toast.LENGTH_LONG).show();  
        Log.d(TAG, "onStart"); 
        
        
        shelper  = new ServiceHelper();
    	Bundle info = new Bundle();
		info = intent.getExtras();
		username = info.getString(Welcome.USER_NAME);
		ipaddress = info.getString(Welcome.IP_ADDRESS);
		serverPort = "8080"; 
        
        /*
		 * Start the Server
		 */
		try {
			int port = Integer.parseInt(serverPort);
			serverSocket = new DatagramSocket(port);
		} catch (Exception e) {
			Log.e(TAG, "Cannot open socket" + e.getMessage());
			return;
		}
		new ReceiveMessages().execute();		
    }  
    
    
	
	protected Service getService() {
		// TODO Auto-generated method stub
		return this;
	}

	// If the socket is OK, then it's running
	boolean socketIsOK() {
		return socketOK;
	}
	
	// Method for closing the socket before exiting application
	public void closeSocket() {
		serverSocket.close();
	}
	
	 void showNotification(Service service, Bundle pinfo) {  
	   NotificationManager mNM = (NotificationManager)service.getSystemService(service.NOTIFICATION_SERVICE);
       Notification notification = new Notification(R.drawable.ic_launcher, "New Messages", System.currentTimeMillis());  
       notification.flags  = Notification.FLAG_AUTO_CANCEL;  
       Intent intent = new Intent(this, PatientsInfo.class);
       /*
        * TODO add the database fill data function
        */
       intent.putExtras(pinfo);
	   intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	   intent.setAction("android.intent.action.receiveMessages");		
       PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);  
       notification.setLatestEventInfo(this, "Healthcare Delivery",  "New Patient Added.", contentIntent);  
       mNM.notify(1, notification);
	 } 
	 
	 
	 class ReceiveMessages extends AsyncTask<Void,Integer,Void> {
		  
	        @Override  
	        protected Void doInBackground(Void... params) { 
	        	Bundle info = null;
	        	while(true){
		        	info = shelper.nextMessage(serverSocket, socketOK, username, ipaddress);
		        	if(shelper.isTopActivity(getService(),"Final.Ryan.Location.PatientsInfo") && info != null){ 
		        		sendBroadcast(shelper.broadcastMessages(info));
		        	}else if(info != null){
		        		showNotification(getService(), info);
		        	}
	        	}
	        }       
    }
	 
		// get the IP address
	    public String getLocalIpAddress() {   
	        try {   
	            for (Enumeration<NetworkInterface> en = NetworkInterface   
	                    .getNetworkInterfaces(); en.hasMoreElements();) {   
	                NetworkInterface intf = en.nextElement();   
	                for (Enumeration<InetAddress> enumIpAddr = intf   
	                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {   
	                    InetAddress inetAddress = enumIpAddr.nextElement();   
	                    if (!inetAddress.isLoopbackAddress()) {   
	                        return inetAddress.getHostAddress().toString();   
	                    }   
	                }   
	            }   
	        } catch (SocketException ex) {   
	            Log.e("WifiPreference IpAddress", ex.toString());   
	        }   
	        return null;   
	    }
	 
}  
