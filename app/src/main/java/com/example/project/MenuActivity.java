package com.example.project;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.project.adapter.FoodAdapter;
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

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCategory;
    private RecyclerView recyclerView;
    private RequestQueue requestQueue;

    List<Food> foodList;
    private List<Category> categoryList;
    private LinearLayoutManager LinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        recyclerViewCategory = findViewById(R.id.recyclerViewCategory);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //Hide title bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();

        foodList = new ArrayList<>();
        categoryList = new ArrayList<>();
        fetchFoods();
        fetchCategories();

        LinearLayoutManager = new LinearLayoutManager(MenuActivity.this,LinearLayoutManager.HORIZONTAL,false);

        recyclerViewCategory = findViewById(R.id.recyclerViewCategory);
        recyclerViewCategory.setHasFixedSize(true);
        recyclerViewCategory.setLayoutManager(LinearLayoutManager);


    }
    private void fetchCategories(){

        // Lay het tat ca san pham
        String url = UrlUtil.ADDRESS + "categories";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i = 0; i < response.length();i ++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        String imageId = jsonObject.getString("attachmentId");
                        String image = UrlUtil.ADDRESS + "download/" + imageId;
                        Category category = new Category(id,name,image);
                        categoryList.add(category);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    CategoryAdapter adapter = new CategoryAdapter(MenuActivity.this,categoryList);

                    recyclerViewCategory.setAdapter(adapter);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MenuActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }
    private void fetchFoods(){

        // Lay het tat ca san pham
        String url = UrlUtil.ADDRESS + "products";
        System.out.println(url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i = 0; i < response.length();i ++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String name = jsonObject.getString("name");
                        int price = jsonObject.getInt("price");
                        String imageId = jsonObject.getString("attachmentId");
                        String image = UrlUtil.ADDRESS + "download/" + imageId;
                        Food food = new Food(name,price,image);
                        foodList.add(food);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    FoodAdapter adapter = new FoodAdapter(MenuActivity.this,foodList);

                    recyclerView.setAdapter(adapter);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MenuActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

}
