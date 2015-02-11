package com.icetech.tradipro;

import java.text.DateFormat;
import java.util.Date;

import com.icetech.data.model.ApplicationModel.Critics;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class CommentsActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comments);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new CommentsFragment()).commit();
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
	 * Loads and displays all the comments for a given id of a given discussion
	 * comments are loaded from a remote site and added to the listview
	 */
	public static class CommentsFragment extends Fragment implements OnClickListener, LoaderManager.LoaderCallbacks<Cursor>{

		View rootView;
		Button sendButton ;
		EditText commentText;
		ListView mListView;
		SimpleCursorAdapter mArrayAdapter ; 
		int _id = 0;
		StringBuilder urlbuilder = new StringBuilder("http://icetech.webege.com/nolangues.php?");
		
		public CommentsFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_comments,container, false);
			sendButton = (Button) rootView.findViewById(R.id.btn_sendChat);
			commentText = (EditText) rootView.findViewById(R.id.comment_edittext);
			mListView = (ListView) rootView.findViewById(R.id.comments_listView);
			_id  = getActivity().getIntent().getExtras().getInt("id");
			
			mArrayAdapter = new SimpleCursorAdapter(getActivity(),
					R.layout.list_item_comment,
					null,
					new String[]{Critics.COLUMN_AUTHOR,Critics.COLUMN_COMMENT, Critics.COLUMN_DATE},
					new int[]{R.id.item_author_l, R.id.item_comment,R.id.item_date},
			0);
			mListView.setAdapter(mArrayAdapter);
			
			return rootView;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btn_sendChat:
				//retrieve the comment 
				String comment = commentText.getText().toString();
				Date nowdate = new Date();
				//retrieve the current date
				String dateStr = DateFormat.getDateTimeInstance().format(nowdate);
				//retrieve the author
				String author = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("username", "anon");
				//send data to the server
				new CommentTask().execute(""+_id,author,comment, dateStr);
				break;
			default:
					break;
			}
		}

		@Override
		public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
			// TODO Auto-generated method stub
			return new CursorLoader(getActivity(),
					Critics.buildDiscussionUri(_id),
					new String[]{Critics.COLUMN_AUTHOR,Critics.COLUMN_COMMENT,Critics.COLUMN_DATE},
					null,
					null,
					Critics.COLUMN_ID+" DESC"
					) ;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
			// TODO Auto-generated method stub
			mArrayAdapter.swapCursor(arg1);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {
			// TODO Auto-generated method stub
			mArrayAdapter.swapCursor(null);
		}

		//send comment info to remote server
		public class CommentTask extends AsyncTask<String, Void, Boolean>{

			@Override
			protected Boolean doInBackground(String... params) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			protected void onPostExecute(Boolean b){
				if(b){
					//show validation message and store comment in the database
				}else{
					//show error message
				}
			}
		}
		
	}
	

}
