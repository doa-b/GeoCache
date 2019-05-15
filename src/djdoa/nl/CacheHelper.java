package djdoa.nl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;

public class CacheHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME="GeoCache.db";
	private static final int SCHEMA_VERSION=1;
	private static String queryString;
	private static File exportDir;
	private static final String backupDBFileName  = "/DatabaseExports//" + DATABASE_NAME;	
	private static final String backupDBPath  = "/DatabaseExports";
	
	public CacheHelper (Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE caches (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, " +
				"type INTEGER, difficulty INTEGER, description TEXT, clue TEXT, found STRING, lat REAL, lon REAL);");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	public Cursor getAll() {
		queryString="SELECT _id, name, type, difficulty, description, clue, found, lat, lon FROM caches";
		
		return(getReadableDatabase().rawQuery(queryString, null));
	}
	
	public Cursor getById(String id) {
		String[] args={id};
		queryString="SELECT _id, name, type, difficulty, description, clue, found, lat, lon FROM caches WHERE _id=?";
		
		return(getReadableDatabase().rawQuery(queryString, args));
	}
	
	public void insert(String name, Integer type, Integer difficulty, String description, String clue, String found, double lat, double lon) {
		ContentValues cv = new ContentValues();
		
		cv.put("name" , name);
		cv.put("type", type);
		cv.put("difficulty", difficulty);
		cv.put("description", description);
		cv.put("clue", clue);
		cv.put("found", found);
		cv.put("lat", lat);
		cv.put("lon", lon);
		
		getWritableDatabase().insert("caches", "name", cv);
	}
	
	public void update(String id, String name, Integer type, Integer difficulty, String description, String clue, String found, double lat, double lon) {
		String[] args={id};
		ContentValues cv = new ContentValues();
		
		cv.put("name" , name);
		cv.put("type", type);
		cv.put("difficulty", difficulty);
		cv.put("description", description);
		cv.put("clue", clue);
		cv.put("found", found);
		cv.put("lat", lat);
		cv.put("lon", lon);
		
		getWritableDatabase().update("caches", cv, "_id=?", args);
	} 
	
	public void delete(String id) {
		String[] args={id};
		getWritableDatabase().delete("caches", "_id=?", args);
	}
	
	public String getID(Cursor c) {
		return(c.getString(c.getColumnIndex("_id")));
	}
	
	public String getName(Cursor c) {
		return(c.getString(c.getColumnIndex("name")));
	}
	
	public Integer getType(Cursor c) {
		return(c.getInt(c.getColumnIndex("type")));
	}
	
	public Integer getDifficulty(Cursor c) {
		return(c.getInt(c.getColumnIndex("difficulty")));
	}
	
	public String getdescription(Cursor c) {
		return(c.getString(c.getColumnIndex("description")));
	}
	
	public String getClue(Cursor c) {
		return(c.getString(c.getColumnIndex("clue")));
	}
	
	public String getFound(Cursor c) {
		return(c.getString(c.getColumnIndex("found")));
	}
	
	public double getLon(Cursor c) {
		return(c.getDouble(c.getColumnIndex("lon")));
	}
	
	public double getLat(Cursor c) {
		return(c.getDouble(c.getColumnIndex("lat")));
	}
	
	public void exportDB(Context context) {
		close();
		// make export directory if it not already exists
		exportDir = new File(Environment.getExternalStorageDirectory() + backupDBPath);
		if(!exportDir.exists())
		 { if(exportDir.mkdir())
		 	{ // directory is created
			 Toast.makeText(context, "Export directory created", Toast.LENGTH_LONG).show();			
		 	}
		 }
		
		try {
            File sd = Environment.getExternalStorageDirectory();
            //File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
	           
                
               File currentDB = context.getApplicationContext().getDatabasePath(DATABASE_NAME);
	          // File currentDB = new File(data, currentDBPath);
	           File backupDB = new File(sd, backupDBFileName);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                
                
                Toast.makeText(context, backupDB.toString(),
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG)
                    .show();

        }
    }
	
	public void importDB(Context context) {
		close();
		
		 try {
             File sd = Environment.getExternalStorageDirectory();
             //File data  = Environment.getDataDirectory();

             if (sd.canWrite()) {
                // String  currentDBPath= "//data//" + "PackageName"
               //          + "//databases//" + "DatabaseName";
                 
            	  File currentDB = context.getApplicationContext().getDatabasePath(DATABASE_NAME);
    	          // File currentDB = new File(data, currentDBPath);
    	           File backupDB = new File(sd, backupDBFileName);

                    FileChannel src = new FileInputStream(backupDB).getChannel();
                    FileChannel dst = new FileOutputStream(currentDB).getChannel();
                    
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                 
                 Toast.makeText(context, backupDB.toString(),
                         Toast.LENGTH_LONG).show();

             }
         } catch (Exception e) {

             Toast.makeText(context, e.toString(), Toast.LENGTH_LONG)
                     .show();

         }
  

	}
		
	
	
		

}
