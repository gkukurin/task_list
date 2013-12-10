package co.kukurin.tasklist;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import co.kukurin.tasklist.dao.DaoMaster;
import co.kukurin.tasklist.dao.DaoSession;

public class TaskListApplication extends Application {

	public static final String	DEBUG_LOG_NAME	= "TaskList";
	public static final String	DBNAME			= "tastlist.db";

	// must be ordered
	public static enum EnumPriorities {
		LOW, NORMAL, HIGH 
	};

	private static DaoSession	mDaoSession;

	@Override
	public void onCreate() {
		super.onCreate();

		setupGreenDao();
	}

	private void setupGreenDao() {

		SQLiteOpenHelper helper = new TaskListOpenHelper(this, TaskListApplication.DBNAME, null);
		SQLiteDatabase db = helper.getWritableDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		mDaoSession = daoMaster.newSession();
	}

	public static DaoSession getDaoSession() {
		return mDaoSession;
	}
}
