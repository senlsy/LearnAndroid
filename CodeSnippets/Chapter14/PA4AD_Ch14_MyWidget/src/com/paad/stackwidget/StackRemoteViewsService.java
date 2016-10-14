package com.paad.stackwidget;

import java.util.ArrayList;

import com.paad.PA4AD_Ch14_MyWidget.R;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;


public class StackRemoteViewsService extends RemoteViewsService
{
    
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(getApplicationContext(), intent);
    }
    
    class StackRemoteViewsFactory implements RemoteViewsFactory
    {
        
        private ArrayList<String> myWidgetText=new ArrayList<String>();
        private Context context;
        private Intent intent;
        private int widgetId;
        
        public StackRemoteViewsFactory(Context context, Intent intent){
            this.context=context;
            this.intent=intent;
            widgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        
        // 建立任何数据连接或者Cursor查询数据，都应该 推迟onDataSetChanged()或者 getViewAt()中执行.
        // 这个方法执行超过20秒会报一个ANR错误
        public void onCreate() {
            myWidgetText.add("The");
            myWidgetText.add("quick");
            myWidgetText.add("brown");
            myWidgetText.add("fox");
            myWidgetText.add("jumps");
            myWidgetText.add("over");
            myWidgetText.add("the");
            myWidgetText.add("lazy");
            myWidgetText.add("droid");
        }
        
        // 当显示的集合数据有更新时会触发这个方法，可以使用AppWidgetManager.notifyAppWidgetViewDataChanged来触发这个方法
        public void onDataSetChanged() {
            Log.e("lintest", "StackRemoteViewsFactory---onDataSetChanged");
        }
        
        // 返回集合的数目
        public int getCount() {
            return myWidgetText.size();
        }
        
        // 如果每个item提供的唯一_id是稳定的，即它们不会在运行时改变，就返回true
        public boolean hasStableIds() {
            return false;
        }
        
        // 返回item的_id
        public long getItemId(int index) {
            return index;
        }
        
        // item的样式类型数目
        public int getViewTypeCount() {
            return 1;
        }
        
        // 可选的指定一个“加载”view作为显示，返回null则使用默认的view.
        public RemoteViews getLoadingView() {
            return null;
        }
        
        // 填充每一条item
        public RemoteViews getViewAt(int index) {
            RemoteViews rv=new RemoteViews(context.getPackageName(), R.layout.stackwidget_item);
            // Populate the view from the underlying data.
            rv.setTextViewText(R.id.widget_title_text, myWidgetText.get(index));
            rv.setTextViewText(R.id.widget_text, "View Number: " + String.valueOf(index));
            // 返回这个Intent，填充AppWidgetProvider创建的PendingIntent
            Intent fillInIntent=new Intent();
            fillInIntent.putExtra(Intent.EXTRA_TEXT, myWidgetText.get(index));
            rv.setOnClickFillInIntent(R.id.widget_title_text, fillInIntent);
            return rv;
        }
        
        // 用于关闭任何数据链接，cursor，释放资源
        public void onDestroy() {
            myWidgetText.clear();
        }
    }
}
