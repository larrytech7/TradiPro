package com.icetech.data.model;

import android.content.ContentUris;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * This class holds the basic data models of our application like the translation tables and it's utility functions
 * @author root
 *
 */
public class ApplicationModel {
	
	public static final String AUTHORITY = "com.icetech.lang.provider";
	public static Uri BASE_CONTENT_URI = Uri.parse("content://"+ AUTHORITY );

	public static final class Language{
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath("tbl_language").build(); 
		public static final String CONTENT_TYPE ="vnd.android.cursor.dir/"+AUTHORITY+"/tbl_language";
		public static final String CONTENT_ITEM_TYPE ="vnd.android.cursor.item/"+AUTHORITY+"/tbl_language";
		
		public static final String TABLE_NAME = "tbl_language"; //name of the language table in the database
		
		public static final String COLUMN_ID = "_id"; // 
		public static final String COLUMN_NAME = "lang_name"; // language name which has to be unique 
		public static final String COLUMN_DESCRIPTION = "lang_description";  //language description as given by the author
		public static final String COLUMN_DATE_ADDED =  "date_add"; // date added into the database.
		
		public static final String SQL = "CREATE TABLE "+TABLE_NAME+"("+
				COLUMN_ID+" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
						COLUMN_NAME+" VARCHAR(255) NOT NULL,"+
						COLUMN_DESCRIPTION+" TEXT ,"+
						COLUMN_DATE_ADDED+" DATE NULL, UNIQUE("+COLUMN_ID+","+COLUMN_NAME+") );";
		
		public static Uri buildLanguageUri(long id)
		{
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}
		public static Uri buildUriByName(String name){
			return CONTENT_URI.buildUpon().appendPath(name).build();
		}
		
	}
	/**
	 * This class defines the translation table which holds mappings of translations from one language to another
	 * The mappings are one to one as of the time of writing this code
	 * @author root
	 *
	 */
	public static final class TranslationTable{
		
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath("tbl_translation").build(); 
		public static final String CONTENT_TYPE ="vnd.android.cursor.dir/"+AUTHORITY+"/tbl_translation";
		public static final String CONTENT_ITEM_TYPE ="vnd.android.cursor.item/"+AUTHORITY+"/tbl_translation";
		
		public static final String TABLE_NAME = "tbl_translation"; //name of the translation table in the database
		
		public static final String COLUMN_ID = "_id"; //the translation's unique identifier id
		public static final String COLUMN_STATUS = "lang_status"; //status of the translation e,g published, submitted, active, inactive
		public static final String COLUMN_TEXTE_DESTINATION = "text_destination"; // the translated string
		public static final String COLUMN_TEXTE_ORIGIN = "text_origin"; // the locale string or string to be translated
		public static final String COLUMN_ID_DEST = "id_lang_destination"; // the id of the foreign/target language
		public static final String COLUMN_TRANS_AUTHOR = "author_name"; //Author of the translation
		public static final String COLUMN_DATE_ADDED =  "date_add"; //the date this translation was added
		public static final String COLUMN_LANG_ID =  "language_id"; //the id of the table with this language and id of the locale(origin) language
		
		public static final String SQL = "CREATE TABLE "+TABLE_NAME+"("+
				COLUMN_ID+" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
						COLUMN_STATUS+" VARCHAR(10),"+
						COLUMN_TEXTE_DESTINATION+" TEXT NOT NULL,"+
						COLUMN_TEXTE_ORIGIN+" TEXT ,"+
						COLUMN_ID_DEST+" INTEGER NOT NULL, "+
						COLUMN_TRANS_AUTHOR+" TEXT NOT NULL,"+
						COLUMN_DATE_ADDED+" DATE NOT NULL,"+
						COLUMN_LANG_ID+" INTEGER NOT NULL, FOREIGN KEY ("+COLUMN_LANG_ID+") REFERENCES "+ApplicationModel.Language.TABLE_NAME+
						"("+Language.COLUMN_ID+") );";
		
		public static Uri buildTranslationUri(long id){
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}
		public static Uri buildUriByName(String name){
			return CONTENT_URI.buildUpon().appendPath(name).build();
		}
		
		//used with the selection to retrieve the appropriate translation string
		public static SQLiteQueryBuilder getTranslationQuery(int orig_lang, int dest_lang, String text){
			SQLiteQueryBuilder mtranslationBuilder = new SQLiteQueryBuilder();
			mtranslationBuilder.setTables(TranslationTable.TABLE_NAME+", "+Language.TABLE_NAME);
			mtranslationBuilder.appendWhere(TranslationTable.COLUMN_LANG_ID +"="+orig_lang);
			mtranslationBuilder.appendWhere(TranslationTable.COLUMN_ID_DEST+"="+dest_lang);
			mtranslationBuilder.appendWhere(TranslationTable.COLUMN_TEXTE_ORIGIN+"="+text);
			
			return mtranslationBuilder == null? null:mtranslationBuilder;
		}
	}
	/**
	 * This class holds user credentials 
	 * @author root
	 *
	 */
	public static final class Person{
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath("tbl_person").build(); 
		public static final String CONTENT_TYPE ="vnd.android.cursor.dir/"+AUTHORITY+"/tbl_person";
		public static final String CONTENT_ITEM_TYPE ="vnd.android.cursor.item/"+AUTHORITY+"/tbl_person";
		
