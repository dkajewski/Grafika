package application;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;


public class Main extends Application {
	public static final int WIDTH = 850;
	public static final int HEIGHT = 600;
	//horizontal box -> do uk�adu paneli
	HBox root = new HBox();
	Scene scene;
	Stage stage;
	GridPane rightPane;
	Pane canvasPane;
	ImageView imgView;
	WritableImage image;
	
	Button line, rectangle, circle, edit, set, mouse, P3P6, save;
	Label w, h, r;
	static TextField w1, h1, r1;
	
	DrawingManager manager;
	Point begin, end;
	
	public PPM ppm;
	
	// ustawienie stanu pocz�tkowego
	// w tym stanie nie mo�na nic narysowa�
	Drawing state = Drawing.none;
	
	
	//zmienne t�a panelu menu
	//private double red, green, blue, opacity = 1;
	
	//enum ze stanami okre�laj�cymi co nale�y rysowa� lub edytowa� oraz
	//w jaki spos�b tego dokona�
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
	
	private void setup(Stage stage) {
		this.stage = stage;
		scene = new Scene(root, WIDTH, HEIGHT);
		
		this.stage.setScene(scene);
		this.stage.setResizable(false);
		this.stage.setTitle("Grafika");
		
		//okre�lenie parametr�w panelu menu
		//initRGB();
		rightPane = new GridPane();
		rightPane.setMinSize(0.25*WIDTH, HEIGHT);
		rightPane.setBackground(new Background(new BackgroundFill(null, null, null)));
		
		//inicjalizacja przycisk�w
		line = new Button("Linia");
		rectangle = new Button("Prostok�t");
		circle = new Button("Okr�g");
		edit = new Button("Edytuj");
		set = new Button("Ustaw");
		mouse = new Button("Myszka");
		P3P6 = new Button("PPM");
		save = new Button("Zapisz");
		
		//ustawienie zawarto�ci etykiet
		w = new Label("szeroko��:");
		h = new Label("wysoko��:");
		r = new Label("promie�:");
		
		//zainicjalizowanie p�l tekstowych
		w1 = new TextField(); h1 = new TextField(); r1 = new TextField();
		w1.setEditable(false);
		h1.setEditable(false);
		r1.setEditable(false);
		w1.setPrefWidth(3);
		h1.setPrefWidth(3);
		r1.setPrefWidth(3);
		
		//dodanie element�w do panelu z menu
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
		rightPane.add(P3P6, 0, 5);
		rightPane.add(save, 1, 5);

		
		//ustawienie panelu z p��tnem
		canvasPane = new Pane();
		canvasPane.setMinSize(0.76*WIDTH, HEIGHT);
		
		imgView = new ImageView();
		
		
		//stworzenie managera rysowania
		manager = new DrawingManager(canvasPane);
		//canvasPane.getChildren().add(canvas);
		
		//dodanie paneli do wy�wietlania
		root.getChildren().addAll(canvasPane, rightPane);
		
		this.stage.show();
	}
	
