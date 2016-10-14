package com.paad.todolist;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;


public class ToDoListActivity extends Activity
{
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate your View
        setContentView(R.layout.main);
        // Get references to UI widgets
        ListView myListView=(ListView)findViewById(R.id.myListView);
        final EditText myEditText=(EditText)findViewById(R.id.myEditText);
        // Create the Array List of to do items
        final ArrayList<String> todoItems=new ArrayList<String>();
        for(int i=0;i < 5;i++){
            String item=getResources().getString(R.string.addItemHint) + i;
            todoItems.add(item);
        }
        // Create the Array Adapter to bind the array to the List View
        final ArrayAdapter<String> aa;
        aa=new ArrayAdapter<String>(this,
                                    android.R.layout.simple_list_item_1, todoItems);
        // Bind the Array Adapter to the List View
        myListView.setAdapter(aa);
        myEditText.setOnKeyListener(new View.OnKeyListener(){
            
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                    if((keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
                       || (keyCode == KeyEvent.KEYCODE_ENTER)){
                        todoItems.add(0, myEditText.getText().toString());
                        aa.notifyDataSetChanged();
                        myEditText.setText("");
                        return true;
                    }
                return false;
            }
        });
        this.findViewById(R.id.addBtn).setOnClickListener(
                                                          new OnClickListener(){
                                                              
                                                              @Override
                                                              public void onClick(View v) {
                                                                  if(myEditText.getText().toString().trim().length() == 0)
                                                                      return;
                                                                  todoItems.add(0, myEditText.getText().toString());
                                                                  aa.notifyDataSetChanged();
                                                                  myEditText.setText("");
                                                              }
                                                          });
    }
}