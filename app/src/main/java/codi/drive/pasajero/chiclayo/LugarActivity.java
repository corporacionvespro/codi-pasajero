package codi.drive.pasajero.chiclayo;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.pasajero.chiclayo.compartido.PrefUtil;

import static codi.drive.pasajero.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class LugarActivity extends FragmentActivity implements OnMapReadyCallback {
    @BindView(R.id.btnContinuar) Button btnContinuar;
    Double latitud = 0.0, longitud = 0.0;
    GoogleMap mMap;
    LatLng origen;
    Marker markerLugar, markerMiUbicacion;
    Polyline ruta;
    PrefUtil prefUtil;
    String idLugar = "", nombreLugar = "", direccionDestino = "", direccionOrigen = "", distancia_destino = "", tiempo_destino = "";
    double ejecutivo = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lugar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (getIntent().getExtras() != null) {
            latitud = getIntent().getExtras().getDouble("latitud");
            longitud = getIntent().getExtras().getDouble("longitud");
            idLugar = getIntent().getExtras().getString("idLugar");
            nombreLugar = getIntent().getExtras().getString("nombre");
            direccionDestino = getIntent().getExtras().getString("direccion");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMinZoomPreference(10);
        mMap.setMaxZoomPreference(20);
        mMap.setPadding(0,150,0,0);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS}, 111);
        }
        if (prefUtil.getStringValue("noche").equals("SI")) {
            try {
                if (mMap != null) {
                    boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapa_noche));
                    if (!success) {
                        Log.i("estilo de mapa", "Style parsing failed");
                    }
                    btnContinuar.setBackground(getDrawable(R.drawable.boton_blanco));
                    btnContinuar.setTextColor(getColor(R.color.azul));
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
                    btnContinuar.setBackground(getDrawable(R.drawable.boton_azul));
                    btnContinuar.setTextColor(getColor(R.color.blanco));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ubicarLugar();
    }

    @OnClick(R.id.btnContinuar) void calcularTarifa() {
//        //dentro de Cix, JLO, La Victoria, parte de Pimentel (la casa del Bryant)
//        double base_ciudad = 1.5;
//        double pkm_ciudad = 0.53;
//        double pmin_ciudad = 0.09;
//
//        //fuera de Cix
//        double base_departamento = 5.0;
//        double pkm_departamento = 0.7;
//        double pmin_departamento = 0.2;
//
//        //fuera del departamento
//        double base_pais = 45.0;
//        double pkm_pais = 0.52;
//        double pmin_pais = 0.85;
//
//        //precio en los peajes
//        double peaje = 10.0;
//
//        //precio nocturno
//        double factor_noche = 1.2;
        Log.i("calcularTarifa", "LugarActivity");
        Thread tr = new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/ws/obtener_precios.php");
                Log.i("calcularTarifa", result);
                runOnUiThread(() -> {
                    try {
                        boolean esnoche;
                        Location origen = new Location("origen");
                        origen.setLatitude(markerMiUbicacion.getPosition().latitude);
                        origen.setLongitude(markerMiUbicacion.getPosition().longitude);
                        Location destino = new Location("destino");
                        destino.setLatitude(markerLugar.getPosition().latitude);
                        destino.setLongitude(markerLugar.getPosition().longitude);
                        double kilometros = origen.distanceTo(destino) * 0.001;
                        double minutos = Integer.parseInt(tiempo_destino) / 60;
//                        Toast.makeText(MainActivity.this, ""
////                                + (Integer.parseInt(distancia_destino) * 0.001)
//                                + String.format("%.2f", kilometros) + " - " + String.format("%.2f", minutos), Toast.LENGTH_SHORT).show();
                        JSONArray jsonArray = new JSONArray(result);
                        String precio = "";
                        if (jsonArray.length() > 0) {
                            double base_ciudad = jsonArray.getJSONObject(0).getDouble("base_ciudad");
                            double pkm_ciudad = jsonArray.getJSONObject(0).getDouble("pkm_ciudad");
                            double pmin_ciudad = jsonArray.getJSONObject(0).getDouble("pmin_ciudad");
                            double base_departamento = jsonArray.getJSONObject(0).getDouble("base_departamento");
                            double pkm_departamento = jsonArray.getJSONObject(0).getDouble("pkm_departamento");
                            double pmin_departamento = jsonArray.getJSONObject(0).getDouble("pmin_departamento");
                            double base_pais = jsonArray.getJSONObject(0).getDouble("base_pais");
                            double pkm_pais = jsonArray.getJSONObject(0).getDouble("pkm_pais");
                            double pmin_pais = jsonArray.getJSONObject(0).getDouble("pmin_pais");
                            double peaje = jsonArray.getJSONObject(0).getDouble("peaje");
                            double factor_noche = jsonArray.getJSONObject(0).getDouble("factor_noche");
                            int horaInicio = jsonArray.getJSONObject(0).getInt("hora_inicio");
                            int horaFin = jsonArray.getJSONObject(0).getInt("hora_fin");
                            double zona_roja = jsonArray.getJSONObject(0).getDouble("zona_roja");
                            double otro = jsonArray.getJSONObject(0).getDouble("nueva_zona");
                            double ejecutivo_ciudad = jsonArray.getJSONObject(0).getDouble("ejecutivo_ciudad");
                            double ejecutivo_departamento = jsonArray.getJSONObject(0).getDouble("ejecutivo_departamento");
                            double ejecutivo_pais = jsonArray.getJSONObject(0).getDouble("ejecutivo_pais");
                            double ferre = jsonArray.getJSONObject(0).getDouble("ferre");
                            double pomalca = jsonArray.getJSONObject(0).getDouble("pomalca");
//                                int horaInicio = 19, horaFin = 4;
                            double ciudad = base_ciudad + (pkm_ciudad * kilometros) + (pmin_ciudad * minutos);
                            double departamento = base_departamento + (pkm_departamento * kilometros) + (pmin_departamento * minutos);
                            double pais = base_pais + (pkm_pais * kilometros) + (pmin_pais * minutos);
                            Calendar cal = Calendar.getInstance();
                            int hora = cal.get(Calendar.HOUR_OF_DAY);
                            esnoche = hora >= horaInicio || hora < horaFin;
//                                esnoche = false;
                            if (dentroCiudad() && esnoche) {
                                Log.i("dentroCiudad", "noche");
                                ciudad *= factor_noche;
                                double decimales = ciudad % 1;
                                if (ciudad < 5) {
                                    ciudad = 5.0;
                                } else if (decimales < 0.3) {
                                    ciudad = ciudad - decimales;
                                } else if (decimales > 0.3 && decimales < 0.75) {
                                    ciudad = ciudad - decimales + 0.5;
                                } else {
                                    ciudad = ciudad - decimales + 1;
                                }
                                precio = "" + ciudad;
                                ejecutivo = Double.parseDouble(precio) + ejecutivo_ciudad;
                            } else if (dentroCiudad()) {
                                Log.i("dentroCiudad", "dia");
                                double decimales = ciudad % 1;
                                if (ciudad < 5) {
                                    ciudad = 5.0;
                                } else if (decimales < 0.3) {
                                    ciudad = ciudad - decimales;
                                } else if (decimales > 0.3 && decimales < 0.75) {
                                    ciudad = ciudad - decimales + 0.5;
                                } else {
                                    ciudad = ciudad - decimales + 1;
                                }
                                precio = "" + ciudad;
                                ejecutivo = Double.parseDouble(precio) + ejecutivo_ciudad;
                            } else if (dentroDepartamento() && esnoche) {
                                Log.i("dentroDepartamento", "noche");
                                if (dentroPeaje()) {
                                    Log.i("dentroPeaje", "noche");
                                    departamento *= factor_noche;
                                    departamento += peaje;
                                    if (nuevaZona()) {
                                        Log.i("nuevaZona", "noche");
                                        departamento = departamento + otro;
                                    }
                                    ejecutivo = departamento * ejecutivo_departamento;
                                    double decimales = departamento % 1;
                                    if (departamento < 5) {
                                        departamento = 5.0;
                                    } else if (decimales < 0.3) {
                                        departamento = departamento - decimales;
                                    } else if (decimales > 0.3 && decimales < 0.75) {
                                        departamento = departamento - decimales + 0.5;
                                    } else {
                                        departamento = departamento - decimales + 1;
                                    }
//                                } else if (ferre()) {
//                                    Log.i("dentroFerreñafe", "noche");
//                                    departamento *= factor_noche;
//                                    departamento += ferre;
//                                    ejecutivo = departamento * ejecutivo_departamento;
//                                    double decimales = departamento % 1;
//                                    if (departamento < 5) {
//                                        departamento = 5.0;
//                                    } else if (decimales < 0.3) {
//                                        departamento = departamento - decimales;
//                                    } else if (decimales > 0.3 && decimales < 0.75) {
//                                        departamento = departamento - decimales + 0.5;
//                                    } else {
//                                        departamento = departamento - decimales + 1;
//                                    }
                                } else if (dentroPomalca()) {
                                    Log.i("dentroPomalca", "noche");
                                    departamento *= pomalca;
                                    departamento *= factor_noche;
                                    ejecutivo = departamento * ejecutivo_departamento;
                                    double decimales = departamento % 1;
                                    if (departamento < 5) {
                                        departamento = 5.0;
                                    } else if (decimales < 0.3) {
                                        departamento = departamento - decimales;
                                    } else if (decimales > 0.3 && decimales < 0.75) {
                                        departamento = departamento - decimales + 0.5;
                                    } else {
                                        departamento = departamento - decimales + 1;
                                    }
                                } else {
                                    Log.i("fueraPeaje", "noche");
                                    departamento *= factor_noche;
                                    ejecutivo = departamento * ejecutivo_departamento;
                                    double decimales = departamento % 1;
                                    if (departamento < 5) {
                                        departamento = 5.0;
                                    } else if (decimales < 0.3) {
                                        departamento = departamento - decimales;
                                    } else if (decimales > 0.3 && decimales < 0.75) {
                                        departamento = departamento - decimales + 0.5;
                                    } else {
                                        departamento = departamento - decimales + 1;
                                    }
                                }
                                precio = "" + departamento;
                            } else if (dentroDepartamento()) {
                                Log.i("dentroDepartamento", "dia");
                                if (dentroPeaje()) {
                                    Log.i("dentroPeaje", "dia");
                                    departamento += peaje;
                                    if (nuevaZona()) {
                                        Log.i("nuevaZona", "dia");
                                        departamento = departamento + otro;
                                    }
                                    ejecutivo = departamento * ejecutivo_departamento;
                                    double decimales = departamento % 1;
                                    if (departamento < 5) {
                                        departamento = 5.0;
                                    } else if (decimales < 0.3) {
                                        departamento = departamento - decimales;
                                    } else if (decimales > 0.3 && decimales < 0.75) {
                                        departamento = departamento - decimales + 0.5;
                                    } else {
                                        departamento = departamento - decimales + 1;
                                    }
//                                } else if (ferre()) {
//                                    Log.i("dentroFerreñafe", "dia");
//                                    departamento += ferre;
//                                    ejecutivo = departamento * ejecutivo_departamento;
//                                    double decimales = departamento % 1;
//                                    if (departamento < 5) {
//                                        departamento = 5.0;
//                                    } else if (decimales < 0.3) {
//                                        departamento = departamento - decimales;
//                                    } else if (decimales > 0.3 && decimales < 0.75) {
//                                        departamento = departamento - decimales + 0.5;
//                                    } else {
//                                        departamento = departamento - decimales + 1;
//                                    }
                                } else if (dentroPomalca()) {
                                    Log.i("dentroPomalca", "dia");
                                    departamento *= pomalca;
                                    ejecutivo = departamento * ejecutivo_departamento;
                                    double decimales = departamento % 1;
                                    if (departamento < 5) {
                                        departamento = 5.0;
                                    } else if (decimales < 0.3) {
                                        departamento = departamento - decimales;
                                    } else if (decimales > 0.3 && decimales < 0.75) {
                                        departamento = departamento - decimales + 0.5;
                                    } else {
                                        departamento = departamento - decimales + 1;
                                    }
                                } else {
                                    Log.i("fueraPeaje", "dia");
                                    double decimales = departamento % 1;
                                    ejecutivo = departamento * ejecutivo_departamento;
                                    if (departamento < 5) {
                                        departamento = 5.0;
                                    } else if (decimales < 0.3) {
                                        departamento = departamento - decimales;
                                    } else if (decimales > 0.3 && decimales < 0.75) {
                                        departamento = departamento - decimales + 0.5;
                                    } else {
                                        departamento = departamento - decimales + 1;
                                    }
                                }
                                precio = "" + departamento;
                            } else if (esnoche) {
                                Log.i("MainActivity", "paisNoche");
                                pais *= factor_noche;
                                double decimales = pais % 1;
                                ejecutivo = pais * ejecutivo_pais;
                                if (pais < 5) {
                                    pais = 5.0;
                                } else if (decimales < 0.3) {
                                    pais = pais - decimales;
                                } else if (decimales > 0.3 && decimales < 0.75) {
                                    pais = pais - decimales + 0.5;
                                } else {
                                    pais = pais - decimales + 1;
                                }
                                precio = "" + pais;
                            } else {
                                Log.i("MainActivity", "paisDia");
                                double decimales = pais % 1;
                                ejecutivo = pais * ejecutivo_pais;
                                if (pais < 5) {
                                    pais = 5.0;
                                } else if (decimales < 0.3) {
                                    pais = pais - decimales;
                                } else if (decimales > 0.3 && decimales < 0.75) {
                                    pais = pais - decimales + 0.5;
                                } else {
                                    pais = pais - decimales + 1;
                                }
                                precio = "" + pais;
                            }
                            ArrayList<LatLng> JLO = new ArrayList<>();
                            JLO.add(new LatLng(-6.7562442531680516, -79.86392645383133));
                            JLO.add(new LatLng(-6.758645850046644, -79.8507444875817));
                            JLO.add(new LatLng(-6.76074300939472, -79.84498802170006));
                            JLO.add(new LatLng(-6.762163660540856, -79.83136325004786));
                            JLO.add(new LatLng(-6.761160696479524, -79.82803329826672));
                            JLO.add(new LatLng(-6.770177158006444, -79.82723130677157));
                            JLO.add(new LatLng(-6.769551415269763, -79.81497229399827));
                            JLO.add(new LatLng(-6.745126355914898, -79.80101115369607));
                            JLO.add(new LatLng(-6.731223600854757, -79.86290167974612));
                            JLO.add(new LatLng(-6.74685148725387, -79.87226871002687));
                            JLO.add(new LatLng(-6.757506575586505, -79.8633444844183));
                            double precio_temp = Double.parseDouble(precio);
                            if (PolyUtil.containsLocation(markerMiUbicacion.getPosition(), JLO, false) ||
                                    PolyUtil.containsLocation(markerLugar.getPosition(), JLO, false)) {
                                Log.i("precio", precio);
                                precio_temp = Double.parseDouble(precio) * zona_roja;
                                ejecutivo *= zona_roja;
                                double decimales = precio_temp % 1;
                                if (precio_temp < 5) {
                                    precio_temp = 5.0;
                                } else if (decimales < 0.3) {
                                    precio_temp = precio_temp - decimales;
                                } else if (decimales > 0.3 && decimales < 0.75) {
                                    precio_temp = precio_temp - decimales + 0.5;
                                } else {
                                    precio_temp = precio_temp - decimales + 1;
                                }
                            }
                            precio = "" + precio_temp;
                            double dec_eje = ejecutivo % 1;
                            if (ejecutivo < 5) {
                                ejecutivo = 5.0;
                            } else if (dec_eje < 0.3) {
                                ejecutivo = ejecutivo - dec_eje;
                            } else if (dec_eje > 0.3 && dec_eje < 0.75) {
                                ejecutivo = ejecutivo - dec_eje + 0.5;
                            } else {
                                ejecutivo = ejecutivo - dec_eje + 1;
                            }
                            Intent intent = new Intent(LugarActivity.this, DatosSolicitudActivity.class);
                            intent.putExtra("direccionOrigen", direccionOrigen);
                            intent.putExtra("direccionDestino", direccionDestino);
                            intent.putExtra("latOrigen", "" + markerMiUbicacion.getPosition().latitude);
                            intent.putExtra("longOrigen", "" + markerMiUbicacion.getPosition().longitude);
                            intent.putExtra("latDestino", "" + latitud);
                            intent.putExtra("longDestino", "" + longitud);
                            intent.putExtra("referenciaOrigen", "");
                            intent.putExtra("referenciaDestino", "");
                            intent.putExtra("distanciaDestino", "" + distancia_destino);
                            intent.putExtra("tiempoDestino", "" + tiempo_destino);
                            intent.putExtra("idLugar", idLugar);
                            intent.putExtra("precio", precio);
                            intent.putExtra("ejecutivo", "" + ejecutivo);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        };
        tr.start();
    }

    public void ubicarLugar() {
        try {
            if (mMap != null) {
                LatLng posicion = new LatLng(latitud, longitud);
                if (markerLugar != null) {
                    markerLugar.setPosition(posicion);
                } else {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(posicion)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bandera));
                    markerLugar = mMap.addMarker(markerOptions);
                }
                if (markerMiUbicacion != null) {
                    markerMiUbicacion.setPosition(new LatLng(MainActivity.latitud, MainActivity.longitud));
                } else {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(MainActivity.latitud, MainActivity.longitud))
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.pasajero));
                    markerMiUbicacion = mMap.addMarker(markerOptions);
                }
                if (ruta != null){
                    ruta.remove();
                }
                origen = new LatLng(MainActivity.latitud, MainActivity.longitud);
                calcularDistancia(origen.latitude, origen.longitude, posicion.latitude, posicion.longitude);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(markerMiUbicacion.getPosition());
                builder.include(markerLugar.getPosition());
                LatLngBounds bounds = builder.build();
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (width * 0.25);
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void calcularDistancia(Double latitudOrigen, Double longitudOrigen, Double latitudDestino, Double longitudDestino) {
        Log.i("calcularDistancia", "MainActivity");
        Thread tr = new Thread() {
            @Override
            public void run() {
                final String result = primero("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=" + latitudOrigen + "," + longitudOrigen + "&destinations="
                        + latitudDestino + "," + longitudDestino + "&key=AIzaSyAgKEH4L5QcjUf-3vl4dgXyUF6VoFFgU48");
                Log.i("calcularDistancia", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            direccionDestino = jsonObject.getString("destination_addresses").substring(2, jsonObject.getString("destination_addresses").length() - 2);
                            direccionOrigen = jsonObject.getString("origin_addresses").substring(2, jsonObject.getString("origin_addresses").length() - 2);
                            String km = jsonObject.getJSONArray("rows")
                                    .getJSONObject(0)
                                    .getJSONArray("elements")
                                    .getJSONObject(0)
                                    .getJSONObject("distance")
                                    .getString("value");
                            String seg = jsonObject.getJSONArray("rows")
                                    .getJSONObject(0)
                                    .getJSONArray("elements")
                                    .getJSONObject(0)
                                    .getJSONObject("duration")
                                    .getString("value");
                            distancia_destino = km;
                            tiempo_destino = seg;
                            showCurvedPolyline(new LatLng(latitudOrigen, longitudOrigen), new LatLng(latitudDestino, longitudDestino), 0.1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        };
        tr.start();
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
            ruta = mMap.addPolyline(options.width(5).color(Color.rgb(255, 255, 255)).geodesic(false).pattern(pattern));
        } else {
            ruta = mMap.addPolyline(options.width(5).color(Color.rgb(96, 121, 133)).geodesic(false).pattern(pattern));
        }
        centrarPosicion();
    }

    public void centrarPosicion() {
        if (mMap != null) {
            if (markerMiUbicacion != null) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(markerMiUbicacion.getPosition());
                builder.include(markerLugar.getPosition());
                LatLngBounds bounds = builder.build();
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (width * 0.4);
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
            }
        }
    }

    public boolean dentroCiudad() {
        boolean estadentro = false;
        ArrayList<LatLng> pdestino = new ArrayList<LatLng>();
        pdestino.add(new LatLng(-6.75073, -79.88293));
        pdestino.add(new LatLng(-6.73103, -79.8379));
        pdestino.add(new LatLng(-6.74362, -79.80449));
        pdestino.add(new LatLng(-6.7757, -79.81286));
        pdestino.add(new LatLng(-6.78477, -79.8192));
        pdestino.add(new LatLng(-6.81186, -79.82175));
        pdestino.add(new LatLng(-6.81366, -79.84901));
        pdestino.add(new LatLng(-6.79922, -79.86632));
        pdestino.add(new LatLng(-6.82485, -79.90545));
        pdestino.add(new LatLng(-6.8201, -79.91393));
        LatLng destino = new LatLng(markerLugar.getPosition().latitude, markerLugar.getPosition().longitude);
        LatLng origen = new LatLng(markerMiUbicacion.getPosition().latitude, markerMiUbicacion.getPosition().longitude);
        if (PolyUtil.containsLocation(origen, pdestino, false) && PolyUtil.containsLocation(destino, pdestino, false)) {
            estadentro = true;
        }
        return estadentro;
    }

    public boolean dentroDepartamento() {
        boolean estadentro = false;
        ArrayList<LatLng> pdestino = new ArrayList<LatLng>();
        pdestino.add(new LatLng(-6.37883, -80.63073));
        pdestino.add(new LatLng(-5.46348, -79.91330));
        pdestino.add(new LatLng(-5.89017, -79.52469));
        pdestino.add(new LatLng(-5.99466, -79.26060));
        pdestino.add(new LatLng(-6.12989, -79.16056));
        pdestino.add(new LatLng(-6.36259, -79.43298));
        pdestino.add(new LatLng(-6.56901, -79.36706));
        pdestino.add(new LatLng(-6.72178, -79.09790));
        pdestino.add(new LatLng(-7.05865, -79.29577));
        pdestino.add(new LatLng(-7.17353, -79.69128));
        LatLng latLng = new LatLng(markerLugar.getPosition().latitude, markerLugar.getPosition().longitude);
        if (PolyUtil.containsLocation(latLng, pdestino, false) && PolyUtil.containsLocation(latLng, pdestino, false)) {
            estadentro = true;
        }
        return estadentro;
    }

    public boolean dentroPeaje() {
        boolean estadentro = false;
        ArrayList<LatLng> pdestino = new ArrayList<LatLng>();
        pdestino.add(new LatLng(-6.66551, -79.89831));
        pdestino.add(new LatLng(-6.42381, -79.95498));
        pdestino.add(new LatLng(-5.89064, -80.14314));
        pdestino.add(new LatLng(-5.57458, -79.97069));
        pdestino.add(new LatLng(-5.48312, -79.89776));
        pdestino.add(new LatLng(-5.79906, -79.62743));
        pdestino.add(new LatLng(-5.94733, -79.45794));
        pdestino.add(new LatLng(-6.02519, -79.33131));
        pdestino.add(new LatLng(-6.27925, -79.49156));
        pdestino.add(new LatLng(-6.43098, -79.76782));
        LatLng latLng = new LatLng(markerLugar.getPosition().latitude, markerLugar.getPosition().longitude);
        if (PolyUtil.containsLocation(latLng, pdestino, false) && PolyUtil.containsLocation(latLng, pdestino, false)) {
            estadentro = true;
        }
        return estadentro;
    }

    //Ferreñafe, Picsi, Capote
//    public boolean ferre() {
//        boolean estadentro = false;
//        ArrayList<LatLng> pdestino = new ArrayList<LatLng>();
//        pdestino.add(new LatLng(-6.74976, -79.80694));
//        pdestino.add(new LatLng(-6.74701, -79.82231));
//        pdestino.add(new LatLng(-6.66551, -79.89831));
//        pdestino.add(new LatLng(-6.43098, -79.76782));
//        pdestino.add(new LatLng(-6.27925, -79.49156));
//        pdestino.add(new LatLng(-6.02519, -79.33131));
//        pdestino.add(new LatLng(-6.06705, -79.14829));
//        pdestino.add(new LatLng(-6.33367, -79.41994));
//        pdestino.add(new LatLng(-6.53481, -79.41021));
//        LatLng latLng = new LatLng(markerLugar.getPosition().latitude, markerLugar.getPosition().longitude);
//        if (PolyUtil.containsLocation(latLng, pdestino, false) && PolyUtil.containsLocation(latLng, pdestino, false)) {
//            estadentro = true;
//        }
//        return estadentro;
//    }

    //Pasando Pomalca y el Chorro
    public boolean dentroPomalca() {
        boolean estadentro = false;
        ArrayList<LatLng> pdestino = new ArrayList<LatLng>();
        pdestino.add(new LatLng(-6.76819, -79.76201));
        pdestino.add(new LatLng(-6.73436, -79.76357));
        pdestino.add(new LatLng(-6.66664, -79.73584));
        pdestino.add(new LatLng(-6.48181, -79.43446));
        pdestino.add(new LatLng(-6.52035, -79.37026));
        pdestino.add(new LatLng(-6.60381, -79.33569));
        pdestino.add(new LatLng(-6.69938, -79.14456));
        pdestino.add(new LatLng(-6.78053, -79.12397));
        pdestino.add(new LatLng(-6.85720, -79.22208));
        pdestino.add(new LatLng(-6.87561, -79.27289));
        pdestino.add(new LatLng(-7.00375, -79.29693));
        pdestino.add(new LatLng(-7.02624, -79.32439));
        pdestino.add(new LatLng(-6.94787, -79.45348));
        pdestino.add(new LatLng(-7.17843, -79.69129));
        pdestino.add(new LatLng(-7.00853, -79.79281));
        LatLng latLng = new LatLng(markerLugar.getPosition().latitude, markerLugar.getPosition().longitude);
        if (PolyUtil.containsLocation(latLng, pdestino, false) && PolyUtil.containsLocation(latLng, pdestino, false)) {
            estadentro = true;
        }
        return estadentro;
    }

    //coordenadas para nueva zona: -6.485463663025454, -79.8561902025042 -> 15.00 + peaje
    public boolean nuevaZona() {
        boolean estadentro = false;
        ArrayList<LatLng> pdestino = new ArrayList<LatLng>();
        pdestino.add(new LatLng(-5.89064, -80.14314));
        pdestino.add(new LatLng(-6.42381, -79.95498));
        pdestino.add(new LatLng(-6.48546, -79.85619));
        pdestino.add(new LatLng(-6.43098, -79.76782));
        pdestino.add(new LatLng(-6.27925, -79.49156));
        pdestino.add(new LatLng(-6.02519, -79.33131));
        pdestino.add(new LatLng(-5.94733, -79.45794));
        pdestino.add(new LatLng(-5.79906, -79.62743));
        pdestino.add(new LatLng(-5.48312, -79.89776));
        pdestino.add(new LatLng(-5.57458, -79.97069));
        LatLng latLng = new LatLng(markerLugar.getPosition().latitude, markerLugar.getPosition().longitude);
        if (PolyUtil.containsLocation(latLng, pdestino, false) && PolyUtil.containsLocation(latLng, pdestino, false)) {
            estadentro = true;
        }
        return estadentro;
    }
}