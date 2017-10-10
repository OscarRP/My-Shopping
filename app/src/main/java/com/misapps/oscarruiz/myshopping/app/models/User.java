package com.misapps.oscarruiz.myshopping.app.models;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Oscar Ruiz on 16/08/2017.
 */

public class User {

    /**
     * Indicates URL to download profile image from firebase storage
     */
    private String downloadURL;

    /**
     * var that indicates uid from database
     */
    private String uid;

    /**
     * var that indicates users contacts
     */
    private ArrayList<User> contacts;

    /**
     * var that indicates users nick
     */
    @SerializedName("nick")
    private String nick;

    /**
     * var that indicates users email
     */
    @SerializedName("email")
    private String email;

    /**
     * var that indicates users password
     */
    @SerializedName("password")
    private String password;

    /**
     * Users profile image
     */
    @SerializedName("profileImage")
    private String profileImage;

    /**
     * var that indicates users shopping lists
     */
    @SerializedName("shoppingLists")
    private ArrayList<ShoppingList> shoppingLists;


    public User(String uid, String nick, String email, String password) {
        this.uid = uid;
        this.nick = nick;
        this.email = email;
        this.password = password;
    }

    public User(String nick, String email, String password) {
        this.email = email;
        this.password = password;
        this.nick = nick;
    }

    public User(String email, String nick) {
        this.email = email;
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public ArrayList<ShoppingList> getShoppingLists() {
        return shoppingLists;
    }

    public void setShoppingLists(ArrayList<ShoppingList> shoppingLists) {
        this.shoppingLists = shoppingLists;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<User> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<User> contacts) {
        this.contacts = contacts;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }
}
