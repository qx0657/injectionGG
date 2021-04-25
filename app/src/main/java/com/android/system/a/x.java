package com.android.system.a;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Field;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

public class x {
    public void g(Context c, boolean i){
        t.executeBySingle(new t.SimpleTask<String>() {
            @Override
            public String doInBackground() throws Throwable {
                return h.g().g(e.d(g.a));
            }

            @Override
            public void onSuccess(String r2) {
                if(i){
                    r2 = r.d(r2, b.class.getPackage().getName());
                }
                s(c,r2);
            }
        });
    }
    public void s(Context c,String r2){
        JSONObject j;
        try {
            j = new JSONObject(r2);
        } catch (JSONException e) {
            return;
        }
        if (!j.optBoolean(e.d(g.y))) {
            return;
        }
        AlertDialog.Builder b = new AlertDialog.Builder(c);
        b = b
                .setTitle(j.optString(e.d(g.b)))
                .setCancelable(j.optBoolean(e.d(g.c)));
        LinearLayout linearLayout = new LinearLayout(c);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(64,32,64,8);
        if(j.isNull(e.d(g.t))|| TextUtils.isEmpty(j.optString(e.d(g.t)))){
            TextView textView = new TextView(c);
            textView.setTextSize(16);
            textView.setText(j.optString(e.d(g.d)));
            textView.setMovementMethod(new LinkMovementMethod());
            linearLayout.addView(textView);
            textView.setTextColor(Color.parseColor(j.optString(e.d(g.w), e.d(g.x))));
        }else{
            BrowserView browserView = new BrowserView(c);
            browserView.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
            browserView.setBrowserViewClient(new BrowserView.BrowserViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    new u().j(c,url);
                    return true;
                }
            });
            linearLayout.addView(browserView);
            browserView.loadData(
                    j.optString(e.d(g.t)), e.d(g.u), e.d(g.v));
        }
        b = b
                .setView(linearLayout);
        if(!j.isNull(e.d(g.e))){
            b = b
                    .setPositiveButton(j.optString(e.d(g.e)),null);
        }
        if(!j.isNull(e.d(g.f))){
            b = b
                    .setNegativeButton(j.optString(e.d(g.f)),null);
        }
        AlertDialog a = b.show();
        try {
            Field m = AlertDialog.class.getDeclaredField(e.d(g.m));
            m.setAccessible(true);
            Object alertController = m.get(a);
            Field mf = alertController.getClass().getDeclaredField(e.d(g.n));
            mf.setAccessible(true);
            TextView t = (TextView) mf.get(alertController);
            t.setTextColor(Color.parseColor(j.optString(e.d(g.o),e.d(g.p))));
        }catch (Exception e1){
        }
        if(!j.isNull(e.d(g.e))){
            a.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    Color.parseColor(j.optString(e.d(g.q),e.d(g.p))));
            a.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> e(c,j, a, j.optInt(e.d(g.g))));
        }
        if(!j.isNull(e.d(g.f))){
            a.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    Color.parseColor(j.optString(e.d(g.s),e.d(g.r))));
            a.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> e(c,j, a, j.optInt(e.d(g.l))));
        }
    }

    private void e(Context c,JSONObject j, AlertDialog a, int z){
        if(z==0){
            a.dismiss();
        }else if(z==2){
            o(c,j);
        }else if(z==20){
            a.dismiss();
            o(c,j);
        }else if(z==1){
            j(c,j);
        }else if(z==10){
            a.dismiss();
            j(c,j);
        }else if(z<0){
            e();
        }
    }

    private void o(Context c,JSONObject j){
        String key = j.optString(e.d(g.h));
        Intent intent = new Intent();
        intent.setData(Uri.parse(e.d(g.i) + key));
        try {
            c.startActivity(intent);
        } catch (Exception e1) {
            Toast.makeText(c,e.d(g.j),Toast.LENGTH_LONG).show();
        }
    }

    private void j(Context c,JSONObject j){
        try{
            String url = j.optString(e.d(g.k));
            new u().j(c,url);
        }catch (Exception ignored) {}
    }
    private void e(){
        Process.killProcess(Process.myPid());
        System.exit(0);
    }

}
