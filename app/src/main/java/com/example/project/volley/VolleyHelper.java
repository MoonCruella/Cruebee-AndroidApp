package com.example.project.volley;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.LoginActivity;
import com.example.project.R;
import com.example.project.helpers.TinyDB;
import com.example.project.interfaces.DialogCloseHandler;
import com.example.project.utils.UrlUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class VolleyHelper {
    private static VolleyHelper instance;
    private final RequestQueue requestQueue;
    private final TinyDB tinyDB;
    private final Context context;
    private boolean isDialogShown ;
    private boolean isRefreshingToken = false;
    private final Queue<Runnable> retryQueue = new LinkedList<>();
    private DialogCloseHandler dialogCloseHandler;

    public void setDialogCloseHandler(DialogCloseHandler handler) {
        this.dialogCloseHandler = handler;
    }

    public VolleyHelper(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        this.tinyDB = new TinyDB(context);
        this.isDialogShown = false;
    }

    public static synchronized VolleyHelper getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyHelper(context);
        }
        return instance;
    }
    public void sendJsonObjectRequestWithAuth(
            int method,
            String url,
            JSONObject body,
            boolean requireAuth,
            Response.Listener<JSONObject> listener,
            Response.ErrorListener errorListener
    ) {
        JsonObjectRequest request = new JsonObjectRequest(method, url, body, listener, error -> {
            if (requireAuth && error.networkResponse != null && error.networkResponse.statusCode == 401) {
                enqueueRequestAfterRefresh(() -> {
                    sendJsonObjectRequestWithAuth(method, url, body, true, listener, errorListener);
                });
            } else {
                errorListener.onErrorResponse(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (requireAuth) {
                    headers.put("Authorization", "Bearer " + tinyDB.getString("token"));
                }
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data, StandardCharsets.UTF_8);
                    return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
                } catch (Exception e) {
                    return Response.error(new ParseError(e));
                }
            }
        };

        requestQueue.add(request);
    }


    // Gửi yêu cầu với JsonArrayRequest và xác thực token
    public void sendJsonArrayRequestWithAuth(
            String url,
            boolean requireAuth,
            Response.Listener<JSONArray> listener,
            Response.ErrorListener errorListener
    ) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, listener, error -> {
            if (requireAuth && error.networkResponse != null && error.networkResponse.statusCode == 401) {
                // Nếu token hết hạn, refresh token và thử lại yêu cầu
                enqueueRequestAfterRefresh(() -> {
                    sendJsonArrayRequestWithAuth(url, true, listener, errorListener);
                });
            } else {
                errorListener.onErrorResponse(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (requireAuth) {
                    headers.put("Authorization", "Bearer " + tinyDB.getString("token"));
                }
                return headers;
            }
        };

        requestQueue.add(request);
    }

    public void sendStringRequestWithAuth(
            int method,
            String url,
            String body,
            boolean requireAuth,
            Response.Listener<String> listener,
            Response.ErrorListener errorListener
    ) {
        StringRequest request = new StringRequest(method, url, listener, error -> {
            if (requireAuth && error.networkResponse != null && error.networkResponse.statusCode == 401) {
                enqueueRequestAfterRefresh(() -> {
                    sendStringRequestWithAuth(method, url, body, true, listener, errorListener);
                });
            } else {
                errorListener.onErrorResponse(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (requireAuth) {
                    headers.put("Authorization", "Bearer " + tinyDB.getString("token"));
                }
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return body != null ? body.getBytes() : null;
            }
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String utf8 = new String(response.data, StandardCharsets.UTF_8);
                    return Response.success(utf8, HttpHeaderParser.parseCacheHeaders(response));
                } catch (Exception e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json"; // hoặc "application/x-www-form-urlencoded" nếu bạn dùng kiểu form
            }
        };

        requestQueue.add(request);
    }

    private void refreshToken(Runnable onSuccess) {
        String refreshToken = tinyDB.getString("refresh_token");

        String url = UrlUtil.ADDRESS + "refresh-token";
        StringRequest refreshRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        tinyDB.putString("token", obj.getString("accessToken"));
                        tinyDB.putString("refresh_token", obj.getString("refreshToken"));
                        Log.e(TAG, "TOKEN REFRESHED.");
                        Toast.makeText(context, "Đã làm mới token", Toast.LENGTH_SHORT).show();
                        onSuccess.run(); // Gọi lại các request bị chặn
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showErrorDialog(); // thêm dòng này để catch lỗi JSON
                    }
                },
                error -> {
                    Log.e(TAG, "Refresh token failed: " + error.toString());

                    // ĐÓNG dialog TẠI ĐÂY nếu cần
                    if (dialogCloseHandler != null) {
                        dialogCloseHandler.closeCurrentDialog();
                    }
                    showErrorDialog();

                    synchronized (VolleyHelper.this) {
                        retryQueue.clear();
                        isRefreshingToken = false;
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + refreshToken);
                return headers;
            }
        };

        requestQueue.add(refreshRequest);
    }

    private synchronized void enqueueRequestAfterRefresh(Runnable request) {
        retryQueue.add(request);
        if (!isRefreshingToken) {
            isRefreshingToken = true;
            refreshToken(() -> {
                synchronized (VolleyHelper.this) {
                    while (!retryQueue.isEmpty()) {
                        retryQueue.poll().run();
                    }
                    isRefreshingToken = false;
                }
            });
        }
    }
    private synchronized void showErrorDialog() {

        if (isDialogShown) return;

        isDialogShown = true;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_error, null);
        TextView errorClose = view.findViewById(R.id.errorClose);
        TextView errorDes = view.findViewById(R.id.errorDes);
        errorDes.setText("Phiên đăng nhập đã hết. Vui lòng đăng nhập lại.");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        errorClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Xoá thông tin đăng nhập
                tinyDB.remove("token");
                tinyDB.remove("savedUser");
                tinyDB.remove("addressShop");
                tinyDB.remove("refresh_token");
                tinyDB.putBoolean("is_logged_in", false);
                alertDialog.dismiss();
                isDialogShown = false;
                // Chuyển về LoginActivity
                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }
}
