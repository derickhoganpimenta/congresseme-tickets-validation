package me.congresse.ticketsvalidation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import me.congresse.ticketsvalidation.utils.Utils;

public class MainActivity extends AppCompatActivity {
    Button btnScanBarcode;
    TextView startTextView;
    TextView eventNameValueTextView;
    TextView eventEditionNameValueTextView;
    TextView userNameValueTextView;
    TextView productNameValueTextView;
    TextView productDescriptionValueTextView;
    ImageView statusImageView;
    private String PAID_STATUS = "paid";
    private String APPROVED_STATUS = "approved";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        btnScanBarcode = findViewById(R.id.btnScanBarcode);
        eventNameValueTextView = findViewById(R.id.eventNameValueTextView);
        eventEditionNameValueTextView = findViewById(R.id.eventEditionNameValueTextView);
        userNameValueTextView = findViewById(R.id.userNameValueTextView);
        productNameValueTextView = findViewById(R.id.productNameValueTextView);
        productDescriptionValueTextView = findViewById(R.id.productDescriptionValueTextView);
        startTextView = findViewById(R.id.startTextView);
        statusImageView = findViewById(R.id.statusImageView);

        if (getIntent().hasExtra("data")) {
            try {
                JSONObject jsonObj = new JSONObject(getIntent().getStringExtra("data"));
                if (jsonObj.has("eventEdition")){
                    startTextView.setText("Ticket encontrado");
                    eventNameValueTextView.setText(jsonObj.getJSONObject("eventEdition").getJSONObject("event").getString("title"));
                    eventEditionNameValueTextView.setText(jsonObj.getJSONObject("eventEdition").getString("title"));
                    userNameValueTextView.setText(jsonObj.getJSONObject("user").getString("name"));
                    productNameValueTextView.setText(jsonObj.getJSONObject("product").getString("name"));
                    productDescriptionValueTextView.setText(Utils.html2text(jsonObj.getJSONObject("product").getString("description")));

                    if (jsonObj.getString("status").equals(PAID_STATUS) || jsonObj.getString("status").equals(APPROVED_STATUS)) {
                        statusImageView.setImageResource(R.drawable.ic_aproved);
                    } else {
                        statusImageView.setImageResource(R.drawable.ic_denied);
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

        btnScanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ReadQRCode.class));
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
    }
}