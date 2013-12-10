package co.kukurin.tasklist;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * TextView tagged with date value
 * 
 * @author goran
 */
public class TextViewDate extends TextView {

	static DateFormat	sdf	= SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT);

	public TextViewDate(Context context) {
		super(context);
	}

	public TextViewDate(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TextViewDate(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setDate(Date date) {
		setTag(date);
		if (date == null)
			setText(R.string.edit_due_date_not_set);
		else
			setText(sdf.format(date));
	}

	public Date getDate() {
		return (Date) getTag();
	}
}
