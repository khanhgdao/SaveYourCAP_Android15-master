package capprotectors.implementation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AndroidFastRenderView extends SurfaceView implements Runnable {
    private AndroidGame game;
    private Bitmap framebuffer;
    private Thread renderThread = null;
    private SurfaceHolder holder;
    private volatile boolean running = false;

    public AndroidFastRenderView(AndroidGame game, Bitmap frameBuffer) {
        super(game);
        this.game = game;
        this.framebuffer = frameBuffer;
        this.holder = getHolder();
    }

    public AndroidFastRenderView(Context context){ // Lint Error
        super(context);
    }

    public void resume() {
        running = true;
        renderThread = new Thread(this);
        renderThread.start();
    }

    public void run() {
        Rect dstRect = new Rect();
        long startTime = System.nanoTime();
        while(running) {
            if (!holder.getSurface().isValid())
                continue;

            float deltaTime = (System.nanoTime() - startTime) / 10000000.000f;
            startTime = System.nanoTime();

            if (deltaTime > 3.15){ // safe cap to prevent major glitch during frame drops
                deltaTime = (float) 3.15;
            }

            game.getCurrentScreen().update(deltaTime);
            game.getCurrentScreen().paint(deltaTime);

            Canvas canvas = holder.lockCanvas();
            canvas.getClipBounds(dstRect);
            canvas.drawBitmap(framebuffer, null, dstRect, null);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        running = false;
        while(true) {
            try {
                renderThread.join();
                break;
            } catch (InterruptedException e) {
                // retry
            }
        }
    }
}
