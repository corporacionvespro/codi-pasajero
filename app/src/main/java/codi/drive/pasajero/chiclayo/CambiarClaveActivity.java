package codi.drive.pasajero.chiclayo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.pasajero.chiclayo.compartido.PrefUtil;
import static codi.drive.pasajero.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class CambiarClaveActivity extends AppCompatActivity {
    @BindView(R.id.etNuevaClave) EditText etNuevaClave;
    PrefUtil prefUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_clave);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
    }

    @OnClick(R.id.btnGuardarCambios) void cambiarClave() {
        Log.i("cambiarClave", "CambiarClaveActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/ws/cambiar_clave.php?idusuario=" + prefUtil.getStringValue("idUsuario") + "&clave="
                        + etNuevaClave.getText().toString());
                Log.i("cambiarClave", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        boolean correcto = jsonObject.getBoolean("correcto");
                        if (correcto) {
                            Toast.makeText(CambiarClaveActivity.this, "Contraseña actualizada satisfactoriamente", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CambiarClaveActivity.this, PerfilActivity.class));
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
        startActivity(new Intent(CambiarClaveActivity.this, PerfilActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
