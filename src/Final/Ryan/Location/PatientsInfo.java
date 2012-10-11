package Final.Ryan.Location;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Final.Ryan.Patient.Patient;
import Final.Ryan.Patient.PatientDbAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import Final.Ryan.R;
public class PatientsInfo extends Activity implements OnItemClickListener{
       
    /**
     * 
     * These Data are temporary and used for test.
     * 
     */
    private static final String[] mNames = new String[] {
   	 	"Benny", "Lorry", "Monica", "Jennifer", "Dunne"
   	 };

    private static final String[] mLatitude = new String[] {
    	"19240000", "35410000", "40780541", "38203655", "34089061"
   	 };
    private static final String[] mLongtitude = new String[] {
  	 	"-99120000", "139460000", "-74003906", "-83496093", "-118212890"
  	 };
    
    private static final int[] mPics = new int[]{
    	 R.drawable.pic1, R.drawable.pic2, R.drawable.pic3, R.drawable.pic4, R.drawable.pic5,
    	 R.drawable.pic6, R.drawable.pic7, R.drawable.pic8, R.drawable.pic9, R.drawable.pic10,
    	 R.drawable.pic11  	 
    };
 
    
    //Start from here is the variables used in the application
	private static final String DATABASE_TABLE_SEE = "ToBeSee";
	private static final String DATABASE_TABLE_HISTORY = "History";
    static final private String TAG = "PatientsInfo";
    static final private String emailOfSender = "luckin89@gmail.com";
    
    static final private int MENU_MAP = Menu.FIRST;
	static final private int MENU_DELETE = Menu.FIRST + 1;
	static final private int MENU_HISTORY = Menu.FIRST + 2;
	
    public final static String ID = "_id";
    public final static String PICTURE = "picture";
    public final static String NAME = "name";
    public final static String LATITUDE = "latitude";
    public final static String LONGTITUDE = "longtitude";
    
    ListView listView;
	Patient patient;
	ArrayList<Patient> patients = new ArrayList<Patient>();
    
	private MyAdapter mSimpleAdapter;
	ArrayList<HashMap<String, Object>> al;

	PatientDbAdapter ptDbAdapter;
	long selectedFromList;
    
	MyReceiver receiver;
	IntentFilter filter;
	Bundle info = new Bundle();
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setTitle("Patient Infomation");
            setContentView(R.layout.multiple_checkbox_main);
            listView = (ListView) findViewById(R.id.listview);
            
            /*
             * Get information from intent
             */
    		info = getIntent().getExtras();
            
            /*
             * Set ListView Data
             */
            ptDbAdapter = new PatientDbAdapter(this);
    		ptDbAdapter.open();
    		//ptDbAdapter.deleteAll(DATABASE_TABLE_SEE);    		
    		//addTestData();
    		fillData(DATABASE_TABLE_SEE);
    		//fillData(DATABASE_TABLE_HISTORY);
    		
            /*
             * Set the Broadcast receiver
             */
            receiver=new MyReceiver();
    		filter=new IntentFilter();
    		filter.addAction("android.intent.action.receiveMessages");
    		
