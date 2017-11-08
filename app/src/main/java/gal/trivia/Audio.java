package gal.trivia;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by Gal on 18/05/2017.
 */

public class Audio {

    static MediaPlayer mp;
    static boolean isPlaying=false;

    public static void playSound(Context app, int id) {
        isPlaying=true;
        mp = MediaPlayer.create(app, id);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                isPlaying=false;
                mediaPlayer.release();
            }

        });
        mp.start();
    }

    public static void stopSound(){
        if (isPlaying) {
            mp.stop();
            mp.release();
            isPlaying=false;
        }

    }


}
