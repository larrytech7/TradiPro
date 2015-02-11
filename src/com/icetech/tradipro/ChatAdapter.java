package com.icetech.tradipro;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.icetech.data.model.ApplicationModel.Discussions;

public class ChatAdapter extends CursorAdapter {

	public ChatAdapter(Context context,Cursor c,  int flags) {
		super(context, c,  flags);
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		//retrieve holder from view passed
		ViewHolder vHolder = (ViewHolder) view.getTag();
		//bind data from the cursor to the view returned by newView
		String title = cursor.getString(cursor.getColumnIndex(Discussions.COLUMN_COMMENT_TITLE));
		vHolder.mTextView.setText(title);
		String text = cursor.getString(cursor.getColumnIndex(Discussions.COLUMN_COMMENT));
		vHolder.mDiscussionText.setText(text);
		String date = cursor.getString(cursor.getColumnIndex(Discussions.COLUMN_DATE));
		vHolder.mDateTextview.setText(date);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		
		View newView = LayoutInflater.from(context).inflate(R.layout.list_item_chat, parent, false);
		ViewHolder vHolder = new ViewHolder(newView);
		newView.setTag(vHolder);
		return newView;
	}
	
	
	public class ViewHolder {
		public final TextView mTextView;
		public final EditText mDiscussionText;
		public final TextView mDateTextview;
		
		public ViewHolder(View v){
			mTextView = (TextView) v.findViewById(R.id.item_current_language);
			mDiscussionText = (EditText) v.findViewById(R.id.item_discussion);
			mDateTextview = (TextView) v.findViewById(R.id.item_date);
			
		}
	}

}
