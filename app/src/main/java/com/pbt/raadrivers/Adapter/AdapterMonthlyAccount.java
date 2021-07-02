package com.pbt.raadrivers.Adapter;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pbt.raadrivers.R;

public class AdapterMonthlyAccount extends BaseAdapter {
    String[] date, time, daily_amount, trip_amount, wallet, kilometer, payment_mode, commission, onhandcash, driver_amount;
    Context mContext;
    private static LayoutInflater inflater;

    public AdapterMonthlyAccount(Context mContext, String[] date, String[] time, String[] daily_amount, String[] trip_amount, String[] kilometer, String[] payment_mode, String[] commission
            , String[] onhandcash, String[] driver_amount, String[] wallet) {
        this.date = date;
        this.time = time;
        this.daily_amount = daily_amount;
        this.trip_amount = trip_amount;
        this.kilometer = kilometer;
        this.driver_amount = driver_amount;
        this.mContext = mContext;
        this.payment_mode = payment_mode;
        this.commission = commission;
        this.onhandcash = onhandcash;
        this.wallet = wallet;
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
        TextView datetime, daily_amount_text, wallet, trip_amount_text, kilometer_text, payment_mode_text, commition, trip_price, driver_amount_text;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.item_monthly_account, null);

        holder.datetime = (TextView) rowView.findViewById(R.id.datetime);
        holder.daily_amount_text = (TextView) rowView.findViewById(R.id.daily_amount_text);
        holder.trip_amount_text = (TextView) rowView.findViewById(R.id.trip_amount_text);
        holder.kilometer_text = (TextView) rowView.findViewById(R.id.kilometer_text);
        holder.payment_mode_text = (TextView) rowView.findViewById(R.id.payment_mode_text);
        holder.commition = (TextView) rowView.findViewById(R.id.commition);
        holder.trip_price = (TextView) rowView.findViewById(R.id.trip_price);
        holder.driver_amount_text = (TextView) rowView.findViewById(R.id.driver_amount_text);
        holder.wallet = (TextView) rowView.findViewById(R.id.wallet);

        holder.trip_price.setText(Html.fromHtml("" + onhandcash[position]));
        if (commission[position] != null && !commission[position].isEmpty() && !commission[position].equals("null"))
            holder.commition.setText(Html.fromHtml("Commission: <b>₹ " + commission[position]));
        else
            holder.commition.setText(Html.fromHtml("Commission: <b>₹ 0"));

        holder.datetime.setText(Html.fromHtml("Date Time: <b>" + date[position] + "  " + time[position]));

        if (daily_amount[position] != null && !daily_amount[position].isEmpty() && !daily_amount[position].equals("null"))
            holder.daily_amount_text.setText(Html.fromHtml("Daily driver amount <b>₹ " + daily_amount[position]));
        else
            holder.daily_amount_text.setText(Html.fromHtml("Daily driver amount <b>₹ 0"));

        if (driver_amount[position] != null && !driver_amount[position].isEmpty() && !driver_amount[position].equals("null"))
            holder.trip_amount_text.setText(Html.fromHtml("Driver amount <b>₹ " + Math.round(Float.parseFloat(driver_amount[position]))));
        else
            holder.trip_amount_text.setText(Html.fromHtml("Driver amount <b>₹ 0"));

        if (kilometer[position] != null && !kilometer[position].isEmpty() && !kilometer[position].equals("null"))
            holder.kilometer_text.setText(Html.fromHtml("Trip Km: <b>" + kilometer[position]));
        else
            holder.kilometer_text.setText(Html.fromHtml("Trip Km: <b> 0"));

        if (payment_mode[position] != null && !payment_mode[position].isEmpty() && !payment_mode[position].equals("null"))
            holder.payment_mode_text.setText(Html.fromHtml("Payment mode: <b>" + payment_mode[position]));

        if (trip_amount[position] != null && !trip_amount[position].isEmpty() && !trip_amount[position].equals("null"))
            holder.driver_amount_text.setText(Html.fromHtml("Trip amount: <b>₹ " + trip_amount[position]));
        else
            holder.driver_amount_text.setText(Html.fromHtml("Trip amount: <b>₹ 0"));

        if (wallet[position] != null && !wallet[position].isEmpty() && !wallet[position].equals("null"))
            holder.wallet.setText(Html.fromHtml("Wallet amount: <b>₹ " + wallet[position]));
        else
            holder.wallet.setText(Html.fromHtml("Wallet amount: <b>₹ 0"));


        Log.e("daily_amount[position]", daily_amount[position]);

        if (daily_amount[position] != null && !daily_amount[position].isEmpty() && !daily_amount[position].equals("null")) {
            holder.daily_amount_text.setVisibility(View.VISIBLE);
            holder.trip_price.setVisibility(View.GONE);
            holder.commition.setVisibility(View.GONE);
            holder.trip_amount_text.setVisibility(View.GONE);
            holder.kilometer_text.setVisibility(View.GONE);
            holder.payment_mode_text.setVisibility(View.GONE);
            holder.driver_amount_text.setVisibility(View.GONE);
            holder.wallet.setVisibility(View.GONE);

        } else {
            holder.daily_amount_text.setVisibility(View.GONE);
        }

        return rowView;
    }

}
