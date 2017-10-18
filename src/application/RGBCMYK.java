package application;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
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
	
}
