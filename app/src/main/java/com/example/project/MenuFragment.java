package com.example.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.project.adapter.CategoryListAdapter;
import com.example.project.model.Category;
import com.example.project.adapter.CategoryAdapter;
import com.example.project.model.Food;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {

    private RecyclerView recyclerViewCategory;
    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private List<Food> foodList;
    private List<Category> categoryList1;
    private List<Category> categoryList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);


        recyclerViewCategory = view.findViewById(R.id.recyclerViewCategory);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategory.setHasFixedSize(true);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        requestQueue = VolleySingleton.getmInstance(getContext()).getRequestQueue();
        categoryList = new ArrayList<>();
        categoryList1 = new ArrayList<>();
        fetchCategoryList();
        return view;
    }

    public void fetchCategoryList() {

        // Fetch all categories
        String url = UrlUtil.ADDRESS + "categories";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        String imageId = jsonObject.getString("attachmentId");
                        String image = UrlUtil.ADDRESS + "download/" + imageId;
                        Category category = new Category(id, name, image);
                        categoryList1.add(category);

                        // Tien hanh fetch cac san pham thuoc category nay
                        fetchFoodByCategoryId(id, name);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    CategoryAdapter adapter = new CategoryAdapter(getContext(), categoryList1);
                    recyclerViewCategory.setAdapter(adapter);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    public void fetchFoodByCategoryId(final int categoryId, final String categoryName) {

        // Fetch all products
        String url = UrlUtil.ADDRESS + "products/" + categoryId;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                foodList = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        int price = jsonObject.getInt("price");
                        String imageId = jsonObject.getString("attachmentId");
                        String image = UrlUtil.ADDRESS + "download/" + imageId;
                        String des = jsonObject.getString("description");
                        Food food = new Food(id, name, price, image, des);
                        foodList.add(food);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Category category = new Category(categoryName, foodList);
                categoryList.add(category);

                // Sau khi lay duoc tat ca san pham cua 1 category,luc nay ta co du thong tin catename va listprouct =>  tien hanh set Adapter
                CategoryListAdapter adapter = new CategoryListAdapter(getContext(), categoryList);
                RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
                recyclerView.addItemDecoration(decoration);
                recyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

}
