package codi.drive.pasajero.chiclayo;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * By: el-bryant
 */

public class NotificadoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificado);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnAceptar) void aceptar() {
        Intent intent = new Intent(NotificadoActivity.this, SolicitudTaxiActivity.class);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        startActivity(intent);
        finish();
    }
}
