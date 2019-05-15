package djdoa.nl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class DetailActivity extends Activity {
	String cacheID=null;
	CacheHelper helper=null;
	LocationManager locMgr=null;
	static Gps gps = null;
	AnimationDrawable frameAnimation=null;
	
	EditText cacheName, cacheDescription;
	Spinner cacheType, cacheDifficulty;
	ImageButton gpsButton;
	TextView lattitude, longitude, distance, distanceLabel, textHint;
	ToggleButton foundIt;		
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (cacheID==null) {
			menu.findItem(R.id.delete).setEnabled(false); // when entering a new record, delete is disabled
		}
		return super.onPrepareOptionsMenu(menu);
	}	

	 @Override
		public boolean onCreateOptionsMenu(Menu menu) {		
			getMenuInflater().inflate(R.menu.menu_detail, menu);
			return true;
		}
	 
	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_detail);
	        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // prevents the soft keyboard from showing up when view is shown
	        
	        getActionBar().setDisplayHomeAsUpEnabled(true);

	        helper=new CacheHelper(this);
	        locMgr=(LocationManager)getSystemService(LOCATION_SERVICE);
	        gps=new Gps(this);
	        
	        cacheID = getIntent().getStringExtra(MainActivity.ID_EXTRA); // cacheID=null when add new cache is chosen	       
	        
	        cacheName=(EditText)findViewById(R.id.cache_name);	        
	        cacheDescription=(EditText)findViewById(R.id.description);
	        cacheType=(Spinner)findViewById(R.id.spinner_cache_type);
	        cacheDifficulty=(Spinner)findViewById(R.id._spinner_cache_difficulty);
	        
	        textHint=(TextView)findViewById(R.id.text_hint);	        
	        lattitude=(TextView)findViewById(R.id.lattitude);
	        longitude=(TextView)findViewById(R.id.longitude);
	        distance=(TextView)findViewById(R.id.cache_distance);
	        distanceLabel=(TextView)findViewById(R.id.distancelabel);
	        
	        foundIt=(ToggleButton)findViewById(R.id.found_it);
	        
	        gpsButton=(ImageButton)findViewById(R.id.gps);
	        gpsButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					getGPS();			
				}
			});
	        
	        if (cacheID!=null) {
	        	load(); // when a cache id is passed in through the intent extra, load the record data in the GUI fields
	        } else distanceLabel.setVisibility(View.INVISIBLE);
	 }
	 
	 @Override
	protected void onPause() {
		locMgr.removeUpdates(onLocationChange);
		super.onPause();
	}
	 
	 @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.save) save();
		else if (id == R.id.delete) areYouSureToDelete();	
			
		else if (id == R.id.enter_gps) {			
			getGPS();
			return true;
		} else if (id == android.R.id.home) {
			finish();
			return true;
		} else if (id == R.id.hint) hint();	
		
		else if (id == R.id.help) {
			Intent k = new Intent(this, HelpPage.class);
			k.putExtra(MainActivity.HELP_EXTRA, "geocache_detail.html");
			startActivity(k);
		}
		
		return false;
	}
	 
	 private void load() {
		 Cursor c=helper.getById(cacheID);
		 c.moveToFirst();
		 
		 cacheName.setText(helper.getName(c));
		 cacheDescription.setText(helper.getdescription(c));
		 cacheType.setSelection(helper.getType(c));
		 cacheDifficulty.setSelection(helper.getDifficulty(c));
		 
		 lattitude.setText(String.valueOf(helper.getLat(c)));
		 longitude.setText(String.valueOf(helper.getLon(c)));
		 
		 foundIt.setChecked(Boolean.parseBoolean(helper.getFound(c)));
		 
		 textHint.setText(helper.getClue(c));
		 
		 distance.setText(String.valueOf(gps.distance(helper.getLat(c), helper.getLon(c))));
	 }
	 
	 private void save() {
		 String name = cacheName.getText().toString();
		 String desc = cacheDescription.getText().toString();
		 Integer type = cacheType.getSelectedItemPosition();
		 Integer Diff = cacheDifficulty.getSelectedItemPosition();
		 String found = "false";
		 
		 
		double lat = Double.parseDouble((lattitude.getText().toString()));
		double lon = Double.parseDouble((longitude.getText().toString()));
				 
		 String clue = textHint.getText().toString();
		 if (foundIt.isChecked()) {
			found = "true";
		 } else found = "false";
		 
		 // check if all the required fields are filled in
		 
		 if (name != null && !name.isEmpty()) {
			 if (lat !=0.0d && lon !=0.0d) {		
		 
				 if (cacheID!=null) {
					 helper.update(cacheID, name, type, Diff, desc, clue, found, lat, lon);
				 } else {
					 helper.insert(name, type, Diff, desc, clue, found, lat, lon);
				 }
				 
				 finish();
		 
			 } else Messages.simpleOK(this, "please add GPS coordinates before saving");	
		 }	else Messages.simpleOK(this, "please fill in a name for your cache");
	 }
	 
	 LocationListener onLocationChange=new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// required for interface, not used
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// required for interface, not used
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			Messages.buildAlertMessageNoGps(DetailActivity.this);
			if(frameAnimation.isRunning()) frameAnimation.stop();
			
			gpsButton.setImageResource(R.drawable.gps1);	
		}
		
		@Override
		public void onLocationChanged(Location location) {
			if(frameAnimation.isRunning()) frameAnimation.stop();
			
			gpsButton.setImageResource(R.drawable.gps1);	
			
			
			
			lattitude.setText(String.valueOf(location.getLatitude()));
			 longitude.setText(String.valueOf(location.getLongitude()));
			 Toast.makeText(DetailActivity.this,  "location Saved", Toast.LENGTH_LONG).show();
			 locMgr.removeUpdates(onLocationChange);			
		}
	};
	
	public void getGPS() {
		gpsButton.setImageResource(R.drawable.radar);	
		frameAnimation = (AnimationDrawable) gpsButton.getDrawable();
		frameAnimation.start();
		
		
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, onLocationChange);
		Messages.simpleOK(DetailActivity.this, "Your location is beeing fixed. Please wait until (new) coordinates appear");	
	}
	
	public void hint() {
		
		String post = "Save";
		if (cacheID!=null) post = "OK";
				
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Hints");

		// Set up the input
		final EditText input = new EditText(this);
		input.setText(textHint.getText().toString());
		
		// Specify the type of input expected
		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton(post, new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        textHint.setText(input.getText().toString());
		    }
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});

		builder.show();		
     }
	
	public void areYouSureToDelete() {				
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Delete " + cacheName.getText().toString());
		builder.setMessage("Are you sure?");		
		builder.setPositiveButton("Yes, delete", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		      helper.delete(cacheID);
		      dialog.dismiss();
		      finish();
		    }
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});
		builder.show();
	}	
}
