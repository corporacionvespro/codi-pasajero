package codi.drive.pasajero.chiclayo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.sinaseyfi.advancedcardview.AdvancedCardView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.pasajero.chiclayo.compartido.PrefUtil;
import static codi.drive.pasajero.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class DatosSolicitudActivity  extends AppCompatActivity {
    @BindView(R.id.acvCodigo) AdvancedCardView acvCodigo;
    @BindView(R.id.acvPedir) AdvancedCardView acvPedir;
    @BindView(R.id.btnCancelar) Button btnCancelar;
    @BindView(R.id.etCodigo) EditText etCodigo;
    @BindView(R.id.etDireccionDestino) EditText etDireccionDestino;
    @BindView(R.id.etDireccionOrigen) EditText etDireccionOrigen;
//    @BindView(R.id.etPrecio) EditText etPrecio;
    @BindView(R.id.etReferencia) EditText etReferencia;
    @BindView(R.id.flayCargando) FrameLayout flayCargando;
    @BindView(R.id.ivMoto) ImageView ivMoto;
    @BindView(R.id.ivEjecutivo) ImageView ivEjecutivo;
    @BindView(R.id.ivStandar) ImageView ivStandar;
    @BindView(R.id.llayEjecutivo) LinearLayout llayEjecutivo;
    @BindView(R.id.llayStandar) LinearLayout llayStandar;
    @BindView(R.id.tvEjecutivo) TextView tvEjecutivo;
    @BindView(R.id.tvEstandar) TextView tvEstandar;
    @BindView(R.id.tvPrecioEjecutivo) TextView tvPrecioEjecutivo;
    @BindView(R.id.tvPrecioEstandar) TextView tvPrecioEstandar;
//    @BindView(R.id.tvCantidad) TextView tvCantidad;
//    @BindView(R.id.tvPrecio) TextView tvPrecio;
    AnimatedCircleLoadingView animatedCircleLoadingView;
    CountDownTimer temporizadorAceptado;
    static double descuento = 0.0;
    PrefUtil prefUtil;
    static String precio = "";
    static String codigo = "";
    String latOrigen = "";
    String longOrigen = "";
    String latDestino = "";
    String longDestino = "";
    String tipoServicio = "E";
    String ejec = "";
    boolean moto = false, standar = false, ejecutivo = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_solicitud);
        ButterKnife.bind(this);
        animatedCircleLoadingView = (AnimatedCircleLoadingView) findViewById(R.id.circle_loading_view_ds);
        animatedCircleLoadingView.setAnimationListener(success -> {
            if (success) {
                flayCargando.setVisibility(View.GONE);
                btnCancelar.setVisibility(View.GONE);
                resetLoading();
            } else {
                finish();
            }
        });
        prefUtil = new PrefUtil(this);
        if (getIntent().getExtras() != null) {
            String direccionOrigen = getIntent().getStringExtra("direccionOrigen");
            String direccionDestino = getIntent().getStringExtra("direccionDestino");
            latOrigen = getIntent().getStringExtra("latOrigen");
            longOrigen = getIntent().getStringExtra("longOrigen");
            latDestino = getIntent().getStringExtra("latDestino");
            longDestino = getIntent().getStringExtra("longDestino");
            String distanciaDestino = getIntent().getStringExtra("distanciaDestino");
            String tiempoDestino = getIntent().getStringExtra("tiempoDestino");
            String idLugar = getIntent().getStringExtra("idLugar");
            precio = getIntent().getStringExtra("precio");
            ejec = getIntent().getStringExtra("ejecutivo");
            etDireccionOrigen.setText(direccionOrigen);
            etDireccionDestino.setText(direccionDestino);
//            etPrecio.setText(String.format("%.2f", Double.parseDouble(precio)));
//            tvPrecio.setText(String.format("%.2f", Double.parseDouble(precio)));
            tvPrecioEstandar.setText(String.format("%.2f", Double.parseDouble(precio)));
            if (Double.parseDouble(ejec) > 0) {
                tvPrecioEjecutivo.setText(String.format("%.2f", Double.parseDouble(ejec)));
            } else {
                tvPrecioEjecutivo.setText(String.format("%.2f", (Double.parseDouble(precio) + 1)));
            }
        }
        verificarCodigoUsado();
        etCodigo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etCodigo.getText().length() >= 6) {
                    descontarCodigo();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!prefUtil.getStringValue("idSolicitud").equals("")) {
            cancelarServicio();
        }
        if (temporizadorAceptado != null) {
            temporizadorAceptado.cancel();
            prefUtil.saveGenericValue("idSolicitud", "");
        }
        finish();
    }

    @OnClick(R.id.btnCancelar) void cancelarServicio() {
        Log.i("cancelarServicio", "DatosSolicitudActivity");
        btnCancelar.setVisibility(View.GONE);
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/cancelar_solicitud.php?idsolicitud=" + prefUtil.getStringValue("idSolicitud")
                        + "&opinion=Canceló el pasajero");
                Log.i("cancelarServicio", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                temporizadorAceptado.cancel();
                                animatedCircleLoadingView.stopFailure();
                                etCodigo.setEnabled(true);
                                etReferencia.setEnabled(true);
                                etDireccionOrigen.setEnabled(true);
                                etDireccionDestino.setEnabled(true);
                                prefUtil.saveGenericValue("idSolicitud", "");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

//    @OnClick(R.id.tvPrecio) void editarPrecio() {
//        Log.i("editarPercio", "DatosSolicitudActivity");
//        tvPrecio.setVisibility(View.GONE);
//        etPrecio.setVisibility(View.VISIBLE);
//        etPrecio.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(etPrecio, InputMethodManager.SHOW_IMPLICIT);
//    }

    private void startLoading() {
        flayCargando.removeAllViews();
        flayCargando.addView(animatedCircleLoadingView);
        animatedCircleLoadingView.startDeterminate();
    }

    public void resetLoading() {
        runOnUiThread(() -> animatedCircleLoadingView.resetLoading());
    }

//    @OnClick(R.id.acvMoto) void moto() {
//        moto = true;
//        standar = false;
//        ejecutivo = false;
//        tvCantidad.setText("1");
//        ivMoto.setBackground(ContextCompat.getDrawable(DatosSolicitudActivity.this, R.drawable.redondo_azul));
//        ivMoto.setColorFilter(getColor(R.color.blanco));
//        ivEjecutivo.setBackground(ContextCompat.getDrawable(DatosSolicitudActivity.this, R.drawable.redondo_blanco));
//        ivEjecutivo.setColorFilter(getColor(R.color.azul));
//        ivStandar.setBackground(ContextCompat.getDrawable(DatosSolicitudActivity.this, R.drawable.redondo_blanco));
//        ivStandar.setColorFilter(getColor(R.color.azul));
//        tvPrecio.setText(String.format("%.2f", (Double.parseDouble(precio) - 1)));
//    }

    @OnClick(R.id.acvStandar) void estandar() {
        moto = false;
        standar = true;
        ejecutivo = false;
        llayEjecutivo.setBackgroundColor(getColor(R.color.blanco));
        ivEjecutivo.setColorFilter(getColor(R.color.azul));
        tvEjecutivo.setTextColor(getColor(R.color.azul));
        tvPrecioEjecutivo.setTextColor(getColor(R.color.azul));
        llayStandar.setBackgroundColor(getColor(R.color.azul));
        ivStandar.setColorFilter(getColor(R.color.blanco));
        tvEstandar.setTextColor(getColor(R.color.blanco));
        tvPrecioEstandar.setTextColor(getColor(R.color.blanco));
        tipoServicio = "S";
        acvPedir.setVisibility(View.VISIBLE);
//        tvCantidad.setText("Estándar");
//        ivStandar.setBackground(ContextCompat.getDrawable(DatosSolicitudActivity.this, R.drawable.redondo_azul));
//        ivStandar.setColorFilter(getColor(R.color.blanco));
//        ivMoto.setBackground(ContextCompat.getDrawable(DatosSolicitudActivity.this, R.drawable.redondo_blanco));
//        ivMoto.setColorFilter(getColor(R.color.azul));
//        ivEjecutivo.setBackground(ContextCompat.getDrawable(DatosSolicitudActivity.this, R.drawable.redondo_blanco));
//        ivEjecutivo.setColorFilter(getColor(R.color.azul));
//        tvPrecio.setText(String.format("%.2f", (Double.parseDouble(precio))));
    }

    @OnClick(R.id.acvEjecutivo) void ejecutivo() {
        moto = false;
        standar = false;
        ejecutivo = true;
        llayEjecutivo.setBackgroundColor(getColor(R.color.azul));
        ivEjecutivo.setColorFilter(getColor(R.color.blanco));
        tvEjecutivo.setTextColor(getColor(R.color.blanco));
        tvPrecioEjecutivo.setTextColor(getColor(R.color.blanco));
        llayStandar.setBackgroundColor(getColor(R.color.blanco));
        ivStandar.setColorFilter(getColor(R.color.azul));
        tvEstandar.setTextColor(getColor(R.color.azul));
        tvPrecioEstandar.setTextColor(getColor(R.color.azul));
        tipoServicio = "E";
        acvPedir.setVisibility(View.VISIBLE);
//        tvCantidad.setText("Ejecutivo");
//        ivEjecutivo.setBackground(ContextCompat.getDrawable(DatosSolicitudActivity.this, R.drawable.redondo_azul));
//        ivEjecutivo.setColorFilter(getColor(R.color.blanco));
//        ivMoto.setBackground(ContextCompat.getDrawable(DatosSolicitudActivity.this, R.drawable.redondo_blanco));
//        ivMoto.setColorFilter(getColor(R.color.azul));
//        ivStandar.setBackground(ContextCompat.getDrawable(DatosSolicitudActivity.this, R.drawable.redondo_blanco));
//        ivStandar.setColorFilter(getColor(R.color.azul));
//        tvPrecio.setText(String.format("%.2f", (Double.parseDouble(precio) + 1)));
    }

    @OnClick(R.id.btnPedirTaxi) void insertarPedido() {
        Log.i("insertarPedido", "DatosSolicitudActivity");
        etReferencia.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etReferencia, InputMethodManager.SHOW_IMPLICIT);
        flayCargando.setVisibility(View.VISIBLE);
//        btnCancelar.setVisibility(View.VISIBLE);
        animatedCircleLoadingView.setVisibility(View.VISIBLE);
        startLoading();
        new CountDownTimer(1000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                etCodigo.setEnabled(false);
                etReferencia.setEnabled(false);
                etDireccionOrigen.setEnabled(false);
                etDireccionDestino.setEnabled(false);
                Date date = new Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String fhSolicitud = df.format(date);
                String estado = "S";
                String tarifa = "";
                if (ejecutivo) {
                    tarifa = String.valueOf(Double.parseDouble(tvPrecioEjecutivo.getText().toString().replace(",", ".")));
                } else if (standar) {
                    tarifa = String.valueOf(Double.parseDouble(tvPrecioEstandar.getText().toString().replace(",", ".")));
                }
                String descuento = String.valueOf(DatosSolicitudActivity.descuento);
                String pagofinal = "";
                if (ejecutivo) {
                    pagofinal = String.valueOf(Double.parseDouble(tvPrecioEjecutivo.getText().toString().replace(",", ".")) - DatosSolicitudActivity.descuento);
                } else if (standar) {
                    pagofinal = String.valueOf(Double.parseDouble(tvPrecioEstandar.getText().toString().replace(",", ".")) - DatosSolicitudActivity.descuento);
                }
                String idpasajero = prefUtil.getStringValue("idPasajero");
                String latitud_recojo = latOrigen;
                String longitud_recojo = longOrigen;
                String referencia_recojo = etReferencia.getText().toString();
                String latitud_destino = latDestino;
                String longitid_destino = longDestino;
                String direccion_origen = etDireccionOrigen.getText().toString();
                String direccion_destino = etDireccionDestino.getText().toString();
                String codigo = DatosSolicitudActivity.codigo;
                prefUtil.saveGenericValue("direccionDestino", direccion_destino);
                String finalPagofinal = pagofinal;
                String finalTarifa = tarifa;
                new Thread() {
                    @Override
                    public void run() {
                        final String result = primero("http://codidrive.com/vespro/logica/insertar_pedido.php?fhsolicitud=" + fhSolicitud + "&estado=" + estado
                                + "&tarifa=" + finalTarifa + "&descuento=" + descuento + "&pagofinal=" + finalPagofinal + "&idpasajero=" + idpasajero + "&latitud_recojo="
                                + latitud_recojo + "&longitud_recojo=" + longitud_recojo + "&referencia_recojo=" + referencia_recojo + "&latitud_destino=" + latitud_destino
                                + "&longitud_destino=" + longitid_destino + "&direccion_origen=" + direccion_origen + "&direccion_destino=" + direccion_destino + "&codigo="
                                + codigo + "&tipo_servicio=" + tipoServicio);
                        Log.i("insertarPedido", result);
                        runOnUiThread(() -> {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                if (jsonObject.length() > 0) {
                                    boolean correcto = jsonObject.getBoolean("correcto");
                                    if (correcto) {
                                        consultarIdPedido(prefUtil.getStringValue("idPasajero"), finalPagofinal);
                                    }
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

    public void descontarCodigo() {
        Log.i("consultarCodigo", "DatosSolicitudActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/consultar_codigo.php?codigo=" + etCodigo.getText().toString());
                Log.i("consultarCodigo", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            descuento = jsonArray.getJSONObject(0).getDouble("porcentaje");
                            enviarCodigo();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void enviarCodigo() {
        Log.i("enviarCodigo", "DatosSolicitudActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/enviar_codigo.php?idpasajero=" + prefUtil.getStringValue("idPasajero")
                        + "&codigo=" + etCodigo.getText().toString());
                Log.i("enviarCodigo", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                if (standar) {
                                    tvPrecioEstandar.setText(String.format("%.2f", (Double.parseDouble(tvPrecioEstandar.getText().toString()) - descuento)));
                                    tvPrecioEjecutivo.setText(String.format("%.2f", (Double.parseDouble(tvPrecioEjecutivo.getText().toString()) - descuento)));
                                }
                                codigo = etCodigo.getText().toString();
                                prefUtil.saveGenericValue("codigo", codigo);
                                acvCodigo.setVisibility(View.GONE);
                                etReferencia.requestFocus();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void buscarUnidades(String idPedido, String pagofinal) {
        Log.i("buscarUnidades", "DatosSolicitudActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/buscar_unidades.php?pagofinal=" + pagofinal + "&latitud=" + latOrigen
                        + "&longitud=" + longOrigen + "&tipo_servicio=" + tipoServicio);
                Log.i("buscarUnidades", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String token = jsonArray.getJSONObject(i).getString("token");
                                if (token != null && !token.equals("null")) {
                                    Log.i("buscarUnidades", token);
                                    notificarConductor(token, idPedido);
                                }
                            }
                            /*
                              Henrry
                             */
//                            notificarConductor("fx4cRiHXS3Q:APA91bEtp1CEPIhIAfzOr5erZvmMTzMMHfNgqt7Z9misQZff9Ab-vlji2pBCuT3nm7sRfdu96ZwrJj7GnbbaCLtnpfr2IbgkY1CdWYvZlYX2hMVBB2IyfyX4LrazRNX0R87tCgYoJB8W", idPedido);
                            /*
                              Bryant
                             */
//                            notificarConductor("cPajTVs3LHI:APA91bFMiKIn_BD7HqqLJ4iJ3fbm_5ylhw284xNMm8Og_Le2seFzO1DqGtTr5NG3GIiL9jRZhWNF_mdznd4JGR6Khr3ibU7BPXYKwhfrIMmFZ6evot37co2IOWep2zfkz4nTRH90xSl-", idPedido);
                            temporizadorAceptado.start();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void consultarIdPedido(String idPasajero, String pagofinal) {
        Log.i("consultarIdPedido", "DatosSolicitudActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/consultar_id_pedido.php?idpasajero=" + idPasajero);
                Log.i("consultarIdPedido", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            btnCancelar.setVisibility(View.VISIBLE);
                            String idPedido = jsonArray.getJSONObject(0).getString("idsolicitud");
                            prefUtil.saveGenericValue("idSolicitud", idPedido);
                            if (codigo.length() > 0) {
                                insertarCodigoSolicitud(idPedido);
                            } else {
                                buscarUnidades(idPedido, pagofinal);
                                temporizadorAceptado = new CountDownTimer(60000, 500) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        verificarPedidoAceptado(idPedido);
                                    }
                                    @Override
                                    public void onFinish() {
                                        cancelarServicio();
                                        animatedCircleLoadingView.stopFailure();
                                        etCodigo.setEnabled(true);
                                        etReferencia.setEnabled(true);
                                        etDireccionOrigen.setEnabled(true);
                                        etDireccionDestino.setEnabled(true);
                                        prefUtil.saveGenericValue("idSolicitud", "");
                                    }
                                };
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void insertarCodigoSolicitud(String idSolicitud) {
        Log.i("insertarCodigoSolicitud", "SolicitudTaxiActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/insertar_codigo.php?codigo=" + etCodigo.getText().toString() + "&idsolicitud="
                        + idSolicitud + "&descuento=" + descuento + "&pagofinal=" + (Double.parseDouble(precio) - descuento));
                Log.i("insertarCodigoSolicitud", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                buscarUnidades(prefUtil.getStringValue("idSolicitud"), "" + (Double.parseDouble(precio) - descuento));
                                temporizadorAceptado = new CountDownTimer(60000, 500) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        verificarPedidoAceptado(idSolicitud);
                                    }
                                    @Override
                                    public void onFinish() {
                                        cancelarServicio();
                                        prefUtil.saveGenericValue("idSolicitud", "");
                                    }
                                };
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void notificarConductor(String token, String idPedido) {
        Log.i("notificarConductor", "DatosSolicitudActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/notificar_conductor.php?token=" + token + "&id_pedido=" + idPedido);
                Log.i("notificarConductor", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                Log.i("notificarConductor", "notificado correctamente a " + token);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void verificarCodigoUsado() {
        Log.i("verificarCodigoUsado", "DatosSolicitudActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/verificar_codigo_usado.php?idpasajero=" + prefUtil.getStringValue("idPasajero"));
                Log.i("verificarCodigoUsado", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            String codigo = jsonArray.getJSONObject(0).getString("codigo");
                            if (!codigo.equals("0") && !codigo.equals("")) {
                                acvCodigo.setVisibility(View.GONE);
                                descuento = 0.0;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void verificarPedidoAceptado(String idPedido) {
        Log.i("verificarPedidoAceptado", "DatosSolicitudActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/verificar_pedido_aceptado.php?idsolicitud=" + idPedido);
                Log.i("verificarPedidoAceptado", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            String estado = jsonArray.getJSONObject(0).getString("estado");
                            if (estado.equals("A")) {
                                prefUtil.saveGenericValue("idSolicitud", idPedido);
                                temporizadorAceptado.cancel();
                                animatedCircleLoadingView.stopOk();
                                Intent intent = new Intent(DatosSolicitudActivity.this, SolicitudTaxiActivity.class);
                                intent.putExtra("idSolicitud", idPedido);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                startActivity(intent);
                                finish();
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
