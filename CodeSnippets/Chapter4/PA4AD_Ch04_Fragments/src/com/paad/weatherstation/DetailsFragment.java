package com.paad.weatherstation;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paad.fragments.R;


public class DetailsFragment extends Fragment
{
    
    public DetailsFragment(){
    }
    
    @Override
    public void onAttach(Activity activity) {
        Log.e("lintest", this.getClass().getSimpleName() + " onAttach ");
        super.onAttach(activity);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("lintest", this.getClass().getSimpleName() + " onCreate ");
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("lintest", this.getClass().getSimpleName() + " onCreateView ");
        return inflater.inflate(R.layout.details_fragment, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e("lintest", this.getClass().getSimpleName() + " onActivityCreated ");
        super.onActivityCreated(savedInstanceState);
    }
    
    @Override
    public void onStart() {
        Log.e("lintest", this.getClass().getSimpleName() + " onStart ");
        super.onStart();
    }
    
    @Override
    public void onResume() {
        Log.e("lintest", this.getClass().getSimpleName() + " onResume ");
        super.onResume();
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e("lintest", this.getClass().getSimpleName() + " onSaveInstanceState ");
        super.onSaveInstanceState(outState);
    }
    
    @Override
    public void onPause() {
        Log.e("lintest", this.getClass().getSimpleName() + " onPause ");
        super.onPause();
    }
    
    @Override
    public void onStop() {
        Log.e("lintest", this.getClass().getSimpleName() + " onStop ");
        super.onStop();
    }
    
    @Override
    public void onDestroyView() {
        Log.e("lintest", this.getClass().getSimpleName() + " onDestroyView ");
        super.onDestroyView();
    }
    
    @Override
    public void onDestroy() {
        Log.e("lintest", this.getClass().getSimpleName() + " onDestroy ");
        super.onDestroy();
    }
    
    @Override
    public void onDetach() {
        Log.e("lintest", this.getClass().getSimpleName() + " onDetacch ");
        super.onDetach();
    }
}