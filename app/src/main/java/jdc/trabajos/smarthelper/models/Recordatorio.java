package jdc.trabajos.smarthelper.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recordatorios")
public class Recordatorio {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String titulo;
    private String descripcion;
    private String fechaHora; // Para recordatorio normal
    private String frecuencia; // diaria, semanal, etc.
    private String estado; // pendiente, completado

    // Campos geográficos (se usan solo si es un geo-recordatorio)
    private Double latitud;
    private Double longitud;
    private Float radio;
    private String direccion;

    public Recordatorio() {}

    // Constructor para recordatorio normal
    public Recordatorio(String titulo, String descripcion, String fechaHora, String frecuencia, String estado) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
        this.frecuencia = frecuencia;
        this.estado = estado;
    }

    // Constructor para geo-recordatorio
    public Recordatorio(String titulo, String descripcion, Double latitud, Double longitud, Float radio, String direccion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.radio = radio;
        this.direccion = direccion;
        this.estado = "pendiente";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFechaHora() { return fechaHora; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }

    public String getFrecuencia() { return frecuencia; }
    public void setFrecuencia(String frecuencia) { this.frecuencia = frecuencia; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public Float getRadio() { return radio; }
    public void setRadio(Float radio) { this.radio = radio; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public boolean esGeoRecordatorio() {
        return latitud != null && longitud != null;
    }

}
