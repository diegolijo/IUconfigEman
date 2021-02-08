package www.vayapedal.emam.datos;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Alarma {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    @ColumnInfo(name = "usuario")
    public String usuario;

    @NonNull
    @ColumnInfo(name = "clave")
    public String clave;


    @ColumnInfo(name = "numTlfTo")
    public String numTlfTo;

    @ColumnInfo(name = "mailFrom")
    public String mailTo;

    @ColumnInfo(name = "enable")
    public boolean enable;

    public Alarma(@NonNull String usuario, @NonNull String clave, String numTlfTo, String mailTo, boolean enable) {
        this.usuario = usuario;
        this.clave = clave;
        this.numTlfTo = numTlfTo;
        this.mailTo = mailTo;
        this.enable = enable;
    }

}