package com.misapps.oscarruiz.myshopping.app.utils;

import android.text.Editable;
import android.text.TextWatcher;

import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;
import com.misapps.oscarruiz.myshopping.app.models.Product;

import java.util.ArrayList;

/**
 * Created by Oscar Ruiz on 18/08/2017.
 */

public class QuantityMutableWatcher  implements TextWatcher {

    /**
     * Modified position
     */
    private int mPosition;

    /**
     * If its active
     */
    private boolean mActive;

    /**
     * Product list
     */
    private ArrayList<Product> products;


    /**
     * Change quantity listener
     */
    private AppInterfaces.IEditItem changeQuantityListener;

    public QuantityMutableWatcher (ArrayList<Product> products, AppInterfaces.IEditItem changeQuantityListener){
        this.products = products;
        this.changeQuantityListener = changeQuantityListener;
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        if (mActive) {
            Product selected = products.get(mPosition);
            if (s.toString().isEmpty()) {
                selected.setQuantity(0);
            } else {
                selected.setQuantity(Integer.parseInt(s.toString()));
            }
            changeQuantityListener.changeQuantity(selected, mPosition);
        }
    }
}
