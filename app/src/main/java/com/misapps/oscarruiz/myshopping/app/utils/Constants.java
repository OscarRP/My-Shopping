package com.misapps.oscarruiz.myshopping.app.utils;

/**
 * Created by Oscar Ruiz on 16/08/2017.
 */

public class Constants {

    /**
     * Defines time incremental
     */
    public static final int INCREMENTAL_TIME = 1000;

    /**
     * Time showing splash activity
     */
    public static final int SPLASH_TIME = 2000;

    /**
     * Indicates if user has been logged in
     */
    public static final String USER_LOGGED = "user_loged";

    /**
     * Deleting shoppinglists state
     */
    public static final String DELETING = "deleting";

    /**
     * Send shopping list state
     */
    public static final String SEND = "send";

    /**
     * Send shopping list int state
     */
    public static final String ITEM = "item";

    /**
     * Indicates if navigation must go from register to login or home
     */
    public static final String GO_TO_LOGIN = "go_to_login";

    /**
     * Indicates if navigation must go from register to register or home
     */
    public static final String GO_TO_REGISTER = "go_to_register";

    /**
     * Indicates users uid that is logged in
     */
    public static final String UID = "uid";

    /**
     * Indicates users nick that is logged in
     */
    public static final String NICK = "nick";

    /**
     * Google sign in request code
     */
    public static final int GOOGLE_SIGN_IN = 25;

    /**
     * Request code to select image from gallery
     */
    public static final int SELECT_GALLERY = 10;

    /**
     * Request code to select camera
     */
    public static final int SELECT_CAMERA = 20;

    /**
     * Directory to save photos
     */
    public static final String PHOTO_DIRECTORY = "myshopping/profile/photos/";

    /**
     * Application states interface
     */
    public interface APLICATION_STATES {
        public static final int SPLASH_STATE = 0;
        public static final int REGISTER_STATE = SPLASH_STATE + 1;
        public static final int LOGIN_STATE = REGISTER_STATE + 1;
        public static final int HOME_STATE = LOGIN_STATE + 1;
    }

    /**
     * Home states to control fragemnt navigation
     */
    public interface HOME_STATES {
        public static final int HOME_STATE = 0;
        public static final int ADD_SHOPPING_LIST_STATE = HOME_STATE + 1;
        public static final int DELETE_SHOPPING_LIST_STATE = ADD_SHOPPING_LIST_STATE + 1;
        public static final int DETAIL_SHOPPING_LIST_STATE = DELETE_SHOPPING_LIST_STATE + 1;
        public static final int SEND_SHOPPING_LIST_STATE = DETAIL_SHOPPING_LIST_STATE + 1;
        public static final int PROFILE_STATE = SEND_SHOPPING_LIST_STATE + 1;
        public static final int MY_CONTACTS_STATE = PROFILE_STATE + 1;
        public static final int ADD_CONTACT_STATE = MY_CONTACTS_STATE + 1;
        public static final int DELETE_CONTACT_STATE = ADD_CONTACT_STATE + 1;
    }

}
