package feisabel.espertolock;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by feisabel on 12/3/17.
 */

public class GetKeysRequest extends StringRequest {
    private static final String LOGIN_URL = "https://aspiratory-suits.000webhostapp.com/GetKeys.php";
    private Map<String, String> params;
    String username;

    public GetKeysRequest(String username, Response.Listener<String> listener) {
        super(Method.POST, LOGIN_URL, listener, null);
        this.username = username;
    }

    @Override
    public Map<String, String> getParams() {
        params = new HashMap<>();
        params.put("username", username);
        return params;
    }
}
