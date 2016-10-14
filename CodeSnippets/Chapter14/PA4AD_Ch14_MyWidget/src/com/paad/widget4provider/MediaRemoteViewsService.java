package com.paad.widget4provider;

import com.paad.PA4AD_Ch14_MyWidget.R;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;


public class MediaRemoteViewsService extends RemoteViewsService
{
    
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MediaRemoteViewsFactory(getApplicationContext());
    }
    
    class MediaRemoteViewsFactory implements RemoteViewsFactory
    {
        
        private Context context;
        private ContentResolver cr;
        private Cursor c;
        
        public MediaRemoteViewsFactory(Context context){
            this.context=context;
            cr=context.getContentResolver();
        }
        
        public void onCreate() {
            // 执行一个查询返回要显示的数据.
            // 任何辅助的查询或者解码等耗时的操作应该放在 onDataSetChanged中处理
            c=cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, null, null, null, null);
        }
        
        public void onDataSetChanged() {
            // 任何辅助的查询或者解码等耗时的操作可以放在这里处理
            // 只有这个方法执行后widget才会更新
        }
        
        public int getCount() {
            if(c != null)
                return c.getCount();
            else
                return 0;
        }
        
        public long getItemId(int index) {
            // Return the unique ID associated with a particular item.
            if(c != null)
                return c.getInt(c
                                 .getColumnIndex(MediaStore.Images.Thumbnails._ID));
            else
                return index;
        }
        
        public RemoteViews getViewAt(int index) {
            c.moveToPosition(index);
            int idIdx=c.getColumnIndex(MediaStore.Images.Thumbnails._ID);
            String id=c.getString(idIdx);
            Uri uri=Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + id);
            RemoteViews rv=new RemoteViews(context.getPackageName(), R.layout.mediawidget_item);
            rv.setImageViewUri(R.id.widget_media_thumbnail, uri);
            Intent fillInIntent=new Intent();
            fillInIntent.setData(uri);
            rv.setOnClickFillInIntent(R.id.widget_media_thumbnail, fillInIntent);
            return rv;
        }
        
        public int getViewTypeCount() {
            // The number of different view definitions to use.
            // For Content Providers, this will almost always be 1.
            return 1;
        }
        
        public boolean hasStableIds() {
            // Content Provider IDs should be unique and permanent.
            return true;
        }
        
        public void onDestroy() {
            // Close the result Cursor.
            c.close();
        }
        
        public RemoteViews getLoadingView() {
            // Use the default loading view.
            return null;
        }
    }
}
