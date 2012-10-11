package Final.Ryan.Location;

import java.util.List;
import java.util.Locale;
import Final.Ryan.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

public class LocationCenter extends MapActivity {
	
	public final static String TAG = "LocationCenter";
    public final static String ID = "_id";
    public final static String PICTURE = "picture";
    public final static String NAME = "name";
    public final static String LATITUDE = "latitude";
    public final static String LONGTITUDE = "longtitude";
    
    LinearLayout linearLayout;
	MapView mapView;
	List<Overlay> mapOverlays;
	Drawable drawable;
	PatientsOverlay itemizedOverlay;
	
	Location mLocation;
	Address mAddress;
	
	private int intZoomLevel = 11;
    private double dLat = 0;
    private double dLng = 0;
    private GeoPoint geoPoint;
    private MapController mapController;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);        
        mapOverlays = mapView.getOverlays();
        
        Bundle info = new Bundle();
        info = getIntent().getExtras();
        int count = info.getInt("count");
        
        String[] picture = new String[count];
        String[] name = new String[count];
        String[] latitude = new String[count];
        String[] longtitude = new String[count];
        
        picture = info.getStringArray(PICTURE);
        name = info.getStringArray(NAME);
        latitude = info.getStringArray(LATITUDE);
        longtitude = info.getStringArray(LONGTITUDE);
        
        Log.i(TAG, String.valueOf(name.length));
        for(int i = 0; i < name.length; i++){
        	drawable = this.getResources().getDrawable(Integer.parseInt(picture[i]));
        	itemizedOverlay = new PatientsOverlay(drawable);
        
        	GeoPoint point = new GeoPoint( (int)(Double.parseDouble(latitude[i])), 
        								   (int)(Double.parseDouble(longtitude[i])));
        	OverlayItem overlayitem = new OverlayItem(point, name[i], "");
        
        	itemizedOverlay.addOverlay(overlayitem);
        	mapOverlays.add(itemizedOverlay);
        }
        
        //Get my Location.
        Location mLocation = getMyLocation(this);
        dLat = mLocation.getLatitude();
        dLng = mLocation.getLongitude();
        geoPoint = new GeoPoint((int) (dLat * 1E6), (int) (dLng * 1E6));//將剛剛取得的座標置入geoPoint
        drawable = this.getResources().getDrawable(R.drawable.cross);
        itemizedOverlay = new PatientsOverlay(drawable);
        OverlayItem overlayitem = new OverlayItem(geoPoint, "My Location", "My Location");
    	itemizedOverlay.addOverlay(overlayitem);
    	mapOverlays.add(itemizedOverlay);
        mapView.displayZoomControls(true);
        mapController = mapView.getController();
        mapController.animateTo(geoPoint);//將map的中心點移到自己所在的位置
        mapController.setZoom(intZoomLevel);//設定地圖級距
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	protected void getMylocation(){
		mLocation = getLocation(this);  
        GeoPoint gp = getGeoByLocation(mLocation);  
        mAddress = getAddressbyGeoPoint(this, gp); 
	}
	
	//Get the Location by GPS or WIFI  
    public Location getLocation(Context context) {  
        LocationManager locMan = (LocationManager) context  
                .getSystemService(Context.LOCATION_SERVICE);  
        Location location = locMan  
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);  
        if (location == null) {  
            location = locMan  
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  
        }  
        return location;  
    }  


     public  GeoPoint getGeoByLocation(Location location) {  
         GeoPoint gp = null;  
         try {  
             if (location != null) {  
                 double geoLatitude = location.getLatitude() * 1E6;  
                 double geoLongitude = location.getLongitude() * 1E6;  
                 gp = new GeoPoint((int) geoLatitude, (int) geoLongitude);  
             }  
         } catch (Exception e) {  
             e.printStackTrace();  
         }  
         return gp;  
     }  
 
     
     public  Address getAddressbyGeoPoint(Context cntext, GeoPoint gp) {  
         Address result = null;  
         try {  
             if (gp != null) {  
                 Geocoder gc = new Geocoder(cntext, Locale.US);  
                  
                 double geoLatitude = (int) gp.getLatitudeE6() / 1E6;  
                 double geoLongitude = (int) gp.getLongitudeE6() / 1E6;  
                   
                 List<Address> lstAddress = gc.getFromLocation(geoLatitude,  
                         geoLongitude, 1);  
                 if (lstAddress.size() > 0) {  
                     result = lstAddress.get(0);  
                 }  
             }  
         } catch (Exception e) {  
             e.printStackTrace();  
         }  
         return result;  
     }  
     
     private Location getMyLocation(Context context) {
    	  LocationManager locMan = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    	  Location location = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	  if (location == null) {
    	   location = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    	  }
    	  return location;
    	 }
}