package codi.drive.pasajero.chiclayo.ws;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import codi.drive.pasajero.chiclayo.compartido.PrefUtil;

/**
 * By: El Bryant
 */

public class wsApp implements Response.Listener, Response.ErrorListener {
    private Activity actividad;
    private final static String URL_WS = "http://46.101.226.155/vespro/ws/ws_app.php";
    private Fragment fragment;
    private PrefUtil prefUtil;
    private RequestQueue colaRequest;

    public wsApp(Context ctx, boolean servicio) {
        colaRequest= Volley.newRequestQueue(ctx);
        prefUtil = new PrefUtil(ctx);
    }

    public wsApp(Activity mActividad) {
        this.actividad = mActividad;
        colaRequest = Volley.newRequestQueue(actividad);
        prefUtil = new PrefUtil(actividad);
    }

    public wsApp(Fragment mFragment){
        this.fragment = mFragment;
        this.actividad = mFragment.getActivity();
        colaRequest = Volley.newRequestQueue(Objects.requireNonNull(actividad));
        prefUtil = new PrefUtil(actividad);
    }

    private void agregarLlamada(Map params){
        StringRequestApp jsObjRequest = new StringRequestApp(Request.Method.POST, URL_WS, this,this);
        params.put("app", "teslapasajero");
        jsObjRequest.setParametros(params);
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        colaRequest.add(jsObjRequest);
    }

    public void crearCuenta() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("accion", "CREAR_CUENTA_PASAJERO");
        params.put("nombres", prefUtil.getStringValue("ini_nombres"));
        params.put("apellidos", prefUtil.getStringValue("ini_apellidos"));
        params.put("dni", prefUtil.getStringValue("ini_dni"));
        params.put("contrasena", prefUtil.getStringValue("ini_password"));
        params.put("email", prefUtil.getStringValue("ini_email"));
        params.put("celular", prefUtil.getStringValue("ini_celular"));
        params.put("imei", prefUtil.getStringValue("ini_imei"));
        params.put("localidad", prefUtil.getStringValue("ini_localidad"));
        params.put("foto", prefUtil.getStringValue("ini_foto"));
        agregarLlamada(params);
    }

    public void recuperarCuenta(String nombres, String apellidos, String dni) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("accion", "RECUPERAR_CUENTA_CONDUCTOR");
        params.put("apellidos", apellidos);
        params.put("nombres", nombres);
        params.put("dni", dni);
        agregarLlamada(params);
    }

    public void editarCuenta(String foto) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("accion", "EDITAR_CUENTA_PASAJERO");
        params.put("idpersona", prefUtil.getStringValue("idpersona"));
        params.put("idpasajero", prefUtil.getStringValue("idpasajero"));
        params.put("foto", foto);
        agregarLlamada(params);
    }

    public void getData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("accion", "GET_DATA_PASAJERO");
        params.put("idusuario", prefUtil.getStringValue("idusuario"));
        params.put("idpersona", prefUtil.getStringValue("idpersona"));
        params.put("idsolicitud", prefUtil.getStringValue("idsolicitud"));
        params.put("login", prefUtil.getStringValue("login"));
        params.put("password", prefUtil.getStringValue("password"));
        agregarLlamada(params);
    }

    public void enviarSolicitudTaxi(double latOrigen, double longOrigen, String latDestino, String longDestino, String direccionDestino, String direccionOrigen, String referencia, String referenciaDestino,
                                    double tarifa, double idtarifario, int idlugar, int intento) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("accion", "PEDIR_TAXI");
        params.put("idusuario", prefUtil.getStringValue("idusuario"));
        params.put("idpasajero", prefUtil.getStringValue("idpasajero"));
        params.put("latorigen", String.valueOf(latOrigen));
        params.put("longorigen", String.valueOf(longOrigen));
        params.put("latdestino", latDestino);
        params.put("longdestino", longDestino);
        params.put("direcciondestino", direccionDestino);
        params.put("direccionorigen", direccionOrigen);
        params.put("referencia", referencia);
        params.put("referencia_destino", referenciaDestino);
        params.put("tarifa", String.valueOf(tarifa));
        params.put("idtarifario", String.valueOf(idtarifario));
        params.put("nombrepasajero", prefUtil.getStringValue("nombres")+" "+prefUtil.getStringValue("apellidos"));
        params.put("token_pasajero", Objects.requireNonNull(FirebaseInstanceId.getInstance().getToken()));
        params.put("fotopasajero", prefUtil.getStringValue("foto"));
        params.put("idlugar", String.valueOf(idlugar));
        params.put("idsolicitud", prefUtil.getStringValue("idsolicitud"));
        params.put("intento", String.valueOf(intento));
        params.put("valoracion", prefUtil.getStringValue("valoracion"));
        agregarLlamada(params);
    }

    public void actualizarMensaje(int idnotificacion, int idusuario) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("accion", "UPDATE_MENSAJE");
        params.put("idnotificacion", String.valueOf(idnotificacion));
        params.put("idusuario", String.valueOf(idusuario));
        agregarLlamada(params);
    }

    public void consultarPromocionPasajero(String codigo) {
        Map<String, String> params= new HashMap<String, String>();
        params.put("accion", "VERIFICAR_PROMOCION_PASAJERO");
        params.put("idpasajero", prefUtil.getStringValue("idpasajero"));
        params.put("codigo", codigo);
        agregarLlamada(params);
    }

    public void actualizarPromocionPasajero(int idpromocion, String estado) {
        Map<String, String> params= new HashMap<String, String>();
        params.put("accion", "ACTUALIZAR_PROMOCION_PASAJERO");
        params.put("idpromocion", String.valueOf(idpromocion));
        params.put("estado", estado);
        agregarLlamada(params);
    }

    public void consultarHistorial(String fechaDesde, String fechaHasta) {
        Map<String, String> params= new HashMap<String, String>();
        params.put("accion", "CONSULTAR_HISTORIAL_CONDUCTOR");
        params.put("idconductor", "0");
        params.put("idpasajero", prefUtil.getStringValue("idpasajero"));
        params.put("fechadesde", fechaDesde);
        params.put("fechahasta", fechaHasta);
        agregarLlamada(params);
    }

    public void actualizarConductoresDisponibles() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("accion", "GET_CONDUCTORES_DISPONIBLES");
        params.put("idusuario", prefUtil.getStringValue("idusuario"));
        params.put("idpasajero", prefUtil.getStringValue("idpasajero"));
        params.put("latitud", prefUtil.getStringValue("latitud"));
        params.put("longitud", prefUtil.getStringValue("longitud"));
        agregarLlamada(params);
    }

    public void SolicitudAbordo(String idsolicitud) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("accion", "SOLICITUD_A_BORDO");
        params.put("idsolicitud", idsolicitud);
        params.put("idusuario", prefUtil.getStringValue("idusuario"));
        params.put("idpasajero", prefUtil.getStringValue("idpasajero"));
        params.put("idconductor", "0");
        params.put("latitud", prefUtil.getStringValue("latitud"));
        params.put("longitud", prefUtil.getStringValue("longitud"));
        params.put("enviadopor", "P");
        agregarLlamada(params);
    }

