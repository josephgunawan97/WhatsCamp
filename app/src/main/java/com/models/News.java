package com.models;

import java.io.Serializable;

public class News implements Serializable {

	private static final long serialVersionUID = -3053478382970635533L;
	int created_at;
	String news_content;
	int news_id;
	String news_title;
	String news_url;
	String photo_url;
	int updated_at;


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public int getCreated_at() {
		return created_at;
	}

	public void setCreated_at(int created_at) {
		this.created_at = created_at;
	}

	public String getNews_content() {
		return news_content;
	}

	public void setNews_content(String news_content) {
		this.news_content = news_content;
	}

	public int getNews_id() {
		return news_id;
	}

	public void setNews_id(int news_id) {
		this.news_id = news_id;
	}

	public String getNews_title() {
		return news_title;
	}

	public void setNews_title(String news_title) {
		this.news_title = news_title;
	}

	public String getNews_url() {
		return news_url;
	}

	public void setNews_url(String news_url) {
		this.news_url = news_url;
	}

	public String getPhoto_url() {return photo_url;}

	public void setPhoto_url(String photo_url) {
		this.photo_url = photo_url;
	}

	public int getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(int updated_at) {
		this.updated_at = updated_at;
	}
}