	/**
	 * obs�uga zdarze�
	 */
	public void addListeners() {
		canvasPane.setOnMousePressed(event -> {
			// obs�u�enie momentu klikni�cia
			// zapisanie wsp�rz�dnych momentu wci�ni�cia przycisku myszy
			// do zmiennej begin
			begin = new Point((int)event.getSceneX(), (int)event.getSceneY());
		});
		
		//obs�u�enie przeci�gania - nie wykorzystywane w programie
		canvasPane.setOnMouseDragged(event -> {
			//System.out.println("dragging");
		});
		
		// obs�u�enie uwolnienia przycisku myszki
		// w zmiennej end przechowywane s� wsp�rz�dne miejsca, gdzie
		// przycisk myszki zosta� zwolniony
		canvasPane.setOnMouseClicked(event -> {
			end = new Point((int)event.getSceneX(), (int)event.getSceneY());
			// wywo�anie metody decyduj�cej o tym, co narysowa� 
			// w parametrach podawany jest stan, czyli informacja co nale�y narysowa�
			// zmienne begin i end to punkty klikni�cia i zwolnienia przycisku myszki
			manager.drawFigure(state, begin, end);
		});
		
		// obs�u�enie klikni�cia w przycisk "Linia"
		// ustawienie stanu na rysowanie linii
		line.setOnAction(event -> {
			state = Drawing.line;
		});
		
		// obs�u�enie klikni�cia w przycisk "Prostok�t"
		// ustawienie stanu na rysowanie prostok�ta
		rectangle.setOnAction(event -> {
			state = Drawing.rectangle;
		});
		
		// obs�u�enie klikni�cia w przycisk "Okr�g"
		// ustawienie stanu na rysowanie okr�gu
		circle.setOnAction(event -> {
			state = Drawing.circle;
		});
		
		// obs�u�enie klikni�cia w przycisk "Edytuj"
		// ustawienie stanu na edycj�
		// po wci�ni�ciu przycisku mo�na klikn�� w jak�� figur� i w ten spos�b j�
		// zaznaczy� jako obiekt przeznaczony do edycji
		edit.setOnAction(event -> {
			state = Drawing.edit;
		});
		
		// obs�u�enie klikni�cia w przycisk "Ustaw"
		// po klikni�ciu w ten przycisk s� ustawiane parametry figury
		// znajduj�cej si� w stanie edycji, wczytywana jest figura z listy obecnie
		// narysowanych figur i ustawia si� dla niej parametry podane w
		// polach tekstowych
		set.setOnAction(event -> {
			// manager.instance daje informacj� jaka figura jest obecnie edytowana
			// ma to na celu zmian� w�a�ciwych parametr�w figury 
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
		
		// obs�u�enie klikni�cia w przycisk "Myszka"
		// ustawienie stanu na edycj� figur za pomoc� myszki
		mouse.setOnAction(event -> {
			state = Drawing.mouse;
		});
		
		//przycisk otwieraj�cy okno umo�liwiaj�ce wyb�r pliku
		//do wczytania
		P3P6.setOnAction(event -> {
			ppm = new PPM(stage);
			
			//je�li metoda sprawdzaj�ca zwr�ci true,
			//wy�wietlany jest wczytany obraz ppm P3
			//w przeciwnym wypadku nast�puje pr�ba
			//otworzenia pliku jpg, je�li to nie jest mo�liwe,
			//nast�puje wy�wietlenie okna z komunikatem o b��dzie
			if(ppm.checkFile()) {
				ppm.prepareImage();
				image = ppm.image;
				
				imgView.setImage(image);
				imgView.setFitHeight(ppm.height);
				imgView.setFitWidth(ppm.width);
				
				canvasPane.getChildren().add(imgView);				
			}else {
				if(ppm.ppm == PPM.Type.jpg) {
					image = ppm.image;
					imgView.setImage(image);
					canvasPane.getChildren().add(imgView);
				}else {
					ppm.showDialog("Co� si�... co� si� popsu�o...");
				}
			}
		});
		
		//obs�u�enie przycisku do zapisu pliku
		save.setOnAction(event -> {
			FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Zapisz obraz");
            fileChooser.getExtensionFilters().addAll(
            		new FileChooser.ExtensionFilter("PNG", "*.png"),
            		new FileChooser.ExtensionFilter("JPG", "*.jpg")
            		);
            
            //nast�puje wczytanie wy�wietlanego obrazu do zmiennej img
            //i okre�lenie jego rozmiar�w
            //nast�pnie plik jest zapisywany w lokacji wskazanej 
            //przez u�ytkownika
            File file = fileChooser.showSaveDialog(stage);
            BufferedImage img = SwingFXUtils.fromFXImage(ppm.image, null);
            BufferedImage imgRGB = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
            imgRGB.createGraphics().drawImage(img, 0, 0, Color.WHITE, null);
            if (file != null) {
                try {
                    ImageIO.write(imgRGB, "png", file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
		});
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
