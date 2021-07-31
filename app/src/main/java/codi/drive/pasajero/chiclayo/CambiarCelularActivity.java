package codi.drive.pasajero.chiclayo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.pasajero.chiclayo.compartido.PrefUtil;
import static codi.drive.pasajero.chiclayo.compartido.Funciones.primero;

public class CambiarCelularActivity extends AppCompatActivity {
    @BindView(R.id.etNuevoCelular) EditText etNuevoCelular;
    PrefUtil prefUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_celular);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
    }

    @OnClick(R.id.btnGuardarCambios) void cambiarClave() {
        Log.i("cambiarClave", "cambiarCelularActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/actualizar_celular.php?idpersona=" + prefUtil.getStringValue("idPersona")
                        + "&celular=" + etNuevoCelular.getText().toString());
                Log.i("cambiarClave", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        boolean correcto = jsonObject.getBoolean("correcto");
                        if (correcto) {
                            prefUtil.saveGenericValue("telefono", etNuevoCelular.getText().toString());
                            Toast.makeText(CambiarCelularActivity.this, "Datos actualizados satisfactoriamente", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CambiarCelularActivity.this, PerfilActivity.class));
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CambiarCelularActivity.this, PerfilActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}