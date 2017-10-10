package com.misapps.oscarruiz.myshopping.app.controller;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.misapps.oscarruiz.myshopping.R;
import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;
import com.misapps.oscarruiz.myshopping.app.models.User;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * Created by Oscar Ruiz on 16/08/2017.
 */

public class DataController {

    /**
     * Load user interface
     */
    private AppInterfaces.ILoadUser loadListener;

    /**
     * User isntance
     */
    private User user;

    /**
     * Firebase Database instance
     */
    private FirebaseDatabase database;

    /**
     * Firebase Storage reference instance
     */
    private FirebaseStorage storage;

    /**
     * Firebase auth instance
     */
    private FirebaseAuth firebaseAuth;

    /**
     * Method to init firebase
     */
    private void initFirebase() {
        //firebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();
        //firebase database instance
        database = FirebaseDatabase.getInstance();
        //firebaseStorage instance
        storage = FirebaseStorage.getInstance();
    }

    /**
     * Method to save user in firebase
     */
    public void saveUser(User user) {
        initFirebase();

        //Save user in database
//        String uid = firebaseAuth.getCurrentUser().getUid();

        String nick = user.getNick();

        DatabaseReference ref = database.getReference().child("users").child(nick);
        ref.setValue(user);
    }

    /**
     * Method to change user password
     */
    public void updatePassword(String newPassword, final Activity activity, final AppInterfaces.IChangePassword changePassListener) {
        initFirebase();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    changePassListener.changePassword();
                }
            }
        });
    }

    /**
     * Method to load user from firebase
     */
    public void loadUser(String nick, final AppInterfaces.ILoadUser loadListener) {
        initFirebase();

        this.loadListener = loadListener;

        DatabaseReference ref = database.getReference().child("users").child(nick);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //convert DataSnapshot to Hashmap
                HashMap<String, JSONObject> jsonSnapshot = (HashMap<String, JSONObject>) dataSnapshot.getValue();

                //convert Hashmap to Json String
                String jsonString = new Gson().toJson(jsonSnapshot);

                //convert jsonString to user
                Gson gson = new Gson();
                user = gson.fromJson(jsonString, User.class);

                //send back user
                loadListener.loadUser(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loadListener.error(databaseError);
        }
        });
    }

    /**
     * Method to search user with single value event
     */
    public void searchUser(String nick, final AppInterfaces.ILoadUser loadListener) {
        initFirebase();

        this.loadListener = loadListener;

        DatabaseReference ref = database.getReference().child("users").child(nick);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //convert DataSnapshot to Hashmap
                HashMap<String, JSONObject> jsonSnapshot = (HashMap<String, JSONObject>) dataSnapshot.getValue();

                //convert Hashmap to Json String
                String jsonString = new Gson().toJson(jsonSnapshot);

                //convert jsonString to user
                Gson gson = new Gson();
                user = gson.fromJson(jsonString, User.class);

                //send back user
                loadListener.loadUser(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loadListener.error(databaseError);
            }
        });
    }

    /**
     * Method to upload profile image to database storage
     */
    public void uploadProfileImage(User user, ImageView imageView, final AppInterfaces.IUploadImage uploadListener) {
        initFirebase();

        //create reference
        StorageReference storageReference = storage.getReference().child(user.getNick()+"/profile.jpg");

        //get data from imageview as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                uploadListener.error(e);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadURL = taskSnapshot.getDownloadUrl();
                uploadListener.uploadIMage(downloadURL);
            }
        });
    }

    /**
     * Method to download profile image from database storage
     */
    public void downloadProfileImage(final Activity activity, User user, final ImageView profileImage, final AppInterfaces.ISetImge setImageListener) {
        initFirebase();

        //create reference
        StorageReference storageReference = storage.getReference().child(user.getNick()+"/profile.jpg");

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //Rounded image
                Glide.with(activity).load(uri).asBitmap().centerCrop().placeholder(R.mipmap.profile).into(new BitmapImageViewTarget(profileImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(activity.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        profileImage.setImageDrawable(circularBitmapDrawable);
                        if (setImageListener != null) {
                            setImageListener.setImage();
                        }
                    }
                });
            }
        });
    }
}
