package application;

import javafx.scene.image.WritableImage;

public class PS5 {
	
	WritableImage img;
	double[][] redPixels, greenPixels, bluePixels;
	
	public PS5(WritableImage img) {
		this.img = img;
		redPixels = new double[(int)img.getWidth()][(int)img.getHeight()];
		greenPixels = new double[(int)img.getWidth()][(int)img.getHeight()];
		bluePixels = new double[(int)img.getWidth()][(int)img.getHeight()];
	}
	
	public void findMinMax() {
		for(int y=0; y<img.getHeight(); y++) {
			for(int x=0; x<img.getWidth(); x++) {
				
			}
		}
	}
	
	public void prepareArrays() {
		for(int y=0; y<img.getHeight(); y++) {
			for(int x=0; x<img.getWidth(); x++) {
				
			}
		}
	}

}
