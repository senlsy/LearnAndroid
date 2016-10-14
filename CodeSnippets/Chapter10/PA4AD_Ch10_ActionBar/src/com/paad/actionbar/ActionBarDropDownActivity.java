package com.paad.actionbar;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;


public class ActionBarDropDownActivity extends Activity
{
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dropdownmain);
        ActionBar actionBar=getActionBar();
        // 显示成下拉列表
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ArrayAdapter dropDownAdapter=ArrayAdapter.createFromResource(this,
                                                                     R.array.my_dropdown_values,
                                                                     android.R.layout.simple_list_item_1);
        actionBar.setListNavigationCallbacks(dropDownAdapter,
                                             new OnNavigationListener(){
                                                 
                                                 public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                                                     Log.e("lintest", "itemPosition=" + itemPosition + ";itemId=" + itemId);
                                                     return true;
                                                 }
                                             });
    }
}