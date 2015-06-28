package capprotectors.saveyourcap;

import android.graphics.Rect;

/**
 * Created by DaoGiaKhanh on 28/6/15.
 */
public class Item {
    private int itemWidth;
    private int itemHeight;
    private int itemX;
    private int itemY;
    private float itemSpeed;
    private boolean gotten = false;

    public Rect r = new Rect(0,0,0,0);

    public Item(int itemWidth, int itemHeight, int itemX, int itemY, float itemSpeed) {
        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;
        this.itemX = itemX;
        this.itemY = itemY;
        this.itemSpeed = itemSpeed;

    }

    public void update() {
        itemX += itemSpeed;
        r.set(itemX-itemWidth/2, itemY-itemHeight/2, itemX+itemWidth/2, itemY+itemHeight/2);
        if (r.intersect(Student.boundingBox)) {
            GameScreen.getStudent().increaseNumberSU();
            die();
        } else if (r.intersect(-this.itemWidth, 200, -this.itemWidth, 800)) { // TODO: replace 800 with screenHeight
            die();
    }

    public void die() {gotten = true;}

    public int getX() {
        return itemX;
    }

    public int getY() {
        return itemY;
    }

    public int getProfessorWidth() {
        return itemWidth;
    }

    public int getProfessorHeight() {
        return itemHeight;
    }

    public boolean isGotten() { return gotten;}
}
