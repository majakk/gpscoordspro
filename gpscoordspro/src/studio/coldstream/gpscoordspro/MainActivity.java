package studio.coldstream.gpscoordspro;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener{
    /** Called when the activity is first created. */
	
	static final String TAG = "Main_GPScoordsPRO"; // for Log
	
	LocationManager lm;
	PowerManager pm;
	PowerManager.WakeLock wl;	
	
	int noOfFixes = 0;
	
	TextView fixText;
	
	TextView latText;
	TextView lonText;

	TextView latText2;
	TextView lonText2;
	
	TextView utmText;
	
	Button lb;
	Button sb;
	
	Location globalLocation;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.acquire();
        
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        
        globalLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
        fixText = (TextView)findViewById(R.id.fixText);
		
		latText = (TextView)findViewById(R.id.latText);
		lonText = (TextView)findViewById(R.id.lonText);
		
		latText2 = (TextView)findViewById(R.id.latText2);
		lonText2 = (TextView)findViewById(R.id.lonText2);
		
		Typeface font = Typeface.createFromAsset(getAssets(), "digit.ttf");
	    		
		latText.setTypeface(font);     
		lonText.setTypeface(font);
		
		utmText = (TextView)findViewById(R.id.utmText);
		
		lb = (Button)findViewById(R.id.locButton);
		lb.setOnClickListener(new OnClickListener() 
        {
			public void onClick(View v) 
            {                
    			Log.d(TAG, "Click!");    			
    			
    			DecimalFormat maxDigitsFormatter = new DecimalFormat("##0.000000");
    			StringBuilder sb = new StringBuilder();
    			
    			if(globalLocation != null){
	    			sb.append("geo:");
	    			sb.append(maxDigitsFormatter.format(globalLocation.getLatitude()));
	    			sb.append(",");
	    			sb.append(maxDigitsFormatter.format(globalLocation.getLongitude()));
	    			sb.append("?q=");
	    			sb.append(maxDigitsFormatter.format(globalLocation.getLatitude()));
	    			sb.append(",");
	    			sb.append(maxDigitsFormatter.format(globalLocation.getLongitude()));
    			}
    			
    			if(sb.toString() != ""){
    				startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(sb.toString())));
    			}
    			else{
    				;//Toast.makeText(getBaseContext(), "Could not find GPS fixation!", Toast.LENGTH_SHORT).show();
    			}
            }
        });
		
		sb = (Button)findViewById(R.id.shareButton);
		sb.setOnClickListener(new OnClickListener() 
        {
			public void onClick(View v) 
            {                
    			Log.d(TAG, "Click!");    			
    			
    			DecimalFormat maxDigitsFormatter = new DecimalFormat("##0.000000");
    			StringBuilder sb = new StringBuilder();
    			
    			if(globalLocation != null){
	    			sb.append("geo:");
	    			sb.append(maxDigitsFormatter.format(globalLocation.getLatitude()));
	    			sb.append(",");
	    			sb.append(maxDigitsFormatter.format(globalLocation.getLongitude()));	    			
    			}
    			
    			if(sb.toString() != ""){    				
    				Intent i = null;
    		        String msg ="Shared location from GPS Coordinates Pro";

    		        i = new Intent(Intent.ACTION_SEND);
    		        i.setType("text/plain");

    		        i.putExtra(Intent.EXTRA_TEXT, sb.toString());
    		        i.putExtra(Intent.EXTRA_SUBJECT, msg);

    		        startActivity(Intent.createChooser(i, "Share Location"));    				
    			}
    			else{
    				;//Toast.makeText(getBaseContext(), "Could not find GPS fixation!", Toast.LENGTH_SHORT).show();
    			}
            }
        });
    }
    
    public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Intent mainIntent = new Intent(MainActivity.this,MainActivity.class);
		MainActivity.this.startActivity(mainIntent);
		MainActivity.this.finish();
	}
    
    public void share() {
        Intent i = null;
        String msg ="Shared location from GPS Coordinates Pro";

        i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");

        i.putExtra(Intent.EXTRA_TEXT, msg);
        i.putExtra(Intent.EXTRA_SUBJECT, "Shared location from GPS Coordinates Pro");

        startActivity(Intent.createChooser(i, "Share " + getText(R.string.app_name)));
    }
    
    @Override
	protected void onResume() {
		/*
		 * onResume is is always called after onStart, even if the app hasn't been
		 * paused
		 *
		 * add location listener and request updates every 1000ms or 10m
		 */
		
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1f, this);
		
		super.onResume();
	}
    
    @Override
	protected void onPause() {
		/* GPS, as it turns out, consumes battery like crazy */
		lm.removeUpdates(this);
		super.onResume();
	}
    
    public void onLocationChanged(Location location) {
		Log.v(TAG, "Location Changed");
		
		globalLocation = location;
		
		DecimalFormat maxDigitsFormatter = new DecimalFormat("##0.000000");
		
		noOfFixes++;
		fixText.setText("GPS position fixes: " + Integer.toString(noOfFixes));
		
		//location.setLatitude(59.320486);
		//location.setLongitude(18.067017);
		
		latText.setText(maxDigitsFormatter.format(location.getLatitude()));
		lonText.setText(maxDigitsFormatter.format(location.getLongitude()));
				
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		
		//DecimalFormat maxDigitsFormatterDeg = new DecimalFormat("##0");
		//DecimalFormat maxDigitsFormatterMin = new DecimalFormat("#0");
		//DecimalFormat maxDigitsFormatterSec = new DecimalFormat("#0.0###");
		
		String adam;
		String bertil[];
		
		adam = Location.convert(location.getLatitude(), Location.FORMAT_SECONDS);
		bertil = adam.split("\\:");
		
		sb1.append(bertil[0]);
		sb1.append("° ");
		sb1.append(bertil[1]);
		sb1.append("' ");
		sb1.append(bertil[2]);
		sb1.append("''");
		
		Log.v(TAG, Location.convert(location.getLatitude(), Location.FORMAT_SECONDS));
		
		latText2.setText(sb1);
		
		
		adam = Location.convert(location.getLongitude(), Location.FORMAT_SECONDS);
		bertil = adam.split("\\:");
		
		sb2.append(bertil[0]);
		sb2.append("° ");
		sb2.append(bertil[1]);
		sb2.append("' ");
		sb2.append(bertil[2]);
		sb2.append("''");
		
		Log.v(TAG, Location.convert(location.getLongitude(), Location.FORMAT_SECONDS));
		
		lonText2.setText(sb2);
		
		//Conversion to UTM
		CoordinateConversion cesar = new CoordinateConversion();			
		String david = cesar.latLon2UTM(location.getLatitude(), location.getLongitude());
		
		utmText.setText("UTM Coordinates: " + david);
    }
    
    public void onProviderEnabled(String provider) {
		Log.v(TAG, "Enabled");
		Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show();

	}
    
    public void onProviderDisabled(String provider) {
		/* this is called if/when the GPS is disabled in settings */
		Log.v(TAG, "Disabled");

		/* bring up the GPS settings */
		Intent intent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);
	}
    
    public void onStatusChanged(String provider, int status, Bundle extras) {
		/* This is called when the GPS status alters */
		switch (status) {
		case LocationProvider.OUT_OF_SERVICE:
			Log.v(TAG, "Status Changed: Out of Service");
			//Toast.makeText(this, "Status Changed: Out of Service",
			//		Toast.LENGTH_SHORT).show();
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			Log.v(TAG, "Status Changed: Temporarily Unavailable");
			//Toast.makeText(this, "Status Changed: Temporarily Unavailable",
			//		Toast.LENGTH_SHORT).show();
			break;
		case LocationProvider.AVAILABLE:
			Log.v(TAG, "Status Changed: Available");
			//Toast.makeText(this, "Status Changed: Available",
			//		Toast.LENGTH_SHORT).show();
			break;
		}
	}
    
    protected void onStop() {
		/* may as well just finish since saving the state is not important for this toy app */
		wl.release();
		//Log.v(tag, "Released wlock??");		
		
		finish();
		super.onStop();
	}    
    
    
}
