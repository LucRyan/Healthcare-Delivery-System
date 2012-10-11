/*
 * Copyright 2010-2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package Final.Ryan.Sqs.Demo;

import Final.Ryan.R;
import Final.Ryan.Sqs.Util.SqsMenu;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SQSDemo extends Activity {
    
	private static final String success = "Welcome to The CHWs Communication Center!";
	private static final String fail = "Load Failed. Please Try Restarting the Application.";
	
	protected Button sqsButton;
	protected TextView welcomeText;
	
    public static ClientManager clientManager = null;
    
    Bundle info = new Bundle();
    	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainsqs);
        sqsButton = (Button) findViewById(R.id.main_queue_button);
        welcomeText = (TextView) findViewById(R.id.main_into_text);                                    

        info = getIntent().getExtras();
        clientManager = new ClientManager();

     	if ( this.clientManager.hasCredentials() ){
    		welcomeText.setText(success);
    		sqsButton.setVisibility(View.VISIBLE);
    		this.wireButtons();
    	} 
        else {
    		this.displayCredentialsIssueAndExit();
    		welcomeText.setText(fail);
    	}       
    }
        
    private void wireButtons(){
		sqsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SQSDemo.this, SqsMenu.class);
				i.putExtras(info);    			
				startActivity(i);
			}
		});
		
    }
        
    protected void displayCredentialsIssueAndExit() {
        AlertDialog.Builder confirm = new AlertDialog.Builder( this );
        confirm.setTitle("Credential Problem!");
        confirm.setMessage( "AWS Credentials not configured correctly.  Please review the README file." );
        confirm.setNegativeButton( "OK", new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int which ) {
                SQSDemo.this.finish();
            }
        } );
        confirm.show().show();                
    }
    
}
