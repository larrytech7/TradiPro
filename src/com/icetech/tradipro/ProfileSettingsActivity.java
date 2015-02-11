package com.icetech.tradipro;

import src.main.java.yuku.ambilwarna.AmbilWarnaDialog;
import src.main.java.yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.icetech.data.model.ApplicationModel;
import com.icetech.data.model.ApplicationModel.Person;

public class ProfileSettingsActivity extends ActionBarActivity {

	public static boolean autoNotify;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_settings);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new SettingsFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		return super.onOptionsItemSelected(item);
	}
	
	public void setAutoPref(View v){
		if(v.getId() == R.id.autoBox){
			CheckBox cb = (CheckBox) findViewById(R.id.autoBox);
			if(cb.isChecked()){
				autoNotify = true;
				
			}else
				autoNotify = false;
		}
	}
	
	public static class SettingsFragment extends Fragment implements OnClickListener, OnItemSelectedListener{

		public static String lang1, lang2, lang3;
		Spinner spin1, spin2, spin3, spintheme;
		EditText fnametext;
		ArrayAdapter<String> mAdapter;
		Button save;
		View rootView;
		
		public SettingsFragment() {
		}
		@Override
		public void onCreate(Bundle bundle){
			super.onCreate(bundle);
			Cursor lCursor = getActivity().getContentResolver().query(ApplicationModel.Language.CONTENT_URI	,
					new String[]{ApplicationModel.Language.COLUMN_ID, ApplicationModel.Language.COLUMN_NAME
				},null	, null,null);
		String[] langs = new String[lCursor.getCount()];
		int i=0;
		while(lCursor.moveToNext()){
			langs[i] = lCursor.getString(lCursor.getColumnIndex(ApplicationModel.Language.COLUMN_NAME));
			i++;
		}
		lCursor.close();
		mAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.support_simple_spinner_dropdown_item,
				langs);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			 rootView = inflater.inflate(
					R.layout.fragment_profile_settings, container, false);
			save = (Button) rootView.findViewById(R.id.btnSavePref);
			save.setOnClickListener(this);
			
			fnametext = (EditText) rootView.findViewById(R.id.pref_fullname);
			fnametext.setText(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("pref_fullname", "Anonym"));
			
			spintheme = (Spinner) rootView.findViewById(R.id.theme_spinner);
			spin1 = (Spinner) rootView.findViewById(R.id.lang1Spinner);spin1.setAdapter(mAdapter);
			spin2 = (Spinner) rootView.findViewById(R.id.lang2Spinner);spin2.setAdapter(mAdapter);
			spin3 = (Spinner) rootView.findViewById(R.id.lang3spinner);spin3.setAdapter(mAdapter);
			spin1.setOnItemSelectedListener(this);
			spin2.setOnItemSelectedListener(this);
			spin3.setOnItemSelectedListener(this);
			
			lang1 = "";
			lang2 = "";
			lang3 = "";
			
			 return rootView;
		}

		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.btnSavePref){
				String fname = fnametext.getText().toString();
				String  theme = (String) spintheme.getSelectedItem();
				String user = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("username", "anonymous");
				
				AmbilWarnaDialog dialog = new AmbilWarnaDialog(getActivity(), 0x00ffaa, new OnAmbilWarnaListener() {
					
					@Override
					public void onOk(AmbilWarnaDialog dialog, int color) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onCancel(AmbilWarnaDialog dialog) {
						// TODO Auto-generated method stub
						
					}
				});
				try{
					dialog.show();
				}catch(Exception ex){
					ex.printStackTrace();
				}
				//save the settings to preferences
			/*	PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
				.putString("lang1", lang1)
				.putString("lang2", lang2)
				.putString("lang3", lang3)
				.putBoolean("auto", autoNotify)
				.putString("theme", theme)
				.putString("pref_fullname", "")
				.commit();
//				Toast.makeText(getActivity(), "theme: "+theme, Toast.LENGTH_LONG).show();
				ContentValues cv = new ContentValues();
				cv.put(Person.COLUMN_NAME, fname);
				int id = getActivity().getContentResolver().update(Person.buildUriByName(user), cv, null, null);
				if(id >0 ){
					Toast.makeText(getActivity(), "Updated Successfully ", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(getActivity(), "Update failed", Toast.LENGTH_LONG).show();
				}*/
			}
		}
		
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			switch(parent.getId()){
			case R.id.lang1Spinner:
				lang1 = (String) parent.getItemAtPosition(position);
			//	Toast.makeText(getActivity(), lang1, Toast.LENGTH_LONG).show();
				break;
			case R.id.lang2Spinner:
				lang2 = (String) parent.getItemAtPosition(position);
				break;
			case R.id.lang3spinner:
				lang3 = (String) parent.getItemAtPosition(position);
				break;
				}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			switch(parent.getId()){
			case R.id.lang1Spinner:
				lang1 = (String) parent.getItemAtPosition(0);
				break;
			case R.id.lang2Spinner:
				lang2 = (String) parent.getItemAtPosition(0);
				break;
			case R.id.lang3spinner:
				lang3 = (String) parent.getItemAtPosition(0);
				break;
				}
		}
	}

}
