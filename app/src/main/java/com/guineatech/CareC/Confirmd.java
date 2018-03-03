package com.guineatech.CareC;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;

/**
 * Created by CAHans on 2017/12/28.
 */

public class Confirmd  extends AppCompatActivity {

    Button con;
    EditText code;
    private AlertDialog userDialog;
    private ProgressDialog waitDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corfin);
        con = findViewById(R.id.bt_con);
        code = findViewById(R.id.et_code);

        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWaitDialog("Confirming...");
                AppHelper.getPool().getUser(AppHelper.userid).confirmSignUpInBackground(code.getText().toString(), true, confHandler);
            }
        });
    }


    GenericHandler confHandler = new GenericHandler() {
        @Override
        public void onSuccess() {
            waitDialog.dismiss();
            showDialogMessage("Confirmation code sent.", "Success.", true);
        }

        @Override
        public void onFailure(Exception exception) {
            waitDialog.dismiss();
            showDialogMessage("Confirmation code request has failed", "Failed " + exception, false);
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
        it.setClass(Confirmd.this, success.class);
        startActivity(it);
        finish();
    }
}
