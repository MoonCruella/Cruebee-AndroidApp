package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class PaymentMethodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_method);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white, getTheme()));
        getWindow().setStatusBarColor(getResources().getColor(R.color.red, getTheme()));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        RadioButton rbCash = findViewById(R.id.cash);
        RadioButton rbZl = findViewById(R.id.zlpay);
        String method = getIntent().getStringExtra("method");
        if (method != null) {
            if (method.equals(rbCash.getTag())) {
                rbCash.setChecked(true);
            } else if (method.equals(rbZl.getTag())) {
                rbZl.setChecked(true);
            }
        }
        TextView confirm = findViewById(R.id.btn_confirm);
        RadioGroup radioGroup = findViewById(R.id.payment_group);

        confirm.setOnClickListener(view -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = findViewById(selectedId);
            String method1 = selectedRadioButton.getText().toString();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("method", method1);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}