package com.guineatech.CareC;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;

public class Rerister extends AppCompatActivity {
    EditText et_Mail,et_pwds,et_pwd;
    Button nexts, bt_sign;
    TextView comfim;
    private ProgressDialog waitDialog;
    private AlertDialog userDialog;
    SignUpHandler signupCallback = new SignUpHandler() {

        @Override
        public void onSuccess(CognitoUser cognitoUser, boolean userConfirmed, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            // Sign-up was successful
            closeWaitDialog();
            // Check if this user (cognitoUser) needs to be confirmed
            if (!userConfirmed) {
                // This user must be confirmed and a confirmation code was sent to the user
                // cognitoUserCodeDeliveryDetails will indicate where the confirmation code was sent
                // Get the confirmation code from user
                showDialogMessage("Sign up successful!", "successful!", true);


            } else {
                // The user has already been confirmed
                showDialogMessage("Sign up successful!", et_Mail.getText().toString() + " has been Confirmed", false);
            }

        }

        @Override
        public void onFailure(Exception exception) {
            // Sign-up failed, check exception for the cause
            closeWaitDialog();
            showDialogMessage("Sign up Fail!", AppHelper.formatException(exception), false);
        }
    };


    //註冊

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);

//        userDialog.setCancelable(false);

        et_Mail = findViewById(R.id.et_email);
        et_pwds = findViewById(R.id.et_conpasswd);
        et_pwd = findViewById(R.id.et_password);
        nexts = findViewById(R.id.bt_ris);
        bt_sign = findViewById(R.id.bt_sign);

        ImageView backic = findViewById(R.id.iv_back);
        backic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        AppHelper.checkpool(getApplicationContext());
        nexts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pwd =et_pwds.getText().toString();
                String pc=et_pwd.getText().toString();
                String email=et_Mail.getText().toString();
                if(email!=null&&pwd!=null&&pc!=null) {
                    CognitoUserAttributes userAttributes = new CognitoUserAttributes();
                    userAttributes.addAttribute("email", email);
                    AppHelper.userid = email.replace("@", "-at-");
                    showWaitDialog("Signing up...");
                    AppHelper.getPool().signUpInBackground(AppHelper.userid, pwd, userAttributes, null, signupCallback);
                }
                else
                {
                    Toast.makeText(view.getContext(), "Can't null", Toast.LENGTH_LONG).show();

                }
            }
        });
        bt_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Rerister.this, Login.class);
                startActivity(it);
                finish();
            }
        });

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
                        exit(et_Mail.getText().toString());
                    }
                } catch (Exception e) {
                    if(exit) {
                        exit(et_Mail.getText().toString());
                    }
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }


    private void exit(String uname) {
        exit(uname, null);
    }

    private void exit(String uname, String password) {
        Intent intent;
        intent = new Intent();
        if (uname == null) {
            uname = "";
        }
        if (password == null) {
            password = "";
        }
        intent.putExtra("name", uname);
        intent.putExtra("password", password);
        setResult(RESULT_OK, intent);
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
