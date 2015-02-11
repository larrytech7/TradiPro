package com.icetech.tradipro;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.icetech.data.model.ApplicationModel;

public class LanguagesAdapter extends CursorAdapter {

	public LanguagesAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		//retrieve holder from view passed
		ViewHolder vHolder = (ViewHolder) view.getTag();
		//bind data from the cursor to the view returned by newView
		vHolder.mTextView.setText(cursor.getString(cursor.getColumnIndex(ApplicationModel.Language.COLUMN_NAME)));
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		
		View newView = LayoutInflater.from(context).inflate(R.layout.list_item_languages, parent, false);
		ViewHolder vHolder = new ViewHolder(newView);
		newView.setTag(vHolder);
		return newView;
	}
	
	
	public class ViewHolder {
		public final TextView mTextView;
		public final ImageView mImageView;
		
		public ViewHolder(View v){
			mTextView = (TextView) v.findViewById(R.id.language_listitem);
			mImageView = (ImageView) v.findViewById(R.id.language_flagitem);
			
		}
	}

}
