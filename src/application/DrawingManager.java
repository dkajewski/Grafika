package application;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class DrawingManager {
	// pole do rysowania figury
	Pane canvas;
	// lista z narysowanymi figurami
	ObservableList<Node> list;
	// zmienna przechowuj�ca numer z edytowan� obecnie figur�
	int checked;
	// zmienna z informacj� o typie edytowanej obecnie figury
	CheckedInstance instance;

	public DrawingManager(Pane canvas) {
		this.canvas = canvas;
	}

	// rysowanie figury
	// zmienna state co nale�y narysowa�, a zmienne begin i end okre�laj�
	// miejsce, w kt�rym ma si� pojawi� figura
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
		// sprawdzenie czy punkt pocz�tku i ko�ca nie s� takie same
		// je�li nie, to jest rysowana linia
		if(begin.x != end.x && begin.y != end.y) {
			Line line = new Line(begin.x, begin.y, end.x, end.y);
			line.setStrokeWidth(3);
			canvas.getChildren().add(line);
		}
	}
	
	// rysowanie prostok�ta
	private void drawRectangle(Point begin, Point end) {
		if(begin.x != end.x && begin.y != end.y) {
			Rectangle rect;
			// sprawdzenie w jaki spos�b zosta� narysowany prostok�t
			// czyli w jaki spos�b zosta� przeci�gni�ty kursor:
			// s� 4 mo�liwo�ci: od lewego g�rnego rogu do dolnego prawego,
			// od prawego g�rnego rogu do lewego dolnego,
			// od dolnego prawego rogu do g�rnego lewego i
			// od dolnego lewego rogu do prawego g�rnego;
			// w zale�no�ci od przypadku r�ne koordynaty s� przypisywane wierzcho�kowi
			// pocz�tkowemu, aby unikn�� ujemnych warto�ci przy podawaniu
			// d�ugo�ci i szeroko�ci
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
	
	//rysowanie okr�gu
	private void drawCircle(Point begin, Point end) {
		Circle circle = new Circle();
		int a = end.x-begin.x;
		int b = end.y-begin.y;
		//obliczanie d�ugo�ci promienia okr�gu
		double radius = Math.sqrt((Math.pow(a, 2))+(Math.pow(b, 2)));
		// je�li promie� jest wi�kszy od 0, to rysowany jest okr�g
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
	
	// enum z dost�pnymi figurami
	// zmienna instance tego typu przechowuje informacje
	// o tym, jakiego typu figura jest obecnie edytowana
	enum CheckedInstance{
		line, rectangle, circle
	}
	
	// zaznaczenie figury przeznaczonej do edycji
	private void markFigureToEdit(Point p) {
		//lista z wsztstkimi narysowanymi figurami
		list = canvas.getChildren();
		for(int i=0; i<list.size(); i++) {
			//sprawdzenie czy element listy jest okr�giem
			if(list.get(i) instanceof Circle) {
				Circle c = (Circle) list.get(i);
				double x0=c.getCenterX(), y0=c.getCenterY();
				//sprawdzenie czy klikni�to w �rodku okr�gu
				//skorzystano z wzoru matematycznego sprawdzaj�cego czy
				//punkt jest w obr�bie okr�gu
				//je�li pierwiastek z sumy iloczyn�w r�nic odleg�o�ci wsp�rz�dnych
				// X i Y jest mniejszy od promienia, to znaczy, �e punkt znajduje si�
				//w okr�gu i mo�na ten okr�g zaznaczy� i przeznaczy� do edycji
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
				// sprawdzenie czy element jest lini�
			}else if(list.get(i) instanceof Line) {
				Line l = (Line) list.get(i);
				//wyznaczenie �rodka odcinka
				double sx = (l.getStartX()+l.getEndX())/2;
				double sy = (l.getStartY()+l.getEndY())/2;
				double r = Math.sqrt(Math.pow(l.getEndX()-sx, 2)+Math.pow(l.getEndY()-sy, 2));
				// podobnie jak w przypadku okr�gu okre�lana jest odleg�o�� punktu
				// klikni�cia od linii;
				// je�li punkt klikni�cia znajduje si� niedaleko �rodka linii,
				// to linia jest zaznaczana i gotowa do edycji
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
				//sprawdzenie czy element listy jest prostok�tem
			} else if(list.get(i) instanceof Rectangle) {
				Rectangle r = (Rectangle) list.get(i);
				int x1 = (int)r.getX();
				int y1 = (int)r.getY();
				int x2 = (int)(r.getX()+r.getWidth());
				int y3 = (int)(r.getY()+r.getHeight());
				// je�li klikni�cie jest w obr�bie punkt�w prostok�tu,
				// to prostok�t jest zaznaczany i gotowy do edycji
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
	
	// usuni�cie zaznaczenia figury
	// wywo�ywane w przypadku zmiany figury przeznaczonej do edycji
	private void uncheck() {
		// wczytanie zaznaczonego elementu
		Node elem = list.get(checked);
		// sprawdzenie czy jaki� element jest zaznaczony
		// je�li jest, to w obecnie zaznaczonym elemencie ustawiany jest kolor
		// obramowania na czarny 
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
	
	// zmiana parametr�w obiekt�w za pomoc� myszki
	private void moveMarkedFigure(Point begin, Point end) {
		switch(instance) {
		case line:
			// zmiana pozycji ko�ca linii
			// ustalona jest tolerancja 10px, by nie trzeba by�o trafi� idealnie w punkt
			Line l = (Line)list.get(checked);
			if(begin.x>l.getEndX()-10 && begin.y>l.getEndY()-10 && begin.x<l.getEndX()+10 && begin.y<l.getEndY()+10) {
				l.setEndX(end.x);
				l.setEndY(end.y);
				list.set(checked, l);
			}
			break;
		case circle:
			// zmiana promienia okr�gu
			// nowy promie� jest sum� obecnego promienia z r�nic� odleg�o�ci klikni�cia
			// wykonanego w celu dokonania edycji figury
			Circle c = (Circle)list.get(checked);
			c.setRadius(c.getRadius()+(end.x-begin.x));
			list.set(checked, c);
			Main.r1.setText(c.getRadius()+"");
			break;
		case rectangle:
			// edycja prostok�ta
			// dopuszczalna jest zmiana jego wysoko�ci oraz szeroko�ci
			// obliczenie nowych parametr�w odbywa si� poprzez 
			// zsumowanie obecnego parametru z r�nic� ko�ca z pocz�tkiem klikni�cia
			// z poszczeg�lnych wsp�rz�dnych
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
