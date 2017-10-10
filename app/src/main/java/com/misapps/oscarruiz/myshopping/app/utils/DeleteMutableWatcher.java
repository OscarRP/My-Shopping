package com.misapps.oscarruiz.myshopping.app.utils;

import android.view.View;

import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;

/**
 * Created by Oscar Ruiz on 18/08/2017.
 */

public class DeleteMutableWatcher implements View.OnClickListener {

    /**
     * Delete listener
     */
    private AppInterfaces.IEditItem deleteListener;

    /**
     * position modified
     */
    private int mPosition;

    /**
     * if its active
     */
    private boolean mActive;


    public DeleteMutableWatcher (AppInterfaces.IEditItem deleteListener) {
        this.deleteListener = deleteListener;
    }

    /**
     * Method to save position
     */
    public void setPosition(int position) { mPosition = position; }

    /**
     * Method to set active
     */
    public void setActive(boolean active) { mActive = active; }


    @Override
    public void onClick(View v) {
        deleteListener.deleteItem(mPosition);
    }
}
