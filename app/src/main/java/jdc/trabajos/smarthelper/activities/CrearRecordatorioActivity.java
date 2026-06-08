package jdc.trabajos.smarthelper.activities;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jdc.trabajos.smarthelper.R;
import jdc.trabajos.smarthelper.database.AppDatabase;
import jdc.trabajos.smarthelper.helpers.AlertaReceiver;
import jdc.trabajos.smarthelper.models.Recordatorio;

public class CrearRecordatorioActivity extends AppCompatActivity {

    private EditText editTextTitulo, editTextDescripcion;
    private Button btnFecha, btnHora, btnGuardar;
    private Spinner spinnerFrecuencia;
    private TextView textViewFechaHora, tvUbicacion;
    private SeekBar seekBarRadio;
    private ImageButton btnMicrofono;
    private GoogleMap mMap;
    private Calendar calendar;

    private double latitudSeleccionada = 0.0;
    private double longitudSeleccionada = 0.0;
    private float radio = 100f;
    private String direccion = "";
    private int idRecordatorioEditar = -1;

    private static final int REQ_CODE_SPEECH_INPUT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_recordatorio);

        editTextTitulo = findViewById(R.id.editTextTitulo);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        btnFecha = findViewById(R.id.buttonFecha);
        btnHora = findViewById(R.id.buttonHora);
        btnGuardar = findViewById(R.id.buttonGuardar);
        spinnerFrecuencia = findViewById(R.id.spinnerFrecuencia);
        textViewFechaHora = findViewById(R.id.textViewFechaHora);
        tvUbicacion = findViewById(R.id.tvUbicacion);
        seekBarRadio = findViewById(R.id.seekBarRadio);
        btnMicrofono = findViewById(R.id.btnMicrofono);

        calendar = Calendar.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.frecuencias_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrecuencia.setAdapter(adapter);

        pedirPermisosCalendario();

        btnFecha.setOnClickListener(v -> seleccionarFecha());
        btnHora.setOnClickListener(v -> seleccionarHora());
        btnMicrofono.setOnClickListener(v -> iniciarReconocimientoVoz());

        seekBarRadio.setMax(500);
        seekBarRadio.setProgress((int) radio);
        seekBarRadio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { radio = progress; }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(CrearRecordatorioActivity.this, "Radio: " + radio + " m", Toast.LENGTH_SHORT).show();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapGeo);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this::configurarMapa);
        }

        idRecordatorioEditar = getIntent().getIntExtra("idRecordatorio", -1);
        if (idRecordatorioEditar != -1) {
            cargarRecordatorioDesdeRoom(idRecordatorioEditar);
        }

        btnGuardar.setOnClickListener(v -> guardarRecordatorio());
    }

    private void seleccionarFecha() {
        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            actualizarFechaHora();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void seleccionarHora() {
        TimePickerDialog timePicker = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            actualizarFechaHora();
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePicker.show();
    }

    private void actualizarFechaHora() {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        textViewFechaHora.setText("Fecha seleccionada: " + formato.format(calendar.getTime()));
    }

    private void configurarMapa(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMapClickListener(latLng -> {
            latitudSeleccionada = latLng.latitude;
            longitudSeleccionada = latLng.longitude;

            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación seleccionada"));
            mMap.addCircle(new CircleOptions().center(latLng).radius(radio).strokeColor(0xFF2196F3).fillColor(0x2200BFFF));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            obtenerDireccionDesdeLatLng(latitudSeleccionada, longitudSeleccionada);
        });
    }

    private void obtenerDireccionDesdeLatLng(double lat, double lon) {
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

    private void guardarRecordatorio() {
        String titulo = editTextTitulo.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();
        if (titulo.isEmpty()) {
            Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        Recordatorio recordatorio;
        if (latitudSeleccionada != 0.0 && longitudSeleccionada != 0.0) {
            recordatorio = new Recordatorio(titulo, descripcion, latitudSeleccionada, longitudSeleccionada, radio, direccion);
        } else {
            String frecuencia = spinnerFrecuencia.getSelectedItem() != null ? spinnerFrecuencia.getSelectedItem().toString() : "Una vez";
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String fechaHora = formato.format(calendar.getTime());
            recordatorio = new Recordatorio(titulo, descripcion, fechaHora, frecuencia, "pendiente");
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            if (idRecordatorioEditar != -1) {
                recordatorio.setId(idRecordatorioEditar);
                AppDatabase.getInstance(this).recordatorioDao().actualizar(recordatorio);
            } else {
                AppDatabase.getInstance(this).recordatorioDao().insertar(recordatorio);
            }

            if (recordatorio.getFechaHora() != null) {
                agregarEventoAlCalendario(recordatorio);
                programarNotificacion(recordatorio);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Recordatorio guardado", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void agregarEventoAlCalendario(Recordatorio r) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) return;
        try {
            long inicio = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(r.getFechaHora()).getTime();
            long fin = inicio + 60 * 60 * 1000;
            long calId = obtenerIdCalendarioDisponible();
            if (calId == -1) return;

            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, inicio);
            values.put(CalendarContract.Events.DTEND, fin);
            values.put(CalendarContract.Events.TITLE, r.getTitulo());
            values.put(CalendarContract.Events.DESCRIPTION, r.getDescripcion());
            values.put(CalendarContract.Events.CALENDAR_ID, calId);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
            getContentResolver().insert(CalendarContract.Events.CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long obtenerIdCalendarioDisponible() {
        Cursor cursor = getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, new String[]{CalendarContract.Calendars._ID}, CalendarContract.Calendars.VISIBLE + " = 1", null, null);
        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            cursor.close();
            return id;
        }
        if (cursor != null) cursor.close();
        return -1;
    }

    private void programarNotificacion(Recordatorio r) {
        try {
            long triggerTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(r.getFechaHora()).getTime();
            Intent intent = new Intent(this, AlertaReceiver.class);
            intent.putExtra("titulo", r.getTitulo());
            intent.putExtra("descripcion", r.getDescripcion());
            PendingIntent pi = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            ((AlarmManager) getSystemService(ALARM_SERVICE)).setExact(AlarmManager.RTC_WAKEUP, triggerTime, pi);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pedirPermisosCalendario() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR}, 100);
        }
    }

    private void iniciarReconocimientoVoz() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora...");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Reconocimiento de voz no soportado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String texto = result.get(0);
                editTextDescripcion.setText(texto);
                editTextTitulo.setText(texto.length() > 40 ? texto.substring(0, 40) + "..." : texto);
                String fechaHoraDetectada = interpretarFechaHora(texto);
                if (fechaHoraDetectada != null) {
                    try {
                        calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(fechaHoraDetectada));
                        actualizarFechaHora();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String interpretarFechaHora(String texto) {
        texto = texto.toLowerCase();
        Calendar cal = Calendar.getInstance();

        if (texto.contains("mañana")) cal.add(Calendar.DAY_OF_YEAR, 1);
        else if (!texto.contains("hoy")) return null;

        int hora = 9, minuto = 0;
        Matcher m = Pattern.compile("a las (\\d{1,2})(?:[:\\.](\\d{2}))?\\s*(am|pm|de la tarde|de la mañana)?").matcher(texto);
        if (m.find()) {
            hora = Integer.parseInt(m.group(1));
            if (m.group(2) != null) minuto = Integer.parseInt(m.group(2));
            String ampm = m.group(3);
            if ((ampm != null && (ampm.contains("pm") || ampm.contains("tarde"))) && hora < 12) hora += 12;
            if ((ampm != null && (ampm.contains("am") || ampm.contains("mañana"))) && hora == 12) hora = 0;
        }

        cal.set(Calendar.HOUR_OF_DAY, hora);
        cal.set(Calendar.MINUTE, minuto);
        cal.set(Calendar.SECOND, 0);

        return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(cal.getTime());
    }

    private void cargarRecordatorioDesdeRoom(int id) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Recordatorio r = AppDatabase.getInstance(this).recordatorioDao().obtenerPorId(id);
            runOnUiThread(() -> {
                if (r != null) {
                    editTextTitulo.setText(r.getTitulo());
                    editTextDescripcion.setText(r.getDescripcion());
                    if (r.getFechaHora() != null) {
                        try {
                            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(r.getFechaHora()));
                            actualizarFechaHora();
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                    if (r.getFrecuencia() != null) {
                        int pos = ((ArrayAdapter) spinnerFrecuencia.getAdapter()).getPosition(r.getFrecuencia());
                        spinnerFrecuencia.setSelection(pos);
                    }
                    if (r.esGeoRecordatorio()) {
                        latitudSeleccionada = r.getLatitud();
                        longitudSeleccionada = r.getLongitud();
                        direccion = r.getDireccion();
                        radio = r.getRadio();
                        tvUbicacion.setText(direccion);
                        seekBarRadio.setProgress((int) radio);
                        if (mMap != null) {
                            LatLng latLng = new LatLng(latitudSeleccionada, longitudSeleccionada);
                            mMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            mMap.addCircle(new CircleOptions().center(latLng).radius(radio).strokeColor(0xFF2196F3).fillColor(0x2200BFFF));
                        }
                    }
                }
            });
        });
    }
}
