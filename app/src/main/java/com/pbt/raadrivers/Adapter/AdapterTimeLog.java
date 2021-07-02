package com.pbt.raadrivers.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.pbt.raadrivers.Fonts.CTextView;
import com.pbt.raadrivers.R;

public class AdapterTimeLog extends BaseAdapter {
    String[] date, total_min;
    Context mContext;
    private static LayoutInflater inflater;

    public AdapterTimeLog(Context mContext, String[] date, String[] total_min) {
        this.date = date;
        this.total_min = total_min;

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
        CTextView datetime, daily_amount_text, trip_amount_text, kilometer_text, payment_mode_text, commition, trip_price, driver_amount_text;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.item_monthly_account, null);

        holder.datetime = (CTextView) rowView.findViewById(R.id.datetime);
        holder.daily_amount_text = (CTextView) rowView.findViewById(R.id.daily_amount_text);

        holder.trip_amount_text = (CTextView) rowView.findViewById(R.id.trip_amount_text);
        holder.kilometer_text = (CTextView) rowView.findViewById(R.id.kilometer_text);
        holder.payment_mode_text = (CTextView) rowView.findViewById(R.id.payment_mode_text);
        holder.commition = (CTextView) rowView.findViewById(R.id.commition);
        holder.trip_price = (CTextView) rowView.findViewById(R.id.trip_price);
        holder.driver_amount_text = (CTextView) rowView.findViewById(R.id.driver_amount_text);

        holder.datetime.setText(Html.fromHtml("Date : <b>" + date[position]));


        holder.daily_amount_text.setText(Html.fromHtml("Total Hours :  <b> " + formatHoursAndMinutes(Integer.valueOf(total_min[position]))));

        holder.trip_amount_text.setVisibility(View.GONE);
        holder.kilometer_text.setVisibility(View.GONE);
        holder.payment_mode_text.setVisibility(View.GONE);
        holder.commition.setVisibility(View.GONE);
        holder.trip_price.setVisibility(View.GONE);
        holder.driver_amount_text.setVisibility(View.GONE);

        return rowView;
    }
    public static String formatHoursAndMinutes(int totalMinutes) {
        String minutes = Integer.toString(totalMinutes % 60);
        minutes = minutes.length() == 1 ? "0" + minutes : minutes;
        String hours = totalMinutes / 60 == 0 ? "" : String.valueOf(totalMinutes/60)+" Hours ";
        return hours + minutes + " Minutes";
    }
}