    		/*
    		 * Start the Service
    		 */
    		//startServer();
        }
    
    protected void onStart(){
    	super.onStart();
		PatientsInfo.this.registerReceiver(receiver,filter);
    }
    
    protected void onRestart(){
    	super.onRestart();
    	PatientsInfo.this.registerReceiver(receiver,filter);
    }
    
	protected void onDestroy(){
		PatientsInfo.this.unregisterReceiver(receiver);
		ptDbAdapter.close();
		super.onDestroy();
	}
    
    protected void onNewIntent(Intent intent) {  
        // TODO Auto-generated method stub  
        super.onNewIntent(intent);  
        PatientsInfo.this.registerReceiver(receiver,filter);
        
		Bundle pinfo = new Bundle();
		pinfo = intent.getExtras();
		//Log.i(TAG, pinfo.getString("pName"));
		if(pinfo.getString("pName") != null){
    	ptDbAdapter.createItem(DATABASE_TABLE_SEE,
				   String.valueOf(mPics[pinfo.getInt("pId")%11]),
			   					  pinfo.getString("pName"), 
			   					  pinfo.getString("pLatitude"),
			   					  pinfo.getString("pLongitude"));	
        fillData(DATABASE_TABLE_SEE);
        listView.setAdapter(mSimpleAdapter);
		}
    }
    
	private void startServer(){
		Intent service = new Intent(this, ServerService.class);
		service.putExtras(info);
		startService(service);
	}
    
    
    
    private void addTestData(){
        for(int i=0; i < mNames.length; i++){
        	patients.add(new Patient(i+1, mPics[i], mNames[i], mLatitude[i], mLongtitude[i]));
        	ptDbAdapter.createItem(DATABASE_TABLE_SEE,
        						   String.valueOf(patients.get(i).getPicture()),
        					   					  patients.get(i).getName(), 
        					   					  patients.get(i).getLatitude(),
        					   					  patients.get(i).getLongtitude());				 
        }
    }
    
	private void fillData(String database_table) {
		// Get all of the books from the cart database 
		Cursor myCursor = ptDbAdapter.fetchAllItems(database_table);
		startManagingCursor(myCursor);
        al = new ArrayList<HashMap<String,Object>>();
		if(myCursor!=null){
		 	myCursor.moveToFirst();
            while(!myCursor.isAfterLast()){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put( ID, myCursor.getString(0));
                map.put( PICTURE, Integer.valueOf(myCursor.getString(1)));
                map.put( NAME, myCursor.getString(2));
                map.put( LATITUDE, myCursor.getString(3));
                map.put( LONGTITUDE, myCursor.getString(4));
                al.add(map);
                myCursor.moveToNext(); 
            }
		}
    
        String[] from = { PICTURE, NAME, LATITUDE, LONGTITUDE};
        int[] to = { R.id.imageView1, R.id.textView1, R.id.textView2, R.id.textView3};
        mSimpleAdapter = new MyAdapter(this, al, R.layout.mylistview, from, to);
        
        listView.setAdapter(mSimpleAdapter);
        listView.setOnItemClickListener(this);
	}
    
    
 
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox1);
                checkBox.toggle();
                mSimpleAdapter.map.put(position, checkBox.isChecked()); 
                Log.i(TAG, String.valueOf(position));
        }
        
    	@Override
    	public boolean onCreateOptionsMenu(Menu menu) {
    		super.onCreateOptionsMenu(menu);
    		menu.add(0, MENU_MAP, 0, R.string.map_menu_goto);
    		menu.add(0, MENU_DELETE, 1, R.string.map_menu_delete);
    		menu.add(0, MENU_HISTORY, 2, R.string.map_menu_history);
    		return true;
    	}

    	@Override
    	public boolean onOptionsItemSelected(MenuItem item) {
    		super.onOptionsItemSelected(item);

    		switch (item.getItemId()) {
    		case (MENU_MAP):
    			getInformation();
    			return true;
    		
    		case (MENU_DELETE):
    			deleteSelect();
    			return true;
    			
    		case (MENU_HISTORY):
    			viewHistory();
    			return true;	
    		}
    		
    		return false;
    	}
        
        public class MyAdapter extends SimpleAdapter {
                
                HashMap<Integer, Boolean> map; 
                
                LayoutInflater mInflater;
                
                private List<HashMap<String, Object>> mList;
                
                public MyAdapter(Context context, List<HashMap<String, Object>> data,
                                int resource, String[] from, int[] to) {
                        super(context, data, resource, from, to);
                        map = new HashMap<Integer, Boolean>();
                        mInflater = LayoutInflater.from(context);
                        mList = data;
                        for(int i = 0; i < data.size(); i++) {
                                map.put(i, false);
                        } 
                }
                
                @Override
                public int getCount() {
                        return mList.size();
                }
 
                @Override
                public Object getItem(int position) {
                        return position;
                }
 
                @Override
                public long getItemId(int position) {
                        return position;
                }
                
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                        if(convertView == null) {
                             convertView = mInflater.inflate(R.layout.mylistview, null);
                        }
                        ImageView im = (ImageView) convertView.findViewById(R.id.imageView1);
                        im.setImageResource((Integer) mList.get(position).get(PICTURE));
                        
                        TextView tN = (TextView) convertView.findViewById(R.id.textView1);
                        tN.setText((String)mList.get(position).get(NAME));
                        
                        TextView tLA = (TextView) convertView.findViewById(R.id.textView2);
                        tLA.setText("Lat. " + (String)mList.get(position).get(LATITUDE));
                        
                        TextView tLO = (TextView) convertView.findViewById(R.id.textView3);
                        tLO.setText("Long. " + (String)mList.get(position).get(LONGTITUDE));

                        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
                        checkBox.setChecked(map.get(position)); 
                        
                        return convertView;
                }
                
        }
        
    	private void getInformation(){
    		boolean itemsBeenSelected = false;
    		int count = 0;
            for(int i = 0; i < mSimpleAdapter.map.size(); i++){
            	if(mSimpleAdapter.map.get(i)){
            		itemsBeenSelected = true;
            		count++;
            	}
            }
            Bundle info = new Bundle();
            String[] picture = new String[count];
            String[] name = new String[count];
            String[] latitude = new String[count];
            String[] longtitude = new String[count];
            		
            if(itemsBeenSelected){
            	int index = 0;
                for(int i = 0; i < mSimpleAdapter.map.size(); i++){
                	if(mSimpleAdapter.map.get(i)){
                		
                    	Log.i(TAG, String.valueOf(al.get(i).get(NAME)));
                    	Log.i(TAG, String.valueOf(al.get(i).get(LATITUDE)));
                    	Log.i(TAG, String.valueOf(al.get(i).get(LONGTITUDE)));
                    	
                    	picture[index] = String.valueOf(al.get(i).get(PICTURE));
                    	name[index] = String.valueOf(al.get(i).get(NAME));
                    	latitude[index] = String.valueOf(al.get(i).get(LATITUDE));
                    	longtitude[index] = String.valueOf(al.get(i).get(LONGTITUDE));
             
                    	index++;
                	}
                }
                info.putStringArray(PICTURE, picture);
            	info.putStringArray(NAME, name);
            	info.putStringArray(LATITUDE, latitude);
            	info.putStringArray(LONGTITUDE, longtitude);
            	info.putInt("count", count);
            	Log.i(TAG, String.valueOf(count));
            	Intent startMap = new Intent(this, LocationCenter.class);
            	startMap.putExtras(info);
            	startActivity(startMap);
            }else{
            	Toast.makeText(this, "No patients selected!", Toast.LENGTH_SHORT)
				.show();
            }
    	}
        
    	private void deleteSelect(){
    		boolean itemsNeedDelete = false;
    		int count = 0;
            for(int i = 0; i < mSimpleAdapter.map.size(); i++){
            	if(mSimpleAdapter.map.get(i)){
            		itemsNeedDelete = true;
            		count++;
            	}
            }

            if(itemsNeedDelete){
            	for(int i = 0; i < mSimpleAdapter.map.size(); i++){
                	if(mSimpleAdapter.map.get(i)){
                		ptDbAdapter.deleteItem( DATABASE_TABLE_SEE, 
                								(long)Integer.parseInt(String.valueOf((al.get(i).get(ID)))));
                		ptDbAdapter.createItem( DATABASE_TABLE_HISTORY, 
                								String.valueOf(al.get(i).get(PICTURE)), 
                            	 				String.valueOf(al.get(i).get(NAME)),
                            	 				String.valueOf(al.get(i).get(LATITUDE)),
                            	 				String.valueOf(al.get(i).get(LONGTITUDE)));
                	}
            	}
            	for(int i = 0; i < mSimpleAdapter.map.size(); i++) {
            		mSimpleAdapter.map.put(i, false);
            	} 
            	
            }else{
            	Toast.makeText(this, "No patients need to Delete!", Toast.LENGTH_SHORT)
				.show();
            }
            fillData(DATABASE_TABLE_SEE);
            listView.setAdapter(mSimpleAdapter);
    	}
    	

    	private void viewHistory(){
        	Intent viewHistory = new Intent(this, HistoryPatient.class);
        	startActivity(viewHistory);
    	}
    	
    	private void testMessage(String message){
            Builder MyAlertDialog = new AlertDialog.Builder(this);
    		MyAlertDialog.setTitle("Test");
    		MyAlertDialog.setMessage(message);

    		DialogInterface.OnClickListener cancelClick = new DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog, int which) {
    			//do nothing with close the message.
    			}
    		};
    		MyAlertDialog.setNegativeButton("OK",cancelClick );
    		MyAlertDialog.show();
    	}
    	
    	
    	public class MyReceiver extends BroadcastReceiver {
    		@Override
    		public void onReceive(Context context, Intent intent) {
    			// TODO Auto-generated method stub
    			Log.i(TAG, "OnReceiver");
    			Bundle pinfo = new Bundle();
    			pinfo = intent.getExtras();
    			//Log.i(TAG, pinfo.getString("pName"));
    			if(pinfo.getString("pName") != null){
            	ptDbAdapter.createItem(DATABASE_TABLE_SEE,
						   String.valueOf(mPics[pinfo.getInt("pId")%11]),
					   					  pinfo.getString("pName"), 
					   					  pinfo.getString("pLatitude"),
					   					  pinfo.getString("pLongitude"));	
                fillData(DATABASE_TABLE_SEE);
                listView.setAdapter(mSimpleAdapter);
    			}
    		}
    	}
    	
    	void c2dmRegister(){
            Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
            registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0)); // boilerplate
            registrationIntent.putExtra("sender", emailOfSender);
            startService(registrationIntent);
        }
        
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
                handleRegistration(context, intent);
            } else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
                //handleMessage(context, intent);
            }
         }
        
        private void handleRegistration(Context context, Intent intent) {
            String registration = intent.getStringExtra("registration_id"); 
            if (intent.getStringExtra("error") != null) {
                // Registration failed, should try again later.
            } else if (intent.getStringExtra("unregistered") != null) {
                // unregistration done, new messages from the authorized sender will be rejected
            } else if (registration != null) {
               // Send the registration ID to the 3rd party site that is sending the messages.
               // This should be done in a separate thread.
               // When done, remember that all registration is done. 
            }
        }
}