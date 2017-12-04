package feisabel.espertolock;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by feisabel on 12/4/17.
 */

public class GetHistoryRequest extends StringRequest{
    private static final String LOGIN_URL = "https://aspiratory-suits.000webhostapp.com/GetHistory.php";
    private Map<String, String> params;

    public GetHistoryRequest(Response.Listener<String> listener) {
        super(Method.POST, LOGIN_URL, listener, null);
        params = new HashMap<>();
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
