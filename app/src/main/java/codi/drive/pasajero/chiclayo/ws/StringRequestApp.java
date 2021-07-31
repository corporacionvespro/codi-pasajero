package codi.drive.pasajero.chiclayo.ws;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.Map;

/**
 * By: El Bryant
 */

public class StringRequestApp extends StringRequest {
    private Map parametros;

    public StringRequestApp(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public void setParametros(Map mParametros){
        parametros = mParametros;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> params = parametros;
        return params;
    }
}
