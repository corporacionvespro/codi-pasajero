package codi.drive.pasajero.chiclayo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.sinaseyfi.advancedcardview.AdvancedCardView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.pasajero.chiclayo.adapter.PlaceAutoSuggestAdapter;
import codi.drive.pasajero.chiclayo.compartido.BorderedCircleTransform;
import codi.drive.pasajero.chiclayo.compartido.PrefUtil;
import in.codeshuffle.typewriterview.TypeWriterView;
import static codi.drive.pasajero.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveListener, NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.acvAceptarDireccion) AdvancedCardView acvAceptarDireccion;
    @BindView(R.id.acvBandera) AdvancedCardView acvBandera;
    @BindView(R.id.acvBanderaNoche) AdvancedCardView acvBanderaNoche;
    @BindView(R.id.acvCancelarDireccion) AdvancedCardView acvCancelarDireccion;
    @BindView(R.id.acvFavoritos) AdvancedCardView acvFavoritos;
    @BindView(R.id.acvFavoritosNoche) AdvancedCardView acvFavoritosNoche;
    @BindView(R.id.acvMenu) AdvancedCardView acvMenu;
    @BindView(R.id.acvMenuNoche) AdvancedCardView acvMenuNoche;
    @BindView(R.id.acvUbicacion) AdvancedCardView acvUbicacion;
    @BindView(R.id.acvUbicacionNoche) AdvancedCardView acvUbicacionNoche;
    @BindView(R.id.autocomplete) AutoCompleteTextView autocomplete;
    @BindView(R.id.dlayMenu) DrawerLayout dlayMenu;
    @BindView(R.id.ivAnimacion1) ImageView ivAnimacion1;
    @BindView(R.id.ivAnimacion2) ImageView ivAnimacion2;
    @BindView(R.id.llayBusqueda) LinearLayout llayBusqueda;
    @BindView(R.id.llayCargando) LinearLayout llayCargando;
    @BindView(R.id.nvMenu) NavigationView nvMenu;
    @BindView(R.id.rlayDireccion) RelativeLayout rlayDireccion;
    @BindView(R.id.tvBusqueda) TextView tvBusqueda;
    @BindView(R.id.tvDireccionDestino) TextView tvDireccionDestino;
    @BindView(R.id.tvDireccionOrigen) TextView tvDireccionOrigen;
    CountDownTimer animacion;
    DatabaseReference mDatabase;
    GoogleMap mMap;
    ImageView ivFotoUsuario;
    Marker markerMiUbicacion, markerDestino;
    Polyline ruta;
    PrefUtil prefUtil;
    String distancia_destino = "", tiempo_destino = "", direccionDestino = "";
    SwitchCompat swNoche;
    TextView tvNombreUsuario;
    boolean destinoManual = false, lugarSeleccionado = false;
    public static double latitud = 0.0, longitud = 0.0, ejecutivo = 0.0;

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Places.initialize(this, "AIzaSyAgKEH4L5QcjUf-3vl4dgXyUF6VoFFgU48");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Menu menu = nvMenu.getMenu();
        ivFotoUsuario = (ImageView) nvMenu.getHeaderView(0).findViewById(R.id.ivFotoUsuario);
        tvNombreUsuario = (TextView) nvMenu.getHeaderView(0).findViewById(R.id.tvNombreUsuario);
        swNoche = (SwitchCompat) MenuItemCompat.getActionView(menu.findItem(R.id.nav_noche)).findViewById(R.id.swNoche);
        prefUtil = new PrefUtil(this);
        swNoche.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                prefUtil.saveGenericValue("noche", "SI");
                try {
                    if (mMap != null) {
                        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapa_noche));
                        if (!success) {
                            Log.i("estilo de mapa", "Style parsing failed");
                        }
                        llayBusqueda.setBackground(getDrawable(R.drawable.boton_blanco));
                        tvBusqueda.setTextColor(getResources().getColor(R.color.azul));
                        acvMenu.setVisibility(View.GONE);
                        acvUbicacion.setVisibility(View.GONE);
                        acvBandera.setVisibility(View.GONE);
                        acvFavoritos.setVisibility(View.GONE);
                        acvMenuNoche.setVisibility(View.VISIBLE);
                        acvUbicacionNoche.setVisibility(View.VISIBLE);
                        acvBanderaNoche.setVisibility(View.VISIBLE);
                        acvFavoritosNoche.setVisibility(View.VISIBLE);
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
                        llayBusqueda.setBackground(getDrawable(R.drawable.boton_azul));
                        tvBusqueda.setTextColor(getResources().getColor(R.color.blanco));
                        acvMenu.setVisibility(View.VISIBLE);
                        acvUbicacion.setVisibility(View.VISIBLE);
                        acvBandera.setVisibility(View.VISIBLE);
                        acvFavoritos.setVisibility(View.VISIBLE);
                        acvMenuNoche.setVisibility(View.GONE);
                        acvUbicacionNoche.setVisibility(View.GONE);
                        acvBanderaNoche.setVisibility(View.GONE);
                        acvFavoritosNoche.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
        autocomplete.setAdapter(new PlaceAutoSuggestAdapter(this, android.R.layout.simple_list_item_1));
        autocomplete.setOnItemClickListener((parent, view, position, id) -> {
            String description = (String) parent.getItemAtPosition(position);
            FindLatLong(description);
            LatLng posicion = new LatLng(latitud, longitud);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.pasajero);
            MarkerOptions markerOptions = new MarkerOptions().position(posicion).icon(icon);
            markerMiUbicacion = mMap.addMarker(markerOptions);
        });
        nvMenu.setNavigationItemSelectedListener(this);
        animacion();
        cargarConductoresDisponibles();
        insertarToken(prefUtil.getStringValue("idPasajero"));
    }

    @OnClick({R.id.acvBandera, R.id.acvBanderaNoche}) void destinoManual() {
        destinoManual = true;
        rlayDireccion.setVisibility(View.VISIBLE);
        if (markerMiUbicacion != null) {
            LatLng destino = mMap.getCameraPosition().target;
            if (markerDestino == null) {
                markerDestino = mMap.addMarker(new MarkerOptions().position(destino).icon(BitmapDescriptorFactory.fromResource(R.mipmap.bandera)));
            } else {
                markerDestino.setPosition(mMap.getCameraPosition().target);
            }
        }
    }

    @OnClick({R.id.acvFavoritos, R.id.acvFavoritosNoche}) void abrirFavoritos() {
        startActivity(new Intent(MainActivity.this, FavoritosActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @OnClick({R.id.acvMenu, R.id.acvMenuNoche}) void abrirMenu() {
        dlayMenu.openDrawer(GravityCompat.START);
    }

    @OnClick({R.id.acvUbicacion, R.id.acvUbicacionNoche}) void centrarPosicion() {
        if (mMap != null) {
            if (markerMiUbicacion != null) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .zoom(16)
                        .target(markerMiUbicacion.getPosition())
                        .tilt(mMap.getCameraPosition().tilt)
                        .bearing(mMap.getCameraPosition().bearing)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    @OnClick(R.id.btnAceptarDireccion) public void calcularTarifa() {
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
        Log.i("calcularTarifa", "MainActvity");
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
                        destino.setLatitude(markerDestino.getPosition().latitude);
                        destino.setLongitude(markerDestino.getPosition().longitude);
                        double kilometros = origen.distanceTo(destino) * 0.001;
                        double minutos = Integer.parseInt(tiempo_destino) / 60;
//                        Toast.makeText(MainActivity.this, ""
////                                + (Integer.parseInt(distancia_destino) * 0.001)
//                                + String.format("%.2f", kilometros) + " - " + String.format("%.2f", minutos), Toast.LENGTH_SHORT).show();
                        JSONArray jsonArray = new JSONArray(result);
                        String precio = "";
                        if (jsonArray.length() > 0) {
//                            double base_ciudad = jsonArray.getJSONObject(0).getDouble("base_ciudad") + 0.8;
//                            double pkm_ciudad = jsonArray.getJSONObject(0).getDouble("pkm_ciudad") - 0.24;
//                            double pmin_ciudad = jsonArray.getJSONObject(0).getDouble("pmin_ciudad") + 0.025;
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
                            double ciudad = base_ciudad + (pmin_ciudad * minutos) + (pkm_ciudad * kilometros);
                            double departamento = base_departamento  + (pmin_departamento * minutos) + (pkm_departamento * kilometros);
                            double pais = base_pais  + (pmin_pais * minutos) + (pkm_pais * kilometros);
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
                            Log.i("precio1", precio);
                            Log.i("precio2", "" + precio_temp);
                            if (PolyUtil.containsLocation(markerMiUbicacion.getPosition(), JLO, false) || PolyUtil.containsLocation(markerDestino.getPosition(), JLO, false)) {
                                Log.i("precio3", precio);
                                precio_temp = Double.parseDouble(precio) * zona_roja;
                                Log.i("precio4", "" + precio_temp);
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
                            Log.i("precio5", precio);
                            Log.i("precio6", "" + precio_temp);
                            precio = "" + precio_temp;
                            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,
                                    Pair.create(tvDireccionOrigen, tvDireccionOrigen.getTransitionName()),
                                    Pair.create(tvDireccionDestino, tvDireccionDestino.getTransitionName())
                            );
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
                            Intent intent = new Intent(MainActivity.this, DatosSolicitudActivity.class);
                            intent.putExtra("direccionOrigen", tvDireccionOrigen.getText().toString());
                            intent.putExtra("direccionDestino", tvDireccionDestino.getText().toString());
                            intent.putExtra("latOrigen", "" + markerMiUbicacion.getPosition().latitude);
                            intent.putExtra("longOrigen", "" + markerMiUbicacion.getPosition().longitude);
                            intent.putExtra("latDestino", "" + markerDestino.getPosition().latitude);
                            intent.putExtra("longDestino", "" + markerDestino.getPosition().longitude);
                            intent.putExtra("distanciaDestino", distancia_destino);
                            intent.putExtra("tiempoDestino", tiempo_destino);
                            intent.putExtra("idLugar", "0");
                            intent.putExtra("precio", precio);
                            intent.putExtra("ejecutivo", "" + ejecutivo);
                            startActivity(intent, activityOptions.toBundle());
                            autocomplete.setText("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        };
        tr.start();
    }

    @OnClick(R.id.btnCancelarDireccion) void cancelarDireccion() {
        destinoManual = false;
        tvDireccionDestino.setText("");
        tvDireccionOrigen.setText("");
        acvAceptarDireccion.setVisibility(View.GONE);
        acvCancelarDireccion.setVisibility(View.GONE);
        rlayDireccion.setVisibility(View.GONE);
        if (ruta != null) {
            ruta.remove();
        }
        if (markerDestino != null) {
            markerDestino.remove();
            markerDestino = null;
        }
    }

    @OnClick(R.id.llayBusqueda) void buscarLugar() {
        final List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        final RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(-6.940396, -79.969844),
                new LatLng(-6.629670, -79771765)
        );
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).setLocationBias(bounds).build(this);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null) {
                if (resultCode == RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    FindLatLong(place.getId());
                    direccionDestino = place.getName();
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    Status status = Autocomplete.getStatusFromIntent(data);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (rlayDireccion.getVisibility() == View.VISIBLE) {
            cancelarDireccion();
        } else {
            finishAffinity();
        }
    }

    @Override
    public void onCameraIdle() {
        if (destinoManual) {
            calcularDistancia(markerMiUbicacion.getPosition().latitude, markerMiUbicacion.getPosition().longitude, markerDestino.getPosition().latitude, markerDestino.getPosition().longitude);
        }
    }

    @Override
    public void onCameraMove() {
        if (destinoManual) {
            LatLng destino = mMap.getCameraPosition().target;
            if (markerDestino == null) {
                markerDestino = mMap.addMarker(new MarkerOptions().position(destino).icon(BitmapDescriptorFactory.fromResource(R.mipmap.bandera)));
            } else {
                markerDestino.setPosition(mMap.getCameraPosition().target);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (prefUtil.getStringValue("noche").equals("SI")) {
            swNoche.setChecked(true);
            try {
                if (mMap != null) {
                    boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapa_noche));
                    if (!success) {
                        Log.i("estilo de mapa", "Style parsing failed");
                    }
                    llayBusqueda.setBackground(getDrawable(R.drawable.boton_blanco));
                    tvBusqueda.setTextColor(getResources().getColor(R.color.azul));
                    acvMenu.setVisibility(View.GONE);
                    acvUbicacion.setVisibility(View.GONE);
                    acvBandera.setVisibility(View.GONE);
                    acvFavoritos.setVisibility(View.GONE);
                    acvMenuNoche.setVisibility(View.VISIBLE);
                    acvUbicacionNoche.setVisibility(View.VISIBLE);
                    acvBanderaNoche.setVisibility(View.VISIBLE);
                    acvFavoritosNoche.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            swNoche.setChecked(false);
            try {
                if (mMap != null) {
                    boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_gris));
                    if (!success) {
                        Log.i("estilo de mapa", "Style parsing failed");
                    }
                    llayBusqueda.setBackground(getDrawable(R.drawable.boton_azul));
                    tvBusqueda.setTextColor(getResources().getColor(R.color.blanco));
                    acvMenu.setVisibility(View.VISIBLE);
                    acvUbicacion.setVisibility(View.VISIBLE);
                    acvBandera.setVisibility(View.VISIBLE);
                    acvFavoritos.setVisibility(View.VISIBLE);
                    acvMenuNoche.setVisibility(View.GONE);
                    acvUbicacionNoche.setVisibility(View.GONE);
                    acvBanderaNoche.setVisibility(View.GONE);
                    acvFavoritosNoche.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 111);
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locListener);
        centrarPosicion();
        new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                llayCargando.setVisibility(View.GONE);
            }
        }.start();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_acerca:
                startActivity(new Intent(MainActivity.this, AcercaAppActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_cerrar:
                prefUtil.clearAll();
                startActivity(new Intent(MainActivity.this, Acceso1Activity.class));
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
                startActivity(new Intent(MainActivity.this, FavoritosActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_historial:
                startActivity(new Intent(MainActivity.this, HistorialActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_inicio:
                dlayMenu.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_lugares:
                startActivity(new Intent(MainActivity.this, CategoriaActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_noche:
                swNoche.setChecked(!swNoche.isChecked());
                break;
            case R.id.nav_perfil:
                startActivity(new Intent(MainActivity.this, PerfilActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_promociones:
                startActivity(new Intent(MainActivity.this, PromocionesActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
        dlayMenu.closeDrawer(GravityCompat.START);
        return true;
    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            latitud = location.getLatitude();
            longitud = location.getLongitude();
            prefUtil.saveGenericValue("latitud", "" + latitud);
            prefUtil.saveGenericValue("longitud", "" + longitud);
            if (markerMiUbicacion != null) {
                markerMiUbicacion.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                llayCargando.setVisibility(View.GONE);
                animacion.cancel();
            } else {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.pasajero));
                markerMiUbicacion = mMap.addMarker(markerOptions);
                centrarPosicion();
            }
        }
    }

    public void animacion() {
        ivAnimacion1.setAlpha(1.0f);
        ivAnimacion2.setAlpha(0.2f);
        animacion = new CountDownTimer(5000, 300) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (ivAnimacion1.getAlpha() == 1.0) {
                    ivAnimacion2.setAlpha(0.2f);
                    ivAnimacion2.setVisibility(View.VISIBLE);
                    ivAnimacion2.animate()
                            .alpha(1.0f)
                            .setDuration(450)
                            .setListener(null);
                    ivAnimacion1.setAlpha(1.0f);
                    ivAnimacion1.setVisibility(View.VISIBLE);
                    ivAnimacion1.animate()
                            .alpha(0.2f)
                            .setDuration(450)
                            .setListener(null);
                } else if (ivAnimacion2.getAlpha() == 1.0) {
                    ivAnimacion2.setAlpha(1.0f);
                    ivAnimacion2.setVisibility(View.VISIBLE);
                    ivAnimacion2.animate()
                            .alpha(0.2f)
                            .setDuration(450)
                            .setListener(null);
                    ivAnimacion1.setAlpha(0.2f);
                    ivAnimacion1.setVisibility(View.VISIBLE);
                    ivAnimacion1.animate()
                            .alpha(1.0f)
                            .setDuration(450)
                            .setListener(null);
                }
            }
            @Override
            public void onFinish() {
                animacion.start();
            }
        }.start();
    }

    public void calcularDistancia(Double latitudOrigen, Double longitudOrigen, Double latitudDestino, Double longitudDestino) {
        Log.i("calcularDistancia", "MainActivity");
        acvAceptarDireccion.setVisibility(View.GONE);
        acvCancelarDireccion.setVisibility(View.GONE);
        new Thread() {
            @Override
            public void run() {
                final String result = primero("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=" + latitudOrigen + "," + longitudOrigen + "&destinations="
                        + latitudDestino + "," + longitudDestino + "&key=AIzaSyAgKEH4L5QcjUf-3vl4dgXyUF6VoFFgU48");
                Log.i("calcularDistancia", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            String direccionDestino = jsonObject.getString("destination_addresses").substring(2, jsonObject.getString("destination_addresses").length() - 2);
                            String direccionOrigen = jsonObject.getString("origin_addresses").substring(2, jsonObject.getString("origin_addresses").length() - 2);
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
                            if (!lugarSeleccionado) {
                                tvDireccionDestino.setText(direccionDestino);
                            }
                            tvDireccionOrigen.setText(direccionOrigen);
                            distancia_destino = km;
                            tiempo_destino = seg;
                            acvCancelarDireccion.setVisibility(View.VISIBLE);
                            acvAceptarDireccion.setVisibility(View.VISIBLE);
                            showCurvedPolyline(new LatLng(latitudOrigen, longitudOrigen), new LatLng(latitudDestino, longitudDestino), 0.1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void cargarConductoresDisponibles() {
        Map<String, Marker> mNamedMarkers = new HashMap<>();
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                double lng = 0.0, lat = 0.0;
                if (dataSnapshot.child("lon").getValue() != null && String.valueOf(dataSnapshot.child("lon").getValue()).length() > 1) {
                    lng = Double.parseDouble(String.valueOf(dataSnapshot.child("lon").getValue()).substring(0, String.valueOf(dataSnapshot.child("lon").getValue()).length() - 1));
                }
                if (dataSnapshot.child("lat").getValue() != null && String.valueOf(dataSnapshot.child("lat").getValue()).length() > 1) {
                    lat = Double.parseDouble(String.valueOf(dataSnapshot.child("lat").getValue()).substring(0, String.valueOf(dataSnapshot.child("lat").getValue()).length() - 1));
                }
                String estado = dataSnapshot.child("estado").getValue(String.class);
                if (estado != null) {
                    if (!estado.equals("SA") && !estado.equals("SB") && !estado.equals("SD")) {
                        LatLng location = new LatLng(lat, lng);
                        Marker marker = mNamedMarkers.get(key);
                        if (marker == null) {
                            MarkerOptions options = getMarkerOptions(key);
                            marker = mMap.addMarker(options.position(location));
                            mNamedMarkers.put(key, marker);
                        } else {
                            marker.setPosition(location);
                        }
                    }
                }

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                Marker marker = mNamedMarkers.get(key);
                if (marker != null)
                    marker.remove();
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "No se pueden mostrar las unidades cercanas.", Toast.LENGTH_SHORT).show();
            }
        });
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
        pdestino.add(new LatLng(-6.79327, -79.88109));
//        pdestino.add(new LatLng(-6.82485, -79.90545));
//        pdestino.add(new LatLng(-6.8201, -79.91393));
        LatLng destino = new LatLng(markerDestino.getPosition().latitude, markerDestino.getPosition().longitude);
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
        LatLng latLng = new LatLng(markerDestino.getPosition().latitude, markerDestino.getPosition().longitude);
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
        LatLng latLng = new LatLng(markerDestino.getPosition().latitude, markerDestino.getPosition().longitude);
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
//        LatLng latLng = new LatLng(markerDestino.getPosition().latitude, markerDestino.getPosition().longitude);
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
        LatLng latLng = new LatLng(markerDestino.getPosition().latitude, markerDestino.getPosition().longitude);
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
        LatLng latLng = new LatLng(markerDestino.getPosition().latitude, markerDestino.getPosition().longitude);
        if (PolyUtil.containsLocation(latLng, pdestino, false) && PolyUtil.containsLocation(latLng, pdestino, false)) {
            estadentro = true;
        }
        return estadentro;
    }

    public void FindLatLong(String place_id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Uri.parse("https://maps.googleapis.com/maps/api/geocode/json")
                .buildUpon()
                .appendQueryParameter("key", "AIzaSyAgKEH4L5QcjUf-3vl4dgXyUF6VoFFgU48")
                .appendQueryParameter("place_id", URLEncoder.encode(place_id))
                .build().toString();
        StringRequest sr = new StringRequest(com.android.volley.Request.Method.GET, url, response -> {
            try {
                LatLng destino, origen;
                JSONObject jsonObject = new JSONObject(response);
                jsonObject.getJSONArray("results");
                JSONArray destination_addresses = jsonObject.getJSONArray("results");
                JSONObject geometry = (JSONObject) destination_addresses.get(0);
                String lat = String.valueOf(geometry.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                String lng = String.valueOf(geometry.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                double latitud = Double.parseDouble(lat);
                double longitud = Double.parseDouble(lng);
                destino = new LatLng(latitud, longitud);
                origen = new LatLng(this.latitud, this.longitud);
                if (markerMiUbicacion == null) {
                    markerMiUbicacion = mMap.addMarker(new MarkerOptions().position(origen).icon(BitmapDescriptorFactory.fromResource(R.mipmap.pasajero)));
                } else {
                    markerMiUbicacion.setPosition(origen);
                }
                rlayDireccion.setVisibility(View.VISIBLE);
                tvDireccionDestino.setText(direccionDestino);
                if (markerDestino != null) {
                    markerDestino.setPosition(destino);
                } else {
                    MarkerOptions markerOptions = new MarkerOptions().position(destino).icon(BitmapDescriptorFactory.fromResource(R.mipmap.bandera));
                    markerDestino = mMap.addMarker(markerOptions);
                }
                CameraPosition cameraPosition = new CameraPosition.Builder().zoom(mMap.getCameraPosition().zoom).target(destino).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                if (ruta != null) {
                    ruta.remove();
                }
                calcularDistancia(markerMiUbicacion.getPosition().latitude, markerMiUbicacion.getPosition().longitude, markerDestino.getPosition().latitude, markerDestino.getPosition().longitude);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> Log.i("Errors", String.valueOf(error)));
        queue.add(sr);
        sr.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void insertarToken(String idpasajero) {
        Log.i("insertarToken", "AccesoActivity");
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("WebService", "actualizarToken", task.getException());
                return;
            }
            String token = task.getResult().getToken();
            Log.i("insertarToken", idpasajero + " - " + token);
            new Thread() {
                @Override
                public void run() {
                    final String result = primero("http://codidrive.com/vespro/logica/insertar_token_pasajero.php?idpasajero=" + idpasajero + "&token=" + token);
                    Log.i("insertarToken", result);
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.length() > 0) {
                                boolean correcto = jsonObject.getBoolean("correcto");
                                if (correcto) {
                                    Log.i("insertarToken", "todo OK");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }.start();
        });
    }

    private MarkerOptions getMarkerOptions(String key) {
        return new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.carro));
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefUtil.getStringValue("noche").equals("SI")) {
            swNoche.setChecked(true);
            try {
                if (mMap != null) {
                    boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapa_noche));
                    if (!success) {
                        Log.i("estilo de mapa", "Style parsing failed");
                    }
                    llayBusqueda.setBackground(getDrawable(R.drawable.boton_blanco));
                    tvBusqueda.setTextColor(getResources().getColor(R.color.azul));
                    acvMenu.setVisibility(View.GONE);
                    acvUbicacion.setVisibility(View.GONE);
                    acvBandera.setVisibility(View.GONE);
                    acvFavoritos.setVisibility(View.GONE);
                    acvMenuNoche.setVisibility(View.VISIBLE);
                    acvUbicacionNoche.setVisibility(View.VISIBLE);
                    acvBanderaNoche.setVisibility(View.VISIBLE);
                    acvFavoritosNoche.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            swNoche.setChecked(false);
            try {
                if (mMap != null) {
                    boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_gris));
                    if (!success) {
                        Log.i("estilo de mapa", "Style parsing failed");
                    }
                    llayBusqueda.setBackground(getDrawable(R.drawable.boton_azul));
                    tvBusqueda.setTextColor(getResources().getColor(R.color.blanco));
                    acvMenu.setVisibility(View.VISIBLE);
                    acvUbicacion.setVisibility(View.VISIBLE);
                    acvBandera.setVisibility(View.VISIBLE);
                    acvFavoritos.setVisibility(View.VISIBLE);
                    acvMenuNoche.setVisibility(View.GONE);
                    acvUbicacionNoche.setVisibility(View.GONE);
                    acvBanderaNoche.setVisibility(View.GONE);
                    acvFavoritosNoche.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}