package com.icetech.tradipro;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.icetech.data.model.ApplicationModel;

public class LanguagesFragment extends Fragment implements LoaderCallbacks<Cursor>{

	LanguagesAdapter mAdapter;
	View rootView, proposeView;
	String[] columns = {
		ApplicationModel.Language.COLUMN_ID,
		ApplicationModel.Language.COLUMN_NAME,
		ApplicationModel.Language.COLUMN_DESCRIPTION,
		ApplicationModel.Language.COLUMN_DATE_ADDED
	};
	
	public LanguagesFragment() {
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
		minflater.inflate(R.menu.languages, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
		switch(id){
		case R.id.action_propose_lang:
			//permit a user to propose a language
			proposeLanguage();
			return true;
			default:
				return true;
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 rootView = inflater.inflate(R.layout.fragment_languages,
				container, false);
		 proposeView = inflater.inflate(R.layout.propose_language, (ViewGroup) rootView, false);
		ListView lv = (ListView) rootView.findViewById(R.id.languagesListView);
		mAdapter = new LanguagesAdapter(getActivity(), null	,0);
		lv.setAdapter(mAdapter);
		
		return rootView;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return new CursorLoader(getActivity(),
				ApplicationModel.Language.CONTENT_URI,
				columns,
				null,
				null,
				ApplicationModel.Language.COLUMN_ID+" DESC"
				);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> laoder, Cursor cursor) {
		// TODO Auto-generated method stub
		mAdapter.swapCursor(cursor);
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		mAdapter.swapCursor(null);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		getLoaderManager().initLoader(0, null, this);
	}

	private void proposeLanguage(){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		final StringBuilder urlbuilder = new StringBuilder("http://icetech.webege.com/nolangues.php?");
		
		final EditText langText = (EditText) proposeView.findViewById(R.id.prop_lang); //language to propose
		final EditText descText = (EditText) proposeView.findViewById(R.id.prop_desc); //language description
		alert.setTitle("Propose a Language");
		alert.setMessage("");

		alert.setView(proposeView);

		alert.setPositiveButton("Propose", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String langvalue = langText.getText().toString();
				String descvalue = descText.getText().toString();
				descvalue = descvalue.replace(" ", "+");
				langvalue = langvalue.replace(" ", "+");

				if(langvalue.isEmpty()){
					langText.setError(getResources().getString(R.string.error_field_required));
				}else if(descvalue.isEmpty()){
					descText.setError(getResources().getString(R.string.error_field_required));
				}else{
					//send N/W request to propose language
					urlbuilder.append("propose=1&lang="+langvalue);
					urlbuilder.append("&desc="+descvalue);
					try{
					new ProposeLangTask().execute(urlbuilder.toString());
					}catch(Exception ex){
						ex.printStackTrace();
					}
					Toast.makeText(getActivity(), "Value: "+langvalue+" "+descvalue, Toast.LENGTH_LONG).show();
				}
				// Do something with value!
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
			  
		  }
		});

		alert.show();
	}
	public class ProposeLangTask extends AsyncTask<String,Void, String> {

		private String serverMessage = "";
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try{ 
				
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
					
					return serverMessage;
				
				}
				catch(Exception e){ 
					e.printStackTrace();
						return serverMessage; 
					}	
		}
		
		@Override
		protected void onPostExecute(String h){
			
			if(h != null & h.contains("success")){
				Toast.makeText(getActivity(), "Posted Successfully", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(getActivity(), "Request failed", Toast.LENGTH_LONG).show();
			}	
		}

	}
}
