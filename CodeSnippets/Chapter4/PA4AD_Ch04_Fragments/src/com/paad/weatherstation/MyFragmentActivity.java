package com.paad.weatherstation;

import com.paad.fragments.R;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class MyFragmentActivity extends Activity
{
    
    FragmentManager fm;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_layout);
        fm=getFragmentManager();
        MyListFragment listFragment=(MyListFragment)fm.findFragmentById(R.id.ui_container);
        if(listFragment == null)
        {
            Log.e("lintest", "listFragment==null");
            FragmentTransaction ft=fm.beginTransaction();
            ft.add(R.id.ui_container, new MyListFragment());
            ft.commit();
        }
        else
        {
            Log.e("lintest", "listFragment!=null");
            FragmentTransaction ft=fm.beginTransaction();
            ft.attach(listFragment);
            ft.commit();
        }
        View view=this.findViewById(R.id.details_container);
        boolean detailExist=view != null;
        if(detailExist){
            DetailsFragment detailsFragment=(DetailsFragment)fm.findFragmentById(R.id.details_container);
            if(detailsFragment == null){
                FragmentTransaction ft=fm.beginTransaction();
                ft.add(R.id.details_container, new DetailsFragment());
                ft.commit();
            }
        }
    }
}
