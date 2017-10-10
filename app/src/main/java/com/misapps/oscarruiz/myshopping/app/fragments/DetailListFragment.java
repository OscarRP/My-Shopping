package com.misapps.oscarruiz.myshopping.app.fragments;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.misapps.oscarruiz.myshopping.R;
import com.misapps.oscarruiz.myshopping.app.activities.HomeActivity;
import com.misapps.oscarruiz.myshopping.app.adapters.DetailedShoppingListAdapter;
import com.misapps.oscarruiz.myshopping.app.controller.DataController;
import com.misapps.oscarruiz.myshopping.app.controller.NavigationController;
import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;
import com.misapps.oscarruiz.myshopping.app.models.Product;
import com.misapps.oscarruiz.myshopping.app.models.ShoppingList;
import com.misapps.oscarruiz.myshopping.app.models.User;
import com.misapps.oscarruiz.myshopping.app.session.Session;
import com.misapps.oscarruiz.myshopping.app.utils.Constants;
import com.misapps.oscarruiz.myshopping.app.utils.Utils;

import org.apache.commons.lang3.text.WordUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DetailListFragment extends Fragment {

    /**
     * Clear text button
     */
    private Button clearTextButton;

    /**
     * Swipe Menu Creator
     */
    private SwipeMenuCreator creator;

    /**
     * Search edit text
     */
    private EditText searchET;

    /**
     * Filtered products
     */
    private ArrayList<Product> filteredProducts;

    /**
     * Utils
     */
    private Utils utils;

    /**
     * Data controller
     */
    private DataController dataController;

    /**
     * Navigation Controller
     */
    private NavigationController navigationController;

    /**
     * User
     */
    private User user;

    /**
     * If its deleting
     */
    private boolean isDeleting;

    /**
     * Accept button
     */
    private Button accept;

    /**
     * Total price Text view
     */
    private TextView totalPriceTextView;

    /**
     * Total price
     */
    private Double totalPrice;

    /**
     * Users shopping lists
     */
    private ArrayList<ShoppingList> shoppingLists;

    /**
     * Shopping lists array position selected
     */
    private int position;

    /**
     * Shopping list selected
     */
    private ArrayList<Product> productList;

    /**
     * Shopping list
     */
    private ShoppingList shoppingList;

    /**
     * Adapter
     */
    private DetailedShoppingListAdapter adapter;

    /**
     * Listview
     */
    private com.baoyz.swipemenulistview.SwipeMenuListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_list, container, false);

        getViews(view);

        setInfo();

        setListeners();

        return view;
    }

    /**
     * Method to get views
     */
    private void getViews(View view) {
        listView = (com.baoyz.swipemenulistview.SwipeMenuListView)view.findViewById(R.id.detail_listview);
        accept = (Button)view.findViewById(R.id.change_quantity_button);
        totalPriceTextView = (TextView)view.findViewById(R.id.total_price_text_view);
        searchET = (EditText)view.findViewById(R.id.search_edit_text);
        clearTextButton = (Button)view.findViewById(R.id.clear_text_button);
    }

    /**
     * Method to set init info
     */
    private void setInfo() {

        //init data and navigation controllers
        dataController = new DataController();
        navigationController = new NavigationController();

        //hide fab
        ((HomeActivity)getActivity()).hideFAB();

        //init utils
        utils = new Utils();

        //position selected, saved in session
        position = Session.getInstance().getPosition();

        //user
        user = Session.getInstance().getUser();

        //users shopping lists
        shoppingLists = Session.getInstance().getUser().getShoppingLists();
        shoppingList = shoppingLists.get(position);

        //selected shopping list
        productList = shoppingLists.get(position).getProducts();

        //Create Swipe Menu
        createSwipeMenu();
        listView.setMenuCreator(creator);

        //Set swipe direction
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);


        //set action bar tittle
        ((HomeActivity)getActivity()).setToolbarTitle(shoppingList.getName());

        totalPriceTextView.setText(utils.calculateTotalPrice(productList));

        adapter = new DetailedShoppingListAdapter(getActivity(), productList, false, new AppInterfaces.IEditItem() {
            @Override
            public void deleteItem(int position) {
            }

            @Override
            public void changeQuantity(Product product, int position) {
                changeProductQuantity(product, position);
            }

            @Override
            public void changePrice(Product product, int position) {
                changeProductPrice(product, position);
            }

            @Override
            public void changeQuantityType(Product product, int productPosition, int spinnerPosition) {
                changeProductQuantityType(product, productPosition, spinnerPosition);
            }
        }, new AppInterfaces.IPickItem() {
            @Override
            public void pickItem(int position) {
                if(productList.get(position).isPicked()) {
                    productList.get(position).setPicked(false);

                } else {
                    //set product picked
                    productList.get(position).setPicked(true);
                }
                //refresh list
                adapter.notifyDataSetChanged();
            }
        });
        listView.setAdapter(adapter);
    }

    /**
     * Method to set listeners
     */
    private void setListeners() {

        clearTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Clear search edit text
                searchET.setText("");
                //hide keyboard
                Utils.hideKeyboard(getActivity());
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDeleting) {
                    //finish deleting
                    isDeleting = false;

                    //hide button
                    accept.setVisibility(View.INVISIBLE);

                    //update user
                    user.setShoppingLists(shoppingLists);
                    dataController.saveUser(user);

                    //new adapter
                    adapter = new DetailedShoppingListAdapter(getActivity(), productList, false, new AppInterfaces.IEditItem() {
                        @Override
                        public void deleteItem(int position) {
                        }

                        @Override
                        public void changeQuantity(Product product, int position) {
                            changeProductQuantity(product, position);
                        }

                        @Override
                        public void changePrice(Product product, int position) {
                            changeProductPrice(product, position);
                        }

                        @Override
                        public void changeQuantityType(Product product, int productPosition, int spinnerPosition) {
                            changeProductQuantityType(product, productPosition, spinnerPosition);
                        }
                    }, new AppInterfaces.IPickItem() {
                        @Override
                        public void pickItem(int position) {
//                            setProductPicked(position);
                        }
                    });
                    listView.setAdapter(adapter);
                }
            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //search coincidences
                String searchText = searchET.getText().toString().toLowerCase();
                filteredProducts = utils.searchProducts(productList, searchText);

                //update listview
                adapter = new DetailedShoppingListAdapter(getContext(), filteredProducts, false, new AppInterfaces.IEditItem() {
                    @Override
                    public void deleteItem(int position) {
                    }

                    @Override
                    public void changeQuantity(Product product, int position) {
                        //send position -1 to know its from filtered product list
                        changeProductQuantity(product, -1);
                    }

                    @Override
                    public void changePrice(Product product, int position) {
                        //send position -1 to know its from filtered product list
                        changeProductPrice(product, -1);
                    }

                    @Override
                    public void changeQuantityType(Product product, int productPosition, int spinnerPosition) {
                        //send position -1 to know its from filtered product list
                        changeProductQuantityType(product, -1, spinnerPosition);
                    }
                }, new AppInterfaces.IPickItem() {
                    @Override
                    public void pickItem(int position) {
                        //send position -1 to know its from filtered product list
                        setProductPicked(-1, position);
                    }
                });
                listView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int productPosition, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        //Pick/UnPick item
                        if (filteredProducts != null) {
                            setProductPicked(-1, productPosition);
                        } else {
                            setProductPicked(productPosition, productPosition);
                        }

                        break;
                    case 1:
                        //Delete item
                        //delete product from list
                        productList.remove(productPosition);

                        //update user
                        user.setShoppingLists(shoppingLists);
                        dataController.saveUser(user);

                        //refresh list
                        adapter.notifyDataSetChanged();

                        //set new total price
                        totalPriceTextView.setText(utils.calculateTotalPrice(productList));
                        break;
                }
                return false;
            }
        });
    }

    /**
     * Create options menu
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem addItem = menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, getResources().getString(R.string.add_item));
//        MenuItem editShoppinList = menu.add(Menu.NONE, Menu.FIRST+1, Menu.NONE, getResources().getString(R.string.delete_item));
        MenuItem deleteShoppingList = menu.add(Menu.NONE, Menu.FIRST+1, Menu.NONE, getResources().getString(R.string.delete_shopping_list));
    }

    /**
     * Method to set menu actions
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 1:
                //add product
                addProduct();
                break;
//            case 2:
//                //Delete product
//                isDeleting = true;
//
//                //show accept button
//                accept.setVisibility(View.VISIBLE);
//
//                //change accept button text
//                accept.setText(getResources().getString(R.string.done));
//
//                adapter = new DetailedShoppingListAdapter(getActivity(), productList, true, new AppInterfaces.IEditItem() {
//                    @Override
//                    public void deleteItem(int position) {
//                        //delete product from list
//                        productList.remove(position);
//
//                        //update user
//                        user.setShoppingLists(shoppingLists);
//                        dataController.saveUser(user);
//
//                        //refresh list
//                        adapter.notifyDataSetChanged();
//
//                        //set new total price
//                        totalPriceTextView.setText(utils.calculateTotalPrice(productList));
//                    }
//
//                    @Override
//                    public void changeQuantity(Product product, int position) {}
//
//                    @Override
//                    public void changePrice(Product product, int position) {}
//
//                    @Override
//                    public void changeQuantityType(Product product, int productPosition, int spinnerPosition) {
//                    }
//
//                }, null);
//                listView.setAdapter(adapter);
//                break;
            case 3:
                //Delete shopping list
                deleteShoppingList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to show dialog to add a product
     */
    private void addProduct() {

        final Dialog dialog = new Dialog(getActivity());


        //dialog content
        dialog.setContentView(R.layout.add_product_dialog);
        dialog.setTitle(getResources().getString(R.string.add_item));

        //Dialog views
        final EditText productName = (EditText)dialog.findViewById(R.id.product_name_dialog_edit_text);
        final EditText productQuantity = (EditText)dialog.findViewById(R.id.product_quantity_dialog_edit_text);
        Button addButton = (Button)dialog.findViewById(R.id.add_button_dialog);
        Button cancelButton = (Button)dialog.findViewById(R.id.cancel_button_dialog);
        final Spinner quantityType = (Spinner)dialog.findViewById(R.id.type_spinner);

        //Listeners
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if product name has been setted
                if (productName.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.need_product_name), Toast.LENGTH_SHORT).show();
                    //add product
                } else if (productQuantity.getText().toString().isEmpty()) {
                    // add with capitalized name
                    productList.add(new Product(WordUtils.capitalize(productName.getText().toString())));
                    //update user
                    user.setShoppingLists(shoppingLists);
                    dataController.saveUser(user);
                } else if (!productQuantity.getText().toString().isEmpty()) {
                    productList.add(new Product(WordUtils.capitalize(productName.getText().toString()), Integer.parseInt(productQuantity.getText().toString()), quantityType.getSelectedItemPosition()));
                    //update user
                    user.setShoppingLists(shoppingLists);
                    dataController.saveUser(user);
                }
                //update list
                adapter.notifyDataSetChanged();
                //close dialog
                dialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Method to delete shopping list
     */
    private void deleteShoppingList() {
        //Dialog to confirm delete
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.dialog_title))
                .setMessage(getResources().getString(R.string.dialog_message))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Se elimina la lista de la compra
                        shoppingLists.remove(position);

                        //update user
                        user.setShoppingLists(shoppingLists);
                        dataController.saveUser(user);

                        //go to home fragment
                        Fragment homeFragement = new HomeFragment();
                        navigationController.changeFragment(getActivity(), homeFragement, null, Constants.HOME_STATES.HOME_STATE);
                    }
                })
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {

        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            totalPriceTextView.requestFocus();
        }
    }

    /**
     * Method to change quantity
     */
    private void changeProductQuantity(Product product, int position) {
        if (position == -1) {
            for (int i=0; i<productList.size();i++) {
                if (productList.get(i).getProductName().equals(product.getProductName())) {
                    position = i;
                }
            }
        }
        //set new quantity
        productList.get(position).setQuantity(product.getQuantity());

        //update user
        user.setShoppingLists(shoppingLists);
        dataController.saveUser(user);

        //set total price
        totalPriceTextView.setText(utils.calculateTotalPrice(productList));
    }

    /**
     * Method to chenge product price
     */
    private void changeProductPrice(Product product, int position) {
        if (position == -1) {
            for (int i=0; i<productList.size();i++) {
                if (productList.get(i).getProductName().equals(product.getProductName())) {
                    position = i;
                }
            }
        }

        //set new price
        productList.get(position).setPrice(product.getPrice());

        //update user
        user.setShoppingLists(shoppingLists);
        dataController.saveUser(user);

        //set total price
        totalPriceTextView.setText(utils.calculateTotalPrice(productList));
    }

    /**
     * Method to change product quantity type
     */
    private void changeProductQuantityType(Product product, int productPosition, int spinnerPosition) {
        if (productPosition == -1) {
            for (int i=0; i<productList.size();i++) {
                if (productList.get(i).getProductName().equals(product.getProductName())) {
                    productPosition = i;
                }
            }
        }

        //set new product quantity type
        productList.get(productPosition).setQuantityType(product.getQuantityType());

        //update user
        user.setShoppingLists(shoppingLists);
        dataController.saveUser(user);
    }

    /**
     * Method to set product as picked
     */
    private void setProductPicked(int productsPosition, int filteredPosition) {
        if (productsPosition == -1) {
            Product product = filteredProducts.get(filteredPosition);
            for (int i=0; i<productList.size();i++) {
                if (productList.get(i).getProductName().equals(product.getProductName())) {
                    productsPosition = i;
                }
            }
        }

        if(productList.get(productsPosition).isPicked()) {
            productList.get(productsPosition).setPicked(false);
        } else {
            //set product picked
            productList.get(productsPosition).setPicked(true);
        }
        //refresh list
        adapter.notifyDataSetChanged();

        //update user
        user.setShoppingLists(shoppingLists);
        dataController.saveUser(user);
    }

    /**
     * Method to create a swipe menu
     */
    private void createSwipeMenu() {
        //Create menu
        creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                //Create open item
                SwipeMenuItem checkItem = new SwipeMenuItem(getActivity().getApplicationContext());
                //Configure open item
                checkItem.setBackground(R.color.background);
                // set item width
                checkItem.setWidth((int)(90*getContext().getResources().getDisplayMetrics().density+0.5f));
                // set item title
                checkItem.setTitle(R.string.pick);
                // set item title fontsize
                checkItem.setTitleSize(12);
                //set item icon
                checkItem.setIcon(R.mipmap.check_icon);
                // set item title font color
                checkItem.setTitleColor(getResources().getColor(R.color.colorAccent));
                // add to menu
                menu.addMenuItem(checkItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity().getApplicationContext());
                // set item background
                deleteItem.setBackground(R.color.colorPrimary);
                // set item width
                deleteItem.setWidth((int)(90*getContext().getResources().getDisplayMetrics().density+0.5f));
                // set item title
                deleteItem.setTitle(R.string.delete_item);
                //set title text size
                deleteItem.setTitleSize(12);
                // set item title font color
                deleteItem.setTitleColor(getResources().getColor(R.color.colorAccent));
                // add to menu
                // set a icon
                deleteItem.setIcon(R.mipmap.delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
    }
}
