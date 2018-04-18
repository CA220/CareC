package com.guineatech.CareC;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;

/**
 * Created by as334 on 2018/2/10.
 */

public class ForgotPassword extends AppCompatActivity {
    EditText et_pwds;
    EditText et_pwd;
    EditText ed_code;
    EditText ed_email;
    Button bt_OK;
    String Email;
    private ForgotPasswordContinuation forgotPasswordContinuation;
    ForgotPasswordHandler forgotPasswordHandler = new ForgotPasswordHandler() {
        @Override
        public void onSuccess() {

            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void getResetCode(ForgotPasswordContinuation forgotPasswordContinuations) {
            //驗證帳號候傳驗證碼
            getForgotPasswordCode(forgotPasswordContinuations);
            ed_email.setVisibility(View.INVISIBLE);
            ed_code.setVisibility(View.VISIBLE);
            et_pwd.setVisibility(View.VISIBLE);
            et_pwds.setVisibility(View.VISIBLE);
            bt_OK.setText("SET Password");


        }

        @Override
        public void onFailure(Exception e) {
            Log.e("Log", AppHelper.formatException(e));
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        et_pwd = findViewById(R.id.et_pwd);
        et_pwds = findViewById(R.id.et_pwds);
        ed_code = findViewById(R.id.et_code);
        ed_email = findViewById(R.id.et_Mail);

        Intent it = this.getIntent();

        ed_email.setText(it.getStringExtra("Email"));

        //凡回見
        ImageView backic = findViewById(R.id.back);
        backic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        bt_OK = findViewById(R.id.ForgotPassword_button);
        bt_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bt_OK.getText().toString().equals("Confirm")) {
                    if (!ed_email.getText().toString().isEmpty()) {
                        Email = ed_email.getText().toString();
                        Email = Email.replace("@", "-at-");
                        AppHelper.getPool().getUser(Email).forgotPasswordInBackground(forgotPasswordHandler);
                    } else {
                        Toast.makeText(getApplicationContext(), "Email can't null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Commit", Toast.LENGTH_LONG).show();
                    if (et_pwd.getText().toString().equals(et_pwds.getText().toString())) {
                        String newPass = et_pwd.getText().toString();
                        String code = ed_code.getText().toString();
                        forgotPasswordContinuation.setPassword(newPass);
                        forgotPasswordContinuation.setVerificationCode(code);
                        forgotPasswordContinuation.continueTask();
                        Toast.makeText(getApplicationContext(), "Commit", Toast.LENGTH_LONG);
                    } else
                        Toast.makeText(getApplicationContext(), "Password different", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void getForgotPasswordCode(ForgotPasswordContinuation forgotPasswordContinuation) {
        this.forgotPasswordContinuation = forgotPasswordContinuation;
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
