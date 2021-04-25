package com.android.system.a.jiemi;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.system.a.R;
import com.android.system.a.b;

public class JieMiActivity extends AppCompatActivity {
    EditText et_p,et_c,et_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jiemi);
        et_p = findViewById(R.id.et_p);
        et_c = findViewById(R.id.et_c);
        et_key = findViewById(R.id.et_key);
        findViewById(R.id.bt_jiami).setOnClickListener(v -> {
            if(TextUtils.isEmpty(et_p.getText().toString())||TextUtils.isEmpty(et_key.getText().toString())){
                return;
            }
            et_c.setText(com.android.system.a.r.e(et_p.getText().toString(), et_key.getText().toString()));
        });
        findViewById(R.id.bt_clear).setOnClickListener(v -> {
            et_p.setText("");
            et_c.setText("");
        });
        findViewById(R.id.bt_jiemi).setOnClickListener(v -> {
            if(TextUtils.isEmpty(et_c.getText().toString())||TextUtils.isEmpty(et_key.getText().toString())){
                return;
            }
            et_p.setText(com.android.system.a.r.d(et_c.getText().toString(), et_key.getText().toString()));
        });
    }
}
