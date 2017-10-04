package application;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class DrawingManager {

	Pane canvas;
	ObservableList<Node> list;
	int checked;
	CheckedInstance instance;

	public DrawingManager(Pane canvas) {
		this.canvas = canvas;
	}

	// rysowanie figury
	public void drawFigure(Main.Drawing state, Point begin, Point end) {
		switch(state.toString()) {
		case "none":
			break;
		case "line":
			drawLine(begin, end);
			break;
		case "rectangle":
			drawRectangle(begin, end);
			break;
		case "circle":
			drawCircle(begin, end);
			break;
		case "edit":
			markFigureToEdit(begin);
			break;
		case "mouse":
			moveMarkedFigure(begin, end);
			break;
		}
	}

	// rysowanie linii
	private void drawLine(Point begin, Point end) {
		if(begin.x != end.x && begin.y != end.y) {
			Line line = new Line(begin.x, begin.y, end.x, end.y);
			line.setStrokeWidth(3);
			canvas.getChildren().add(line);
		}
	}
	
	// rysowanie prostok¹ta
	private void drawRectangle(Point begin, Point end) {
		if(begin.x != end.x && begin.y != end.y) {
			Rectangle rect;
			// sprawdzenie w jaki sposób zosta³ narysowany prostok¹t
			if(begin.x<end.x && begin.y<end.y) {
				rect = new Rectangle(begin.x, begin.y, end.x-begin.x, end.y-begin.y);
			}else if(begin.x>end.x && begin.y<end.y) {
				rect = new Rectangle(end.x, begin.y, begin.x-end.x, end.y-begin.y);
			}else if(begin.x>end.x && begin.y>end.y) {
				rect = new Rectangle(end.x, end.y, begin.x-end.x, begin.y-end.y);
			}else {//begin.x<end.x && begin.y>end.y
				rect = new Rectangle(begin.x, end.y, end.x-begin.x, begin.y-end.y);
			}
			
			rect.setFill(Color.TRANSPARENT);
			rect.setStroke(Color.BLACK);
			rect.setStrokeWidth(3);
			canvas.getChildren().add(rect);
		}
	}
	
	//rysowanie okrêgu
	private void drawCircle(Point begin, Point end) {
		Circle circle = new Circle();
		int a = end.x-begin.x;
		int b = end.y-begin.y;
		//obliczanie d³ugoœci promienia ko³a
		double radius = Math.sqrt((Math.pow(a, 2))+(Math.pow(b, 2)));
		if(radius>0) {
			circle.setCenterX(begin.x);
			circle.setCenterY(begin.y);
			circle.setRadius(radius);
			circle.setFill(Color.TRANSPARENT);
			circle.setStroke(Color.BLACK);
			circle.setStrokeWidth(3);
			canvas.getChildren().add(circle);
		}
	}
	
	enum CheckedInstance{
		line, rectangle, circle
	}
	
	// zaznaczenie figury przeznaczonej do edycji
	private void markFigureToEdit(Point p) {
		list = canvas.getChildren();
		for(int i=0; i<list.size(); i++) {
			if(list.get(i) instanceof Circle) {
				Circle c = (Circle) list.get(i);
				double x0=c.getCenterX(), y0=c.getCenterY();
				//sprawdzenie czy klikniêto w œrodku okrêgu
				if(Math.sqrt((p.x-x0)*(p.x-x0) + (p.y-y0)*(p.y-y0)) < c.getRadius()) {
					uncheck();
					c.setStroke(Color.RED);
					checked = i;
					instance = CheckedInstance.circle;
					Main.r1.setEditable(true);
					Main.r1.setText(c.getRadius()+"");
					Main.w1.setEditable(false);
					Main.w1.setText("");
					Main.h1.setEditable(false);
					Main.h1.setText("");
					break;
				}
			}else if(list.get(i) instanceof Line) {
				Line l = (Line) list.get(i);
				//wyznaczenie œrodka odcinka
				double sx = (l.getStartX()+l.getEndX())/2;
				double sy = (l.getStartY()+l.getEndY())/2;
				double r = Math.sqrt(Math.pow(l.getEndX()-sx, 2)+Math.pow(l.getEndY()-sy, 2));
				double distance = Math.sqrt((p.x-sx)*(p.x-sx) + (p.y-sy)*(p.y-sy));
				//System.out.println(distance);
				if(distance < r/2) {
					uncheck();
					l.setStroke(Color.RED);
					checked = i;
					instance = CheckedInstance.line;
					Main.r1.setEditable(false);
					Main.h1.setEditable(false);
					Main.w1.setEditable(true);
					Main.r1.setText("");
					Main.h1.setText("");
					Main.w1.setText(l.getStrokeWidth()+"");
					break;
				}
			} else if(list.get(i) instanceof Rectangle) {
				Rectangle r = (Rectangle) list.get(i);
				int x1 = (int)r.getX();
				int y1 = (int)r.getY();
				int x2 = (int)(r.getX()+r.getWidth());
				int y3 = (int)(r.getY()+r.getHeight());
				
				if(p.x>x1 && p.x<x2 && p.y>y1 && p.y<y3) {
					uncheck();
					r.setStroke(Color.RED);
					checked = i;
					instance = CheckedInstance.rectangle;
					Main.r1.setEditable(false);
					Main.r1.setText("");
					Main.w1.setEditable(true);
					Main.w1.setText(r.getWidth()+"");
					Main.h1.setEditable(true);
					Main.h1.setText(r.getHeight()+"");
					break;
					
				}
			}
		}
		
	}
	
	// usuniêcie zaznaczenia
	private void uncheck() {
		Node elem = list.get(checked);
		if(instance!=null) {
			switch(instance) {
			case line:
				Line l = (Line)elem;
				l.setStroke(Color.BLACK);
				break;
			case rectangle:
				Rectangle r = (Rectangle)elem;
				r.setStroke(Color.BLACK);
				break;
			case circle:
				Circle c = (Circle)elem;
				c.setStroke(Color.BLACK);
				break;
			}
		}
	}
	
	// zmiana parametrów obiektów za pomoc¹ myszki
	private void moveMarkedFigure(Point begin, Point end) {
		switch(instance) {
		case line:
			Line l = (Line)list.get(checked);
			if(begin.x>l.getEndX()-10 && begin.y>l.getEndY()-10 && begin.x<l.getEndX()+10 && begin.y<l.getEndY()+10) {
				l.setEndX(end.x);
				l.setEndY(end.y);
				list.set(checked, l);
			}
			break;
		case circle:
			Circle c = (Circle)list.get(checked);
			c.setRadius(c.getRadius()+(end.x-begin.x));
			list.set(checked, c);
			Main.r1.setText(c.getRadius()+"");
			break;
		case rectangle:
			Rectangle r = (Rectangle)list.get(checked);
			r.setWidth(r.getWidth()+(end.x-begin.x));
			r.setHeight(r.getHeight()+(end.y-begin.y));
			Main.w1.setText(r.getWidth()+"");
			Main.h1.setText(r.getHeight()+"");
			list.set(checked, r);
			break;
		}
	}
	
}
