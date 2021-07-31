package codi.drive.pasajero.chiclayo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.pasajero.chiclayo.compartido.CircleTransform;
import codi.drive.pasajero.chiclayo.compartido.PrefUtil;
import static codi.drive.pasajero.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class FinalizarPedidoActivity extends AppCompatActivity {
    @BindView(R.id.etOpinion) EditText etOpinion;
    @BindView(R.id.ivFotoConductor) ImageView ivFotoConductor;
    @BindView(R.id.rbOpinion) RatingBar rbOpinion;
    @BindView(R.id.tvAgregarFrecuente) TextView tvAgregarFrecuente;
    @BindView(R.id.tvApellidosConductor) TextView tvApellidosConductor;
    @BindView(R.id.tvNombresConductor) TextView tvNombresConductor;
    @BindView(R.id.tvTarifa) TextView tvTarifa;
    public static Double latitud, longitud;
    PrefUtil prefUtil;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setContentView(R.layout.activity_finalizar_pedido);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
        Picasso.get().load(prefUtil.getStringValue("fotoConductor")).transform(new CircleTransform()).into(ivFotoConductor);
        String nombres = prefUtil.getStringValue("nombres");
        char[] caracteres_apellidos = (prefUtil.getStringValue("apellidosConductor").toLowerCase()).toCharArray();
        caracteres_apellidos[0] = Character.toUpperCase(caracteres_apellidos[0]);
        for (int i = 0; i < (prefUtil.getStringValue("apellidosConductor").toLowerCase()).length(); i ++) {
            if (caracteres_apellidos[i] == ' ') {
                if (i < prefUtil.getStringValue("apellidosConductor").length() - 1) {
                    caracteres_apellidos[i + 1] = Character.toUpperCase(caracteres_apellidos[i + 1]);
                }
            }
            tvApellidosConductor.setText("" + tvApellidosConductor.getText().toString() + caracteres_apellidos[i]);
        }
        char[] caracteres_nombres = (prefUtil.getStringValue("nombresConductor").toLowerCase()).toCharArray();
        caracteres_nombres[0] = Character.toUpperCase(caracteres_nombres[0]);
        for (int i = 0; i < (prefUtil.getStringValue("nombresConductor").toLowerCase()).length(); i ++) {
            if (caracteres_nombres[i] == ' ') {
                if (i < prefUtil.getStringValue("nombresConductor").length() - 1) {
                    caracteres_nombres[i + 1] = Character.toUpperCase(caracteres_nombres[i + 1]);
                }
            }
            tvNombresConductor.setText("" + tvNombresConductor.getText().toString() + caracteres_nombres[i]);
        }
        tvTarifa.setText("S/ " + String.format("%.2f", Double.parseDouble(prefUtil.getStringValue("pagoFinal"))));
        if (getIntent().getDoubleExtra("latitud", 0.0) > 0 && getIntent().getDoubleExtra("longitud", 0.0) > 0) {
            tvAgregarFrecuente.setVisibility(View.VISIBLE);
            latitud = getIntent().getDoubleExtra("latitud", 0.0);
            longitud = getIntent().getDoubleExtra("longitud", 0.0);
        }
    }

    @OnClick(R.id.btnAceptar) void EnviarOpinion() {
        Log.i("enviarOpinion", "click");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/enviar_valoracion_de_pasajero.php?idsolicitud=" + prefUtil.getStringValue("idSolicitud")
                        + "&valoracion=" + rbOpinion.getRating() + "&opinion=" + etOpinion.getText().toString());
                Log.i("enviarOpinion", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.length() > 0) {
                         boolean correcto = jsonObject.getBoolean("correcto");
                         if (correcto) {
                             Log.i("EnviarOpinion", "enviado satisfactoriamente");
                         }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        Toast.makeText(this, "Muchas gracias por su opinión", Toast.LENGTH_SHORT).show();
        prefUtil.saveGenericValue("idSolicitud", "");
        Intent intent = new Intent(FinalizarPedidoActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @OnClick(R.id.tvAgregarFrecuente) void agregarFrecuente() {
        Log.i("agregarFrecuente", "FinalizarPedidoActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/agregar_frecuente.php?latitud=" + latitud + "&longitud=" + longitud + "&nombre="
                        + prefUtil.getStringValue("direccionDestino") + "&idusuario=" + prefUtil.getStringValue("idUsuario"));
                Log.i("agregarFrecuente", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                tvAgregarFrecuente.setVisibility(View.GONE);
                                Toast.makeText(FinalizarPedidoActivity.this, "Agregado a lugares favoritos satisfactoriamente", Toast.LENGTH_SHORT).show();
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
