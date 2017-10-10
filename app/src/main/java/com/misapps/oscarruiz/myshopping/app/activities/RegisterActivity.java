package com.misapps.oscarruiz.myshopping.app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.misapps.oscarruiz.myshopping.R;
import com.misapps.oscarruiz.myshopping.app.controller.DataController;
import com.misapps.oscarruiz.myshopping.app.controller.NavigationController;
import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;
import com.misapps.oscarruiz.myshopping.app.models.User;
import com.misapps.oscarruiz.myshopping.app.session.Session;
import com.misapps.oscarruiz.myshopping.app.utils.Constants;
import com.misapps.oscarruiz.myshopping.app.utils.Utils;

public class RegisterActivity extends AppCompatActivity {

    /**
     * Utils instance
     */
    private Utils utils;

    /**
     * User instance
     */
    private User user;

    /**
     * Data controller
     */
    private DataController dataController;

    /**
     * Navigation controller
     */
    private NavigationController navigationController;

    /**
     * Firebase Auth instance
     */
    private FirebaseAuth firebaseAuth;

    /**
     * Google Api client
     */
    private GoogleApiClient googleApiClient;

    /**
     * Register button
     */
    private Button registerButton;

    /**
     * Go to login button
     */
    private Button loginButton;

    /**
     * User nickname
     */
    private TextInputEditText nickET;

    /**
     * User email
     */
    private TextInputEditText emailET;

    /**
     * User password
     */
    private TextInputEditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getViews();
        setInfo();
        setListeners();
    }

    /**
     * Method to get views
     */
    private void getViews() {
        nickET = (TextInputEditText) findViewById(R.id.nick_edit_text);
        emailET = (TextInputEditText) findViewById(R.id.email_edit_text);
        passwordET = (TextInputEditText) findViewById(R.id.password_edit_text);

        registerButton = (Button) findViewById(R.id.register_button);
        loginButton = (Button) findViewById(R.id.go_login_button);
    }

    /**
     * Method to set initial info
     */
    private void setInfo() {
        //init Google sign in options
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        //Google Api client
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        //set data controller
        dataController = new DataController();

        //set navigation controller
        navigationController = new NavigationController();

        //firebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();

        // utils instance
        utils = new Utils();
    }

    /**
     * Method to set listeners
     */
    private void setListeners() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkFields()) {
                    //check if user already exists
                    dataController.loadUser(nickET.getText().toString(), new AppInterfaces.ILoadUser() {
                        @Override
                        public void loadUser(User userLoaded) {
                            //if user dont exists
                            if (userLoaded == null) {
                                //Create user
                                firebaseAuth.createUserWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString()).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {
                                            //Save user in session
                                            user = new User(firebaseAuth.getCurrentUser().getUid(), nickET.getText().toString(), emailET.getText().toString(), passwordET.getText().toString());
                                            Session.getInstance().setUser(user);

                                            //Save user in database
                                            dataController.saveUser(user);

                                            utils.register(RegisterActivity.this, user.getNick());
                                            Toast.makeText(RegisterActivity.this, getString(R.string.user_created), Toast.LENGTH_SHORT).show();

                                            //go to HomeActivity
                                            Bundle bundle = new Bundle();
                                            bundle.putBoolean(Constants.GO_TO_LOGIN, false);
                                            navigationController.changeActivity(RegisterActivity.this, bundle);
                                            //finish activity
                                            finish();

                                        } else {
                                            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(RegisterActivity.this, getString(R.string.user_exsits), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void error(DatabaseError error) {

                        }
                    });
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to login
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.GO_TO_LOGIN, true);
                navigationController.changeActivity(RegisterActivity.this, bundle);
            }
        });
    }

    /**
     * Method to check if all fields are correctly filled
     */
    private boolean checkFields() {
        if (nickET.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.nick_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (emailET.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.email_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordET.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.password_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}


