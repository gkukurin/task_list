package co.kukurin.tasklist;

import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import co.kukurin.tasklist.dao.DaoSession;
import co.kukurin.tasklist.dao.Tasks;
import co.kukurin.tasklist.dao.TasksDao;

/**
 * EditTask Activity 
 * 
 * This activity is used to insert or modify task data.
 * If task with id given by EXTRA_TASK_ID exists, the task data will be shown. 
 * If user modifies the data for existing task, the record will be updated. Otherwise, new record will be created.
 * 
 * @author goran
 *
 */

public class EditTask extends ActionBarActivity implements OnClickListener {

	public static final String	EXTRA_TASK_ID	= "task_id";

	/**
	 * ID placeholder for new entries (insert mode)
	 */
	private static final long	ID_NEW			= -1L;

	/**
	 * task id, or -1 if insert mode
	 */
	private long				mTaskId			= ID_NEW;

	private EditText			mTask_title;
	private CheckBox			mCheck_completed;
	private EditText			mTask_description;
	private ImageButton			mButton_set_due_date;
	private TextViewDate		mText_due_date;
	private Spinner				mSpinner_priority;				;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_task);

		setupViews();

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mTaskId = bundle.getLong(EXTRA_TASK_ID, ID_NEW);
		}

		fillForm(mTaskId);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		if(mTaskId == ID_NEW)
			setTitle(R.string.edit_title_add);
		else
			setTitle(R.string.edit_title_edit);
	}

	
	
	/**
	 * initial set up of all views used in the layout
	 */
	private void setupViews() {
		
		mTask_title = (EditText) findViewById(R.id.task_title);
		mTask_description = (EditText) findViewById(R.id.task_description);
		mCheck_completed = (CheckBox) findViewById(R.id.check_completed);
		mText_due_date = (TextViewDate) findViewById(R.id.text_due_date);

		mButton_set_due_date = (ImageButton) findViewById(R.id.button_set_due_date);
		mButton_set_due_date.setOnClickListener(this);

		mSpinner_priority = (Spinner) findViewById(R.id.spinner_priority);
		String[] priorities = new String[3];
		priorities[0] = getString(R.string.priority_low);
		priorities[1] = getString(R.string.priority_normal);
		priorities[2] = getString(R.string.priority_high);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				android.R.id.text1, priorities);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner_priority.setAdapter(adapter);
	}
	

	/**
	 * fills form fields for current task entry, or sets default values if insert mode (no task with given id is found)
	 */
	private void fillForm(long id) {

		DaoSession daoSession = TaskListApplication.getDaoSession();
		TasksDao dao = daoSession.getTasksDao();
		Tasks task = dao.load(id);
		Date dueDate = null;
		
		if (task != null) {
			mTask_title.setText(task.getTitle());
			mTask_description.setText(task.getDescription());
			mCheck_completed.setChecked(task.getCompleted());
			mSpinner_priority.setSelection(task.getPriority());

			dueDate = task.getDate_due();

		} else {
			mSpinner_priority.setSelection(TaskListApplication.EnumPriorities.NORMAL.ordinal());
		}
		
		mText_due_date.setDate(dueDate);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		if (mTaskId == ID_NEW)
			// delete make no sense in insert mode, until entry is committed to a database
			menu.findItem(R.id.item_delete_task).setVisible(false);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home || item.getItemId() == R.id.item_discard_changes) {
			setResult(RESULT_CANCELED);
			finish();
		} else if (item.getItemId() == R.id.item_save_changes) {
			try{
				saveChanges(mTaskId);
				setResult(RESULT_OK);
				finish();
			} catch(IllegalArgumentException e){
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		} else if (item.getItemId() == R.id.item_delete_task) {
			deleteTask();
		}
		return super.onOptionsItemSelected(item);
	}

	
	
	/**
	 * presents the confirmation dialog and if the user confirms deletion, deletes the active task
	 */
	private void deleteTask() {

		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.edit_task_delete_this_task);
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which==DialogInterface.BUTTON_POSITIVE){
					DaoSession daoSession = TaskListApplication.getDaoSession();
					daoSession.getTasksDao().deleteByKey(mTaskId);
					setResult(RESULT_OK);
					finish();
				}
			}
		};
		
		builder.setPositiveButton(R.string.yes, listener);
		builder.setNegativeButton(R.string.no, listener);
		Dialog dialog = builder.create();
		dialog.show();
	}
	

	/**
	 * saves the form data into a database. If insert mode, new item will be inserted, otherwise the entry with ID will
	 * be updated
	 * 
	 * @param id
	 *        id of task item entry. If not found, the record will be inserted
	 */
	private void saveChanges(Long id) throws IllegalArgumentException{

		
		if(mTask_title.getText().toString().trim().length()==0)
			throw new IllegalArgumentException(getString(R.string.edit_task_err_task_title_may_not_be_empty));
		
		
		DaoSession daoSession = TaskListApplication.getDaoSession();
		TasksDao dao = daoSession.getTasksDao();
		Tasks task = dao.load(id);
		Date dateCreated;
		boolean insertMode = false;
		
		if (task == null) {
			task = new Tasks();
			dateCreated = new Date();
			task.setDate_created(dateCreated);
			insertMode = true;
		}

		task.setTitle(mTask_title.getText().toString());
		task.setDescription(mTask_description.getText().toString());
		task.setCompleted(mCheck_completed.isChecked());
		task.setPriority((byte) mSpinner_priority.getSelectedItemPosition());

		Date dueDate = mText_due_date.getDate();
		
		task.setDate_due(dueDate);

		if (insertMode)
			dao.insert(task);
		else
			dao.update(task);
	}
	
	

	
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.button_set_due_date){
			pickDueDate();
		}
		
	}

	
	/**
	 * Shows dialog to set or clear due date.
	 */
	@SuppressLint("NewApi")
	private void pickDueDate() {

		Builder builder = new AlertDialog.Builder(this);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		final Date minDate = DateUtils.truncDate(cal.getTime());
		
		
		final DatePicker dp = new DatePicker(this);
		
		dp.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		
		if(android.os.Build.VERSION.SDK_INT >= 11){
			dp.setCalendarViewShown(false);
			dp.setSpinnersShown(true);
			dp.setMinDate(minDate.getTime());
		}
		
		builder.setView(dp);
		builder.setTitle(R.string.edit_date_picker_title);
		
		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which){
					case DialogInterface.BUTTON_POSITIVE:
						
						Calendar cal = Calendar.getInstance();
						cal.set(Calendar.YEAR, dp.getYear());
						cal.set(Calendar.MONTH, dp.getMonth());
						cal.set(Calendar.DAY_OF_MONTH, dp.getDayOfMonth());
						Date date = DateUtils.truncDate(cal.getTime());
						
						if(!date.before(minDate)){
							mText_due_date.setDate(date);
						} else{
							Toast.makeText(EditTask.this, "Due date in the past is not allowed", Toast.LENGTH_SHORT).show();
						}

						break;
						
					case DialogInterface.BUTTON_NEUTRAL:
						mText_due_date.setDate(null);
						break;
						
					case DialogInterface.BUTTON_NEGATIVE:
						break;
				}
				
			}
		};

		builder.setPositiveButton(R.string.edit_date_picker_set, onClickListener);
		builder.setNegativeButton(R.string.edit_date_picker_cancel, onClickListener);
		builder.setNeutralButton(R.string.edit_date_picker_clear, onClickListener);

		builder.create().show();
	}

}
