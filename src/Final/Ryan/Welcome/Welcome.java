package Final.Ryan.Welcome;

import Final.Ryan.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Welcome extends Activity {
	

	final static private String TAG = "WELCOME";
	
	final static public String USER_NAME = "USERNAME"; 
	final static public String IP_ADDRESS = "IPADDRESS";
	EditText nameEdit, ipAddressEdit;
	Button Login;
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        setTitle("Welcome to Healthcare Delivery System.");
        
		Login = (Button)findViewById(R.id.login);
		Login.setOnClickListener(okListener);
		nameEdit = (EditText)findViewById(R.id.username);
		ipAddressEdit = (EditText)findViewById(R.id.ipaddress);       
	}
	
	
	// On click listener for the OK button
	private OnClickListener okListener = new OnClickListener() {
		public void onClick(View v) {
			startNext();
		}
		
	};
	
	void startNext(){
		Intent i = new Intent(this, ChoiceOption.class);
		Bundle info = new Bundle();
		info.putString(USER_NAME, nameEdit.getText().toString());
		info.putString(IP_ADDRESS, ipAddressEdit.getText().toString());
		i.putExtras(info);
		startActivity(i);
	}
}
