package capprotectors.saveyourcap;

import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

public class Professor {
    public static ArrayList<String> grades = new ArrayList<>();
    public static ArrayList<Integer> marks = new ArrayList<>();

    private int professorWidth;
    private int professorHeight;
    private int professorX; //center
    private int professorY;
    private float professorSpeed;
    private boolean dead = false;

    private String grade;
    private int mark;

    public Rect r = new Rect(0, 0, 0, 0);

    public Professor(int professorWidth, int professorHeight, int professorX, int professorY, float professorSpeed, int gradeId) {
        this.professorWidth = professorWidth;
        this.professorHeight = professorHeight;
        this.professorX = professorX;
        this.professorY = professorY;
        this.professorSpeed = professorSpeed;

        this.grade = grades.get(gradeId);
        this.mark = marks.get(gradeId);
    }

    public void update() {
        professorX += professorSpeed;
        r.set(professorX-professorWidth/2, professorY-professorHeight/2, professorX+professorWidth/2, professorY+professorHeight/2);
        if (r.intersect(Student.boundingBox)){
            if (this.grade.equals("F")) //TODO move condition to raw
                GameScreen.getStudent().lostALife();
            GameScreen.addScore(this.getScore());
            die();
        } else if (r.intersect(-this.professorWidth, 200, -this.professorWidth, 800)) { // TODO: replace 800 with screenHeight
            die();
        }
    }

    public void die(){
        dead = true;
    }

    public int getX() {
        return professorX;
    }

    public int getY() {
        return professorY;
    }

    public int getProfessorWidth() {
        return professorWidth;
    }

    public int getProfessorHeight() {
        return professorHeight;
    }

    public boolean isDead() {
        return dead;
    }
    
    public String getGrade() {return grade;}

    public int getScore() {
        return this.mark;
    }
}
