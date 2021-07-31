package codi.drive.pasajero.chiclayo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * By: el-bryant
 */

public class Acceso1Activity extends AppCompatActivity {
    @BindView(R.id.ivIsotipo) ImageView ivIsotipo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceso1);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnNuevoUsuario) void nuevoUsuario() {
        startActivity(new Intent(Acceso1Activity.this, Registro1Activity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @OnClick(R.id.btnTengoCuenta) void tengoCuenta() {
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(Acceso1Activity.this, Pair.create(ivIsotipo, ivIsotipo.getTransitionName()));
        startActivity(new Intent(Acceso1Activity.this, Acceso2Activity.class), activityOptions.toBundle());
    }
}
