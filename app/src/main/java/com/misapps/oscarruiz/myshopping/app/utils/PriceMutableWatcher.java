package com.misapps.oscarruiz.myshopping.app.utils;

import android.text.Editable;
import android.text.TextWatcher;
import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;
import com.misapps.oscarruiz.myshopping.app.models.Product;
import java.util.ArrayList;

/**
 * Created by Oscar Ruiz on 18/08/2017.
 */

/**
 * Class to save position where product price changes, and send listener to set new data
 */
public class PriceMutableWatcher implements TextWatcher {
    /**
     * Position modified
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
     * Change price listener
     */
    private AppInterfaces.IEditItem changePriceListener;

    public PriceMutableWatcher (ArrayList<Product> products, AppInterfaces.IEditItem changePriceListener){
        this.products = products;
        this.changePriceListener = changePriceListener;
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
        //if its active, set new price in product and send listener
        if (mActive) {
            Product selected = products.get(mPosition);
            if (s.toString().isEmpty()) {
                selected.setPrice(0);
            } else {
                selected.setPrice(Double.parseDouble(s.toString()));
            }
            changePriceListener.changePrice(selected, mPosition);
        }
    }
}
