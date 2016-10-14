package com.paad.earthquake;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class EarthquakeMapFragment extends Fragment
{
    
    Earthquake act=null;
    
    @Override
    public void onAttach(Activity activity) {
        Log.e("", this.getClass().getSimpleName() + " onAttach ");
        super.onAttach(activity);
        if(activity != null && activity instanceof Earthquake)
            act=(Earthquake)activity;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("", this.getClass().getSimpleName() + " onCreate ");
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("", this.getClass().getSimpleName() + " onCreateView ");
        View view=inflater.inflate(R.layout.map_fragment, container, false);
        TextView text1=(TextView)view.findViewById(R.id.textView1);
        TextView text2=(TextView)view.findViewById(R.id.textView2);
        if(act != null && act.i % 2 == 0)
        {
            act.i=act.i + 1;
            text1.setTextColor(0xff00ff00);
            text1.setText("google maps view");
        }
        else if((act != null && act.i % 2 == 1))
        {
            act.i=act.i + 1;
            text2.setTextColor(0xff00ffff);
            text2.setText("baidu maps view");
        }
        return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e("", this.getClass().getSimpleName() + " onActivityCreated ");
        super.onActivityCreated(savedInstanceState);
    }
    
    @Override
    public void onStart() {
        Log.e("", this.getClass().getSimpleName() + " onStart ");
        super.onStart();
    }
    
    @Override
    public void onResume() {
        Log.e("", this.getClass().getSimpleName() + " onResume ");
        super.onResume();
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e("", this.getClass().getSimpleName() + " onSaveInstanceState ");
        super.onSaveInstanceState(outState);
    }
    
    @Override
    public void onPause() {
        Log.e("", this.getClass().getSimpleName() + " onPause ");
        super.onPause();
    }
    
    @Override
    public void onStop() {
        Log.e("", this.getClass().getSimpleName() + " onStop ");
        super.onStop();
    }
    
    @Override
    public void onDestroyView() {
        Log.e("", this.getClass().getSimpleName() + " onDestroyView ");
        super.onDestroyView();
    }
    
    @Override
    public void onDestroy() {
        Log.e("", this.getClass().getSimpleName() + " onDestroy ");
        super.onDestroy();
    }
    
    @Override
    public void onDetach() {
        Log.e("", this.getClass().getSimpleName() + " onDetacch ");
        super.onDetach();
    }
}
