package com.misapps.oscarruiz.myshopping.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.misapps.oscarruiz.myshopping.R;
import com.misapps.oscarruiz.myshopping.app.controller.DataController;
import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;
import com.misapps.oscarruiz.myshopping.app.models.User;

import java.util.ArrayList;

/**
 * Created by Oscar Ruiz on 24/08/2017.
 */

public class MyContactsAdapter extends BaseAdapter {

    /**
     * Is deleting contact
     */
    private boolean isDeleting;

    /**
     * Delete contact listener
     */
    private AppInterfaces.IDeleteContact deleteListener;

    /**
     * Activity
     */
    private Activity activity;

    /**
     * Viewholder class
     */
    private ViewHolder viewHolder;

    /**
     * Layout inflater
     */
    private LayoutInflater inflater;

    /**
     * User contacts list
     */
    private ArrayList<User> contacts;

    public MyContactsAdapter(Activity activity, ArrayList<User> contacts, boolean isDeleting, AppInterfaces.IDeleteContact deleteListener) {
        this.contacts = contacts;
        this.activity = activity;
        this.isDeleting = isDeleting;
        this.deleteListener = deleteListener;

        //init inflater
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int i) {
        return contacts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            //inflate view
            view = inflater.inflate(R.layout.item_my_contacts_list, viewGroup, false);

            //initialize viewholder
            viewHolder = new ViewHolder();

            //getviews
            viewHolder.profileImageView = (ImageView)view.findViewById(R.id.image_view);
            viewHolder.nickTV = (TextView)view.findViewById(R.id.nick_text_view);
            viewHolder.emailTV = (TextView)view.findViewById(R.id.email_text_view);
            viewHolder.deleteButton = (ImageButton)view.findViewById(R.id.delete_contact_button);

            //set tag
            view.setTag(viewHolder);
        } else {
            //get holder
            viewHolder = (ViewHolder)view.getTag();
        }

        //set info
        viewHolder.nickTV.setText(contacts.get(position).getNick());
        viewHolder.emailTV.setText(contacts.get(position).getEmail());

            DataController dataController = new DataController();
            dataController.downloadProfileImage(activity, contacts.get(position), viewHolder.profileImageView, new AppInterfaces.ISetImge() {
                @Override
                public void setImage() {
                }
            });

        if (isDeleting) {
            viewHolder.deleteButton.setVisibility(View.VISIBLE);
            viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteListener.deleteContact(position);
                }
            });
        }

        return view;
    }

    /**
     * Viewholder class
     */
    private class ViewHolder {
        private ImageView profileImageView;
        private TextView nickTV;
        private TextView emailTV;
        private ImageButton deleteButton;
    }
}
