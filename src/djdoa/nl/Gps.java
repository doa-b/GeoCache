package djdoa.nl;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class Gps {
	private static Double meLat = 0.0d;
	private static Double meLon = 0.0d;
	private static LocationManager locMgr=null;
	private static Location myLocation = null;
	
	
	public Gps (Context context) {
		locMgr=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		myLocation = locMgr.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		if (myLocation !=null) {
			meLat = myLocation.getLatitude();
			meLon = myLocation.getLongitude();
		}
	}
	
	public Integer distance (double lat, double lon) {
		
		if (meLat > 0.0d) {
			float dis[] = new float[1];
			 
			 Location.distanceBetween(meLat, meLon, lat, lon, dis);		
			 Integer disInt = (int) Math.round(dis[0]); 		
			 return disInt;
			 
		} else return (0);		
	}
}
