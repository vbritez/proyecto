package com.tigo.cs.android.activity.tigomoney;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment implements
		OnDateSetListener {
	
	private int pyear;
	private int pmont;
	private int pday;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		pyear = year;
		pmont = monthOfYear;
		pday = dayOfMonth;
		
	}

	public int getPyear() {
		return pyear;
	}

	public int getPmont() {
		return pmont;
	}

	public int getPday() {
		return pday;
	}

	public void setPyear(int pyear) {
		this.pyear = pyear;
	}

	public void setPmont(int pmont) {
		this.pmont = pmont;
	}

	public void setPday(int pday) {
		this.pday = pday;
	}
	
	
}
