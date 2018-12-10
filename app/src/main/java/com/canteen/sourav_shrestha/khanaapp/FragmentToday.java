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

public class FragmentToday extends Fragment {

    public ArrayList<String> listNameItem;
    public ArrayList<String> listIdItem;
    public ArrayList<String> listPriceItem;
    public ArrayList<String> listCheckItems;
    public ArrayList<String> listCheckItemsID;
    public ArrayList<String> listCheckItemsPrice;
    OptionsFabLayout floatingActionButton;
    MyAdapterOne adaper;
    ListView listTodayMenu;
    static final String queryToday = "SELECT product_name,product_price,p.product_id FROM todays_meal t,table_product p WHERE " +
            "p.product_id = t.product_id;";
    static final String queryDeleteToday = "DELETE FROM todays_meal where product_id ='";
    static final String queryTodayCheck = "SELECT product_name,product_id,product_price FROM table_product;";
    static final String queryAddItems = "INSERT INTO todays_meal VALUES('";

    public FragmentToday(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_today, container, false);
        listNameItem = new ArrayList<>();
        listPriceItem = new ArrayList<>();
        listCheckItems = new ArrayList<>();
        listCheckItemsID = new ArrayList<>();
        listCheckItemsPrice = new ArrayList<>();
        listIdItem = new ArrayList<>();
        listIdItem = new ArrayList<>();
        Log.d("timeItem","OnCreate");
        new FragmentToday.TodayMenu().execute();
        floatingActionButton = view.findViewById(R.id.fab_l);
        listTodayMenu = view.findViewById(R.id.list_today_menu);
        adaper = new MyAdapterOne(getContext(),listNameItem,listPriceItem);
        listTodayMenu.setAdapter(adaper);

        floatingActionButton.setMainFabOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Main fab clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        floatingActionButton.setMiniFabSelectedListener(new OptionsFabLayout.OnMiniFabSelectedListener() {
            @Override
            public void onMiniFabSelected(MenuItem fabItem) {
                switch (fabItem.getItemId()) {
                    case R.id.lunchAdd:
                        listCheckItems.clear();
                        new TodayAddCheck().execute();
                        new LovelyChoiceDialog(getContext(), R.style.TintTheme)
                                .setTopColorRes(R.color.colorPrimary)
                                .setTitle("ADD LUNCH ITEMS")
                                .setItemsMultiChoice(listCheckItems, new LovelyChoiceDialog.OnItemsSelectedListener<String>() {
                                    @Override
                                    public void onItemsSelected(List<Integer> positions, List<String> items) {
                                        for(String item : items){
                                            String idC = listCheckItemsID.get(listCheckItems.indexOf(item));
//                                            Log.d("IDSD",idC);
                                            new TodayMenuAdd().execute(idC,"1","0");
                                            listIdItem.add(listCheckItemsID.get(listCheckItems.indexOf(item)));
                                            listNameItem.add(item);
                                            listPriceItem.add(listCheckItemsPrice.get(listCheckItems.indexOf(item)));
                                            floatingActionButton.closeOptionsMenu();
                                            adaper.notifyDataSetChanged();
                                        }
                                    }
                                })
                                .setConfirmButtonText("ADD")
                                .show();
                        break;
                    case R.id.dinnnerAdd:
                        Toast.makeText(getContext(),
                                fabItem.getTitle() + "clicked!",
                                Toast.LENGTH_SHORT).show();
                    default:
                        break;
                }
            }
        });

        return view;
    }

    class TodayMenu extends AsyncTask {

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
                        + "=" + URLEncoder.encode(queryToday, "UTF-8");
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
                    listNameItem.add(columnPerRow[0]);
                    listPriceItem.add(columnPerRow[1]);
                    listIdItem.add(columnPerRow[2]);
                }
                return sb;

            } catch (IOException e) {
                Toast.makeText(getContext(),e.toString(),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            for(int i=0;i<listNameItem.size();i++)
                Log.d("hello",listNameItem.get(i)+","+listPriceItem.get(i));
            adaper.notifyDataSetChanged();
            super.onPostExecute(o);
        }
    }


    class TodayMenuDelete extends AsyncTask<String, String, String> {

        String sb="";
        String id;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String link="http://172.29.5.23/collegecanteen/khana.php";
            try {
                id = strings[0];
                URL url = new URL(link);
                String data  = URLEncoder.encode("khana", "UTF-8")
                        + "=" + URLEncoder.encode(queryDeleteToday+id+"';", "UTF-8");
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
                Toast.makeText(getContext(),e.toString(),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String o) {
            for(int i=0;i<listNameItem.size();i++)
                Log.d("hello",listNameItem.get(i)+","+listPriceItem.get(i));
            adaper.notifyDataSetChanged();
            super.onPostExecute(o);
        }
    }


    class TodayAddCheck extends AsyncTask<String, String, String> {

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
                        + "=" + URLEncoder.encode(queryTodayCheck, "UTF-8");
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
                    if(!listNameItem.contains(columnPerRow[0])) {
                        listCheckItems.add(columnPerRow[0]);
                        listCheckItemsID.add(columnPerRow[1]);
                        listCheckItemsPrice.add(columnPerRow[2]);
                    }
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
            for(int i=0;i<listNameItem.size();i++)
                Log.d("hello",listNameItem.get(i)+","+listPriceItem.get(i));
            adaper.notifyDataSetChanged();
            super.onPostExecute(o);
        }
    }

    class TodayMenuAdd extends AsyncTask<String, String, String> {

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
                String data  = URLEncoder.encode("khana", "UTF-8")
                        + "=" + URLEncoder.encode(queryAddItems+id+"','"+lunch+"','"+dinner+"');", "UTF-8");
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
                Toast.makeText(getContext(),e.toString(),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String o) {
            adaper.notifyDataSetChanged();
            super.onPostExecute(o);
        }
    }

    class MyAdapterOne extends BaseAdapter {

        public ArrayList<String> listNameItem;
        public ArrayList<String> listPriceItem;
        private Context context;

        public MyAdapterOne(Context context,ArrayList<String> listNameItem,ArrayList<String> listPriceItem) {
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
            view = inflater.inflate(R.layout.each_list_today,null);
            final TextView title = view.findViewById(R.id.textItemToday);
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
                            .setMessage("Do You Really Want To Remove "+title.getText()+" From Today's Menu ?")
                            .setPositiveButton("Yes", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    Toast.makeText(context, "Positive Clicked", Toast.LENGTH_SHORT).show();
                                    new TodayMenuDelete().execute(listIdItem.get(i));
                                    listIdItem.remove(i);
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
}