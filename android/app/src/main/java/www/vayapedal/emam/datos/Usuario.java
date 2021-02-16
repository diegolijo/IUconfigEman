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

    @NonNull
    @ColumnInfo(name = "loginPass")
    public String loginPass;

    @NonNull
    @ColumnInfo(name = "mailFrom")
    public String mailFrom;

    @NonNull
    @ColumnInfo(name = "mailPass")
    public String mailPass;


    public Usuario(@NonNull String usuario, String loginPass, String mailFrom, String mailPass) {
        this.usuario = usuario;
        this.loginPass = loginPass;
        this.mailFrom = mailFrom;
        this.mailPass = mailPass;
    }

}



