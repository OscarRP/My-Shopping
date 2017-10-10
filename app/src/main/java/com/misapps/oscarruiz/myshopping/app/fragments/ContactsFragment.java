package com.misapps.oscarruiz.myshopping.app.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.misapps.oscarruiz.myshopping.R;
import com.misapps.oscarruiz.myshopping.app.activities.HomeActivity;
import com.misapps.oscarruiz.myshopping.app.adapters.MyContactsAdapter;
import com.misapps.oscarruiz.myshopping.app.controller.DataController;
import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;
import com.misapps.oscarruiz.myshopping.app.models.ShoppingList;
import com.misapps.oscarruiz.myshopping.app.models.User;
import com.misapps.oscarruiz.myshopping.app.session.Session;
import com.misapps.oscarruiz.myshopping.app.utils.Constants;
import com.misapps.oscarruiz.myshopping.app.utils.Utils;

import java.util.ArrayList;


public class ContactsFragment extends Fragment {

    /**
     * To know if is deleting contact
     */
    private boolean isDeleting;

    /**
     * Data controller
     */
    private DataController dataController;

    /**
     * Utils instance
     */
    private Utils utils;

    /**
     * User to send shopping list
     */
    private User userToSend;

    /**
     * Shopping list to send
     */
    private ShoppingList shoppingListToSend;

    /**
     * Shopping list position to send
     */
    private int listPosition;

    /**
     * boolean is to choose a contact to send list
     */
    private boolean isSending;

    /**
     * No contacts container
     */
    private LinearLayout noContactsLayout;

    /**
     * Contacts adapter
     */
    private MyContactsAdapter adapter;

    /**
     * User contacts list
     */
    private ArrayList<User> contacts;

    /**
     * User
     */
    private User user;

    /**
     * Contacts list view
     */
    private ListView listview;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        getViews(view);
        getInfo();
        setListeners();

        return view;
    }

    /**
     * Method to get views
     */
    private void getViews(View view) {
        listview = (ListView)view.findViewById(R.id.list_view);
        noContactsLayout = (LinearLayout)view.findViewById(R.id.no_contacts_container);
    }

    /**
     * Method to set initial info
     */
    private void getInfo() {

        //init data controller
        dataController = new DataController();

        //init utils
        utils = new Utils();

        //get user from session
        user = Session.getInstance().getUser();

        //check if fragment is to send a shopping list
        if (getArguments() != null) {
            isDeleting = getArguments().getBoolean(Constants.DELETING);
            isSending = getArguments().getBoolean(Constants.SEND);
            //get shopping list position to send
            listPosition = getArguments().getInt(Constants.ITEM);
            shoppingListToSend = user.getShoppingLists().get(listPosition);

        } else {
            isSending = false;
            isDeleting = false;
        }

        if (isSending) {
            //hide floating action button
            ((HomeActivity)getActivity()).hideFAB();
        }

        //get contact list from user
        if (user.getContacts() != null) {
            contacts = user.getContacts();
            //hide no contacts layout
            noContactsLayout.setVisibility(View.GONE);

            if (isDeleting) {
                //hide fab
                ((HomeActivity)getActivity()).hideFAB();

                //show contacts with deleting button
                adapter = new MyContactsAdapter(getActivity(), contacts, true, new AppInterfaces.IDeleteContact() {
                    @Override
                    public void deleteContact(int position) {
                        //delete contact
                        contacts.remove(position);

                        //save contacts
                        user.setContacts(contacts);
                        Session.getInstance().setUser(user);

                        //save user
                        dataController.saveUser(user);

                        //reload contacts list
                        adapter.notifyDataSetChanged();
                    }
                });
                listview.setAdapter(adapter);
            } else {
                //show contacts list
                adapter = new MyContactsAdapter(getActivity(), contacts, false, null);
                listview.setAdapter(adapter);
            }
        } else {
            //user has no contacts
            //show no contacts layout
            noContactsLayout.setVisibility(View.VISIBLE);
            //show fab
            ((HomeActivity)getActivity()).showFAB();
        }
    }

    /**
     * Method to set listeners
     */
    private void setListeners() {
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //get selected user data from databasa
                dataController.searchUser(contacts.get(position).getNick(), new AppInterfaces.ILoadUser() {
                    @Override
                    public void loadUser(User userLoaded) {
                        userToSend = userLoaded;
                        //check if user to send has any shopping list
                        if (userToSend.getShoppingLists() != null) {
                            //check if list to send already exist in user to send
                            int userToSendListPosition = utils.userHasShoppingList(userToSend, shoppingListToSend.getName());
                            if (userToSendListPosition > -1) {
                                //Merge both shopping lists
                                ShoppingList finalShoppingList = utils.mergeShoppingLists(user.getShoppingLists().get(listPosition), userToSend.getShoppingLists().get(userToSendListPosition));

                                //replace userToSend list by finalShoppingLists
                                userToSend.getShoppingLists().set(userToSendListPosition, finalShoppingList);

                                Toast.makeText(getActivity(), R.string.list_send, Toast.LENGTH_SHORT).show();
                            } else {
                                //Shopping list new to userToSend
                                userToSend.getShoppingLists().add(shoppingListToSend);
                                Toast.makeText(getActivity(), R.string.list_send, Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            //User hasnt any shopping list
                            // create Array list
                            ArrayList<ShoppingList> shoppingLists = new ArrayList<>();
                            shoppingLists.add(shoppingListToSend);
                            userToSend.setShoppingLists(shoppingLists);
                        }
                        //save userToSend
                        dataController.saveUser(userToSend);
                    }

                    @Override
                    public void error(DatabaseError error) {

                    }
                });

            }
        });
    }
}
