package www.vayapedal.emam;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/************************************************* MAIL *********************************************/
public class MailBuilder extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private final String passMail;
    private final String mailFrom;
    private final String mailTo;
    private final String cuerpoMail;/*= "Envio alerta! \nposición:";*/
    private final String asuntoMail; /*= "EMAN Alerta";*/
    private final String texto;
    private final String localizacion;

    MailBuilder(Context context, String passMail, String mailFrom,
                String mailTo, String cuerpoMail, String asuntoMail, String texto,  String localizacion) {

        this.context = context;
        this.passMail = passMail;
        this.mailFrom = mailFrom;
        this.mailTo = mailTo;
        this.cuerpoMail = cuerpoMail;
        this.asuntoMail = asuntoMail;
        this.texto = texto;
        this.localizacion = localizacion;

    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Properties propiedades = new Properties();
            propiedades.put("mail.smtp.auth", "true");
            propiedades.put("mail.smtp.starttls.enable", "true");
            propiedades.put("mail.smtp.host", "smtp.gmail.com");
            propiedades.put("mail.smtp.port", "587");
            propiedades.put("mail.smtp.socketFactory.port", 587);
            Session session = Session.getInstance(propiedades, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailFrom, passMail);
                }
            });
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailFrom));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(mailTo));
            message.setSubject(asuntoMail);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy 'T'HH:mm:ss'Z'", new Locale("ES"));
            Date date = new Date();
            String dateTime = dateFormat.format(date);
            String text = cuerpoMail + " " + Constantes.W_MAPS + localizacion + "\n" + "\n" + dateTime + "\n--" + texto;
            message.setText(text);
            Transport.send(message);


        } catch (MessagingException e) {
            /** NOS REDIRIGE A LA WEB DE GOOGLE para permitir al acceso de aplicaciones poco seguras */
            // fixme- debe ser el gmail@  con el que está logeado el telefono
            String url;
            url = Constantes.PERMITIR_APPS_POCO_SEGURAS;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Log.e("MailBuilder", Objects.requireNonNull(e.getMessage()));
        }
        return null;
    }

    //todo  FUNCIONES NUEVAS:
    // ---  tarea programada que chequee la actividad del usuario con el dispositivo para disparar la alarma

}