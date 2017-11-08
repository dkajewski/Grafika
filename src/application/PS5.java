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
	
	// tablice daj¹ informacjê na temat iloœci pikseli danego koloru
	int[] redVals, greenVals, blueVals;
	
	// tablice do dystrybuanty
	double[] redD, greenD, blueD;
	
	public PS5(WritableImage img) {
		this.img = img;
		//inicjalizacja tablic z wartoœciami pikseli
		redPixels = new double[(int)img.getWidth()][(int)img.getHeight()];
		greenPixels = new double[(int)img.getWidth()][(int)img.getHeight()];
		bluePixels = new double[(int)img.getWidth()][(int)img.getHeight()];
		
		// inicjacja tablic w których przechowywana jest iloœæ pikseli danego odcienia koloru
		redVals = new int[256];
		greenVals = new int[256];
		blueVals = new int[256];
		
		pr = img.getPixelReader();
		pw = img.getPixelWriter();
		prepareArrays();
		findMinMax();
	}
	
	// metoda do znajdowania wartoœci minimalnej danej barwy
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
	
	// metoda wstawiaj¹ca do tablic wartoœci danych pikseli
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
	
	// metoda do rozszerzenia histogramu
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
	
	// stworzenie dystrybuanty
	public void createD() {
		redD = new double[256];
		greenD = new double[256];
		blueD = new double[256];
		// zmienna przechowuje iloœæ pikseli w obrazie
		int s = (int)(img.getWidth()*img.getHeight());
		
		// ustalenie pocz¹tkowych wartoœci dystrybuanty
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
	
	// metoda do okreœlenia pierwszej niezerowej wartoœci dystrybuanty
	private double getFirstD(double[] d) {
		for (int i=0; i<256; i++) {
			if (d[i] != 0) {
				return d[i];
			}
		}
		return 0;
	}
	
	// metoda do wyrównania histogramu
	public WritableImage histogramEqualization() {
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
	
	// metoda do binaryzacji
	public WritableImage binary(int value) {
		int r, g, b;
		for(int y=0; y<img.getHeight(); y++) {
			for(int x=0; x<img.getWidth(); x++) {
				r = (int) (pr.getColor(x, y).getRed()*255);
				g = (int) (pr.getColor(x, y).getGreen()*255);
				b = (int) (pr.getColor(x, y).getBlue()*255);
				if((r+g+b)/3 > value) {
					pw.setColor(x, y, new Color(1, 1, 1, 1));
				}else {
					pw.setColor(x, y, new Color(0, 0, 0, 1));
				}
			}
		}
		
		return img;
	}
	
	// metoda binaryzacji procentowej
	public WritableImage binaryPercent(int percent) {
		int r, g, b;
		int value = (percent*255/100);
		
		for(int y=0; y<img.getHeight(); y++) {
			for(int x=0; x<img.getWidth(); x++) {
				r = (int) (pr.getColor(x, y).getRed()*255);
				g = (int) (pr.getColor(x, y).getGreen()*255);
				b = (int) (pr.getColor(x, y).getBlue()*255);
				if((r+g+b)/3 > value) {
					pw.setColor(x, y, new Color(1, 1, 1, 1));
				}else {
					pw.setColor(x, y, new Color(0, 0, 0, 1));
				}
			}
		}
		
		return img;
	}

}
