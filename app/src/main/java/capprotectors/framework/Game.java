package capprotectors.framework;

public interface Game {

    Audio getAudio();

    Input getInput();

    FileIO getFileIO();

    Graphics getGraphics();

    void setScreen(Screen screen);

    Screen getCurrentScreen();

    Screen getInitScreen();
}
