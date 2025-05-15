package com.example.project.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project.R;
import com.example.project.helpers.TinyDB;


public class PaymentMethodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        ConstraintLayout mainLayout = findViewById(R.id.main);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.red));
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemInsets.top, 0, systemInsets.bottom); // tránh cả status và navigation bar
            return insets;
        });
        RadioButton rbCash = findViewById(R.id.cash);
        RadioButton rbZl = findViewById(R.id.zlpay);
        TinyDB tinyDB = new TinyDB(this);
        if(tinyDB.getBoolean("is_logged_in")){
            rbCash.setVisibility(VISIBLE);
        }
        else{
            rbCash.setVisibility(GONE);
        }
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