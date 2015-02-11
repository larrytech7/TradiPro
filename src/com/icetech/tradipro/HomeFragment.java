package com.icetech.tradipro;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.icetech.content.FetchUpdates;
import com.icetech.data.model.ApplicationModel.Language;
import com.icetech.data.model.ApplicationModel.TranslationTable;

public class HomeFragment extends Fragment implements OnEditorActionListener,TextToSpeech.OnInitListener, OnClickListener{

	private TextToSpeech tts;
	View rootView;
	EditText langOriginEditText, langdestEditText1, langdestEditText2;
	LinearLayout mLinearLayout;
	String mLang1, mLang2, mLang3;
	SharedPreferences mSp;
	ImageView im1, im2, im3;
	public static int currentLang = 0;
	
	public HomeFragment(){
		//HasOptions(true);
	}

	@Override	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//set so that this fragment can handle menu options
		setHasOptionsMenu(true);
		mSp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		new FetchUpdates(getActivity()).execute("http://icetech.webege.com/nolangues.php?set=1");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 rootView = inflater.inflate(R.layout.fragment_home, container,false);
		 
		 im1 = (ImageView) rootView.findViewById(R.id.img_speakorigin);im1.setOnClickListener(this);
		 im2 = (ImageView) rootView.findViewById(R.id.img_speakprimary);im2.setOnClickListener(this);
		 im3 = (ImageView) rootView.findViewById(R.id.img_speaksecondary);im3.setOnClickListener(this);
		 
		 mLinearLayout = (LinearLayout) rootView.findViewById(R.id.LinearLayout1);
		 
		 langOriginEditText = (EditText) rootView.findViewById(R.id.langueOriginTextview);
		 langdestEditText1 = (EditText) rootView.findViewById(R.id.targetLanguageTextview1);
		 langdestEditText2 = (EditText) rootView.findViewById(R.id.targetLanguageTextview2);
		 TextView welcome = (TextView) rootView.findViewById(R.id.welcomeTextview);
		 	
		 	mLang1 = mSp.getString("lang1", "");
			mLang2 = mSp.getString("lang2", "");
			mLang3 = mSp.getString("lang3", "");
			
		 welcome.append(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("username", ""));
		
		 langOriginEditText.setOnEditorActionListener(this);
		 
