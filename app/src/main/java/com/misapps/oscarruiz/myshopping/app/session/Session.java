package com.misapps.oscarruiz.myshopping.app.session;

import com.misapps.oscarruiz.myshopping.app.models.User;

/**
 * Created by Carlos Ruiz on 16/08/2017.
 */

public class Session {

    /**
     * User session singleton
     */
    private static Session session;

    /**
     * User session
     */
    private User user;

    /**
     * Shopping list position to see detail
     */
    private int position;

    /**
     * Method to create singleton
     */
    public static Session getInstance() {
        //check if session is created
        if (session == null) {
            //create session
            session = new Session();
        }
        //return sessions
        return session;
    }

    public static Session getSession() {
        return session;
    }

    public static void setSession(Session session) {
        Session.session = session;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPosition() { return position; }

    public void setPosition(int position) {this.position = position;}
}
