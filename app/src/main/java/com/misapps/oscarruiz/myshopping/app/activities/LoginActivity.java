package com.misapps.oscarruiz.myshopping.app.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.misapps.oscarruiz.myshopping.R;
import com.misapps.oscarruiz.myshopping.app.controller.DataController;
import com.misapps.oscarruiz.myshopping.app.controller.NavigationController;
import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;
import com.misapps.oscarruiz.myshopping.app.models.User;
import com.misapps.oscarruiz.myshopping.app.session.Session;
import com.misapps.oscarruiz.myshopping.app.utils.Constants;
import com.misapps.oscarruiz.myshopping.app.utils.Utils;


public class LoginActivity extends AppCompatActivity {

    /**
     * User Uid
     */
    private String uid;

    /**
     * User instance
     */
    private User user;

    /**
     * Firebase Auth
     */
    private FirebaseAuth firebaseAuth;

    /**
     * Firebase database
     */
    private FirebaseDatabase database;

    /**
     * Login button
     */
    private Button loginButton;

    /**
     * Go to register button
     */
    private Button goToRegisterButton;

    /**
     * Password Input Edit Text
     */
    private TextInputEditText passwordET;

    /**
     * Email Input Edit Text
     */
    private TextInputEditText userET;

    /**
     * Data controller
     */
    private DataController dataController;

    /**
     * Navigation Controller
     */
    private NavigationController navigationController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getViews();
        setInfo();
        setListeners();
    }

    /**
     * Method to get views
     */
    private void getViews() {
        userET = (TextInputEditText)findViewById(R.id.user_edit_text);
        passwordET = (TextInputEditText)findViewById(R.id.password_edit_text);
        loginButton = (Button)findViewById(R.id.login_button);
        goToRegisterButton = (Button)findViewById(R.id.go_to_register);
    }

    /**
     * Method to set initial info
     */
    private void setInfo() {
        //init navigation controller
        navigationController = new NavigationController();

        dataController = new DataController();

        //Firebase Auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        //firebase database instance
        database = FirebaseDatabase.getInstance();
    }

    /**
     * Method to set listeners
     */
    private void setListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFields()) {

                    //load user
                    dataController.loadUser(userET.getText().toString(), new AppInterfaces.ILoadUser() {
                        @Override
                        public void loadUser(final User user) {

                            if (user != null) {
                                firebaseAuth.signInWithEmailAndPassword(user.getEmail(), passwordET.getText().toString()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {
                                            //save user in session
                                            Session.getInstance().setUser(user);

                                            //save user in preferences
                                            Utils utils = new Utils();
                                            utils.register(LoginActivity.this, user.getNick());

                                            //change activity to home
                                            Bundle bundle = new Bundle();
                                            bundle.putBoolean(Constants.GO_TO_REGISTER, false);
                                            navigationController.changeActivity(LoginActivity.this, bundle);

                                            //close activity
                                            finish();

                                        } else {
                                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_error), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(LoginActivity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void error(DatabaseError error) {
                            Log.i("PRUEBA", "ERROR: " + error.getMessage());
                        }
                    });
                }
            }
        });

        goToRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to register
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.GO_TO_REGISTER, true);
                navigationController.changeActivity(LoginActivity.this, bundle);
            }
        });
    }

    /**
     * Method to check if all fields are correctly filled
     */
    private boolean checkFields() {
        if (userET.getText().toString().isEmpty()){
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
