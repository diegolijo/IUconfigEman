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


    @Query("SELECT * FROM Palabra WHERE palabra.funcion LIKE :funcion AND palabra.usuario LIKE :usuario ORDER BY funcion")
    List<Palabra> selectPalabrasFuncion(String usuario, String funcion);


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

    @Query("SELECT * FROM alarma WHERE alarma.usuario LIKE :usuario ORDER BY funcion")
    List<Alarma> selectAlarmas(String usuario);


    @Query("SELECT * FROM alarma WHERE usuario LIKE :usuario AND funcion LIKE :funcion LIMIT 1")
    Alarma selectAlarmasFun(String usuario, String funcion);


    @Delete
    void deleteAlarmas(Alarma alarma);

}