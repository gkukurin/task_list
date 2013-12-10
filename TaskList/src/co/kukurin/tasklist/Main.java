package co.kukurin.tasklist;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import co.kukurin.tasklist.dao.DaoSession;
import co.kukurin.tasklist.dao.Tasks;
import co.kukurin.tasklist.dao.TasksDao;
import de.greenrobot.dao.WhereCondition;

/**
 * Main Activity 
 * 
 * This is main application activity. it is used to show and manipulate the task list.
 * 
 * @author goran
 *
 */
public class Main extends ActionBarActivity implements OnClickListener, OnItemClickListener, OnItemLongClickListener, Callback {

	private static final String	KEY_FILTER_STATE	= "filter_state";
	private static final String	KEY_SORT_STATE		= "sort_state";

	private ActionMode			mActionMode;
	SparseBooleanArray			mSelectedItems		= new SparseBooleanArray();
	int							mSelectedItemsCount;

	private ListView			mListTasks;
	private TextView			mMessageEmpty;
	private ViewGroup			mFilterInfo;
	private TextView			mFilterName;
	private ImageButton			mButtonClearFilter;

	// use menu item string id-s as as filter state id-s
	int[]						mFilterStates		= new int[] 
			{
			R.string.main_menu_filter_completed,
			R.string.main_menu_filter_due, 
			R.string.main_menu_filter_uncompleted, 
			R.string.main_menu_filter_show_all
			};
	int							mFilterState;

	// use menu item string id-s as as sort state id-s
	int[]						mSortStates			= new int[] 
			{
			R.string.main_menu_sort_by_created_asc,
			R.string.main_menu_sort_by_created_desc, 
			R.string.main_menu_sort_by_due_asc,
			R.string.main_menu_sort_by_due_desc, 
			R.string.main_menu_sort_by_prio_asc,
			R.string.main_menu_sort_by_prio_desc	
			};

	int							mSortState;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setupViews();

