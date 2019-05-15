package djdoa.nl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LijstActivity extends Activity {

	static int [] typeArray = new int [] {R.drawable.traditional_cache, R.drawable.multi_cache, R.drawable.mystery_cache,
			R.drawable.earth_cache, R.drawable.letter_box_hybrid_cache, R.drawable.event_cache};	
	
	Cursor model=null;
	CacheHelper helper=null;
	CacheAdapter adapter=null;
	ListView lijst=null;
	static Gps gps = null;	
	 
	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_lijst);
	        
	        getActionBar().setDisplayHomeAsUpEnabled(true);	        
	       
	        helper=new CacheHelper(this);
	        gps=new Gps(this);      
	        
	        initList();
	 }
	 
	 @Override
	protected void onDestroy() {
		super.onDestroy();
		helper.close();		
	}	 

	 @Override
		public boolean onCreateOptionsMenu(Menu menu) {		
			getMenuInflater().inflate(R.menu.menu_list, menu);
			return true;
		}
	
	 public boolean onOptionsItemSelected(MenuItem item) {		
			int id = item.getItemId();
			if (id == R.id.go_to_map) {
				finish();				
				return true;
			} else if (id == R.id.add_new_cache) {
				Intent i = new Intent(this, DetailActivity.class);
				startActivity(i);
			} else if (id == android.R.id.home) {
				finish();
				return true;
			} else if (id == R.id.help) {
				Intent k = new Intent(this, HelpPage.class);
				k.putExtra(MainActivity.HELP_EXTRA, "geocache_list.html");
				startActivity(k);
			} else if (id == R.id.export_database) helper.exportDB(this);		
			
			else if (id == R.id.import_database) helper.importDB(this);
			
			return super.onOptionsItemSelected(item);
		}


	private void initList() {
		 if(model!=null) {
			 stopManagingCursor(model);
			 model.close();
		 }
		 
		 model=helper.getAll();
		 startManagingCursor(model);
		 
		 adapter=new CacheAdapter(model);
		 
		 ListView lijst = (ListView)findViewById(R.id.listView1);
			lijst.setAdapter(adapter);
			lijst.setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?>parent, View view, int position,
						long id) {
					Intent i=new Intent(LijstActivity.this, DetailActivity.class);
	
					i.putExtra(MainActivity.ID_EXTRA, String.valueOf(id));
					startActivity(i);
					
				}			
			});
		}
	
	class CacheAdapter extends CursorAdapter {
		CacheAdapter(Cursor c) {
			super(LijstActivity.this,c);
		}
		
		@Override
		public void bindView(View row, Context ctxt, Cursor c) {
			CacheHolder holder = (CacheHolder)row.getTag();
			
			holder.populateFrom(c, helper);
			
		}
		
		@Override
		public View newView(Context ctxt, Cursor c, ViewGroup parent) {
			LayoutInflater inflater=getLayoutInflater();
			View row=inflater.inflate(R.layout.row,  parent,false);
			CacheHolder holder = new CacheHolder(row);
			row.setTag(holder);
			return (row);
		}
	}
	
	static class CacheHolder {
		private TextView name=null;
		private TextView distance=null;
		private ImageView type=null;
		private ImageView found=null;
		
		CacheHolder (View row) {
			
			name=(TextView)row.findViewById(R.id.row_name);
			type=(ImageView)row.findViewById(R.id.image_type);
			found=(ImageView)row.findViewById(R.id.image_found);
			distance=(TextView)row.findViewById(R.id.distance);
		}
		
		void populateFrom(Cursor c, CacheHelper helper) {
			
			name.setText(String.valueOf(helper.getName(c)));
			type.setImageResource(typeArray[helper.getType(c)]);			

			distance.setText(String.valueOf(gps.distance(helper.getLat(c), helper.getLon(c))));
			
			
			if (helper.getFound(c).equals("true")) {
				found.setImageResource(R.drawable.checked_no_box_small);
				found.setVisibility(View.VISIBLE);
			
			} else found.setVisibility(View.GONE);
		}
	}
}
