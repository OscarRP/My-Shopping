package com.misapps.oscarruiz.myshopping.app.adapters;

import android.content.Context;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.misapps.oscarruiz.myshopping.R;
import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;
import com.misapps.oscarruiz.myshopping.app.models.Product;
import com.misapps.oscarruiz.myshopping.app.utils.DecimalDigitsInputFilter;
import com.misapps.oscarruiz.myshopping.app.utils.DeleteMutableWatcher;
import com.misapps.oscarruiz.myshopping.app.utils.PickMutableWatcher;
import com.misapps.oscarruiz.myshopping.app.utils.PriceMutableWatcher;
import com.misapps.oscarruiz.myshopping.app.utils.QuantityMutableWatcher;
import com.misapps.oscarruiz.myshopping.app.utils.QuantityTypeMutableWatcher;
import com.misapps.oscarruiz.myshopping.app.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Oscar Ruiz on 18/08/2017.
 */

public class DetailedShoppingListAdapter extends BaseAdapter {

    /**
     * Pick item listener
     */
    private AppInterfaces.IPickItem pickListener;

    /**
     * Boolean to know if its deleting
     */
    private boolean isDeleting;

    /**
     * Viewholder Instance
     */
    private ViewHolder viewHolder;

    /**
     * Layout inflater
     */
    private LayoutInflater layoutInflater;

    /**
     * Products list
     */
    private ArrayList<Product> products;

    /**
     * Context
     */
    private Context context;

    /**
     * Edit Item listener
     */
    private AppInterfaces.IEditItem editItemListener;

    public DetailedShoppingListAdapter(Context context, ArrayList<Product> products, boolean isDeleting, AppInterfaces.IEditItem editItemListener, AppInterfaces.IPickItem pickListener) {

        this.products = products;
        this.context = context;
        this.isDeleting = isDeleting;
        this.editItemListener = editItemListener;
        this.pickListener = pickListener;

        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
//            convertView = layoutInflater.inflate(R.layout.item_detailed_shopping_list, parent, false);
            convertView = layoutInflater.inflate(R.layout.testing_layout, parent, false);
            //init viewholder
            viewHolder = new ViewHolder();
            viewHolder.mWatcherQuantity = new QuantityMutableWatcher(products, editItemListener);
            viewHolder.mWatcherPrice = new PriceMutableWatcher(products, editItemListener);
//            viewHolder.mWatcherDelete = new DeleteMutableWatcher(editItemListener);
//            viewHolder.mWatcherPick = new PickMutableWatcher(pickListener);
            viewHolder.mWatcherType = new QuantityTypeMutableWatcher(products, editItemListener);

            //init views
            viewHolder.productName = (TextView)convertView.findViewById(R.id.name_text_view);
            viewHolder.productQuantity = (EditText)convertView.findViewById(R.id.quantity_edit_text);
            viewHolder.productPrice = (EditText)convertView.findViewById(R.id.price_edit_text);
            //filter with 5 integers and 2 decimals for price
            viewHolder.productPrice.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5,2)});
//            viewHolder.deleteButton = (Button)convertView.findViewById(R.id.delete_button);
//            viewHolder.pickButton = (Button)convertView.findViewById(R.id.pick_button);
            viewHolder.contentLayout = (RelativeLayout) convertView.findViewById(R.id.linearLayout);
            viewHolder.typeSpinner = (Spinner)convertView.findViewById(R.id.type_spinner);

            viewHolder.productQuantity.addTextChangedListener(viewHolder.mWatcherQuantity);
            viewHolder.productPrice.addTextChangedListener(viewHolder.mWatcherPrice);
//            viewHolder.deleteButton.setOnClickListener(viewHolder.mWatcherDelete);
//            viewHolder.pickButton.setOnClickListener(viewHolder.mWatcherPick);
            viewHolder.typeSpinner.setOnItemSelectedListener(viewHolder.mWatcherType);

            if (isDeleting) {
                //show delete button
//                viewHolder.deleteButton.setVisibility(View.VISIBLE);
//                viewHolder.pickButton.setVisibility(convertView.INVISIBLE);
            }

            //set tag
            convertView.setTag(viewHolder);
        } else {
            //get tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Mutable watchers to false
        viewHolder.mWatcherQuantity.setActive(false);
        viewHolder.mWatcherPrice.setActive(false);
//        viewHolder.mWatcherDelete.setActive(false);
//        viewHolder.mWatcherPick.setActive(false);
        viewHolder.mWatcherType.setmActive(false);

        //set info
        viewHolder.productName.setText(products.get(position).getProductName());
        viewHolder.productQuantity.setText(String.valueOf(products.get(position).getQuantity()), TextView.BufferType.EDITABLE);

        String price = String.valueOf(products.get(position).getPrice());
        viewHolder.productPrice.setText((price), TextView.BufferType.EDITABLE);
        viewHolder.typeSpinner.setSelection(products.get(position).getQuantityType());

        if (products.get(position).isPicked()) {
            viewHolder.contentLayout.setBackgroundColor(context.getResources().getColor(R.color.light_green));
        } else {
            viewHolder.contentLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        //save position in mutable watchers
        viewHolder.mWatcherQuantity.setPosition(position);
        viewHolder.mWatcherPrice.setPosition(position);
//        viewHolder.mWatcherDelete.setPosition(position);
//        viewHolder.mWatcherPick.setPosition(position);
        viewHolder.mWatcherType.setmPosition(position);

        //Mutable watchers actives
        viewHolder.mWatcherQuantity.setActive(true);
        viewHolder.mWatcherPrice.setActive(true);
//        viewHolder.mWatcherDelete.setActive(true);
//        viewHolder.mWatcherPick.setActive(true);
        viewHolder.mWatcherType.setmActive(true);

        return convertView;
    }

    /**
     * Viewholder class
     */
    private class ViewHolder {
        private TextView productName;
        private EditText productQuantity;
        private EditText productPrice;
        private Button deleteButton;
        private RelativeLayout contentLayout;
        private Button pickButton;
        private Spinner typeSpinner;

        public QuantityMutableWatcher mWatcherQuantity;
        public PriceMutableWatcher mWatcherPrice;
        public DeleteMutableWatcher mWatcherDelete;
        public PickMutableWatcher mWatcherPick;
        public QuantityTypeMutableWatcher mWatcherType;
    }
}
