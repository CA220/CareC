package com.guineatech.CareC;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by CAHans on 2018/1/15.
 */

public class decive_data extends AppCompatActivity {
    TextView tv_nick, tv_heart, tv_br, tv_sleep, tv_data;
    ImageView iv_setting, iv_clan;
    String did;
    Calendar calendar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleeprecord);
        tv_nick = findViewById(R.id.textView16);
        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        iv_setting = findViewById(R.id.iv_setting);

        iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), iv_setting);//第二个参数是绑定的那个view
                //获取菜单填充器
                MenuInflater inflater = popup.getMenuInflater();
                //填充菜单
                inflater.inflate(R.menu.devicemenu, popup.getMenu());
                //绑定菜单项的点击事件
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.device_Edit:
                                Toast.makeText(getApplicationContext(), "Edit···", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.device_Delete:
                                Toast.makeText(getApplicationContext(), "Detete···", Toast.LENGTH_SHORT).show();
                                new DBadddata().execute();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popup.show(); //这一行代码不要忘记了
            }
        });
        Intent it = this.getIntent();
        String id = it.getStringExtra("devicename");
        tv_nick.setText(id);
        did = it.getStringExtra("deviceid");
        tv_heart = findViewById(R.id.tv_heart);
        tv_br = findViewById(R.id.tv_br);
        tv_sleep = findViewById(R.id.tv_sleep);
        tv_data = findViewById(R.id.tv_data);
        tv_sleep.setText("-");
        tv_br.setText("-");
        tv_heart.setText("-");
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");


        calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(new java.util.Date());//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天

        String date = sDateFormat.format(calendar.getTime());   //得到前一天的时间


        iv_clan = findViewById(R.id.iv_clan);
        tv_data.setText(date);
        iv_clan.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                DatePickerDialog pickerDialog = new DatePickerDialog(decive_data.this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));


                pickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());  //设置日期最大值
                pickerDialog.show();
                // pickerDialog.getDatePicker().setMinDate();
//设置日期最小值
                // pickerDialog.show();
            }
        });
    }

    /*
    TextView t0, t1, t2;

    String BMS = "";
    XAxis x1;
    private AmazonDynamoDBClient dbClient;
    private Table dbTable;
    private String DYNAMODB_TABLE="SSTP";

    private ProgressDialog waitDialog;
    private LineChart m;
    private View mapage, datapage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_showdata);
        super.onCreate(savedInstanceState);
        mapage = findViewById(R.id.ln_main);


        Intent it=this.getIntent();
        String dn=it.getStringExtra("devicename");

        ImageView backic = findViewById(R.id.back);
        backic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        TextView t5 = findViewById(R.id.textView5);
        t5.setText(dn);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbClient = new AmazonDynamoDBClient(AppHelper.credentialsProvider);
        dbClient.setRegion(Region.getRegion(Regions.US_WEST_2));

        m = findViewById(R.id.chart);
        m.setDragEnabled(true);
        m.setDrawingCacheEnabled(false);

        x1=m.getXAxis();
        x1.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis rightYAxis=m.getAxisRight();
        rightYAxis.setEnabled(false);


        t0=findViewById(R.id.textView3);
        t1=findViewById(R.id.textView);
        t2=findViewById(R.id.textView2);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 設置要用哪個menu檔做為選單
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // 取得點選項目的id
        int id = item.getItemId();

        // 依照id判斷點了哪個項目並做相應事件
        if (id == R.id.nav_devicename) {
            Toast.makeText(this, "devicename", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.nav_delete) {
            Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.nav_notification) {
            Intent it =new Intent();
            it.setClass(decive_data.this,notifications.class);
            startActivity(it);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    private  class DBloaddata extends AsyncTask<String,Void,List<Document>>
    {
        String howlong;
        DBloaddata()
        {
            waitDialog = new ProgressDialog(decive_data.this);
            waitDialog.setTitle("Load......");
            waitDialog.show();
        }

        @Override
        protected List<Document> doInBackground(String... voids) {
            dbTable= Table.loadTable(dbClient, DYNAMODB_TABLE);
            howlong=voids[0];
            return dbTable.query(new Primitive("client_01")).getAllResults();
        }

        @Override
        protected void onPostExecute(List<Document> documents) {
            super.onPostExecute(documents);

            final List<String>timeList =new ArrayList<>();
            SimpleDateFormat f = new SimpleDateFormat("EEE MMM dd yyyy hh:mm:ss");

            ArrayList<Entry> [] ya=new ArrayList[3];
            ya[0] =new ArrayList<>();
            ya[1] =new ArrayList<>();
            ya[2] =new ArrayList<>();
            int rrr=0;
            if(howlong=="day") {
                rrr=5;
            }
            else if(howlong=="month") {
                rrr=6;
            }
            else if(howlong=="week") {
                rrr=30;
            }

            Date d = new Date(System.currentTimeMillis());
            String dat="";
            long da=0;
            int avg1=0,avg2=0,cot=0;
            int i1=0,i2=0,i3=0;
            if(documents!=null)
            {
               /* ArrayAdapter<Document> ld = new ArrayAdapter<Document>(decive_data.this, R.layout.listlayout,R.id.text, documents);

                mRecyclerView.setAdapter(ld);*/

