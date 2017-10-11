package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PPM {
	public Stage stage;
	public File file;
	public Integer width=null, height=null, color=null;
	private Scanner s;
	public Type ppm;
	//lista z pikselami
	public ArrayList<Pixel> pixels;
	//reprezentacja obrazu
	public WritableImage image;
	public PixelWriter pWriter;
	
	public static enum Type {
		p3, p6, jpg
	}
	
	public PPM(Stage stage) {
		this.stage = stage;
		//wybieranie pliku do otwarcia
		FileChooser fc = new FileChooser();
		fc.setTitle("Wybierz plik");
		
		file = fc.showOpenDialog(stage);
		pixels = new ArrayList<>();
	}
	
	//metoda sprawdzaj�ca poprawno�� pliku
	public boolean checkFile() {
		String error = "";
		int errorCounter = 0;
		//przygotowanie pliku do czytania
		try {
			s = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//otrzymanie rozszerzenia wczytanego pliku
		int i = file.getName().lastIndexOf('.');
		String ext = file.getName().substring(i+1);
		ext.trim();
		
		//sprawdzenie czy d�ugo�� rozszerzenia jest odpowiednia
		if(ext.length()!=3){
			errorCounter++;
		}
		
		//sprawdzenie czy wczytywany plik jest plikiem jpg lub ppm
		if(!ext.equals("jpg") && !ext.equals("ppm")) {
			errorCounter++;
		}
		
		//je�li na poprzednich etapach powsta�y zdarzenia
		//modyfikuj�ce errorCounter, to wy�wietlany jest komunikat
		//o niepowodzeniu wczytania pliku
		if(errorCounter>0) {
			error+="Nieprawid�owe rozszerzenie\n";
			showDialog(error);
			return false;
		}
		
		String now = "";
		//pr�ba wczytania tekstu z pliku
		//je�li pr�ba zako�czy si� sukcesem, to
		//warto�� linii jest wczytywana to zmiennej now
		//w przeciwnym wypadku plik jest graficzny i nast�puje jego przypisanie do zmiennej
		//przechowuj�cej obraz
		try {
			now = s.next();
		} catch(Exception e) {
			ppm = Type.jpg;
			try {
                BufferedImage bufferedImage = ImageIO.read(file);
                image = SwingFXUtils.toFXImage(bufferedImage, null);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
			return false;
		}
 
		//sprawdzenie czy tekst w pliku jest r�ny od P3 lub P6
		if(!now.equals("P3") && !now.equals("P6")) {
			error+="Uszkodzony plik\n";
			errorCounter++;
		}
		
		//je�li w zmiennej now jest P3, to zmienna ppm przybiera typ P3
		//podobnie dzieje si� w przypadku P6
		//ostatni else na wypadek wyst�pienia innego b��du
		if(now.equals("P3")) {
			ppm = Type.p3;
		} else if(now.equals("P6")) {
			ppm = Type.p6;
		} else {
			return false;
		}
		
		//je�li plik jest typu P3 lub P6
		//to nast�puje pobranie warto�ci koloru
		//i rozmiar�w obrazu
		//jest r�wnie� sprawdzenie czy nie wyst�puj� przypadkiem komentarze
		if(ppm == Type.p6 || ppm == Type.p3) {
			while(s.hasNext()) {
				now = s.next();
				
				if(now.charAt(0)!='#') {
					if(width==null) {
						width = Integer.parseInt(now);
					}else if(height==null) {
						height = Integer.parseInt(now);
					}else if(color==null) {
						color = Integer.parseInt(now);
						image = new WritableImage(width, height);
						break;
					}
				}else {
					s.nextLine();
				}
			}
		}
		
		switch(ppm) {
		case p3:
			//lista z warto�ciami danych P3
			ArrayList<Integer> list = new ArrayList<Integer>();

			while(s.hasNext()) {
				list.add(s.nextInt());
			}
			//zmienna do przemieszczania si� po li�cie z danymi P3
			int index=-1;
			
			//je�li sk�adowych by�o 3, to nast�puje dodanie pikseli do listy pikseli, gdzie poszczeg�lne piksele
			//maj� okre�lon� warto��
			if(list.size()==(width*height*3)) {
				for(int y=0; y<height; y++) {
					for(int x=0; x<width; x++) {
						if(color<255 || color>255) {
							int r = adjustColor(list.get(++index));
							int g = adjustColor(list.get(++index));
							int b = adjustColor(list.get(++index));
							pixels.add(new Pixel(r, g, b, new Point(x, y)));
						}else if(color==255) {
							pixels.add(new Pixel(list.get(++index), list.get(++index), list.get(++index), new Point(x, y)));
						}
					}
				}
			}else {
				return false;
			}
			return true;
		case p6:
			//P6 nie jest zrobione na pliki binarne i nie dzia�a poprawnie
			//zrobione na tej samej zasadzie co P3
			//tylko warto�ci ASCII s� przypisywane do zmiennych typu int
			String values = s.next();
			while(s.hasNext()) {
				values+=s.next();
			}
			
			int index1 = -1;
			System.out.println(values.length()+" "+width*height*3);
			if(values.length()<=(width*height*3)) {
				if(color==255) {
					for(int y1=0; y1<height; y1++) {
						for(int x1=0; x1<width; x1++) {
							if(index1+2<values.length()) {
								int r = values.charAt(++index1);
								int g = values.charAt(++index1);
								int b = values.charAt(++index1);
								if(b>1) {
									b=1;
								}
								System.out.println(r+" "+g+" "+b);
								pixels.add(new Pixel(r, g, b, new Point(x1, y1)));
							}
							
						}
						
					}
					return true;
				}else if(color>255 || color<255) {
					for(int y1=0; y1<height; y1++) {
						for(int x1=0; x1<width; x1++) {
							int r = adjustColor(values.charAt(++index1));
							int g = adjustColor(values.charAt(++index1));
							int b = adjustColor(values.charAt(++index1));
							System.out.println(r+" "+g+" "+b);
							pixels.add(new Pixel(r, g, b, new Point(x1, y1)));
						}
					}
				}
			}
			
			return false;
		default:
			break;
		}
		
		return false;
	}
	
	//metoda s�u��ca do wyr�wnywania koloru
	public int adjustColor(int number) {
		int result = (number*255)/color;
		return result;
	}
	
	//metoda przygotowuj�ca obrazek do wy�wietlenia
	public void prepareImage() {
		pWriter = image.getPixelWriter();
		//w p�tli dla ka�dego piksela jest ustawiana warto�� na podstawie wcze�niej
		//przygotowanej listy
		for(int i=0; i<pixels.size(); i++) {
			double r = pixels.get(i).r/255.0;
			double g = pixels.get(i).g/255.0;
			double b = pixels.get(i).b/255.0;
			int x = pixels.get(i).p.x;
			int y = pixels.get(i).p.y;
			
			pWriter.setColor(x, y, new Color(r, g, b, 1));
		}
	}
	
	//metoda pozwalaj�ca na wy�wietlanie komunikat�w o niepowodzeniu
	//przy wczytywaniu pliku
	public void showDialog(String message) {
		final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().add(new Text(message));
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
	}
}
