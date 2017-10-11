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
	public ArrayList<Pixel> pixels;
	public WritableImage image;
	public PixelWriter pWriter;
	
	public static enum Type {
		p3, p6, jpg
	}
	
	public PPM(Stage stage) {
		this.stage = stage;
		FileChooser fc = new FileChooser();
		fc.setTitle("Wybierz plik");
		
		file = fc.showOpenDialog(stage);
		pixels = new ArrayList<>();
	}
	
	public boolean checkFile() {
		String error = "";
		int errorCounter = 0;
		try {
			s = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		int i = file.getName().lastIndexOf('.');
		String ext = file.getName().substring(i+1);
		ext.trim();
		if(ext.length()!=3){
			errorCounter++;
		}
		
		if(!ext.equals("jpg") && !ext.equals("ppm")) {
			errorCounter++;
		}
		
		if(errorCounter>0) {
			error+="Nieprawid³owe rozszerzenie\n";
			showDialog(error);
			return false;
		}
		
		String now = "";
		try {
			now = s.nextLine();
		} catch(Exception e) {
			ppm = Type.jpg;
			try {
                BufferedImage bufferedImage = ImageIO.read(file);
                image = SwingFXUtils.toFXImage(bufferedImage, null);
                //myImageView.setImage(image);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
			return false;
		}
 
		if(!now.equals("P3") && !now.equals("P6")) {
			error+="Uszkodzony plik\n";
			errorCounter++;
		}
		
		if(now.equals("P3")) {
			ppm = Type.p3;
		} else if(now.equals("P6")) {
			ppm = Type.p6;
		} else {
			return false;
		}
		s.nextLine();
		if(ppm == Type.p6 || ppm == Type.p3) {
			while(s.hasNext()) {
				if(now.charAt(0)!='#') {
					if(width==null) {
						width = s.nextInt();
						height = s.nextInt();
						color = s.nextInt();
						image = new WritableImage(width, height);
						break;
					}
				}
			}
		}
		
		switch(ppm) {
		case p3:
			//lista z wartoœciami danych P3
			ArrayList<Integer> list = new ArrayList<Integer>();

			while(s.hasNext()) {
				list.add(s.nextInt());
			}
			//zmienna do przemieszczania siê po liœcie z danymi P3
			int index=-1;
			
			//jeœli sk³adowych by³o 3, to nastêpuje dodanie pikseli do listy pikseli, gdzie poszczególne piksele
			//maj¹ okreœlon¹ wartoœæ
			if(list.size()==(width*height*3)) {
				for(int y=0; y<width; y++) {
					for(int x=0; x<height; x++) {
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
			}else if(list.size()==(width*height)) {
				
			}else {
				return false;
			}
			return true;
		case p6:
			break;
		default:
			break;
		}
		
		return false;
	}
	
	public int adjustColor(int number) {
		int result = (number*255)/color;
		return result;
	}
	
	public void prepareImage() {
		pWriter = image.getPixelWriter();
		for(int i=0; i<pixels.size(); i++) {
			double r = pixels.get(i).r/255.0;
			double g = pixels.get(i).g/255.0;
			double b = pixels.get(i).b/255.0;
			int x = pixels.get(i).p.x;
			int y = pixels.get(i).p.y;
			
			pWriter.setColor(x, y, new Color(r, g, b, 1));
		}
	}
	
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
