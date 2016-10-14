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
        
        // �����κ��������ӻ���Cursor��ѯ���ݣ���Ӧ�� �Ƴ�onDataSetChanged()���� getViewAt()��ִ��.
        // �������ִ�г���20��ᱨһ��ANR����
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
        
        // ����ʾ�ļ��������и���ʱ�ᴥ���������������ʹ��AppWidgetManager.notifyAppWidgetViewDataChanged�������������
        public void onDataSetChanged() {
            Log.e("lintest", "StackRemoteViewsFactory---onDataSetChanged");
        }
        
        // ���ؼ��ϵ���Ŀ
        public int getCount() {
            return myWidgetText.size();
        }
        
        // ���ÿ��item�ṩ��Ψһ_id���ȶ��ģ������ǲ���������ʱ�ı䣬�ͷ���true
        public boolean hasStableIds() {
            return false;
        }
        
        // ����item��_id
        public long getItemId(int index) {
            return index;
        }
        
        // item����ʽ������Ŀ
        public int getViewTypeCount() {
            return 1;
        }
        
        // ��ѡ��ָ��һ�������ء�view��Ϊ��ʾ������null��ʹ��Ĭ�ϵ�view.
        public RemoteViews getLoadingView() {
            return null;
        }
        
        // ���ÿһ��item
        public RemoteViews getViewAt(int index) {
            RemoteViews rv=new RemoteViews(context.getPackageName(), R.layout.stackwidget_item);
            // Populate the view from the underlying data.
            rv.setTextViewText(R.id.widget_title_text, myWidgetText.get(index));
            rv.setTextViewText(R.id.widget_text, "View Number: " + String.valueOf(index));
            // �������Intent�����AppWidgetProvider������PendingIntent
            Intent fillInIntent=new Intent();
            fillInIntent.putExtra(Intent.EXTRA_TEXT, myWidgetText.get(index));
            rv.setOnClickFillInIntent(R.id.widget_title_text, fillInIntent);
            return rv;
        }
        
        // ���ڹر��κ��������ӣ�cursor���ͷ���Դ
        public void onDestroy() {
            myWidgetText.clear();
        }
    }
}
