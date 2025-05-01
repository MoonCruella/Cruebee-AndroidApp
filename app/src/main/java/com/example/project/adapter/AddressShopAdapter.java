package com.example.project.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.BaseActivity;
import com.example.project.R;
import com.example.project.helpers.TinyDB;
import com.example.project.model.AddressShop;
import java.util.List;

public class AddressShopAdapter extends RecyclerView.Adapter<AddressShopAdapter.AddressShopHoder> {
    private Context context;
    private List<AddressShop> addressShops;
    public AddressShopAdapter(Context context, List<AddressShop> addressShops) {
        this.context = context;
        this.addressShops = addressShops;
    }
    @NonNull
    @Override
    public AddressShopAdapter.AddressShopHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address_shop, parent, false);
        return new AddressShopAdapter.AddressShopHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressShopAdapter.AddressShopHoder holder, int position) {

        AddressShop addressShop = addressShops.get(position);
        holder.shopName.setText(addressShop.getName());
        holder.addressTxt.setText(addressShop.getAddress());
        holder.timeOpenTxt.setText(addressShop.getOpenTime());
        holder.phoneTxt.setText(addressShop.getPhone());
        String dis = String.valueOf(addressShop.getDistance());
        holder.countKm.setText(dis + " km");

        holder.chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BaseActivity.class);
                TinyDB tinyDB = new TinyDB(context);
                tinyDB.putObject("addressShop",addressShop);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return addressShops.size();
    }

    public class AddressShopHoder extends RecyclerView.ViewHolder{
        TextView shopName,addressTxt,chooseBtn,timeOpenTxt,phoneTxt,countKm;
        public AddressShopHoder(@NonNull View itemView) {
            super(itemView);
            shopName = itemView.findViewById(R.id.shopName);
            addressTxt = itemView.findViewById(R.id.addressTxt);
            chooseBtn = itemView.findViewById(R.id.chooseBtn);
            timeOpenTxt = itemView.findViewById(R.id.timeOpenTxt);
            phoneTxt = itemView.findViewById(R.id.phoneTxt);
            countKm = itemView.findViewById(R.id.countKm);
        }
    }
}
