package com.icetech.data.model;

import com.icetech.data.model.ApplicationModel.Discussions;
import com.icetech.data.model.ApplicationModel.Language;
import com.icetech.data.model.ApplicationModel.Person;
import com.icetech.data.model.ApplicationModel.TranslationTable;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class LanguageContentProvider extends ContentProvider {
	
	private static final String TAG = LanguageContentProvider.class.getName();
	private static final int TRANSLATION  =  1;
	private static final int TRANSLATION_ID  =  2;
	private static final int TRANSLATION_TEXT  =  3;
	
	private static final int LANGUAGE = 4;
	private static final int LANGUAGE_ID = 5;
	private static final int LANGUAGE_TEXT = 6;
	
	private static final int PERSON = 7;
	private static final int PERSON_TEXT = 8;
	
	
	private static final int DISCUSSION = 9;
	private static final int DISCUSSION_ID = 10;
	
	private LangDatabaseHelper database;
	private SQLiteDatabase sqDB;
	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	static{
		uriMatcher.addURI(ApplicationModel.AUTHORITY, ApplicationModel.TranslationTable.TABLE_NAME, TRANSLATION); //matching all translations
		uriMatcher.addURI(ApplicationModel.AUTHORITY, ApplicationModel.TranslationTable.TABLE_NAME+"/#",TRANSLATION_ID ); //matching particular translation
		uriMatcher.addURI(ApplicationModel.AUTHORITY, ApplicationModel.TranslationTable.TABLE_NAME+"/*",TRANSLATION_TEXT );
		uriMatcher.addURI(ApplicationModel.AUTHORITY, ApplicationModel.Language.TABLE_NAME, LANGUAGE); //match all the languages
		uriMatcher.addURI(ApplicationModel.AUTHORITY, ApplicationModel.Language.TABLE_NAME+"/#", LANGUAGE_ID); //match a particular language
		uriMatcher.addURI(ApplicationModel.AUTHORITY, ApplicationModel.Language.TABLE_NAME+"/*", LANGUAGE_TEXT);
		uriMatcher.addURI(ApplicationModel.AUTHORITY, ApplicationModel.Person.TABLE_NAME, PERSON);
		uriMatcher.addURI(ApplicationModel.AUTHORITY, ApplicationModel.Person.TABLE_NAME+"/*", PERSON_TEXT);
		uriMatcher.addURI(ApplicationModel.AUTHORITY, ApplicationModel.Discussions.TABLE_NAME, DISCUSSION);
		uriMatcher.addURI(ApplicationModel.AUTHORITY, ApplicationModel.Discussions.TABLE_NAME+"/#", DISCUSSION_ID);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// Implement this to handle requests to delete one or more rows.
		sqDB = database.getWritableDatabase();
		int rowsAffected = 0;
		switch(uriMatcher.match(uri)){
		case TRANSLATION:
			return sqDB.delete(ApplicationModel.TranslationTable.TABLE_NAME, selection, selectionArgs);
		case TRANSLATION_ID:
			return sqDB.delete(ApplicationModel.TranslationTable.TABLE_NAME,
					ApplicationModel.TranslationTable.COLUMN_ID+" =?",
					new String[]{uri.getLastPathSegment()});
		case LANGUAGE:
			return sqDB.delete(ApplicationModel.Language.TABLE_NAME, 
					selection,
					selectionArgs);
		case LANGUAGE_ID:
			return sqDB.delete(ApplicationModel.Language.TABLE_NAME, ApplicationModel.Language.COLUMN_ID+"=?",
			new String[]{uri.getLastPathSegment()});
		}
		Log.d(TAG, "DELETED: "+rowsAffected+" rows");
		return rowsAffected;
		
	}

	@Override
	public String getType(Uri uri) {
		// TODO: Implement this to handle requests for the MIME type of the data
		// at the given URI.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		//sqDB = database.getWritableDatabase();
		long i = 0;
		switch(uriMatcher.match(uri)){
		case LANGUAGE:
			 i = database.getWritableDatabase().insert(ApplicationModel.Language.TABLE_NAME, null, values);
			if(i>0)
				return ApplicationModel.Language.buildLanguageUri(i);
			else
				return uri;
		case TRANSLATION:
			 i = database.getWritableDatabase().insert(ApplicationModel.TranslationTable.TABLE_NAME, null, values);
			if(i>0)
				return ApplicationModel.Language.buildLanguageUri(i);
			else
				return uri;
		default:
				return uri;
		case PERSON:
				i = database.getWritableDatabase().insert(ApplicationModel.Person.TABLE_NAME, null, values);
				return ApplicationModel.Person.buildPersonUri(i);
		}
	
	}

	@Override
	public boolean onCreate() {
		 database = new LangDatabaseHelper(getContext());
		 //sqDb =  database.getWritableDatabase()
		 return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {
		Cursor cursor;
		
		switch(uriMatcher.match(uri)){
		case LANGUAGE:
			cursor = database.getReadableDatabase().query(ApplicationModel.Language.TABLE_NAME,
					projection, 
					selection,
					selectionArgs,
					null, null,
					sortOrder);
			break;
		case LANGUAGE_ID:
			cursor = database.getReadableDatabase().query( ApplicationModel.Language.TABLE_NAME	,
					projection,
					ApplicationModel.Language.COLUMN_ID+"= ?",
					new String[]{uri.getLastPathSegment()},
					null, null,null);
			break;
		case LANGUAGE_TEXT:
			cursor = database.getReadableDatabase().query(ApplicationModel.Language.TABLE_NAME, new String[]{},
					Language.COLUMN_NAME+"= ?", new String[]{uri.getLastPathSegment()},
					null, null, null);
			break;
		case TRANSLATION:
			cursor = database.getReadableDatabase().query(ApplicationModel.TranslationTable.TABLE_NAME,
					null, 
					selection,
					selectionArgs,
					null, null,
					sortOrder);
			break;
		case TRANSLATION_ID:
			cursor = database.getReadableDatabase().query(ApplicationModel.TranslationTable.TABLE_NAME,
					projection, 
					ApplicationModel.TranslationTable.COLUMN_ID+"=?", 
					new String[]{uri.getLastPathSegment()},
					null, null,
					sortOrder);
			break;
		case TRANSLATION_TEXT:
			SQLiteQueryBuilder mtranslationBuilder = new SQLiteQueryBuilder();
			mtranslationBuilder.setTables(TranslationTable.TABLE_NAME+", "+Language.TABLE_NAME);
			mtranslationBuilder.appendWhere(TranslationTable.COLUMN_LANG_ID +" = "+Integer.parseInt(selectionArgs[0])+" AND ");
			mtranslationBuilder.appendWhere(TranslationTable.COLUMN_ID_DEST+" ="+Integer.parseInt(selectionArgs[1]));
		//	mtranslationBuilder.appendWhere(TranslationTable.COLUMN_TEXTE_ORIGIN+"="+selectionArgs[2]);
			//mtranslationBuilder.
			
			cursor =  mtranslationBuilder.query(database.getReadableDatabase(),
					new String[]{TranslationTable.COLUMN_TEXTE_DESTINATION},
					TranslationTable.COLUMN_TEXTE_ORIGIN+" LIKE ?",
					new String[]{selectionArgs[2]},
					null, null, null);
			
			/*cursor = database.getReadableDatabase().query(TranslationTable.TABLE_NAME,new String[]{TranslationTable.COLUMN_TEXTE_DESTINATION},
					TranslationTable.COLUMN_LANG_ID+"= "+Integer.parseInt(selectionArgs[0])+" AND "
				    +TranslationTable.COLUMN_ID_DEST+"= "+Integer.parseInt(selectionArgs[1])+" AND "
					+TranslationTable.COLUMN_TEXTE_ORIGIN+"="+selectionArgs[2],
					null,
					null, null, null); */
			
			break;
		case PERSON:
				cursor = database.getReadableDatabase().query(ApplicationModel.Person.TABLE_NAME, projection, 
						selection, 
						selectionArgs,
						null, null, null);
				break;
		case DISCUSSION:
			cursor = database.getReadableDatabase().query(Discussions.TABLE_NAME,
					projection, 
					selection,
					selectionArgs,
					null, null, null);
				break;
		case DISCUSSION_ID:
			cursor = database.getReadableDatabase().query(Discussions.TABLE_NAME,
					projection, 
					Discussions.COLUMN_ID+"=?", 
					new String[]{uri.getLastPathSegment()},
					null, null,
					sortOrder);
		default:
				throw new android.database.SQLException("Unknown Uri: "+uri);
		}
		//registers a content observer and causes the system to watch for changes to any uri beginning with the current scheme
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = database.getWritableDatabase();
		int insertedCount = 0;
		
		switch(uriMatcher.match(uri)){
		case LANGUAGE:
			break;
		case LANGUAGE_ID:
			break;
		case LANGUAGE_TEXT:
			break;
		case PERSON:
		case PERSON_TEXT:
			insertedCount = db.update(Person.TABLE_NAME, values, Person.COLUMN_USERNAME+" =?", new String[]{uri.getLastPathSegment()});
			break;
		case TRANSLATION:
			break;
		case TRANSLATION_ID:
			break;
		case TRANSLATION_TEXT:
			break;
		}
		
		return insertedCount;
	}
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] contentValues){
		SQLiteDatabase db = database.getWritableDatabase();
		int insertedCount = 0;
		
		switch(uriMatcher.match(uri)){
		case LANGUAGE:
			db.beginTransaction();
			try{
				long id;
				//insert each content value
				for(ContentValues value: contentValues){
					id = db.insert(ApplicationModel.Language.TABLE_NAME, null, value);
					if(id != -1){
					insertedCount++;	
					}
				}
				db.setTransactionSuccessful();
			}finally{
				db.endTransaction();
			}
			Log.d(TAG, "BULK INSERTED "+insertedCount+" rows");
			getContext().getContentResolver().notifyChange(uri, null);
			return insertedCount;
		case TRANSLATION:
			db.beginTransaction();
			try{
				long id;
				//insert each content value
				for(ContentValues value: contentValues){
					id = db.insert(ApplicationModel.TranslationTable.TABLE_NAME, null, value);
					if(id != -1){
					insertedCount++;	
					}
				}
				db.setTransactionSuccessful();
			}finally{
				db.endTransaction();
			}
			Log.d(TAG, "BULK INSERTED "+insertedCount+" rows");
			getContext().getContentResolver().notifyChange(uri, null);
			return insertedCount;
		case PERSON:
			db.beginTransaction();
			try{
				long id;
				//insert each content value
				for(ContentValues value: contentValues){
					id = db.insert(ApplicationModel.Person.TABLE_NAME, null, value);
					if(id != -1){
					insertedCount++;	
					}
				}
				db.setTransactionSuccessful();
				
			}finally{
				db.endTransaction();
			}
			Log.d(TAG, "BULK INSERTED "+insertedCount+" rows");
			getContext().getContentResolver().notifyChange(uri, null);
			return insertedCount;
		case DISCUSSION:
			db.beginTransaction();
			try{
				long id;
				//insert each content value
				for(ContentValues value: contentValues){
					id = db.insert(ApplicationModel.Discussions.TABLE_NAME, null, value);
					if(id != -1){
					insertedCount++;	
					}
				}
				db.setTransactionSuccessful();
				
			}finally{
				db.endTransaction();
			}
			Log.d(TAG, "BULK INSERTED "+insertedCount+" rows");
			getContext().getContentResolver().notifyChange(uri, null);
			return insertedCount;
		default:
			Log.d(TAG, "BULK INSERTED "+insertedCount+" rows");
			getContext().getContentResolver().notifyChange(uri, null);
				return insertedCount;
						
		}//end switch block
	}
	
}
