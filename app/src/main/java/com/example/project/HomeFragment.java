package com.example.project;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.project.adapter.AddressUserAdapter;
import com.example.project.adapter.FoodAdapter;
import com.example.project.adapter.FoodTopTenAdapter;
import com.example.project.adapter.RcmFoodAdapter;
import com.example.project.helpers.TinyDB;
import com.example.project.interfaces.OnFragmentSwitchListener;
import com.example.project.model.Address;
import com.example.project.model.Food;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView addressTxt,usernameTxt,viewAllBtn;
    private RecyclerView mustTryRcView,topTenRcView;
    private ImageView editAddress;
    private List<Food> rcmFoodList;
    private List<Food> topTenFoodList;
    private ActivityResultLauncher<Intent> addressActivityResultLauncher;
    private RequestQueue requestQueue;
    private OnFragmentSwitchListener listener;
    private TinyDB tinyDB;
    private ViewFlipper viewFlipper;
    String token;
    private ArrayList<Integer> discountList = new ArrayList<>();

    public HomeFragment(OnFragmentSwitchListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        getListRcmFood(token);
        getLisTopTenFood(token);
        return view;
    }

    public void init(View view) {
        viewFlipper = view.findViewById(R.id.discountView);
        addressTxt = view.findViewById(R.id.addressTxt);
        editAddress = view.findViewById(R.id.editAddress);
        mustTryRcView = view.findViewById(R.id.rcmFoodList);
        topTenRcView = view.findViewById(R.id.topTenList);
        usernameTxt = view.findViewById(R.id.usernameTxt);
        viewAllBtn = view.findViewById(R.id.viewAllBtn);
        tinyDB = new TinyDB(requireContext());
        requestQueue = VolleySingleton.getmInstance(requireContext()).getRequestQueue();
        rcmFoodList = new ArrayList<>();
        topTenFoodList = new ArrayList<>();

        if(tinyDB.getString("user_address") != null){
            String fullAddress = tinyDB.getString("user_address");
            addressTxt.setText(fullAddress);
        }
        if(tinyDB.getBoolean("is_logged_in")){
            String username = tinyDB.getString("username");
            token = tinyDB.getString("token");
            usernameTxt.setText(username);
            int userId = tinyDB.getInt("userId");
            loadAddress(userId);
        }


        Animation inAnimation = AnimationUtils.loadAnimation(requireContext(), android.R.anim.slide_in_left);
        Animation outAnimation = AnimationUtils.loadAnimation(requireContext(), android.R.anim.slide_out_right);

        viewFlipper.setInAnimation(inAnimation);
        viewFlipper.setOutAnimation(outAnimation);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        mustTryRcView.setLayoutManager(layoutManager);
        mustTryRcView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        topTenRcView.setLayoutManager(gridLayoutManager);
        topTenRcView.setHasFixedSize(true);


        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mustTryRcView);

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

        editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), AddressShopActivity.class);
                startActivity(intent);
            }
        });

        viewAllBtn.setOnClickListener(v -> listener.onSwitchToFragment("MENU"));

        viewFlipper.setOnClickListener(v -> listener.onSwitchToFragment("MENU"));
    }

    public void getListRcmFood(String token) {
        String url = UrlUtil.ADDRESS + "products/6";

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
                            String des = jsonObject.isNull("description") ? "" : jsonObject.getString("description");
                            int soldCount = jsonObject.getInt("soldCount");
                            rcmFoodList.add(new Food(id, name, price, image,soldCount, des));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    RcmFoodAdapter adapter = new RcmFoodAdapter(requireContext(), rcmFoodList);
                    mustTryRcView.setAdapter(adapter);
                },
                error -> Toast.makeText(requireContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(jsonArrayRequest);
    }

    public void getLisTopTenFood(String token) {
        String url = UrlUtil.ADDRESS + "products/top-ten-sold";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject resObj = response.getJSONObject("response");
                        JSONArray contentArray = resObj.getJSONArray("content");

                        topTenFoodList.clear();

                        for (int i = 0; i < contentArray.length(); i++) {
                            JSONObject jsonObject = contentArray.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String name = jsonObject.getString("name");
                            int price = jsonObject.getInt("price");
                            String imageId = jsonObject.getString("attachmentId");
                            String image = UrlUtil.ADDRESS + "download/" + imageId;
                            String des = jsonObject.isNull("description") ? "" : jsonObject.getString("description");
                            int soldCount = jsonObject.getInt("soldCount");
                            topTenFoodList.add(new Food(id, name, price, image,soldCount, des));
                        }

                        FoodTopTenAdapter adapter = new FoodTopTenAdapter(requireContext(), topTenFoodList);
                        topTenRcView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Lỗi JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Lỗi mạng: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(jsonObjectRequest);
    }
    public void loadAddress(int userId){

        String url = UrlUtil.ADDRESS + "addresses/primary?userId=" + userId;

        StringRequest jsonObjectRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject address = new JSONObject(response);

                            int id = address.getInt("id");
                            int isPrimary = address.getInt("isPrimary");
                            String addressDetails = address.getString("addressDetails");
                            double latitude = address.getDouble("latitude");
                            double longitude = address.getDouble("longitude");
                            String username = address.getString("username");
                            String sdt = address.getString("sdt");
                            String note = address.getString("note");
                            Address address1 = new Address(id,isPrimary,addressDetails,latitude,longitude,userId,username,note,sdt);
                            tinyDB.putObject("address",address1);
                            addressTxt.setText(addressDetails);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }
    @Override
    public void onResume() {
        super.onResume();
        boolean hasAddress = tinyDB.getAll().containsKey("address");
        boolean hasUAddress = tinyDB.getAll().containsKey("user_address");
        if(hasUAddress){
            String fullAddress = tinyDB.getString("user_address");
            addressTxt.setText(fullAddress);
        }
        if (tinyDB.getBoolean("is_logged_in") && hasAddress) {
            addressTxt.setText(tinyDB.getObject("address", Address.class).getAddress_details());
        }
    }
}
