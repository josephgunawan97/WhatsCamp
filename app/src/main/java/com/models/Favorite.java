package com.models;

import java.io.Serializable;

public class Favorite implements Serializable {

	private static final long serialVersionUID = -3053478382970635555L;
    int event_id;
	int favorite_id;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public int getFavorite_id() {
        return favorite_id;
    }

    public void setFavorite_id(int favorite_id) {
        this.favorite_id = favorite_id;
    }
}
