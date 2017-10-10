package com.misapps.oscarruiz.myshopping.app.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.misapps.oscarruiz.myshopping.R;
import com.misapps.oscarruiz.myshopping.app.controller.DataController;
import com.misapps.oscarruiz.myshopping.app.controller.NavigationController;
import com.misapps.oscarruiz.myshopping.app.fragments.ProfileFragment;
import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;
import com.misapps.oscarruiz.myshopping.app.models.Product;
import com.misapps.oscarruiz.myshopping.app.models.ShoppingList;
import com.misapps.oscarruiz.myshopping.app.models.User;
import com.misapps.oscarruiz.myshopping.app.session.Session;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.View.VISIBLE;

/**
 * Created by Oscar Ruiz on 16/08/2017.
 */

public class Utils {

    /**
     * Add image listener
     */
    private AppInterfaces.IAddImage listener;

    /**
     * Firebase Auth instance
     */
    private FirebaseAuth firebaseAuth;

    /**
     * Method to register user, save in preferences
     */
    public void register(Activity activity, String nick) {
        //save user is registered in preferences
        boolean userLogged = true;
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.USER_LOGGED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.USER_LOGGED, userLogged);
        editor.putString(Constants.NICK, nick);

        editor.commit();
    }

    /**
     * Method to get actual date
     */
    public String getDate() {
        //date instance
        Calendar date = Calendar.getInstance();

        //date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        //return actual date
        return dateFormat.format(date.getTime());
    }

    /**
     * Method to close session and go to login
     */
    public void closeSession(Activity activity) {
        //close session
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.USER_LOGGED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.USER_LOGGED, false);

        editor.commit();

        //go to login
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.GO_TO_LOGIN, true);
        NavigationController navigationController = new NavigationController();
        navigationController.changeActivity(activity, bundle);
    }

    /**
     * Method to calculate total price
     */
    public String calculateTotalPrice(ArrayList<Product> productList) {
        Double totalPrice = 0.0;
        for (int i=0; i<productList.size(); i++) {
            totalPrice = totalPrice + productList.get(i).getPrice() * productList.get(i).getQuantity();
        }
        DecimalFormat df = new DecimalFormat("####0.00");
        String totalPriceString = String.valueOf(df.format(totalPrice)) + " €";
        return totalPriceString;
    }

    /**
     * Method to select a Image source
     */
    public static void selectProfileImage(final String[] options, final Activity activity, final String tittle, final AppInterfaces.IRemoveImage removeListener) {
        //create dialog
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
        builder.setTitle(tittle);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    //check for permissions
                    if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        //open camera
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        activity.startActivityForResult(intent, Constants.SELECT_CAMERA);
                    } else {
                        //request permissions
                        checkAndRequestPermissions(activity, Constants.SELECT_CAMERA);
                    }

                } else if (item==1) {
                    //check for permissions
                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        //open gallery
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        activity.startActivityForResult(Intent.createChooser(intent, tittle), Constants.SELECT_GALLERY);
                    } else {
                        //request permissions
                        checkAndRequestPermissions(activity, Constants.SELECT_GALLERY);
                    }
                }else if (item == 2){
                    removeListener.removeImage();
                } else if (item == 3) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * Method to check all request permissions
     */
    private static void checkAndRequestPermissions(Activity activity, int permission) {
        int permissionCamera = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int permissionWrite = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //check if user pressed select from gallery
        if (permission == Constants.SELECT_GALLERY) {
            //check permission
            if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
                //request permission
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.SELECT_GALLERY);
        }
            //check if user pressed select from camera
        } else if (permission == Constants.SELECT_CAMERA) {
            //list of permissions needed
            List<String> listPermissionsNeeded = new ArrayList<>();
            //check external storage permission
            if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
                //add to permissions needed
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            //check camera permission
            if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
                //add to permissions needed
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }
            //request permissions needed
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), Constants.SELECT_CAMERA);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.CAMERA},  Constants.SELECT_CAMERA);
            }
        }
    }

    /**
     * Method to check write external permission
     */
    public static boolean isPermission(Activity activity) {

        //init variable
        boolean ok = true;

        //check id device is Android M
        if (Build.VERSION.SDK_INT >= 23) {
            if(activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        return  ok;
    }

    /**
     * Method to set image
     */
    public void setImage(String userPhotoUrl, final Activity activity, final ImageView profileImage, final AppInterfaces.ISetImge setImageListener) {

        if  (userPhotoUrl.isEmpty()) {
            profileImage.setImageResource(R.mipmap.profile);
        } else {
            try {
                //Rounded image
                Glide.with(activity).load(userPhotoUrl).asBitmap().centerCrop().placeholder(R.mipmap.profile).into(new BitmapImageViewTarget(profileImage) {
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
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    /**
     * Method to search a shopping list in an user
     */
    public int userHasShoppingList (User user, String listName) {
        int found = -1;
        //check if user has any shopping list
        if (user.getShoppingLists() != null) {
            //search shopping list
            for (int i = 0; i < user.getShoppingLists().size(); i++) {
                if (user.getShoppingLists().get(i).getName().equals(listName)) {
                    found = i;
                }
            }
        } else {
            found = 0;
        }
        return found;
    }

    /**
     * Method to merge shopping lists
     */
    public ShoppingList mergeShoppingLists (ShoppingList list1, ShoppingList list2) {
        //init final shopping list, merge result
        ShoppingList finalShoppingList = new ShoppingList(list1.getProducts(), list1.getName(), list1.getCreationDate());
        //to know if a product exists in both list
        boolean found;
        int count;

        //check if list2 products are in finalshoppinglist
        for (int i = 0; i<list2.getProducts().size(); i++) {
            count = 0;
            found = false;

            do {
                if (finalShoppingList.getProducts().get(count).getProductName().equals(list2.getProducts().get(i).getProductName())) {
                    found = true;
                }
                count++;
            } while (!found && count < finalShoppingList.getProducts().size());

            if (!found) {
                finalShoppingList.getProducts().add(list2.getProducts().get(i));
            }
        }
        return finalShoppingList;
    }

    /**
     * Method to hide keyboard
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Method to search products
     */
    public ArrayList<Product> searchProducts(ArrayList<Product> productList, String text) {
        //init aux vars
        ArrayList<Product> finalProductList = new ArrayList<>();
        String auxString;

        if (productList != null) {
            //search coincidences trhough hole users list
            for (int i=0; i<productList.size(); i++) {
                //check text contains any character
                if (text.length() > 0) {
                    if (productList.get(i).getProductName().length() >= text.length()){
                        auxString = productList.get(i).getProductName().substring(0, text.length()).toLowerCase();
                        if (text.equals(auxString)) {
                            finalProductList.add(productList.get(i));
                        }
                    }
                } else {
                    finalProductList.add(productList.get(i));
                }
            }
        }
        return finalProductList;
    }
}
