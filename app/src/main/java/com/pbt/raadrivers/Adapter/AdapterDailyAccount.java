package com.pbt.raadrivers.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pbt.raadrivers.R;


public class AdapterDailyAccount extends BaseAdapter {
    String[] date, time, price, km_array, commission, onhandcash;
    Context mContext;
    private static LayoutInflater inflater;

    public AdapterDailyAccount(Context mContext, String[] date, String[] time, String[] price, String[] km_array, String[] commission, String[] onhandcash) {
        this.date = date;
        this.time = time;
        this.price = price;
        this.km_array = km_array;
        this.commission = commission;
        this.onhandcash = onhandcash;

        this.mContext = mContext;

        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        // TODO Auto-generated method stub
        return date.length;
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private class Holder {
        TextView datetime, price, km, commition, trip_price;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.item_daily_account, null);

        holder.datetime = (TextView) rowView.findViewById(R.id.datetime);
        holder.price = (TextView) rowView.findViewById(R.id.price);
        holder.km = (TextView) rowView.findViewById(R.id.km);
        holder.commition = (TextView) rowView.findViewById(R.id.commition);
        holder.trip_price = (TextView) rowView.findViewById(R.id.trip_price);

        holder.datetime.setText(Html.fromHtml("Date Time: <b>" + date[position] + "  " + time[position]));
        holder.price.setText(Html.fromHtml("Driver Amount: <b>" + "₹ " + price[position]));
        holder.km.setText(Html.fromHtml("Trip Km: <b>" + km_array[position]));
        holder.commition.setText(Html.fromHtml("Trip Commission: <b>₹ " + commission[position]));
        holder.trip_price.setText(Html.fromHtml("Trip Amount: <b>₹ " + onhandcash[position]));

        return rowView;
    }

}
