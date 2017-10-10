package com.misapps.oscarruiz.myshopping.app.interfaces;

import android.net.Uri;
import android.view.View;

import com.google.firebase.database.DatabaseError;
import com.misapps.oscarruiz.myshopping.app.models.Product;
import com.misapps.oscarruiz.myshopping.app.models.User;

/**
 * Created by Oscar Ruiz on 17/08/2017.
 */

public class AppInterfaces {

    /**
     * Interface to edit item
     */
    public interface IEditItem {
        void deleteItem (int position);
        void changeQuantity (Product product, int position);
        void changePrice (Product product, int position);
        void changeQuantityType (Product product,int productPosition, int spinnerPosition);
    }

    /**
     * Interface to set item as picked
     */
    public interface IPickItem {
        public abstract void pickItem(int position);
    }

    /**
     * Interface to load user
     */
    public interface ILoadUser {
        void loadUser (User userLoaded);
        void error (DatabaseError error);
    }

    /**
     * Interface to delete shopping list
     */
    public interface IOperateList {
        void deleteList (int positiom);
        void sendList (int position);
    }

    /**
     * Interface to add profile image
     */
    public interface IAddImage{
        public abstract void addImage(String userPhotoUrl);
    }

    /**
     * Interface to remove profile image
     */
    public interface IRemoveImage {
        public abstract void removeImage();
    }

    /**
     * Interface to change password
     */
    public interface IChangePassword {
        public abstract void changePassword();
    }

    /**
     * Interface to upload image
     */
    public interface IUploadImage {
        public abstract void uploadIMage(Uri uri);
        public abstract void error(Exception exception);
    }

    /**
     * Interface to download image
     */
    public interface IDownloadImage {
        public abstract void downloadImage();
        public abstract void error(Exception exception);
    }

    /**
     * Interface to set image with glide in imageview
     */
    public interface ISetImge {
        public abstract void setImage();
    }

    /**
     * Interface to delete contact
     */
    public interface IDeleteContact {
        public abstract void deleteContact(int position);
    }
}
