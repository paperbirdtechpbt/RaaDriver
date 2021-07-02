package com.pbt.raadrivers.Adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.pbt.raadrivers.Fonts.CTextView;
import com.pbt.raadrivers.R;

public class AdapterMissTrip extends BaseAdapter {
    String[] date, time, source, destination, customername;
    Context mContext;
    private static LayoutInflater inflater;
    public AdapterMissTrip(Activity mContext, String[] date, String[] time, String[] source, String[] destination, String[] customername)
    {
        this.mContext = mContext;
        this.date = date;
        this.time = time;
        this.source = source;
        this.destination = destination;
        this.customername = customername;
    }

    @Override
    public int getCount() {
         return date.length;
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class Holder {
        CTextView datetime,custname,source,destination;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {

            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.item_misstrip, null);

            CTextView datetime = (CTextView) view.findViewById(R.id.datetime);
        CTextView custname = (CTextView) view.findViewById(R.id.cust_name);
            CTextView sourcedata = (CTextView) view.findViewById(R.id.trip_source);
            CTextView destinationdata = (CTextView) view.findViewById(R.id.trip_destination);

            custname.setText(Html.fromHtml("Customer: <b> " + customername[i]));
            sourcedata.setText(Html.fromHtml("Startpoint: <b> " + source[i]));
            datetime.setText(Html.fromHtml("Date Time: <b>" + date[i]));
            destinationdata.setText(Html.fromHtml("Endpoint: <b> " + destination[i]));
                }
        return view;
        }
    }

