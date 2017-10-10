package com.misapps.oscarruiz.myshopping.app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.misapps.oscarruiz.myshopping.R;
import com.misapps.oscarruiz.myshopping.app.activities.HomeActivity;
import com.misapps.oscarruiz.myshopping.app.adapters.HomeShopingListsAdapter;
import com.misapps.oscarruiz.myshopping.app.controller.DataController;
import com.misapps.oscarruiz.myshopping.app.controller.NavigationController;
import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;
import com.misapps.oscarruiz.myshopping.app.models.Product;
import com.misapps.oscarruiz.myshopping.app.models.ShoppingList;
import com.misapps.oscarruiz.myshopping.app.models.User;
import com.misapps.oscarruiz.myshopping.app.session.Session;
import com.misapps.oscarruiz.myshopping.app.utils.Constants;
import com.misapps.oscarruiz.myshopping.app.utils.Utils;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    /**
     * Shopping list to send
     */
    private ShoppingList listToSend;

    /**
     * User to send list
     */
    private User userToSend;

    /**
     * Done button
     */
    private Button doneButton;

    /**
     * Toolbar
     */
    private Toolbar toolbar;

    /**
     * Indicates if is send list
     */
    private boolean isSend;

    /**
     * Indicates if is Deleting
     */
    private boolean isDeleting;

    /**
     * Data controller
     */
    private DataController dataController;

    /**
     * Navigation controller
     */
    private NavigationController navigationController;

    /**
     * Session instance
     */
    private Session session;

    /**
     * Curretn user
     */
    private User user;

    /**
     * Users shopping lists
     */
    private ArrayList<ShoppingList> shoppingLists;

    /**
     * No lists layout
     */
    private LinearLayout noListsLayout;

    /**
     * Shopping lists listview
     */
    private ListView listView;

    /**
     * Listview adapter
     */
    private ListAdapter adapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //firebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();
        //firebase database instance
        database = FirebaseDatabase.getInstance();

        getViews(view);
        setInfo();
        setListeners();

        return view;
    }

    /**
     * Method to get views
     */
    private void getViews(View view) {
        noListsLayout = (LinearLayout)view.findViewById(R.id.no_lists_container);
        listView =(ListView)view.findViewById(R.id.list_view);
        toolbar = (Toolbar)view.findViewById(R.id.toolbar);
        doneButton = (Button)view.findViewById(R.id.done_button);
    }

    /**
     * Method to set initial info
     */
    private void setInfo() {

        //init data controller
        dataController = new DataController();

        //init navigation controller
        navigationController = new NavigationController();

        //init shoppingLists
        shoppingLists = new ArrayList<>();

        //get session and user
        user = Session.getInstance().getUser();

        //set toolbar title
        ((HomeActivity)getActivity()).setToolbarTitle(getString(R.string.home_title));

        if (user != null) {
            if (user.getShoppingLists() != null) {
                //get user shopping lists
                shoppingLists = user.getShoppingLists();
                //hide no list layout
                noListsLayout.setVisibility(View.GONE);
            } else {
                //show no list layout
                noListsLayout.setVisibility(View.VISIBLE);
            }
        }

        //check params to know if its deleting
        if (getArguments() != null) {
            //if is deleting
            if (getArguments().getBoolean(Constants.DELETING)) {
                //show toolbar
                toolbar.setVisibility(View.VISIBLE);
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                //hide floating action button
                ((HomeActivity)getActivity()).hideFAB();

                //Hide Action bar
                ((HomeActivity) getActivity()).getSupportActionBar().hide();

                isDeleting = getArguments().getBoolean(Constants.DELETING);
                isSend = false;

                //init adapter
                adapter = new HomeShopingListsAdapter(getActivity(), shoppingLists, isDeleting, isSend, new AppInterfaces.IOperateList() {
                    @Override
                    public void deleteList(int positiom) {
                        //remove list from array
                        shoppingLists.remove(positiom);
                        //reload list
                        ((BaseAdapter)adapter).notifyDataSetChanged();
                        //save lists in user
                        user.setShoppingLists(shoppingLists);
                        //save user in firebas
                        dataController.saveUser(user);
                    }

                    @Override
                    public void sendList(int position) {
                    }
                });
                listView.setAdapter(adapter);

                //if is send list
            } else if (getArguments().getBoolean(Constants.SEND)) {

                //show toolbar
                toolbar.setVisibility(View.VISIBLE);
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                //hide floating action button
                ((HomeActivity)getActivity()).hideFAB();

                //Hide Action bar
                ((HomeActivity) getActivity()).getSupportActionBar().hide();

                isSend = true;
                isDeleting = false;

                //init adapter
                adapter = new HomeShopingListsAdapter(getActivity(), shoppingLists, isDeleting, isSend, new AppInterfaces.IOperateList() {
                    @Override
                    public void deleteList(int positiom) {
                    }

                    @Override
                    public void sendList(int position) {
                        listToSend = shoppingLists.get(position);

                        //go to contacts fragment with list to send and boolean is sending and shopping list position
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Constants.SEND, true);
                        bundle.putInt(Constants.ITEM, position);

                        ContactsFragment contactsFragment = new ContactsFragment();
                        navigationController.changeFragment(getActivity(), contactsFragment, bundle, Constants.HOME_STATES.MY_CONTACTS_STATE);
                    }
                });
                listView.setAdapter(adapter);
            }

        } else {
            //show floating action button
            ((HomeActivity)getActivity()).showFAB();

            //show Action bar
            ((HomeActivity) getActivity()).getSupportActionBar().show();

            isDeleting = false;
            isSend = false;

            //set options menu
            setHasOptionsMenu(true);
            adapter = new HomeShopingListsAdapter(getActivity(), shoppingLists, isDeleting, isSend, null);
            listView.setAdapter(adapter);
        }
    }

    /**
     * Method to set listeners
     */
    private void setListeners() {
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load fragment with deleting false
                Fragment homeFragment = new HomeFragment();
                navigationController.changeFragment(getActivity(), homeFragment, null, Constants.HOME_STATES.HOME_STATE);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                session.getInstance().setPosition(position);
                //change to detail fragment
                Fragment detailFragment = new DetailListFragment();
                navigationController.changeFragment(getActivity(), detailFragment, null, Constants.HOME_STATES.DETAIL_SHOPPING_LIST_STATE);
            }
        });
    }

    /**
     * Method to create options menu
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu
        getActivity().getMenuInflater().inflate(R.menu.home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.close_session) {
            Utils utils = new Utils();
            utils.closeSession(getActivity());
        }

        return super.onOptionsItemSelected(item);
    }
}
