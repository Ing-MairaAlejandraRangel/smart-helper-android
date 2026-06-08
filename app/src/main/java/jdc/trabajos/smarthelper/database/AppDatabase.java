package jdc.trabajos.smarthelper.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import jdc.trabajos.smarthelper.models.Recordatorio;

@Database(entities = {Recordatorio.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract RecordatorioDao recordatorioDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "smart_helper_db"
                            )
                            .fallbackToDestructiveMigration() // ⚠️ Borra y recrea la base de datos si hay conflicto de versión
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
