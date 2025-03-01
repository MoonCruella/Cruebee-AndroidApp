package com.example.project;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.model.Category;
import com.example.project.model.CategoryAdapter;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCategory;
    private CategoryAdapter categoryAdapter;
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

        // D? li?u danh m?c
        categoryList = new ArrayList<>();
        categoryList.add(new Category(1,"Gà rán", R.drawable.avt,true));
        categoryList.add(new Category(2,"Burger", R.drawable.avt,true));
        categoryList.add(new Category(3,"Spaghetti", R.drawable.avt,true));
        categoryList.add(new Category(4,"Th?c u?ng", R.drawable.avt,true));

        LinearLayoutManager = new LinearLayoutManager(MenuActivity.this,LinearLayoutManager.HORIZONTAL,false);

        categoryAdapter = new CategoryAdapter(categoryList);
        recyclerViewCategory.setAdapter(categoryAdapter);
        
    }
}
