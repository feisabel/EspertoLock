package feisabel.espertolock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoadingActivity extends AppCompatActivity {
    ArrayList<String> keysArray;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        Response.Listener<String> getKeysResponseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    keysArray = new ArrayList<>();
                    Log.i("tagconvertstr", "["+response+"]");
                    JSONArray jsonResponse = new JSONArray(response);
                    JSONObject object;
                    for(int i = 0; i < jsonResponse.length(); i++) {
                        object = jsonResponse.getJSONObject(i);
                        keysArray.add(object.getString("key"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        GetKeysRequest getKeysRequest = new GetKeysRequest(username, getKeysResponseListener);
        RequestQueue queue = Volley.newRequestQueue(LoadingActivity.this);
        queue.add(getKeysRequest);
    }
}
