package com.paad.actionbar;

import java.io.File;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ShareActionProvider;
import android.widget.TextView;


public class ActionBarActivity extends Activity
{
    
    protected static final String TAG="ActionBarTabActivity";
    
    TextView myView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // actionbar悬浮在界面上
        // getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ActionBar actionBar=getActionBar();
        // ----------------------------
        // 更换背景颜色
        Resources r=getResources();
        Drawable myDrawable=r.getDrawable(R.drawable.gradient_header);
        actionBar.setBackgroundDrawable(myDrawable);
        // 设置标题
        actionBar.setTitle("ActionBar标题");
        actionBar.setSubtitle("子标题");
        // actionBar.setDisplayShowHomeEnabled(false);//不显示图标icon
        // actionBar.setDisplayUseLogoEnabled(false);// 不显示徽标logo
        actionBar.setHomeButtonEnabled(true);// 设置图标可以点击
        actionBar.setDisplayHomeAsUpEnabled(true);// 设置图标可以向上导航
        // 设置自定义导航view
        actionBar.setDisplayShowCustomEnabled(true);// 设置自定义导航view可显示
        actionBar.setCustomView(R.layout.my_action_view);// 填充自定义导航view布局
        // 弹出上下文菜单，也叫快捷菜单
        Button contextMenu=(Button)this.findViewById(R.id.contextMenu);
        registerForContextMenu(contextMenu);
        // 弹出式菜单-----------
        Button popMenu=(Button)this.findViewById(R.id.popMenu);
        final PopupMenu popupMenu=new PopupMenu(this, popMenu);
        popMenu.setOnClickListener(new OnClickListener(){
            
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });
        popupMenu.inflate(R.menu.my_popup_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
            
            public boolean onMenuItemClick(MenuItem item) {
                Log.e("lintest", "popupMenu itemid=" + item.getItemId());
                return true;
            }
        });
        // --------------------
    }
    
    /**
     * Listing 10-6: Handling application icon clicks Listing 10-18: Handling
     * Menu Item selections
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Log.e("lintest", "menu itemid=" + item.getItemId());
        // 如果是图标或者徽标
        // itemid==android.R.id.home
        return true;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        int groupId=0;
        int menuItemId=0;
        int menuItemOrder=0;
        // Create the Menu Item and keep a reference to it
        // menuItem对象 menuItemId=1
        MenuItem menuItem=menu.add(groupId, menuItemId, menuItemOrder, "菜单1");
        menuItem=menu.add(groupId, ++menuItemId, menuItemOrder, "菜单2");
        // 显示成操作栏操作
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menuItem=menu.add(groupId, ++menuItemId, menuItemOrder, "菜单3");
        // 显示成自定义view
        menuItem.setActionView(R.layout.my_action_view)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM |
                                      MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        View myView=menuItem.getActionView();
        Button button=(Button)myView.findViewById(R.id.goButton);
        button.setOnClickListener(new OnClickListener(){
            
            public void onClick(View v) {
                Log.d(TAG, "ActionView Button Pressed");
            }
        });
        // -------------------------------------
        menuItem=menu.add(groupId, ++menuItemId, menuItemOrder, "菜单4");
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        Uri uri=Uri.fromFile(new File(getFilesDir(), "test_1.jpg"));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri.toString());
        ShareActionProvider shareProvider=new ShareActionProvider(this);
        shareProvider.setShareIntent(shareIntent);
        // 显示成调用系统分享
        menuItem.setActionProvider(shareProvider)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem=menu.findItem(0);// 取出菜单1
        return true;
    }
}