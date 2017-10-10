package com.misapps.oscarruiz.myshopping.app.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.misapps.oscarruiz.myshopping.R;
import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;
import com.misapps.oscarruiz.myshopping.app.models.ShoppingList;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Oscar Ruiz on 17/08/2017.
 */

public class HomeShopingListsAdapter extends BaseAdapter {

    /**
     * Delete list listener
     */
    private AppInterfaces.IOperateList operateListener;

    /**
     * Holder with all views
     */
    private ViewHolder viewHolder;

    /**
     * Layout inflater
     */
    private LayoutInflater inflater;

    /**
     * Shopping lists
     */
    private ArrayList<ShoppingList> shoppingLists;

    /**
     * Is deleting
     */
    private boolean isDeleting;

    /**
     * Is send
     */
    private boolean isSend;

    /**
     * Context
     */
    private Context context;

    public HomeShopingListsAdapter(Context context, ArrayList<ShoppingList> shoppingLists, boolean isDeleting, boolean isSend, AppInterfaces.IOperateList operateListener) {
        this.shoppingLists = shoppingLists;
        this.context = context;
        this.isDeleting = isDeleting;
        this.isSend = isSend;
        this.operateListener = operateListener;

        //init inflater
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return shoppingLists.size();
    }

    @Override
    public Object getItem(int i) {
        return shoppingLists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            //inflate view
            view = inflater.inflate(R.layout.home_shoppinglists_item, viewGroup, false);

            //initialize viewholder
            viewHolder = new ViewHolder();

            //getviews
            viewHolder.nameTV =(TextView)view.findViewById(R.id.shopping_list_name_text_view);
            viewHolder.firstProductTV = (TextView)view.findViewById(R.id.first_product_text_view);
            viewHolder.secondProductTV = (TextView)view.findViewById(R.id.second_product_text_view);
            viewHolder.thirdProductTV = (TextView)view.findViewById(R.id.third_product_text_view);
            viewHolder.deleteButton = (ImageButton) view.findViewById(R.id.delete_list_button);
            viewHolder.dateTV = (TextView)view.findViewById(R.id.date_text_view);
            viewHolder.sendButton = (ImageButton)view.findViewById(R.id.send_list_button);

            //set tag
            view.setTag(viewHolder);
        } else {
            //get holder
            viewHolder = (ViewHolder)view.getTag();
        }

        //set info
        viewHolder.nameTV.setText(shoppingLists.get(position).getName());
        viewHolder.dateTV.setText(shoppingLists.get(position).getCreationDate());
        viewHolder.firstProductTV.setText(shoppingLists.get(position).getProducts().get(0).getProductName());
        if (shoppingLists.get(position).getProducts().size() > 1) {
            viewHolder.secondProductTV.setText(shoppingLists.get(position).getProducts().get(1).getProductName());
            if (shoppingLists.get(position).getProducts().size() > 2) {
                viewHolder.thirdProductTV.setText(shoppingLists.get(position).getProducts().get(2).getProductName());
            } else {
                viewHolder.thirdProductTV.setText("");
            }
        } else {
            viewHolder.secondProductTV.setText("");
            viewHolder.thirdProductTV.setText("");
        }

        //Deleting lists
        if (isDeleting) {
            //show delete button
            viewHolder.deleteButton.setVisibility(View.VISIBLE);
            //send position to delete
            viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    operateListener.deleteList(position);
                }
            });
        } else {
            viewHolder.deleteButton.setVisibility(View.GONE);
        }

        //Sending list
        if (isSend) {
            //show delete button
            viewHolder.sendButton.setVisibility(View.VISIBLE);
            //send position to delete
            viewHolder.sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    operateListener.sendList(position);
                }
            });
        } else {
            viewHolder.sendButton.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     * View Holder class
     */
    private class ViewHolder {
        private TextView nameTV;
        private TextView firstProductTV;
        private TextView secondProductTV;
        private TextView thirdProductTV;
        private TextView dateTV;
        private ImageButton deleteButton;
        private ImageButton sendButton;

    }
}
