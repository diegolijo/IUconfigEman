package www.vayapedal.emam.datos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface DeviceDao {

    /**
     * operacions Palabra
     */
    @Query("SELECT * FROM Palabra ORDER BY rol")
    List<Palabra> selectPalabras();

    @Query("SELECT * FROM Palabra WHERE palabra.clave LIKE :s  LIMIT 1")
    Palabra selectPalabra(String s);

    @Insert
            (onConflict = OnConflictStrategy.REPLACE)
    void insertPalabra(Palabra palabra);

    @Query("UPDATE Palabra SET fecha=:fecha WHERE clave = :clave")
    void upDate(Date fecha, String clave);

    @Delete
    void deletePalabras(Palabra palabra);


    /**
     * operacions PerfilUsuario
     */

    @Query("SELECT * FROM Usuario")
    List<Usuario> selectUsuarios();

    @Insert
            (onConflict = OnConflictStrategy.REPLACE)
    void insertUsuario(Usuario user);

    @Query("SELECT * FROM Usuario WHERE usuario LIKE :usuario  LIMIT 1")
    Usuario selectUsuario(String usuario);


    @Delete
    void delete(Usuario user);

    /**
     * operacions Alarma
     */

    @Insert
            (onConflict = OnConflictStrategy.REPLACE)
    void insertAlarma(Alarma alarma);


}