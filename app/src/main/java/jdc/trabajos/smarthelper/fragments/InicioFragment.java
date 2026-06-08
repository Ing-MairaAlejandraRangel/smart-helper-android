package jdc.trabajos.smarthelper.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import jdc.trabajos.smarthelper.R;
import jdc.trabajos.smarthelper.activities.CrearRecordatorioActivity;

public class InicioFragment extends Fragment {

    public InicioFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener nombre guardado
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String nombre = prefs.getString("nombreUsuario", "Usuario");

        // Mostrar saludo en el TextView
        TextView txtSaludo = view.findViewById(R.id.textSaludo);
        txtSaludo.setText("Hola, " + nombre);

        // Botón para crear recordatorio
        Button btnNuevo = view.findViewById(R.id.btnCrearRecordatorio);
        btnNuevo.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CrearRecordatorioActivity.class);
            startActivity(intent);
        });
    }
}
