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
    @Query("SELECT * FROM Palabra WHERE palabra.usuario LIKE :usuario ORDER BY funcion")
    List<Palabra> selectPalabras(String usuario);


    @Query("SELECT * FROM Palabra WHERE palabra.funcion LIKE :funcion")
    List<Palabra> selectFuncion(String funcion);


    @Query("SELECT * FROM Palabra WHERE palabra.clave LIKE :s  LIMIT 1")
    Palabra selectPalabra(String s);

    @Insert
            (onConflict = OnConflictStrategy.ABORT)
    void insertPalabra(Palabra palabra);

    @Query("UPDATE Palabra SET fecha=:fecha WHERE clave = :clave")
    void upDate(Date fecha, String clave);

    @Delete
    void deletePalabras(Palabra palabra);


    /**
     * operacions PerfilUsuario
     */
    @Insert
            (onConflict = OnConflictStrategy.ABORT)
    void insertUsuario(Usuario user);

    @Query("SELECT * FROM Usuario")
    List<Usuario> selectUsuarios();


    @Query("SELECT * FROM Usuario WHERE usuario LIKE :usuario  LIMIT 1")
    Usuario selectUsuario(String usuario);

    @Delete
    void delete(Usuario user);

    /**
     * operacions Alarma
     */

    @Insert
            (onConflict = OnConflictStrategy.ABORT)
    void insertAlarma(Alarma alarma);

    @Query("SELECT * FROM alarma WHERE alarma.usuario LIKE :usuario ORDER BY clave")
    List<Alarma> selectAlarmas(String usuario);

    @Delete
    void deleteAlarmas(Alarma alarma);
}