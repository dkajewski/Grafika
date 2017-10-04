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
	// zmienna przechowuj¹ca numer z edytowan¹ obecnie figur¹
	int checked;
	// zmienna z informacj¹ o typie edytowanej obecnie figury
	CheckedInstance instance;

	public DrawingManager(Pane canvas) {
		this.canvas = canvas;
	}

	// rysowanie figury
	// zmienna state co nale¿y narysowaæ, a zmienne begin i end okreœlaj¹
	// miejsce, w którym ma siê pojawiæ figura
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
		// sprawdzenie czy punkt pocz¹tku i koñca nie s¹ takie same
		// jeœli nie, to jest rysowana linia
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
			// czyli w jaki sposób zosta³ przeci¹gniêty kursor:
			// s¹ 4 mo¿liwoœci: od lewego górnego rogu do dolnego prawego,
			// od prawego górnego rogu do lewego dolnego,
			// od dolnego prawego rogu do górnego lewego i
			// od dolnego lewego rogu do prawego górnego;
			// w zale¿noœci od przypadku ró¿ne koordynaty s¹ przypisywane wierzcho³kowi
			// pocz¹tkowemu, aby unikn¹æ ujemnych wartoœci przy podawaniu
			// d³ugoœci i szerokoœci
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
		//obliczanie d³ugoœci promienia okrêgu
		double radius = Math.sqrt((Math.pow(a, 2))+(Math.pow(b, 2)));
		// jeœli promieñ jest wiêkszy od 0, to rysowany jest okr¹g
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
	
	// enum z dostêpnymi figurami
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
			//sprawdzenie czy element listy jest okrêgiem
			if(list.get(i) instanceof Circle) {
				Circle c = (Circle) list.get(i);
				double x0=c.getCenterX(), y0=c.getCenterY();
				//sprawdzenie czy klikniêto w œrodku okrêgu
				//skorzystano z wzoru matematycznego sprawdzaj¹cego czy
				//punkt jest w obrêbie okrêgu
				//jeœli pierwiastek z sumy iloczynów ró¿nic odleg³oœci wspó³rzêdnych
				// X i Y jest mniejszy od promienia, to znaczy, ¿e punkt znajduje siê
				//w okrêgu i mo¿na ten okr¹g zaznaczyæ i przeznaczyæ do edycji
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
				// sprawdzenie czy element jest lini¹
			}else if(list.get(i) instanceof Line) {
				Line l = (Line) list.get(i);
				//wyznaczenie œrodka odcinka
				double sx = (l.getStartX()+l.getEndX())/2;
				double sy = (l.getStartY()+l.getEndY())/2;
				double r = Math.sqrt(Math.pow(l.getEndX()-sx, 2)+Math.pow(l.getEndY()-sy, 2));
				// podobnie jak w przypadku okrêgu okreœlana jest odleg³oœæ punktu
				// klikniêcia od linii;
				// jeœli punkt klikniêcia znajduje siê niedaleko œrodka linii,
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
				//sprawdzenie czy element listy jest prostok¹tem
			} else if(list.get(i) instanceof Rectangle) {
				Rectangle r = (Rectangle) list.get(i);
				int x1 = (int)r.getX();
				int y1 = (int)r.getY();
				int x2 = (int)(r.getX()+r.getWidth());
				int y3 = (int)(r.getY()+r.getHeight());
				// jeœli klikniêcie jest w obrêbie punktów prostok¹tu,
				// to prostok¹t jest zaznaczany i gotowy do edycji
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
	
	// usuniêcie zaznaczenia figury
	// wywo³ywane w przypadku zmiany figury przeznaczonej do edycji
	private void uncheck() {
		// wczytanie zaznaczonego elementu
		Node elem = list.get(checked);
		// sprawdzenie czy jakiœ element jest zaznaczony
		// jeœli jest, to w obecnie zaznaczonym elemencie ustawiany jest kolor
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
	
	// zmiana parametrów obiektów za pomoc¹ myszki
	private void moveMarkedFigure(Point begin, Point end) {
		switch(instance) {
		case line:
			// zmiana pozycji koñca linii
			// ustalona jest tolerancja 10px, by nie trzeba by³o trafiæ idealnie w punkt
			Line l = (Line)list.get(checked);
			if(begin.x>l.getEndX()-10 && begin.y>l.getEndY()-10 && begin.x<l.getEndX()+10 && begin.y<l.getEndY()+10) {
				l.setEndX(end.x);
				l.setEndY(end.y);
				list.set(checked, l);
			}
			break;
		case circle:
			// zmiana promienia okrêgu
			// nowy promieñ jest sum¹ obecnego promienia z ró¿nic¹ odleg³oœci klikniêcia
			// wykonanego w celu dokonania edycji figury
			Circle c = (Circle)list.get(checked);
			c.setRadius(c.getRadius()+(end.x-begin.x));
			list.set(checked, c);
			Main.r1.setText(c.getRadius()+"");
			break;
		case rectangle:
			// edycja prostok¹ta
			// dopuszczalna jest zmiana jego wysokoœci oraz szerokoœci
			// obliczenie nowych parametrów odbywa siê poprzez 
			// zsumowanie obecnego parametru z ró¿nic¹ koñca z pocz¹tkiem klikniêcia
			// z poszczególnych wspó³rzêdnych
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
