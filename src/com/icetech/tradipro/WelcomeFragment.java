package com.icetech.tradipro;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class WelcomeFragment extends Fragment implements OnClickListener,ConnectionCallbacks, OnConnectionFailedListener{

	View rootView;
	private GoogleApiClient mGoogleApiClient;
	private final static String LOG_TAG = "WelcomeFragment";
	private final int RC_SIGN_IN = 0;
	private ProgressDialog sign_in_progress;
	private boolean mIntentInProgress ;
	private static final int DIALOG_GET_GOOGLE_PLAY_SERVICES = 1;

	private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;
	
	public WelcomeFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API)
        .addScope(Plus.SCOPE_PLUS_LOGIN)
        .addScope(Plus.SCOPE_PLUS_PROFILE)
        .build();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.fragment_welcome,container, false);
		
		((Button) rootView.findViewById(R.id.buttonLogin)).setOnClickListener(this);
		((Button) rootView.findViewById(R.id.buttonSignUp)).setOnClickListener(this);
		(rootView.findViewById(R.id.sign_in_button)).setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.buttonLogin:{
			// causes the login activity to be started
			startActivity(new Intent(getActivity(), LoginActivity.class));
		}
			break;
		case R.id.buttonSignUp:{
			//causes the signup activity to be started			
			startActivity(new Intent(getActivity(), SignupActivity.class));
		}
			break;
		case R.id.sign_in_button:{
			//causes signing in with Google
			int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
			 if (available != ConnectionResult.SUCCESS) {
	                onCreateDialog(DIALOG_GET_GOOGLE_PLAY_SERVICES);
	                return;
	            }else{
            		mGoogleApiClient.connect();
            		sign_in_progress = ProgressDialog.show(getActivity(), "Signing In", "Connecting to Google.\n Please Wait ...");
            	}
			
		}
			break;
		}
	}
	@Override
	public void onStop()
	{
		super.onStop();
		mGoogleApiClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		sign_in_progress.dismiss();
		 if (!mIntentInProgress && result.hasResolution()) {
			    try {
			      mIntentInProgress = true;

			       }finally{
			    	   
			       }
			  }
	      Toast.makeText(getActivity(), "Connection Failed. Please try again", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		sign_in_progress.dismiss();
		String name = "Welcome ", personName = null, personEmail = null, personLanguage = null;
		if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) 
		{
		    Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
		    personName = currentPerson.getDisplayName(); //full display name
		    personEmail = currentPerson.getId(); //may be email
		    personLanguage = currentPerson.getLanguage();
		    
		}
		
		Intent go = new Intent(getActivity(), HomeActivity.class);
		go.putExtra("usrname", name);
		startActivity(go);
		Toast.makeText(getActivity(), "Details: Name: "+personName+"\nEmail: "+personEmail+"\nLanguage: "+personLanguage, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onConnectionSuspended(int cause) {
		sign_in_progress.dismiss();
		switch(cause){
		case GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST:
			Toast.makeText(getActivity(), "Connection Suspended. "+getResources().getString(R.string.common_google_play_services_network_error_text),
					Toast.LENGTH_LONG).show();
			break;
		case GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED:
			Toast.makeText(getActivity(), "ConnectionSuspended. "+getResources().getString(R.string.common_google_play_services_unknown_issue)+".\nService has disconnected",
					Toast.LENGTH_LONG).show();	
			break;
		}
		
	}
	
	protected Dialog onCreateDialog(int id) {
	        if (id != DIALOG_GET_GOOGLE_PLAY_SERVICES) {
	        	
	            return null;//super.onCreateDialog(id);
	        }
	        //checks for the availability of google play services on user's device
	        //creates a dialog box that enables the user to be able to install play servies 
	        // on their devices.
	        int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
	        if (available == ConnectionResult.SUCCESS) {
	            return null;
	        }
	        if (GooglePlayServicesUtil.isUserRecoverableError(available)) {
	            return GooglePlayServicesUtil.getErrorDialog(
	                    available, getActivity(), REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES);
	        }
	        return new AlertDialog.Builder(getActivity())
	                .setMessage(R.string.plus_generic_error)
	                .setCancelable(true)
	                .create();
	    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == RC_SIGN_IN) {
	        mIntentInProgress = false;
	        if (resultCode != Activity.RESULT_OK) {
	            //mSignInClicked = false;
	        	Log.d(LOG_TAG, "ResultCode = "+resultCode);
	          }
	        
	        if (!mGoogleApiClient.isConnecting()) {
	        	try{
	        		Toast.makeText(getActivity(), "Sign In error. Please try again. ", Toast.LENGTH_LONG).show();
	        	}
	        	catch(Exception e)
	        	{
	        		e.printStackTrace();
	        		Log.d("Connection error", ""+e.getMessage());// sign_in_progress.
	        	}
	        }
	      }
	}

}
