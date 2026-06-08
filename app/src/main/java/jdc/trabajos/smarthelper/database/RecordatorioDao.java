package jdc.trabajos.smarthelper.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import jdc.trabajos.smarthelper.models.Recordatorio;

@Dao
public interface RecordatorioDao {

    // Insertar un nuevo recordatorio (normal o geo)
    @Insert
    void insertar(Recordatorio recordatorio);

    // Actualizar un recordatorio existente
    @Update
    void actualizar(Recordatorio recordatorio);

    // Eliminar un recordatorio
    @Delete
    void eliminar(Recordatorio recordatorio);

    // Obtener todos los recordatorios, ordenados por fecha
    @Query("SELECT * FROM recordatorios ORDER BY fechaHora ASC")
    List<Recordatorio> obtenerTodos();

    // Obtener solo recordatorios con estado 'pendiente'
    @Query("SELECT * FROM recordatorios WHERE estado = 'pendiente' ORDER BY fechaHora ASC")
    List<Recordatorio> obtenerPendientes();

    // Obtener recordatorio por su ID
    @Query("SELECT * FROM recordatorios WHERE id = :id")
    Recordatorio obtenerPorId(int id);

    // Obtener historial: estados 'completado' o 'vencido'
    @Query("SELECT * FROM recordatorios WHERE estado = :estado1 OR estado = :estado2 ORDER BY fechaHora DESC")
    List<Recordatorio> obtenerPorEstados(String estado1, String estado2);
}
