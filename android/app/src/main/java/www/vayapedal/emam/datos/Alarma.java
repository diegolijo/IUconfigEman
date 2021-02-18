package www.vayapedal.emam.datos;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Alarma {


    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "funcion")
    public String funcion;

    @NonNull
    @ColumnInfo(name = "usuario")
    public String usuario;


    @ColumnInfo(name = "numTlfTo")
    public String numTlfTo;

    @ColumnInfo(name = "mailFrom")
    public String mailTo;


    @ColumnInfo(name = "enable")
    public boolean enable;

    public Alarma(@NonNull String funcion, @NonNull String usuario, String numTlfTo, String mailTo, boolean enable) {
        this.usuario = usuario;
        this.funcion = funcion;
        this.numTlfTo = numTlfTo;
        this.mailTo = mailTo;
        this.enable = enable;
    }

}