package com.misapps.oscarruiz.myshopping.app.controller;

/**
 * Created by Oscar Ruiz on 17/08/2017.
 */

public class HomeStateController {

    /**
     * Singleton
     */
    private static HomeStateController controller;

    /**
     * Home State
     */
    private static int state;

    /**
     * Method to create controller
     */
    public static HomeStateController getInstance() {
        //check if is created
        if(controller==null){
            //create controller
            controller = new HomeStateController();
        }

        return controller;
    }

    /**
     * Method to get App state
     */
    public static int getState() {
        return state;
    }

    /**
     * Method to set App state
     */
    public static void setState(int state) {
        HomeStateController.state = state;
    }
}
