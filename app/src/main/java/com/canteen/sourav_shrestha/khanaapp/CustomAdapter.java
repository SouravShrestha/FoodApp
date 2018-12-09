package com.canteen.sourav_shrestha.khanaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class MyAdapter extends BaseAdapter {

    public ArrayList<String> listNameItem;
    public ArrayList<String> listPriceItem;
    private Context context;

    public MyAdapter(Context context,ArrayList<String> listNameItem,ArrayList<String> listPriceItem) {
        this.listNameItem = listNameItem;
        this.listPriceItem = listPriceItem;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listPriceItem.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.each_list_today,null);
        TextView title = view.findViewById(R.id.textItemToday);
        title.setText(listNameItem.get(i));
        TextView tPrice = view.findViewById(R.id.textItemPrice);
        tPrice.setText("Price : "+listPriceItem.get(i));
        return view;
    }
}
