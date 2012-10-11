package Final.Ryan.Welcome;

import Final.Ryan.Location.PatientsInfo;
import Final.Ryan.Location.ServerService;
import Final.Ryan.Sqs.Demo.SQSDemo;
import Final.Ryan.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChoiceOption extends Activity {
	

	final static private String TAG = "WELCOME";
	String username, ipAddress;
	Button Patient, CHW;
	TextView hello;
	Bundle info = new Bundle();
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options);
        

		info = getIntent().getExtras();
		username = info.getString(Welcome.USER_NAME);
		ipAddress = info.getString(Welcome.IP_ADDRESS);
		
		hello = (TextView)findViewById(R.id.helloTextView);
		hello.setText("Hello: "+ username + "!");
		Patient = (Button)findViewById(R.id.patientbutton);
		Patient.setOnClickListener(patientListener);
		CHW = (Button)findViewById(R.id.chwbutton);
		CHW.setOnClickListener(chwListener);
        
	}
	
	// On click listener for the OK button
	private OnClickListener patientListener = new OnClickListener() {
		public void onClick(View v) {
			startNext();
		}
		
	};
	// On click listener for the OK button
	private OnClickListener chwListener = new OnClickListener() {
		public void onClick(View v) {
			gotoChwCenter();
		}
		
	};
	
	void startNext(){
		Intent i = new Intent(this, PatientsInfo.class);
		i.putExtras(info);
		startServer();
		startActivity(i);
	}
	
	private void startServer(){
		Intent service = new Intent(this, ServerService.class);
		service.putExtras(info);
		startService(service);
	}
	
	void gotoChwCenter(){
		Intent i = new Intent(this, SQSDemo.class);
		i.putExtras(info);
		startActivity(i);
	}
}
