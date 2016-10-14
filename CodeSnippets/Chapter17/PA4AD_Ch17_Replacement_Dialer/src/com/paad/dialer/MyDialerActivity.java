package com.paad.dialer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class MyDialerActivity extends Activity
{
    TextView phone_no;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        phone_no=(TextView)this.findViewById(R.id.phone_no);
        this.findViewById(R.id.buttonCall).setOnClickListener(new OnClickListener(){
            
            @Override
            public void onClick(View v) {
                Intent whoyougonnacall=new Intent(Intent.ACTION_CALL, Uri.parse("tel:555-2368"));
                startActivity(whoyougonnacall);
            }
        });
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri=intent.getData();
        if(uri!=null)
        {
           String string= uri.getAuthority();
           phone_no.setText(string);
        }
    }
    
    
    
}