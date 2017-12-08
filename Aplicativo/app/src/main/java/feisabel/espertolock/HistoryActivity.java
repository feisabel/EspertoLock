package feisabel.espertolock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    ArrayList<String> historyArray = new ArrayList<>();
    String username;
    String currentKey;
    String IP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        currentKey = intent.getStringExtra("currentKey");
        IP = intent.getStringExtra("IP");

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.history_item, historyArray);
        final ListView listView = findViewById(R.id.lvHistory);
        listView.setAdapter(adapter);

        Response.Listener<String> getKeysResponseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    historyArray.clear();
                    JSONArray jsonResponse = new JSONArray(response);
                    JSONObject object;
                    for(int i = 0; i < jsonResponse.length(); i++) {
                        object = jsonResponse.getJSONObject(i);
                        historyArray.add(object.getString("time"));
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        GetHistoryRequest getHistoryRequest = new GetHistoryRequest(getKeysResponseListener);
        RequestQueue queue = Volley.newRequestQueue(HistoryActivity.this);
        queue.add(getHistoryRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.lock:
                Intent lockIntent = new Intent(HistoryActivity.this, OpenDoorActivity.class);
                lockIntent.putExtra("username", username);
                lockIntent.putExtra("currentKey", currentKey);
                HistoryActivity.this.startActivity(lockIntent);
                return true;
            case R.id.keys:
                Intent keysIntent = new Intent(HistoryActivity.this, KeysActivity.class);
                keysIntent.putExtra("username", username);
                keysIntent.putExtra("IP", IP);
                keysIntent.putExtra("currentKey", currentKey);
                HistoryActivity.this.startActivity(keysIntent);
                return true;
            case R.id.battery:
                Intent batteryIntent = new Intent(HistoryActivity.this, BatteryActivity.class);
                batteryIntent.putExtra("username", username);
                batteryIntent.putExtra("currentKey", currentKey);
                batteryIntent.putExtra("IP", IP);
                HistoryActivity.this.startActivity(batteryIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
