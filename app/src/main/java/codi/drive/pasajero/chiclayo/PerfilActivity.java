package codi.drive.pasajero.chiclayo;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
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

public class PerfilActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Response.ErrorListener, Response.Listener<String> {
    @BindView(R.id.btnCambiarClave) Button btnCambiarClave;
    @BindView(R.id.dlayMenu) DrawerLayout dlayMenu;
    @BindView(R.id.flayCargando) FrameLayout flayCargando;
    @BindView(R.id.ivFoto) ImageView ivFoto;
    @BindView(R.id.nvMenu) NavigationView nvMenu;
    @BindView(R.id.rbValoracion) RatingBar rbValoracion;
    @BindView(R.id.tvApellidos) TextView tvApellidos;
    @BindView(R.id.tvCambiarFoto) TextView tvCambiarFoto;
    @BindView(R.id.tvCelular) TextView tvCelular;
    @BindView(R.id.tvCorreo) TextView tvCorreo;
    @BindView(R.id.tvDni) TextView tvDni;
    @BindView(R.id.tvNombres) TextView tvNombres;
    ImageView ivFotoUsuario;
    PrefUtil prefUtil;
    RequestQueue requestQueue;
    String contenidoImagen = "";
    SwitchCompat swNoche;
    TextView tvNombreUsuario;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
        requestQueue = Volley.newRequestQueue(this);
        Menu menu = nvMenu.getMenu();
        ivFotoUsuario = (ImageView) nvMenu.getHeaderView(0).findViewById(R.id.ivFotoUsuario);
        tvNombreUsuario = (TextView) nvMenu.getHeaderView(0).findViewById(R.id.tvNombreUsuario);
        swNoche = (SwitchCompat) MenuItemCompat.getActionView(menu.findItem(R.id.nav_noche)).findViewById(R.id.swNoche);
        swNoche.setChecked(prefUtil.getStringValue("noche").equals("SI"));
        swNoche.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                prefUtil.saveGenericValue("noche", "SI");
            } else {
                prefUtil.saveGenericValue("noche", "NO");
            }
        });
        nvMenu.setNavigationItemSelectedListener(this);
        Picasso.get().load(prefUtil.getStringValue("foto")).transform(new BorderedCircleTransform(5, Color.rgb(0, 214, 209))).into(ivFoto);
        Picasso.get().load(prefUtil.getStringValue("foto")).transform(new BorderedCircleTransform(5, Color.rgb(0, 214, 209))).into(ivFotoUsuario);
        String apellidos = prefUtil.getStringValue("apellidos");
        char[] caracteres_apellidos = (apellidos.toLowerCase()).toCharArray();
        caracteres_apellidos[0] = Character.toUpperCase(caracteres_apellidos[0]);
        for (int i = 0; i < (apellidos.toLowerCase()).length(); i ++) {
            if (caracteres_apellidos[i] == ' ') {
                if (i < apellidos.length() - 1) {
                    caracteres_apellidos[i + 1] = Character.toUpperCase(caracteres_apellidos[i + 1]);
                }
            }
            tvApellidos.setText("" + tvApellidos.getText().toString() + caracteres_apellidos[i]);
        }
        tvDni.setText(prefUtil.getStringValue("nro_documento"));
        tvCelular.setText(prefUtil.getStringValue("telefono"));
        tvCorreo.setText(prefUtil.getStringValue("correo"));
        String nombres = prefUtil.getStringValue("nombres");
        char[] caracteres_nombres = (nombres.toLowerCase()).toCharArray();
        caracteres_nombres[0] = Character.toUpperCase(caracteres_nombres[0]);
        for (int i = 0; i < (nombres.toLowerCase()).length(); i ++) {
            if (caracteres_nombres[i] == ' ') {
                if (i < nombres.length() - 1) {
                    caracteres_nombres[i + 1] = Character.toUpperCase(caracteres_nombres[i + 1]);
                }
            }
            tvNombres.setText("" + tvNombres.getText().toString() + caracteres_nombres[i]);
        }
        tvNombreUsuario.setText(tvNombres.getText().toString() + " " + tvApellidos.getText().toString());
        promedioEstrellas();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_acerca:
                startActivity(new Intent(PerfilActivity.this, AcercaAppActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_cerrar:
                prefUtil.clearAll();
                startActivity(new Intent(PerfilActivity.this, Acceso1Activity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_compartir:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                String msj = "Te invito a descargar la App CODI DRIVE PASAJERO y solicitar servicio de taxi de forma segura. Atrévete a vivir la experiencia." +
                        " https://play.google.com/store/apps/details?id=codi.drive.pasajero.chiclayo ";
                intent.putExtra(Intent.EXTRA_TEXT, msj);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Compartir en"));
                break;
            case R.id.nav_frecuentes:
                startActivity(new Intent(PerfilActivity.this, FavoritosActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_historial:
                startActivity(new Intent(PerfilActivity.this, HistorialActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_inicio:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_lugares:
                startActivity(new Intent(PerfilActivity.this, CategoriaActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_perfil:
                dlayMenu.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_promociones:
                startActivity(new Intent(PerfilActivity.this, PromocionesActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
        }
        dlayMenu.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5675 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                contenidoImagen = getStringImage(bitmap);
                Picasso.get().load(getImageUri(this, bitmap)).transform(new BorderedCircleTransform(5, Color.rgb(0, 214, 209))).rotate(270).into(ivFoto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
        Log.i("PerfilActivity", "onErrorResponse - " + error);
    }

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean correcto = jsonObject.getBoolean("correcto");
            if (correcto) {
                tvCambiarFoto.setText("Cambiar foto");
                prefUtil.saveGenericValue("foto", jsonObject.getString("foto"));
                Picasso.get().load(jsonObject.getString("foto")).transform(new BorderedCircleTransform(5, Color.parseColor("#00d6d1"))).into(ivFotoUsuario);
            } else {
                Log.i("PerfilActivity",  "onResponse - " + jsonObject.getString("error"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.acvMenu) void abrirMenu()  {
        dlayMenu.openDrawer(GravityCompat.START);
    }

    @OnClick(R.id.btnActualizarCelular) void actualizarCelular() {
        startActivity(new Intent(PerfilActivity.this, CambiarCelularActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @OnClick(R.id.btnCambiarClave) void cambiarClave() {
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(PerfilActivity.this, Pair.create(btnCambiarClave, btnCambiarClave.getTransitionName()));
        startActivity(new Intent(PerfilActivity.this, CambiarClaveActivity.class), activityOptions.toBundle());
        finish();
    }

    @OnClick(R.id.tvCambiarFoto) void cambiarFoto() {
        if (tvCambiarFoto.getText().toString().equals("Cambiar foto")) {
            tvCambiarFoto.setText("Guardar cambios");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Seleccione Imagen"), 5675);
        } else if (tvCambiarFoto.getText().toString().equals("Guardar cambios")) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("idpasajero", prefUtil.getStringValue("idPasajero"));
            params.put("foto", contenidoImagen);
            agregarLlamada(params);
        }
    }

    private void agregarLlamada(Map<String, String> params){
        StringRequestApp jsObjRequest = new StringRequestApp(Request.Method.POST, "http://46.101.226.155/vespro/logica/modificar_foto_pasajero.php", this,this);
        jsObjRequest.setParametros(params);
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsObjRequest);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 25, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }

    public void promedioEstrellas() {
        Log.i("promedioEstrellas", "click");
        Thread tr = new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/promedio_estrellas_pasajero.php?idpasajero=" + prefUtil.getStringValue("idPasajero"));
                Log.i("promedioEstrellas", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            DecimalFormat df = new DecimalFormat("0.00");
                            float promedio = Float.parseFloat(df.format(jsonArray.getJSONObject(0).getDouble("promedio")).replace(",", "."));
                            rbValoracion.setRating(promedio);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        };
        tr.start();
    }
}