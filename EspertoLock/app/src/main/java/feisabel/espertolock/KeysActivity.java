package feisabel.espertolock;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class KeysActivity extends AppCompatActivity {

    ArrayList<String> keysArray = new ArrayList<>();
    String username;
    String currentKey;
    String IP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keys);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        currentKey = intent.getStringExtra("currentKey");
        IP = intent.getStringExtra("IP");


        final KeysAdapter adapter = new KeysAdapter(this, keysArray, currentKey);
        final ListView listView = findViewById(R.id.lvMainMenu);
        listView.setAdapter(adapter);

        Response.Listener<String> getKeysResponseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    keysArray.clear();
                    JSONArray jsonResponse = new JSONArray(response);
                    JSONObject object;
                    for(int i = 0; i < jsonResponse.length(); i++) {
                        object = jsonResponse.getJSONObject(i);
                        keysArray.add(object.getString("key"));
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        GetKeysRequest getKeysRequest = new GetKeysRequest(username, getKeysResponseListener);
        RequestQueue queue = Volley.newRequestQueue(KeysActivity.this);
        queue.add(getKeysRequest);

        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                currentKey = item;
                adapter.currentKey = item;
                adapter.notifyDataSetChanged();
            }
        };

        listView.setOnItemClickListener(clickListener);

        Button bAddKey = findViewById(R.id.bAddKey);
        bAddKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rand = new Random();
                int k = rand.nextInt(9000) + 1000;
                String key = "" + k;

                RequestQueue ipQueue = Volley.newRequestQueue(KeysActivity.this);
                String url = IP + "s" + key;

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

                ipQueue.add(stringRequest);

                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if(success) {
                                recreate();
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(KeysActivity.this);
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

                AddKeyRequest addKeyRequest = new AddKeyRequest(username, key, responseListener);
                RequestQueue queue = Volley.newRequestQueue(KeysActivity.this);
                queue.add(addKeyRequest);
            }
        });

        Button bSendKey = findViewById(R.id.bSendKey);
        bSendKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dBuilder = new AlertDialog.Builder(KeysActivity.this);
                View dView = getLayoutInflater().inflate(R.layout.dialog, null);
                final EditText etTargetUsername = dView.findViewById(R.id.etTargetUsername);
                Button bSend = dView.findViewById(R.id.bSend);
                Button bCancel = dView.findViewById(R.id.bCancel);
                dBuilder.setView(dView);
                final AlertDialog dialog = dBuilder.create();
                dialog.show();
                bSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Random rand = new Random();
                        int k = rand.nextInt(9000) + 1000;
                        String key = "" + k;

                        RequestQueue ipQueue = Volley.newRequestQueue(KeysActivity.this);
                        String url = IP + "s" + key;

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

                        ipQueue.add(stringRequest);
                        String targetUsername = etTargetUsername.getText().toString();

                        Response.Listener<String> sendKeyResponseListener = new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");

                                    if(success) {
                                    }
                                    else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(KeysActivity.this);
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

                        AddKeyRequest sendKeyRequest = new AddKeyRequest(targetUsername, key, sendKeyResponseListener);
                        RequestQueue queue = Volley.newRequestQueue(KeysActivity.this);
                        queue.add(sendKeyRequest);

                        dialog.dismiss();
                    }
                });
                bCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.keys_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.lock:
                Intent lockIntent = new Intent(KeysActivity.this, OpenDoorActivity.class);
                lockIntent.putExtra("username", username);
                lockIntent.putExtra("currentKey", currentKey);
                KeysActivity.this.startActivity(lockIntent);
                return true;
            case R.id.history:
                Intent historyIntent = new Intent(KeysActivity.this,HistoryActivity.class);
                historyIntent.putExtra("username", username);
                historyIntent.putExtra("currentKey", currentKey);
                historyIntent.putExtra("IP", IP);
                KeysActivity.this.startActivity(historyIntent);
                return true;
            case R.id.battery:
                Intent batteryIntent = new Intent(KeysActivity.this, BatteryActivity.class);
                batteryIntent.putExtra("username", username);
                batteryIntent.putExtra("currentKey", currentKey);
                batteryIntent.putExtra("IP", IP);
                KeysActivity.this.startActivity(batteryIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
