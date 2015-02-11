package com.icetech.content;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.icetech.data.model.ApplicationModel.Language;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Reads data form the server given the url and the context of the application, inserts into the local Database.
 * @author root
 *
 */
public class FetchUpdates extends AsyncTask<String,Void, Integer> {

	Context context;
	private String serverMessage;
	
	public FetchUpdates(Context ctx) {
		// TODO Auto-generated constructor stub
		context = ctx;
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		// TODO Auto-generated method stub
		try{ 
			int count = 0;
			URL url = new URL(params[0]);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			BufferedReader reader = new BufferedReader (new InputStreamReader(conn.getInputStream())); 
			final StringBuilder sb = new StringBuilder(); 
			String line = ""; 
			// Read Server Response
			while((line = reader.readLine()) != null) 
			{ 
				sb.append(line);
				break;
			}
				serverMessage = sb.toString(); //should be a json string 
				JSONObject lJson = new JSONObject(serverMessage);
				JSONArray jsonId = lJson.getJSONArray("_id");
				JSONArray jsonName = lJson.getJSONArray("lang_name");
				JSONArray jsonDescription = lJson.getJSONArray("lang_description");
				JSONArray jsonDate = lJson.getJSONArray("date_add");
				//JSONArray jsonStatus = lJson.getJSONArray("_id");
				
				Vector<ContentValues> mVector = new Vector<ContentValues>();
				for(int i=0; i <jsonId.length(); i++){
					ContentValues cv = new ContentValues();
					cv.put(Language.COLUMN_ID, jsonId.getInt(i));
					cv.put(Language.COLUMN_NAME, jsonName.getString(i));
					cv.put(Language.COLUMN_DESCRIPTION, jsonDescription.getString(i));
					cv.put(Language.COLUMN_DATE_ADDED, jsonDate.getString(i));
					
					mVector.add(cv);
				}
				//add the content values to the SQLite Database
				if(mVector.size() > 0){
					ContentValues[] cvalues = new ContentValues[mVector.size()];
					mVector.toArray(cvalues);
					count = context.getContentResolver().bulkInsert(Language.CONTENT_URI, cvalues);
				}
				Log.d("UpdateTask", "Inserted "+count+" new values");
				return count;
			
			}
			catch(Exception e){ 
				e.printStackTrace();
					return -1; 
				}	
	}
	
	@Override
	protected void onPostExecute(Integer h){
		
		if(h != null & h.intValue()>0){
			Toast.makeText(context, "New updates Downloaded", Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(context, "No new Updates", Toast.LENGTH_LONG).show();
		}
		
	}

}
