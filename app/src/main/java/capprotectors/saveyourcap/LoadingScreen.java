package capprotectors.saveyourcap;

import android.util.Log;

import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmActiveUser;
import com.swarmconnect.SwarmActivity;
import com.swarmconnect.SwarmMainActivity;
import com.swarmconnect.delegates.SwarmLoginListener;

import capprotectors.framework.Game;
import capprotectors.framework.Graphics;
import capprotectors.framework.Graphics.ImageFormat;
import capprotectors.framework.Screen;
import capprotectors.implementation.AndroidGame;

public class LoadingScreen extends Screen {
    public LoadingScreen(Game game) {
        super(game);
    }

    @Override
    public void update(float deltaTime) {
        Graphics g = game.getGraphics();

        Assets.mainmenu = g.newImage("mainmenu.png", ImageFormat.RGB565);
        Assets.background = g.newImage("background.png", ImageFormat.RGB565);
//        Assets.button = g.newImage("button.jpg", ImageFormat.RGB565);
        Assets.student = g.newImage("student.jpg", ImageFormat.ARGB4444);
        Assets.professor = g.newImage("professor.jpg", ImageFormat.ARGB4444);

        Assets.SU = g.newImage("SU.png", ImageFormat.ARGB4444);

        Assets.click = game.getAudio().createSound("click.ogg");

        game.getAudio().createSound("fuse.ogg").play(1f);
        try {
            Thread.sleep(2408/2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Swarm.enableAlternativeMarketCompatability();
        Swarm.setAllowGuests(true);

        SwarmLoginListener mySwarmLoginListener = new SwarmLoginListener() {

            // This method is called when the login process has started
            // (when a login dialog is displayed to the user).
            public void loginStarted() {
                Log.d("login","started");
            }

            // This method is called if the user cancels the login process.
            public void loginCanceled() {
                Log.d("login","canceled");
            }

            // This method is called when the user has successfully logged in.
            public void userLoggedIn(SwarmActiveUser user) {
                Log.d("login","loggedIn");
                game.setScreen(new SplashLoadingScreen(game));
//                Swarm.e();
            }

            // This method is called when the user logs out.
            public void userLoggedOut() {
                Log.d("login","loggedOut");
            }

        };



        game.setScreen(new MainMenuScreen(game));
    }

    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();
        g.drawScaledImage(Assets.splash, 0, 0, g.getWidth(), g.getHeight(), 0, 0, Assets.splash.getWidth(), Assets.splash.getHeight());
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void backButton() {

    }
}
