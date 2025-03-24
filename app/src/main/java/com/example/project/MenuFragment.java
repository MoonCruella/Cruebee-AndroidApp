    package com.example.project;

    import android.os.Bundle;
    import android.util.DisplayMetrics;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;
    import androidx.recyclerview.widget.DividerItemDecoration;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.LinearSmoothScroller;
    import androidx.recyclerview.widget.RecyclerView;

    import com.android.volley.Request;
    import com.android.volley.RequestQueue;
    import com.android.volley.Response;
    import com.android.volley.VolleyError;
    import com.android.volley.toolbox.JsonArrayRequest;
    import com.example.project.adapter.CategoryListAdapter;
    import com.example.project.interfaces.OnCategoryScrollListener;
    import com.example.project.interfaces.OnItemClickListener;
    import com.example.project.model.Category;
    import com.example.project.adapter.CategoryAdapter;
    import com.example.project.model.Food;
    import com.example.project.utils.UrlUtil;
    import com.example.project.volley.VolleySingleton;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    import java.util.ArrayList;
    import java.util.LinkedHashMap;
    import java.util.List;
    import java.util.Map;

    public class MenuFragment extends Fragment {

        private RecyclerView recyclerViewCategory;
        private OnCategoryScrollListener onCategoryScrollListener;
        private RecyclerView recyclerView;
        private RequestQueue requestQueue;
        private List<Food> foodList;
        private List<Category> categoryList1;
        private List<Category> categoryList;
        CategoryAdapter adapter;
        private Map<Integer, Category> categoryMap = new LinkedHashMap<>();
        private int position;
        private int totalCategories = 0; // ??m t?ng s? danh m?c c?n t?i
        private int loadedCategories = 0; // ??m s? danh m?c ?� t?i xong m�n ?n

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
                    totalCategories = response.length();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String name = jsonObject.getString("name");
                            String imageId = jsonObject.getString("attachmentId");
                            String image = UrlUtil.ADDRESS + "download/" + imageId;
                            Category category = new Category(id, name, image);
                            categoryList1.add(category);
                            categoryMap.put(id, new Category(name, new ArrayList<>()));
                            // Tien hanh fetch cac san pham thuoc category nay
                            fetchFoodByCategoryId(id, name);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        adapter = new CategoryAdapter(getContext(), categoryList1, new OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                                    private static final float MILLISECONDS_PER_INCH = 5f;
                                    @Override
                                    protected int getVerticalSnapPreference() {
                                        return SNAP_TO_START; // Ensures the item scrolls to the top
                                    }
                                    @Override
                                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                                        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                                    }
                                };
                                // Set the target position to scroll to
                                smoothScroller.setTargetPosition(position);

                                // Smoothly scroll to the position
                                layoutManager.startSmoothScroll(smoothScroller);

                                LinearLayoutManager layoutManagerCate = (LinearLayoutManager) recyclerViewCategory.getLayoutManager();
                                LinearSmoothScroller smoothScrollerCate = new LinearSmoothScroller(recyclerViewCategory.getContext()) {
                                    @Override
                                    protected int getHorizontalSnapPreference() {
                                        return SNAP_TO_START; // Ensures the item scrolls to the top
                                    }
                                };

                                // Set the target position to scroll to
                                smoothScrollerCate.setTargetPosition(position);

                                // Smoothly scroll to the position
                                layoutManagerCate.startSmoothScroll(smoothScrollerCate);
                            }
                        });
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
                    categoryMap.put(categoryId, category);

                    loadedCategories++; // ??m s? danh m?c ?� t?i xong
                    checkAndUpdateRecyclerView();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            requestQueue.add(jsonArrayRequest);
        }
        private void checkAndUpdateRecyclerView() {
            if (loadedCategories == totalCategories) { // Khi t?t c? danh m?c ?� t?i xong
                List<Category> orderedCategoryList = new ArrayList<>(categoryMap.values());

                CategoryListAdapter itemAdapter = new CategoryListAdapter(getContext(), orderedCategoryList, new OnCategoryScrollListener() {
                    @Override
                    public void onCategoryScrolled(int position) {
                        // C?p nh?t RecyclerView Category khi cu?n danh s�ch m�n ?n
                        if (adapter != null) {
                            adapter.selectedPosition = position;
                            adapter.notifyDataSetChanged();

                            // Cu?n danh m?c v? ?�ng v? tr�
                            LinearLayoutManager layoutManagerCate = (LinearLayoutManager) recyclerViewCategory.getLayoutManager();
                            LinearSmoothScroller smoothScrollerCate = new LinearSmoothScroller(recyclerViewCategory.getContext()) {
                                private static final float MILLISECONDS_PER_INCH = 1f;
                                @Override
                                protected int getHorizontalSnapPreference() {
                                    return SNAP_TO_START; // Ensures the item scrolls to the top
                                }
                                @Override
                                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                                }
                            };

                            // Set the target position to scroll to
                            smoothScrollerCate.setTargetPosition(position);

                            // Smoothly scroll to the position
                            layoutManagerCate.startSmoothScroll(smoothScrollerCate);
                        }
                    }
                });
                recyclerView.setAdapter(itemAdapter);
                itemAdapter.attachScrollListenerToRecyclerView(recyclerView);

                RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
                recyclerView.addItemDecoration(decoration);
            }
        }

    }