package application;

public class Pixel {
	
	int r, g, b;
	Point p;
	
	//konstruktory do poszczeg�lnych pikseli
	//obiekt Pixel przechowuje informacje na temat
	//barwy RGB, punkt przechowuje lokalizaj� piksela na obrazku
	public Pixel(int r, int g, int b, Point p) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.p = p;
	}
	
	public Pixel(int c, Point p) {
		r=c;
		g=c;
		b=c;
		this.p = p;
	}

}
