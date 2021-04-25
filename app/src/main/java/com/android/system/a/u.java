package com.android.system.a;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class u {
    public void j(Context c, String u){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(u));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        c.startActivity(intent);
    }
}
