package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;


public class Main extends Application {
	public static final int WIDTH = 850;
	public static final int HEIGHT = 600;
	//horizontal box -> do uk³adu paneli
	HBox root = new HBox();
	Scene scene;
	Stage stage;
	GridPane rightPane;
	Pane canvasPane;
	
	Button line, rectangle, circle, edit, set, mouse;
	Label w, h, r;
	static TextField w1, h1, r1;
	
	DrawingManager manager;
	Point begin, end;
	
	Drawing state = Drawing.none;
	
	
	
	private double red, green, blue, opacity = 1;
	
	public static enum Drawing{
		none, line, rectangle, circle, edit, mouse
	}
	
	/**
	 * 
	 * start programu
	 */
	@Override
	public void start(Stage stage) {
		try {
			//ustawienie okna aplikacji
			setup(stage);
			
			addListeners();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * ustawienie wartoœci t³a menu
	 */
	private void initRGB(){
		
		int color = 200;
		
		red = color/255.0;
		green = color/255.0;
		blue = color/255.0;
	}
	
	private void setup(Stage stage) {
		this.stage = stage;
		scene = new Scene(root, WIDTH, HEIGHT);
		
		//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		this.stage.setScene(scene);
		this.stage.setResizable(false);
		this.stage.setTitle("Grafika");
		
		initRGB();
		rightPane = new GridPane();
		rightPane.setMinSize(0.25*WIDTH, HEIGHT);
		rightPane.setBackground(new Background(new BackgroundFill(new Color(red, green, blue, opacity), null, null)));
		
		line = new Button("Linia");
		rectangle = new Button("Prostok¹t");
		circle = new Button("Okr¹g");
		edit = new Button("Edytuj");
		set = new Button("Ustaw");
		mouse = new Button("Myszka");
		
		w = new Label("szerokoœæ:");
		h = new Label("wysokoœæ:");
		r = new Label("promieñ:");
		
		w1 = new TextField(); h1 = new TextField(); r1 = new TextField();
		w1.setEditable(false);
		h1.setEditable(false);
		r1.setEditable(false);
		w1.setPrefWidth(3);
		h1.setPrefWidth(3);
		r1.setPrefWidth(3);
		
		rightPane.add(line, 0, 0);
		rightPane.add(rectangle, 1, 0);
		rightPane.add(circle, 2, 0);
		rightPane.add(edit, 0, 1);
		rightPane.add(w, 0, 2);
		rightPane.add(h, 0, 3);
		rightPane.add(r, 0, 4);
		rightPane.add(w1, 1, 2);
		rightPane.add(h1, 1, 3);
		rightPane.add(r1, 1, 4);
		rightPane.add(set, 2, 3);
		rightPane.add(mouse, 1, 1);

		
		//ustawienie panelu z p³ótnem
		canvasPane = new Pane();
		canvasPane.setMinSize(0.76*WIDTH, HEIGHT);
		
		//stworzenie managera rysowania
		manager = new DrawingManager(canvasPane);
		//canvasPane.getChildren().add(canvas);
		
		//dodanie paneli do wyœwietlania
		root.getChildren().addAll(canvasPane, rightPane);
		
		this.stage.show();
	}
	
	/**
	 * obs³uga zdarzeñ
	 */
	public void addListeners() {
		canvasPane.setOnMousePressed(event -> {
			// obs³u¿enie momentu klikniêcia
			begin = new Point((int)event.getSceneX(), (int)event.getSceneY());
		});
		
		canvasPane.setOnMouseDragged(event -> {
			//System.out.println("dragging");
		});
		
		canvasPane.setOnMouseClicked(event -> {
			end = new Point((int)event.getSceneX(), (int)event.getSceneY());
			manager.drawFigure(state, begin, end);
		});
		
		line.setOnAction(event -> {
			state = Drawing.line;
		});
		
		rectangle.setOnAction(event -> {
			state = Drawing.rectangle;
		});
		
		circle.setOnAction(event -> {
			state = Drawing.circle;
		});
		
		edit.setOnAction(event -> {
			state = Drawing.edit;
		});
		
		set.setOnAction(event -> {
			switch(manager.instance.toString()) {
			case "line":
				Line l = (Line)manager.list.get(manager.checked);
				int a = Integer.parseInt(w1.getText());
				l.setStrokeWidth(a);
				manager.list.set(manager.checked, l);
				break;
			case "circle":
				Circle c = (Circle)manager.list.get(manager.checked);
				double r = Double.parseDouble(r1.getText());
				c.setRadius(r);
				manager.list.set(manager.checked, c);
				break;
			case "rectangle":
				Rectangle re = (Rectangle)manager.list.get(manager.checked);
				double w = Double.parseDouble(w1.getText());
				double h = Double.parseDouble(h1.getText());
				re.setWidth(w);
				re.setHeight(h);
				manager.list.set(manager.checked, re);
				break;
			}
		});
		
		mouse.setOnAction(event -> {
			state = Drawing.mouse;
		});
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
