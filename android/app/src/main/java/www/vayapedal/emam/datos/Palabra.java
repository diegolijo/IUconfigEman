package www.vayapedal.emam.datos;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;


@Entity(primaryKeys = {"clave", "usuario"})
public class Palabra {

    @NonNull
    @ColumnInfo(name = "clave")
    public String clave;

    @NonNull
    @ColumnInfo(name = "usuario")
    public String usuario;

    @NonNull
    @ColumnInfo(name = "funcion")
    public String funcion;

    @ColumnInfo(name = "descripcion")
    public String descripcion;

    @ColumnInfo(name = "fecha")
    public Date fecha;

    public Palabra(@NonNull String clave, @NonNull String funcion, @NonNull String usuario, String descripcion) {
        this.clave = clave;
        this.funcion = funcion;
        this.descripcion = descripcion;
        this.usuario = usuario;
    }
}
