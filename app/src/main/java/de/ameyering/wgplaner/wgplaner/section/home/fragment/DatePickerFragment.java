package de.ameyering.wgplaner.wgplaner.section.home.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;


public class DatePickerFragment extends DialogFragment implements
    DatePickerDialog.OnDateSetListener {
    private OnDatePickedListener listener = null;

    public interface OnDatePickedListener {

        void onDatePicked(Date date);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        if (listener != null) {
            final Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            listener.onDatePicked(c.getTime());
        }
    }

    public void setListener(OnDatePickedListener listener) {
        this.listener = listener;
    }
}
