package com.icetech.tradipro;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.icetech.data.model.ApplicationModel;

public class SignupFragment extends Fragment implements OnClickListener, OnItemSelectedListener{
	View rootView;
	EditText usernameText,passwordText, fullNameText,emailText;
	Spinner langSpinner1, langSpinner2, langSpinner3;
	public static String username, password, fullname, email, lang1, lang2, lang3;
	Button registerButton;
	StringBuilder urlbuilder = new StringBuilder("http://icetech.webege.com/nolangues.php?");
	ArrayAdapter<String> spinnerAdapter;
	
	public SignupFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_signup,container, false);
		
		usernameText = (EditText) rootView.findViewById(R.id.usernameTextview);
		passwordText = (EditText) rootView.findViewById(R.id.passwordTextview);
		fullNameText = (EditText) rootView.findViewById(R.id.nameTextView);
		emailText = (EditText) rootView.findViewById(R.id.emailTextview);
		registerButton = (Button) rootView.findViewById(R.id.button2);
		registerButton.setOnClickListener(this);
		
		langSpinner1 = (Spinner) rootView.findViewById(R.id.defaultLanguageSpinner);
		langSpinner1.setOnItemSelectedListener(this);
		langSpinner2 = (Spinner) rootView.findViewById(R.id.altLanguageSpinner1);
		langSpinner2.setOnItemSelectedListener(this);
		langSpinner3 = (Spinner) rootView.findViewById(R.id.altLanguageSpinner2);
		langSpinner3.setOnItemSelectedListener(this);
		
		
		new LoadLanguageTask().execute();
			
		
		lang1 = (String) langSpinner1.getAdapter().getItem(0);
		lang2 = (String) langSpinner2.getAdapter().getItem(0);
		lang3 = (String) langSpinner3.getAdapter().getItem(0);
		
		return rootView;
	}
	
	/**
	 * Represents an asynchronous registration task used to register a user.
	 * Registers a new user into the online database
	 * @author Larry_Lite
	 * @param params the user's data to be saved in the database
	 */
	public class UserRegisterTask extends AsyncTask<String, Void, Boolean> {
		
		String registerMessage = "no response";
		ProgressDialog regDialog;
		
		@Override
		protected void onPreExecute()
		{
			 regDialog = ProgressDialog.show(getActivity(), "Creating User",
					 "Your registration is being processed. Please wait.\n");
			 getActivity();
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO: attempt authentication against a network service.
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
				registerMessage = sb.toString();
				return registerMessage.contains("success")? true:false;
			
			}
			catch(Exception e){ 
					registerMessage = e.getMessage();
					return false; 
				}	
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
			if (success) {	
				startActivity(new Intent(getActivity(), HomeActivity.class));
				//save the user into the content provider
				ContentValues cv = new ContentValues();
				cv.put(ApplicationModel.Person.COLUMN_USERNAME, username);
				cv.put(ApplicationModel.Person.COLUMN_PASSWORD, password);
				cv.put(ApplicationModel.Person.COLUMN_NAME, fullname);
				cv.put(ApplicationModel.Person.COLUMN_EMAIL, email);
				cv.put(ApplicationModel.Person.COLUMN_LANG_PRIMARY, lang1);
				cv.put(ApplicationModel.Person.COLUMN_LANG_SECONDARY, lang2);
				cv.put(ApplicationModel.Person.COLUMN_LANG_TERTIARY, lang3);
				
				Uri uri = getActivity().getContentResolver().insert(ApplicationModel.Person.CONTENT_URI, cv);
				 sp.edit()
				 .putString("username", username)
				 .putString("lang1", lang1)
				 .putString("lang2", lang2)
				 .putString("lang3", lang3)
				 .putString("pref_fullname", fullname)
				 .commit();
				Log.d("RegistrationTask", uri.toString());
				getActivity().finish();
			} else {
				// String error = getResources().getString(R.string.error_incorrect_password);
				Toast.makeText(getActivity(), "Registration failed. "+registerMessage, Toast.LENGTH_LONG).show();
			}
			regDialog.dismiss();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
	}

	
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		case R.id.button2:
			//validate fields and log the user in
			if(TextUtils.isEmpty(usernameText.getText().toString()) || usernameText.getText().toString().length() < 4){
				usernameText.setError(getResources().getString(R.string.error_field_required)+". Enter atleast 4 characters ");
				usernameText.requestFocus();
				break;
			}else if(TextUtils.isDigitsOnly(passwordText.getText().toString()) || passwordText.getText().toString().length() < 4){
				passwordText.setError(getResources().getString(R.string.error_invalid_password)+". Enter atleast 4 characters");
				passwordText.requestFocus();
				break;
			}else if(TextUtils.isEmpty(fullNameText.getText().toString())){
				fullNameText.setError(getResources().getString(R.string.error_field_required)+". Enter atleast 4 characters");
				fullNameText.requestFocus();
				
				break;
			}else if(TextUtils.isEmpty(emailText.getText().toString()) || !emailText.getText().toString().contains("@") ){
				emailText.setError(getResources().getString(R.string.error_invalid_email));
				emailText.requestFocus();
				break;
			}else if(lang1.isEmpty() ){
				Toast.makeText(getActivity(), "Default Language is obligatory", Toast.LENGTH_LONG).show();
				break;
			}else{
				username = usernameText.getText().toString();
				password = passwordText.getText().toString();
				fullname = fullNameText.getText().toString();
				fullname = fullname.replace(" ", "+");
				email = emailText.getText().toString();
				urlbuilder.append("register=1")
				.append("&user="+username)
				.append("&p="+password)
				.append("&fn="+fullname)
				.append("&em="+email)
				.append("&l1="+lang1)
				.append("&l2="+lang2)
				.append("&l3="+lang3);
				//we are good to go , start the registration task
				Log.d("Register", urlbuilder.toString());
				new UserRegisterTask().execute(urlbuilder.toString());
			}
			break;
		}	
	}
 /**
 * Takes care of saving the spinner variables when any of them changes
 * @param parent
 * @param view
 * @param position
 * @param id
 */

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
		long id) {
	// TODO Auto-generated method stub
	switch(parent.getId()){
	case R.id.defaultLanguageSpinner:
		lang1 = (String) parent.getItemAtPosition(position);
		break;
	case R.id.altLanguageSpinner1:
		lang2 = (String) parent.getItemAtPosition(position);
		break;
	case R.id.altLanguageSpinner2:
		lang3 = (String) parent.getItemAtPosition(position);
		break;
		}
	}

