/*
 * Copyright 2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package yulei.android.client;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.androidtest.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;



import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
// Requires Android 2.2 or higher, Google Play Services on the target device, and an active google account on the device.
import android.widget.Toast;

public class AndroidMobilePushApp extends Activity {
    public static final String CONSUMER_KEY = "75y91ej2f1ymf7";
    public static final String CONSUMER_SECRET = "FZoSuMl41LPQt0vc";
    public static final String OAUTH_CALLBACK_SCHEME = "https";
    public static final String OAUTH_CALLBACK_HOST = "https://www.linkedin.com/uas/oauth/authorize?oauth_token=77--38f94f01-0a42-4514-a839-5a2f8d8a619a";
    public static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;
    
    
    
    private TextView tView;
    private SharedPreferences savedValues;
    private String numOfMissedMessages;
    private GoogleMap mMap;
    JSONParser parser= new JSONParser();
    List<LatLng> list = new ArrayList<LatLng>();
    HeatmapTileProvider mProvider;
    TileOverlay mOverlay ;
    //HeatmapTileProvider mProvider;
   
    // Since this activity is SingleTop, there can only ever be one instance. This variable corresponds to this instance.
    public static Boolean inBackground = true;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        numOfMissedMessages = getString(R.string.num_of_missed_messages);
        setContentView(R.layout.activity_main);
        tView = (TextView) findViewById(R.id.tViewId);
        tView.setMovementMethod(new ScrollingMovementMethod());
        startService(new Intent(this, MessageReceivingService.class));
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        
        Button bt1 = (Button)findViewById(R.id.button1);
        bt1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				myClickHandler(v);		        
			}
        	
        });
        
     // Create a heat map tile provider, passing it the latlngs of the police stations.
         mProvider = new HeatmapTileProvider.Builder()
            .data(list)
            .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        mOverlay =mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

    }

    public void onStop(){
        super.onStop();
        inBackground = true;
    }

    public void onRestart(){
        super.onRestart();
        tView.setText("");;
    }

    public void onResume(){
        super.onResume();
        inBackground = false;
        savedValues = MessageReceivingService.savedValues;
        int numOfMissedMessages = 0;
        if(savedValues != null){
            numOfMissedMessages = savedValues.getInt(this.numOfMissedMessages, 0);
        }
        String newMessage=null;		
		newMessage = getMessage(numOfMissedMessages);

        if(newMessage!=""){
            Log.i("displaying message", newMessage);
            tView.append(newMessage);
        }
    }

    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
    }

    // If messages have been missed, check the backlog. Otherwise check the current intent for a new message.
    private String getMessage(int numOfMissedMessages) {
        String message = "";
        String linesOfMessageCount = getString(R.string.lines_of_message_count);
        if(numOfMissedMessages > 0){
            String plural = numOfMissedMessages > 1 ? "s" : "";
            Log.i("onResume","missed " + numOfMissedMessages + " message" + plural);
            tView.append("You missed " + numOfMissedMessages +" message" + plural + ". Your most recent was:\n");
            for(int i = 0; i < savedValues.getInt(linesOfMessageCount, 0); i++){
                String line = "***"+savedValues.getString("MessageLine"+i, "")+"&&&&";
                message+= (line + "\n");
            }
            NotificationManager mNotification = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotification.cancel(R.string.notification_number);
            SharedPreferences.Editor editor=savedValues.edit();
            editor.putInt(this.numOfMissedMessages, 0);
            editor.putInt(linesOfMessageCount, 0);
            editor.commit();
        }
        else{
            Log.i("onResume","no missed messages");
            Intent intent = getIntent();
            if(intent!=null){
                Bundle extras = intent.getExtras();
                if(extras!=null){

                    String realData = extras.getString("default");
                   // message += "___" +realData +"___";
                    if(realData!= null &&realData.length()>2){
                    //addMarker(decodeJSONforLat(realData),decodeJSONforLon(realData));

							if(realData.length()>2){
								Log.e("here", realData);
							try{

								Toast toast = Toast.makeText(getApplicationContext(), decodeJSONforName(realData)+decodeJSONforTime(realData), Toast.LENGTH_SHORT);
								toast.show();
							Log.e("heres", "sssss");
							//addMarker(decodeJSONforLat(realData),decodeJSONforLon(realData));
							addMarker(decodeJSONforLat(realData),decodeJSONforLon(realData),decodeJSONforName(realData),decodeJSONforSentiment(realData),decodeJSONforScore(realData));
							message = "\n"+decodeJSONforName(realData)+"\n"+decodeJSONforContent(realData)+"\n";
							addHeatMap(decodeJSONforLat(realData),decodeJSONforLon(realData));
							//addMarker((Double)decodeObj.get("latitute"),(Double)decodeObj.get("longitude"),(String)decodeObj.get("time"),(String)decodeObj.get("userName"));
							}catch (Exception e){
								Log.e("exception","Exception throwed");
							}							
							}
                    }
                }
            }
        }
        message+="\n";
        return message;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_clear){
            tView.setText("");
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }
    
    public void addMarker(double latitude, double longtitute,String name,String sentiment,String score){
    	mMap.addMarker(new MarkerOptions()
        .position(new LatLng(latitude, longtitute))
        .title(name+"::"+sentiment+"::"+score));
    }
    
    public Double decodeJSONforLat(String jsonString) throws ParseException, JSONException{
    	JSONObject decodeObj = (JSONObject) parser.parse(jsonString);
    	return (Double)(decodeObj.get("latitude"));
    	
    }
    public Double decodeJSONforLon(String jsonString) throws ParseException, JSONException{
    	JSONObject decodeObj = (JSONObject) parser.parse(jsonString);
    	return (Double)(decodeObj.get("longitude"));
    	
    }
    
    public String decodeJSONforName(String jsonString) throws ParseException, JSONException{
    	JSONObject decodeObj = (JSONObject) parser.parse(jsonString);
    	return (String)(decodeObj.get("userName"));
    	
    }
    
    public String decodeJSONforSentiment(String jsonString) throws ParseException, JSONException{
    	JSONObject decodeObj = (JSONObject) parser.parse(jsonString);
    	return (String)(decodeObj.get("sentiment"));
    	
    }    
    
    public String decodeJSONforScore(String jsonString) throws ParseException, JSONException{
    	JSONObject decodeObj = (JSONObject) parser.parse(jsonString);
    	return (String)(decodeObj.get("score"));
    	
    }
    public String decodeJSONforContent(String jsonString) throws ParseException, JSONException{
    	JSONObject decodeObj = (JSONObject) parser.parse(jsonString);
    	return (String)(decodeObj.get("content"));
    	
    }
    public String decodeJSONforTime(String jsonString) throws ParseException, JSONException{
    	JSONObject decodeObj = (JSONObject) parser.parse(jsonString);
    	return (String)(decodeObj.get("time"));
    	
    }
    private void addHeatMap(Double s1, Double s2) {
       list.add(new LatLng(s1,s2));
        // Create a heat map tile provider, passing it the latlngs of the police stations.
        //HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder().data(list).build();
        // Add a tile overlay to the map, using the heat map tile provider.      
     // Create a heat map tile provider, passing it the latlngs of the police stations.
        //  mProvider = new HeatmapTileProvider.Builder()
          //  .data(list)
            //.build();
        // Add a tile overlay to the map, using the heat map tile provider.
        //TileOverlay mOverlay =
        		//mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
       mProvider.setData(list);
       mOverlay.clearTileCache();
    }
    
  /////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    
    private static final String DEBUG_TAG = "HttpExample";
   
    
    
    public void myClickHandler(View view) {
        // Gets the URL from the UI's text field.
        String stringUrl ="http://localhost:8080/JavaSer2/HelloWorld";                     //urlText.getText().toString();
        ConnectivityManager connMgr = (ConnectivityManager) 
            getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
            //textView.setText("No network connection available.");
        	Log.e("net","No network connection available.");
        }
    }
    
    // Uses AsyncTask to create a task away from the main UI thread. This task takes a 
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
       @Override
       protected String doInBackground(String... urls) {
             
           // params comes from the execute() call: params[0] is the url.
           try {
               //return downloadUrl("http://www.google.com/");
        	   return downloadUrl("http://www.google.com/");
           } catch (IOException e) {
        	   Log.e("h",e.toString());
               return "Unable to retrieve web page. URL may be invalid.";
           }
       }
       // onPostExecute displays the results of the AsyncTask.
       @Override
       protected void onPostExecute(String result) {
          // textView.setText(result);
    	   Log.e("suc",result);
      }
   }
    
 // Given a URL, establishes an HttpUrlConnection and retrieves
 // the web page content as a InputStream, which it returns as
 // a string.
 private String downloadUrl(String myurl) throws IOException {
     InputStream is = null;
     // Only display the first 500 characters of the retrieved
     // web page content.
     int len = 500;
         
     try {
         URL url = new URL(myurl);
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setReadTimeout(50000 /* milliseconds */);
         conn.setConnectTimeout(85000 /* milliseconds */);
         conn.setRequestMethod("POST");
         conn.setDoInput(true);
         // Starts the query
         conn.connect();
         int response = conn.getResponseCode();
         Log.d(DEBUG_TAG, "The response is: " + response);
         is = conn.getInputStream();

         // Convert the InputStream into a string
         String contentAsString = readIt(is, len);
         return contentAsString;
         
     // Makes sure that the InputStream is closed after the app is
     // finished using it.
     } finally {
         if (is != null) {
             is.close();
         } 
     }
 }
 
//Reads an InputStream and converts it to a String.
public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
  Reader reader = null;
  reader = new InputStreamReader(stream, "UTF-8");        
  char[] buffer = new char[len];
  reader.read(buffer);
  return new String(buffer);
}
    
    
    
    
    
}
