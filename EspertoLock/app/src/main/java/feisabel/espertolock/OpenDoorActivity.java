package feisabel.espertolock;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OpenDoorActivity extends AppCompatActivity {
    String username;
    String IP = "http://192.168.2.101/";
    String currentKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_door);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        if(intent.hasExtra("currentKey")) {
            currentKey = intent.getStringExtra("currentKey");
        }

        final ImageButton ibOpenDoor = findViewById(R.id.ibOpenDoor);

        ibOpenDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                Date today = Calendar.getInstance().getTime();
                String time = df.format(today);

                RequestQueue queue = Volley.newRequestQueue(OpenDoorActivity.this);
                String url = IP + "u" + currentKey;

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                queue.add(stringRequest);

                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if(success) {
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(OpenDoorActivity.this);
                                builder.setMessage("Adding new key failed")
                                        .setNegativeButton("Ok", null)
                                        .create()
                                        .show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                AddHistoryRequest addHistoryRequest = new AddHistoryRequest(username, currentKey, time, responseListener);
                RequestQueue queue2 = Volley.newRequestQueue(OpenDoorActivity.this);
                queue2.add(addHistoryRequest);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.keys:
                Intent keysIntent = new Intent(OpenDoorActivity.this, KeysActivity.class);
                keysIntent.putExtra("username", username);
                keysIntent.putExtra("IP", IP);
                keysIntent.putExtra("currentKey", currentKey);
                OpenDoorActivity.this.startActivity(keysIntent);
                return true;
            case R.id.history:
                Intent historyIntent = new Intent(OpenDoorActivity.this, HistoryActivity.class);
                historyIntent.putExtra("username", username);
                historyIntent.putExtra("IP", IP);
                historyIntent.putExtra("currentKey", currentKey);
                OpenDoorActivity.this.startActivity(historyIntent);
                return true;
            case R.id.battery:
                Intent batteryIntent = new Intent(OpenDoorActivity.this, BatteryActivity.class);
                batteryIntent.putExtra("username", username);
                batteryIntent.putExtra("IP", IP);
                batteryIntent.putExtra("currentKey", currentKey);
                OpenDoorActivity.this.startActivity(batteryIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
