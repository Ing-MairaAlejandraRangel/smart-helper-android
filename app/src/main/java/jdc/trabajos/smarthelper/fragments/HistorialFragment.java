package jdc.trabajos.smarthelper.fragments;

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

import java.util.List;
import java.util.concurrent.Executors;

import jdc.trabajos.smarthelper.R;
import jdc.trabajos.smarthelper.adapters.RecordatorioAdapter;
import jdc.trabajos.smarthelper.database.AppDatabase;
import jdc.trabajos.smarthelper.models.Recordatorio;

public class HistorialFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView textoVacio, textoInfo;

    public HistorialFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_historial, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewHistorial);
        textoVacio = view.findViewById(R.id.textoVacioHistorial);
        textoInfo = view.findViewById(R.id.textInfoHistorial);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        cargarHistorial();

        return view;
    }

    private void cargarHistorial() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Recordatorio> lista = AppDatabase.getInstance(requireContext())
                    .recordatorioDao().obtenerPorEstados("completado", "vencido");

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
        cargarHistorial(); // Refrescar al volver
    }
}
