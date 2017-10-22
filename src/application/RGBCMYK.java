package application;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public class RGBCMYK {
	
	ArrayList<Pixel> pixels;
	ArrayList<CMYKpixel> CMYKp;

	final static String pathToCMYK = "CoatedFOGRA39.icc";
	public RGBCMYK() {
		pixels = new ArrayList<Pixel>();
		CMYKp = new ArrayList<CMYKpixel>();	
	}
	
	public void preparePixelsList(WritableImage image) {
		PixelReader pr = image.getPixelReader();
		
		for(int y=0; y<image.getHeight(); y++) {
			for(int x=0; x<image.getWidth(); x++) {
				Color c = pr.getColor(x, y);
				pixels.add(new Pixel(c.getRed(), c.getGreen(), c.getBlue(), x, y));
			}
		}
	}
	
	public float[] cmykToRGB(float[] cmyk) {
		try {
			if(cmyk.length == 4) {
				ColorSpace instance = new ICC_ColorSpace(ICC_Profile.getInstance(pathToCMYK));
				float[] toRGB = instance.toRGB(cmyk);
				return toRGB;
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void makeRGB(WritableImage image) {
		PixelWriter pWriter = image.getPixelWriter();
		ColorSpace cspace = null;
		try {
			cspace = new ICC_ColorSpace(ICC_Profile.getInstance(pathToCMYK));
		}catch(IOException e) {
			e.printStackTrace();
		}
		for(int i=0; i<CMYKp.size(); i++) {
			System.out.println(i);
			float[] toRGB = cspace.toRGB(CMYKp.get(i).cmyk);
			java.awt.Color c = new java.awt.Color(toRGB[0], toRGB[1], toRGB[2], 0);
			pWriter.setColor(CMYKp.get(i).x, CMYKp.get(i).y, new Color(c.getRed()/255.0, c.getGreen()/255.0, c.getBlue()/255.0, 1));
		}
	}
	
	public void makeCMYK(WritableImage image) {
		PixelWriter pWriter = image.getPixelWriter();
		ColorSpace cspace = null;
		try {
			cspace = new ICC_ColorSpace(ICC_Profile.getInstance(pathToCMYK));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0; i<pixels.size(); i++) {
			float[] toCmyk = cspace.fromRGB(new float[] {(float)pixels.get(i).red, (float)pixels.get(i).green, (float)pixels.get(i).blue});
			java.awt.Color c = new java.awt.Color(cspace, toCmyk, 0);
			pWriter.setColor(pixels.get(i).p.x, pixels.get(i).p.y, new Color(c.getRed()/255.0, c.getGreen()/255.0, c.getBlue()/255.0, 1));
			CMYKp.add(new CMYKpixel(toCmyk, pixels.get(i).p.x, pixels.get(i).p.y));
		}
	}
	
	public void setRgbLabel(int r, int g, int b) {
		Main.rgbLabel.setBackground(new Background(new BackgroundFill(new Color(r/255.0,
				g/255.0, b/255.0, 1), null, null)));
		int[] arr = convertToCmyk(new int[] {r, g, b});
		Main.ctf.setText(arr[0]+"");
		Main.mtf.setText(arr[1]+"");
		Main.ytf.setText(arr[2]+"");
		Main.ktf.setText(arr[3]+"");
		
	}
	
	public void setCmykLabel(int c, int m, int y, int k) {
		float c1 = (float) (c/255.0);
		float m1 = (float) (c/255.0);
		float y1 = (float) (c/255.0);
		float k1 = (float) (c/255.0);
		int[] arr = convertToRgb(new float[] {c1, m1, y1, k1});
		Main.cmykLabel.setBackground(new Background(new BackgroundFill(new Color(arr[0]/255.0,
				arr[1]/255.0, arr[2]/255.0, 1), null, null)));
		Main.rtf.setText(arr[0]+"");
		Main.gtf.setText(arr[1]+"");
		Main.btf.setText(arr[2]+"");
		
	}
	
	public int[] convertToCmyk(int[] arr) {
		int[] cmyk = new int[4];
		float c, m, y, k;
		k =(float) (1-(max(arr)/255.0));
		c = (float) ((1-arr[0]/255.0-k)/(1-k));
		m = (float) ((1-arr[1]/255.0-k)/(1-k));
		y = (float) ((1-arr[2]/255.0-k)/(1-k));
		
		cmyk = new int[]{(int)c*255, (int)m*255, (int)y*255, (int)k*255};
		return cmyk;
	}
	
	public int max(int[] arr) {
		int m = arr[0];
		if(arr[1]>m) {
			m=arr[1];
		}
		
		if(arr[2]>m) {
			m=arr[2];
		}
		return m;
	}
	
	public int[] convertToRgb(float arr[]) {
		float r = 255*(1-arr[0])*(1-arr[3]);
		float g = 255*(1-arr[1])*(1-arr[3]);
		float b = 255*(1-arr[2])*(1-arr[3]);
		return new int[] {(int) r, (int) g, (int) b};
	}
	
}
