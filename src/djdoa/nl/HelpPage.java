package djdoa.nl;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

public class HelpPage extends Activity {
	private WebView browser;
	String URL;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		URL="file:///android_asset/" + getIntent().getStringExtra(MainActivity.HELP_EXTRA);
				
		browser=(WebView)findViewById(R.id.help_view);
		browser.loadUrl(URL);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
