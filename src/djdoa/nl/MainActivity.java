package djdoa.nl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity implements OnInfoWindowClickListener {
	
	LocationManager locMgr=null;
	
	CacheHelper helper = null;
	Gps gps = null;
	
	List<Marker> cacheMarkers = new ArrayList<Marker>();
	private HashMap<Marker, String> markerCursorID; // make hashmap to link sqlite database id to each marker 

	
	static int [] typeArray = new int [] {R.drawable.traditional_cache, R.drawable.multi_cache, R.drawable.mystery_cache,
		R.drawable.earth_cache, R.drawable.letter_box_hybrid_cache, R.drawable.event_cache};	
	
	public final static String ID_EXTRA="nl.djdoa.ID";	
	public final static String HELP_EXTRA="nl.djdoa.HELP";
	static final LatLng NEDERLAND = new LatLng(52.230919,  5.420182);
		
	private GoogleMap map;	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        helper = new CacheHelper(this);
        locMgr=(LocationManager)getSystemService(LOCATION_SERVICE);
       
        markerCursorID = new HashMap<Marker, String>();
        
        if ( !locMgr.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) { // check is gps is enabled. If not present dialog box to user to enable.
            Messages.buildAlertMessageNoGps(this);
        }

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        
        map.setMyLocationEnabled(true);
        
        map.setOnInfoWindowClickListener(this);
        
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);          
                
        // move the camera instantly to the Netherlands with a zoom of 15.        
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(NEDERLAND, 15));
        
        // Zoom in, animating the camera
        map.animateCamera(CameraUpdateFactory.zoomTo(7),  2000, null);
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
    
    @Override
    protected void onResume() {
    	Cursor c = helper.getAll();
    	cacheMarkers.clear(); // clear all markers from list
    	map.clear(); // clear all markers from map
    	gps = new Gps(MainActivity.this);
    	
    	String snippet = "not discovered yet.";
    	String title = "";

    	while(c.moveToNext()) {	
    		if (helper.getFound(c).equals("true")) {
    			snippet = "Cache already found.";    			
    		}else snippet = "Not discovered yet.";
    		snippet = snippet + " Click for details";	
    		
    		title = String.valueOf(String.valueOf(gps.distance(helper.getLat(c), helper.getLon(c))));
    		title = title + "m   ";
    		title = title + helper.getName(c);
    		
    		Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(helper.getLat(c), helper.getLon(c)))
    		.title(title)    		
    		.snippet(snippet)
    		.icon(BitmapDescriptorFactory.fromResource(typeArray[helper.getType(c)])));
    		
    		cacheMarkers.add(marker);
    		markerCursorID.put(marker, helper.getID(c));
    	}    	
    	super.onResume();
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {		
		int id = item.getItemId();
		
		if (id == R.id.go_to_list) {			
			Intent i = new Intent(this, LijstActivity.class);	
			startActivity(i);
			
		} else if (id == R.id.add_new_cache) {
			Intent j = new Intent(this, DetailActivity.class);
			startActivity(j);			
		  
		} else if (id == R.id.help) {
			Intent k = new Intent(this, HelpPage.class);
			k.putExtra(HELP_EXTRA, "geocache_main.html");
			startActivity(k);
		} else if (id == R.id.export_database) helper.exportDB(this);		
		
		else if (id == R.id.import_database) helper.importDB(this);
		
		return super.onOptionsItemSelected(item);		
	}
    
    @Override
    public void onInfoWindowClick(Marker welke) {
    	//Toast.makeText(this, "CursorID " + markerCursorID.get(welke), Toast.LENGTH_LONG).show();    
    	
    	Intent i=new Intent(MainActivity.this, DetailActivity.class);    	
    	
    	i.putExtra(MainActivity.ID_EXTRA, markerCursorID.get(welke));
    	startActivity(i);
    }
}