//    public void finalizarSolicitudTaxi(String idsolicitud) {
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("accion", "FINALIZAR_SOLICITUD_TAXI");
//        params.put("idsolicitud", idsolicitud);
//        params.put("idusuario", prefUtil.getStringValue("idusuario"));
//        params.put("idpasajero", prefUtil.getStringValue("idpasajero"));
//        params.put("idconductor", "0");
//        params.put("latitud", prefUtil.getStringValue("latitud"));
//        params.put("longitud", prefUtil.getStringValue("longitud"));
//        params.put("enviadopor", "P");
//        agregarLlamada(params);
//    }

    public void enviarOpinion(String idsolicitud, float rating, String opinion) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("accion", "ENVIAR_OPINION");
        params.put("idsolicitud", idsolicitud);
        params.put("idusuario", prefUtil.getStringValue("idusuario"));
        params.put("idpasajero", prefUtil.getStringValue("idpasajero"));
        params.put("idconductor", "0");
        params.put("valoracion", String.valueOf(rating));
        params.put("opinion", String.valueOf(opinion));
        agregarLlamada(params);
    }

    public void cancelarSolicitudPasajero() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("accion", "CANCELAR_SOLICITUD_PASAJERO");
        params.put("idsolicitud", prefUtil.getStringValue("idsolicitud"));
        agregarLlamada(params);
    }

    @Override
    public void onResponse(Object response) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(response.toString());
            procesarLlamada(obj);
        } catch (Exception e) {
            Log.i("wsApp-onResponse", e.getMessage());
        }
    }

    private void procesarLlamada(JSONObject json){
        try {
            String accion = json.getString("accion");
            JSONObject data = null;
            boolean correcto = false;
            switch (accion) {
                case "GET_DATA_PASAJERO":
                    correcto = json.getBoolean("correcto");
                    if(correcto) {
                        //GET DATOS USUARIO
                        JSONObject usuario = json.getJSONObject("usuario");
                        prefUtil.saveGenericValue("idusuario", usuario.getString("idusuario"));
                        prefUtil.saveGenericValue("login", usuario.getString("login"));
                        prefUtil.saveGenericValue("password", usuario.getString("password"));
                        prefUtil.saveGenericValue("estado", usuario.getString("estado"));
                        prefUtil.saveGenericValue("foto", usuario.getString("foto"));
                        prefUtil.saveGenericValue("idperfil", usuario.getString("idperfil"));
                        prefUtil.saveGenericValue("idpersona", usuario.getString("idpersona"));
                        prefUtil.saveGenericValue("apellidos", usuario.getString("apellidos"));
                        prefUtil.saveGenericValue("nombres", usuario.getString("nombres"));
                        prefUtil.saveGenericValue("dni", usuario.getString("dni"));
                        prefUtil.saveGenericValue("email", usuario.getString("email"));
                        prefUtil.saveGenericValue("telefono", usuario.getString("telefono"));
                        prefUtil.saveGenericValue("iddistrito", usuario.getString("iddistrito"));
                        prefUtil.saveGenericValue("distrito", usuario.getString("distrito"));
                        prefUtil.saveGenericValue("idprovincia", usuario.getString("idprovincia"));
                        prefUtil.saveGenericValue("provincia", usuario.getString("provincia"));
                        prefUtil.saveGenericValue("iddepartamento", usuario.getString("iddepartamento"));
                        prefUtil.saveGenericValue("departamento", usuario.getString("departamento"));
                        prefUtil.saveGenericValue("idpasajero", usuario.getString("idpasajero"));
                        prefUtil.saveGenericValue(PrefUtil.LOGIN_STATUS,"1");
                        //NUMEROS SOS
                        prefUtil.saveGenericValue("numeros", json.getString("numeros"));
                        //VALORACION
                        prefUtil.saveGenericValue("valoracion", json.getString("valoracion"));
                        //ACERCA_APP
                        JSONObject acercaapp = json.getJSONObject("acercaapp");
                        prefUtil.saveGenericValue("app_acerca", acercaapp.getString("acerca"));
                        prefUtil.saveGenericValue("app_ganar", acercaapp.getString("ganar"));
                        prefUtil.saveGenericValue("app_requerimiento", acercaapp.getString("requerimiento"));
                        prefUtil.saveGenericValue("app_conectarse", acercaapp.getString("conectarse"));
                        prefUtil.saveGenericValue("app_politicas", acercaapp.getString("politicas"));
                    }
                    break;
                case "CREAR_CUENTA_PASAJERO":
                    correcto = json.getBoolean("correcto");
                    if (correcto) {
                        //MensajeError("Cuenta creada satisfactoriamente");
                        prefUtil.clearAll();
                        prefUtil.saveGenericValue(PrefUtil.PASAJERO_STATUS,"0");
                        //GET DATOS USUARIO
                        JSONObject usuario = json.getJSONObject("pasajero");
                        prefUtil.saveGenericValue("idusuario", usuario.getString("idusuario"));
                        prefUtil.saveGenericValue("login", usuario.getString("login"));
                        prefUtil.saveGenericValue("password", usuario.getString("password"));
                        prefUtil.saveGenericValue("estado", usuario.getString("estado"));
                        prefUtil.saveGenericValue("foto", usuario.getString("foto"));
                        prefUtil.saveGenericValue("idperfil", usuario.getString("idperfil"));
                        prefUtil.saveGenericValue("idpersona", usuario.getString("idpersona"));
                        prefUtil.saveGenericValue("apellidos", usuario.getString("apellidos"));
                        prefUtil.saveGenericValue("nombres", usuario.getString("nombres"));
                        prefUtil.saveGenericValue("dni", usuario.getString("dni"));
                        prefUtil.saveGenericValue("email", usuario.getString("email"));
                        prefUtil.saveGenericValue("telefono", usuario.getString("telefono"));
                        prefUtil.saveGenericValue("iddistrito", usuario.getString("iddistrito"));
                        prefUtil.saveGenericValue("distrito", usuario.getString("distrito"));
                        prefUtil.saveGenericValue("idprovincia", usuario.getString("idprovincia"));
                        prefUtil.saveGenericValue("provincia", usuario.getString("provincia"));
                        prefUtil.saveGenericValue("iddepartamento", usuario.getString("iddepartamento"));
                        prefUtil.saveGenericValue("departamento", usuario.getString("departamento"));
                        prefUtil.saveGenericValue("idpasajero", usuario.getString("idpasajero"));
                        prefUtil.saveGenericValue("idsolicitud", "0");
                        prefUtil.saveGenericValue(PrefUtil.LOGIN_STATUS, "1");
                    } else {
                        MensajeError("Error al crear cuenta. " + json.getString("error"));
                    }
                    break;
                case "PEDIR_TAXI":
                    correcto = json.getBoolean("correcto");
                    if (correcto) {
                        MensajeError("Solicitud enviada.");
                        prefUtil.saveGenericValue("idsolicitud", json.getString("idsolicitud"));
                    } else {
                        prefUtil.saveGenericValue("finalizado","1");
                        prefUtil.saveGenericValue("servicio_abordo","0");
                        prefUtil.saveGenericValue("idsolicitud","0");
                    }
                    break;
                case "SOS":
                    correcto = json.getBoolean("correcto");
                    if (correcto) {
                        MensajeError("Ayuda en camino");
                    } else {
                        MensajeError("Intente nuevamente");
                    }
                    break;
                case "ACTUALIZAR_ESTADO_CONDUCTOR":
                    correcto = json.getBoolean("correcto");
                    if (!correcto) {
                        MensajeError("Se sincronizarán datos al concetarse a internet");
                    }
                    break;
                case "UPDATE_MENSAJE":
                case "EDITAR_CUENTA_PASAJERO":
                    correcto = json.getBoolean("correcto");
                    if (correcto) {
                        MensajeError("Foto actualizada satisfactoriamente");
                        prefUtil.clearAll();
                        prefUtil.saveGenericValue(PrefUtil.PASAJERO_STATUS,"0");
                        //GET DATOS USUARIO
                        JSONObject usuario = json.getJSONObject("pasajero");
                        prefUtil.saveGenericValue("idusuario", usuario.getString("idusuario"));
                        prefUtil.saveGenericValue("login", usuario.getString("login"));
                        prefUtil.saveGenericValue("password", usuario.getString("password"));
                        prefUtil.saveGenericValue("estado", usuario.getString("estado"));
                        prefUtil.saveGenericValue("foto", usuario.getString("foto"));
                        prefUtil.saveGenericValue("idperfil", usuario.getString("idperfil"));
                        prefUtil.saveGenericValue("idpersona", usuario.getString("idpersona"));
                        prefUtil.saveGenericValue("apellidos", usuario.getString("apellidos"));
                        prefUtil.saveGenericValue("nombres", usuario.getString("nombres"));
                        prefUtil.saveGenericValue("dni", usuario.getString("dni"));
                        prefUtil.saveGenericValue("email", usuario.getString("email"));
                        prefUtil.saveGenericValue("telefono", usuario.getString("telefono"));
                        prefUtil.saveGenericValue("iddistrito", usuario.getString("iddistrito"));
                        prefUtil.saveGenericValue("distrito", usuario.getString("distrito"));
                        prefUtil.saveGenericValue("idprovincia", usuario.getString("idprovincia"));
                        prefUtil.saveGenericValue("provincia", usuario.getString("provincia"));
                        prefUtil.saveGenericValue("iddepartamento", usuario.getString("iddepartamento"));
                        prefUtil.saveGenericValue("departamento", usuario.getString("departamento"));
                        prefUtil.saveGenericValue("idpasajero", usuario.getString("idpasajero"));
                        prefUtil.saveGenericValue(PrefUtil.LOGIN_STATUS,"1");
                    } else {
                        MensajeError("Error al editar cuenta. " + json.getString("error"));
                    }
                    break;
                case "FINALIZAR_SOLICITUD_TAXI":
                case "SOLICITUD_A_BORDO":
                case "ENVIAR_OPINION":
                case "CANCELAR_SOLICITUD_PASAJERO":
                    break;
                default:
                    Log.i("wsApp-procesarLlamada", "no se ha definido procesamiento de respuesta");
            }
        } catch (JSONException e) {
            MensajeError(e.getMessage());
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        String message = "";
        if (volleyError instanceof NetworkError) {
            message = "No se puede conectar a Internet... Por favor, compruebe su conexión!";
        } else if (volleyError instanceof ServerError) {
            message = "No se pudo encontrar el servidor. ¡Inténtalo nuevamente dentro de unos minutos!" + volleyError.getMessage();
        } else if (volleyError instanceof AuthFailureError) {
            message = "No se puede conectar a Internet ... Por favor, compruebe su conexión!";
        } else if (volleyError instanceof ParseError) {
            message = "¡Error! ¡Inténtalo nuevamente dentro de unos minutos!";
        } else if (volleyError instanceof NoConnectionError) {
            message = "No se puede conectar a Internet ... Por favor, compruebe su conexión!";
        } else if (volleyError instanceof TimeoutError) {
            message = "¡El tiempo de conexión expiro! Por favor revise su conexion a internet.";
        } else {
            message = "¡Error en obtención de datos! " + volleyError.getMessage();
        }
        Log.i("wsApp-onErrorResponse", message);
    }

    private void MensajeError(String msj) {
        Log.i("wsApp-MensajeError", msj);
    }
}