package feisabel.espertolock;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by feisabel on 12/3/17.
 */

public class AddKeyRequest extends StringRequest {

    private static final String REGISTER_URL = "https://aspiratory-suits.000webhostapp.com/AddKey.php";
    private Map<String, String> params;

    public AddKeyRequest(String username, String key, Response.Listener<String> listener) {
        super(Request.Method.POST, REGISTER_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("key", key);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
