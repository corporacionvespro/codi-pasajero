package codi.drive.pasajero.chiclayo.compartido;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * By: El Bryant
 */

public class PrefUtil {
    private Context context;
    private static final String VERSION_CODE = "version_code", NAME_PREFERENCE = "version_code", PREFIJO = "codi_pasajero_";
    public static final String LOGIN_STATUS = "login_status", PASAJERO_STATUS = "conductor_status", URL_USER = "http://d30y9cdsu7xlg0.cloudfront.net/png/212110-200.png",
            URL_PORTADA = "http://s.hswstatic.com/gif/taxi-meters-2.jpg", BROADCAST_SOLICITUD_TAXI = "BroadcastSolicitudTaxi", BROADCAST_ACTUALIZAR_UBICACION = "BroadcastActualizarUbicacion";

    // Constructor
    public PrefUtil(Context ctx) {
        this.context = ctx;
    }

    public void saveGenericValue(String campo, String valor) {
        SharedPreferences prefs = context.getSharedPreferences(NAME_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFIJO + campo, valor);
        editor.apply();
    }

    public void saveAccessToken(String token) {
        SharedPreferences prefs = context.getSharedPreferences(NAME_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fb_access_token", token);
        editor.apply(); // This line is IMPORTANT !!!
    }

    public void clearAll() {
        SharedPreferences prefs = context.getSharedPreferences(NAME_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply(); // This line is IMPORTANT !!!
    }

    public void getFacebookUserInfo(){
        SharedPreferences prefs = context.getSharedPreferences(NAME_PREFERENCE, Context.MODE_PRIVATE);
        Log.d("MyApp", "Name : " + prefs.getString("fb_name",null) + "\nEmail : " + prefs.getString("fb_email",null));
    }

    public String getStringValue(String campo){
        SharedPreferences prefs = context.getSharedPreferences(NAME_PREFERENCE, Context.MODE_PRIVATE);
        return prefs.getString(PREFIJO + campo,"");
    }

    public long getVersionCode(){
        SharedPreferences prefs = context.getSharedPreferences(NAME_PREFERENCE, Context.MODE_PRIVATE);
        return prefs.getLong(VERSION_CODE, -1);
    }

    public void setVersionCode(long versionCode){
        SharedPreferences prefs = context.getSharedPreferences(NAME_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(VERSION_CODE, versionCode);
        editor.apply();
    }
}