package  www.vayapedal.emam.datos;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.util.Date;


@Entity(primaryKeys = {"clave", "funcion"})
public class Palabra {

    @NonNull
    @ColumnInfo(name = "clave")
    public String clave;

    @NonNull
    @ColumnInfo(name = "funcion")
    public String funcion;

    @ColumnInfo(name = "fecha")
    public Date fecha;

    public Palabra(@NonNull String clave, @NonNull String funcion) {
        this.clave = clave;
        this.funcion = funcion;
    }
}
