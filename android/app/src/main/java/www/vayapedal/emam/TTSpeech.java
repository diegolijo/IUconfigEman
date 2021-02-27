package www.vayapedal.emam;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TTSpeech {
    private static final String TAG_TTS = "TextToSpeech: ";
    /********************************************   TTS   *********************************************/

    private TextToSpeech ttsEngine;
    private Context context;

    public TTSpeech(Context context) {
        this.context = context;
        this.initTTS();
    }

    private void initTTS() {
        ttsEngine = new TextToSpeech(context, initStatus -> {
            if (initStatus == TextToSpeech.SUCCESS) {
                final Locale spanish = new Locale("es", "ES");
                final Locale france = Locale.FRANCE;
                ttsEngine.setLanguage(spanish);
            } else {
                Log.d(TAG_TTS, "Can't initialize TextToSpeech");
            }
        });
    }

    public void speak(String text, boolean remplaza, float vol) {
        // todo
        Bundle bundle = new Bundle();
        bundle.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, vol);
        ttsEngine.speak(
                text,
                (remplaza) ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD,
                bundle,
                "capacitoraccessibility" + System.currentTimeMillis());
    }

}
