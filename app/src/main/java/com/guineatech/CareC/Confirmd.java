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

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;

/**
 * Created by CAHans on 2017/12/28.
 */

public class Confirmd  extends AppCompatActivity {

    Button con;
    EditText code;
    TextView resend;
    private AlertDialog userDialog;
    VerificationHandler resendConfCodeHandler = new VerificationHandler() {
        @Override
        public void onSuccess(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            showDialogMessage("Confirmation code sent.", "Code sent to " + cognitoUserCodeDeliveryDetails.getDestination() + " via " + cognitoUserCodeDeliveryDetails.getDeliveryMedium() + ".", false);
        }

        @Override
        public void onFailure(Exception exception) {
            showDialogMessage("Confirmation code request has failed", AppHelper.formatException(exception), false);
        }
    };
    private ProgressDialog waitDialog;
    GenericHandler confHandler = new GenericHandler() {
        @Override
        public void onSuccess() {
            waitDialog.dismiss();
            showDialogMessage("Confirmation code sent.", "Success.", true);
        }

        @Override
        public void onFailure(Exception exception) {
            waitDialog.dismiss();
            showDialogMessage("Confirmation code request has failed", AppHelper.formatException(exception), false);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corfin);
        con = findViewById(R.id.bt_con);
        code = findViewById(R.id.et_code);
        resend = findViewById(R.id.tv_recode);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reqConfCode();
            }
        });
        //凡回見
        ImageView backic = findViewById(R.id.back);
        backic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //userDialog.setCancelable(false);
        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWaitDialog("Confirming...");
                AppHelper.getPool().getUser(AppHelper.userid).confirmSignUpInBackground(code.getText().toString(), true, confHandler);
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
        } catch (Exception e) {
            //
        }
    }

    private void showDialogMessage(String title, String body, final boolean exitActivity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if (exitActivity) {
                        exit();
                    }
                } catch (Exception e) {
                    exit();
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void exit() {
        Intent it = new Intent();
        it.setClass(Confirmd.this, Sign_Rerister.class);
        startActivity(it);
        finish();
    }

    private void reqConfCode() {
        AppHelper.getPool().getUser(AppHelper.userid).resendConfirmationCodeInBackground(resendConfCodeHandler);
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
