package feisabel.espertolock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BatteryActivity extends AppCompatActivity {
    String username;
    String currentKey;
    String IP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        currentKey = intent.getStringExtra("currentKey");
        IP = intent.getStringExtra("IP");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.battery_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.lock:
                Intent lockIntent = new Intent(BatteryActivity.this, OpenDoorActivity.class);
                lockIntent.putExtra("username", username);
                lockIntent.putExtra("currentKey", currentKey);
                BatteryActivity.this.startActivity(lockIntent);
                return true;
            case R.id.history:
                Intent historyIntent = new Intent(BatteryActivity.this, HistoryActivity.class);
                historyIntent.putExtra("username", username);
                historyIntent.putExtra("currentKey", currentKey);
                historyIntent.putExtra("IP", IP);
                BatteryActivity.this.startActivity(historyIntent);
                return true;
            case R.id.keys:
                Intent keysIntent = new Intent(BatteryActivity.this, KeysActivity.class);
                keysIntent.putExtra("username", username);
                keysIntent.putExtra("IP", IP);
                keysIntent.putExtra("currentKey", currentKey);
                BatteryActivity.this.startActivity(keysIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
