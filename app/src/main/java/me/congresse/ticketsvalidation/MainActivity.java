package me.congresse.ticketsvalidation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    TextView startTextView;
    TextView eventNameValueTextView;
    TextView eventEditionNameValueTextView;
    TextView userNameValueTextView;
    TextView productNameValueTextView;
    TextView productDescriptionValueTextView;
    ImageView statusImageView;
    Button confirmAttendanceListButton;

    private String PAID_STATUS = "paid";
    private String APPROVED_STATUS = "approved";
    JSONObject jsonObj = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        scanBarcodeButton = findViewById(R.id.scanBarcodeButton);
        eventNameValueTextView = findViewById(R.id.eventNameValueTextView);
        eventEditionNameValueTextView = findViewById(R.id.eventEditionNameValueTextView);
        userNameValueTextView = findViewById(R.id.userNameValueTextView);
        productNameValueTextView = findViewById(R.id.productNameValueTextView);
        productDescriptionValueTextView = findViewById(R.id.productDescriptionValueTextView);
        startTextView = findViewById(R.id.startTextView);
        statusImageView = findViewById(R.id.statusImageView);
        confirmAttendanceListButton = findViewById(R.id.confirmAttendanceListButton);

        if (getIntent().hasExtra("data")) {
            try {
                jsonObj = new JSONObject(getIntent().getStringExtra("data"));
                if (jsonObj.has("eventEdition")){
                    startTextView.setText("Ticket encontrado");
                    eventNameValueTextView.setText(jsonObj.getJSONObject("eventEdition").getJSONObject("event").getString("title"));
                    eventEditionNameValueTextView.setText(jsonObj.getJSONObject("eventEdition").getString("title"));
                    userNameValueTextView.setText(jsonObj.getJSONObject("user").getString("name"));
                    productNameValueTextView.setText(jsonObj.getJSONObject("product").getString("name"));
                    productDescriptionValueTextView.setText(Utils.html2text(jsonObj.getJSONObject("product").getString("description")));

                    if (jsonObj.getString("status").equals(PAID_STATUS) || jsonObj.getString("status").equals(APPROVED_STATUS)) {
                        statusImageView.setImageResource(R.drawable.ic_aproved);
                        confirmAttendanceListButton.setVisibility(View.VISIBLE);
                    } else {
                        statusImageView.setImageResource(R.drawable.ic_denied);
                        confirmAttendanceListButton.setVisibility(View.INVISIBLE);
                    }
                    statusImageView.setVisibility(View.VISIBLE);
                } else {
                    setInvalid();
                }

            } catch (JSONException e) {
                setInvalid();
                e.printStackTrace();
            }
        }

        scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ReadQRCodeActivity.class));
            }
        });

        confirmAttendanceListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest sr = new StringRequest(Request.Method.POST, Constants.CONGRESSE_ME_API_URL.concat(Constants.ATTENDANCE_LISTS_PATH), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.confirmed_attendance_list), Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            int statusCode = error.networkResponse.statusCode;
                            String responseBody = null;
                            try {
                                responseBody = new String(error.networkResponse.data, "utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            JSONObject data = null;
                            try {
                                data = new JSONObject(responseBody);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String message = data.optString("message");
                            if (statusCode == HttpURLConnection.HTTP_CONFLICT) {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            }
                        }
                    }) {
                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            HashMap<String, String> params = new HashMap<String, String>();
                            try {
                                params.put("user_id", String.valueOf(jsonObj.getString("user_id")));
                                params.put("event_edition_id", String.valueOf(jsonObj.getString("event_edition_id")));
                                params.put("product_id", String.valueOf(jsonObj.getString("product_id")));
                                System.out.println(new JSONObject(params));

                                return new JSONObject(params).toString().getBytes();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return new byte[0];
                        }

                        @Override
                        public String getBodyContentType() {
                            return "application/json";
                        }
                    };
                    queue.add(sr);
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, Constants.REQUEST_INTERNET_PERMISSION);
                }
            }
        });
    }

    private void setInvalid() {
        String nothingToShow = getResources().getString(R.string.nothing_to_show);
        startTextView.setText("Ticket inválido");
        eventNameValueTextView.setText(nothingToShow);
        eventEditionNameValueTextView.setText(nothingToShow);
        userNameValueTextView.setText(nothingToShow);
        productNameValueTextView.setText(nothingToShow);
        productDescriptionValueTextView.setText(nothingToShow);
        statusImageView.setImageResource(R.drawable.ic_denied);
        statusImageView.setVisibility(View.VISIBLE);
        confirmAttendanceListButton.setVisibility(View.INVISIBLE);
    }
}