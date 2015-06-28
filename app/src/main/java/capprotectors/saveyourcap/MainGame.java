package capprotectors.saveyourcap;

import android.util.Log;

import com.swarmconnect.Swarm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import capprotectors.framework.Screen;
import capprotectors.implementation.AndroidGame;

public class MainGame extends AndroidGame {
    public static String grades;
    boolean firstTimeCreate = true;
    boolean gameOn = false;

    @Override
    public Screen getInitScreen() {
        if (firstTimeCreate) {
            Assets.load(this);
            firstTimeCreate = false;
        }

        InputStream is = getResources().openRawResource(R.raw.grades);
        grades = convertStreamToString(is);
        Swarm.preload(this, 17981, "b3efa3ee656161523093b42ecad22ae5");

        gameOn = true;
        return new SplashLoadingScreen(this);
    }

    @Override
    public void onBackPressed() {
        getCurrentScreen().backButton();
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            Log.w("LOG", e.getMessage());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.w("LOG", e.getMessage());
            }
        }
        return sb.toString();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (gameOn)
            Assets.theme.play();
    }

    @Override
    public void onPause() {
        super.onPause();
        Assets.theme.pause();
    }

    @Override
    public void finish() {
        super.finish();
        Assets.theme.stop();
    }
}
