package com.misapps.oscarruiz.myshopping.app.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.misapps.oscarruiz.myshopping.R;
import com.misapps.oscarruiz.myshopping.app.activities.HomeActivity;
import com.misapps.oscarruiz.myshopping.app.controller.DataController;
import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;
import com.misapps.oscarruiz.myshopping.app.models.User;
import com.misapps.oscarruiz.myshopping.app.session.Session;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class AddContactFragment extends Fragment {

    /**
     * Data controller
     */
    private DataController dataController;

    /**
     * User to add as contact
     */
    private User newContact;

    /**
     * Current user
     */
    private User user;

    /**
     * Search button
     */
    private Button searchButton;

    /**
     * Accept button
     */
    private Button acceptButton;

    /**
     * Nick to search
     */
    private EditText nickToSearchET;

    /**
     * Contact to add
     */
    private TextView contactToAddTV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_contact, container, false);

        getViews(view);

        setInfo();

        setListeners();

        return view;
    }

    /**
     * Method to get views
     */
    private void getViews(View view){
        searchButton = (Button)view.findViewById(R.id.search_button);
        acceptButton = (Button)view.findViewById(R.id.accept_button);
        nickToSearchET = (EditText)view.findViewById(R.id.nick_edit_text);
        contactToAddTV = (TextView)view.findViewById(R.id.contact_nick_text_view);
    }

    /**
     * Method to set listeners
     */
    private void setListeners() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if user has typed a nickname
                if (nickToSearchET.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), getString(R.string.no_user_typed), Toast.LENGTH_SHORT).show();
                } else {
                    if (StringUtils.isAlphanumeric(nickToSearchET.getText().toString())) {
                        //search contact
                        dataController.searchUser(nickToSearchET.getText().toString(), new AppInterfaces.ILoadUser() {
                            @Override
                            public void loadUser(User userLoaded) {
                                if (userLoaded != null) {
                                    //user found
                                    newContact = userLoaded;

                                    //show results text view and button
                                    acceptButton.setVisibility(View.VISIBLE);
                                    contactToAddTV.setVisibility(View.VISIBLE);

                                    //set user found nick name
                                    contactToAddTV.setText(userLoaded.getNick());
                                } else {
                                    contactToAddTV.setVisibility(View.VISIBLE);
                                    contactToAddTV.setText(getString(R.string.user_not_found));
                                }
                            }

                            @Override
                            public void error(DatabaseError error) {

                            }
                        });
                    } else {
                        contactToAddTV.setVisibility(View.VISIBLE);
                        contactToAddTV.setText(getString(R.string.user_not_found));
                    }
                }
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if user already has any contact
                if (user.getContacts() != null) {
                    user.getContacts().add(newContact);
                } else {
                    ArrayList<User> contacts = new ArrayList<>();
                    contacts.add(newContact);
                    //set new contacts list
                    user.setContacts(contacts);

                    //save user in session
                    Session.getInstance().setUser(user);

                    //save user in database
                    dataController.saveUser(user);

                    Toast.makeText(getActivity(), R.string.user_added, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Method to set initial info
     */
    private void setInfo() {
        //hide floating button
        ((HomeActivity)getActivity()).hideFAB();

        //init data controller
        dataController = new DataController();

        //hide results text view and accept button
        acceptButton.setVisibility(View.GONE);
        contactToAddTV.setVisibility(View.GONE);

        //get user from session
        user = Session.getInstance().getUser();
    }
}
