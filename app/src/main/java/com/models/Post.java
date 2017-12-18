package com.models;

import java.io.Serializable;

public class Post implements Serializable {

	private static final long serialVersionUID = -3053478382970635533L;
	int created_at;
	int event_id;
	int is_deleted;
	String post;
	int post_id;
	int updated_at;
	String user_full_name;
	int user_id;
	String user_thumb_url;
	String gmt_date_added;

	String full_name;
	String thumb_url;

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getThumb_url() {
		return thumb_url;
	}

	public void setThumb_url(String thumb_url) {
		this.thumb_url = thumb_url;
	}

	public String getGmt_date_added() {
		return gmt_date_added;
	}

	public void setGmt_date_added(String gmt_date_added) {
		this.gmt_date_added = gmt_date_added;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
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

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public int getPost_id() {
		return post_id;
	}

	public void setPost_id(int post_id) {
		this.post_id = post_id;
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
