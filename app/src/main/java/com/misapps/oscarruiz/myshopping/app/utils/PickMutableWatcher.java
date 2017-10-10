package com.misapps.oscarruiz.myshopping.app.utils;

import android.view.View;

import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;

/**
 * Created by Oscar Ruiz on 21/08/2017.
 */

public class PickMutableWatcher implements View.OnClickListener {

    /**
     * pick listener
     */
    private AppInterfaces.IPickItem pickListener;

    /**
     * position modified
     */
    private int mPosition;

    /**
     * if its active
     */
    private boolean mActive;

    public PickMutableWatcher(AppInterfaces.IPickItem pickListener) {
        this.pickListener = pickListener;
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
        pickListener.pickItem(mPosition);
    }
}
