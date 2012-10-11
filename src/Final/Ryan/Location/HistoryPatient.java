package Final.Ryan.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Final.Ryan.R;
import Final.Ryan.Patient.Patient;
import Final.Ryan.Patient.PatientDbAdapter;
import android.app.Activity;
import android.content.Context;
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
import android.widget.AdapterView.OnItemClickListener;



public class HistoryPatient extends Activity implements OnItemClickListener{
	
	 //Start from here is the variables used in the application
		private static final String DATABASE_TABLE_SEE = "ToBeSee";
		private static final String DATABASE_TABLE_HISTORY = "History";
	    static final private String TAG = "PatientsInfo";
	    static final private String emailOfSender = "luckin89@gmail.com";
	    
	    static final private int MENU_CHRON = Menu.FIRST;
		static final private int MENU_ALPHB = Menu.FIRST + 1;
		
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
	    

	   
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	            super.onCreate(savedInstanceState);
	            
	            setTitle("History Patient Infomation");
	            
	            setContentView(R.layout.multiple_checkbox_main);
	            
	            listView = (ListView) findViewById(R.id.listview);
	            
	            ptDbAdapter = new PatientDbAdapter(this);
	    		ptDbAdapter.open();  		
	    		fillData(DATABASE_TABLE_HISTORY);
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
	        mSimpleAdapter = new MyAdapter(this, al, R.layout.mylistview_history, from, to);
	        
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
	    		menu.add(0, MENU_CHRON, 0, R.string.history_menu_chron);
	    		menu.add(0, MENU_ALPHB, 1, R.string.history_menu_alphb);
	    		return true;
	    	}

	    	@Override
	    	public boolean onOptionsItemSelected(MenuItem item) {
	    		super.onOptionsItemSelected(item);

	    		switch (item.getItemId()) {
	    		case (MENU_CHRON):
	    			fillData(DATABASE_TABLE_HISTORY);
	    			return true;
	    		
	    		case (MENU_ALPHB):
	    			sortAlphbetically();
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
	                             convertView = mInflater.inflate(R.layout.mylistview_history, null);
	                        }
	                        ImageView im = (ImageView) convertView.findViewById(R.id.imageView1);
	                        im.setImageResource((Integer) mList.get(position).get(PICTURE));
	                        
	                        TextView tN = (TextView) convertView.findViewById(R.id.textView1);
	                        tN.setText((String)mList.get(position).get(NAME));
	                        
	                        TextView tLA = (TextView) convertView.findViewById(R.id.textView2);
	                        tLA.setText("Lat. " + (String)mList.get(position).get(LATITUDE));
	                        
	                        TextView tLO = (TextView) convertView.findViewById(R.id.textView3);
	                        tLO.setText("Long. " + (String)mList.get(position).get(LONGTITUDE));

	                       
	                        return convertView;
	                }
	                
	        }
	        
	        private void sortAlphbetically(){
	        	if (!al.isEmpty()) {
	        	    Collections.sort(al, new Comparator<Map<String, Object>>() {
	        	        @Override
	        	        public int compare(Map<String, Object> object1,
	        	            Map<String, Object> object2) {
	        	        		return object1.get(NAME).toString()
	        	                    .compareTo(object2.get(NAME).toString());
	        	        }
	        	   });
	        	}
	        	listView.setAdapter(mSimpleAdapter);
	        }
	        

}
