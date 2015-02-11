package com.icetech.tradipro;

import java.util.Date;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.icetech.data.model.ApplicationModel.Language;
import com.icetech.data.model.ApplicationModel.TranslationTable;

/**
 * Edit a translation set
 * @author root
 *
 */
public class EditLanguageFragment extends Fragment implements OnClickListener{

	View rootView;
	String mLang1, mLang2, mLang3; //the three languages of choice
	int id_origin=0, id_destination1=0, id_destination2=0;
	EditText mEditText1, mEditText2, mEditText3;
	Button saveTranslation;
	SharedPreferences mSp;
	
	public EditLanguageFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		mSp = PreferenceManager.getDefaultSharedPreferences(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_edit_language,
				container, false);
		mEditText1 = (EditText) rootView.findViewById(R.id.editText1);
		mEditText2 = (EditText) rootView.findViewById(R.id.editText2);
		mEditText3 = (EditText) rootView.findViewById(R.id.editText3);
		saveTranslation = (Button) rootView.findViewById(R.id.btnSaveTranslation);
		saveTranslation.setOnClickListener(this);
		
		TextView ltextview = (TextView) rootView.findViewById(R.id.textorigin);
		TextView ltext2 = (TextView) rootView.findViewById(R.id.textdestination1);
		TextView ltext3 = (TextView) rootView.findViewById(R.id.textdestination2);
		mLang1 = mSp.getString("lang1", "");
		mLang2 = mSp.getString("lang2", "");
		mLang3 = mSp.getString("lang3", "");
		ltextview.setText(mLang1);
		ltext2.setText(mLang2);
		ltext3.setText(mLang3);
		
		return rootView;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		//get the id's of the user's preferred languages
		Cursor lCursor = getActivity().getContentResolver().query(Language.buildUriByName(mLang1)	,
					new String[]{Language.COLUMN_ID},null, null,null);
		Cursor cursor1 = getActivity().getContentResolver().query(Language.buildUriByName(mLang2)	,
				new String[]{Language.COLUMN_ID},null, null,null);
		Cursor cursor2 = getActivity().getContentResolver().query(Language.buildUriByName(mLang3)	,
				new String[]{Language.COLUMN_ID},null, null,null);
		
		try {
			if(lCursor.moveToFirst() && cursor1.moveToFirst() && cursor2.moveToFirst()){
				id_origin = lCursor.getInt(lCursor.getColumnIndex(Language.COLUMN_ID));
				id_destination1 = cursor1.getInt(cursor1.getColumnIndex(Language.COLUMN_ID));
				id_destination2 = cursor2.getInt(cursor2.getColumnIndex(Language.COLUMN_ID));
				//Toast.makeText(getActivity(), "ID's = "+id_origin+" "+id_destination1+ " "+id_destination2, Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lCursor.close();
		cursor1.close();
		cursor2.close();
		//Toast.makeText(getActivity(),"Languages: "+mLang1+" "+mLang2+" "+mLang3,Toast.LENGTH_LONG).show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.btnSaveTranslation){
			//process the translation
			if(mEditText1.getText().toString().length() > 0 && 
					(mEditText2.getText().toString().length()> 0 ||
							mEditText3.getText().toString().length()>0)){
				//add translation
				String textOrigin  = mEditText1.getText().toString();
				String textdestination = mEditText2.getText().toString();
				String textDestination1 = mEditText3.getText().toString();
				String author = mSp.getString("username", "anonymous");
				
				if(!textdestination.isEmpty()){
					String date = "";
					try {
						date = Date.class.newInstance().toString();
					} catch (java.lang.InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ContentValues cv = new ContentValues();
							cv.put(TranslationTable.COLUMN_STATUS, "PUBLISHED");
							cv.put(TranslationTable.COLUMN_TEXTE_DESTINATION, textdestination);
							cv.put(TranslationTable.COLUMN_TEXTE_ORIGIN, textOrigin);
							cv.put(TranslationTable.COLUMN_ID_DEST, id_destination1);
							cv.put(TranslationTable.COLUMN_TRANS_AUTHOR, author);
							cv.put(TranslationTable.COLUMN_DATE_ADDED, date);
							cv.put(TranslationTable.COLUMN_LANG_ID, id_origin);
				 Uri luri = getActivity().getContentResolver().insert(TranslationTable.CONTENT_URI, cv);
				 Log.d("Add Translation", luri.toString());
				 Toast.makeText(getActivity(), getResources().getString(R.string.translated)+" "+mLang2, Toast.LENGTH_LONG).show();
				
				}
				if(!textDestination1.isEmpty()){
					String date = "";
					try {
						date = Date.class.newInstance().toString();
					} catch (java.lang.InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ContentValues cv = new ContentValues();
					cv.put(TranslationTable.COLUMN_STATUS, "PUBLISHED");
					cv.put(TranslationTable.COLUMN_TEXTE_DESTINATION, textDestination1);
					cv.put(TranslationTable.COLUMN_TEXTE_ORIGIN, textOrigin);
					cv.put(TranslationTable.COLUMN_ID_DEST, id_destination2);
					cv.put(TranslationTable.COLUMN_TRANS_AUTHOR, author);
					cv.put(TranslationTable.COLUMN_DATE_ADDED, date);
					cv.put(TranslationTable.COLUMN_LANG_ID, id_origin);
					 Uri luri = getActivity().getContentResolver().insert(TranslationTable.CONTENT_URI, cv);
					 Log.d("Add Translation", luri.toString());
					 Toast.makeText(getActivity(), getResources().getString(R.string.translated)+" "+mLang3, Toast.LENGTH_LONG).show();
				}
				
			}else{
				mEditText1.setError(getResources().getString(R.string.error_field_required));
			}
				
		}	
	}
}
