package jdc.trabajos.smarthelper.activities;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import jdc.trabajos.smarthelper.R;
import jdc.trabajos.smarthelper.fragments.InicioFragment;
import jdc.trabajos.smarthelper.fragments.RecordatoriosFragment;
import jdc.trabajos.smarthelper.fragments.HistorialFragment;
import jdc.trabajos.smarthelper.fragments.AjustesFragment;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISO_NOTIFICACION = 100;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar tema según preferencias
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean temaOscuro = prefs.getBoolean("temaOscuro", false);
        AppCompatDelegate.setDefaultNightMode(
                temaOscuro ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Solicitar permiso de notificaciones (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, PERMISO_NOTIFICACION);
            }
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        loadFragment(new InicioFragment()); // fragmento inicial

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                fragment = new InicioFragment();
            } else if (id == R.id.nav_recordatorios) {
                fragment = new RecordatoriosFragment();
            } else if (id == R.id.nav_historial) {
                fragment = new HistorialFragment();
            } else if (id == R.id.nav_ajustes) {
                fragment = new AjustesFragment();
            }

            return loadFragment(fragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_NOTIFICACION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
            } else {
                // Permiso denegado
            }
        }
    }
}
