package com.paad.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * MOVED TO PA4AD_Ch04_Seasons
 */
public class SeasonFragment extends Fragment
{
    
    public interface OnSeasonSelectedListener
    {
        
        public void onSeasonSelected();
    }
    
    private OnSeasonSelectedListener onSeasonSelectedListener;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        return inflater.inflate(R.layout.season_fragment, container, false);
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            onSeasonSelectedListener=(OnSeasonSelectedListener)activity;
        } catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement OnSeasonSelectedListener");
        }
    }
    
    private void callBackAct() {
        onSeasonSelectedListener.onSeasonSelected();
    }
}
