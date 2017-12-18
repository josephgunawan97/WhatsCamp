package com.models;

import java.io.Serializable;

public class Attendee implements Serializable {

	private static final long serialVersionUID = -3053478382970635533L;

	int attendee_id;
	int created_at;
	int event_id;
	int is_deleted;
	int is_going;
	int is_interested;
	int is_invited;
	int updated_at;
	String user_full_name;
	int user_id;
	String user_thumb_url;

	String thumb_url;
	String full_name;

	String email;

	String events_attended_count;

	public String getEvents_attended_count() {
		return events_attended_count;
	}

	public void setEvents_attended_count(String events_attended_count) {
		this.events_attended_count = events_attended_count;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getThumb_url() {
		return thumb_url;
	}

	public void setThumb_url(String thumb_url) {
		this.thumb_url = thumb_url;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public int getAttendee_id() {
		return attendee_id;
	}

	public void setAttendee_id(int attendee_id) {
		this.attendee_id = attendee_id;
	}

	public int getCreated_at() {
		return created_at;
	}

	public void setCreated_at(int created_at) {
		this.created_at = created_at;
	}

	public int getEvent_id() {
		return event_id;
	}

	public void setEvent_id(int event_id) {
		this.event_id = event_id;
	}

	public int getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(int is_deleted) {
		this.is_deleted = is_deleted;
	}

	public int getIs_going() {
		return is_going;
	}

	public void setIs_going(int is_going) {
		this.is_going = is_going;
	}

	public int getIs_interested() {
		return is_interested;
	}

	public void setIs_interested(int is_interested) {
		this.is_interested = is_interested;
	}

	public int getIs_invited() {
		return is_invited;
	}

	public void setIs_invited(int is_invited) {
		this.is_invited = is_invited;
	}

	public int getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(int updated_at) {
		this.updated_at = updated_at;
	}

	public String getUser_full_name() {
		return user_full_name;
	}

	public void setUser_full_name(String user_full_name) {
		this.user_full_name = user_full_name;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getUser_thumb_url() {
		return user_thumb_url;
	}

	public void setUser_thumb_url(String user_thumb_url) {
		this.user_thumb_url = user_thumb_url;
	}
}
