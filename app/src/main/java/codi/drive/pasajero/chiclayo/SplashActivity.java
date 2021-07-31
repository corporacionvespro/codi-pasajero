package codi.drive.pasajero.chiclayo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.pasajero.chiclayo.compartido.PrefUtil;
import codi.drive.pasajero.chiclayo.ws.wsApp;
import static codi.drive.pasajero.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class SplashActivity extends AppCompatActivity {
    @BindView(R.id.btnAceptar) Button btnAceptar;
    @BindView(R.id.ivAnimacion1) ImageView ivAnimacion1;
    @BindView(R.id.ivAnimacion2) ImageView ivAnimacion2;
    @BindView(R.id.flayVersion) FrameLayout flayVersion;
    AlertDialog alert = null;
    CountDownTimer animacion;
    LocationManager locationManager;
    PrefUtil prefUtil;
    String latitudMaps = "", longitudMaps = "";
    wsApp ws;
    final static int timeSlash = 3000;
    public static int i = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Intent intent = getIntent();
        String action = intent.getAction();
        String data = String.valueOf(intent.getData());
        Log.i("SplashAction", action);
        Log.i("SplashData", data);
        if (action.equals("android.intent.action.VIEW")) {
            latitudMaps = data.substring(data.indexOf("@") + 1, data.indexOf("@") + 10);
            longitudMaps = data.substring(data.indexOf("@") + 12, data.indexOf("@") + 21);
        }
        Log.i("SplashData", latitudMaps + ", " + longitudMaps);
        prefUtil = new PrefUtil(this);
        ws = new wsApp(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CALL_PHONE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            checkUpdates();
        } else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertNoGps();
            } else {
                checkUpdates();
            }
        }
    }

    @OnClick(R.id.btnAceptar) void actualizar() {
        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=codi.drive.pasajero.chiclayo");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        finish();
    }

    public void AlertNoGps() {
        if (alert == null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("El GPS está desactivado, ¿desea activarlo?")
                    .setCancelable(false)
                    .setPositiveButton("Sí", (dialog, which) -> {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        CountDownTimer countDownTimer = new CountDownTimer(3000, i) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                i = 1000;
                            }
                            @Override
                            public void onFinish() {
                                animacion();
                            }
                        };
                        countDownTimer.start();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.cancel());
            alert = builder.create();
            alert.show();
        }
    }

    public void animacion() {
        ivAnimacion1 = (ImageView) findViewById(R.id.ivAnimacion1);
        ivAnimacion2 = (ImageView) findViewById(R.id.ivAnimacion2);
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
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(timeSlash);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            continuar();
                        }
                    }
                };
                timerThread.start();
            }
        }.start();
    }

    public void continuar() {
        if (prefUtil.getStringValue(PrefUtil.LOGIN_STATUS).equals("1")) {
            Intent intent;
            if (prefUtil.getStringValue("idSolicitud").length() > 0) {
                intent = new Intent(this, SolicitudTaxiActivity.class);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(intent);
            } else {
                intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                animacion.cancel();
            }
        } else {
            Intent intent = new Intent(this, Acceso1Activity.class);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            startActivity(intent);
        }
        finish();
    }

    public void checkUpdates() {
        Log.i("checkUpdates", "SplashActivity");
        Thread tr = new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/version_pasajero.php");
                Log.i("checkUpdates", result);
                runOnUiThread(() -> {
                    try {
                        if (!result.equals("" + getPackageManager().getPackageInfo(getPackageName(), 0).versionCode)) {
                            Toast.makeText(SplashActivity.this, "Tenemos nueva versión lista, descárguela para continuar viajando con Codi", Toast.LENGTH_SHORT).show();
                            flayVersion.setVisibility(View.VISIBLE);
                        } else {
                            flayVersion.setVisibility(View.GONE);
                            animacion();
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }
        };
        tr.start();
    }
}
