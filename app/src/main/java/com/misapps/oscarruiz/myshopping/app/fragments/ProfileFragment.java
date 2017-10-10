package com.misapps.oscarruiz.myshopping.app.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.misapps.oscarruiz.myshopping.R;
import com.misapps.oscarruiz.myshopping.app.activities.HomeActivity;
import com.misapps.oscarruiz.myshopping.app.controller.DataController;
import com.misapps.oscarruiz.myshopping.app.controller.NavigationController;
import com.misapps.oscarruiz.myshopping.app.dialogs.Dialogs;
import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;
import com.misapps.oscarruiz.myshopping.app.models.User;
import com.misapps.oscarruiz.myshopping.app.session.Session;
import com.misapps.oscarruiz.myshopping.app.utils.Constants;
import com.misapps.oscarruiz.myshopping.app.utils.Utils;

import static android.view.View.VISIBLE;

public class ProfileFragment extends Fragment {

    /**
     * Data controller
     */
    private DataController dataController;

    /**
     * Navigation controller
     */
    private NavigationController navigationController;

    /**
     * Utils
     */
    private Utils utils;

    /**
     * Change image listener
     */
    private AppInterfaces.IAddImage listener;

    /**
     * Check fields
     */
    private boolean fieldsOK;

    /**
     * if is changing password
     */
    private boolean isChangingPassword;

    /**
     * Button to cancel password change
     */
    private Button cancel;

    /**
     * new password edit text
     */
    private EditText newPassword;

    /**
     * Repeat password Edittext
     */
    private EditText repeatPassword;

    /**
     * Current password edit text
     */
    private EditText currentPassword;

    /**
     * Change password button
     */
    private Button changePassword;

    /**
     * Current session
     */
    private Session session;

    /**
     * Current user
     */
    private User user;

    /**
     * User name text view
     */
    private TextView userName;

    /**
     * Change profile image button
     */
    private ImageView changeProfileImage;

    /**
     * Profile image image view
     */
    private ImageView profileImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        getViews(view);

        setInfo();

        setListeners();

        return view;
    }

    /**
     * Method to get views
     */
    private void getViews(View view) {

        profileImage = (ImageView)view.findViewById(R.id.user_image);
        changeProfileImage = (ImageView)view.findViewById(R.id.change_profile_image);
        userName =(TextView)view.findViewById(R.id.user_name_text_view);
        changePassword = (Button)view.findViewById(R.id.change_password_button);
        cancel = (Button)view.findViewById(R.id.cancel_button);
        currentPassword = (EditText)view.findViewById(R.id.current_password_edit_text);
        newPassword = (EditText)view.findViewById(R.id.password_edit_text);
        repeatPassword = (EditText)view.findViewById(R.id.repeat_password_edit_text);
    }

    /**
     * Method to set initial info
     */
    private void setInfo() {
        //set options menu
        setHasOptionsMenu(true);

        ((HomeActivity)getActivity()).hideFAB();

        //set toolbar title
        ((HomeActivity)getActivity()).setToolbarTitle(getString(R.string.profile_title));

        //init utils
        utils = new Utils();

        //init data controller
        dataController = new DataController();

        //init navigation controller
        navigationController = new NavigationController();

        user = Session.getInstance().getUser();
        if (user.getProfileImage() != null) {
            utils.setImage(user.getProfileImage(), getActivity(), profileImage, null);
        }

    }

    /**
     * Method to create Options Menu
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.home, menu);
    }

    /**
     * Method to set menu actions
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.close_session:
                utils.closeSession(getActivity());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to set listeners
     */
    private void setListeners() {
        //change profile image
        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Choose photo menu options
                ((HomeActivity)getActivity()).changeImageProfile(new AppInterfaces.IAddImage() {
                    @Override
                    public void addImage(final String userPhotoUrl) {
                        //set profile image into imageview
                        utils.setImage(userPhotoUrl, getActivity(), profileImage, new AppInterfaces.ISetImge() {
                            @Override
                            public void setImage() {
                                //upload image to firebase storage
                                dataController.uploadProfileImage(user, profileImage, new AppInterfaces.IUploadImage() {
                                    @Override
                                    public void uploadIMage(Uri uri) {
                                        user.setDownloadURL(uri.toString());

                                        //set profile image into imageview
                                        user.setProfileImage(userPhotoUrl);

                                        //save image
                                        Session.getInstance().setUser(user);
                                        dataController.saveUser(user);
                                    }

                                    @Override
                                    public void error(Exception exception) {

                                    }
                                });
                            }
                        });


                    }
                }, new AppInterfaces.IRemoveImage() {
                    @Override
                    public void removeImage() {
                        user.setProfileImage("");
                        Session.getInstance().setUser(user);
                        utils.setImage("", getActivity(), profileImage, null);
                    }
                });
            }
        });

        //Change password
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChangingPassword) {
                    //check fields
                    if (checkFields()) {
                        //set new user password
                        user.setPassword(newPassword.getText().toString());

                        //show loading dialog
                        final Dialogs dialogs = new Dialogs();
                        dialogs.showLoadingDialog(getActivity());

                        //update password in firebase
                        dataController.updatePassword(newPassword.getText().toString(), getActivity(), new AppInterfaces.IChangePassword() {
                            @Override
                            public void changePassword() {
                                dialogs.hideLoadingDialog();
                                Toast.makeText(getActivity(), getString(R.string.password_changed), Toast.LENGTH_SHORT).show();
                                //save new password in session
                                user.setPassword(newPassword.getText().toString());
                                Session.getInstance().setUser(user);
                                dataController.saveUser(user);

                                //Hide change password fields
                                cancel.setVisibility(View.GONE);
                                currentPassword.setVisibility(View.INVISIBLE);
                                newPassword.setVisibility(View.INVISIBLE);
                                repeatPassword.setVisibility(View.INVISIBLE);

                                //change button text
                                changePassword.setText(getResources().getString(R.string.change_password));

                                isChangingPassword = false;
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.register_error), Toast.LENGTH_SHORT).show();
                    }

                } else {

                    isChangingPassword = true;

                    //Show change password fields
                    cancel.setVisibility(VISIBLE);
                    currentPassword.setVisibility(VISIBLE);
                    newPassword.setVisibility(VISIBLE);
                    repeatPassword.setVisibility(VISIBLE);

                    //change text button
                    changePassword.setText(getResources().getString(R.string.dialog_ok_button));
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isChangingPassword = false;

                changePassword.setText(getResources().getString(R.string.change_password));

                cancel.setVisibility(View.GONE);
                currentPassword.setText("");
                currentPassword.setVisibility(View.INVISIBLE);
                newPassword.setText("");
                newPassword.setVisibility(View.INVISIBLE);
                repeatPassword.setText("");
                repeatPassword.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * Method to check fields are correctly filled
     */
    private boolean checkFields() {

        fieldsOK = false;
        if (!currentPassword.getText().toString().isEmpty() && currentPassword.getText().toString().equals(user.getPassword())) {
            if(!newPassword.getText().toString().isEmpty() && !repeatPassword.getText().toString().isEmpty() && newPassword.getText().toString().equals(repeatPassword.getText().toString())) {
                fieldsOK = true;
            }
        }
        return fieldsOK;
    }
}
