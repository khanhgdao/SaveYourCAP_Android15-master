package capprotectors.saveyourcap;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmLeaderboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import capprotectors.framework.Game;
import capprotectors.framework.Graphics;
import capprotectors.framework.Input.TouchEvent;
import capprotectors.framework.Screen;

public class GameScreen extends Screen {


    enum GameState {
        Ready, Running, Paused, GameOver
    }
    GameState state = GameState.Ready;

    // Variable Setup

    private static Background bg1, bg2;

    private static Student student;
    public static ArrayList<Professor> professors = new ArrayList<>();
    int lives = 3;

    private static int score = 0;

    private static ArrayList<Item> SU = new ArrayList<>();

    // You would create game objects here.

    private float spawnChance = 0.02f;
    private float spawnChanceIncRate = 0.0021f;
    private float[] biggerGradeChance;
    private float gradeChanceChange = 0.975f;
    private int difficulty = 0;
    private int scrollSpeed = -9;
    Graphics g = game.getGraphics();

    public static int screenWidth;
    public static int screenHeight;
    Paint paint, paint2, redBigPaint, greenMedPaint;

    public GameScreen(Game game) {

        super(game);

        screenWidth = g.getWidth();
        screenHeight = g.getHeight();

        // Initialize game objects here
        bg1 = new Background(0, 0);
        bg2 = new Background(2160, 0);
        bg1.setSpeedX(scrollSpeed);
        bg2.setSpeedX(scrollSpeed);

        student = new Student(lives, Assets.student.getWidth(), Assets.student.getHeight(), 100, screenHeight/2);

        loadRaw();

        biggerGradeChance = new float[Professor.grades.size()];
        for (int i = 0; i<biggerGradeChance.length-1; i++)
            biggerGradeChance[i] = (float) Math.sqrt(1-1f/(biggerGradeChance.length-i)); //higher chance for bigger grades at first;
        biggerGradeChance[biggerGradeChance.length-1] = 0;
        for (int i = 0; i<biggerGradeChance.length; i++)
            Log.i("Grades up chance from ", i+": "+biggerGradeChance[i]);

        // Defining a paint object
        paint = new Paint();
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);

        paint2 = new Paint();
        paint2.setTextSize(100);
        paint2.setTextAlign(Paint.Align.CENTER);
        paint2.setAntiAlias(true);
        paint2.setColor(Color.WHITE);

        redBigPaint = new Paint();
        redBigPaint.setTextSize(90);
        redBigPaint.setTextAlign(Paint.Align.LEFT);
        redBigPaint.setTextScaleX(.8f);
        redBigPaint.setAntiAlias(true);
        redBigPaint.setColor(Color.RED);