@Override
	public void onNothingSelected(AdapterView<?> parent) {
	// TODO Auto-generated method stub
	
}
    
	private class LoadLanguageTask extends AsyncTask<Void, Void ,List<String>> {
    	private final String USER_AGENT = "Mozilla/5.0";
    	List<String> myList = new ArrayList<String>();
	   
    	@Override
		protected List<String> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String url = "http://icetech.webege.com/nolangues.php";
			try {
				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
				//add reuqest header
				con.setRequestMethod("POST");
				con.setRequestProperty("User-Agent", USER_AGENT);
				con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
 
				String urlParameters = "languages=language";
 
				// Send post request
				con.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				wr.writeBytes(urlParameters);
				wr.flush();
				wr.close();
 
				System.out.println("\nSending 'POST' request to URL : " + url);
				
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
 
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				
				JSONObject json= new JSONObject(response.toString());
				JSONArray jarray = json.getJSONArray("lang_name");
				for(int i=0; i<jarray.length(); i++){
					myList.add(jarray.getString(i));
				}
				in.close();
				return myList;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return myList;
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return myList;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return myList;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return myList;
			}
			
		}
		
		@Override
		protected void onPostExecute(List<String> list){
			String[] data = new String[]{};
			if(list != null){
				data = new String[list.size()];
				for(int j=0; j<list.size(); j++)
					data[j] = list.get(j);
				spinnerAdapter = new ArrayAdapter<String>(getActivity(),
						R.layout.support_simple_spinner_dropdown_item,
						data);
			}
			try {
				langSpinner1.setAdapter(null);langSpinner2.setAdapter(null);langSpinner3.setAdapter(null);
				langSpinner1.setAdapter(spinnerAdapter);
				langSpinner2.setAdapter(spinnerAdapter);
				langSpinner3.setAdapter(spinnerAdapter);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	
    }
}
