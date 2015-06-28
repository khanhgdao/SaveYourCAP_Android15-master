package capprotectors.implementation;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;

import capprotectors.framework.Music;

public class AndroidMusic implements Music, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener {
    private MediaPlayer mediaPlayer;
    private boolean isPrepared = false;

    public AndroidMusic(AssetFileDescriptor assetDescriptor) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(assetDescriptor.getFileDescriptor(),
                    assetDescriptor.getStartOffset(),
                    assetDescriptor.getLength());
            mediaPlayer.prepare();
            isPrepared = true;
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnVideoSizeChangedListener(this);

        } catch (Exception e) {
            throw new RuntimeException("Couldn't load music");
        }
    }

    @Override
    public void dispose() {

        if (this.mediaPlayer.isPlaying()){
            this.mediaPlayer.stop();
        }
        this.mediaPlayer.release();
    }

    @Override
    public boolean isLooping() {
        return mediaPlayer.isLooping();
    }

    @Override
    public boolean isPlaying() {
        return this.mediaPlayer.isPlaying();
    }

    @Override
    public boolean isStopped() {
        return !isPrepared;
    }

    @Override
    public void pause() {
        if (this.mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }



    @Override
    public void play() {
        if (this.mediaPlayer.isPlaying())
            return;

        try {
            synchronized (this) {
                if (!isPrepared)
                    mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setLooping(boolean isLooping) {
        mediaPlayer.setLooping(isLooping);
    }

    @Override
    public void setVolume(float volume) {
        mediaPlayer.setVolume(volume, volume);
    }

    @Override
    public void stop() {
        if (this.mediaPlayer.isPlaying()){
            this.mediaPlayer.stop();

            synchronized (this) {
                isPrepared = false;
            }}
    }

    @Override
    public void onCompletion(MediaPlayer player) {
        synchronized (this) {
            isPrepared = false;
        }
    }

    @Override
    public void seekBegin() {
        mediaPlayer.seekTo(0);

    }


    @Override
    public void onPrepared(MediaPlayer player) {
        synchronized (this) {
            isPrepared = true;
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer player) {
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer player, int width, int height) {
    }
}