        greenMedPaint = new Paint();
        greenMedPaint.setTextSize(75);
        greenMedPaint.setTextAlign(Paint.Align.CENTER);
        greenMedPaint.setAntiAlias(true);
        greenMedPaint.setColor(Color.GREEN);
    }

    private void loadRaw() {
        Scanner sc = new Scanner(MainGame.grades);
        while (sc.hasNext()) {
            Professor.grades.add(sc.next());
            Professor.marks.add(sc.nextInt());
        }
    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();

        // We have four separate update methods in this example.
        // Depending on the state of the game, we call different update methods.
        // Refer to Unit 3's code. We did a similar thing without separating the
        // update methods.

        if (state == GameState.Ready)
            updateReady(touchEvents);
        else if (state == GameState.Running)
            updateRunning(touchEvents, deltaTime);
        else if (state == GameState.Paused)
            updatePaused(touchEvents);
        else if (state == GameState.GameOver)
            updateGameOver(touchEvents);
    }

    private void updateReady(List<TouchEvent> touchEvents) {

        // This example starts with a "Ready" screen.
        // When the user touches the screen, the game begins.
        // state now becomes GameState.Running.
        // Now the updateRunning() method will be called!

        if (touchEvents.size() > 0) {
            Assets.click.play(1f);
            state = GameState.Running;
        }
    }

    private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {

        //This is identical to the update() method from our Unit 2/3 game.


        // 1. All touch input is handled here:
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);

            if (event.type == TouchEvent.SWIPE_UP) {
                if (student.getY() > screenHeight/2)
                    student.moveTo(2);
                else if (student.getY() > screenHeight/4)
                    student.moveTo(1);
                break;
            }
            else if (event.type == TouchEvent.SWIPE_DOWN) {
                if (student.getY() < screenHeight/2)
                    student.moveTo(2);
                else if (student.getY() < screenHeight*3/4)
                    student.moveTo(3);
                break;
            }
            else if (event.type == TouchEvent.TOUCH_UP) {
                if (event.x > screenWidth-100 && event.y < 100)
                    pause();
                if (event.y > screenHeight*3/4)
                    student.moveTo(3);
                else if (event.y > screenHeight/2)
                    student.moveTo(2);
                else if (event.y > screenHeight/4)
                    student.moveTo(1);
            }
        }

        // 2. Check miscellaneous events like death:

        if (student.getLives() < 1) {
            state = GameState.GameOver;
        }


        // 3. Call individual update() methods here.
        // This is where all the game updates happen.
        student.update();

        while (score - difficulty > 50) {
            increaseDifficulty();
        }

        if (Math.random()<spawnChance)
            professors.add(new Professor(Assets.professor.getWidth(), Assets.professor.getHeight(),
                    screenWidth+Assets.professor.getWidth()/2, (int) Math.floor((Math.random()*3+1))*screenHeight/4,
                    scrollSpeed, nextGrade()));

        for (int i = professors.size()-1; i>=0; i--) {
            Professor professor = professors.get(i);
            if (professor.isDead()) {
                professors.remove(i);
            }
            else {
                professor.update();
            }
        }

        bg1.update();
        bg2.update();
    }

    private void increaseDifficulty() {
        difficulty+=50;

        spawnChance += spawnChanceIncRate;
        spawnChanceIncRate -= spawnChance/200;
        spawnChanceIncRate = spawnChanceIncRate>0?spawnChanceIncRate:0;

        gradeChanceChange *= 0.975f;

        scrollSpeed = -9-score/50;
        bg1.setSpeedX(scrollSpeed);
        bg2.setSpeedX(scrollSpeed);
    }

    private int nextGrade() {
        // a method to generate next grade
        int next = 0;
        while (Math.random() < biggerGradeChance[next]*gradeChanceChange)
            next++;
        return next;
    }

    private boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
        return event.x > x && event.x < x + width - 1 && event.y > y && event.y < y + height - 1;
    }

    private void updatePaused(List<TouchEvent> touchEvents) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if (inBounds(event, screenWidth/4, screenHeight/4, screenWidth/2, screenHeight/4)) {
                    Assets.click.play(1f);
                    resume();
                }

                if (inBounds(event, screenWidth/4, screenHeight/2, screenWidth/2, screenHeight/4)) {
                    Assets.click.play(1f);
                    nullify();
                    goToMenu();
                }
            }
        }
    }

    private void updateGameOver(List<TouchEvent> touchEvents) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if (inBounds(event, 0, 0, 1280, 400)) {
                    Assets.click.play(.85f);
                    nullify();
                    game.setScreen(new MainMenuScreen(game));
                    return;
                }
                if (inBounds(event, 0, 400, 1280, 400)) {
                    Assets.click.play(.85f);
                    SwarmLeaderboard.submitScoreAndShowLeaderboard(19727, score);
                }
            }
        }

    }

    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();

        // First draw the game elements.

        // Example:
        // g.drawImage(Assets.background, 0, 0);
        // g.drawImage(Assets.character, characterX, characterY);
        g.drawImage(Assets.background, bg1.getBgX(), bg1.getBgY());
        g.drawImage(Assets.background, bg2.getBgX(), bg2.getBgY());

        g.drawImage(Assets.student, student.getX()-student.getWidth()/2, student.getY()-student.getHeight()/2);
        for (Professor professor : professors) {
            g.drawImage(Assets.professor, professor.getX()-professor.getProfessorWidth()/2, professor.getY()-professor.getProfessorHeight()/2);
            g.drawString(professor.getGrade(), professor.getX(), professor.getY(), greenMedPaint);
        }

        String hearts = "";
        for (int i=0; i<student.getLives(); i++) hearts+="♥";
        g.drawString(hearts, 50, 100, redBigPaint);
        g.drawString(score+"", screenWidth - 200, 100, paint2);
        g.drawString("FPS:"+((int) (100/deltaTime)), 60, screenHeight-25, paint); //TODO option to show/hide in setting

        //Khanh added
        if (student.getNumberSU()>0) {
            g.drawString("S/U: "+student.getNumberSU(), screenWidth-400, 100, paint2);
        }

        // Secondly, draw the UI above the game elements.
        if (state == GameState.Ready)
            drawReadyUI();
        if (state == GameState.Running)
            drawRunningUI();
        if (state == GameState.Paused)
            drawPausedUI();
        if (state == GameState.GameOver)
            drawGameOverUI();

    }

    private void nullify() {

        // Set all variables to null. You will be recreating them in the
        // constructor.
        paint = null;
        bg1 = null;
        bg2 = null;
        student = null;
        professors = null;
        // Call garbage collector to clean up memory.
        System.gc();
    }

    private void drawReadyUI() {
        Graphics g = game.getGraphics();

        g.drawARGB(155, 255, 255, 255);
        g.drawString("Swipe to move to another lane.",
                640, 300, paint);

    }

    private void drawRunningUI() {
        Graphics g = game.getGraphics();

    }

    private void drawPausedUI() {
        Graphics g = game.getGraphics();
        // Fill transparently the entire screen so you can display the Paused screen.
        g.drawARGB(155, 0, 0, 0);
        g.drawString("Resume", screenWidth/2, screenHeight*3/8, paint2);
        g.drawString("Menu",   screenWidth/2, screenHeight*5/8, paint2);
    }

    private void drawGameOverUI() {
        Graphics g = game.getGraphics();
        g.drawRect(0, 0, 1281, 801, Color.BLACK);
        g.drawString("GAME OVER.", 640, 400, paint2);
        g.drawString("Tap to return", 640, 290, paint);
        g.drawString("Submit score?", 640, 460, paint);
    }

    @Override
    public void pause() {
        if (state == GameState.Running)
            state = GameState.Paused;
    }

    @Override
    public void resume() {
        if (state == GameState.Paused)
            state = GameState.Running;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void backButton() {
        Assets.click.play(.85f);
        pause();
    }

    private void goToMenu() {
        game.setScreen(new MainMenuScreen(game));
    }

    public static Background getBg1() {
        return bg1;
    }

    public static Background getBg2() {
        return bg2;
    }

    public static Student getStudent() {
        return student;
    }

    public static void addScore(int dScore) {
        score += dScore;
    }
}