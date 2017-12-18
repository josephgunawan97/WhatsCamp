package com.models;

import java.util.ArrayList;

public class DataResponse {

	private Status status;
	private ArrayList<Event> events;
    private ArrayList<EventCategory> categories;
    private float max_distance;
    private float default_distance;
    private User user_info;
    private Event event;
    private ArrayList<News> news;
    private ArrayList<User> users;
    private ArrayList<Post> posts;

    private int min_count;
    private int result_count;
    private int total_no_of_rows;

    public int getMin_count() {
        return min_count;
    }

    public void setMin_count(int min_count) {
        this.min_count = min_count;
    }

    public int getResult_count() {
        return result_count;
    }

    public void setResult_count(int result_count) {
        this.result_count = result_count;
    }

    public int getTotal_no_of_rows() {
        return total_no_of_rows;
    }

    public void setTotal_no_of_rows(int total_no_of_rows) {
        this.total_no_of_rows = total_no_of_rows;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public ArrayList<News> getNews() {
        return news;
    }

    public void setNews(ArrayList<News> news) {
        this.news = news;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public ArrayList<EventCategory> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<EventCategory> categories) {
        this.categories = categories;
    }

    public User getUser_info() {
        return user_info;
    }

    public void setUser_info(User user_info) {
        this.user_info = user_info;
    }


    public float getMax_distance() {
        return max_distance;
    }

    public void setMax_distance(float max_distance) {
        this.max_distance = max_distance;
    }

    public float getDefault_distance() {
        return default_distance;
    }

    public void setDefault_distance(float default_distance) {
        this.default_distance = default_distance;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
