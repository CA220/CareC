package com.guineatech.CareC;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by CAHans on 2018/5/31.
 */

public class addmattre extends AppCompatActivity {
    EditText nickname;
    private String bcode, name;
    private Button bt_c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmattress);
        Intent it = this.getIntent();
        bcode = it.getStringExtra("bcode");
        bt_c = findViewById(R.id.bt_confirm);
        nickname = findViewById(R.id.et_name);



        bt_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nickname.getText().toString().trim().length() > 0) {
                    name = nickname.getText().toString();
                } else name = bcode;

                Intent it = new Intent(addmattre.this, wifi.class);
                it.putExtra("bcode", bcode);
                it.putExtra("nickname", name);
                startActivity(it);
                finish();
            }
        });
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
