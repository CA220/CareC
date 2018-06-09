package com.guineatech.CareC;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Sign_Rerister extends AppCompatActivity {
    // public static File file;
    public SharedPreferences setting;
    ImageView back;
    Button bt_Ris, bt_Sign;
    String email,pwd;
    String valuestring = null;
    //登入AWS
    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            Log.e("First", "ons");
            String idToken = cognitoUserSession.getIdToken().getJWTToken();
            Map<String, String> logins = new HashMap<String, String>();
            logins.put("cognito-idp.us-west-2.amazonaws.com/us-west-2_Aj3frUrZo", idToken);
            AppHelper.credentialsProvider.setLogins(logins);
        }

        //
        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
            //Toast.makeText(getApplicationContext(),"2",Toast.LENGTH_LONG).show();
            getUserAuthentication(authenticationContinuation, username);
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {

        }

        @Override
        public void onFailure(Exception e) {

        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstpage);
        AppHelper.checkpool(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            AppHelper.serial = Build.SERIAL;
        } else {
            AppHelper.serial = "1234567890";
        }

//持續登入
        FirebaseMessaging.getInstance().subscribeToTopic("CareBed_Main");
        File file = new File("/data/data/com.guineatech.CareC/shared_prefs","Data.xml");
        if(file.exists()){
            setting = getSharedPreferences("Data",0);
            valuestring = setting.getString("account","");
            if(!valuestring.equals("")){
                if(AppHelper.userid==null)
                {
                    AppHelper.userid=setting.getString("account","");
                    AppHelper.getPool().getUser(AppHelper.userid).getSessionInBackground(authenticationHandler);
                    pwd=setting.getString("password","");
                }
                Intent it = new Intent();
                it.setClass(Sign_Rerister.this, Frame.class);
                startActivity(it);
                finish();

            }
        }

        back = findViewById(R.id.iv_back);
        bt_Sign = findViewById(R.id.bt_ris);
        bt_Ris = findViewById(R.id.bt_sign);
        //註冊
        bt_Ris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View arg0) {
                        Intent it = new Intent();
                        it.setClass(Sign_Rerister.this, Rerister.class);
                        startActivityForResult(it,1);
                    }});

        //登入
        bt_Sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                it.setClass(Sign_Rerister.this, Login.class);
                if(email==null)
                    email="";
                if(pwd==null)
                    pwd="";
                it.putExtra("email",email);
                it.putExtra("pwd",pwd);
                startActivity(it);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK) {
                    String name = data.getStringExtra("name");
                    if (!name.isEmpty()) {
                        email=name;
                    }
                    String userPasswd = data.getStringExtra("password");
                    if (!userPasswd.isEmpty()) {
                        pwd=userPasswd;
                    }
                    Intent it = new Intent();
                    it.setClass(Sign_Rerister.this, Confirmd.class);
                    startActivity(it);
                }
                break;

        }
    }

    private void getUserAuthentication(AuthenticationContinuation continuation, String username)
    {
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(username, pwd, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

}
