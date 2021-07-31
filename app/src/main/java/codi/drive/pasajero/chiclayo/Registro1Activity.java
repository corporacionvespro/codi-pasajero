package codi.drive.pasajero.chiclayo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sinaseyfi.advancedcardview.AdvancedCardView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.pasajero.chiclayo.compartido.BorderedCircleTransform;
import codi.drive.pasajero.chiclayo.compartido.PrefUtil;
import static codi.drive.pasajero.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class Registro1Activity extends AppCompatActivity {
    @BindView(R.id.acvCe) AdvancedCardView acvCe;
    @BindView(R.id.acvDni) AdvancedCardView acvDni;
    @BindView(R.id.btnAceptar) Button btnAceptar;
    public static Button btnSiguiente;
    public static Button btnVerificar;
    @BindView(R.id.chkPoliticas) CheckBox chkPoliticas;
    @BindView(R.id.flayPoliticas) FrameLayout flayPoliticas;
    @BindView(R.id.llayDatos) LinearLayout llayDatos;
    @BindView(R.id.etApellidos) EditText etApellidos;
    @BindView(R.id.etCe) EditText etCe;
    @BindView(R.id.etCelular) EditText etCelular;
    @BindView(R.id.etClave) EditText etClave;
    @BindView(R.id.etCorreo) EditText etCorreo;
    @BindView(R.id.etDni) EditText etDni;
    @BindView(R.id.etNombres) EditText etNombres;
    @BindView(R.id.rbtCe) RadioButton rbtCe;
    @BindView(R.id.rbtDni) RadioButton rbtDni;
    @BindView(R.id.tvCondiciones) TextView tvCondiciones;
    @BindView(R.id.tvContenido) TextView tvContenido;
    PrefUtil prefUtil;
    public static String celular;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro1);
        ButterKnife.bind(this);
        btnSiguiente = (Button) findViewById(R.id.btnSiguiente);
        btnVerificar = (Button) findViewById(R.id.btnVerificacion);
        btnVerificar.setOnClickListener(v -> validar());
        btnSiguiente.setOnClickListener(v -> registro());
        prefUtil = new PrefUtil(this);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Registro1Activity.this, Acceso1Activity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    public void validar() {
        celular = etCelular.getText().toString();
        startActivity(new Intent(Registro1Activity.this, CodigoCelularActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @OnClick(R.id.btnAceptar) void cerrarTerminos() {
        flayPoliticas.setVisibility(View.GONE);
    }

    public void registro() {
        Intent intent = new Intent(Registro1Activity.this, Registro2Activity.class);
        if (rbtDni.isChecked()) {
//            if (etDni.getText().length() == 8 && etNombres.getText().length() > 3 && etApellidos.getText().length() > 7 && etCelular.getText().length() >= 9 && etCorreo.getText().length() > 12
//                    && etClave.getText().length() > 4) {
                intent.putExtra("dni", etDni.getText().toString());
                intent.putExtra("nombres", etNombres.getText().toString());
                intent.putExtra("apellidos", etApellidos.getText().toString());
                intent.putExtra("celular", etCelular.getText().toString());
                intent.putExtra("correo", etCorreo.getText().toString());
                intent.putExtra("clave", etClave.getText().toString());
                intent.putExtra("documento", "DNI");
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(intent);
//            } else {
//                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
//            }
        } else if (rbtCe.isChecked()) {
//            if (etCe.getText().length() > 0 && etNombres.getText().length() > 3 && etApellidos.getText().length() > 7 && etCelular.getText().length() >= 9 && etCorreo.getText().length() >= 12
//                    && etClave.getText().length() >= 4) {
                intent.putExtra("ce", etCe.getText().toString());
                intent.putExtra("nombres", etNombres.getText().toString());
                intent.putExtra("apellidos", etApellidos.getText().toString());
                intent.putExtra("celular", etCelular.getText().toString());
                intent.putExtra("correo", etCorreo.getText().toString());
                intent.putExtra("clave", etClave.getText().toString());
                intent.putExtra("documento", "CE");
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(intent);
//            } else {
//                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
//            }
        }
    }

    @OnClick(R.id.chkPoliticas) void mostrarBoton() {
        if (chkPoliticas.isChecked()) {
            btnVerificar.setVisibility(View.VISIBLE);
            btnVerificar.setEnabled(true);
        } else {
            btnVerificar.setVisibility(View.GONE);
            btnVerificar.setEnabled(false);
        }
    }

    @OnClick(R.id.rbtCe) void Ce() {
        rbtDni.setChecked(false);
        acvCe.setVisibility(View.VISIBLE);
        acvDni.setVisibility(View.GONE);
    }

    @OnClick(R.id.rbtDni) void Dni() {
        rbtCe.setChecked(false);
        acvDni.setVisibility(View.VISIBLE);
        acvCe.setVisibility(View.GONE);
    }

    @OnClick(R.id.tvCondiciones) void condiciones() {
        Log.i("condiciones", "Registro1Activity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/obtener_terminos.php");
                Log.i("condiciones", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            String p_c = jsonArray.getJSONObject(0).getString("p_c");
                            if (p_c.equals("P")) {
                                String terminos = jsonArray.getJSONObject(0).getString("contenido");
                                flayPoliticas.setVisibility(View.VISIBLE);
                                tvContenido.setText(terminos);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void comprobardatos(final String dni) {
        Log.i("comprobardatos", "click");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://.../consulta_dni.php?dni=" + dni);
                Log.i("comprobardatos", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String nombres, apellido_paterno, apellido_materno;
                        if (jsonObject.length() > 0) {
                            nombres = jsonObject.getString("nombres");
                            apellido_paterno = jsonObject.getString("apellido_paterno");
                            apellido_materno = jsonObject.getString("apellido_materno");
                            if (!nombres.equals("") && !apellido_paterno.equals("") && !apellido_materno.equals("") && !nombres.equals("null") && !apellido_paterno.equals("null") &&
                                    !apellido_materno.equals("null")) {
                                etNombres.setText(nombres);
                                etApellidos.setText(apellido_paterno + " " + apellido_materno);
                                etNombres.setEnabled(false);
                                etApellidos.setEnabled(false);
                            } else {
                                etNombres.setText("");
                                etApellidos.setText("");
                                etNombres.setEnabled(true);
                                etApellidos.setEnabled(true);
                                Toast.makeText(Registro1Activity.this, "No se pudo cargar los datos, por favor registre manualmente", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            etNombres.setText("");
                            etApellidos.setText("");
                            etNombres.setEnabled(true);
                            etApellidos.setEnabled(true);
                            Toast.makeText(Registro1Activity.this, "No se pudo cargar los datos, por favor registre manualmente", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }
}
