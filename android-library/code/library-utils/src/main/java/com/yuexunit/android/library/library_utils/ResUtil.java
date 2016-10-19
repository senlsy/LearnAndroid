package com.yuexunit.android.library.library_utils;

import java.lang.reflect.Field;

import com.yuexunit.android.library.library_utils.log.Logger;

import android.content.Context;


public class ResUtil
{
    
    public static int getPluralsRes(Context context, String resName)
    {
        int resId=0;
        String packageName=context.getPackageName();
        try{
            Class clazz=Class.forName((new StringBuilder()).append(packageName).append(".R$plurals").toString());
            resId=getResId(clazz, resName);
        } catch(ClassNotFoundException e){
            Logger.e(e);
        }
        if(resId <= 0)
            resId=context.getResources().getIdentifier(resName, "plurals", packageName);
        if(resId <= 0)
            Logger.w("找不到该布局资源Id for " + resName, true);
        return resId;
    }
    
    public static int getRawRes(Context context, String resName)
    {
        int resId=0;
        String packageName=context.getPackageName();
        try{
            Class clazz=Class.forName((new StringBuilder()).append(packageName).append(".R$raw").toString());
            resId=getResId(clazz, resName);
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        if(resId <= 0)
            resId=context.getResources().getIdentifier(resName, "raw", packageName);
        if(resId <= 0)
            Logger.w("找不到该布局资源Id for " + resName, true);
        return resId;
    }
    
    public static int getColorRes(Context context, String resName)
    {
        int resId=0;
        String packageName=context.getPackageName();
        try{
            Class clazz=Class.forName((new StringBuilder()).append(packageName).append(".R$color").toString());
            resId=getResId(clazz, resName);
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        if(resId <= 0)
            resId=context.getResources().getIdentifier(resName, "color", packageName);
        if(resId <= 0)
            Logger.w("找不到该布局资源Id for " + resName, true);
        return resId;
    }
    
    public static int getIdRes(Context context, String resName)
    {
        int resId=0;
        String packageName=context.getPackageName();
        try{
            Class clazz=Class.forName((new StringBuilder()).append(packageName).append(".R$id").toString());
            resId=getResId(clazz, resName);
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        if(resId <= 0)
            resId=context.getResources().getIdentifier(resName, "id", packageName);
        if(resId <= 0)
            Logger.w("找不到该布局资源Id for " + resName, true);
        return resId;
    }
    
    public static int getStyleRes(Context context, String resName)
    {
        int resId=0;
        String packageName=context.getPackageName();
        try{
            Class clazz=Class.forName((new StringBuilder()).append(packageName).append(".R$style").toString());
            resId=getResId(clazz, resName);
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        if(resId <= 0)
            resId=context.getResources().getIdentifier(resName, "style", packageName);
        if(resId <= 0)
            Logger.w("找不到该布局资源Id for " + resName, true);
        return resId;
    }
    
    public static int getStringArrayRes(Context context, String resName)
    {
        int resId=0;
        String packageName=context.getPackageName();
        try{
            Class clazz=Class.forName((new StringBuilder()).append(packageName).append(".R$array").toString());
            resId=getResId(clazz, resName);
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        if(resId <= 0)
            resId=context.getResources().getIdentifier(resName, "array", packageName);
        if(resId <= 0)
            Logger.w("找不到该布局资源Id for " + resName, true);
        return resId;
    }
    
    public static int getStringRes(Context context, String resName)
    {
        int resId=0;
        String packageName=context.getPackageName();
        try{
            Class clazz=Class.forName((new StringBuilder()).append(packageName).append(".R$string").toString());
            resId=getResId(clazz, resName);
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        if(resId <= 0)
            resId=context.getResources().getIdentifier(resName, "string", packageName);
        if(resId <= 0)
            Logger.w("找不到该布局资源Id for " + resName, true);
        return resId;
    }
    
    public static int getDrawableRes(Context context, String resName)
    {
        int resId=0;
        String packageName=context.getPackageName();
        try{
            Class clazz=Class.forName((new StringBuilder()).append(packageName).append(".R$drawable").toString());
            resId=getResId(clazz, resName);
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        if(resId <= 0)
            resId=context.getResources().getIdentifier(resName, "drawable", packageName);
        if(resId <= 0)
            Logger.w("找不到该布局资源Id for " + resName, true);
        return resId;
    }
    
    public static int getLayoutRes(Context context, String resName)
    {
        int resId=0;
        String packageName=context.getPackageName();
        try{
            Class clazz=Class.forName((new StringBuilder()).append(packageName).append(".R$layout").toString());
            resId=getResId(clazz, resName);
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        if(resId <= 0)
            resId=context.getResources().getIdentifier(resName, "layout", packageName);
        if(resId <= 0)
            Logger.w("找不到该布局资源Id for " + resName, true);
        return resId;
    }
    
    public static int getResId(Class clazz, String resName)
    {
        int resId=0;
        if(resName != null){
            Field field;
            try{
                field=clazz.getField(resName);
                field.setAccessible(true);
                resId=((Integer)field.get(null)).intValue();
            } catch(Exception e){
                e.printStackTrace();
                String lowResName=resName.toLowerCase();
                try
                {
                    Field field1=clazz.getField(lowResName);
                    field1.setAccessible(true);
                    resId=((Integer)field1.get(null)).intValue();
                } catch(Exception e2)
                {
                    resId=0;
                }
            }
        }
        return resId;
    }
}
