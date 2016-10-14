package com.paad.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ToDoItemAdapter extends ArrayAdapter<ToDoItem> {

	int resource;

	public ToDoItemAdapter(Context context, int resource, List<ToDoItem> items) {
		super(context, resource, items);
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LinearLayout todoView;

		if (convertView == null) {
			todoView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li;
			li = (LayoutInflater) getContext().getSystemService(inflater);
			li.inflate(resource, todoView, true);//todoView作为resource的父布局
		} else {
			todoView = (LinearLayout) convertView;
		}

		ToDoItem item = getItem(position);
		String taskString = item.getTask();
		Date createdDate = item.getCreated();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		String dateString = sdf.format(createdDate);
		TextView dateView = (TextView) todoView.findViewById(R.id.rowDate);
		TextView taskView = (TextView) todoView.findViewById(R.id.row);
		dateView.setText(dateString);
		taskView.setText(taskString);

		return todoView;
	}
}