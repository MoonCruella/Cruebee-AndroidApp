package com.example.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.project.R;

import java.util.List;

public class AddressAdapter  extends BaseAdapter {
    private Context context;
    private List<String> addressList;
    private LayoutInflater inflater;

    public AddressAdapter(Context context, List<String> addressList) {
        this.context = context;
        this.addressList = addressList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return addressList.size();
    }

    @Override
    public Object getItem(int position) {
        return addressList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_address, parent, false);
            holder = new ViewHolder();
            holder.tvAddress = convertView.findViewById(R.id.txtAddress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Gán dữ liệu
        holder.tvAddress.setText(addressList.get(position));

        return convertView;
    }

    static class ViewHolder {
        TextView tvAddress;
    }
}
