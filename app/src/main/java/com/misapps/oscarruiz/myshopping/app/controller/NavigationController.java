package com.misapps.oscarruiz.myshopping.app.controller;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.misapps.oscarruiz.myshopping.R;
import com.misapps.oscarruiz.myshopping.app.activities.HomeActivity;
import com.misapps.oscarruiz.myshopping.app.activities.LoginActivity;
import com.misapps.oscarruiz.myshopping.app.activities.RegisterActivity;
import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;
import com.misapps.oscarruiz.myshopping.app.utils.Constants;

/**
 * Created by Oscar Ruiz on 16/08/2017.
 */

public class NavigationController {

    /**
     * go to login
     */
    private boolean goToLogin;

    /**
     * check if user is logged in
     */
    private boolean isLogged;

    /**
     * Home State controller
     */
    private HomeStateController homeStateController;

    /**
     * Fragment Manager
     */
    private FragmentManager fragmentManager;

    /**
     * App state controller
     */
    private AppStateController controller;


    public NavigationController() {
        controller = AppStateController.getInstance();
        homeStateController = HomeStateController.getInstance();
    }

    /**
     * Method to change activity
     */
    public void changeActivity(Activity activity, Bundle params) {
        //Create Intent
        Intent intent;

        switch (controller.getState()) {
            case Constants.APLICATION_STATES.SPLASH_STATE:
                //close activity
                activity.finish();

                isLogged = params.getBoolean(Constants.USER_LOGGED);
                //set intent
                if (isLogged) {
                    intent = new Intent(activity, HomeActivity.class);
                    //change controller state
                    controller.setState(Constants.APLICATION_STATES.HOME_STATE);
                } else {
                    intent = new Intent(activity, RegisterActivity.class);
                    //change controller state
                    controller.setState(Constants.APLICATION_STATES.REGISTER_STATE);
                }

                //check parms
                if(params!=null) {
                    //add paramas
                    intent.putExtras(params);
                }

                //start activity
                activity.startActivity(intent);

                break;

            case Constants.APLICATION_STATES.REGISTER_STATE:
                //check where to go
                goToLogin = params.getBoolean(Constants.GO_TO_LOGIN);
                if (goToLogin) {
                    intent = new Intent(activity, LoginActivity.class);
                    controller.setState(Constants.APLICATION_STATES.LOGIN_STATE);
                } else {
                    intent = new Intent(activity, HomeActivity.class);
                    controller.setState(Constants.APLICATION_STATES.HOME_STATE);
                }

                //start activity
                activity.startActivity(intent);

                break;

            case Constants.APLICATION_STATES.LOGIN_STATE:
                //check where to go
                boolean goToRegister = params.getBoolean(Constants.GO_TO_REGISTER);
                if (goToRegister) {
                    intent = new Intent(activity, RegisterActivity.class);
                    controller.setState(Constants.APLICATION_STATES.REGISTER_STATE);
                } else {
                    intent = new Intent(activity, HomeActivity.class);
                    controller.setState(Constants.APLICATION_STATES.HOME_STATE);
                }

                //start activity
                activity.startActivity(intent);

                break;
            case Constants.APLICATION_STATES.HOME_STATE:
                //check where to go
                goToLogin = params.getBoolean(Constants.GO_TO_LOGIN);
                if (goToLogin) {
                    intent = new Intent(activity, LoginActivity.class);
                    controller.setState(Constants.APLICATION_STATES.LOGIN_STATE);

                    //start activity
                    activity.startActivity(intent);
                }
                break;
        }
    }

    /**
     * Method to change fragment
     */
    public void changeFragment(Activity activity, Fragment fragment, Bundle params, int homeState) {

        //check params
        if (params != null) {
            fragment.setArguments(params);
        }
        //change fragment
        fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();

        if (homeStateController.getState() != homeState) {
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out).replace(R.id.fragment_container, fragment).commit();
        } else {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
        //set home state
        homeStateController.setState(homeState);
    }

    /**
     * Method to change fragment
     */
    public void backNavigation (Activity activity, Fragment fragment, Bundle params, int homeState) {

        if (homeStateController.getState() != homeState) {
            //check params
            if (params != null) {
                fragment.setArguments(params);
            }
            //change fragment
            fragmentManager = ((AppCompatActivity)activity).getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.back_fragment_in, R.anim.back_fragment_out).replace(R.id.fragment_container, fragment).commit();

            //set home state
            homeStateController.setState(homeState);
        }
    }

    /**
     * Method to init navigation at home
     */
    public void initNavigation(Activity activity, Fragment fragment) {
        //change fragment
        fragmentManager = ((AppCompatActivity)activity).getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out).replace(R.id.fragment_container, fragment).commit();

        //set home state
        homeStateController.setState(Constants.HOME_STATES.HOME_STATE);
    }

    /**
     * Method to get home state
     */
    public int getHomeState () {
        return homeStateController.getState();
    }

}
