package codi.drive.pasajero.chiclayo;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import org.json.JSONArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.pasajero.chiclayo.compartido.PrefUtil;
import in.codeshuffle.typewriterview.TypeWriterListener;
import in.codeshuffle.typewriterview.TypeWriterView;

import static codi.drive.pasajero.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class Acceso2Activity extends AppCompatActivity {
    @BindView(R.id.etClave) EditText etClave;
    @BindView(R.id.etDni) EditText etDni;
    @BindView(R.id.flayCargando) FrameLayout flayCargando;
    @BindView(R.id.ivIsotipo) ImageView ivIsotipo;
    @BindView(R.id.tvRecuperarClave) TextView tvRecuperarClave;
    @BindView(R.id.twvCargando) TypeWriterView twvCargando;
    PrefUtil prefUtil;
    AnimatedCircleLoadingView animatedCircleLoadingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceso2);
        ButterKnife.bind(this);
        animatedCircleLoadingView = (AnimatedCircleLoadingView) findViewById(R.id.circle_loading_view_a2);
        animatedCircleLoadingView.setAnimationListener(success -> {
            if (success) {
                Typeface poppins_bold = Typeface.createFromAsset(getAssets(), "fonts/poppins_bold.ttf");
                twvCargando.setTypeface(poppins_bold);
                twvCargando.setTextColor(getResources().getColor(R.color.blanco));
                twvCargando.setDelay(2000);
                twvCargando.setWithMusic(false);
                twvCargando.animateText("¿A dónde quieres ir?");
            } else {
                flayCargando.setVisibility(View.GONE);
                resetLoading();
            }
        });
        twvCargando.setTypeWriterListener(new TypeWriterListener() {
            @Override
            public void onTypingStart(String text) {
            }
            @Override
            public void onTypingEnd(String text) {
                new CountDownTimer(1000, 500) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }
                    @Override
                    public void onFinish() {
                        flayCargando.setVisibility(View.GONE);
                        Intent intent = new Intent(Acceso2Activity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                        resetLoading();
                    }
                }.start();
            }
            @Override
            public void onCharacterTyped(String text, int position) {
            }
            @Override
            public void onTypingRemoved(String text) {
            }
        });
        prefUtil = new PrefUtil(this);
    }

    private void startLoading() {
        flayCargando.removeAllViews();
        flayCargando.addView(animatedCircleLoadingView);
        flayCargando.addView(twvCargando);
        animatedCircleLoadingView.startDeterminate();
    }

    public void resetLoading() {
        runOnUiThread(() -> animatedCircleLoadingView.resetLoading());
    }

    @OnClick(R.id.btnIngresar) void ingresar() {
        Log.i("ingresar", "click");
        InputMethodManager immDni = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        immDni.hideSoftInputFromWindow(etDni.getWindowToken(), 0);
        InputMethodManager immClave = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        immClave.hideSoftInputFromWindow(etClave.getWindowToken(), 0);
        flayCargando.setVisibility(View.VISIBLE);
        etDni.setEnabled(false);
        etClave.setEnabled(false);
        startLoading();
        final String login = etDni.getText().toString();
        final String pass = etClave.getText().toString();
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                new Thread() {
                    @Override
                    public void run() {
                        final String result = primero("http://codidrive.com/vespro/logica/acceder.php?login=" + login + "&pass=" + pass);
                        Log.i("ingresar", result);
                        runOnUiThread(() -> {
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                if (jsonArray.length() > 0) {
                                    String nombres = jsonArray.getJSONObject(0).getString("nombres");
                                    String apellidos = jsonArray.getJSONObject(0).getString("apellidos");
                                    String email = jsonArray.getJSONObject(0).getString("email");
                                    String telefono = jsonArray.getJSONObject(0).getString("telefono");
                                    String foto = jsonArray.getJSONObject(0).getString("foto");
                                    String idUsuario = jsonArray.getJSONObject(0).getString("idusuario");
                                    String idPersona = jsonArray.getJSONObject(0).getString("idpersona");
                                    String idPasajero = jsonArray.getJSONObject(0).getString("idpasajero");
                                    String apikey = jsonArray.getJSONObject(0).getString("apikey");
                                    String tipoPasajero = jsonArray.getJSONObject(0).getString("tipo_pasajero");
                                    prefUtil.saveGenericValue("nombres", nombres);
                                    prefUtil.saveGenericValue("apellidos", apellidos);
                                    prefUtil.saveGenericValue("correo", email);
                                    prefUtil.saveGenericValue("telefono", telefono);
                                    prefUtil.saveGenericValue("foto", foto);
                                    prefUtil.saveGenericValue("idUsuario", idUsuario);
                                    prefUtil.saveGenericValue("idPersona", idPersona);
                                    prefUtil.saveGenericValue("idPasajero", idPasajero);
                                    prefUtil.saveGenericValue("token", apikey);
                                    prefUtil.saveGenericValue("tipoPasajero", tipoPasajero);
                                    prefUtil.saveGenericValue("nro_documento", login);
                                    prefUtil.saveGenericValue(PrefUtil.LOGIN_STATUS, "1");
                                    etDni.setEnabled(true);
                                    etClave.setEnabled(true);
                                    animatedCircleLoadingView.stopOk();
                                } else {
                                    etDni.setEnabled(true);
                                    etClave.setEnabled(true);
                                    Toast.makeText(Acceso2Activity.this, "Las credenciales proporcionadas son incorrectas, por favor intente de nuevo.", Toast.LENGTH_LONG).show();
                                    etDni.setText("");
                                    etClave.setText("");
                                    etDni.requestFocus();
                                    animatedCircleLoadingView.stopFailure();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }.start();
            }
        }.start();
    }

    @OnClick(R.id.tvRecuperarClave) void recuperarClave() {
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(Acceso2Activity.this, Pair.create(ivIsotipo, ivIsotipo.getTransitionName()),
                Pair.create(tvRecuperarClave, tvRecuperarClave.getTransitionName()));
        startActivity(new Intent(Acceso2Activity.this, RecuperarClaveActivity.class), activityOptions.toBundle());
        finish();
    }

    @Override
    public void onBackPressed() {
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(Acceso2Activity.this, Pair.create(ivIsotipo, ivIsotipo.getTransitionName()));
        startActivity(new Intent(Acceso2Activity.this, Acceso1Activity.class), activityOptions.toBundle());
        finish();
    }
}
