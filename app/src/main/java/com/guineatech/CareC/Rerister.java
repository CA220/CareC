package com.guineatech.CareC;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;

public class Rerister extends AppCompatActivity {
    EditText et_Mail,et_pwds,et_pwd;
    Button nexts;
    private ProgressDialog waitDialog;
    private AlertDialog userDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rerister);
        et_Mail=findViewById(R.id.et_Mail);
        et_pwds=findViewById(R.id.et_pwds);
        et_pwd=findViewById(R.id.et_pwd);
        nexts=findViewById(R.id.nexts);
        AppHelper.checkpool(getApplicationContext());
        nexts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pwd =et_pwds.getText().toString();
                String pc=et_pwd.getText().toString();
                String email=et_Mail.getText().toString();
                CognitoUserAttributes userAttributes = new CognitoUserAttributes();
                userAttributes.addAttribute("email", email);
                AppHelper.userid= email.replace("@","-at-");
                showWaitDialog("Signing up...");
                AppHelper.getPool().signUpInBackground(AppHelper.userid, pwd, userAttributes, null, signupCallback);




            }
        });
    }


    //註冊





    SignUpHandler signupCallback = new SignUpHandler() {

        @Override
        public void onSuccess(CognitoUser cognitoUser, boolean userConfirmed, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            // Sign-up was successful
            closeWaitDialog();
            // Check if this user (cognitoUser) needs to be confirmed
            if(!userConfirmed) {
                // This user must be confirmed and a confirmation code was sent to the user
                // cognitoUserCodeDeliveryDetails will indicate where the confirmation code was sent
                // Get the confirmation code from user
                showDialogMessage("Sign up successful!","successful!", true);


            }
            else {
                // The user has already been confirmed
                showDialogMessage("Sign up successful!",et_Mail.getText().toString()+" has been Confirmed", true);
            }

        }

        @Override
        public void onFailure(Exception exception) {
            // Sign-up failed, check exception for the cause
            closeWaitDialog();
            showDialogMessage("Sign up Fail!",et_Mail.getText().toString()+" has been Sign up", true);
        }
    };

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

}
