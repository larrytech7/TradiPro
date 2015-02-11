package com.icetech.data.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.icetech.data.model.ApplicationModel.Critics;
import com.icetech.data.model.ApplicationModel.Discussions;
import com.icetech.data.model.ApplicationModel.Language;
import com.icetech.data.model.ApplicationModel.Person;
import com.icetech.data.model.ApplicationModel.TranslationTable;

public class LangDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "langDB.db";
	private static final int DATABASE_VERSION = 2;
//	private Context ctx;

	public LangDatabaseHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		//ctx = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Language.SQL);
		db.execSQL(TranslationTable.SQL);
		db.execSQL(Person.SQL);
		db.execSQL(Discussions.SQL);
		db.execSQL(Critics.SQL);
		
		/*ContentValues cv = new ContentValues();
		cv.put(ApplicationModel.Person.COLUMN_USERNAME, "larry");
		cv.put(ApplicationModel.Person.COLUMN_PASSWORD, "larry");
		cv.put(ApplicationModel.Person.COLUMN_NAME, "Larry tech");
		cv.put(ApplicationModel.Person.COLUMN_EMAIL, "larry@example.com");
		cv.put(ApplicationModel.Person.COLUMN_LANG_PRIMARY, "english");
		cv.put(ApplicationModel.Person.COLUMN_LANG_SECONDARY, "bafut");
		cv.put(ApplicationModel.Person.COLUMN_LANG_TERTIARY, "meta");
		
		Uri uri = ctx.getContentResolver().insert(ApplicationModel.Person.CONTENT_URI, cv);*/
		Log.d("LoginActivity", "oncreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS "+TranslationTable.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+Language.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+Person.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+Discussions.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+Critics.TABLE_NAME);
		onCreate(db);
	}

}
