package Utilities;

public abstract class Tweens {
	
	public Tweens() {
		//nothin
	}
	
	public static double linearEase(double currentTime, double startValue, double changeInValue, double duration) {
		return changeInValue * currentTime/duration + startValue;
	}
	
	public static double quadEaseIn(double currentTime, double startValue, double changeInValue, double duration) {
		currentTime /= duration;
		return changeInValue*currentTime*currentTime + startValue;
	}
	
	public static double quadEaseOut(double currentTime, double startValue, double changeInValue, double duration) {
		currentTime /= duration;
		return -changeInValue * currentTime*(currentTime-2) + startValue;
	}
	
	public static double quadEaseInOut(double currentTime, double startValue, double changeInValue, double duration) {
		
		double value;
		
		currentTime /= duration/2;
		if (currentTime < 1)
			value = changeInValue/2*currentTime*currentTime + startValue;
		else {
			currentTime--;
			value = -changeInValue/2 * (currentTime*(currentTime-2) - 1) + startValue;
		}
		
		return value;
		
	}
	
	public static double expoEaseIn(double currentTime, double startValue, double changeInValue, double duration) {
		return changeInValue * Math.pow( 2, 10 * (currentTime/duration - 1) ) + startValue;
	}
	
	public static double expoEaseOut(double currentTime, double startValue, double changeInValue, double duration) {
		return changeInValue * ( -Math.pow( 2, -10 * currentTime/duration ) + 1 ) + startValue;
	}
	
}