package www.vayapedal.emam.datos;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Usuario {

    @PrimaryKey
    @NonNull
    public String usuario;

    @ColumnInfo(name = "loginPass")
    public String loginPass;

    @ColumnInfo(name = "mailPass")
    public String mailPass;

    @ColumnInfo(name = "numTlfTo")
    public int numTlfTo;

    @ColumnInfo(name = "mailFrom")
    public String mailFrom;

    public Usuario(@NonNull String usuario, String loginPass, String mailPass, String numTlfTo, String mailFrom) {
        this.usuario = usuario;
        this.loginPass = loginPass;
        this.loginPass = mailPass;
        this.loginPass = numTlfTo;
        this.loginPass = mailFrom;
    }

}