		public static final String TABLE_NAME = "tbl_person"; //name of the user table in the database
		
		public static final String COLUMN_ID = "_id"; //the user's unique identifier id
		public static final String COLUMN_USERNAME = "username"; //username used to login
		public static final String COLUMN_PASSWORD = "password"; // the user's password surely hash
		public static final String COLUMN_NAME = "fullname"; // the user's fullname
		public static final String COLUMN_EMAIL = "email"; //the user's email
		public static final String COLUMN_LANG_PRIMARY = "primary_language"; // the user's primary language
		public static final String COLUMN_LANG_SECONDARY = "secondary_language"; //first target language
		public static final String COLUMN_LANG_TERTIARY =  "tertiary_language"; //second target language
		
		public static final String SQL = "CREATE TABLE "+TABLE_NAME+"("+
		COLUMN_ID+" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
				COLUMN_USERNAME+" VARCHAR(255) NOT NULL,"+
				COLUMN_PASSWORD+" VARCHAR(255) NOT NULL,"+
				COLUMN_NAME+" TEXT ,"+
				COLUMN_EMAIL+" TEXT NOT NULL,"+
				COLUMN_LANG_PRIMARY+" TEXT NOT NULL,"+
				COLUMN_LANG_SECONDARY+" TEXT NOT NULL, "+
				COLUMN_LANG_TERTIARY+" TEXT NOT NULL);";
		
		public static Uri buildPersonUri(long id)
		{
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}
		public static Uri buildUriByName(String name){
			return CONTENT_URI.buildUpon().appendPath(name).build();
		}
		
	}
	
	public static final class Discussions{
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath("tbl_discussion").build(); 
		public static final String CONTENT_TYPE ="vnd.android.cursor.dir/"+AUTHORITY+"/tbl_discussion";
		public static final String CONTENT_ITEM_TYPE ="vnd.android.cursor.item/"+AUTHORITY+"/tbl_discussion";
		
		public static final String TABLE_NAME = "tbl_discussion"; //name of the translation table in the database
		
		public static final String COLUMN_ID = "_id"; //discussion id
		public static final String COLUMN_AUTHOR = "d_author"; //discussion author
		public static final String COLUMN_COMMENT = "d_comment"; // content
		public static final String COLUMN_DATE = "d_date"; // the date
		public static final String COLUMN_COMMENT_TITLE = "d_title"; //the title of a particular discussion, usually the language name
		
		public static final String SQL = "CREATE TABLE "+TABLE_NAME+"("+
				COLUMN_ID+" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
						COLUMN_AUTHOR+" VARCHAR(255) NOT NULL,"+
						COLUMN_COMMENT+" TEXT NOT NULL,"+
						COLUMN_DATE+" DATE NOT NULL ,"+
						COLUMN_COMMENT_TITLE+" TEXT NOT NULL);";
		
		public static Uri buildDiscussionUri(long id)
		{
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}
		
	}
	//this class denotes the comments/critics table which holds critics about discussions
	public static final class Critics{
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath("tbl_critics").build(); 
		public static final String CONTENT_TYPE ="vnd.android.cursor.dir/"+AUTHORITY+"/tbl_critics";
		public static final String CONTENT_ITEM_TYPE ="vnd.android.cursor.item/"+AUTHORITY+"/tbl_critics";
		
		public static final String TABLE_NAME = "tbl_critics"; //name of the translation table in the database
		
		public static final String COLUMN_ID = "_id"; //critic id
		public static final String COLUMN_DISCUSSION_ID = "d_id"; //discussion id of the critic
		public static final String COLUMN_AUTHOR = "c_author"; //discussion author
		public static final String COLUMN_COMMENT = "c_comment"; // content
		public static final String COLUMN_DATE = "c_date"; // the date
		
		public static final String SQL = "CREATE TABLE "+TABLE_NAME+"("+
				COLUMN_ID+" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
				COLUMN_DISCUSSION_ID+" INTEGER NOT NULL,"+
						COLUMN_AUTHOR+" VARCHAR(255) NOT NULL,"+
						COLUMN_COMMENT+" TEXT NOT NULL,"+
						COLUMN_DATE+" DATE NOT NULL ,"+
						" FOREIGN KEY("+COLUMN_DISCUSSION_ID+") REFERENCES "+Discussions.TABLE_NAME+"("+Discussions.COLUMN_ID+") );";
		
		public static Uri buildDiscussionUri(long id)
		{
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}
		
	}
}
