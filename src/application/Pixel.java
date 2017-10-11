package application;

public class Pixel {
	
	int r, g, b;
	Point p;
	
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
