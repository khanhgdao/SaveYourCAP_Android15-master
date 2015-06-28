package capprotectors.implementation;

import android.graphics.Bitmap;

import capprotectors.framework.Graphics.ImageFormat;
import capprotectors.framework.Image;

public class AndroidImage implements Image {
    Bitmap bitmap;
    private ImageFormat format;

    public AndroidImage(Bitmap bitmap, ImageFormat format) {
        this.bitmap = bitmap;
        this.format = format;
    }

    @Override
    public int getWidth() {
        return bitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return bitmap.getHeight();
    }

    @Override
    public ImageFormat getFormat() {
        return format;
    }

    @Override
    public void dispose() {
        bitmap.recycle();
    }
}