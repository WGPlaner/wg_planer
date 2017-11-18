package de.ameyering.wgplaner.wgplaner.section.home.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Currency;

public class LocaleSpinnerAdapter extends ArrayAdapter {
    private ArrayList<Currency> currencies = new ArrayList<>();

    public LocaleSpinnerAdapter(Context context, int textViewResourceId,
        ArrayList<Currency> currencies) {
        super(context, textViewResourceId);
        this.currencies = currencies;
    }

    @Override
    public int getCount() {
        return currencies.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_item,
                parent, false);
        }
        TextView text = convertView.findViewById(android.R.id.text1);

        text.setText(currencies.get(position).getSymbol());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(
                android.R.layout.simple_spinner_dropdown_item, parent, false);
        TextView text = convertView.findViewById(android.R.id.text1);

        text.setText(currencies.get(position).getSymbol());
        return convertView;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return currencies.get(position);
    }
}
