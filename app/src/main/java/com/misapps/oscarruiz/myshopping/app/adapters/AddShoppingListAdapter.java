package com.misapps.oscarruiz.myshopping.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.misapps.oscarruiz.myshopping.R;
import com.misapps.oscarruiz.myshopping.app.interfaces.AppInterfaces;
import com.misapps.oscarruiz.myshopping.app.models.Product;

import java.util.ArrayList;

/**
 * Created by Oscar Ruiz on 17/08/2017.
 */

public class AddShoppingListAdapter extends BaseAdapter {
    /**
     * listener interface IDeleteItem
     */
    private AppInterfaces.IEditItem listener;

    /**
     * Booleano para saber si se ha pulsado el botón borrar elemento
     */
    private boolean isDeleting;

    /**
     * Botón de borrar item
     */
    private Button deleteButton;

    /**
     * Nombre del producto
     */
    private TextView productName;

    /**
     * Cantidad del prodcuto
     */
    private TextView productQuantity;

    /**
     * Contexto
     */
    private Context context;

    /**
     * Lista de productos
     */
    private ArrayList<Product> products;

    /**
     * ViewHolder para reutilizar las celdas
     */
    private ViewHolder viewHolder;

    /**
     * Layout Inflater
     */
    private LayoutInflater inflater;

    public AddShoppingListAdapter (Context context, ArrayList<Product> products, boolean isDeleting, AppInterfaces.IEditItem listener) {
        this.context = context;
        this.products = products;
        this.isDeleting = isDeleting;
        this.listener = listener;

        //inicializar inflater
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            // se infla la vista por primera vez
            convertView = inflater.inflate(R.layout.item_add_shopping_list, parent, false);

            //Se inicializa instancia de viewHolder
            viewHolder = new ViewHolder();

            //se establecen las vistas
            viewHolder.productName = (TextView)convertView.findViewById(R.id.product_name_text_view);
            viewHolder.productQuantity = (TextView)convertView.findViewById(R.id.quantity_text_view);
            viewHolder.deleteButton = (Button)convertView.findViewById(R.id.delete_button);

            //establecer tag
            convertView.setTag(viewHolder);
        } else {
            //get holder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //set info
        viewHolder.productName.setText(products.get(position).getProductName());
        if (products.get(position).getQuantity() == 0) {
            viewHolder.productQuantity.setText("");
        } else {
            viewHolder.productQuantity.setText(String.valueOf(products.get(position).getQuantity()));
        }

        //si se ha pulsado el botón de borrar elemento
        if (isDeleting) {
            //se muestra el botón eliminar producto
            viewHolder.deleteButton.setVisibility(View.VISIBLE);
            viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //se elimina el producto
                    //products.remove(position);
                    listener.deleteItem(position);

                }
            });
        }

        return convertView;
    }

    /**
     * Clase para guardar los datos y reutilizar celdas
     */
    private class ViewHolder {
        private TextView productName;
        private TextView productQuantity;
        private Button deleteButton;
    }
}
