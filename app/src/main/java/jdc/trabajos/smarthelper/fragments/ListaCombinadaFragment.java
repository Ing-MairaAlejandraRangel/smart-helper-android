package jdc.trabajos.smarthelper.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.Executors;

import jdc.trabajos.smarthelper.R;
import jdc.trabajos.smarthelper.activities.CrearRecordatorioActivity;
import jdc.trabajos.smarthelper.adapters.RecordatorioAdapter;
import jdc.trabajos.smarthelper.database.AppDatabase;
import jdc.trabajos.smarthelper.models.Recordatorio;

public class ListaCombinadaFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView textoVacio, textoInfo;
    private FloatingActionButton fabAgregar;

    public ListaCombinadaFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lista_combinada, container, false);

        // Referencias UI
        recyclerView = view.findViewById(R.id.recyclerViewCombinado);
        textoVacio = view.findViewById(R.id.textoVacioCombinado);
        textoInfo = view.findViewById(R.id.textInfoEdicion);
        fabAgregar = view.findViewById(R.id.fabAgregar);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CrearRecordatorioActivity.class);
            startActivity(intent);
        });

        cargarRecordatorios();

        return view;
    }

    private void cargarRecordatorios() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Recordatorio> lista = AppDatabase.getInstance(requireContext())
                    .recordatorioDao().obtenerTodos();

            new Handler(Looper.getMainLooper()).post(() -> {
                if (lista.isEmpty()) {
                    textoVacio.setVisibility(View.VISIBLE);
                    textoInfo.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    textoVacio.setVisibility(View.GONE);
                    textoInfo.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(new RecordatorioAdapter(requireContext(), lista));
                }
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarRecordatorios(); // Recargar al volver
    }
}
