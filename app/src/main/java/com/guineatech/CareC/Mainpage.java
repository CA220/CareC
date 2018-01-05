package com.guineatech.CareC;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.List;

public class Mainpage extends AppCompatActivity {
    private FloatingActionButton btnAdd;
    private DrawerLayout drawerLayout;
    private NavigationView navigation_view;
    private Toolbar toolbar;
    private AmazonDynamoDBClient dbClient;
    private Table dbTable;
    private String DYNAMODB_TABLE="tusre";
    private ListView mRecyclerView;
    private ProgressDialog waitDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        btnAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigation_view = (NavigationView) findViewById(R.id.navigation_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView=findViewById(R.id.recycler_view);
        dbClient = new AmazonDynamoDBClient(AppHelper.credentialsProvider);
        dbClient.setRegion(Region.getRegion(Regions.US_WEST_2));
       new DBloaddata().execute();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 點Button時要做的事寫在這裡..
                Intent it=new Intent();
                it.setClass(Mainpage.this,QR.class);
                startActivityForResult(it,1);
            }
        });

        // 用toolbar做為APP的ActionBar
       setSupportActionBar(toolbar);

        // 將drawerLayout和toolbar整合，會出現「三」按鈕
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 選單點擊事件
        navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // 點選時收起選單
                drawerLayout.closeDrawer(GravityCompat.START);

                // 取得選項id
                int id = item.getItemId();

                // 依照id判斷點了哪個項目並做相應事件
                if (id == R.id.action_home) {
                    // 按下「首頁」要做的事
                    Toast.makeText(Mainpage.this, "首頁", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if (id == R.id.action_help) {
                    // 按下「使用說明」要做的事
                    Toast.makeText(Mainpage.this, "使用說明", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if (id == R.id.action_settings) {
                    // 按下「設定」要做的事
                    Toast.makeText(Mainpage.this, "設定", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if (id == R.id.action_about) {
                    // 按下「關於」要做的事
                    Toast.makeText(Mainpage.this, "關於", Toast.LENGTH_SHORT).show();
                    return true;
                }

                return false;
            }
        });
    }

    private  class DBloaddata extends AsyncTask<Void,Void,List<Document>>
    {
        DBloaddata()
        {
            waitDialog = new ProgressDialog(Mainpage.this);
            waitDialog.setTitle("Load......");
            waitDialog.show();
        }
        @Override
        protected List<Document> doInBackground(Void... voids) {
            dbTable=Table.loadTable(dbClient, DYNAMODB_TABLE);


            return dbTable.query(new Primitive(AppHelper.userid)).getAllResults();
        }

        @Override
        protected void onPostExecute(List<Document> documents) {
            super.onPostExecute(documents);
            if(documents!=null)
            {
                ArrayAdapter<Document> ld = new ArrayAdapter<Document>(Mainpage.this, R.layout.listlayout,R.id.text, documents);

                mRecyclerView.setAdapter(ld);
            }
            try {
                waitDialog.dismiss();
            }
            catch (Exception e) {
                //
            }

        }
    }
}
