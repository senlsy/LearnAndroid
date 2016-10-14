package com.paad.actionbar;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;


public class ActionBarTabActivity extends Activity
{
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabmain);
        ActionBar actionBar=getActionBar();
        // œ‘ æ≥…Tabµº∫Ω
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        Tab tabOne=actionBar.newTab();
        tabOne.setText("First Tab")
              .setIcon(R.drawable.ic_launcher)
              .setContentDescription("Tab the First")
              .setTabListener(new TabListener<MyFragment>(this, R.id.fragmentContainer, MyFragment.class));
        Tab tabSecond=actionBar.newTab();
        tabSecond.setText("Second Tab")
                 .setIcon(R.drawable.icon)
                 .setContentDescription("Tab the Second")
                 .setTabListener(new TabListener<MyFragment>(this, R.id.fragmentContainer, MyFragment.class));
        actionBar.addTab(tabOne);
        actionBar.addTab(tabSecond);
    }
    
    public static class TabListener<T extends Fragment> implements ActionBar.TabListener
    {
        
        private MyFragment fragment;
        
        private Activity activity;
        
        private Class<T> fragmentClass;
        
        private int fragmentContainer;
        
        public TabListener(Activity activity, int fragmentContainer, Class<T> fragmentClass){
            this.activity=activity;
            this.fragmentContainer=fragmentContainer;
            this.fragmentClass=fragmentClass;
        }
        
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            if(fragment == null){
                String fragmentName=fragmentClass.getName();
                fragment=(MyFragment)Fragment.instantiate(activity, fragmentName);
                ft.add(fragmentContainer, fragment, null);
                fragment.setFragmentText(tab.getText());
            }
            else{
                ft.attach(fragment);
            }
        }
        
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if(fragment != null){
                ft.detach(fragment);
            }
        }
        
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }
    }
}