package com.guineatech.CareC;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;

/**
 * Created by CAHans on 2018/4/8.
 */

public class ConfrimHome extends AppCompatActivity {
    // private ProgressDialog waitDialog;
    //private AlertDialog userDialog;
    //verification code
    GenericHandler confHandler = new GenericHandler() {
        @Override
        public void onSuccess() {
            // waitDialog.dismiss();
            //  showDialogMessage("Confirmation code sent.", "Success.", true);
            Toast.makeText(getApplicationContext(), "Confirmation code sent Success", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(Exception exception) {
            //   waitDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Confirmation code request has failed", Toast.LENGTH_LONG).show();
            //  showDialogMessage("Confirmation code request has failed", AppHelper.formatException(exception), false);
        }
    };
    //resend code
    VerificationHandler resendConfCodeHandler = new VerificationHandler() {
        @Override
        public void onSuccess(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            // showDialogMessage("Confirmation code sent.", "Code sent to " + cognitoUserCodeDeliveryDetails.getDestination() + " via " + cognitoUserCodeDeliveryDetails.getDeliveryMedium() + ".", false);
            Toast.makeText(getApplicationContext(), "Code sent Success", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(Exception exception) {
            Toast.makeText(getApplicationContext(), "Code sent Fail", Toast.LENGTH_LONG).show();
            // showDialogMessage("Confirmation code request has failed", AppHelper.formatException(exception), false);
        }
    };
    private Button con;
    private EditText code, email;
    private TextView resend, title;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_code);
        code = findViewById(R.id.et_concode);
        email = findViewById(R.id.et_email);
        resend = findViewById(R.id.tvb_resendcode);
        con = findViewById(R.id.bt_confirm);
        title = findViewById(R.id.tv_title);
        title.setText("Confrim Code");
        ImageView backic = findViewById(R.id.iv_back);
        backic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        email.setVisibility(View.VISIBLE);

        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!email.getText().toString().isEmpty() && email.getText().toString() != "" && !code.getText().toString().isEmpty()) {
                    String em = email.getText().toString();
                    em = em.replace("@", "-at-");
                    //  showWaitDialog("Confirming...");
                    AppHelper.getPool().getUser(em).confirmSignUpInBackground(code.getText().toString(), true, confHandler);

                }
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em = email.getText().toString();
                em = em.replace("@", "-at-");
                AppHelper.getPool().getUser(em).resendConfirmationCodeInBackground(resendConfCodeHandler);
            }
        });
    }

   /* private void showWaitDialog(String message) {
        try {
            waitDialog.dismiss();
        } catch (Exception e) {
            //
        }
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle(message);
        waitDialog.show();
    }

    private void showDialogMessage(String title, String body, final boolean exitActivity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if (exitActivity) {
                        finish();
                    }
                } catch (Exception e) {
                    finish();
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }
*/

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
