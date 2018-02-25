package com.guineatech.CareC;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Sign_Rerister extends AppCompatActivity {
    TextView text_Sign;
    Button bt_Ris;
    String email,pwd;

    String valuestring = null;
    public SharedPreferences setting;
    public static File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppHelper.checkpool(this);
        setContentView(R.layout.activity_sign__rerister);

        File file = new File("/data/data/com.guineatech.CareC/shared_prefs","CognitoIdentityProviderCache.xml");
        if(file.exists()){
            ReadValue();
            if(!valuestring.equals("")){
                if(AppHelper.userid==null)
                {
                    AppHelper.userid=setting.getString("account","");
                    AppHelper.getPool().getUser(AppHelper.userid).getSessionInBackground(authenticationHandler);
                    pwd=setting.getString("password","");
                }
                SendIntent();
            }
        }
        text_Sign=(TextView)findViewById(R.id.textBt_Sign);
        bt_Ris=(Button)findViewById(R.id.bt_Ris);
        //註冊
        bt_Ris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View arg0) {
                        Intent it = new Intent();
                        it.setClass(Sign_Rerister.this, Rerister.class);
                        startActivityForResult(it,1);
                    }});

        //登入
        text_Sign.setOnClickListener(new View.OnClickListener() {
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
        public void ReadValue(){
        setting = getSharedPreferences("Data",0);
        valuestring = setting.getString("account","");
    }
    public void SendIntent(){
        Intent it = new Intent();
        it.setClass(Sign_Rerister.this,Mainpage.class);
        startActivity(it);
        finish();
    }

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {

            String idToken= cognitoUserSession.getIdToken().getJWTToken();
            Map<String, String> logins = new HashMap<String, String>();
            logins.put("cognito-idp.us-west-2.amazonaws.com/us-west-2_Aj3frUrZo",idToken);
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

    private void getUserAuthentication(AuthenticationContinuation continuation, String username)
    {
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(username, pwd, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

}
