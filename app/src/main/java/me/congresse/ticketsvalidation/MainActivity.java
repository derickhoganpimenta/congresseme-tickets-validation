package me.congresse.ticketsvalidation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.HashMap;

import me.congresse.ticketsvalidation.utils.Constants;
import me.congresse.ticketsvalidation.utils.Utils;

public class MainActivity extends AppCompatActivity {
    Button scanBarcodeButton;
    ImageView userImageView;
    TextView welcomeFirstMessageTextView;
    TextView welcomeSecondMessageTextView;

    private String PAID_STATUS = "paid";
    private String APPROVED_STATUS = "approved";
    JSONObject jsonObj = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        scanBarcodeButton = findViewById(R.id.scanBarcodeButton);
        userImageView = findViewById(R.id.userImageView);
        welcomeFirstMessageTextView = findViewById(R.id.welcomeFirstTextTextView);
        welcomeSecondMessageTextView = findViewById(R.id.welcomeSecondTextTextView);

        getSupportActionBar().setTitle(R.string.action_bar_text);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.indigo_dye)));
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (getIntent().hasExtra("data")) {
            try {
                jsonObj = new JSONObject(getIntent().getStringExtra("data"));
                if (jsonObj.has("eventEdition")){
                    if (jsonObj.getString("status").equals(PAID_STATUS) || jsonObj.getString("status").equals(APPROVED_STATUS)) {
                        userImageView.setImageResource(R.drawable.ic_approved);
                        welcomeFirstMessageTextView.setText(R.string.first_message_successful_user_verification);
                        welcomeSecondMessageTextView.setText(R.string.second_message_successful_user_verification);
                    } else {
                        setInvalid();
                    }
                } else {
                    setInvalid();
                }

            } catch (JSONException e) {
                setInvalid();
                e.printStackTrace();
            }
        } else {
            userImageView.setImageResource(R.drawable.ic_user);
            welcomeFirstMessageTextView.setText(R.string.welcome_first_text);
            welcomeSecondMessageTextView.setText(R.string.welcome_second_text);
        }

        scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ReadQRCodeActivity.class));
            }
        });

    }
    private void setInvalid() {
        userImageView.setImageResource(R.drawable.ic_denied);
        welcomeFirstMessageTextView.setText("");
        welcomeSecondMessageTextView.setText("");
    }
}