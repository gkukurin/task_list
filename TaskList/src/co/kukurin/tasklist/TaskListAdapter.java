package co.kukurin.tasklist;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import co.kukurin.tasklist.TaskListApplication.EnumPriorities;
import co.kukurin.tasklist.dao.Tasks;

public class TaskListAdapter extends ArrayAdapter<Tasks> {

	/**
	 * Background color of the selected item (long tap-->multiselect) in the list
	 */
	private static final int	COLOR_SELECTED	= 0xff33b5e5;
	
	private SparseBooleanArray	mSelectedTasks;

	public TaskListAdapter(Context context, int resource, int textViewResourceId, List<Tasks> tasks, SparseBooleanArray selectedTasks) {
		super(context, resource, textViewResourceId, tasks);
		
		this.mSelectedTasks = selectedTasks;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		convertView = super.getView(position, convertView, parent);

		TextView textDueDate1 = (TextView) convertView.findViewById(R.id.textDueDate1);
		TextView textTaskTitle = (TextView) convertView.findViewById(R.id.textTaskTitle);
		CheckBox checkCompleted = (CheckBox) convertView.findViewById(R.id.checkCompleted);
		ImageView imagePriorityHigh = (ImageView) convertView.findViewById(R.id.imagePriorityHigh);
		ImageView imagePriorityLow = (ImageView) convertView.findViewById(R.id.imagePriorityLow);

		Tasks entry = getItem(position);
		
		textTaskTitle.setText(entry.getTitle());
		checkCompleted.setChecked(entry.getCompleted());
		
		Date dueDate = entry.getDate_due();
		if(dueDate!=null){
			DateFormat sdf = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT);
			textDueDate1.setText(sdf.format(dueDate));
			textDueDate1.setVisibility(View.VISIBLE);
		} else{
			textDueDate1.setVisibility(View.INVISIBLE);
		}
		
		
		// default for normal priority, without icon
		imagePriorityLow.setVisibility(View.INVISIBLE);
		imagePriorityHigh.setVisibility(View.INVISIBLE);
		
		if(entry.getPriority()==EnumPriorities.HIGH.ordinal())
			imagePriorityHigh.setVisibility(View.VISIBLE);
		else if(entry.getPriority()==EnumPriorities.LOW.ordinal())
			imagePriorityLow.setVisibility(View.VISIBLE);

		if (mSelectedTasks.get(position))
			convertView.setBackgroundColor(COLOR_SELECTED);
		else
			convertView.setBackgroundColor(Color.TRANSPARENT);
		
		return convertView;
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}
}
