package codi.drive.pasajero.chiclayo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.pasajero.chiclayo.compartido.BorderedCircleTransform;
import codi.drive.pasajero.chiclayo.compartido.PrefUtil;
import codi.drive.pasajero.chiclayo.ws.StringRequestApp;

import static codi.drive.pasajero.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class Registro2Activity extends AppCompatActivity implements Response.ErrorListener, Response.Listener<String> {
    @BindView(R.id.flayCargando) FrameLayout flayCargando;
    @BindView(R.id.ivFoto) ImageView ivFoto;
    AnimatedCircleLoadingView animatedCircleLoadingView;
    PrefUtil prefUtil;
    RequestQueue requestQueue;
    String contenidoImagen = "", dni = "", nombres = "", apellidos = "", celular = "", correo = "", clave = "", ce = "", documento = "";
    int PICK_PERFIL_REQUEST = 5675;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro2);
        ButterKnife.bind(this);
        animatedCircleLoadingView = (AnimatedCircleLoadingView) findViewById(R.id.circle_loading_view_r2);
        prefUtil = new PrefUtil(this);
        requestQueue = Volley.newRequestQueue(this);
        if (!contenidoImagen.equals("")) {
            Picasso.get()
                    .load(getImageUri(this, getBitmap(contenidoImagen)))
                    .placeholder(R.drawable.icono_pasajero)
                    .transform(new BorderedCircleTransform(80, Color.rgb(1, 40, 105)))
                    .into(ivFoto);
        } else {
            Picasso.get()
                    .load(R.drawable.icono_pasajero)
                    .placeholder(R.drawable.icono_pasajero)
                    .transform(new BorderedCircleTransform(80, Color.rgb(1, 40, 105)))
                    .into(ivFoto);
        }
        if (getIntent().getExtras() != null) {
            dni = getIntent().getStringExtra("dni");
            nombres = getIntent().getStringExtra("nombres");
            apellidos = getIntent().getStringExtra("apellidos");
            celular = getIntent().getStringExtra("celular");
            correo = getIntent().getStringExtra("correo");
            clave = getIntent().getStringExtra("clave");
            ce = getIntent().getStringExtra("ce");
            documento = getIntent().getStringExtra("documento");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PERFIL_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                contenidoImagen = getStringImage(bitmap);
                Picasso.get()
                        .load(getImageUri(this, bitmap))
                        .transform(new BorderedCircleTransform(50, Color.rgb(1, 40, 105)))
                        .into(ivFoto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean correcto = jsonObject.getBoolean("correcto");
            if (correcto) {
                animatedCircleLoadingView.stopOk();
                flayCargando.removeAllViews();
                prefUtil.saveGenericValue("foto", jsonObject.getString("foto"));
                Intent intent = new Intent(Registro2Activity.this, MainActivity.class);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(intent);
                finish();
            } else {
                Log.i("PerfilActivity",  "onResponse - " + jsonObject.getString("error"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnFinalizarRegistro) void registrarPersona() {
        Log.i("registrarPersona", "Registro2Activity");
        if (animatedCircleLoadingView.getParent() != null) {
            ((ViewGroup) animatedCircleLoadingView.getParent()).removeView(animatedCircleLoadingView);
            flayCargando.addView(animatedCircleLoadingView);
            animatedCircleLoadingView.startIndeterminate();
        }
        new Thread() {
            @Override
            public void run() {
                String result = "";
                if (documento.equals("DNI")) {
                    result = primero("http://codidrive.com/vespro/logica/registro_persona.php?apellidos=" + apellidos + "&nombres=" + nombres + "&nro_documento=" + dni
                                + "&email=" + correo + "&telefono=" + celular);
                    prefUtil.saveGenericValue("nro_documento", dni);
                } else if (documento.equals("CE")) {
                    result = primero("http://codidrive.com/vespro/logica/registro_persona.php?apellidos=" + apellidos + "&nombres=" + nombres + "&nro_documento=" + ce
                            + "&email=" + correo + "&telefono=" + celular);
                    prefUtil.saveGenericValue("nro_documento", ce);
                }
                Log.i("registrarPersona", result);
                String finalResult = result;
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(finalResult);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                prefUtil.saveGenericValue("apellidos", apellidos);
                                prefUtil.saveGenericValue("nombres", nombres);
                                prefUtil.saveGenericValue("correo", correo);
                                prefUtil.saveGenericValue("celular", celular);
                                obtenerIdPersona();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    @OnClick({R.id.tvSubirFoto, R.id.ivFoto, R.id.flaySubirFoto}) void buscarFoto() {
        Log.i("buscarFoto", "Registro2Activity");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccione Imagen"), PICK_PERFIL_REQUEST);
    }

    public Bitmap getBitmap(String imagenContent) {
        String base64Image = imagenContent;
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void agregarLlamada(Map<String, String> params){
        StringRequestApp jsObjRequest = new StringRequestApp(Request.Method.POST, "http://46.101.226.155/vespro/logica/modificar_foto_pasajero.php", this,this);
        jsObjRequest.setParametros(params);
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsObjRequest);
    }

    public void obtenerIdPasajero() {
        Log.i("obtenerIdPasajero", "Registro2Activity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/obtener_id_pasajero.php?idpersona=" + prefUtil.getStringValue("idPersona"));
                Log.i("obtenerIdPasajero", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            String idPasajero = jsonArray.getJSONObject(0).getString("idpasajero");
                            prefUtil.saveGenericValue("idPasajero", idPasajero);
                            registrarUsuario();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void obtenerIdPersona() {
        Log.i("obtenerIdPersona", "Registro2Activity");
        new Thread() {
            @Override
            public void run() {
                String result = "";
                if (documento.equals("DNI")) {
                    result = primero("http://codidrive.com/vespro/logica/obtener_id_persona.php?apellidos=" + apellidos + "&nombres=" + nombres + "&nro_documento=" + dni
                            + "&email=" + correo + "&telefono=" + celular);
                } else if (documento.equals("CE")) {
                    result = primero("http://codidrive.com/vespro/logica/obtener_id_persona.php?apellidos=" + apellidos + "&nombres=" + nombres + "&nro_documento=" + ce
                            + "&email=" + correo + "&telefono=" + celular);
                }
                Log.i("obtenerIdPersona", result);
                String finalResult = result;
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(finalResult);
                        if (jsonArray.length() > 0) {
                            String idPersona = jsonArray.getJSONObject(0).getString("idpersona");
                            prefUtil.saveGenericValue("idPersona", idPersona);
                            registrarPasajero();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void registrarPasajero() {
        Log.i("registrarPasajero", "Registro2Activity");
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/registro_pasajero.php?idpersona=" + prefUtil.getStringValue("idPersona") + "&fregistro="
                        + dateFormat.format(date) + "&tipo_pasajero=CN");
                Log.i("registrarPasajero", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                obtenerIdPasajero();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void subirFoto() {
        Log.i("subirFoto", "Registro2Activity");
        Map<String, String> params = new HashMap<String, String>();
        params.put("idpasajero", prefUtil.getStringValue("idPasajero"));
        params.put("foto", contenidoImagen);
        agregarLlamada(params);
    }

    public void registrarUsuario() {
        Log.i("registrarUsuario", "Registro2Activity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/registrar_usuario.php?login=" + prefUtil.getStringValue("nro_documento") + "&password=" + clave
                        + "&idperfil=3&idpersona=" + prefUtil.getStringValue("idPersona") + "&estado=N");
                Log.i("registrarUsuario", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                subirFoto();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }
}
