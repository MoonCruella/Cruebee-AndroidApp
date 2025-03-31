package com.example.project;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.project.adapter.RcmFoodAdapter;
import com.example.project.helpers.TinyDB;
import com.example.project.model.Food;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleySingleton;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private TextView addressTxt,usernameTxt;
    private RecyclerView recyclerView;
    private ImageView editAddress;
    private List<Food> rcmFoodList;
    private RequestQueue requestQueue;
    private TinyDB tinyDB;
    private ViewFlipper viewFlipper;
    String token;
    private ArrayList<Integer> discountList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        init(view);
        Log.d("TOKEN", "Token được gửi: " + token);
        getListRcmFood(token);

        return view;
    }

    public void init(View view) {
        viewFlipper = view.findViewById(R.id.discountView);
        addressTxt = view.findViewById(R.id.addressTxt);
        editAddress = view.findViewById(R.id.editAddress);
        recyclerView = view.findViewById(R.id.rcmFoodList);
        usernameTxt = view.findViewById(R.id.usernameTxt);
        tinyDB = new TinyDB(requireContext());
        requestQueue = VolleySingleton.getmInstance(requireContext()).getRequestQueue();
        rcmFoodList = new ArrayList<>();

        String fullAddress = tinyDB.getString("UserAddress");
        if(tinyDB.getBoolean("is_logged_in")){
            String username = tinyDB.getString("username");
            token = tinyDB.getString("token");
            usernameTxt.setText(username);
        }
        addressTxt.setText(fullAddress);

        Animation inAnimation = AnimationUtils.loadAnimation(requireContext(), android.R.anim.slide_in_left);
        Animation outAnimation = AnimationUtils.loadAnimation(requireContext(), android.R.anim.slide_out_right);

        viewFlipper.setInAnimation(inAnimation);
        viewFlipper.setOutAnimation(outAnimation);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        discountList.add(R.drawable.banner_discount_01);
        discountList.add(R.drawable.banner_discount_02);
        discountList.add(R.drawable.banner_discount_03);
        discountList.add(R.drawable.banner_discount_04);

        for (int imageRes : discountList) {
            ImageView imageView = new ImageView(requireContext());
            imageView.setImageResource(imageRes);
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            ));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewFlipper.addView(imageView);
        }

        editAddress.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddressActivity.class)));
    }

    public void getListRcmFood(String token) {
        String url = UrlUtil.ADDRESS + "products/1";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    rcmFoodList.clear(); // Xóa danh sách cũ trước khi load mới
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String name = jsonObject.getString("name");
                            int price = jsonObject.getInt("price");
                            String imageId = jsonObject.getString("attachmentId");
                            String image = UrlUtil.ADDRESS + "download/" + imageId;
                            String des = jsonObject.getString("description");
                            rcmFoodList.add(new Food(id, name, price, image, des));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    RcmFoodAdapter adapter = new RcmFoodAdapter(requireContext(), rcmFoodList);
                    recyclerView.setAdapter(adapter);
                },
                error -> Toast.makeText(requireContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(jsonArrayRequest);
    }

}
