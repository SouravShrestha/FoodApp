package com.canteen.sourav_shrestha.khanaapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.github.ag.floatingactionmenu.OptionsFabLayout;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class FragmentTomorrow extends Fragment {

    public ArrayList<String> listNameLunch;
    public ArrayList<String> listIdLunch;
    public ArrayList<String> listPriceLunch;
    public ArrayList<String> listNameDinner;
    public ArrayList<String> listIdDinner;
    public ArrayList<String> listPriceDinner;
    public ArrayList<String> listCheckItems;
    public ArrayList<String> listCheckItemsID;
    public ArrayList<String> listCheckItemsPrice;
    OptionsFabLayout floatingActionButton;
    MyAdapterLunch adaperLunch;
    MyAdapterDinner adapterDinner;
    ListView listTomorrowLunch;
    ListView listTomorrowDinner;
    static final String queryTomorrow = "SELECT product_name,product_price,p.product_id,t.lunch,t.dinner FROM tomorrow_meal t,table_product p WHERE " +
            "p.product_id = t.product_id;";
    static final String queryDeleteTomorrow = "DELETE FROM tomorrow_meal where product_id ='";
    static final String queryTomorrowCheck = "SELECT product_name,product_id,product_price FROM table_product;";
    static final String queryAddItems = "INSERT INTO tomorrow_meal VALUES('";
    static final String queryUpdateItems = "UPDATE tomorrow_meal SET dinner=1,lunch=1 WHERE product_id='";
    static final String queryUpdateItemSingle = "UPDATE tomorrow_meal SET dinner='";

    public FragmentTomorrow(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_tomorrow, container, false);
        listNameLunch = new ArrayList<>();
        listPriceLunch = new ArrayList<>();
        listNameDinner = new ArrayList<>();
        listPriceDinner = new ArrayList<>();
        listIdDinner = new ArrayList<>();
        listCheckItems = new ArrayList<>();
        listCheckItemsID = new ArrayList<>();
        listCheckItemsPrice = new ArrayList<>();
        listIdLunch = new ArrayList<>();
        listIdLunch = new ArrayList<>();
        Log.d("timeItem","OnCreate");
        new FragmentTomorrow.TomorrowMenu().execute();
        floatingActionButton = view.findViewById(R.id.fab_lTomorrow);
        listTomorrowLunch = view.findViewById(R.id.list_tomorrow_menu);
        listTomorrowDinner = view.findViewById(R.id.list_dinner_tomorrow);
        adaperLunch = new MyAdapterLunch(getContext(), listNameLunch, listPriceLunch);
        adapterDinner = new MyAdapterDinner(getContext(),listNameDinner,listPriceDinner);
        listTomorrowDinner.setAdapter(adapterDinner);
        listTomorrowLunch.setAdapter(adaperLunch);
        floatingActionButton.setMiniFabsColors(R.color.yellowSun,R.color.colorPrimary);

        floatingActionButton.setMainFabOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionButton.closeOptionsMenu();
            }
        });

        floatingActionButton.setMiniFabSelectedListener(new OptionsFabLayout.OnMiniFabSelectedListener() {
            @Override
            public void onMiniFabSelected(MenuItem fabItem) {
                switch (fabItem.getItemId()) {
                    case R.id.lunchAdd:
                        listCheckItems.clear();
                        new TomorrowAddCheckLunch().execute();
                        new LovelyChoiceDialog(getContext(), R.style.TintTheme)
                                .setTopColorRes(R.color.colorPrimary)
                                .setTitle("ADD LUNCH ITEMS")
                                .setItemsMultiChoice(listCheckItems, new LovelyChoiceDialog.OnItemsSelectedListener<String>() {
                                    @Override
                                    public void onItemsSelected(List<Integer> positions, List<String> items) {
                                        for(String item : items){
                                            String idC = listCheckItemsID.get(listCheckItems.indexOf(item));
                                            new TomorrowMenuAdd().execute(idC,"1 0",item+"&"+listCheckItemsPrice.get(listCheckItems.indexOf(item)));
                                            floatingActionButton.closeOptionsMenu();
                                            adaperLunch.notifyDataSetChanged();
                                        }
                                    }
                                })
                                .setConfirmButtonText("ADD")
                                .show();
                        break;
                    case R.id.dinnnerAdd:
                        listCheckItems.clear();
                        new TomorrowAddCheckDinner().execute();
                        new LovelyChoiceDialog(getContext(), R.style.TintTheme)
                                .setTopColorRes(R.color.colorPrimary)
                                .setTitle("ADD DINNER ITEMS")
                                .setItemsMultiChoice(listCheckItems, new LovelyChoiceDialog.OnItemsSelectedListener<String>() {
                                    @Override
                                    public void onItemsSelected(List<Integer> positions, List<String> items) {
                                        for(String item : items){
                                            String idC = listCheckItemsID.get(listCheckItems.indexOf(item));
                                            new TomorrowMenuAdd().execute(idC,"0 1",item+"&"+listCheckItemsPrice.get(listCheckItems.indexOf(item)));
                                            floatingActionButton.closeOptionsMenu();
                                            adapterDinner.notifyDataSetChanged();
                                        }
                                    }
                                })
                                .setConfirmButtonText("ADD")
                                .show();
                        break;
                    default:
                        break;
                }
            }
        });

        return view;
    }

    class TomorrowMenu extends AsyncTask {

        String sb="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String link="http://172.29.5.23/collegecanteen/khana.php";
            try {
                URL url = new URL(link);
                String data  = URLEncoder.encode("khana", "UTF-8")
                        + "=" + URLEncoder.encode(queryTomorrow, "UTF-8");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                OutputStream os = conn.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os));
                bufferedWriter.write(data);
                bufferedWriter.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                while((line = reader.readLine()) != null) {
                    sb = sb + line;
                }
                String rowsToday[] = sb.split("&");
                for(String x:rowsToday) {
                    String columnPerRow[] = x.split(",");
                    int isDinner = Integer.parseInt(columnPerRow[4]);
                    int isLunch = Integer.parseInt(columnPerRow[3]);
                    if(isLunch == 1) {
                        listNameLunch.add(columnPerRow[0]);
                        listPriceLunch.add(columnPerRow[1]);
                        listIdLunch.add(columnPerRow[2]);
                        Log.d("dataQuery","lunch"+columnPerRow[0]+columnPerRow[2]);
                    }
                    if(isDinner==1){
                        listNameDinner.add(columnPerRow[0]);
                        listPriceDinner.add(columnPerRow[1]);
                        listIdDinner.add(columnPerRow[2]);
                        Log.d("dataQuery","dinner"+columnPerRow[0]+columnPerRow[2]);
                    }
                }
                return sb;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            adaperLunch.notifyDataSetChanged();
            super.onPostExecute(o);
        }
    }


    class TomorrowMenuDelete extends AsyncTask<String, String, String> {

        String sb="";
        String id,lunch,dinner;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String link="http://172.29.5.23/collegecanteen/khana.php";
            try {
                id = strings[0];
                lunch = strings[1];
                dinner = strings[2];
                URL url = new URL(link);
                String data;
                if(listIdDinner.contains(id) || listIdLunch.contains(id))
                    data = URLEncoder.encode("khana", "UTF-8")
                            + "=" + URLEncoder.encode(queryUpdateItemSingle+dinner+"',lunch='"+lunch+"'WHERE product_id='"+id+"';", "UTF-8");
                else
                    data  = URLEncoder.encode("khana", "UTF-8")
                            + "=" + URLEncoder.encode(queryDeleteTomorrow+id+"';", "UTF-8");HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                OutputStream os = conn.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os));
                bufferedWriter.write(data);
                bufferedWriter.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                while((line = reader.readLine()) != null) {
                    sb = sb + line;
                }
                return sb;

            } catch (IOException e) {
                Toast.makeText(getContext(),e.toString(),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String o) {
            adaperLunch.notifyDataSetChanged();
            super.onPostExecute(o);
        }
    }


    class TomorrowAddCheckLunch extends AsyncTask<String, String, String> {

        String sb="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String link="http://172.29.5.23/collegecanteen/khana.php";
            try {
                URL url = new URL(link);
                String data  = URLEncoder.encode("khana", "UTF-8")
                        + "=" + URLEncoder.encode(queryTomorrowCheck, "UTF-8");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                OutputStream os = conn.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os));
                bufferedWriter.write(data);
                bufferedWriter.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                while((line = reader.readLine()) != null) {
                    sb = sb + line;
                }
                String rowsToday[] = sb.split("&");
                for(String x:rowsToday) {
                    String columnPerRow[] = x.split(",");
                    if(!listNameLunch.contains(columnPerRow[0])) {
                        listCheckItems.add(columnPerRow[0]);
                        listCheckItemsID.add(columnPerRow[1]);
                        listCheckItemsPrice.add(columnPerRow[2]);
                    }
                }
                return sb;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String o) {
            for(int i = 0; i< listNameLunch.size(); i++)
                Log.d("hello", listNameLunch.get(i)+","+ listPriceLunch.get(i));
            adaperLunch.notifyDataSetChanged();
            super.onPostExecute(o);
        }
    }


    class TomorrowAddCheckDinner extends AsyncTask<String, String, String> {

        String sb="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String link="http://172.29.5.23/collegecanteen/khana.php";
            try {
                URL url = new URL(link);
                String data  = URLEncoder.encode("khana", "UTF-8")
                        + "=" + URLEncoder.encode(queryTomorrowCheck, "UTF-8");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                OutputStream os = conn.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os));
                bufferedWriter.write(data);
                bufferedWriter.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                while((line = reader.readLine()) != null) {
                    sb = sb + line;
                }
                String rowsToday[] = sb.split("&");
                for(String x:rowsToday) {
                    String columnPerRow[] = x.split(",");
                    if(!listNameDinner.contains(columnPerRow[0])) {
                        listCheckItems.add(columnPerRow[0]);
                        listCheckItemsID.add(columnPerRow[1]);
                        listCheckItemsPrice.add(columnPerRow[2]);
                    }
                }
                return sb;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String o) {
            adaperLunch.notifyDataSetChanged();
            super.onPostExecute(o);
        }
    }

    class TomorrowMenuAdd extends AsyncTask<String, String, String> {

        String sb="";
        String id,lunch,dinner;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String link="http://172.29.5.23/collegecanteen/khana.php";
            try {
                id = strings[0];
                Log.d("dataQuery","id"+id);
                String lD[] = strings[1].split(" ");
                lunch = lD[0];
                dinner = lD[1];
                Log.d("errP",lunch+dinner);
                String namePrice[] = strings[2].split("&");
                String name = namePrice[0];
                String price = namePrice[1];
                URL url = new URL(link);
                String data;
                if(listIdDinner.contains(id) || listIdLunch.contains(id))
                    data = URLEncoder.encode("khana", "UTF-8")
                            + "=" + URLEncoder.encode(queryUpdateItems+id+"';", "UTF-8");
                else
                    data = URLEncoder.encode("khana", "UTF-8")
                            + "=" + URLEncoder.encode(queryAddItems + id + "','" + dinner + "','" + lunch + "');", "UTF-8");

                if(lunch.equals("1")){
                    listNameLunch.add(name);
                    listIdLunch.add(id);
                    listPriceLunch.add(price);
                }else{
                    listNameDinner.add(name);
                    listIdDinner.add(id);
                    listPriceDinner.add(price);
                }
                Log.d("dataQuery",data);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                OutputStream os = conn.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os));
                bufferedWriter.write(data);
                bufferedWriter.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                while((line = reader.readLine()) != null) {
                    sb = sb + line;
                }
                return sb;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String o) {
            adaperLunch.notifyDataSetChanged();
            super.onPostExecute(o);
        }
    }

    class MyAdapterLunch extends BaseAdapter {

        public ArrayList<String> listNameItem;
        public ArrayList<String> listPriceItem;
        private Context context;

        public MyAdapterLunch(Context context, ArrayList<String> listNameItem, ArrayList<String> listPriceItem) {
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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.each_list_layout,null);
            final TextView title = view.findViewById(R.id.textItem);
            title.setText(listNameItem.get(i));
            final TextView tPrice = view.findViewById(R.id.textItemPrice);
            tPrice.setText(listPriceItem.get(i));
            ImageView imageItem = view.findViewById(R.id.imgItem);
            imageItem.setBackgroundResource(R.drawable.vegetarian_symbol);

            BootstrapButton btn_edit = view.findViewById(R.id.bEdit);
            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new LovelyTextInputDialog(getContext(), R.style.TintTheme)
                            .setTopColorRes(R.color.colorPrimary)
                            .setTitle("Edit Item Price")
                            .setMessage(title.getText()).setInputType(2).setHint(tPrice.getText().toString())
                            .setInputFilter("Invalid Input", new LovelyTextInputDialog.TextFilter() {
                                @Override
                                public boolean check(String text) {
                                    return text.matches("\\w+");
                                }
                            })
                            .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                                @Override
                                public void onTextInputConfirmed(String text) {
                                    Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                }
            });


            BootstrapButton btn_del = view.findViewById(R.id.bRemove);
            btn_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new LovelyStandardDialog(getContext(), LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                            .setTopColorRes(R.color.bootstrap_brand_danger)
                            .setButtonsColorRes(R.color.colorIndicator)
                            .setTitle("DELETE "+title.getText())
                            .setMessage("Do You Really Want To Remove "+title.getText()+" From Tomorrow's Menu ?")
                            .setPositiveButton("Yes", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    Toast.makeText(context, "Positive Clicked", Toast.LENGTH_SHORT).show();
                                    new TomorrowMenuDelete().execute(listIdLunch.get(i),"0","1");
                                    listIdLunch.remove(i);
                                    listNameItem.remove(i);
                                    listPriceItem.remove(i);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });

            return view;
        }
    }


    class MyAdapterDinner extends BaseAdapter {

        public ArrayList<String> listNameItem;
        public ArrayList<String> listPriceItem;
        private Context context;

        public MyAdapterDinner(Context context, ArrayList<String> listNameItem, ArrayList<String> listPriceItem) {
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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.each_list_layout,null);
            final TextView title = view.findViewById(R.id.textItem);
            title.setText(listNameItem.get(i));
            final TextView tPrice = view.findViewById(R.id.textItemPrice);
            tPrice.setText(listPriceItem.get(i));
            ImageView imageItem = view.findViewById(R.id.imgItem);
            imageItem.setBackgroundResource(R.drawable.vegetarian_symbol);

            BootstrapButton btn_edit = view.findViewById(R.id.bEdit);
            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new LovelyTextInputDialog(getContext(), R.style.TintTheme)
                            .setTopColorRes(R.color.colorPrimary)
                            .setTitle("Edit Item Price")
                            .setMessage(title.getText()).setInputType(2).setHint(tPrice.getText().toString())
                            .setInputFilter("Invalid Input", new LovelyTextInputDialog.TextFilter() {
                                @Override
                                public boolean check(String text) {
                                    return text.matches("\\w+");
                                }
                            })
                            .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                                @Override
                                public void onTextInputConfirmed(String text) {
                                    Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                }
            });


            BootstrapButton btn_del = view.findViewById(R.id.bRemove);
            btn_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new LovelyStandardDialog(getContext(), LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                            .setTopColorRes(R.color.bootstrap_brand_danger)
                            .setButtonsColorRes(R.color.colorIndicator)
                            .setTitle("DELETE "+title.getText())
                            .setMessage("Do You Really Want To Remove "+title.getText()+" From Tomorrow's Menu ?")
                            .setPositiveButton("Yes", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    Toast.makeText(context, "Positive Clicked", Toast.LENGTH_SHORT).show();
                                    new TomorrowMenuDelete().execute(listIdDinner.get(i),"1","0");
                                    listIdDinner.remove(i);
                                    listNameDinner.remove(i);
                                    listPriceDinner.remove(i);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });

            return view;
        }
    }
}