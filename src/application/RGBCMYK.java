package application;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public class RGBCMYK {
	
	// metoda do ustawienia barwy t³a etykiety RGB
	// w parametrach podawane s¹ wartoœci z pól tekstowych RGB
	// metoda uruchamia siê po klikniêciu w przycisk RGB
	public void setRgbLabel(int r, int g, int b) {
		//ustawienie koloru t³a etykiety
		Main.rgbLabel.setBackground(new Background(new BackgroundFill(new Color(r/255.0,
				g/255.0, b/255.0, 1), null, null)));
		// stworzenie tablicy z wartoœciami CMYK dla wartoœci RGB podanych w parametrach metody
		// konwersja danych RGB na CMYK
		int[] arr = convertToCmyk(new int[] {r, g, b});
		
		// uzupe³nienie pól tekstowych wartoœciami CMYK
		Main.ctf.setText(arr[0]+"");
		Main.mtf.setText(arr[1]+"");
		Main.ytf.setText(arr[2]+"");
		Main.ktf.setText(arr[3]+"");
	}
	
	// metoda do ustawienia barwy t³a etykiety CMYK
	// w parametrach podawane s¹ wartoœci z pól tekstowych CMYK
	// metoda uruchamia siê po klikniêciu w przycisk CMYK
	public void setCmykLabel(int c, int m, int y, int k) {
		float c1 = (float) (c/255.0);
		float m1 = (float) (m/255.0);
		float y1 = (float) (y/255.0);
		float k1 = (float) (k/255.0);
		// konwersja wartoœci CMYK do RGB
		int[] arr = convertToRgb(new float[] {c1, m1, y1, k1});
		// wype³nienie etykiety kolorem
		Main.cmykLabel.setBackground(new Background(new BackgroundFill(new Color(arr[0]/255.0,
				arr[1]/255.0, arr[2]/255.0, 1), null, null)));
		
		//ustawienie wartoœci pól tekstowych
		Main.rtf.setText(arr[0]+"");
		Main.gtf.setText(arr[1]+"");
		Main.btf.setText(arr[2]+"");
	}
	
	// metoda s³u¿¹ca do konwersji z RGB do CMYK
	public int[] convertToCmyk(int[] arr) {
		float c, m, y, k;
		// wykorzystanie wzorów do ustalenia wartoœci poszczególnych sk³adowych w CMYK
		k = (float) (1-(max(arr)/255.0));
		c = (float) ((1-arr[0]/255.0-k)/(1-k));
		m = (float) ((1-arr[1]/255.0-k)/(1-k));
		y = (float) ((1-arr[2]/255.0-k)/(1-k));
		
		int[] cmyk = new int[]{(int)c*255, (int)m*255, (int)y*255, (int)k*255};
		return cmyk;
	}
	
	// metoda do znalezienia maksimum wœród wartoœci RGB
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
	
	// metoda do konwersji CMYK do RGB
	// wykorzystane wzory, zwracana jest tablica typu int z wartoœciami RGB
	public int[] convertToRgb(float arr[]) {
		float r = 255*(1-arr[0])*(1-arr[3]);
		float g = 255*(1-arr[1])*(1-arr[3]);
		float b = 255*(1-arr[2])*(1-arr[3]);
		return new int[] {(int) r, (int) g, (int) b};
	}
	
}
