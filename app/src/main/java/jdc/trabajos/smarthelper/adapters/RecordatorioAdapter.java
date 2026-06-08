package jdc.trabajos.smarthelper.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.Executors;

import jdc.trabajos.smarthelper.R;
import jdc.trabajos.smarthelper.activities.CrearRecordatorioActivity;
import jdc.trabajos.smarthelper.database.AppDatabase;
import jdc.trabajos.smarthelper.models.Recordatorio;

public class RecordatorioAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TIPO_NORMAL = 0;
    private static final int TIPO_GEO = 1;

    private final Context context;
    private final List<Recordatorio> lista;

    public RecordatorioAdapter(Context context, List<Recordatorio> lista) {
        this.context = context;
        this.lista = lista;
    }

    @Override
    public int getItemViewType(int position) {
        return lista.get(position).esGeoRecordatorio() ? TIPO_GEO : TIPO_NORMAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == TIPO_GEO) {
            View view = inflater.inflate(R.layout.item_geo_recordatorio, parent, false);
            return new GeoViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_recordatorio, parent, false);
            return new NormalViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Recordatorio r = lista.get(position);

        if (holder instanceof NormalViewHolder) {
            NormalViewHolder vh = (NormalViewHolder) holder;

            vh.titulo.setText(r.getTitulo());
            vh.descripcion.setText(r.getDescripcion());

            if ("completado".equalsIgnoreCase(r.getEstado())) {
                vh.btnCompletar.setText("Completado ✅");
                vh.btnCompletar.setEnabled(false);
            } else {
                vh.btnCompletar.setText("✔");
                vh.btnCompletar.setEnabled(true);
                vh.btnCompletar.setOnClickListener(v -> completar(r, position));
            }

            vh.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, CrearRecordatorioActivity.class);
                intent.putExtra("idRecordatorio", r.getId());
                context.startActivity(intent);
            });

        } else if (holder instanceof GeoViewHolder) {
            GeoViewHolder vh = (GeoViewHolder) holder;

            vh.titulo.setText(r.getTitulo());
            vh.descripcion.setText(r.getDescripcion());
            vh.coordenadas.setText("Lat: " + r.getLatitud() + ", Lng: " + r.getLongitud());

            if ("completado".equalsIgnoreCase(r.getEstado())) {
                vh.btnCompletarGeo.setText("Completado ✅");
                vh.btnCompletarGeo.setEnabled(false);
            } else {
                vh.btnCompletarGeo.setText("✔");
                vh.btnCompletarGeo.setEnabled(true);
                vh.btnCompletarGeo.setOnClickListener(v -> completar(r, position));
            }

            vh.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, CrearRecordatorioActivity.class);
                intent.putExtra("idRecordatorio", r.getId());
                context.startActivity(intent);
            });
        }

        // Long click para eliminar (ambos tipos)
        holder.itemView.setOnLongClickListener(v -> {
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Eliminar recordatorio")
                    .setMessage("¿Deseas eliminar este recordatorio?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            AppDatabase.getInstance(context).recordatorioDao().eliminar(r);
                            ((Activity) context).runOnUiThread(() -> {
                                lista.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, "Eliminado", Toast.LENGTH_SHORT).show();
                            });
                        });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
            return true;
        });
    }

    private void completar(Recordatorio r, int position) {
        r.setEstado("completado");

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(context).recordatorioDao().actualizar(r);
            ((Activity) context).runOnUiThread(() -> {
                lista.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Recordatorio completado", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // ViewHolder normal
    static class NormalViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, descripcion;
        Button btnCompletar;

        public NormalViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textTitulo);
            descripcion = itemView.findViewById(R.id.textDescripcion);
            btnCompletar = itemView.findViewById(R.id.btnCompletar);
        }
    }

    // ViewHolder geo
    static class GeoViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, descripcion, coordenadas;
        Button btnCompletarGeo;

        public GeoViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textTituloGeo);
            descripcion = itemView.findViewById(R.id.textDescripcionGeo);
            coordenadas = itemView.findViewById(R.id.textCoordenadas);
            btnCompletarGeo = itemView.findViewById(R.id.btnCompletarGeo);
        }
    }
}
