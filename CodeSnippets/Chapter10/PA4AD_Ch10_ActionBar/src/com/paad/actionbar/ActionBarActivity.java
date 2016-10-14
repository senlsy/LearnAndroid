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
        // actionbar�����ڽ�����
        // getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ActionBar actionBar=getActionBar();
        // ----------------------------
        // ����������ɫ
        Resources r=getResources();
        Drawable myDrawable=r.getDrawable(R.drawable.gradient_header);
        actionBar.setBackgroundDrawable(myDrawable);
        // ���ñ���
        actionBar.setTitle("ActionBar����");
        actionBar.setSubtitle("�ӱ���");
        // actionBar.setDisplayShowHomeEnabled(false);//����ʾͼ��icon
        // actionBar.setDisplayUseLogoEnabled(false);// ����ʾ�ձ�logo
        actionBar.setHomeButtonEnabled(true);// ����ͼ����Ե��
        actionBar.setDisplayHomeAsUpEnabled(true);// ����ͼ��������ϵ���
        // �����Զ��嵼��view
        actionBar.setDisplayShowCustomEnabled(true);// �����Զ��嵼��view����ʾ
        actionBar.setCustomView(R.layout.my_action_view);// ����Զ��嵼��view����
        // ���������Ĳ˵���Ҳ�п�ݲ˵�
        Button contextMenu=(Button)this.findViewById(R.id.contextMenu);
        registerForContextMenu(contextMenu);
        // ����ʽ�˵�-----------
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
        // �����ͼ����߻ձ�
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
        // menuItem���� menuItemId=1
        MenuItem menuItem=menu.add(groupId, menuItemId, menuItemOrder, "�˵�1");
        menuItem=menu.add(groupId, ++menuItemId, menuItemOrder, "�˵�2");
        // ��ʾ�ɲ���������
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menuItem=menu.add(groupId, ++menuItemId, menuItemOrder, "�˵�3");
        // ��ʾ���Զ���view
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
        menuItem=menu.add(groupId, ++menuItemId, menuItemOrder, "�˵�4");
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        Uri uri=Uri.fromFile(new File(getFilesDir(), "test_1.jpg"));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri.toString());
        ShareActionProvider shareProvider=new ShareActionProvider(this);
        shareProvider.setShareIntent(shareIntent);
        // ��ʾ�ɵ���ϵͳ����
        menuItem.setActionProvider(shareProvider)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem=menu.findItem(0);// ȡ���˵�1
        return true;
    }
}