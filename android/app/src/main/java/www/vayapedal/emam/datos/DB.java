package  www.vayapedal.emam.datos;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Usuario.class, Palabra.class}, version = 1,exportSchema = false)
@TypeConverters({Converters.class})
public abstract class DB extends RoomDatabase {
    public abstract DeviceDao Dao();
}