package com.example.project.adapter;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.activity.AddressDetailsActivity;
import com.example.project.R;
import com.example.project.model.Address;
import java.util.List;

public class AddressUserAdapter extends RecyclerView.Adapter<AddressUserAdapter.AddressUserHoder> {
    private Context context;
    private List<Address> addresses;
    public AddressUserAdapter(Context context, List<Address> addresses) {
        this.context = context;
        this.addresses = addresses;
    }
    @NonNull
    @Override
    public AddressUserAdapter.AddressUserHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address_user, parent, false);
        return new AddressUserAdapter.AddressUserHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressUserAdapter.AddressUserHoder holder, int position) {

        Address address = addresses.get(position);
        holder.addressTxt.setText(address.getAddress_details());
        if(address.getIs_primary() == 1){
            holder.primaryTxt.setVisibility(VISIBLE);
        }
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddressDetailsActivity.class);
                intent.putExtra("object",address);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public class AddressUserHoder extends RecyclerView.ViewHolder{
        TextView addressTxt,primaryTxt;
        ConstraintLayout constraintLayout;
        public AddressUserHoder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.main_layout);
            addressTxt = itemView.findViewById(R.id.addressTxt);
            primaryTxt = itemView.findViewById(R.id.primaryTxt);
        }
    }
}
