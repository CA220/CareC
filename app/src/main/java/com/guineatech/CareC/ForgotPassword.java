package com.guineatech.CareC;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;

/**
 * Created by as334 on 2018/2/10.
 */

public class ForgotPassword extends AppCompatActivity {
    EditText et_pwds = (EditText) findViewById(R.id.et_pwds);
    EditText et_pwd = (EditText) findViewById(R.id.et_pwd);
    EditText ed_code = (EditText) findViewById(R.id.ed_code);
    Button bt_OK = (Button) findViewById(R.id.bt_OK);
    private ProgressDialog waitDialog;
    private AlertDialog userDialog;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        bt_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_pwd.getText().toString().equals(et_pwds.getText().toString()))
                getCode();

            }
        });

    }

    private void getCode() {
        String newPassword = et_pwd.getText().toString();

        if (newPassword == null || newPassword.length() < 1) {
           // TextView label = (TextView) findViewById(R.id.textViewForgotPasswordUserIdMessage);
            //label.setText(passwordInput.getHint() + " cannot be empty");
            //passwordInput.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        String verCode = ed_code.getText().toString();

        if (verCode == null || verCode.length() < 1) {
           // TextView label = (TextView) findViewById(R.id.textViewForgotPasswordCodeMessage);
          //  label.setText(codeInput.getHint() + " cannot be empty");
           // codeInput.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }
        exit(newPassword, verCode);
    }

    private void exit(String newPass, String code) {
        Intent intent = new Intent();
        if(newPass == null || code == null) {
            newPass = "";
            code = "";
        }
        intent.putExtra("newPass", newPass);
        intent.putExtra("code", code);
        setResult(RESULT_OK, intent);
        finish();
    }
}
