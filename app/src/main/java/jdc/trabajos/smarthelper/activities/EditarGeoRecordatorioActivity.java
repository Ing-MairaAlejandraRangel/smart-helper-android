package jdc.trabajos.smarthelper.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import jdc.trabajos.smarthelper.R;
import jdc.trabajos.smarthelper.database.AppDatabase;
import jdc.trabajos.smarthelper.models.Recordatorio;

public class EditarGeoRecordatorioActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText etTitulo, etDescripcion;
    private TextView tvUbicacion;
    private SeekBar seekBarRadio;
    private Button btnGuardar;
    private double latitud;
    private double longitud;
    private float radio;
    private int recordatorioId;
    private String direccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_recordatorio);

        etTitulo = findViewById(R.id.editTextTitulo);
        etDescripcion = findViewById(R.id.editTextDescripcion);
        tvUbicacion = findViewById(R.id.tvUbicacion);
        seekBarRadio = findViewById(R.id.seekBarRadio);
        btnGuardar = findViewById(R.id.buttonGuardar);

        recordatorioId = getIntent().getIntExtra("recordatorioId", -1);
        if (recordatorioId == -1) {
            Toast.makeText(this, "ID inválido", Toast.LENGTH_SHORT).show();
            finish();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapGeo);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        seekBarRadio.setMax(500);
        seekBarRadio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radio = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(EditarGeoRecordatorioActivity.this, "Radio: " + radio + " m", Toast.LENGTH_SHORT).show();
            }
        });

        cargarDatos();

        btnGuardar.setOnClickListener(v -> actualizarRecordatorio());
    }

    private void cargarDatos() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Recordatorio recordatorio = AppDatabase.getInstance(this).recordatorioDao().obtenerPorId(recordatorioId);
            runOnUiThread(() -> {
                if (recordatorio != null) {
                    etTitulo.setText(recordatorio.getTitulo());
                    etDescripcion.setText(recordatorio.getDescripcion());
                    latitud = recordatorio.getLatitud();
                    longitud = recordatorio.getLongitud();
                    direccion = recordatorio.getDireccion();
                    tvUbicacion.setText(direccion);
                    radio = recordatorio.getRadio() != null ? recordatorio.getRadio() : 100f;
                    seekBarRadio.setProgress((int) radio);

                    if (mMap != null) {
                        LatLng latLng = new LatLng(latitud, longitud);
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación seleccionada"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        mMap.addCircle(new CircleOptions()
                                .center(latLng)
                                .radius(radio)
                                .strokeColor(0xFF2196F3)
                                .fillColor(0x2200BFFF));
                    }
                }
            });
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            latitud = latLng.latitude;
            longitud = latLng.longitude;

            mMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación seleccionada"));
            mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(radio)
                    .strokeColor(0xFF2196F3)
                    .fillColor(0x2200BFFF));

            obtenerDireccion(latitud, longitud);
        });
    }

    private void obtenerDireccion(double lat, double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> direcciones = geocoder.getFromLocation(lat, lon, 1);
            if (!direcciones.isEmpty()) {
                direccion = direcciones.get(0).getAddressLine(0);
                tvUbicacion.setText(direccion);
            }
        } catch (IOException e) {
            tvUbicacion.setText("Dirección no disponible");
        }
    }

    private void actualizarRecordatorio() {
        String titulo = etTitulo.getText().toString();
        String descripcion = etDescripcion.getText().toString();

        if (titulo.isEmpty() || direccion == null || direccion.isEmpty()) {
            Toast.makeText(this, "Debe completar los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Recordatorio recordatorio = new Recordatorio(titulo, descripcion, latitud, longitud, radio, direccion);
        recordatorio.setId(recordatorioId);

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(this).recordatorioDao().actualizar(recordatorio);
            runOnUiThread(() -> {
                Toast.makeText(this, "Geo-recordatorio actualizado", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
