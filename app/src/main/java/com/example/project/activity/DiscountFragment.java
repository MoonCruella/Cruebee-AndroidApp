package com.example.project.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.project.R;
import com.example.project.adapter.PromotionAdapter;
import com.example.project.model.Promotion;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DiscountFragment extends Fragment {
    private RecyclerView recyclerView;
    private PromotionAdapter adapter;
    private List<Promotion> promotionList;
    private RequestQueue requestQueue;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discount, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        requestQueue = VolleySingleton.getmInstance(requireContext()).getRequestQueue();
        promotionList = new ArrayList<>();
        adapter = new PromotionAdapter(promotionList, requireContext());
        recyclerView.setAdapter(adapter);

        getListPromotion();
        return view;
    }

    public void getListPromotion() {
        String url = UrlUtil.ADDRESS + "promotions";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    String title = jsonObject.getString("title");
                    String description = jsonObject.getString("description");
                    String imageId = jsonObject.getString("attachmentId");
                    String image = UrlUtil.ADDRESS + "download/" + imageId;
                    promotionList.add(new Promotion( title, description, image));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            adapter = new PromotionAdapter(promotionList, requireContext());
            recyclerView.setAdapter(adapter);
        }, error -> Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show());

        requestQueue.add(jsonArrayRequest);
    }


}