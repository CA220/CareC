package com.guineatech.CareC;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    TextView text_forgot;
    EditText ed_mail,ed_pwd;
    Button bt_ok, bt_ris;
    String email,pwd;
    private AlertDialog userDialog;
    private ProgressDialog waitDialog;
    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            closeWaitDialog();
            String idToken = cognitoUserSession.getIdToken().getJWTToken();
            Map<String, String> logins = new HashMap<String, String>();
            logins.put("cognito-idp.us-west-2.amazonaws.com/us-west-2_Aj3frUrZo", idToken);
            AppHelper.credentialsProvider.setLogins(logins);
            SharedPreferences setting = getSharedPreferences("Data", 0);
            setting.edit()
                    .putString("account", AppHelper.userid)
                    .putString("passwrod", ed_pwd.getText().toString())
                    .commit();
            exhere();


        }

        //
        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {

            getUserAuthentication(authenticationContinuation, username);
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {

        }

        @Override
        public void onFailure(Exception e) {
            closeWaitDialog();
            showDialogMessage("Sign in", "Fail " + e, false);
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        ImageView backic = findViewById(R.id.iv_back);
        backic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //userDialog.setCancelable(false);

        text_forgot = findViewById(R.id.tvb_forgotpwd);
        bt_ok = findViewById(R.id.bt_ris);
        ed_mail = findViewById(R.id.et_email);
        ed_pwd = findViewById(R.id.et_password);
        bt_ris = findViewById(R.id.bt_sign);

        text_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                it.putExtra("Email", ed_mail.getText().toString());
                it.setClass(Login.this, ForgotPassword.class);
                startActivity(it);
            }
        });

        Intent it=this.getIntent();
        email=it.getStringExtra("email");
        pwd=it.getStringExtra("pwd");
        ed_mail.setText(email);
        ed_pwd.setText(pwd);
        AppHelper.checkpool(this);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    AppHelper.userid= ed_mail.getText().toString().replace("@","-at-");

                //要球憑證
                AppHelper.getPool().getUser(AppHelper.userid).getSessionInBackground(authenticationHandler);
                showWaitDialog("Login......");

            }
        });
        bt_ris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Login.this, Rerister.class);
                startActivity(it);
                finish();
            }
        });


    }



    private void getUserAuthentication(AuthenticationContinuation continuation, String username)
    {
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(username, ed_pwd.getText().toString(), null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

    private void showWaitDialog(String message) {
        closeWaitDialog();
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle(message);
        waitDialog.show();
    }

    private void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        }
        catch (Exception e) {
            //
        }
    }


    private void showDialogMessage(String title, String body, final boolean exit) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if(exit) {
                        exhere();
                    }
                } catch (Exception e) {
                    if(exit) {
                        exhere();
                    }
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void exhere()
    {
        Intent it = new Intent(Login.this, Frame.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(it);
        finish();

    }


    //點空白取消鍵盤
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (HideInputUtils.isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }
}
