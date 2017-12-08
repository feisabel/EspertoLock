package feisabel.espertolock;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by feisabel on 12/4/17.
 */

public class AddHistoryRequest extends StringRequest {
    private static final String REGISTER_URL = "https://aspiratory-suits.000webhostapp.com/AddHistory.php";
    private Map<String, String> params;

    public AddHistoryRequest(String username, String key, String time, Response.Listener<String> listener) {
        super(Request.Method.POST, REGISTER_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("key", key);
        params.put("time", time);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
