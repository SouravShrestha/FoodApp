package com.canteen.sourav_shrestha.khanaapp.orderSummary;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.canteen.sourav_shrestha.khanaapp.R;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

public class Order extends AppCompatActivity implements View.OnClickListener {


    Button date;
    Spinner meal_time,category;
    int mYear=0,mMonth=0,mDay=0;
    String sb = null;
    RecyclerView recyclerView;
    recycler_ViewAdapter adapter;
    String queryToday = "select product_id,product_name,product_type from table_product";
    ArrayList<Product> set = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState)

    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        date = findViewById(R.id.date);
        meal_time = findViewById(R.id.meal_time);
        category = findViewById(R.id.category);
        ArrayAdapter<String> mealAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,new String[]{"Lunch","Dinner"});
        ArrayAdapter<String> CategoryAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,new String[]{"All","Veg","Non-Veg"});
        meal_time.setAdapter(mealAdapter);
        category.setAdapter(CategoryAdapter);

        date.setOnClickListener(this);

        try {
            set.addAll(product());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        queryToday = "select order_contents from table_order";
        try {
            sb = new TodayMenu().execute().get().toString();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            adapter = new recycler_ViewAdapter(StringParser(sb),getApplicationContext());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        meal_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();

                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();

                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    @Override
    public void onClick(View v) {
        if(v==date)
        {
            Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,R.style.datepicker,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            Toast.makeText(Order.this, year+"-"+monthOfYear+"-"+dayOfMonth, Toast.LENGTH_SHORT).show();

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    }


    public HashSet<Product> product() throws ExecutionException, InterruptedException {
        HashSet<Product> hashMap = new HashSet<Product>();
        String sb = new TodayMenu().execute().get().toString();
        String[] temp = sb.split(",&");
        for(String s:temp)
        {
            String temp1[] = s.split(",");
            hashMap.add(new Product(temp1[0],temp1[1],temp1[2]));


        }

        return  hashMap;

    }

    public  ArrayList<OrderSummary> StringParser(String sb) throws ExecutionException, InterruptedException {

        //Toast.makeText(this, "String Parse", Toast.LENGTH_SHORT).show();

        for(Product product:set)
        {
            //Toast.makeText(this, product.productid+"" +product.product, Toast.LENGTH_SHORT).show();
            sb =sb.replaceAll(product.productid,product.product);

        }
        //Toast.makeText(this, sb, Toast.LENGTH_SHORT).show();
        ArrayList<OrderSummary> list = new ArrayList<>();

        int pos = -1;
        String s[] = sb.split(",&");
        for (String s1: s)
        {
            String s2[] = s1.split("\\|");
            for(String s3:s2)
            {
                String temp[] = s3.split(" ");
                pos = search(temp[0], list);
                if (pos == -1) {

                    list.add(new OrderSummary(temp[0], Integer.parseInt(temp[1]), meal_type(temp[0],set)));

                } else {
                    list.get(pos).quantity += Integer.parseInt(temp[1]);

                }
            }


        }


        return list;
    }

    public int search(String s,ArrayList<OrderSummary> set)
    {
        int i=0;
        for(OrderSummary p : set)
        {
            if(p.product.equals(s))
            {
                return  i;
            }
            i++;



        }
        return  -1;
    }

    public String meal_type(String s,ArrayList<Product> products)
    {
        for(int i=0;i<products.size();i++)
        {
            if(s.equals(products.get(i).product))
            {
                return  products.get(i).mealType;
            }
        }

        return "0";

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
                                return sb;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

            super.onPostExecute(o);
        }
    }


}
