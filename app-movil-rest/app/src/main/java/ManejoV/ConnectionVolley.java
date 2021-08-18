package ManejoV;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ConnectionVolley {
    private static ConnectionVolley InstanciaConnection;
    private RequestQueue requestQueue;
    private static Context context;

    private ConnectionVolley(Context context){
        this.context=context;
        requestQueue = getRequestQueue();
    }

    public static synchronized ConnectionVolley getInstancia(Context context) {
        if (InstanciaConnection == null) {
            InstanciaConnection = new ConnectionVolley(context);
        }
        return InstanciaConnection;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

}
