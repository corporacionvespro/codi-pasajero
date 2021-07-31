package codi.drive.pasajero.chiclayo;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.maps.android.SphericalUtil;
import com.sinaseyfi.advancedcardview.AdvancedCardView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.pasajero.chiclayo.compartido.BorderedCircleTransform;
import codi.drive.pasajero.chiclayo.compartido.PrefUtil;
import static codi.drive.pasajero.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class SolicitudTaxiActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.acvDatosSolicitud) AdvancedCardView acvDatosSolicitud;
    @BindView(R.id.acvMenu) AdvancedCardView acvMenu;
    @BindView(R.id.acvMenuNoche) AdvancedCardView acvMenuNoche;
    @BindView(R.id.acvUbicacion) AdvancedCardView acvUbicacion;
    @BindView(R.id.acvUbicacionNoche) AdvancedCardView acvUbicacionNoche;
    @BindView(R.id.btnCancelar) Button btnCancelar;
    @BindView(R.id.dlayMenu) DrawerLayout dlayMenu;
    @BindView(R.id.flayCancelar) FrameLayout flayCancelar;
    @BindView(R.id.ivFotoConductor) ImageView ivFotoConductor;
    @BindView(R.id.ivMostrar) ImageView ivMostrar;
    @BindView(R.id.ivOcultar) ImageView ivOcultar;
    @BindView(R.id.nvMenu) NavigationView nvMenu;
    @BindView(R.id.tvDatosVehiculo) TextView tvDatosVehiculo;
    @BindView(R.id.tvNombresConductor) TextView tvNombresConductor;
    @BindView(R.id.tvPlaca) TextView tvPlaca;
    @BindView(R.id.tvTarifa) TextView tvTarifa;
    @BindView(R.id.tvTiempo) TextView tvTiempo;
    CountDownTimer temporizadorEstadoSolicitud;
    Double latitudOrigen = 0.0, longitudOrigen = 0.0, latitudDestino = 0.0, longitudDestino = 0.0;
    GoogleMap mMap;
    ImageView ivFotoUsuario;
    Marker markerOrigen, markerDestino, markerCarro;
    Polyline ruta;
    PrefUtil prefUtil;
    SwitchCompat swNoche;
    TextView tvNombreUsuario;
    static String idSolicitud  = "", estado = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_taxi);
        ButterKnife.bind(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Menu menu = nvMenu.getMenu();
        ivFotoUsuario = (ImageView) nvMenu.getHeaderView(0).findViewById(R.id.ivFotoUsuario);
        tvNombreUsuario = (TextView) nvMenu.getHeaderView(0).findViewById(R.id.tvNombreUsuario);
        swNoche = (SwitchCompat) MenuItemCompat.getActionView(menu.findItem(R.id.nav_noche)).findViewById(R.id.swNoche);
        swNoche.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                prefUtil.saveGenericValue("noche", "SI");
                try {
                    if (mMap != null) {
                        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapa_noche));
                        if (!success) {
                            Log.i("estilo de mapa", "Style parsing failed");
                        }
                        acvMenu.setVisibility(View.GONE);
                        acvUbicacion.setVisibility(View.GONE);
                        acvMenuNoche.setVisibility(View.VISIBLE);
                        acvUbicacionNoche.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                prefUtil.saveGenericValue("noche", "NO");
                try {
                    if (mMap != null) {
                        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_gris));
                        if (!success) {
                            Log.i("estilo de mapa", "Style parsing failed");
                        }
                        acvMenu.setVisibility(View.VISIBLE);
                        acvUbicacion.setVisibility(View.VISIBLE);
                        acvMenuNoche.setVisibility(View.GONE);
                        acvUbicacionNoche.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        prefUtil = new PrefUtil(this);
        String nombre_completo = prefUtil.getStringValue("nombres") + " " + prefUtil.getStringValue("apellidos");
        char[] caracteres = (nombre_completo.toLowerCase()).toCharArray();
        caracteres[0] = Character.toUpperCase(caracteres[0]);
        for (int i = 0; i < (nombre_completo.toLowerCase()).length(); i ++) {
            if (caracteres[i] == ' ') {
                if (i < nombre_completo.length() - 1) {
                    caracteres[i + 1] = Character.toUpperCase(caracteres[i + 1]);
                }
            }
            tvNombreUsuario.setText("" + tvNombreUsuario.getText().toString() + caracteres[i]);
        }
        Picasso.get().load(prefUtil.getStringValue("foto")).transform(new BorderedCircleTransform(15, Color.rgb(0, 214, 209))).into(ivFotoUsuario);
        nvMenu.setNavigationItemSelectedListener(this);
        if (getIntent().getExtras() != null) {
            idSolicitud = getIntent().getStringExtra("idSolicitud");
            prefUtil.saveGenericValue("idSolicitud", idSolicitud);
        }
        temporizadorEstadoSolicitud = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                consultarEstadoSolicitud();
            }
            @Override
            public void onFinish() {
                this.start();
            }
        };
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (prefUtil.getStringValue("noche").equals("SI")) {
            try {
                if (mMap != null) {
                    boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapa_noche));
                    if (!success) {
                        Log.i("estilo de mapa", "Style parsing failed");
                    }
                    acvMenu.setVisibility(View.GONE);
                    acvUbicacion.setVisibility(View.GONE);
                    acvMenuNoche.setVisibility(View.VISIBLE);
                    acvUbicacionNoche.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (mMap != null) {
                    boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_gris));
                    if (!success) {
                        Log.i("estilo de mapa", "Style parsing failed");
                    }
                    acvMenu.setVisibility(View.VISIBLE);
                    acvUbicacion.setVisibility(View.VISIBLE);
                    acvMenuNoche.setVisibility(View.GONE);
                    acvUbicacionNoche.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setPadding(0, getResources().getDisplayMetrics().heightPixels / 4, 0, 0);
        cargarDatosSolicitud();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_acerca:
                startActivity(new Intent(SolicitudTaxiActivity.this, AcercaAppActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_cerrar:
                prefUtil.clearAll();
                startActivity(new Intent(SolicitudTaxiActivity.this, Acceso1Activity.class));
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
                startActivity(new Intent(SolicitudTaxiActivity.this, FavoritosActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_historial:
                startActivity(new Intent(SolicitudTaxiActivity.this, HistorialActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_inicio:
                dlayMenu.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_lugares:
                startActivity(new Intent(SolicitudTaxiActivity.this, CategoriaActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_perfil:
                startActivity(new Intent(SolicitudTaxiActivity.this, PerfilActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_promociones:
                startActivity(new Intent(SolicitudTaxiActivity.this, PromocionesActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
        dlayMenu.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefUtil.getStringValue("noche").equals("SI")) {
            try {
                if (mMap != null) {
                    boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapa_noche));
                    if (!success) {
                        Log.i("estilo de mapa", "Style parsing failed");
                    }
                    acvMenu.setVisibility(View.GONE);
                    acvUbicacion.setVisibility(View.GONE);
                    acvMenuNoche.setVisibility(View.VISIBLE);
                    acvUbicacionNoche.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (mMap != null) {
                    boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_gris));
                    if (!success) {
                        Log.i("estilo de mapa", "Style parsing failed");
                    }
                    acvMenu.setVisibility(View.VISIBLE);
                    acvUbicacion.setVisibility(View.VISIBLE);
                    acvMenuNoche.setVisibility(View.GONE);
                    acvUbicacionNoche.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick({R.id.acvMenu, R.id.acvMenuNoche}) void abrirMenu() {
        dlayMenu.openDrawer(GravityCompat.START);
    }

    @OnClick({R.id.acvUbicacion, R.id.acvUbicacionNoche}) void centrarPosicion() {
        if (mMap != null) {
            switch (estado) {
                case "A":
                    if (markerOrigen != null) {
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .zoom(16)
                                .target(markerOrigen.getPosition())
                                .tilt(mMap.getCameraPosition().tilt)
                                .bearing(mMap.getCameraPosition().bearing)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                    break;
                case "B":
                case "N":
                    if (markerCarro != null) {
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .zoom(16)
                                .target(markerCarro.getPosition())
                                .tilt(mMap.getCameraPosition().tilt)
                                .bearing(mMap.getCameraPosition().bearing)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                    break;
            }
        }
    }

    @OnClick(R.id.btnCancelar) void cancelar() {
        prefUtil.saveGenericValue("idSolicitud", "");
        flayCancelar.setVisibility(View.GONE);
        Intent intent = new Intent(SolicitudTaxiActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.ivLlamada) void llamarConductor() {
        Log.i("llamarConductor", "SolicitudTaxiActivity");
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + prefUtil.getStringValue("telefonoConductor")));
        startActivity(intent);
    }

    public void cargarDatosSolicitud() {
        Log.i("cargarDatosSolicitud", "SolicitudTaxiActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/obtener_datos_solicitud.php?idsolicitud=" + prefUtil.getStringValue("idSolicitud"));
                Log.i("cargarDatosSolicitud", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            String fotoConductor = jsonArray.getJSONObject(0).getString("foto");
                            String marca = jsonArray.getJSONObject(0).getString("marca");
                            String modelo = jsonArray.getJSONObject(0).getString("modelo");
                            String color = jsonArray.getJSONObject(0).getString("color");
                            String nombresConductor = jsonArray.getJSONObject(0).getString("nombres");
                            String apellidosConductor = jsonArray.getJSONObject(0).getString("apellidos");
                            String placa = jsonArray.getJSONObject(0).getString("placa");
                            String pagoFinal = jsonArray.getJSONObject(0).getString("pagofinal");
                            String telefonoConductor = jsonArray.getJSONObject(0).getString("telefono");
                            latitudOrigen = jsonArray.getJSONObject(0).getDouble("latitud_recojo");
                            longitudOrigen = jsonArray.getJSONObject(0).getDouble("longitud_recojo");
                            latitudDestino = jsonArray.getJSONObject(0).getDouble("latitud_destino");
                            longitudDestino = jsonArray.getJSONObject(0).getDouble("longitud_destino");
                            Picasso.get().load(fotoConductor).transform(new BorderedCircleTransform(5, Color.rgb(0, 214, 209))).into(ivFotoConductor);
                            tvDatosVehiculo.setText(marca + " - " + modelo + " - " + color);
                            String nombres = nombresConductor + " " + apellidosConductor;
                            char[] caracteres_nombres = (nombres.toLowerCase()).toCharArray();
                            caracteres_nombres[0] = Character.toUpperCase(caracteres_nombres[0]);
                            for (int i = 0; i < (nombres.toLowerCase()).length(); i ++) {
                                if (caracteres_nombres[i] == ' ') {
                                    if (i < nombres.length() - 1) {
                                        caracteres_nombres[i + 1] = Character.toUpperCase(caracteres_nombres[i + 1]);
                                    }
                                }
                                tvNombresConductor.setText("" + tvNombresConductor.getText().toString() + caracteres_nombres[i]);
                            }
                            tvPlaca.setText(placa);
                            tvTarifa.setText("S/ " + String.format("%.2f", Double.parseDouble(pagoFinal)));
                            prefUtil.saveGenericValue("fotoConductor", fotoConductor);
                            prefUtil.saveGenericValue("nombresConductor", nombresConductor);
                            prefUtil.saveGenericValue("apellidosConductor", apellidosConductor);
                            prefUtil.saveGenericValue("pagoFinal", pagoFinal);
                            prefUtil.saveGenericValue("latitudDestino", "" + latitudDestino);
                            prefUtil.saveGenericValue("longitudDestino", "" + longitudDestino);
                            prefUtil.saveGenericValue("telefonoConductor", telefonoConductor);
                            if (markerOrigen != null) {
                                markerOrigen.setPosition(new LatLng(latitudOrigen, longitudOrigen));
                            } else {
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(new LatLng(latitudOrigen, longitudOrigen))
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.pasajero));
                                markerOrigen = mMap.addMarker(markerOptions);
                            }
                            if (markerDestino != null) {
                                markerDestino.setPosition(new LatLng(latitudDestino, longitudDestino));
                            } else {
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(new LatLng(latitudDestino, longitudDestino))
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bandera));
                                markerDestino = mMap.addMarker(markerOptions);
                            }
                            temporizadorEstadoSolicitud.start();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void consultarEstadoSolicitud() {
        Log.i("consultarEstadoSoli", "SolicitudTaxiActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/obtener_estado_solicitud.php?idsolicitud=" + prefUtil.getStringValue("idSolicitud"));
                Log.i("consultarEstadoSoli", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            estado = jsonArray.getJSONObject(0).getString("estado");
                            double latitudConductor = jsonArray.getJSONObject(0).getDouble("latitud");
                            double longitudConductor = jsonArray.getJSONObject(0).getDouble("longitud");
                            LatLng posicionConductor = new LatLng(latitudConductor, longitudConductor);
                            if (estado.equals("A") || estado.equals("N")) {
                                if (markerCarro != null) {
                                    markerCarro.setPosition(posicionConductor);
                                } else {
                                    MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.carro)).position(posicionConductor);
                                    markerCarro = mMap.addMarker(markerOptions);
                                    centrarPosicion();
                                }
                                if (markerCarro != null && markerOrigen != null) {
                                    showCurvedPolyline(markerCarro.getPosition(), markerOrigen.getPosition(), 0.1);
                                }
                            }
                            if (estado.equals("B")) {
                                if (markerCarro != null) {
                                    markerCarro.setPosition(posicionConductor);
                                } else {
                                    MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.carro)).position(posicionConductor);
                                    markerCarro = mMap.addMarker(markerOptions);
                                    centrarPosicion();
                                }
                                if (markerOrigen != null) {
                                    markerOrigen.remove();
                                }
                                if (markerCarro != null && markerDestino != null) {
                                    showCurvedPolyline(markerCarro.getPosition(), markerDestino.getPosition(), 0.1);
                                }
                            }
                            if (estado.equals("C")) {
                                flayCancelar.setVisibility(View.VISIBLE);
                                temporizadorEstadoSolicitud.cancel();
                            }
                            if (estado.equals("F")) {
                                temporizadorEstadoSolicitud.cancel();
                                startActivity(new Intent(SolicitudTaxiActivity.this, FinalizarPedidoActivity.class).putExtra("latitud",
                                        markerDestino.getPosition().latitude).putExtra("longitud", markerDestino.getPosition().longitude));
                                FinalizarPedidoActivity.latitud = markerDestino.getPosition().latitude;
                                FinalizarPedidoActivity.longitud = markerDestino.getPosition().longitude;
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

    private void showCurvedPolyline (LatLng p1, LatLng p2, double k) {
        double d = SphericalUtil.computeDistanceBetween(p1, p2);
        double h = SphericalUtil.computeHeading(p1, p2);
        LatLng p = SphericalUtil.computeOffset(p1, d * 0.5, h);
        double x = (1 - k * k) * d * 0.5 / (2 * k);
        double r = (1 + k * k) * d * 0.5 / (2 * k);
        LatLng c = SphericalUtil.computeOffset(p, x, h > 40 ? h + 90.0 : h - 90.0);
        PolylineOptions options = new PolylineOptions();
        List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dash(30), new Gap(20));
        double h1 = SphericalUtil.computeHeading(c, p1);
        double h2 = SphericalUtil.computeHeading(c, p2);
        int numpoints = 100;
        double step = (h2 - h1) / numpoints;
        for (int i = 0; i < numpoints; i++) {
            LatLng pi = SphericalUtil.computeOffset(c, r, h1 + i * step);
            options.add(pi);
        }
        if (ruta != null) {
            ruta.remove();
        }
        if (prefUtil.getStringValue("noche").equals("SI")) {
            ruta = mMap.addPolyline(options.width(10).color(Color.rgb(255, 255, 255)).geodesic(false).pattern(pattern));
        } else {
            ruta = mMap.addPolyline(options.width(10).color(Color.rgb(96, 121, 133)).geodesic(false).pattern(pattern));
        }
    }
}
