package me.congresse.ticketsvalidation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class ConfirmationActivity extends AppCompatActivity {

    Button cancelQRCodeButton;
    TextView startTextView;
    TextView eventNameValueTextView;
    TextView eventEditionNameValueTextView;
    TextView userNameValueTextView;
    TextView productNameValueTextView;
    TextView productDescriptionValueTextView;
    Button confirmAttendanceListButton;

    private String PAID_STATUS = "paid";
    private String APPROVED_STATUS = "approved";
    JSONObject jsonObj = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_activity);

        getSupportActionBar().setTitle(R.string.action_bar_text);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.indigo_dye)));
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        cancelQRCodeButton = findViewById(R.id.cancelQRCodeButton);
        eventNameValueTextView = findViewById(R.id.eventNameValueTextView);
        eventEditionNameValueTextView = findViewById(R.id.eventEditionNameValueTextView);
        userNameValueTextView = findViewById(R.id.userNameValueTextView);
        productNameValueTextView = findViewById(R.id.productNameValueTextView);
        productDescriptionValueTextView = findViewById(R.id.productDescriptionValueTextView);
        startTextView = findViewById(R.id.startTextView);
        confirmAttendanceListButton = findViewById(R.id.confirmAttendanceListButton);

        if (getIntent().hasExtra("data")) {
            try {
                jsonObj = new JSONObject(getIntent().getStringExtra("data"));
                if (jsonObj.has("eventEdition")){
                    startTextView.setText(getResources().getString(R.string.found_ticket));
                    eventNameValueTextView.setText(jsonObj.getJSONObject("eventEdition").getJSONObject("event").getString("title"));
                    eventEditionNameValueTextView.setText(jsonObj.getJSONObject("eventEdition").getString("title"));
                    userNameValueTextView.setText(jsonObj.getJSONObject("user").getString("name"));
                    productNameValueTextView.setText(jsonObj.getJSONObject("product").getString("name"));
                    productDescriptionValueTextView.setText(Utils.html2text(jsonObj.getJSONObject("product").getString("description")));
                    if (jsonObj.getString("status").equals(PAID_STATUS) || jsonObj.getString("status").equals(APPROVED_STATUS)) {
                        confirmAttendanceListButton.setVisibility(View.VISIBLE);
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
        }

        cancelQRCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        confirmAttendanceListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()){
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest sr = new StringRequest(Request.Method.POST, Constants.CONGRESSE_ME_API_URL.concat(Constants.ATTENDANCE_LISTS_PATH), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("data", jsonObj.toString());
                            startActivity(intent);
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
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("data", jsonObj.toString());
                                startActivity(intent);
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
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    private void setInvalid() {
        String nothingToShow = getResources().getString(R.string.nothing_to_show);
        startTextView.setText(getResources().getString(R.string.invalid_ticket));
        eventNameValueTextView.setText(nothingToShow);
        eventEditionNameValueTextView.setText(nothingToShow);
        userNameValueTextView.setText(nothingToShow);
        productNameValueTextView.setText(nothingToShow);
        productDescriptionValueTextView.setText(nothingToShow);
        confirmAttendanceListButton.setVisibility(View.INVISIBLE);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
