package com.models;

import java.io.Serializable;

public class Event implements Serializable {

	private static final long serialVersionUID = -3053478382970635533L;

	String address;
	String attendees;
	int attendees_fetch_count;
	int attendees_total;
	String categories;
	String contact_no;
	int created_at;
	double distance;
	String email_address;
	String event_desc;
	int event_id;
	String full_name;
	String gmt_date_set;
	int is_deleted;
	int is_featured;
	int is_going_count;
	int is_interested_count;
	int is_invited_count;
	int is_ticket_available;
	double lat;
	double lon;
	String photo_url;
	String posts;
	String thumb_url;
	String ticket_url;

	String title;
	int updated_at;
	int user_id;
	String user_ids;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAttendees() {
		return attendees;
	}

	public void setAttendees(String attendees) {
		this.attendees = attendees;
	}

	public int getAttendees_fetch_count() {
		return attendees_fetch_count;
	}

	public void setAttendees_fetch_count(int attendees_fetch_count) {
		this.attendees_fetch_count = attendees_fetch_count;
	}

	public int getAttendees_total() {
		return attendees_total;
	}

	public void setAttendees_total(int attendees_total) {
		this.attendees_total = attendees_total;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public String getContact_no() {
		return contact_no;
	}

	public void setContact_no(String contact_no) {
		this.contact_no = contact_no;
	}

	public int getCreated_at() {
		return created_at;
	}

	public void setCreated_at(int created_at) {
		this.created_at = created_at;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getEmail_address() {
		return email_address;
	}

	public void setEmail_address(String email_address) {
		this.email_address = email_address;
	}

	public String getEvent_desc() {
		return event_desc;
	}

	public void setEvent_desc(String event_desc) {
		this.event_desc = event_desc;
	}

	public int getEvent_id() {
		return event_id;
	}

	public void setEvent_id(int event_id) {
		this.event_id = event_id;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getGmt_date_set() {
		return gmt_date_set;
	}

	public void setGmt_date_set(String gmt_date_set) {
		this.gmt_date_set = gmt_date_set;
	}

	public int getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(int is_deleted) {
		this.is_deleted = is_deleted;
	}

	public int getIs_featured() {
		return is_featured;
	}

	public void setIs_featured(int is_featured) {
		this.is_featured = is_featured;
	}

	public int getIs_going_count() {
		return is_going_count;
	}

	public void setIs_going_count(int is_going_count) {
		this.is_going_count = is_going_count;
	}

	public int getIs_interested_count() {
		return is_interested_count;
	}

	public void setIs_interested_count(int is_interested_count) {
		this.is_interested_count = is_interested_count;
	}

	public int getIs_invited_count() {
		return is_invited_count;
	}

	public void setIs_invited_count(int is_invited_count) {
		this.is_invited_count = is_invited_count;
	}

	public int getIs_ticket_available() {
		return is_ticket_available;
	}

	public void setIs_ticket_available(int is_ticket_available) {
		this.is_ticket_available = is_ticket_available;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getPhoto_url() {
		return photo_url;
	}

	public void setPhoto_url(String photo_url) {
		this.photo_url = photo_url;
	}

	public String getPosts() {
		return posts;
	}

	public void setPosts(String posts) {
		this.posts = posts;
	}

	public String getThumb_url() {
		return thumb_url;
	}

	public void setThumb_url(String thumb_url) {
		this.thumb_url = thumb_url;
	}

	public String getTicket_url() {
		return ticket_url;
	}

	public void setTicket_url(String ticket_url) {
		this.ticket_url = ticket_url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(int updated_at) {
		this.updated_at = updated_at;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getUser_ids() {
		return user_ids;
	}

	public void setUser_ids(String user_ids) {
		this.user_ids = user_ids;
	}
}
