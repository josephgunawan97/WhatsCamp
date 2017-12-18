package com.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.models.Event;
import com.models.EventCategory;
import com.models.Favorite;
import com.models.News;

import java.util.ArrayList;

public class Queries {
	
	private SQLiteDatabase db;
	private DbHelper dbHelper;

	public Queries(SQLiteDatabase db, DbHelper dbHelper) {
		this.db = db;
		this.dbHelper = dbHelper;
	}
	
	public void deleteTable(String tableName) {
		db = dbHelper.getWritableDatabase();
		try{
			db.delete(tableName, null, null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}


	public void insertEvent(Event entry) {
		db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("address", entry.getAddress());
		values.put("event_desc", entry.getEvent_desc());
		values.put("attendees", entry.getAttendees());
		values.put("attendees_fetch_count", entry.getAttendees_fetch_count());
		values.put("attendees_total", entry.getAttendees_total());
		values.put("categories", entry.getCategories());
		values.put("contact_no", entry.getContact_no());
		values.put("created_at", entry.getCreated_at());
		values.put("distance", entry.getDistance());
		values.put("email_address", entry.getEmail_address());
		values.put("event_id", entry.getEvent_id());
		values.put("full_name", entry.getFull_name());
		values.put("gmt_date_set", entry.getGmt_date_set());
		values.put("is_deleted", entry.getIs_deleted());
		values.put("is_featured", entry.getIs_featured());
		values.put("is_going_count", entry.getIs_going_count());
		values.put("is_interested_count", entry.getIs_interested_count());
		values.put("is_invited_count", entry.getIs_invited_count());
		values.put("is_ticket_available", entry.getIs_ticket_available());
		values.put("lat", entry.getLat());
		values.put("lon", entry.getLon());
		values.put("photo_url", entry.getPhoto_url());
		values.put("posts", entry.getPosts());
		values.put("thumb_url", entry.getThumb_url());
		values.put("ticket_url", entry.getTicket_url());
		values.put("title", entry.getTitle());
		values.put("updated_at", entry.getUpdated_at());
		values.put("user_id", entry.getUser_id());
		values.put("user_ids", entry.getUser_ids());
		db.insert("events", null, values);
	}

	public void updateEvent(Event entry) {
		db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("address", entry.getAddress());
		values.put("event_desc", entry.getEvent_desc());
		values.put("attendees", entry.getAttendees());
		values.put("attendees_fetch_count", entry.getAttendees_fetch_count());
		values.put("attendees_total", entry.getAttendees_total());
		values.put("categories", entry.getCategories());
		values.put("contact_no", entry.getContact_no());
		values.put("created_at", entry.getCreated_at());
		values.put("distance", entry.getDistance());
		values.put("email_address", entry.getEmail_address());
		values.put("event_id", entry.getEvent_id());
		values.put("full_name", entry.getFull_name());
		values.put("gmt_date_set", entry.getGmt_date_set());
		values.put("is_deleted", entry.getIs_deleted());
		values.put("is_featured", entry.getIs_featured());
		values.put("is_going_count", entry.getIs_going_count());
		values.put("is_interested_count", entry.getIs_interested_count());
		values.put("is_invited_count", entry.getIs_invited_count());
		values.put("is_ticket_available", entry.getIs_ticket_available());
		values.put("lat", entry.getLat());
		values.put("lon", entry.getLon());
		values.put("photo_url", entry.getPhoto_url());
		values.put("posts", entry.getPosts());
		values.put("thumb_url", entry.getThumb_url());
		values.put("ticket_url", entry.getTicket_url());
		values.put("title", entry.getTitle());
		values.put("updated_at", entry.getUpdated_at());
		values.put("user_id", entry.getUser_id());
		values.put("user_ids", entry.getUser_ids());
		db.update("events", values, "event_id = " + entry.getEvent_id(), null);
	}

	public void insertCategory(EventCategory entry) {
		db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("category_id", entry.getCategory_id());
		values.put("category", entry.getCategory());
		values.put("created_at", entry.getCreated_at());
		values.put("is_deleted", entry.getIs_deleted());
		values.put("category_icon", entry.getCategory_icon());
		values.put("updated_at", entry.getUpdated_at());
		db.insert("categories", null, values);
	}

	public void updateCategory(EventCategory entry) {
		db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("category_id", entry.getCategory_id());
		values.put("category", entry.getCategory());
		values.put("created_at", entry.getCreated_at());
		values.put("is_deleted", entry.getIs_deleted());
		values.put("category_icon", entry.getCategory_icon());
		values.put("updated_at", entry.getUpdated_at());
		db.update("categories", values, "category_id = " + entry.getCategory_id(), null);
	}

	public void insertFavorite(Favorite entry) {
		db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("event_id", entry.getEvent_id());
		db.insert("favorites", null, values);
	}

    public void deleteFavorite(int coupon_id) {
        db = dbHelper.getWritableDatabase();
        db.delete("favorites", "coupon_id = " + String.valueOf(coupon_id), null);
    }

	public void deleteEvent(int event_id) {
		db = dbHelper.getWritableDatabase();
		db.delete("events", "event_id = " + String.valueOf(event_id), null);
	}

	public void deleteCategory(int category_id) {
		db = dbHelper.getWritableDatabase();
		db.delete("categories", "category_id = " + String.valueOf(category_id), null);
	}

	public Favorite getFavoriteByEventId(int event_id) {
		Favorite entry = null;
		String sql = String.format("SELECT * FROM favorites WHERE event_id = %s", String.valueOf(event_id));
		db = dbHelper.getReadableDatabase();
		Cursor mCursor = db.rawQuery(sql , null);
		mCursor.moveToFirst();
		if (!mCursor.isAfterLast()) {
			do {
				entry = new Favorite();
				entry.setFavorite_id( mCursor.getInt( mCursor.getColumnIndex("favorite_id")) );
				entry.setEvent_id( mCursor.getInt( mCursor.getColumnIndex("event_id")) );
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return entry;
	}

	public ArrayList<Event> getEvents() {
		ArrayList<Event> list = new ArrayList<Event>();
		db = dbHelper.getReadableDatabase();
		Cursor mCursor = db.rawQuery("SELECT * FROM events", null);
		mCursor.moveToFirst();
		if (!mCursor.isAfterLast()) {
			do {
                Event entry = formatEvent(mCursor);
				list.add(entry);
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return list;
	}

	public Event getEventByEventId(int event_id) {
		Event entry = null;
		db = dbHelper.getReadableDatabase();
		String sql = String.format("SELECT * FROM events WHERE event_id = %s", String.valueOf(event_id));
		Cursor mCursor = db.rawQuery(sql, null);
		mCursor.moveToFirst();
		if (!mCursor.isAfterLast()) {
			do {
                entry = formatEvent(mCursor);
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return entry;
	}

	public ArrayList<Event> getEventsFeatured() {
		ArrayList<Event> list = new ArrayList<Event>();
		db = dbHelper.getReadableDatabase();
		Cursor mCursor = db.rawQuery("SELECT * FROM events WHERE is_featured = 1", null);
		mCursor.moveToFirst();
		if (!mCursor.isAfterLast()) {
			do {
				Event entry = formatEvent(mCursor);
				list.add(entry);
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return list;
	}

	public ArrayList<EventCategory> getCategories() {
		ArrayList<EventCategory> list = new ArrayList<EventCategory>();
		db = dbHelper.getReadableDatabase();
		Cursor mCursor = db.rawQuery("SELECT * FROM categories ORDER BY category ASC", null);
		mCursor.moveToFirst();
		if (!mCursor.isAfterLast()) {
			do {
				EventCategory entry = formatCategory(mCursor);
				list.add(entry);
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return list;
	}

	public ArrayList<Event> getEventsFavorites() {
		ArrayList<Event> list = new ArrayList<Event>();
		db = dbHelper.getReadableDatabase();
		Cursor mCursor = db.rawQuery("SELECT * FROM events INNER JOIN favorites ON events.event_id = favorites.event_id ORDER BY events.title", null);
		mCursor.moveToFirst();
		if (!mCursor.isAfterLast()) {
			do {
				Event entry = formatEvent(mCursor);
				list.add(entry);
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return list;
	}

	public EventCategory getCategoryByCategoryId(int categoryId) {
		EventCategory entry = null;
		db = dbHelper.getReadableDatabase();
		String sql = String.format("SELECT * FROM categories WHERE category_id = %s", String.valueOf(categoryId));
		Cursor mCursor = db.rawQuery(sql, null);
		mCursor.moveToFirst();
		if (!mCursor.isAfterLast()) {
			do {
                entry = formatCategory(mCursor);
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return entry;
	}

    public EventCategory formatCategory(Cursor mCursor) {
		EventCategory entry = new EventCategory();
        entry.setCreated_at( mCursor.getInt( mCursor.getColumnIndex("created_at")) );
        entry.setCategory_id( mCursor.getInt( mCursor.getColumnIndex("category_id")) );
        entry.setCategory( mCursor.getString( mCursor.getColumnIndex("category")) );
        entry.setUpdated_at( mCursor.getInt( mCursor.getColumnIndex("updated_at")) );
        entry.setIs_deleted( mCursor.getInt( mCursor.getColumnIndex("is_deleted")) );
        entry.setCategory_icon( mCursor.getString( mCursor.getColumnIndex("category_icon")) );
        return entry;
    }

    public Event formatEvent(Cursor mCursor) {
		Event entry = new Event();
        entry.setAddress( mCursor.getString( mCursor.getColumnIndex("address")) );
		entry.setAttendees( mCursor.getString( mCursor.getColumnIndex("attendees")) );
		entry.setAttendees_fetch_count( mCursor.getInt( mCursor.getColumnIndex("attendees_fetch_count")) );
		entry.setAttendees_total( mCursor.getInt( mCursor.getColumnIndex("attendees_total")) );
		entry.setCategories( mCursor.getString( mCursor.getColumnIndex("categories")) );
		entry.setContact_no( mCursor.getString( mCursor.getColumnIndex("contact_no")) );
		entry.setCreated_at( mCursor.getInt( mCursor.getColumnIndex("created_at")) );
		entry.setDistance( mCursor.getFloat( mCursor.getColumnIndex("distance")) );
		entry.setEmail_address( mCursor.getString( mCursor.getColumnIndex("email_address")) );
		entry.setEvent_desc( mCursor.getString( mCursor.getColumnIndex("event_desc")) );
		entry.setEvent_id( mCursor.getInt( mCursor.getColumnIndex("event_id")) );
		entry.setFull_name( mCursor.getString( mCursor.getColumnIndex("full_name")) );
		entry.setGmt_date_set( mCursor.getString( mCursor.getColumnIndex("gmt_date_set")) );
		entry.setIs_deleted( mCursor.getInt( mCursor.getColumnIndex("is_deleted")) );
		entry.setIs_featured( mCursor.getInt( mCursor.getColumnIndex("is_featured")) );
		entry.setIs_going_count( mCursor.getInt( mCursor.getColumnIndex("is_going_count")) );
		entry.setIs_interested_count( mCursor.getInt( mCursor.getColumnIndex("is_interested_count")) );
		entry.setIs_invited_count( mCursor.getInt( mCursor.getColumnIndex("is_invited_count")) );
		entry.setIs_ticket_available( mCursor.getInt( mCursor.getColumnIndex("is_ticket_available")) );
		entry.setLat( mCursor.getDouble( mCursor.getColumnIndex("lat")) );
		entry.setLon( mCursor.getDouble( mCursor.getColumnIndex("lon")) );
		entry.setPhoto_url( mCursor.getString( mCursor.getColumnIndex("photo_url")) );
		entry.setPosts( mCursor.getString( mCursor.getColumnIndex("posts")) );
		entry.setThumb_url( mCursor.getString( mCursor.getColumnIndex("thumb_url")) );
		entry.setTicket_url( mCursor.getString( mCursor.getColumnIndex("ticket_url")) );
		entry.setTitle( mCursor.getString( mCursor.getColumnIndex("title")) );
		entry.setUpdated_at( mCursor.getInt( mCursor.getColumnIndex("updated_at")) );
		entry.setUser_id( mCursor.getInt( mCursor.getColumnIndex("user_id")) );
		entry.setUser_ids( mCursor.getString( mCursor.getColumnIndex("user_ids")) );
        return entry;
    }

	public void closeDatabase() {
		dbHelper.close();
	}
}
