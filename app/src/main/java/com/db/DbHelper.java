package com.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	
	static final String TAG = "DbHelper";
	static final String DB_NAME = "db_whatscamp";
	static final int DB_VERSION = 1;
	static Context context;
	
	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE IF NOT EXISTS attendees("
				+ "attendee_id INTEGER PRIMARY KEY,"
				+ "created_at INTEGER,"
				+ "event_id INTEGER, "
				+ "is_deleted INTEGER, "
				+ "is_going INTEGER, "
				+ "is_interested INTEGER, "
				+ "is_invited INTEGER, "
				+ "updated_at INTEGER, "
				+ "user_full_name TEXT, "
				+ "user_id INTEGER, "
				+ "user_thumb_url TEXT "
				+ ");");

		db.execSQL("CREATE TABLE IF NOT EXISTS events("
				+ "event_id INTEGER PRIMARY KEY,"
				+ "address TEXT,"
				+ "attendees TEXT, "
				+ "attendees_fetch_count INTEGER, "
				+ "attendees_total INTEGER, "
				+ "categories TEXT, "
				+ "contact_no TEXT, "
				+ "created_at INTEGER, "
				+ "distance INTEGER, "
				+ "email_address TEXT, "
				+ "event_desc TEXT, "
				+ "full_name TEXT, "
				+ "gmt_date_set INTEGER, "
				+ "is_deleted INTEGER, "
				+ "is_featured INTEGER, "
				+ "is_going_count INTEGER, "
				+ "is_interested_count INTEGER, "
				+ "is_invited_count INTEGER, "
				+ "is_ticket_available INTEGER, "
				+ "lat TEXT, "
				+ "lon TEXT, "
				+ "photo_url TEXT, "
				+ "posts TEXT, "
				+ "thumb_url TEXT, "
				+ "ticket_url TEXT, "
				+ "title TEXT, "
				+ "updated_at INTEGER, "
				+ "user_id INTEGER, "
				+ "user_ids TEXT "
				+ ");");

	    db.execSQL("CREATE TABLE IF NOT EXISTS categories("
	    		+ "category_id INTEGER PRIMARY KEY,"
	    		+ "category TEXT,"
                + "category_icon TEXT,"
	    		+ "created_at INTEGER, "
	    		+ "is_deleted INTEGER, "
	    		+ "updated_at INTEGER "
	    		+ ");");
	    
	    db.execSQL("CREATE TABLE IF NOT EXISTS news("
	    		+ "news_id INTEGER PRIMARY KEY,"
	    		+ "created_at INTEGER, "
	    		+ "news_content TEXT, "
	    		+ "news_title TEXT, "
	    		+ "news_url TEXT, "
	    		+ "photo_url TEXT, "
	    		+ "updated_at INTEGER "
	    		+ ");");

	    db.execSQL("CREATE TABLE IF NOT EXISTS favorites("
	    		+ "favorite_id INTEGER PRIMARY KEY AUTOINCREMENT," //AUTOINCREMENT
	    		+ "event_id INTEGER"
	    		+ ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS posts("
                + "post_id INTEGER PRIMARY KEY,"
                + "created_at INTEGER, "
                + "event_id INTEGER, "
                + "is_deleted INTEGER, "
                + "post TEXT, "
                + "updated_at INTEGER, "
                + "user_full_name TEXT, "
                + "user_id TEXT, "
				+ "gmt_date_added TEXT, "
                + "user_thumb_url TEXT "
                + ");");
	    
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS categories");
		db.execSQL("DROP TABLE IF EXISTS favorites");
		db.execSQL("DROP TABLE IF EXISTS coupons");
	}
}