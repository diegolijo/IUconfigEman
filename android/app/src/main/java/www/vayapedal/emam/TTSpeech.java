package www.vayapedal.emam;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TTSpeech {
    private static final String TAG_TTS = "TextToSpeech: ";
    /********************************************   TTS   *********************************************/

    private TextToSpeech ttsEngine;
    private final Context context;
    public boolean isSpeaking = false;


    public TTSpeech(Context context) {
        this.context = context;
        this.initTTS();
    }

    private void initTTS() {
        ttsEngine = new TextToSpeech(context, initStatus -> {
            if (initStatus == TextToSpeech.SUCCESS) {
                final Locale spanish = new Locale("es", "ES");
                // final Locale france = Locale.FRANCE;
                ttsEngine.setLanguage(spanish);

                //todo buscar otra forma de no procesar la voz sinetizada del tts
                ttsEngine.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        isSpeaking = true;
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                () -> isSpeaking = false,
                                3000);
                    }
                    @Override
                    public void onError(String utteranceId) {
                        Log.i("TextToSpeech", "On Error");
                    }
                });

            } else {
                Log.d(TAG_TTS, "Can't initialize TextToSpeech");
            }
        });
    }

    public void speak(String text, boolean remplaza, float vol) {

        Bundle bundle = new Bundle();
        bundle.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, vol);
        ttsEngine.speak(
                text,
                (remplaza) ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD,
                bundle,
                "capacitoraccessibility" + System.currentTimeMillis());

    }


    public void shutdown() {
        ttsEngine.shutdown();
        ttsEngine = null;
    }

}
