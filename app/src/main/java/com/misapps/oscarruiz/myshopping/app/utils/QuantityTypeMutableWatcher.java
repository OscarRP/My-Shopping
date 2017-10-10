package com.misapps.oscarruiz.myshopping.app.utils;

import android.view.View;
import android.widget.AdapterView;

import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;
import com.misapps.oscarruiz.myshopping.app.models.Product;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Oscar Ruiz on 25/08/2017.
 *
 */

public class QuantityTypeMutableWatcher implements AdapterView.OnItemSelectedListener{

    /**
     * Product
     */
    private ArrayList<Product> products;

    /**
     * Change type listener
     */
    private AppInterfaces.IEditItem changeTypeListener;

    /**
     * position modified
     */
    private int mPosition;

    /**
     * if its active
     */
    private boolean mActive;

    public QuantityTypeMutableWatcher(ArrayList<Product> products, AppInterfaces.IEditItem changeTypeListener) {
        this.changeTypeListener = changeTypeListener;
        this.products = products;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public void setmActive(boolean mActive) {
        this.mActive = mActive;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mActive) {
            Product selected = products.get(mPosition);
            selected.setQuantityType(position);
            changeTypeListener.changeQuantityType(selected, mPosition, position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