/*
                try {
                    for(Document dd:documents)
                    {

                        timeList.add(dd.get("TimeStamp").asString().substring(16,24));


                        dat+=(dd.get("TimeStamp").asString());
                        Date a=f.parse(dat);
                        da=d.getTime()-a.getTime();
                        //sum += dd.toString();
                        //BMS=dd.get("BodyMotionState").asString()+"   ";
                        if((da/(1000*60*60*24))<=rrr) {

                            BMS+=dd.get("TimeStamp").asString()+"\n";
                            i1 = Integer.parseInt(dd.get("BodyMotionState").asString());
                            i2 = Integer.parseInt(dd.get("BreathingValue").asString());
                            i3 = Integer.parseInt(dd.get("HeartBeatValue").asString());
                            avg1 += Integer.parseInt(dd.get("HeartBeatValue").asString());
                            avg2 += Integer.parseInt(dd.get("BreathingValue").asString());
                            ya[0].add(new Entry(cot, i1));
                            ya[1].add(new Entry(cot, i2));
                            ya[2].add(new Entry(cot, i3));
                            cot++;
                        }
                    }} catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    if(cot==0)
                    {
                        ya[0].add(new Entry(cot, 0));
                        ya[1].add(new Entry(cot, 0));
                        ya[2].add(new Entry(cot, 0));
                    }
                }
            }

            x1.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return timeList.get((int) value % timeList.size());
                }
            });


            //t0.setText(BMS);
            t0.setText(da/(1000*60*60*24)+"");
            t1.setText(avg1+" ");
            t2.setText(avg2+" ");

            LineDataSet [] set=new LineDataSet[3];
            String[] item={"BodyMotionState","BreathingValue","HeartBeatValue"};
            int [] color={Color.BLUE,Color.BLACK,Color.RED};
            ArrayList<ILineDataSet> dataset = new ArrayList<>();
            for(int i=0;i<3;i++)
            {
                set[i]= new LineDataSet( ya[i],item[i]);
                set[i].setDrawValues(false);
                set[i].setDrawCircles(false);
                set[i].setFillAlpha(10);
                set[i].setColor(color[i]);
                set[i].setCircleColor(Color.BLACK);
                set[i].setHighLightColor(Color.YELLOW);
                set[i].setLineWidth(1f);
                set[i].setValueTextSize(12f);
                set[i].setValueTextColor(Color.RED);
                dataset.add(set[i]);
            }
            LineData data =new LineData(dataset);

            try {
                waitDialog.dismiss();
                m.setData(data);
            }
            catch (Exception e) {
                //
            }

        }
    }*/

    private class DBadddata extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            {
                HttpURLConnection urlConnection = null;
                InputStream is = null;
                String result = "";
                try {

                    URL url = new URL("https://gr7yvkqkte.execute-api.us-west-2.amazonaws.com/dev/my-test/deledevice");//php的位置
                    urlConnection = (HttpURLConnection) url.openConnection();//對資料庫打開連結
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setUseCaches(false);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("deviceid", did);
                    jsonParam.put("userid", AppHelper.userid);

                    wr.writeBytes(jsonParam.toString());
                    wr.flush();
                    wr.close();
                    is = urlConnection.getInputStream();//從database 開啟 stream
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
                    StringBuilder builder = new StringBuilder();


                    builder.append(bufReader.readLine());

                    is.close();
                    result = builder.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "F";
                }


                Log.e("Log", result);

                return result;
            }

        }

        @Override
        protected void onPostExecute(String devicedata) {
            super.onPostExecute(devicedata);
            Log.e("Log", devicedata);
            if (!devicedata.equals("F")) {
                Log.e("Log", "in");
                Intent i = new Intent();
                i.setAction("DB");
                i.putExtra("DB", "R");
                sendBroadcast(i);
                finish();
            }

        }
    }

}