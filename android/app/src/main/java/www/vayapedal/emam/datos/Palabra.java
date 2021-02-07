package  www.vayapedal.emam.datos;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.util.Date;


@Entity(primaryKeys = {"clave", "rol"})
public class Palabra {

    @NonNull
    @ColumnInfo(name = "clave")
    public String clave;

    @NonNull
    @ColumnInfo(name = "rol")
    public String rol;

    @ColumnInfo(name = "fecha")
    public Date fecha;

    public Palabra(@NonNull String clave, @NonNull String rol) {
        this.clave = clave;
        this.rol = rol;
    }
}
