package jdc.trabajos.smarthelper.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import jdc.trabajos.smarthelper.R;

public class AjustesFragment extends Fragment {

    private Switch switchTemaOscuro, switchNotificaciones, switchSincronizar;
    private EditText editTextNombre, editTextCorreo;
    private Button buttonGuardar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ajustes, container, false);

        switchTemaOscuro = view.findViewById(R.id.switchTemaOscuro);
        switchNotificaciones = view.findViewById(R.id.switchNotificaciones);
        switchSincronizar = view.findViewById(R.id.switchSincronizarCalendario);
        editTextNombre = view.findViewById(R.id.editTextNombreUsuario);
        editTextCorreo = view.findViewById(R.id.editTextCorreoUsuario);
        buttonGuardar = view.findViewById(R.id.buttonGuardarConfiguracion);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Cargar valores actuales
        boolean temaOscuro = prefs.getBoolean("temaOscuro", false);
        switchTemaOscuro.setChecked(temaOscuro);
        switchNotificaciones.setChecked(prefs.getBoolean("notificaciones", true));
        switchSincronizar.setChecked(prefs.getBoolean("sincronizar", false));

        editTextNombre.setText(prefs.getString("nombreUsuario", ""));
        editTextCorreo.setText(prefs.getString("correoUsuario", ""));

        // Listeners de los switches
        switchTemaOscuro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("temaOscuro", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            requireActivity().recreate();
        });

        switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("notificaciones", isChecked).apply());

        switchSincronizar.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("sincronizar", isChecked).apply());

        // Guardar configuración del usuario
        buttonGuardar.setOnClickListener(v -> {
            String nombre = editTextNombre.getText().toString().trim();
            String correo = editTextCorreo.getText().toString().trim();

            prefs.edit()
                    .putString("nombreUsuario", nombre)
                    .putString("correoUsuario", correo)
                    .apply();

            Toast.makeText(getContext(), "Configuración guardada", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
