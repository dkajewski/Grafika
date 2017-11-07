package application;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class PS5 {
	
	WritableImage img;
	double[][] redPixels, greenPixels, bluePixels;
	PixelReader pr;
	PixelWriter pw;
	
	double rmin=1, gmin=1, bmin=1, rmax=0, gmax=0, bmax=0;
	
	// tablice daj� informacj� na temat ilo�ci pikseli danego koloru
	int[] redVals, greenVals, blueVals;
	
	double[] redD, greenD, blueD;
	
	public PS5(WritableImage img) {
		this.img = img;
		redPixels = new double[(int)img.getWidth()][(int)img.getHeight()];
		greenPixels = new double[(int)img.getWidth()][(int)img.getHeight()];
		bluePixels = new double[(int)img.getWidth()][(int)img.getHeight()];
		
		redVals = new int[256];
		greenVals = new int[256];
		blueVals = new int[256];
		
		pr = img.getPixelReader();
		pw = img.getPixelWriter();
		prepareArrays();
		findMinMax();
	}
	
	public void findMinMax() {
		for(int y=0; y<img.getHeight(); y++) {
			for(int x=0; x<img.getWidth(); x++) {
				if(rmin>pr.getColor(x, y).getRed()) rmin=pr.getColor(x, y).getRed();
				if(gmin>pr.getColor(x, y).getGreen()) gmin=pr.getColor(x, y).getGreen();
				if(bmin>pr.getColor(x, y).getBlue()) bmin=pr.getColor(x, y).getBlue();
				
				if(rmax<pr.getColor(x, y).getRed()) rmax=pr.getColor(x, y).getRed();
				if(gmax<pr.getColor(x, y).getGreen()) gmax=pr.getColor(x, y).getGreen();
				if(bmax<pr.getColor(x, y).getBlue()) bmax=pr.getColor(x, y).getBlue();
			}
		}
	}
	
	public void prepareArrays() {
		for(int y=0; y<img.getHeight(); y++) {
			for(int x=0; x<img.getWidth(); x++) {
				redPixels[x][y]=pr.getColor(x, y).getRed();
				greenPixels[x][y]=pr.getColor(x, y).getGreen();
				bluePixels[x][y]=pr.getColor(x, y).getBlue();
				
				redVals[(int)(pr.getColor(x, y).getRed()*255)]++;
				greenVals[(int)(pr.getColor(x, y).getGreen()*255)]++;
				blueVals[(int)(pr.getColor(x, y).getBlue()*255)]++;
			}
		}
	}
	
	public WritableImage stretchHistogram() {
		int rpix;
		int gpix;
		int bpix;
		for(int y=0; y<img.getHeight(); y++) {
			for(int x=0; x<img.getWidth(); x++) {
				rpix=(int)(255/((rmax*255) - (rmin*255))*((redPixels[x][y]*255)-(rmin*255)));
				gpix=(int)(255/((gmax*255) - (gmin*255))*((pr.getColor(x, y).getGreen()*255)-(gmin*255)));
				bpix=(int)(255/((bmax*255) - (bmin*255))*((pr.getColor(x, y).getBlue()*255)-(bmin*255)));
			
				pw.setColor(x, y, new Color(rpix/255.0, gpix/255.0, bpix/255.0, 1));
			}
		}
		
		return img;
	}
	
	public void createD() {
		redD = new double[256];
		greenD = new double[256];
		blueD = new double[256];
		
		int s = (int)(img.getWidth()*img.getHeight());
		redD[0] = redVals[0];
		greenD[0] = greenVals[0];
		blueD[0] = blueVals[0];
		
		for(int i=1; i<256; i++) {
			redD[i] = redD[i-1]+redVals[i];
			redD[i-1]/=s;
			greenD[i] = greenD[i-1]+greenVals[i];
			greenD[i-1]/=s;
			blueD[i] = blueD[i-1]+blueVals[i];
			blueD[i-1]/=s;
		}
		
		redD[255]/=s;
		greenD[255]/=s;
		blueD[255]/=s;
	}
	
	private double getFirstD(double[] d) {
		for (int i=0; i<256; i++) {
			if (d[i] != 0) {
				return d[i];
			}
		}
		return 0;
	}
	
	public WritableImage alignHistogram() {
		double dR = getFirstD(redD);
		double dG = getFirstD(greenD);
		double dB = getFirstD(blueD);
		double r, g, b;
		for(int y=0; y<img.getHeight(); y++) {
			for(int x=0; x<img.getWidth(); x++) {
				r = pr.getColor(x, y).getRed()*255;
				g = pr.getColor(x, y).getGreen()*255;
				b = pr.getColor(x, y).getBlue()*255;
				
				redPixels[x][y] = ((redD[(int)r]-dR) / (1-dR)*255)/255.0;
				greenPixels[x][y] = ((greenD[(int)g]-dG) / (1-dG)*255)/255.0;
				bluePixels[x][y] = ((blueD[(int)b]-dB) / (1-dB)*255)/255.0;
				pw.setColor(x, y, new Color(redPixels[x][y], greenPixels[x][y], bluePixels[x][y], 1));
			}
		}
		
		return img;
	}

}
