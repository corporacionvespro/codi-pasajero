package codi.drive.pasajero.chiclayo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.pasajero.chiclayo.adapter.AcercaAppAdapter;
import codi.drive.pasajero.chiclayo.compartido.BorderedCircleTransform;
import codi.drive.pasajero.chiclayo.compartido.PrefUtil;
import codi.drive.pasajero.chiclayo.entity.AcercaApp;
import static codi.drive.pasajero.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class AcercaAppActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.dlayMenu) DrawerLayout dlayMenu;
    @BindView(R.id.nvMenu) NavigationView nvMenu;
    @BindView(R.id.rvAcercaApp) RecyclerView rvAcercaApp;
    ArrayList<AcercaApp> acercaApps;
    AcercaAppAdapter acercaAppAdapter;
    ImageView ivFotoUsuario;
    PrefUtil prefUtil;
    SwitchCompat swNoche;
    TextView tvNombreUsuario;
    public static FrameLayout flayAcercaApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca_app);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
        Menu menu = nvMenu.getMenu();
        flayAcercaApp = (FrameLayout) findViewById(R.id.flayAcercaApp);
        swNoche = (SwitchCompat) MenuItemCompat.getActionView(menu.findItem(R.id.nav_noche)).findViewById(R.id.swNoche);
        ivFotoUsuario = (ImageView) nvMenu.getHeaderView(0).findViewById(R.id.ivFotoUsuario);
        tvNombreUsuario = (TextView) nvMenu.getHeaderView(0).findViewById(R.id.tvNombreUsuario);
        swNoche.setChecked(prefUtil.getStringValue("noche").equals("SI"));
        swNoche.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                prefUtil.saveGenericValue("noche", "SI");
            } else {
                prefUtil.saveGenericValue("noche", "NO");
            }
        });
        rvAcercaApp.setLayoutManager(new LinearLayoutManager(this));
        cargarAcercaApp();
        nvMenu.setNavigationItemSelectedListener(this);
        Picasso.get().load(prefUtil.getStringValue("foto")).transform(new BorderedCircleTransform(5, Color.rgb(0, 214, 209))).into(ivFotoUsuario);
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void cargarAcercaApp() {
        Log.i("cargarAcercaApp", "AcercaAppActivity");
        acercaApps = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/obtener_acerca_app.php");
                Log.i("cargarAcercaApp", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String idAcercaApp = jsonArray.getJSONObject(i).getString("id_acerca_app");
                            String nombre = jsonArray.getJSONObject(i).getString("nombre");
                            String contenido = jsonArray.getJSONObject(i).getString("contenido");
                            String PC = jsonArray.getJSONObject(i).getString("p_c");
                            if (PC.equals("P")) {
                                acercaApps.add(new AcercaApp(idAcercaApp, nombre, contenido, PC));
                            }
                        }
                        acercaAppAdapter = new AcercaAppAdapter(AcercaAppActivity.this, acercaApps);
                        rvAcercaApp.setAdapter(acercaAppAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_acerca:
                dlayMenu.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_cerrar:
                prefUtil.clearAll();
                startActivity(new Intent(AcercaAppActivity.this, Acceso1Activity.class));
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
                startActivity(new Intent(AcercaAppActivity.this, FavoritosActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_historial:
                startActivity(new Intent(AcercaAppActivity.this, HistorialActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_inicio:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_lugares:
                startActivity(new Intent(AcercaAppActivity.this, CategoriaActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_perfil:
                startActivity(new Intent(AcercaAppActivity.this, PerfilActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_promociones:
                startActivity(new Intent(AcercaAppActivity.this, PromocionesActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
        }
        dlayMenu.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.acvMenu) void abrirMenu() {
        dlayMenu.openDrawer(GravityCompat.START);
    }
}
