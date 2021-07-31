package codi.drive.pasajero.chiclayo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sinaseyfi.advancedcardview.AdvancedCardView;
import org.json.JSONArray;
import java.util.ArrayList;
import codi.drive.pasajero.chiclayo.FavoritosActivity;
import codi.drive.pasajero.chiclayo.LugarActivity;
import codi.drive.pasajero.chiclayo.R;
import codi.drive.pasajero.chiclayo.entity.Favorito;
import static codi.drive.pasajero.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class FavoritosAdapter extends RecyclerView.Adapter<FavoritosAdapter.viewHolder> {
    Activity activity;
    ArrayList<Favorito> favoritos;

    public FavoritosAdapter(Activity activity, ArrayList<Favorito> favoritos) {
        this.activity = activity;
        this.favoritos = favoritos;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.item_favorito, parent, false);
        return new FavoritosAdapter.viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Favorito favorito = favoritos.get(position);
        String idFavorito = favorito.getIdFavorito();
        String nombre = favorito.getNombre();
        Double latitud = favorito.getLatitud();
        Double longitud = favorito.getLongitud();
        String direccion = favorito.getDireccion();
        holder.tvNombre.setText(nombre);
        holder.ivEditar.setOnClickListener(v -> {
            holder.etNombre.setVisibility(View.VISIBLE);
            holder.tvNombre.setVisibility(View.GONE);
            holder.etNombre.requestFocus();
            holder.etNombre.setText(nombre);
            holder.ivAceptar.setVisibility(View.VISIBLE);
            holder.ivEditar.setVisibility(View.GONE);
        });
        holder.ivAceptar.setOnClickListener(v -> {
            holder.ivEditar.setVisibility(View.VISIBLE);
            holder.ivAceptar.setVisibility(View.GONE);
            holder.etNombre.setVisibility(View.GONE);
            holder.tvNombre.setVisibility(View.VISIBLE);
            holder.tvNombre.setText(holder.etNombre.getText().toString());
            modificarFavorito(idFavorito, holder.etNombre.getText().toString());
        });
        holder.ivEliminar.setOnClickListener(v -> {
            eliminarFavorito(idFavorito);
        });
        holder.acvFavoritos.setOnClickListener(v -> {
            Intent intent = new Intent(activity, LugarActivity.class);
            intent.putExtra("idLugar", "0");
            intent.putExtra("nombre", favorito.getDireccion());
            intent.putExtra("latitud", favorito.getLatitud());
            intent.putExtra("longitud", favorito.getLongitud());
            intent.putExtra("direccion", favorito.getDireccion());
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    @Override
    public int getItemCount() {
        return favoritos.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        AdvancedCardView acvFavoritos;
        EditText etNombre;
        ImageView ivAceptar, ivEditar, ivEliminar;
        TextView tvNombre;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            acvFavoritos = (AdvancedCardView) itemView.findViewById(R.id.acvFavoritos);
            etNombre = (EditText) itemView.findViewById(R.id.etNombre);
            ivAceptar = (ImageView) itemView.findViewById(R.id.ivAceptar);
            ivEditar = (ImageView) itemView.findViewById(R.id.ivEditar);
            ivEliminar = (ImageView) itemView.findViewById(R.id.ivEliminar);
            tvNombre = (TextView) itemView.findViewById(R.id.tvNombre);
        }
    }

    public void modificarFavorito(String idFavorito, String nombre) {
        Log.i("modificarFavorito", "FavoritosAdapter");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/modificar_favorito.php?idfavorito=" + idFavorito + "&nombre=" + nombre);
                Log.i("modificarFavorito", result);
                activity.runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            boolean correcto = jsonArray.getJSONObject(0).getBoolean("correcto");
                            if (correcto) {
                                Toast.makeText(activity, "Modificado satisfactoriamente", Toast.LENGTH_SHORT).show();
                            } else {

                                Toast.makeText(activity, "Ha ocurrido un error y no se pudo modificar", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    private void eliminarFavorito(String idFavorito) {
        Log.i("eliminarFavorito", "FavoritosAdapter");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/eliminar_favorito.php?idlugar=" + idFavorito);
                Log.i("eliminarFavorito", result);
                activity.runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            boolean correcto = jsonArray.getJSONObject(0).getBoolean("correcto");
                            if (correcto) {
                                Toast.makeText(activity, "Eliminado satisfactoriamente", Toast.LENGTH_SHORT).show();
                                FavoritosActivity.favoritosAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(activity, "Ha ocurrido un error y no se pudo eliminar", Toast.LENGTH_SHORT).show();
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
