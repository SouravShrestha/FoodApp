package com.canteen.sourav_shrestha.khanaapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MenuTodayAdmin extends AppCompatActivity {

    ArrayList<String> listNameItem = new ArrayList<>();
    ArrayList<String> listPriceItem = new ArrayList<>();
    ArrayList<String> listImageItem = new ArrayList<>();
    TextView textItemToday,textItemPrice;
    ImageView imgItemToday;
    ListView listTodayMenu;
    MyAdapter adaper;
    static final String queryToday = "SELECT product_name,product_price,product_image FROM todays_meal t,table_product p WHERE " +
            "p.product_id = t.product_id;";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_today_admin);

        textItemPrice = findViewById(R.id.textItemPrice);
        textItemToday = findViewById(R.id.textItemToday);
        imgItemToday = findViewById(R.id.imgItemToday);
        listTodayMenu = findViewById(R.id.list_today_menu);

        new TodayMenu().execute();
        adaper = new MyAdapter(this);
        listTodayMenu.setAdapter(adaper);

    }

    class TodayMenu extends AsyncTask{

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
                    listImageItem.add(columnPerRow[2]);
                }
                return sb;
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
//            for(int i=0;i<listImageItem.size();i++)
//                Log.d("hello",listNameItem.get(i)+","+listPriceItem.get(i)+","+listImageItem.get(i));
//            Log.d("data",sb);
            super.onPostExecute(o);
        }
    }

    class MyAdapter extends BaseAdapter {


        private Context context;

        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return listImageItem.size();
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
            view = getLayoutInflater().inflate(R.layout.each_list_today,null);
            TextView title = view.findViewById(R.id.textItemToday);
            title.setText(listNameItem.get(i));
            TextView tDate = view.findViewById(R.id.textItemPrice);
            tDate.setText("Price : "+listPriceItem.get(i));
            return view;
        }
    }


}
