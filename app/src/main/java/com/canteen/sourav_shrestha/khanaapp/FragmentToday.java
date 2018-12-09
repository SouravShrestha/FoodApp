package com.canteen.sourav_shrestha.khanaapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class FragmentToday extends Fragment {

    public ArrayList<String> listNameItem;
    public ArrayList<String> listPriceItem;
    MyAdapterOne adaper;
    ListView listTodayMenu;
    static final String queryToday = "SELECT product_name,product_price,product_image FROM todays_meal t,table_product p WHERE " +
            "p.product_id = t.product_id;";

    public FragmentToday(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_today, container, false);
        listNameItem = new ArrayList<>();
        listPriceItem = new ArrayList<>();
        new FragmentToday.TodayMenu().execute();
        listTodayMenu = view.findViewById(R.id.list_today_menu);
        adaper = new MyAdapterOne(getContext(),listNameItem,listPriceItem);
        listTodayMenu.setAdapter(adaper);
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
//                URL url = new URL(link);
                String data  = URLEncoder.encode("khana", "UTF-8")
                        + "=" + URLEncoder.encode(queryToday, "UTF-8");
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//                conn.setRequestMethod("POST");
//                OutputStream os = conn.getOutputStream();
//                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os));
//                bufferedWriter.write(data);
//                bufferedWriter.flush();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                String line = "";
//                while((line = reader.readLine()) != null) {
//                    sb = sb + line;
//                }
//
//                String rowsToday[] = sb.split("&");
//                for(String x:rowsToday) {
//                    String columnPerRow[] = x.split(",");
//                    listNameItem.add(columnPerRow[0]);
//                    listPriceItem.add(columnPerRow[1]);
//                    listImageItem.add(columnPerRow[2]);
//                }
//                return sb;
                listNameItem.add("Roti");
                listPriceItem.add("20");
                listNameItem.add("Dal");
                listPriceItem.add("40");
                listNameItem.add("Roti");
                listPriceItem.add("20");
                listNameItem.add("Dal");
                listPriceItem.add("40");
                listNameItem.add("Roti");
                listPriceItem.add("20");
                listNameItem.add("Dal");
                listPriceItem.add("40");
                listNameItem.add("Roti");
                listPriceItem.add("20");
                listNameItem.add("Dal");
                listPriceItem.add("40");
                listNameItem.add("Roti");
                listPriceItem.add("20");
                listNameItem.add("Dal");
                listPriceItem.add("40");
                listNameItem.add("Roti");
                listPriceItem.add("20");
                listNameItem.add("Dal");
                listPriceItem.add("40");
                listNameItem.add("Roti");
                listPriceItem.add("20");
                listNameItem.add("Dal");
                listPriceItem.add("40");

            } catch (IOException e) {
                Toast.makeText(getContext(),e.toString(),Toast.LENGTH_SHORT).show();
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.each_list_today,null);
            TextView title = view.findViewById(R.id.textItemToday);
            title.setText(listNameItem.get(i));
            TextView tPrice = view.findViewById(R.id.textItemPrice);
            tPrice.setText("Price : "+listPriceItem.get(i));
            ImageView imageItem = view.findViewById(R.id.imgItem);
            imageItem.setBackgroundResource(R.drawable.vegetarian_symbol);
            return view;
        }
    }
}