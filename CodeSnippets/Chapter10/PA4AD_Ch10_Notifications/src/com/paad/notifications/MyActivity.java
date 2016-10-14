package com.paad.notifications;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RemoteViews;


public class MyActivity extends Activity implements OnClickListener
{
    
    private static final String BUTTON_CLICK_ACTION="com.paad.notifications.action.BUTTON_CLICK";
    private static final int NOTIFICATION_REF=1;
    BroadcastReceiver receiver;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.findViewById(R.id.button1).setOnClickListener(this);
        this.findViewById(R.id.button2).setOnClickListener(this);
        IntentFilter filter=new IntentFilter(BUTTON_CLICK_ACTION);
        receiver=new BroadcastReceiver(){
            
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("lintest", "接受到通知");
            }
        };
        registerReceiver(receiver, filter);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
    
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button1)
        {
             Notification notifycation=simpleNotification();
            // Notification notifycation=customNotificationWindowNotification();
            // Notification notifycation=customLayoutNotification();
            // Notification notifycation=customNotificationWindowNotification();
//            Notification notifycation=onGoingNotification();
            // Notification notifycation=autoCancelNotification();
            triggerNotification(notifycation);
        }
        else{
            cancelNotification();
        }
    }
    
    // 3.0之前构造Notification
    private Notification simpleNotification() {
        int icon=R.drawable.icon;// 设置图标
        String tickerText="滚动文本";// 滚动文本
        long when=System.currentTimeMillis();// 触发时间
        Notification notification=new Notification(icon, tickerText, when);
        // notification.number=3;// 数字覆盖
        // 设置默认声音和振动和通知灯(记得加权限)
        // notification.defaults=Notification.DEFAULT_SOUND
        // | Notification.DEFAULT_VIBRATE
        // | Notification.DEFAULT_LIGHTS;
        // 设置自定义通知声音---------------
        // Uri
        // soundUir=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // notification.sound=soundUir;
        // 设置通知振动------------
        // long[] virbate=new long[]{1000,1000,1000};
        // notification.vibrate=virbate;
        // 设置通知Led颜色------------
        // notification.flags=notification.flags |
        // Notification.FLAG_SHOW_LIGHTS;
        // notification.ledARGB=Color.GREEN;
        // notification.ledOffMS=0;
        // notification.ledOnMS=1;
        // 触发PendingIntent-----------
        // Intent newIntent=new Intent(BUTTON_CLICK_ACTION);
        // PendingIntent
        // pendingIntent=PendingIntent.getBroadcast(MyActivity.this, 2,
        // newIntent, 0);
        // notification.contentIntent=pendingIntent;
        return notification;
    }
    
    // 3.0之后引入Notifycation Builder来构造Notification。
    private Notification notificationBuilder() {
        Intent newIntent=new Intent(BUTTON_CLICK_ACTION);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(MyActivity.this, 2, newIntent, 0);
        Notification.Builder builder=new Notification.Builder(MyActivity.this);
        builder.setSmallIcon(R.drawable.ic_launcher)// 设置图标
        .setTicker("滚动文本")// 设置滚动文本
        .setWhen(System.currentTimeMillis())// 触发时间
        .setDefaults(Notification.DEFAULT_SOUND |
                     Notification.DEFAULT_VIBRATE |
                     Notification.DEFAULT_LIGHTS
               )
               .setSound(RingtoneManager.getDefaultUri(
                                        RingtoneManager.TYPE_NOTIFICATION))
               .setVibrate(new long[]{1000,1000,1000,1000,1000})
               .setLights(Color.RED, 1, 0)
               .setContentIntent(pendingIntent)
               .setNumber(3);
        Notification notification=builder.getNotification();
        return notification;
    }
    
    // Notifycation Builder来构建notifycation UI（系统本身带有几个现成的样式）
    private Notification customLayoutNotification() {
        // 通知托盘中点击通知触发的PendingIntent。
        Intent newIntent=new Intent(BUTTON_CLICK_ACTION);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(MyActivity.this, 2, newIntent, 0);
        Notification.Builder builder=new Notification.Builder(MyActivity.this);
        Bitmap myIconBitmap=null;
        builder.setSmallIcon(R.drawable.ic_launcher)
               .setTicker("滚动文本")
               .setWhen(System.currentTimeMillis())
               .setContentTitle("标题")
               .setContentText("子标题")
               .setContentInfo("信息内容")
               .setLargeIcon(myIconBitmap)
               .setContentIntent(pendingIntent);
        return builder.getNotification();
    }
    
    // 自定义notifycation UI
    private Notification customNotificationWindowNotification() {
        Notification.Builder builder=new Notification.Builder(MyActivity.this);
        RemoteViews myRemoteView=new RemoteViews(this.getPackageName(), R.layout.my_notification_layout);
        builder.setSmallIcon(R.drawable.ic_launcher)
               .setTicker("Notification")
               .setWhen(System.currentTimeMillis())
               .setContentTitle("Progress")
               .setProgress(100, 50, false)
               .setContent(myRemoteView);// 设置自定义notifycation UI
        Notification notification=builder.getNotification();
        // 为remoteView设置内容---------
        notification.contentView.setImageViewResource(R.id.status_icon, R.drawable.icon);
        notification.contentView.setTextViewText(R.id.status_text, "Current Progress:");
        notification.contentView.setProgressBar(R.id.status_progress, 100, 50, false);
        // 为remoteView上的控件设置点击事件----------
        Intent newIntent=new Intent(BUTTON_CLICK_ACTION);
        PendingIntent newPendingIntent=PendingIntent.getBroadcast(MyActivity.this, 2, newIntent, 0);
        notification.contentView.setOnClickPendingIntent(R.id.status_progress, newPendingIntent);
        return notification;
    }
    
    // 配置持续性的notifycation或连续
    private Notification onGoingNotification() {
        Notification.Builder builder=new Notification.Builder(MyActivity.this);
        builder.setSmallIcon(R.drawable.notification_icon)
               .setTicker("滚动文本")
               .setWhen(System.currentTimeMillis())
               .setContentTitle("通知标题")
               .setProgress(100, 50, false)
               .setOngoing(true);// 设置持续性
        Notification notifycation=builder.getNotification();
        // 设置持续性也可以由flags设置
        // notifycation.flags=notifycation.flags|Notification.FLAG_ONGOING_EVENT;
        // 设置notifycation为连续性，只有通过flags设置
        // notifycation.flags=notifycation.flags|Notification.FLAG_INSISTENT;
        return notifycation;
    }
    
    // 配置一个点击notifycation后自动取消的notifycation
    private Notification autoCancelNotification() {
        Notification.Builder builder=new Notification.Builder(MyActivity.this);
        Intent newIntent=new Intent(BUTTON_CLICK_ACTION);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(MyActivity.this, 2, newIntent, 0);
        Bitmap myIconBitmap=null;
        builder.setSmallIcon(R.drawable.ic_launcher)
               .setTicker("Notification")
               .setWhen(System.currentTimeMillis())
               .setContentTitle("Title")
               .setContentText("Subtitle")
               .setContentInfo("Info")
               .setLargeIcon(myIconBitmap)
               .setContentIntent(pendingIntent)
               .setAutoCancel(true);// 设置点击notifycation后，自动取消
        return builder.getNotification();
    }
    
    // 触发一个notifycation
    private void triggerNotification(Notification notifycation) {
        String svc=Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager=(NotificationManager)getSystemService(svc);
        notificationManager.notify(NOTIFICATION_REF, notifycation);
    }
    
    // 取消一个notifycation
    private void cancelNotification() {
        String svc=Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager=(NotificationManager)getSystemService(svc);
        notificationManager.cancel(NOTIFICATION_REF);
    }
}