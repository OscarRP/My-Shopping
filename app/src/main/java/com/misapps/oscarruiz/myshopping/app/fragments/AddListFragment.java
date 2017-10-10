package com.misapps.oscarruiz.myshopping.app.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.misapps.oscarruiz.myshopping.R;
import com.misapps.oscarruiz.myshopping.app.activities.HomeActivity;
import com.misapps.oscarruiz.myshopping.app.adapters.AddShoppingListAdapter;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class AddListFragment extends Fragment {

    /**
     * Data controller
     */
    private DataController dataController;

    /**
     * Navigation controller
     */
    private NavigationController navigationController;

    /**
     * Utils
     */
    private Utils utils;

    /**
     * Shopping list guardada del usuario
     */
    private ArrayList<ShoppingList> userShoppingList;

    /**
     * Shopping list a añadir
     */
    private ShoppingList shoppingList;

    /**
     * instancia de la sesión acutal
     */
    private Session session;

    /**
     * Usuario
     */
    private User user;

    /**
     * Adapter para el Listview
     */
    private AddShoppingListAdapter adapter;

    /**
     * EditText Nombre del producto
     */
    private EditText productName;

    /**
     * EditText Cantidad del producto
     */
    private EditText productQuantity;

    /**
     * Lista de productos
     */
    private ArrayList<Product> products;

    /**
     * Nombre establecido de la lista
     */
    private TextInputEditText listName;

    /**
     * Fecha de creación de la lista
     */
    private TextView listDate;

    /**
     * Quantity type spinner
     */
    private Spinner typeSpinner;

    /**
     * Botón añadir producto
     */
    private Button addButton;

    /**
     * ListView
     */
    private ListView listView;

    /**
     * Botón guardar lista
     */
    private Button saveButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_list, container, false);

        getViews(view);
        setInfo();
        setListeners();

        return view;
    }

    /**
     * Method to get views
     */
    private void getViews(View view) {
        listName = (TextInputEditText) view.findViewById(R.id.list_name_text_view);
        listDate = (TextView)view.findViewById(R.id.list_date_text_view);
        addButton = (Button)view.findViewById(R.id.add_button);
        listView = (ListView)view.findViewById(R.id.list_view);
        saveButton = (Button)view.findViewById(R.id.save_button);
        productName = (EditText)view.findViewById(R.id.product_name_edit_text);
        productQuantity = (EditText)view.findViewById(R.id.product_quantity_edit_text);
        typeSpinner = (Spinner)view.findViewById(R.id.type_spinner);
    }

    /**
     * Method to set initial info
     */
    private void setInfo() {
        //set options menu
        setHasOptionsMenu(true);

        //hide floating action button
        ((HomeActivity)getActivity()).hideFAB();

        //init navigation controller
        navigationController = new NavigationController();

        //init data controller
        dataController = new DataController();

        //set toolbar title
        ((HomeActivity)getActivity()).setToolbarTitle(getString(R.string.add_list_tittle));

        //utils instance
        utils = new Utils();

        //init products
        products = new ArrayList<>();

        //set list date
        listDate.setText(utils.getDate());
    }

    /**
     * Method to set listeners
     */
    private void setListeners() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check name has been typed
                if (productName.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.need_product_name), Toast.LENGTH_SHORT).show();

                } else {
                    //capitalize product name
                    String capitalizedName = WordUtils.capitalize(productName.getText().toString());

                    if (!productQuantity.getText().toString().isEmpty()) {
                        //add product
                        products.add(new Product(capitalizedName, Integer.parseInt(productQuantity.getText().toString()), typeSpinner.getSelectedItemPosition()));

                        //set adapter and refresh list
                        adapter = new AddShoppingListAdapter(getActivity(), products, false, null);
                        listView.setAdapter(adapter);
                    } else {
                        //add producto with quantity 0
                        products.add(new Product(capitalizedName, 0, typeSpinner.getSelectedItemPosition()));

                        //set adapter and refresh list
                        adapter = new AddShoppingListAdapter(getActivity(), products, false, null);
                        listView.setAdapter(adapter);
                    }
                    //clear fields
                    productName.setText("");
                    productName.requestFocus();
                    productQuantity.setText("");
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check any product has been added
                if (products.size() == 0) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.add_product_needed), Toast.LENGTH_SHORT).show();
                } else {
                    if (listName.getText().toString().isEmpty()) {
                        //create shopping list
                        shoppingList = new ShoppingList(products, getString(R.string.default_name), listDate.getText().toString());
                    } else {
                        //Capitalize list name
                        String capitalizedListName = WordUtils.capitalize(listName.getText().toString());

                        //sort product list by name
                        Collections.sort(products, new Comparator<Product>() {
                            @Override
                            public int compare(Product product1, Product product2) {
                                return product1.getProductName().compareToIgnoreCase(product2.getProductName());
                            }
                        });

                        //create shopping list
                        shoppingList = new ShoppingList(products, capitalizedListName, listDate.getText().toString());
                    }
                    //session instance and get user session
                    session = new Session().getInstance();
                    user = session.getUser();

                    if (session.getUser().getShoppingLists() == null) {
                        //init shopping lists
                        userShoppingList = new ArrayList<>();
                    } else {
                        //get shopping lists
                        userShoppingList = user.getShoppingLists();
                    }
                    userShoppingList.add(shoppingList);
                    user.setShoppingLists(userShoppingList);

                    //save user in firebase
                    dataController.saveUser(user);

                    Toast.makeText(getActivity(), getString(R.string.list_saved), Toast.LENGTH_LONG).show();

                    //hide keyboard
                    utils.hideKeyboard(getActivity());

                    //go to home fragment
                    Fragment homeFragment = new HomeFragment();
                    navigationController.changeFragment(getActivity(), homeFragment, null, Constants.HOME_STATES.HOME_STATE);
                }
            }
        });
    }

    /**
     * Method to create Options Menu
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.add_shopping_list, menu);
    }

    /**
     * Method to set menu actions
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_list:
                //clear product list
                products = new ArrayList<>();
                adapter = new AddShoppingListAdapter(getActivity(), products, false, null);
                listView.setAdapter(adapter);
                break;
            case R.id.delete_product:
                //delete product
                adapter = new AddShoppingListAdapter(getActivity(), products, true, new AppInterfaces.IEditItem() {
                    @Override
                    public void deleteItem(int position) {
                        //delete product from list
                        products.remove(position);

                        adapter = new AddShoppingListAdapter(getActivity(), products, false, null);
                        listView.setAdapter(adapter);
                    }
                    @Override
                    public void changeQuantity(Product product, int position) {}
                    @Override
                    public void changePrice(Product product, int position) {}

                    @Override
                    public void changeQuantityType(Product product, int productPosition, int spinnerPosition) {
                    }
                });
                listView.setAdapter(adapter);

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