		return rootView;
	}
	@Override
	public void onResume()
	{
		super.onResume();
		String themeColor = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("theme", "BLUE");
		switch(themeColor){
		case "BLUE":
			mLinearLayout.setBackgroundColor(Color.BLUE);
			break;
		case "RED":
			mLinearLayout.setBackgroundColor(Color.RED);
			break;
		case "GREEN":
			mLinearLayout.setBackgroundColor(Color.GREEN);
			break;
		case "TEAL":
			mLinearLayout.setBackgroundColor(Color.green(50));
			break;
		}
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater minflater) {

		// Inflate the menu; this adds items to the action bar if it is present.
		minflater.inflate(R.menu.home, menu);
		MenuItem  mitem = menu.findItem(R.id.action_share);
		ShareActionProvider lShareActionProvider;
		//get an action provider and bind to this menu item to reference it
		lShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(mitem);
		if(lShareActionProvider !=null){
			
			lShareActionProvider.setShareIntent(getShareIntent());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
		case R.id.action_editprofile:  //edit a user's profile
			startActivity(new Intent(getActivity(), ProfileSettingsActivity.class));
			return true;
		case R.id.action_forums: //launches the forums activity to talk about translations
			startActivity(new Intent(getActivity(), ChatActivity.class));
			return true;
		case R.id.action_editLanguage: //launches the edit language activity to add a translation
			startActivity(new Intent(getActivity(), EditLanguageActivity.class));
			return true;
		case R.id.action_viewlanguages: //launches a languages listing activity where a user can add an available language
			startActivity(new Intent(getActivity(), LanguagesActivity.class));
			return true;
		case R.id.action_update:
			//launch a sync task
			new FetchUpdates(getActivity()).execute("http://icetech.webege.com/nolangues.php?set=1");
			return true;
		case R.id.action_about:
			TextView edt = new TextView(getActivity());
			edt.setTextSize(22);
			edt.setText(Html.fromHtml(getResources().getString(R.string.about_text)));
			edt.setClickable(false);
			edt.setEnabled(false);
			edt.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
			Dialog d = new Dialog(getActivity());
			d.setTitle("About ");
			d.setCancelable(true);
			d.setContentView(edt);
			d.show();
		default:
			return super.onOptionsItemSelected(item);
	
		}
	}
	
	@SuppressLint("InlinedApi")
	private Intent getShareIntent(){
		String shareString = Html.fromHtml(getActivity().getResources().getString(R.string.share_text)).toString();
//		Uri sUri = Uri.parse(shareString);
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
			.setType("text/plain")
			.putExtra(Intent.EXTRA_TEXT, "NosLangues: "+shareString)
			.putExtra(Intent.EXTRA_HTML_TEXT, shareString);

			return shareIntent;
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		int kcode = event.getKeyCode();
		int id_origin =0, id_dest1=0,id_dest2=0;
		if(actionId == R.id.langueOriginTextview || 
				actionId == EditorInfo.IME_ACTION_DONE ||
				actionId == EditorInfo.IME_ACTION_SEND ||
				actionId == EditorInfo.IME_ACTION_NEXT ||
				actionId == EditorInfo.IME_NULL){
			if(kcode == KeyEvent.KEYCODE_ENTER || kcode == KeyEvent.KEYCODE_SPACE ){
				//langdestEditText1.setText(v.getText());
				//langdestEditText2.setText(v.getText());
				
			Cursor lCursor = getActivity().getContentResolver().query(Language.buildUriByName(mLang1)	,
						new String[]{Language.COLUMN_ID},null, null,null);
				if(lCursor.moveToFirst())
					id_origin = lCursor.getInt(lCursor.getColumnIndex(Language.COLUMN_ID));
				lCursor.close();
			Cursor cursor1 = getActivity().getContentResolver().query(Language.buildUriByName(mLang2)	,
					new String[]{Language.COLUMN_ID},null, null,null);
				if(cursor1.moveToFirst())
					id_dest1 = cursor1.getInt(cursor1.getColumnIndex(Language.COLUMN_ID));
				cursor1.close();
			Cursor cursor2 = getActivity().getContentResolver().query(Language.buildUriByName(mLang3)	,
					new String[]{Language.COLUMN_ID},null, null,null);
				if(cursor2.moveToFirst())
					id_dest2 = cursor2.getInt(cursor2.getColumnIndex(Language.COLUMN_ID));
				cursor2.close();
				//perform translation
			    String translate = v.getText().toString();//.toLowerCase(Locale.getDefault());
				//translateText = translateText.substring(0, translateText.length() -1 );
				//translate to the first language if available
			Cursor resultcursor = getActivity().getContentResolver().query(TranslationTable.buildUriByName(mLang1),
						null,null, new String[]{""+id_origin, ""+id_dest1, translate},null);
				if(resultcursor!= null && resultcursor.moveToFirst()){
					langdestEditText1.setText(resultcursor.getString(resultcursor.getColumnIndex(TranslationTable.COLUMN_TEXTE_DESTINATION)));
					
				}else{
					langdestEditText1.setText("");
				}
				resultcursor.close();
				//translate second language if available
			Cursor rescursor = getActivity().getContentResolver().query(TranslationTable.buildUriByName(mLang1),
					null,null, new String[]{""+id_origin, ""+id_dest2, translate},null);
				if(rescursor != null && rescursor.moveToFirst()){
					langdestEditText2.setText(rescursor.getString(rescursor.getColumnIndex(TranslationTable.COLUMN_TEXTE_DESTINATION)));
				}else{
					langdestEditText2.setText("");
				}
				rescursor.close();
				//v.setText("");
			}
			Log.d("EditorAction", "reading field");
			return true;
		}else
		return false;
	}

	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
				if(status == TextToSpeech.SUCCESS){
				     
		            //SharedPreferences appsettings = PreferenceManager.getDefaultSharedPreferences(getActivity());
		 			//String localeString = "ENGLISH";
		 		
		 		//	Locale loc = new Locale(localeString.toUpperCase(Locale.getDefault()));
		 			
		 			int result = tts.setLanguage(Locale.getDefault());//=tts.setLanguage(Locale.FRENCH);
		 		/*	switch(localeString)
		 			{
		 			case "ENGLISH":
		 				result = tts.setLanguage(Locale.ENGLISH);
		 				break; 
		 			case "FRENCH":
		 				result = tts.setLanguage(Locale.FRENCH);
		 				break;
		 			case "UK":
		 				result = tts.setLanguage(Locale.UK);
		 				break;
		 			case "US":
		 				result = tts.setLanguage(Locale.US);
		 				break;
		 			default:
		 				result = tts.setLanguage(Locale.US);
		 				break;
		 			}*/
		 			 if(result==TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED){
		                 Log.e("error", getResources().getString(R.string.err_lang_unsupported));
		                 Toast.makeText(getActivity(), 
		                		 getResources().getString(R.string.err_lang_unsupported),
		                		 Toast.LENGTH_LONG).show();
		             }else{
		            	 switch(currentLang){
		            	 case 1:
		            		 tts.speak(langOriginEditText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
		            		 break;
		            	 case 2:
		            		 tts.speak(langdestEditText1.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
		            		 break;
		            	 case 3:
		            		 tts.speak(langdestEditText2.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
		            		 break;
		            	 }
		             }
		            
		         }
		         else{
		             Log.e("error", "Initilization Failed!");
		             Toast.makeText(getActivity(), "Initialization Failed", Toast.LENGTH_LONG).show();
		     }
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.img_speakorigin:
			currentLang = 1;
			tts = new TextToSpeech(getActivity(), this);
			break;
		case R.id.img_speakprimary:
			currentLang = 2;
			tts = new TextToSpeech(getActivity(), this);
			break;
		case R.id.img_speaksecondary:
			currentLang = 3;
			tts = new TextToSpeech(getActivity(), this);
			break;
		}
	}

	
	private void proposeTranslation(String text){
		AlertDialog.Builder alertBuilder =  new AlertDialog.Builder(getActivity());
		AlertDialog dialog;
		
		final EditText edt = new EditText(getActivity());
		edt.setTextSize(22);
		edt.setHint("Enter text to propose");
		edt.setMinLines(2);
		edt.setMaxLines(6);
		
		alertBuilder.setTitle("Remote Translation");
		alertBuilder.setView(edt);
		alertBuilder.setCancelable(true);
		alertBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String textToTranslate = edt.getText().toString();
				if(textToTranslate.isEmpty()){
					Toast.makeText(getActivity(), "Text is required", Toast.LENGTH_LONG).show();
				}else{
					//submit to web db
				}
			}
		});
		alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		
		dialog = alertBuilder.create();
		dialog.show();
	}
}
