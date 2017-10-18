package application;

public class Pixel {
	
	int r, g, b;
	double red, green, blue;
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
	
	//konstruktor do zmiennych typu double
	//dzi�ki temu nie trzeba dzieli� kolor�w przez 255
	public Pixel(double r, double g, double b, int x, int y) {
		red = r;
		green = g;
		blue = b;
		p = new Point(x, y);
	}

}
