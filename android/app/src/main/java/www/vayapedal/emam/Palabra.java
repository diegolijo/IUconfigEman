package www.vayapedal.emam;



import java.util.Date;

public class Palabra {
        public String clave;
        public String rol;
        public Date fecha;
        public Palabra(String clave, String rol, Date fecha) {
            this.fecha= new Date(); // todo recoger la fecha del envio de la vista
            this.clave = clave;
            this.rol = rol;
        }
    }
