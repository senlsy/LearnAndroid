package com.paad.earthquake;

import com.paad.earthquake.preferences.FragmentPreferences;
import com.paad.earthquake.preferences.PreferencesActivity;
import com.paad.earthquake.service.EarthquakeUpdateService;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;


public class Earthquake extends Activity
{
    
    TabListener<EarthquakeListFragment> listTabListener;
    TabListener<EarthquakeMapFragment> mapTabListener;
    public static int i=0;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        updateFromPreferences();
        // ----------------
        SearchManager searchManager=(SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo=searchManager.getSearchableInfo(getComponentName());
        SearchView searchView=(SearchView)findViewById(R.id.searchView);
        searchView.setSearchableInfo(searchableInfo);
        // ----------------------
        ActionBar actionBar=getActionBar();
        View fragmentContainer=findViewById(R.id.EarthquakeFragmentContainer);
        boolean tabletLayout=fragmentContainer == null;
        if( !tabletLayout){
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.setDisplayShowTitleEnabled(false);
            Tab listTab=actionBar.newTab();
            listTabListener=new TabListener<EarthquakeListFragment>(this, R.id.EarthquakeFragmentContainer, EarthquakeListFragment.class);
            listTab.setText("List").setContentDescription("List of earthquakes").setTabListener(listTabListener);
            actionBar.addTab(listTab);
            Tab mapTab=actionBar.newTab();
            mapTabListener=new TabListener<EarthquakeMapFragment>(this, R.id.EarthquakeFragmentContainer, EarthquakeMapFragment.class);
            mapTab.setText("Map").setContentDescription("Map of earthquakes").setTabListener(mapTabListener);
            actionBar.addTab(mapTab);
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // 保存当前用户所处的tab的位置，以防被运行时充值时能正确恢复
        View fragmentContainer=findViewById(R.id.EarthquakeFragmentContainer);
        boolean tabletLayout=fragmentContainer == null;
        if( !tabletLayout){
            // Save the current Action Bar tab selection
            int actionBarIndex=getActionBar().getSelectedTab().getPosition();
            SharedPreferences.Editor editor=getPreferences(Activity.MODE_PRIVATE).edit();
            editor.putInt(ACTION_BAR_INDEX, actionBarIndex);
            editor.apply();
            Log.e("lintest", "onSaveInstanceState----" + actionBarIndex);
            // Detach each of the Fragments
            FragmentTransaction ft=getFragmentManager().beginTransaction();
            if(mapTabListener.fragment != null)
                ft.detach(mapTabListener.fragment);
            if(listTabListener.fragment != null)
                ft.detach(listTabListener.fragment);
            ft.commit();
        }
        super.onSaveInstanceState(outState);
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // 恢复用户保存tab的位置
        View fragmentContainer=findViewById(R.id.EarthquakeFragmentContainer);
        boolean tabletLayout=fragmentContainer == null;
        if( !tabletLayout){
            listTabListener.fragment=getFragmentManager().findFragmentByTag(EarthquakeListFragment.class.getName());
            mapTabListener.fragment=getFragmentManager().findFragmentByTag(EarthquakeMapFragment.class.getName());
            SharedPreferences sp=getPreferences(Activity.MODE_PRIVATE);
            int actionBarIndex=sp.getInt(ACTION_BAR_INDEX, 0);
            getActionBar().setSelectedNavigationItem(actionBarIndex);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 恢复用户保存tab的位置
        View fragmentContainer=findViewById(R.id.EarthquakeFragmentContainer);
        boolean tabletLayout=fragmentContainer == null;
        if( !tabletLayout){
            SharedPreferences sp=getPreferences(Activity.MODE_PRIVATE);
            int actionBarIndex=sp.getInt(ACTION_BAR_INDEX, 0);
            getActionBar().setSelectedNavigationItem(actionBarIndex);
        }
    }
    
    static final private int MENU_PREFERENCES=Menu.FIRST + 1;
    static final private int MENU_UPDATE=Menu.FIRST + 2;
    private static final int SHOW_PREFERENCES=1;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case (MENU_PREFERENCES):
            {
                Class c=Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? PreferencesActivity.class : FragmentPreferences.class;
                Intent i=new Intent(this, c);
                startActivityForResult(i, SHOW_PREFERENCES);
                return true;
            }
        }
        return false;
    }
    
    public int minimumMagnitude=0;
    public boolean autoUpdateChecked=false;
    public int updateFreq=0;
    
    private void updateFromPreferences() {
        Context context=getApplicationContext();
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
        minimumMagnitude=Integer.parseInt(prefs.getString(PreferencesActivity.PREF_MIN_MAG, "3"));
        updateFreq=Integer.parseInt(prefs.getString(PreferencesActivity.PREF_UPDATE_FREQ, "60"));
        autoUpdateChecked=prefs.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE, false);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SHOW_PREFERENCES){
            updateFromPreferences();
            startService(new Intent(this, EarthquakeUpdateService.class));
        }
    }
    
    private static String ACTION_BAR_INDEX="ACTION_BAR_INDEX";
    
    public static class TabListener<T extends Fragment> implements ActionBar.TabListener
    {
        
        // ft.detach(fragment)只是将fragment分离出显示的父容器，fragment还是绑定于当前的fragmentMamager管理，
        // fragment的生命周期只调用到了onDestoryView（）。可以使用ft.attch（fragment）绑定回来，
        // fragment的生命周期从onCreateView（）开始调用。
        // 如果想用ft.add（）将detach的fragment添加回来将不会触发任何生命周期，也无法重新显示在布局容器里,
        // 因为fragment并未分离开过fragmentMamager
        // 而ft.remove(fragment)则是将fragment分离出了fragmentMamager的管理，
        // 是不能通过ft.attch（fragment）将remove的fragment绑定回来，只能通过执行ft.add()添加回来，
        // fragment执行了完整的生命周期
        // 绑定分离于FragmentMamager等意于绑定分离与Activity，不能忘记ft.commit(),来提shiwu交哦！
        private Fragment fragment;
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
                fragment=Fragment.instantiate(activity, fragmentName);
                ft.add(fragmentContainer, fragment, fragmentName);
            }
            else
            {
                ft.attach(fragment);
                // ft.add(fragmentContainer, fragment);
            }
        }
        
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if(fragment != null)
            {
                ft.detach(fragment);
                // ft.remove(fragment);
                // ft.isAddToBackStackAllowed();
                // ft.addToBackStack(null);
            }
        }
        
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            // if(fragment != null)
            // ft.attach(fragment);
        }
    }
}