package com.misapps.oscarruiz.myshopping.app.activities;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.misapps.oscarruiz.myshopping.R;
import com.misapps.oscarruiz.myshopping.app.adapters.MyContactsAdapter;
import com.misapps.oscarruiz.myshopping.app.controller.NavigationController;
import com.misapps.oscarruiz.myshopping.app.fragments.AddContactFragment;
import com.misapps.oscarruiz.myshopping.app.fragments.AddListFragment;
import com.misapps.oscarruiz.myshopping.app.fragments.ContactsFragment;
import com.misapps.oscarruiz.myshopping.app.fragments.HomeFragment;
import com.misapps.oscarruiz.myshopping.app.fragments.ProfileFragment;
import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;
import com.misapps.oscarruiz.myshopping.app.models.User;
import com.misapps.oscarruiz.myshopping.app.session.Session;
import com.misapps.oscarruiz.myshopping.app.utils.Constants;
import com.misapps.oscarruiz.myshopping.app.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Utils
     */
    private Utils utils;

    /**
     * Interface listener to add image
     */
    private AppInterfaces.IAddImage listener;

    /**
     * Intent content user select photo
     */
    private Intent data;

    /**
     * Array with all profile image bytes
     */
    private  ByteArrayOutputStream bytes;

    /**
     * User profile image ImageView
     */
    private ImageView imageView;

    /**
     * User nick Text View
     */
    private TextView nickTV;

    /**
     * User email Text View
     */
    private TextView emailTV;

    /**
     * Session instance
     */
    private Session session;

    /**
     * Current user
     */
    private User user;

    /**
     * Navigation controller
     */
    private NavigationController navigationController;

    /**
     * Action bar drawer toggle
     */
    private ActionBarDrawerToggle toggle;

    /**
     * Toolbar
     */
    private Toolbar toolbar;

    /**
     * Floating action button
     */
    private FloatingActionButton fab;

    /**
     * Drawer layout
     */
    private DrawerLayout drawer;

    /**
     * Navigation view
     */
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getViews();
        getInfo();
        setListeners();

        //init navigation
        Fragment homeFragment = new HomeFragment();

        navigationController.initNavigation(HomeActivity.this, homeFragment);
    }

    /**
     * Method to get views
     */
    private void getViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        View headerView = LayoutInflater.from(this).inflate(R.layout.nav_header_home, navigationView, true);
        nickTV = (TextView)headerView.findViewById(R.id.nick_text_view);
        emailTV = (TextView)headerView.findViewById(R.id.email_text_view);
        imageView = (ImageView)headerView.findViewById(R.id.profile_image_view);
    }

    /**
     * Method to set listeners
     */
    private void setListeners() {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navigationController.getHomeState() == Constants.HOME_STATES.HOME_STATE) {
                    //go to Add List Fragment
                    Fragment addListFragment = new AddListFragment();
                    navigationController.changeFragment(HomeActivity.this, addListFragment, null, Constants.HOME_STATES.ADD_SHOPPING_LIST_STATE);

                } else if (navigationController.getHomeState() == Constants.HOME_STATES.MY_CONTACTS_STATE) {
                    //go to add contact
                    Fragment addContactFragment = new AddContactFragment();
                    navigationController.changeFragment(HomeActivity.this, addContactFragment, null, Constants.HOME_STATES.ADD_CONTACT_STATE);
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        drawer.addDrawerListener(toggle);
    }

    /**
     * method to get info
     */
    private void getInfo() {
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //get user data
                user = Session.getInstance().getUser();
                if (user != null) {
                    nickTV.setText(user.getNick());
                    emailTV.setText(user.getEmail());
                    if (user.getProfileImage() != null) {
                        utils.setImage(user.getProfileImage(), HomeActivity.this, imageView, null);
                    }
                }
            }
        };
        toggle.syncState();

        //init navigation controller
        navigationController = new NavigationController();

        //init utils
        utils = new Utils();

        //init session and get user
        session = Session.getInstance();
        user = session.getUser();

        if (user != null) {
            nickTV.setText(user.getNick());
            emailTV.setText(user.getEmail());
            if (user.getProfileImage() != null) {
                utils.setImage(user.getProfileImage(), HomeActivity.this, imageView, null);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //Handle navigation
        switch (item.getItemId()) {
            case R.id.my_lists:
                //go to Home Fragment
                Fragment homeFragment = new HomeFragment();
                navigationController.changeFragment(HomeActivity.this, homeFragment, null, Constants.HOME_STATES.HOME_STATE);
                //change floating button image
                fab.setImageResource(R.mipmap.floating_icon);
                break;
            case R.id.add_list:
                //go to Add List Fragment
                Fragment addListFragment = new AddListFragment();
                navigationController.changeFragment(HomeActivity.this, addListFragment, null, Constants.HOME_STATES.ADD_SHOPPING_LIST_STATE);
                break;
            case R.id.delete_list:
                //go to Home Fragment with params Deleting
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.DELETING, true);
                Fragment deletingHomeFragment = new HomeFragment();
                navigationController.changeFragment(HomeActivity.this, deletingHomeFragment, bundle, Constants.HOME_STATES.DELETE_SHOPPING_LIST_STATE);
                break;
            case R.id.send_list:
                //go to Home Fragment with params Send list
                Bundle sendBundle = new Bundle();
                sendBundle.putBoolean(Constants.SEND, true);
                Fragment sendHomeFragment = new HomeFragment();
                navigationController.changeFragment(HomeActivity.this, sendHomeFragment, sendBundle, Constants.HOME_STATES.SEND_SHOPPING_LIST_STATE);
                break;
            case R.id.my_profile:
                Fragment profileFragmnent = new ProfileFragment();
                navigationController.changeFragment(HomeActivity.this, profileFragmnent, null, Constants.HOME_STATES.PROFILE_STATE);
                break;
            case R.id.my_contacts:
                //set toolbar title
                setToolbarTitle(getString(R.string.my_contacts));

                Fragment myContactsFragment = new ContactsFragment();
                navigationController.changeFragment(HomeActivity.this, myContactsFragment, null, Constants.HOME_STATES.MY_CONTACTS_STATE);
                //change floating button image
                fab.setImageResource(R.mipmap.add_contact_fab);
                break;
            case R.id.add_contact:
                //set toolbar title
                setToolbarTitle(getString(R.string.add_contact_header));
                //go to add contact
                Fragment addContactFragment = new AddContactFragment();
                navigationController.changeFragment(HomeActivity.this, addContactFragment, null, Constants.HOME_STATES.ADD_CONTACT_STATE);
                break;
            case R.id.delete_contact:
                //set toolbar title
                setToolbarTitle(getString(R.string.delete_contact_header));

                //go to delete contact
                Fragment myContactsDeletingFragment = new ContactsFragment();
                Bundle deleteContactBundle = new Bundle();
                deleteContactBundle.putBoolean(Constants.DELETING, true);
                navigationController.changeFragment(HomeActivity.this, myContactsDeletingFragment, deleteContactBundle, Constants.HOME_STATES.MY_CONTACTS_STATE);
                //change floating button image
                fab.setImageResource(R.mipmap.add_contact_fab);
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Method to hide floating action button
     */
    public void hideFAB() {
        fab.setVisibility(View.GONE);
    }

    /**
     * Method to show floating action buton
     */
    public void showFAB() {
        fab.setVisibility(View.VISIBLE);
    }

    /**
     * Method to set title in Action bar
     */
    public void setToolbarTitle (String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //if is home fragment, exit
            if (navigationController.getHomeState() == Constants.HOME_STATES.HOME_STATE) {
                super.onBackPressed();
//                finishAndRemoveTask();
            } else {
                //go to Home Fragment
                Fragment homeFragment = new HomeFragment();
                navigationController.backNavigation(HomeActivity.this, homeFragment, null, Constants.HOME_STATES.HOME_STATE);

                //change fab image
                fab.setImageResource(R.mipmap.floating_icon);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //save data
        this.data = data;

        //user profile photo url
        String userPhotoUrl = null;

        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.SELECT_CAMERA) {
                //get data from intent
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                FileOutputStream fo;

                if(Utils.isPermission(this)){
                    try {

                        //get directory
                        File direct = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "myshopping/profile/photos/");

                        //if directory not exits
                        if(!direct.exists()){
                            //create
                            if(!direct.mkdirs()){
                                direct.mkdir();
                            }
                        }

                        //save image
                        String filename = System.currentTimeMillis() + ".jpg";
                        File destination = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "myshopping/profile/photos/", filename);
                        destination.createNewFile();
                        //get path
                        userPhotoUrl =Environment.getExternalStorageDirectory().toString() + File.separator + "myshopping/profile/photos/" + filename;
                        //write in external storage
                        fo = new FileOutputStream(destination);
                        fo.write(bytes.toByteArray());
                        //close
                        fo.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //callback
                    listener.addImage(userPhotoUrl);
                }
            } else if (requestCode == Constants.SELECT_GALLERY) {
                if(Utils.isPermission(this)) {
                    //get data from intent
                    Uri selectedImageUri = data.getData();

                    //callback
                    listener.addImage(selectedImageUri.toString());
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //camera result permission
        if (requestCode == Constants.SELECT_CAMERA && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, Constants.SELECT_CAMERA);
        }

        //gallery result permission
        if (requestCode == Constants.SELECT_GALLERY && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
            //open gallery
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, Constants.SELECT_GALLERY);
        }

        //check permision
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED && data!=null){

            if (requestCode == Constants.SELECT_CAMERA) {

            } else if (requestCode == Constants.SELECT_GALLERY) {
                if(Utils.isPermission(this)) {
                    //get data from intent
                    Uri selectedImageUri = data.getData();
                    //callback
                    listener.addImage(selectedImageUri.toString());
                }
            }
        }
    }

    /**
     * Method to change profile image
     */
    public void changeImageProfile(AppInterfaces.IAddImage listener, AppInterfaces.IRemoveImage removeListener){
        //save listener
        this.listener = listener;
        //show dialog with options
        Utils.selectProfileImage(getResources().getStringArray(R.array.add_image_options),this,getString(R.string.select_image_dialog_tittle), removeListener);
    }

}
