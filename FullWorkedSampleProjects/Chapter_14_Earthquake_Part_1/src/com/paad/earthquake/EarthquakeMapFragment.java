package com.paad.earthquake;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paad.earthquake.receiver.EarthquakeProvider;


public class EarthquakeMapFragment extends Fragment
{
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}