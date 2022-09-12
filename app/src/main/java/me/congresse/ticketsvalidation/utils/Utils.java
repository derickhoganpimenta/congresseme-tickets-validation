package me.congresse.ticketsvalidation.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.text.Html;
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

import me.congresse.ticketsvalidation.R;

public class Utils extends AppCompatActivity {
    public static String html2text(String html) {
        return Html.fromHtml(html).toString();
    }
}
