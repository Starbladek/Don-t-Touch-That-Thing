package Audio;

import java.io.BufferedInputStream;
import java.util.HashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class JukeBox {
	
	private static HashMap<String, Clip> clips;	//Holds the audio
	private static int gap;
	private static boolean mute = false;
	
	//Sounds
	
	public static void init() {
		
		clips = new HashMap<String, Clip>();
		gap = 0;
		
		load("/SFX/Hit1.wav", "Hit1", 0);
		load("/SFX/Hit2.wav", "Hit2", -10);
		load("/SFX/Hit3.wav", "Hit3", -10);
		
		load("/SFX/Score1.wav", "Score1", -10);
		
		load("/SFX/ThingTouch1.wav", "ThingTouch1", -10);
		load("/SFX/ThingTouch2.wav", "ThingTouch2", -10);
		load("/SFX/ThingTouch3.wav", "ThingTouch3", -10);
		load("/SFX/ThingTouch4.wav", "ThingTouch4", -10);
		
		load("/SFX/PowerUp1.wav", "PowerUp1", -10);
		
		load("/SFX/ChargeShot.wav", "ChargeShot", 0);
		
		load("/SFX/MenuSelect.wav", "MenuSelect", -10);
		load("/SFX/Transition1.wav", "Transition1", 0);
		
		load("/SFX/Scare.wav", "Scare", 0);
		
		load("/SFX/Bark1.wav", "Bark1", -15);
		load("/SFX/Bark2.wav", "Bark2", -15);
		
		load("/Music/MenuMusic.mp3", "MenuMusic", -15);
		load("/Music/StarPower.wav", "StarPower", -10);
		load("/Music/PowerMusic.wav", "PowerMusic", -5);
		load("/Music/GameMusic.mp3", "GameMusic", -10);
		load("/Music/HURRYUP.mp3", "HURRYUP", -10);
		
	}
	
	//Basic audio loader for .WAV only
	/*public static void load(String s, String n) {
		
		if(clips.get(n) != null) return;
		
		try {
			
			Clip clip = AudioSystem.getClip();
			
			//BufferedInputStream allows for mark/reset support
			BufferedInputStream myStream = new BufferedInputStream(BufferedInputStream.class.getClass().getResourceAsStream(s));
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(myStream);
			
			//This does not allow for mark/reset support, leading to errors on command startup
			//AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(JukeBox.class.getResourceAsStream(s));
			
			clip.open(audioInputStream);
			clips.put(n, clip);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}*/
	
	//Audio loader for any audio format
	public static void load(String s, String n, int volume) {
		
		if(clips.get(n) != null) return;
		
		try {
			
			Clip clip = AudioSystem.getClip();
			
			BufferedInputStream myStream = new BufferedInputStream(BufferedInputStream.class.getClass().getResourceAsStream(s));
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(myStream);
			
			AudioFormat baseFormat = audioInputStream.getFormat();
			AudioFormat decodeFormat = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED,
				baseFormat.getSampleRate(),
				16,
				baseFormat.getChannels(),
				baseFormat.getChannels() * 2,
				baseFormat.getSampleRate(),
				false
			);
			
			AudioInputStream decodedAudioInputStream = AudioSystem.getAudioInputStream(decodeFormat, audioInputStream);
			clip = AudioSystem.getClip();
			
			clip.open(decodedAudioInputStream);
			clips.put(n, clip);
			
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(volume);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void play(String s) {
		play(s, gap);
	}
	
	public static void play(String s, int i) {
		
		if(mute) return;
		Clip c = clips.get(s);
		if(c == null) return;
		if(c.isRunning()) c.stop();
		c.setFramePosition(i);
		while(!c.isRunning()) c.start();
		
	}
	
	public static void stop(String s) {
		
		if(clips.get(s) == null) return;
		if(clips.get(s).isRunning()) clips.get(s).stop();
		
	}
	
	public static void resume(String s) {
		
		if(mute) return;
		if(clips.get(s).isRunning()) return;
		clips.get(s).start();
		
	}
	
	public static void loop(String s) {
		loop(s, gap, gap, clips.get(s).getFrameLength() - 1);
	}
	
	public static void loop(String s, int frame) {
		loop(s, frame, gap, clips.get(s).getFrameLength() - 1);
	}
	
	public static void loop(String s, int start, int end) {
		loop(s, gap, start, end);
	}
	
	public static void loop(String s, int frame, int start, int end) {
		
		stop(s);
		if (mute) return;
		clips.get(s).setLoopPoints(start, end);
		clips.get(s).setFramePosition(frame);
		clips.get(s).loop(Clip.LOOP_CONTINUOUSLY);
		
	}
	
	public static void setPosition(String s, int frame) {
		clips.get(s).setFramePosition(frame);
	}
	
	public static int getFrames(String s) { return clips.get(s).getFrameLength(); }
	public static int getPosition(String s) { return clips.get(s).getFramePosition(); }
	public static boolean getRunning(String s) { return clips.get(s).isRunning(); }
	
	public static void close(String s) {
		stop(s);
		clips.get(s).close();
	}
	
	public static void setVolume(String s, float value) {
		FloatControl gainControl = (FloatControl) clips.get(s).getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(value);
	}
	
}