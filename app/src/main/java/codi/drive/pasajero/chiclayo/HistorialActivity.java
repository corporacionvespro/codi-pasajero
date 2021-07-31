package codi.drive.pasajero.chiclayo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.pasajero.chiclayo.compartido.BorderedCircleTransform;
import codi.drive.pasajero.chiclayo.compartido.PrefUtil;

/**
 * By: el-bryant
 */

public class HistorialActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.dlayMenu) DrawerLayout dlayMenu;
    @BindView(R.id.ivAnio) ImageView ivAnio;
    @BindView(R.id.ivHoy) ImageView ivHoy;
    @BindView(R.id.ivMes) ImageView ivMes;
    @BindView(R.id.ivSemana) ImageView ivSemana;
    @BindView(R.id.nvMenu) NavigationView nvMenu;
    @BindView(R.id.tvAnio) TextView tvAnio;
    @BindView(R.id.tvEntreFechas) TextView tvEntreFechas;
    @BindView(R.id.tvHoy) TextView tvHoy;
    @BindView(R.id.tvMes) TextView tvMes;
    @BindView(R.id.tvSemana) TextView tvSemana;
    ImageView ivFotoUsuario;
    PrefUtil prefUtil;
    SwitchCompat swNoche;
    TextView tvNombreUsuario;
    int mDiaDesde, mMesDesde, mAnioDesde, mDiaHasta, mMesHasta, mAnioHasta;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
        Menu menu = nvMenu.getMenu();
        ivFotoUsuario = (ImageView) nvMenu.getHeaderView(0).findViewById(R.id.ivFotoUsuario);
        tvNombreUsuario = (TextView) nvMenu.getHeaderView(0).findViewById(R.id.tvNombreUsuario);
        swNoche = (SwitchCompat) MenuItemCompat.getActionView(menu.findItem(R.id.nav_noche)).findViewById(R.id.swNoche);
        swNoche.setChecked(prefUtil.getStringValue("noche").equals("SI"));
        swNoche.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                prefUtil.saveGenericValue("noche", "SI");
            } else {
                prefUtil.saveGenericValue("noche", "NO");
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
        nvMenu.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_acerca:
                startActivity(new Intent(HistorialActivity.this, AcercaAppActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_cerrar:
                prefUtil.clearAll();
                startActivity(new Intent(HistorialActivity.this, Acceso1Activity.class));
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
                startActivity(new Intent(HistorialActivity.this, FavoritosActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_historial:
                dlayMenu.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_inicio:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_lugares:
                startActivity(new Intent(HistorialActivity.this, RecomendadosActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_perfil:
                startActivity(new Intent(HistorialActivity.this, PerfilActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_promociones:
                startActivity(new Intent(HistorialActivity.this, PromocionesActivity.class));
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

    @OnClick(R.id.clayAnio) void cargarAnio() {
        Calendar newCalendar = Calendar.getInstance();
        this.mDiaDesde = 1;
        this.mMesDesde = 0;
        this.mAnioDesde = newCalendar.get(Calendar.YEAR);
        this.mDiaHasta = newCalendar.get(Calendar.DAY_OF_MONTH);
        this.mMesHasta = newCalendar.get(Calendar.MONTH);
        this.mAnioHasta = newCalendar.get(Calendar.YEAR);
        consultarHistorial(tvAnio, ivAnio, R.drawable.ic_anio);
    }

    @OnClick(R.id.clayEntreFechas) void cargarEntreFechas() {
        Intent intent = new Intent(HistorialActivity.this, HistorialEntreFechasActivity.class);
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(tvEntreFechas, tvEntreFechas.getTransitionName()));
        startActivity(intent, activityOptions.toBundle());
    }

    @OnClick(R.id.clayHoy) void cargarHoy() {
        Calendar newCalendar = Calendar.getInstance();
        this.mDiaDesde = newCalendar.get(Calendar.DAY_OF_MONTH);
        this.mMesDesde = newCalendar.get(Calendar.MONTH);
        this.mAnioDesde = newCalendar.get(Calendar.YEAR);
        this.mDiaHasta = newCalendar.get(Calendar.DAY_OF_MONTH);
        this.mMesHasta = newCalendar.get(Calendar.MONTH);
        this.mAnioHasta = newCalendar.get(Calendar.YEAR);
        consultarHistorial(tvHoy, ivHoy, R.drawable.ic_dia);
    }

    @OnClick(R.id.clayMes) void cargarMes() {
        Calendar newCalendar = Calendar.getInstance();
        this.mDiaDesde = 1;
        this.mMesDesde = newCalendar.get(Calendar.MONTH);
        this.mAnioDesde = newCalendar.get(Calendar.YEAR);
        this.mDiaHasta = newCalendar.get(Calendar.DAY_OF_MONTH);
        this.mMesHasta = newCalendar.get(Calendar.MONTH);
        this.mAnioHasta = newCalendar.get(Calendar.YEAR);
        consultarHistorial(tvMes, ivMes, R.drawable.ic_mes);
    }

    @OnClick(R.id.claySemana) void cargarSemana() {
        Calendar newCalendar = Calendar.getInstance();
        this.mDiaHasta = newCalendar.get(Calendar.DAY_OF_MONTH);
        this.mMesHasta = newCalendar.get(Calendar.MONTH);
        this.mAnioHasta = newCalendar.get(Calendar.YEAR);
        newCalendar.set(Calendar.DAY_OF_WEEK, newCalendar.getFirstDayOfWeek());
        this.mDiaDesde = newCalendar.get(Calendar.DAY_OF_MONTH);
        this.mMesDesde = newCalendar.get(Calendar.MONTH);
        this.mAnioDesde = newCalendar.get(Calendar.YEAR);
        consultarHistorial(tvSemana, ivSemana, R.drawable.ic_semana);
    }

    public void consultarHistorial(TextView tvTitulo, ImageView ivIcono, int recurso) {
        String fechaDesde = mAnioDesde + "-" + (mMesDesde + 1) + "-" + mDiaDesde;
        String fechaHasta = mAnioHasta + "-" + (mMesHasta + 1) + "-" + mDiaHasta;
        Log.i("consultarHistorial", fechaDesde + ", " + fechaHasta);
        Intent intent = new Intent(HistorialActivity.this, HistorialListaActivity.class);
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(this,
                Pair.create(tvTitulo, tvTitulo.getTransitionName()),
                Pair.create(ivIcono, ivIcono.getTransitionName())
        );
        intent.putExtra("fecha_desde", fechaDesde);
        intent.putExtra("fecha_hasta", fechaHasta);
        intent.putExtra("titular", tvTitulo.getText().toString());
        intent.putExtra("icono", recurso);
        startActivity(intent, activityOptions.toBundle());
    }
}
