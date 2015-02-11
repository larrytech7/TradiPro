package com.icetech.tradipro;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.icetech.data.model.ApplicationModel.Language;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CreateChatActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_chat);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Add a new topic of discussion to the forum
	 */
	public static class PlaceholderFragment extends Fragment implements OnClickListener{
		View rootView;
		Spinner dSpinner;
		EditText discussionText;
		Button btnSave;
		
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			rootView = inflater.inflate(R.layout.fragment_create_chat,container, false);
			dSpinner = (Spinner) rootView.findViewById(R.id.discussionSpinner);
			discussionText = (EditText) rootView.findViewById(R.id.edittextdiscussion);
			btnSave = (Button) rootView.findViewById(R.id.btn_save_discussion);
			btnSave.setOnClickListener(this);
			
			return rootView;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String topic = (String) dSpinner.getSelectedItem();
			String message = discussionText.getText().toString();
			String usern = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("username", "anonymous");
			message = message.replace(" " ,"+");
			if(null != topic){
				new CreateDiscussionTask().execute("&title="+topic,"&message="+message, "&author="+usern);
			}else{
				Toast.makeText(getActivity(), "Please choose a language as topic", Toast.LENGTH_LONG).show();
			}
		}
		@Override
		public void onResume(){
			//get list of languages from the cP and populate the spinner
			super.onResume();
			Cursor mCursor = getActivity()
					.getContentResolver()
					.query(Language.CONTENT_URI,
							new String[]{Language.COLUMN_NAME},
							null, null, null);
			String[] langs = new String[mCursor.getCount()];
			
			int k=0;
			while(mCursor.moveToNext()){
				String l =mCursor.getString(mCursor.getColumnIndex(Language.COLUMN_NAME));
				langs[k] = l;
				k++;
			}
			mCursor.close();
			ArrayAdapter<String> madapter = new ArrayAdapter<String>(getActivity(),
					R.layout.support_simple_spinner_dropdown_item,
					langs);
			if(madapter != null)
				dSpinner.setAdapter(madapter);
		}
		
		public class CreateDiscussionTask extends AsyncTask<String, Void ,String> {
	    	private final String USER_AGENT = "Mozilla/5.0";
	    	ProgressDialog pb;
	    	@Override
	    	protected void onPreExecute(){
	    		pb = ProgressDialog.show(getActivity(), "Discussion", "Please wait while we create discussion");
	    	}
	    	
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
					
					String urlParameters = "action=create";
	 
					// Send post request
					con.setDoOutput(true);
					
					DataOutputStream wr = new DataOutputStream(con.getOutputStream());
					wr.writeBytes(urlParameters); //post request parameter
					wr.writeBytes(params[0]);
					wr.writeBytes(params[1]);
					wr.writeBytes(params[2]);
					wr.flush();
					wr.close();
//					con.connect();
					System.out.println("\nSending 'POST' request to URL : " + url);
					
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();
	 
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					
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
				}
			}
			
			@Override
			protected void onPostExecute(String status){
				pb.dismiss();

				if(status != null){
					Log.d("CreateDiscussion", status);
					if(status.contains("success")){
						Toast.makeText(getActivity(), "Discussion successfuly created", Toast.LENGTH_LONG).show();
					}else
					{
						Toast.makeText(getActivity(), "Failed to create discussion. check network", Toast.LENGTH_LONG).show();
					}
				}
				try{
					getActivity().finish();
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
	    }
		
	}
}
