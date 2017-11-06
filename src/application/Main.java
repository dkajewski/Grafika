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
	
	Button line, rectangle, circle, edit, set, mouse, P3P6, save, RGB, CMYK,
		fmid, fmed, fsobel, fhpass, fgauss, fmask, ftogray,
		fadd, fmin, fmulti, fdivide;
	static Label w, h, r, rgbLabel, cmykLabel,
		rLabel, gLabel, bLabel, cLabel, mLabel, yLabel, kLabel,
		rl, gl, bl, cl, ml, yl, kl;
	static TextField w1, h1, r1,
		rtf, gtf, btf, ctf, mtf, ytf, ktf,
		_1, _2, _3, _4, _5, _6, _7, _8, _9,
		foperations;
	
	DrawingManager manager;
	Point begin, end;
	
	Filter f;
	
	public PPM ppm;
	public RGBCMYK rc;
	
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
		RGB = new Button("RGB");
		CMYK = new Button("CMYK");
		
		fmid = new Button("u�r");
		fmed = new Button("med");
		fsobel = new Button("sobel");
		fhpass = new Button("grnp");
		fgauss = new Button("Gauss");
		fmask = new Button("maska");
		ftogray = new Button("szary");
		fadd = new Button("plus");
		fmin = new Button("minus");
		fmulti = new Button("mno�");
		fdivide = new Button("dziel");
		
		//ustawienie zawarto�ci etykiet
		w = new Label("szeroko��:");
		h = new Label("wysoko��:");
		r = new Label("promie�:");
		rgbLabel = new Label("");
		cmykLabel =  new Label("");
		//javafx.scene.paint.Color c = new javafx.scene.paint.Color(arg0, arg1, arg2, arg3)
		rgbLabel.setPrefSize(20, 20);
		cmykLabel.setPrefSize(20, 20);
		rgbLabel.setBackground(new Background(new 
				BackgroundFill(javafx.scene.paint.Color.BLACK, null, null)));
		cmykLabel.setBackground(new Background(new 
				BackgroundFill(javafx.scene.paint.Color.BLACK, null, null)));
		rl = new Label("R"); gl = new Label("G"); bl = new Label("B");
		cl = new Label("C"); ml = new Label("M"); yl = new Label("Y"); kl = new Label("K");
		
		//zainicjalizowanie p�l tekstowych
		w1 = new TextField(); h1 = new TextField(); r1 = new TextField();
		w1.setEditable(false);
		h1.setEditable(false);
		r1.setEditable(false);
		w1.setPrefWidth(3);
		h1.setPrefWidth(3);
		r1.setPrefWidth(3);
		rtf = new TextField("0"); gtf = new TextField("0"); btf = new TextField("0");
		rtf.setPrefWidth(3);
		gtf.setPrefWidth(3);
		btf.setPrefWidth(3);
		
		ctf = new TextField("0"); mtf = new TextField("0"); ytf = new TextField("0"); ktf = new TextField("255");
		ctf.setPrefWidth(3);
		mtf.setPrefWidth(3);
		ytf.setPrefWidth(3);
		ktf.setPrefWidth(3);
		
		_1 = new TextField("0"); _2 = new TextField("0"); _3 = new TextField("0"); _4 = new TextField("0"); 
		_5 = new TextField("0"); _6 = new TextField("0"); _7 = new TextField("0"); _8 = new TextField("0"); 
		_9 = new TextField("0"); 
		_1.setPrefWidth(3);
		_2.setPrefWidth(3);
		_3.setPrefWidth(3);
		_4.setPrefWidth(3);
		_5.setPrefWidth(3);
		_6.setPrefWidth(3);
		_7.setPrefWidth(3);
		_8.setPrefWidth(3);
		_9.setPrefWidth(3);
		foperations = new TextField("0");
		foperations.setPrefWidth(3);
		
		
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
		rightPane.add(RGB, 3, 8);
		rightPane.add(CMYK, 3, 11);
		rightPane.add(rgbLabel, 0, 7);
		rightPane.add(rtf, 1, 7);
		rightPane.add(gtf, 1, 8);
		rightPane.add(btf, 1, 9);
		rightPane.add(rl, 2, 7);
		rightPane.add(gl, 2, 8);
		rightPane.add(bl, 2, 9);
		rightPane.add(cmykLabel, 0, 10);
		rightPane.add(ctf, 1, 10);
		rightPane.add(mtf, 1, 11);
		rightPane.add(ytf, 1, 12);
		rightPane.add(ktf, 1, 13);
		rightPane.add(cl, 2, 10);
		rightPane.add(ml, 2, 11);
		rightPane.add(yl, 2, 12);
		rightPane.add(kl, 2, 13);
		rightPane.add(fmid, 0, 14);
		rightPane.add(fmed, 1, 14);
		rightPane.add(fsobel, 2, 14);
		rightPane.add(fhpass, 0, 15);
		rightPane.add(fgauss, 1, 15);
		rightPane.add(fmask, 2, 15);
		rightPane.add(_1, 0, 16);
		rightPane.add(_2, 1, 16);
		rightPane.add(_3, 2, 16);
		rightPane.add(_4, 0, 17);
		rightPane.add(_5, 1, 17);
		rightPane.add(_6, 2, 17);
		rightPane.add(_7, 0, 18);
		rightPane.add(_8, 1, 18);
		rightPane.add(_9, 2, 18);
		rightPane.add(ftogray, 3, 14);
		rightPane.add(foperations, 0, 19);
		rightPane.add(fadd, 1, 19);
		rightPane.add(fmin, 2, 19);
		rightPane.add(fmulti, 3, 19);
		rightPane.add(fdivide, 0, 20);
		
		//ustawienie panelu z p��tnem
		canvasPane = new Pane();
		canvasPane.setMinSize(0.76*WIDTH, HEIGHT);
		
		imgView = new ImageView();
		
		
		//stworzenie managera rysowania
		manager = new DrawingManager(canvasPane);
		//canvasPane.getChildren().add(canvas);
		canvasPane.getChildren().add(imgView);
		
		//dodanie paneli do wy�wietlania
		root.getChildren().addAll(canvasPane, rightPane);
		
		
		rc = new RGBCMYK();
		
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
					
					f = new Filter(image);
					
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
		
		RGB.setOnAction(event -> {
			// wczytanie warto�ci RGB z p�l tekstowych
			int r = Integer.parseInt(rtf.getText());
			int g = Integer.parseInt(gtf.getText());
			int b = Integer.parseInt(btf.getText());
			// ustawienie koloru etykiety RGB i warto�ci p�l tekstowych w CMYK
			rc.setRgbLabel(r, g, b);
			
		});
		
		CMYK.setOnAction(event -> {
			// wczytanie warto�ci CMYK z p�l tekstowych
			int c = Integer.parseInt(ctf.getText());
			int m = Integer.parseInt(mtf.getText());
			int y = Integer.parseInt(ytf.getText());
			int k = Integer.parseInt(ktf.getText());
			// ustawienie koloru etykiety CMYK i warto�ci p�l tekstowych w RGB
			rc.setCmykLabel(c, m, y, k);
		});
		
		fmask.setOnAction(event -> {
			// przycisk dodaj�cy filtr z w�asn� mask�
			// wczytywana jest warto�� z p�l tekstowych
			// i wywo�uje si� metoda nak�adaj�ca filtr na obraz
			int[][] arr = getTextFromTextFields();
			image = f.Convolution(arr);
		});
		
		fmid.setOnAction(event -> {
			// filtr u�redniaj�cy, ustawienie warto�ci p�l maski,
			// i wykonanie metody stosuj�cej filtr
			_1.setText("1");_2.setText("1");_3.setText("1");
			_4.setText("1");_5.setText("1");_6.setText("1");
			_7.setText("1");_8.setText("1");_9.setText("1");
			int[][] arr = getTextFromTextFields();
			image = f.Convolution(arr);
		});
		
		fmed.setOnAction(event -> {
			// filtr medianowy
			image = f.Mediana(3);
		});
		
		fsobel.setOnAction(event -> {
			// sobel
			// ustawienie warto�ci p�l i wywo�anie metody z tablic�
			// utworzon� na podstawie warto�ci p�l tekstowych
			_1.setText("1");_2.setText("2");_3.setText("1");
			_4.setText("0");_5.setText("0");_6.setText("0");
			_7.setText("-1");_8.setText("-2");_9.setText("-1");
			int[][] arr = getTextFromTextFields();
			image = f.Convolution(arr);
		});
		
		fhpass.setOnAction(event -> {
			// filtr g�rnoprzepustowy wyostrzaj�cy
			// ustawienie warto�ci p�l i wywo�anie metody z tablic�
			// utworzon� na podstawie warto�ci p�l tekstowych
			_1.setText("-1");_2.setText("-1");_3.setText("-1");
			_4.setText("-1");_5.setText("9");_6.setText("-1");
			_7.setText("-1");_8.setText("-1");_9.setText("-1");
			int[][] arr = getTextFromTextFields();
			image = f.Convolution(arr);
		});
		
		fgauss.setOnAction(event -> {
			// rozmycie Gaussa
			// ustawienie warto�ci p�l i wywo�anie metody z tablic�
			// utworzon� na podstawie warto�ci p�l tekstowych
			_1.setText("1");_2.setText("2");_3.setText("1");
			_4.setText("2");_5.setText("4");_6.setText("2");
			_7.setText("1");_8.setText("2");_9.setText("1");
			int[][] arr = getTextFromTextFields();
			image = f.Convolution(arr);
		});
		
		ftogray.setOnAction(event -> {
			// wywo�anie metody do konwersji obrazu w skali szaro�ci
			image = f.toGrayScale();
		});
		
		fadd.setOnAction(event -> {
			// dodanie warto�ci z pola tekstowego do ka�dego piksela na obrazie
			int a = Integer.parseInt(foperations.getText());
			image = f.add(a);
		});
		
		fmin.setOnAction(event -> {
			// odj�cie warto�ci z pola tekstowego od ka�dego piksela na obrazie
			int a = Integer.parseInt(foperations.getText());
			image = f.minus(a);
		});
		
		fmulti.setOnAction(event -> {
			// pomno�enie warto�ci z pola tekstowego z ka�dym pikselem w obrazie
			int a = Integer.parseInt(foperations.getText());
			image = f.multiply(a);
		});
		
		fdivide.setOnAction(event -> {
			// dzielenie warto�ci piksela przez liczb� z pola tekstowego
			int a = Integer.parseInt(foperations.getText());
			image = f.divide(a);
		});
		
	}
	
	// metoda konwertuj�ca warto�ci z p�l tekstowych do dwuwymiarowej tablicy typu int
	private static int[][] getTextFromTextFields() {
		int[][] arr = new int[][] {
			{Integer.parseInt(_1.getText()), Integer.parseInt(_2.getText()), Integer.parseInt(_3.getText())}, 
			{Integer.parseInt(_4.getText()), Integer.parseInt(_5.getText()), Integer.parseInt(_6.getText())}, 
			{Integer.parseInt(_7.getText()), Integer.parseInt(_8.getText()), Integer.parseInt(_9.getText())}
		};
		
		return arr;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
