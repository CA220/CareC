package com.guineatech.CareC;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.List;

/**
 * Created by CAHans on 2018/1/15.
 */

public class decive_data extends AppCompatActivity {
    private Toolbar toolbar;
    private AmazonDynamoDBClient dbClient;
    private Table dbTable;
        private String DYNAMODB_TABLE="SSTP";
   // private ListView mRecyclerView;
    private ProgressDialog waitDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decive_data);
        dbClient = new AmazonDynamoDBClient(AppHelper.credentialsProvider);
        dbClient.setRegion(Region.getRegion(Regions.US_WEST_2));
    }
    private  class DBloaddata extends AsyncTask<Void,Void,List<Document>>
    {
        DBloaddata()
        {
            waitDialog = new ProgressDialog(decive_data.this);
            waitDialog.setTitle("Load......");
            waitDialog.show();
        }
        @Override
        protected List<Document> doInBackground(Void... voids) {
            dbTable= Table.loadTable(dbClient, DYNAMODB_TABLE);


            return dbTable.query(new Primitive("client_01")).getAllResults();
        }

        @Override
        protected void onPostExecute(List<Document> documents) {
            super.onPostExecute(documents);
            if(documents!=null)
            {
               /* ArrayAdapter<Document> ld = new ArrayAdapter<Document>(decive_data.this, R.layout.listlayout,R.id.text, documents);

                mRecyclerView.setAdapter(ld);*/
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
