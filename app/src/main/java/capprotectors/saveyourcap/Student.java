package capprotectors.saveyourcap;

import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

public class Student {

    final float inertia = (float) 3;

    private int lives;
    private int studentWidth;
    private int studentHeight;
    private int studentX; //center
    private int studentY;
    private int studentDestY = studentY;
    private float studentSpeed;
    private boolean newMove = false;
//    private int midPoint;

    //public static ArrayList<Item> items = new ArrayList<>();
    private int numberSU;

    public static Rect boundingBox = new Rect(0, 0, 0, 0);

    public Student(int lives, int studentWidth, int studentHeight, int studentX, int studentY) {
        this.lives = lives;
        this.studentWidth = studentWidth;
        this.studentHeight = studentHeight;
        this.studentX = studentX;
        this.studentY = studentY;
    }

    public void update() {
        /*if (studentY != studentDestY) {
            if ((studentY < midPoint && midPoint < studentDestY) || (studentY > midPoint && midPoint > studentDestY)) {
                studentSpeed += Math.signum(studentDestY - studentY)*inertia;
            }
            else {
                studentSpeed -= Math.signum(studentDestY - studentY)*inertia;
            }
        }*/
        if (newMove && studentY != studentDestY) {
            studentSpeed = (studentDestY - studentY) / 18; //TODO
            newMove = false;
        }

//        if (Math.abs(studentDestY-studentY)<2*inertia && studentSpeed > 0 && (studentY+studentSpeed > studentDestY) || (studentSpeed < 0 && studentY+studentSpeed < studentDestY)) {
        if (Math.abs(studentDestY-studentY) < Math.abs(studentSpeed)) {
            studentSpeed = 0;
            studentY = studentDestY;
        }
        else {
            studentY += studentSpeed;
        }
        boundingBox.set(studentX-studentWidth/2, studentY-studentHeight/2, studentX+studentWidth/2, studentY+studentHeight/2);
    }

    public void moveTo(int y) {
        int dest = GameScreen.screenHeight*y/4;
        if (dest != studentDestY) {
            studentDestY = dest;
            newMove = true;
//            midPoint = (y + studentY) / 2;
        }
    }

    public void lostALife(){
        if (numberSU > 0) {numberSU -=1;}
        else {
            lives -= 1;
        }

    }

    public void increaseNumberSU() {numberSU +=1;}

    public int getLives() {
        return lives;
    }

    public int getX() {
        return studentX;
    }

    public int getY() {
        return studentY;
    }

    public int getWidth() {
        return studentWidth;
    }

    public int getHeight() {
        return studentHeight;
    }

    public int getNumberSU() {return numberSU;}


}
