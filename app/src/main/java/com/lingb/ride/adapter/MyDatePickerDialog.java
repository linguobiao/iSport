package com.lingb.ride.adapter;

import com.isport.trackernew.R;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;

public class MyDatePickerDialog extends DatePickerDialog{

	public MyDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
	}

	public MyDatePickerDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
		super(context, theme, callBack, year, monthOfYear, dayOfMonth);
		CharSequence Cancel = context.getString(R.string.ride_Cancel);
		CharSequence Confirm = context.getString(R.string.ride_Confirm);
		MyDatePickerDialog.this.setButton(DialogInterface.BUTTON_NEGATIVE, Cancel, this);
		MyDatePickerDialog.this.setButton(DialogInterface.BUTTON_POSITIVE, Confirm, this);
		
	}
}
