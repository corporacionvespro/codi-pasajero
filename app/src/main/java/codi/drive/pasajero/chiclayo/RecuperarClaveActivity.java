package codi.drive.pasajero.chiclayo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import static codi.drive.pasajero.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class RecuperarClaveActivity extends AppCompatActivity {
    @BindView(R.id.etRecuperarCelular) EditText etRecuperarCelular;
    @BindView(R.id.etRecuperarCorreo) EditText etRecuperarCorreo;
    @BindView(R.id.etRecuperarDni) EditText etRecuperarDni;
    @BindView(R.id.ivIsotipo) ImageView ivIsotipo;
    @BindView(R.id.tvRecuperarClave) TextView tvRecuperarclave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_clave);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnRecuperar) void recuperar() {
        Log.i("recuperar", "LoginActivity");
        Thread tr = new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/recuperar_clave.php?dni=" + etRecuperarDni.getText().toString() + "&celular="
                        + etRecuperarCelular.getText().toString() + "&correo=" + etRecuperarCorreo.getText().toString());
                Log.i("recuperar", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            String idUsuario = jsonArray.getJSONObject(0).getString("idusuario");
                            actualizarClave(idUsuario);
                        } else {
                            Toast.makeText(RecuperarClaveActivity.this, "Los datos ingresados son incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(RecuperarClaveActivity.this, "Los datos ingresados son incorrectos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        tr.start();
    }

    public void actualizarClave(final String idUsuario) {
        Log.i("actualizarClave", "LoginActivity");
        Thread tr = new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/nueva_clave.php?idusuario=" + idUsuario + "&clave=" + etRecuperarDni.getText().toString());
                Log.i("actualizarClave", result);
                runOnUiThread(() -> {
                    Toast.makeText(RecuperarClaveActivity.this, "Su clave ha vuelto a ser su número de DNI", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RecuperarClaveActivity.this, Acceso1Activity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                });
            }
        };
        tr.start();
    }

    @Override
    public void onBackPressed() {
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(RecuperarClaveActivity.this, Pair.create(ivIsotipo, ivIsotipo.getTransitionName()),
                Pair.create(tvRecuperarclave, tvRecuperarclave.getTransitionName()));
        startActivity(new Intent(RecuperarClaveActivity.this, Acceso2Activity.class), activityOptions.toBundle());
        finish();
    }
}
