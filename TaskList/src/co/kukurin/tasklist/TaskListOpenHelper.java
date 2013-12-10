package co.kukurin.tasklist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import co.kukurin.tasklist.dao.DaoMaster.OpenHelper;

public class TaskListOpenHelper extends OpenHelper {

	public TaskListOpenHelper(Context context, String name, CursorFactory factory) {
		super(context, name, factory);
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		try {
			db.beginTransaction();

			Log.d(TaskListApplication.DEBUG_LOG_NAME, "onUpgrade oldVersion = " + oldVersion + " newVersion=" + newVersion);

			switch (oldVersion) {

			}

			db.setTransactionSuccessful();
			
		} catch (Exception e) {

			Log.d(TaskListApplication.DEBUG_LOG_NAME, "onUpgrade ERROR " + e.getMessage());
			e.printStackTrace();

		} finally {

			db.endTransaction();
			Log.d(TaskListApplication.DEBUG_LOG_NAME, "onUpgrade SUCCESS");
		}
	}

}
