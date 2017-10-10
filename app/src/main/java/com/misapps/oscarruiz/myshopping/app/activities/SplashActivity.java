package com.misapps.oscarruiz.myshopping.app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.misapps.oscarruiz.myshopping.R;
import com.misapps.oscarruiz.myshopping.app.controller.DataController;
import com.misapps.oscarruiz.myshopping.app.controller.NavigationController;
import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;
import com.misapps.oscarruiz.myshopping.app.models.User;
import com.misapps.oscarruiz.myshopping.app.session.Session;
import com.misapps.oscarruiz.myshopping.app.utils.Constants;

import org.json.JSONObject;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {

    /**
     * Data controller
     */
    private DataController dataController;

    /**
     * Handler to controll time in splash activity
     */
    private Handler handler;

    /**
     * Controls time in splash activity
     */
    private int time;

    /**
     * Navigation controller
     */
    private NavigationController navigationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //init navigation controller
        navigationController = new NavigationController();

        //init data controller
        dataController = new DataController();

        startHandler();
    }

    private void startHandler() {

        //time init
        time = 0;

        //Handler creation
        handler = new Handler();

        handler.postAtTime(new Runnable() {
            @Override
            public void run() {
                if (time >= Constants.SPLASH_TIME) {
                    //remove callbacks
                    handler.removeCallbacks(this);

                    //check user is logged
                    //retrieve info from sharedPreferences
                    SharedPreferences sharedPreferences = SplashActivity.this.getSharedPreferences(Constants.USER_LOGGED, Context.MODE_PRIVATE);
                    boolean userLoged = sharedPreferences.getBoolean(Constants.USER_LOGGED, false);
                    String nick = sharedPreferences.getString(Constants.NICK, "");

                    if (userLoged) {
                        //load user from database
                        dataController.loadUser(nick, new AppInterfaces.ILoadUser() {
                            @Override
                            public void loadUser(User user) {
                                //save user in session
                                Session session = Session.getInstance();
                                session.setUser(user);

                                //go to home activity
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(Constants.USER_LOGGED, true);
                                navigationController.changeActivity(SplashActivity.this, bundle);

                                //finish activity
                                finish();
                            }

                            @Override
                            public void error(DatabaseError error) {
                                Toast.makeText(SplashActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        //change Activity to register
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Constants.USER_LOGGED, false);
                        navigationController.changeActivity(SplashActivity.this, bundle);

                        //finish activity
                        finish();
                    }

                } else {
                    //increments time
                    time = time + Constants.INCREMENTAL_TIME;
                    handler.postDelayed(this, Constants.INCREMENTAL_TIME);
                }
            }
        }, Constants.INCREMENTAL_TIME);
    }
}
