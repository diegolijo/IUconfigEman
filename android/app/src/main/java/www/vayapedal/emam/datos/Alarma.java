package www.vayapedal.emam.datos;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(primaryKeys = {"funcion", "usuario", "numTlfTo"})
public class Alarma {


    @NonNull
    @ColumnInfo(name = "funcion")
    public String funcion;

    @NonNull
    @ColumnInfo(name = "usuario")
    public String usuario;

    @NonNull
    @ColumnInfo(name = "numTlfTo")
    public String numTlfTo;


    @ColumnInfo(name = "mailTo")
    public String mailTo;


    @ColumnInfo(name = "enable")
    public boolean enable;

    public Alarma(@NonNull String funcion, @NonNull String usuario, @NonNull String numTlfTo, String mailTo, boolean enable) {
        this.usuario = usuario;
        this.funcion = funcion;
        this.numTlfTo = numTlfTo;
        this.mailTo = mailTo;
        this.enable = enable;
    }

}