		if (savedInstanceState == null) {
			mFilterState = R.string.main_menu_filter_show_all;
			mSortState = R.string.main_menu_sort_by_created_desc;
		} else {
			mFilterState = savedInstanceState.getInt(KEY_FILTER_STATE);
			mSortState = savedInstanceState.getInt(KEY_SORT_STATE);
		}

	}

	/**
	 * initial set up of all views used in the layout
	 */
	private void setupViews(){
		mListTasks = (ListView) findViewById(R.id.list_tasks);
		mMessageEmpty = (TextView) findViewById(R.id.text_empty_list);
		mFilterInfo = (ViewGroup) findViewById(R.id.filter_info);
		mFilterName = (TextView) findViewById(R.id.filter_name);
		mButtonClearFilter = (ImageButton) findViewById(R.id.clear_filter);

		mButtonClearFilter.setOnClickListener(this);
		mListTasks.setOnItemClickListener(this);
		mListTasks.setLongClickable(true);
		mListTasks.setOnItemLongClickListener(this);
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();

		reloadTaskList(mFilterState, mSortState);
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// remember active filter and sort
		outState.putInt(KEY_FILTER_STATE, mFilterState);
		outState.putInt(KEY_SORT_STATE, mSortState);
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.item_add_task1 || item.getItemId() == R.id.item_add_task2) {
			Intent intent = new Intent(this, EditTask.class);
			startActivity(intent);
			return true;
		} 


		// all the rest must be filter or sort
		if (item.getItemId() == R.id.item_filter_completed)
			mFilterState = R.string.main_menu_filter_completed;	
		else if (item.getItemId() == R.id.item_filter_uncompleted)
			mFilterState = R.string.main_menu_filter_uncompleted;	
		else if (item.getItemId() == R.id.item_filter_due)
			mFilterState = R.string.main_menu_filter_due;	
		else if (item.getItemId() == R.id.item_filter_show_all) 
			mFilterState = R.string.main_menu_filter_show_all;
		else if (item.getItemId() == R.id.item_filter_show_all) 
			mFilterState = R.string.main_menu_filter_show_all;
		else if (item.getItemId() == R.id.item_sort_by_created_asc) 
			mSortState = R.string.main_menu_sort_by_created_asc;
		else if (item.getItemId() == R.id.item_sort_by_created_desc) 
			mSortState = R.string.main_menu_sort_by_created_desc;
		else if (item.getItemId() == R.id.item_sort_by_due_asc) 
			mSortState = R.string.main_menu_sort_by_due_asc;
		else if (item.getItemId() == R.id.item_sort_by_due_desc) 
			mSortState = R.string.main_menu_sort_by_due_desc;
		else if (item.getItemId() == R.id.item_sort_by_prio_asc) 
			mSortState = R.string.main_menu_sort_by_prio_asc;
		else if (item.getItemId() == R.id.item_sort_by_prio_desc) 
			mSortState = R.string.main_menu_sort_by_prio_desc;
		
		reloadTaskList(mFilterState, mSortState);
		
		return true;
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.clear_filter) {
			mFilterState = R.string.main_menu_filter_show_all;
			reloadTaskList(mFilterState, mSortState);
		}
	}

	
	/**
	 * Reads the task list from a database according to filter and sort options and shows the list on the screen.
	 *  
	 * If there are no tasks in a database, shows the corresponding message.
	 * Otherwise, shows filtered list, sorted by defined criteria. If there are no items satisfying the filter condition, an empty screen will be shown. 
	 * 
	 * @param filterState	
	 * @param sortState
	 */
	private void reloadTaskList(int filterState, int sortState) {
		
		boolean empty = TaskListApplication.getDaoSession().getTasksDao().count() == 0;
		
		if(empty){
			
			mMessageEmpty.setVisibility(View.VISIBLE);
			mFilterInfo.setVisibility(View.GONE);
			
		} else{
		
			mMessageEmpty.setVisibility(View.GONE);
			mFilterInfo.setVisibility(filterState == R.string.main_menu_filter_show_all ? View.GONE : View.VISIBLE);
			mFilterName.setText(getString(filterState));
		}
		
		TasksDao dao = TaskListApplication.getDaoSession().getTasksDao();
		WhereCondition where = null;

		switch (filterState) {
			
			case R.string.main_menu_filter_completed:
				where = TasksDao.Properties.Completed.eq(true);
				break;
				
			case R.string.main_menu_filter_uncompleted:
				where = TasksDao.Properties.Completed.eq(false);
				break;
				
			case R.string.main_menu_filter_due:
				Calendar cal = Calendar.getInstance();
				Date today = DateUtils.truncDate(cal.getTime());
				where = TasksDao.Properties.Date_due.lt(today);
				break;
				
			case R.string.main_menu_filter_show_all:
				where = TasksDao.Properties.Id.isNotNull();
				break;
				
			default:
				throw new IllegalArgumentException(getString(R.string.main_err_illegal_filter) + filterState);

		}

		String sort;
		final String ASC = " ASC ";
		final String DESC = " DESC ";
		
		switch (sortState) {
			
			case R.string.main_menu_sort_by_created_asc:
				sort = TasksDao.Properties.Date_created.columnName + ASC;
				break;
				
			case R.string.main_menu_sort_by_created_desc:
				sort = TasksDao.Properties.Date_created.columnName + DESC;
				break;
					
			case R.string.main_menu_sort_by_due_asc:
				sort = TasksDao.Properties.Date_due.columnName + ASC;
				break;
				
			case R.string.main_menu_sort_by_due_desc:
				sort = TasksDao.Properties.Date_due.columnName + DESC;
				break;
				
			case R.string.main_menu_sort_by_prio_asc:
				sort = TasksDao.Properties.Priority.columnName + ASC;
				break;
			
			case R.string.main_menu_sort_by_prio_desc:
				sort = TasksDao.Properties.Priority.columnName + DESC;
				break;
				
			default:
				throw new IllegalArgumentException(getString(R.string.main_err_illegal_sort) + sortState);
		}
		
		List<Tasks> tasks = dao.queryBuilder().where(where).orderRaw(sort).list();

		TaskListAdapter adapter = new TaskListAdapter(this, R.layout.item_task, R.id.textTaskTitle, tasks, mSelectedItems);

		int position = mListTasks.getFirstVisiblePosition();

		mListTasks.setAdapter(adapter);

		if (position >= mListTasks.getCount())
			position = mListTasks.getCount() - 1;
		if (position >= 0)
			mListTasks.setSelectionFromTop(position, 0);

	}
	
	
	

	@Override
	public void onItemClick(AdapterView<?> arg0,  View v, int position, long id) {
		
		if (mActionMode == null){
			Intent intent = new Intent(this, EditTask.class);
			intent.putExtra(EditTask.EXTRA_TASK_ID, id);
			startActivity(intent);
		}
		else
			toggleItemSelectedState(v, position);
		
		
	}



	@Override
	public boolean onItemLongClick(AdapterView<?> arg0,  View v, int position, long id) {
		
		if (mActionMode != null) {
			return false;
		}
		
		mActionMode = startSupportActionMode(this);
		toggleItemSelectedState(v, position);

		return true;
	}

	
	
	/**
	 * toggles list item selected state. If there are no selected items left, leaves the action mode.
	 *  
	 * @param view
	 * @param position	position of the item in the list
	 */
	private void toggleItemSelectedState(View view, int position) {

		boolean newCheckedValue = !mSelectedItems.get(position);
		mSelectedItems.append(position, newCheckedValue);

		mListTasks.invalidateViews();	// redraw item background to show selected status

		if (newCheckedValue)
			mSelectedItemsCount++;
		else
			mSelectedItemsCount--;

		if (mSelectedItemsCount > 0) {
			mActionMode.setTitle(String.format(getString(R.string.main_text_N_items_selected), mSelectedItemsCount));
			mActionMode.invalidate();
		} else {
			mActionMode.finish();
		}
	}


	@Override
	public boolean onActionItemClicked(final ActionMode mode, MenuItem item)
	{

		if (item.getItemId() == R.id.item_delete) {
			
			actOnSelectedItems(getString(R.string.main_confirmation_delete_all_selected_tasks), new Callable() {
				
				@Override
				public void act(TasksDao dao, Tasks entity) {
					dao.delete(entity);
				}
			});
			return true;
			
		} else if (item.getItemId() == R.id.item_mark_complete) {
			
			actOnSelectedItems(getString(R.string.main_confirmation_mark_all_selected_tasks_as_completed), new Callable() {
				
				@Override
				public void act(TasksDao dao, Tasks entity) {
					entity.setCompleted(true);
					dao.update(entity);
				}
			});
			return true;
		}
		
		return false;
	}

	
	interface Callable {
		public void act(TasksDao dao, Tasks entity);
	}
	
	
	/**
	 * Presents dialog with confirmation message.
	 * After user confirms, iterates through selected tasks and call action.act on each task.
	 * Finally, reloads the task list and leaves the action mode
	 *  
	 * @param confirmationQuestion	text to display in confirmation dialog
	 * @param action				action to be taken on each task
	 * 
	 */
	private void actOnSelectedItems(final String confirmationQuestion, final Callable action) {

		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(confirmationQuestion);
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which==DialogInterface.BUTTON_POSITIVE){
					
					DaoSession daoSession = TaskListApplication.getDaoSession();
					final TasksDao dao = daoSession.getTasksDao();
					
					try {
						daoSession.runInTx(new Runnable() {

							@Override
							public void run() {

								for (int i = 0; i < mSelectedItems.size(); i++) {
									if (mSelectedItems.valueAt(i)) {
										Tasks entity = (Tasks)mListTasks.getItemAtPosition(mSelectedItems.keyAt(i));
										action.act(dao, entity);
									}
								}
							}
						});

						reloadTaskList(mFilterState, mSortState);

						mActionMode.finish();

					} catch (SQLiteException e) {
						Toast.makeText(Main.this, e.getMessage(), Toast.LENGTH_SHORT).show();
					} 
				}
			}
		};
		
		builder.setPositiveButton(R.string.yes, listener);
		builder.setNegativeButton(R.string.no, listener);
		Dialog dialog = builder.create();
		dialog.show();
	}

	
	
	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.main_contextual, menu);

		mSelectedItems.clear();
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode arg0) {
		mActionMode = null;
		mSelectedItems.clear();
		mSelectedItemsCount = 0;
		mListTasks.invalidateViews();
	}

	@Override
	public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
		return false;
	}

}
