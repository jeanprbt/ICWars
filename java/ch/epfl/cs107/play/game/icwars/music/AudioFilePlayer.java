package ch.epfl.cs107.play.game.icwars.music;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class AudioFilePlayer {

    //-----------------------------------API-------------------------------------//

    public static synchronized void playLoop(String filename) {
        if (filename == null) throw new IllegalArgumentException();
        // code adapted from: http://stackoverflow.com/questions/26305/how-can-i-play-sound-in-java
        try {
            Clip clip = AudioSystem.getClip();
            final File file = new File(filename);
            AudioInputStream in = getAudioInputStream(file);
            clip.open(in);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        catch (UnsupportedAudioFileException e) {
            throw new IllegalArgumentException("unsupported audio format: '" + filename + "'", e);
        }
        catch (LineUnavailableException e) {
            throw new IllegalArgumentException("could not play '" + filename + "'", e);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("could not play '" + filename + "'", e);
        }
    }
}
