package com.android.system.a.ceshi;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.system.a.R;

public class CeshiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceshi);
        ((EditText)findViewById(R.id.et_config)).setText("{\"gg\":\"true\",\"title\":\"弹窗标题\",\"titleColor\":\"#FF0000\",\"message\":\"123\",\"messageColor\":\"#00FF00\",\"messageHtml\":\"<p style=\\\"color:blue\\\">消息内容，支持html</p>\",\"cancelable\":\"true\",\"confirmText\":\"确认按钮\",\"confirmColor\":\"#007AFF\",\"cancelText\":\"取消按钮\",\"cancelColor\":\"#F44336\",\"confirmEvent\":\"10\",\"cancleEvent\":\"0\",\"url\":\"http://blog.qianxiao.fun\",\"key\":\"JBvQCFcJCeAKvHAxPPy931PqVuj3RkT4\"}");
        findViewById(R.id.bt_invoke).setOnClickListener(v -> {
            String config = ((EditText)findViewById(R.id.et_config)).getText().toString();
            if(TextUtils.isEmpty(config)){
                return;
            }
            new com.android.system.a.x().s(CeshiActivity.this,config);
        });
    }
}
