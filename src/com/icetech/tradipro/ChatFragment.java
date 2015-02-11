package com.icetech.tradipro;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.icetech.data.model.ApplicationModel.Discussions;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * Displays a list of chats/discussions from the app's content provider
 * @author root
 *
 */
public class ChatFragment extends Fragment implements LoaderCallbacks<Cursor>, OnItemClickListener{

	ChatAdapter mAdapter ;
	ListView mListView;
	
	private final String TAG = "ChatFragment";
	
	public ChatFragment() {
		// TODO Auto-generated constructor stub
	}
	@Override	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//set so that this fragment can handle menu options
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater minflater) {
		minflater.inflate(R.menu.chat, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
		switch(id){
		case R.id.action_new_chat:
			startActivity(new Intent(getActivity(), CreateChatActivity.class));
			return true;
			default:
				return true;
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_chat, container,false);
		mAdapter = new ChatAdapter(getActivity(), null, 0);
		mListView = (ListView) rootView.findViewById(R.id.listView1);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		new FetchDiscussionTask().execute("");
		
		getLoaderManager().initLoader(1, null, this);
		
		return rootView;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		Log.d(TAG, "Loading Loader ...");
		
		return new CursorLoader(getActivity(),
				Discussions.CONTENT_URI,
				null,
				null,
				null,
				Discussions.COLUMN_ID+" DESC"
				);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// TODO Auto-generated method stub
		mAdapter.swapCursor(cursor);
		Log.d(TAG, "Loader Finished. ContentLoaded: "+loader.dataToString(cursor));
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		loader.reset();
		mAdapter.swapCursor(null);
		Log.d(TAG, "Cursor loader reset");
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ChatAdapter adapter = (ChatAdapter) parent.getAdapter();
		Cursor cursor = adapter.getCursor();
		//pass all the data from this cursor to the next 
		int did = cursor.getInt(cursor.getColumnIndex(Discussions.COLUMN_ID));
		String title = cursor.getString(cursor.getColumnIndex(Discussions.COLUMN_COMMENT_TITLE));
		String text = cursor.getString(cursor.getColumnIndex(Discussions.COLUMN_COMMENT));
		String date = cursor.getString(cursor.getColumnIndex(Discussions.COLUMN_DATE));
		Intent sIntent = new Intent(getActivity(), CommentsActivity.class);
		sIntent.putExtra("title", title);
		sIntent.putExtra("text", text);
		sIntent.putExtra("date", date);
		sIntent.putExtra("id", did);
		
		startActivity(sIntent);
		
	}

	private class FetchDiscussionTask extends AsyncTask<String, Void ,String> {
    	private final String USER_AGENT = "Mozilla/5.0";
    	
    	@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = "http://icetech.webege.com/nolangues.php";
			try {
				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
				//add request header
				con.setRequestMethod("POST");
				con.setRequestProperty("User-Agent", USER_AGENT);
				con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
				
				String urlParameters = "action=read";
 
				// Send post request
				con.setDoOutput(true);
				
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				wr.writeBytes(urlParameters); //post request parameter
				wr.flush();
				wr.close();
				System.out.println("\nSending 'POST' request to URL : " + url);
				
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
 
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				JSONObject jobj = new JSONObject(response.toString());
				JSONArray jid = jobj.getJSONArray("_id");
				JSONArray jauthor = jobj.getJSONArray("d_author");
				JSONArray jcontent = jobj.getJSONArray("d_comment");
				JSONArray jdate =  jobj.getJSONArray("d_date");
				JSONArray jtitle = jobj.getJSONArray("d_title");
				
				Vector<ContentValues> mVector = new Vector<ContentValues>();
				for(int i=0; i <jid.length(); i++){
					ContentValues cv = new ContentValues();
					cv.put(Discussions.COLUMN_ID, jid.getInt(i));
					cv.put(Discussions.COLUMN_AUTHOR, jauthor.getString(i));
					cv.put(Discussions.COLUMN_COMMENT, jcontent.getString(i));
					cv.put(Discussions.COLUMN_DATE, jdate.getString(i));
					cv.put(Discussions.COLUMN_COMMENT_TITLE, jtitle.getString(i));
					mVector.add(cv);
				}
				int count = 0;
				//add the content values to the SQLite Database
				if(mVector.size() > 0){
					ContentValues[] cvalues = new ContentValues[mVector.size()];
					mVector.toArray(cvalues);
					count = getActivity().getContentResolver().bulkInsert(Discussions.CONTENT_URI, cvalues);
				}
				Log.d("UpdateTask", "Inserted "+count+" new values");
				
				in.close();
				return response.toString();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.getMessage();
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.getMessage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.getMessage();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.getMessage();
			}
		}
		
		@Override
		protected void onPostExecute(String status){

			if(status != null){
				Log.d("Discussions", status);
				if(status.contains("success")){
					Toast.makeText(getActivity(), "Discussions loaded", Toast.LENGTH_LONG).show();
				}
			}
		}
    }

}
