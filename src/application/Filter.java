package application;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Filter {

    private WritableImage image;
    private static Color[][] imageColors;
    private PixelWriter pw;
    private PixelReader pr;

    public Filter(WritableImage i){
        image = i;
        pw = image.getPixelWriter();
        pr = image.getPixelReader();
        imageColors = new Color[(int)image.getWidth()][(int)image.getHeight()];

        // wczytanie wartoœci pikseli w obrazie
        for (int j = 0; j < imageColors.length; j++) {
            for (int k = 0; k < imageColors[0].length; k++) {
                Color c = pr.getColor(j, k);

                imageColors[j][k] = c;
            }
        }
    }

    // metoda nak³adaj¹ca filtr
    // w parametrze podawana jest tablica z mask¹
    public WritableImage Convolution(int[][] kernel) {
        int w = (int)image.getWidth(), h = (int)image.getHeight();
        int sumR = 0, sumG = 0, sumB = 0;
        int margin = (kernel.length-1)/2;

        for (int i = margin; i < w - margin; i++) {
            for (int j = margin; j < h - margin; j++) {

            	// mno¿enie wartoœci poszczególnych kolorów
            	// przez wartoœci z maski
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        sumR += (imageColors[i+k-margin][j+l-margin].getRed()*255) * kernel[k][l];
                        sumG += (imageColors[i+k-margin][j+l-margin].getGreen()*255) * kernel[k][l];
                        sumB += (imageColors[i+k-margin][j+l-margin].getBlue()*255) * kernel[k][l];
                    }
                }
                
                // zabezpieczenie przed posiadaniem wartoœci mniejszych od 0 i wiêkszych od 255
                sumR = Math.min(Math.max(sumR,0),255);
                sumB = Math.min(Math.max(sumB,0),255);
                sumG = Math.min(Math.max(sumR,0),255);

                // stworzenie nowego koloru
                Color c = new Color(sumR/255.0, sumG/255.0, sumB/255.0, 1);

                //ustawienie wartoœci pocz¹tkowych w zmiennych przechowuj¹cych nowy kolor
                sumR = sumG = sumB = 0;

                // ustawienie nowej wartoœci 
                pw.setColor(i, j, c);

            }
        }
        return image;
    }

    // metoda nak³adaj¹ca filtr medianowy
    public WritableImage Mediana(int size){
        int w = (int)image.getWidth(), h = (int)image.getHeight();
        int med[] = new int[3];
        int[][] colors = new int[3][size*size];
        int margin = (size-1)/2;

        for (int i = margin; i < w - margin; i++) {
            for (int j = margin; j < h - margin; j++) {
                int x = 0;
                // wczytanie wartoœci pikseli 
                for (int k = 0; k < size; k++) {
                    for (int l = 0; l < size; l++) {
                        colors[0][x] = (int)(imageColors[i+k-margin][j+l-margin].getRed()*255);
                        colors[1][x] = (int)(imageColors[i+k-margin][j+l-margin].getGreen()*255);
                        colors[2][x] = (int)(imageColors[i+k-margin][j+l-margin].getBlue()*255);
                        x++;
                    }
                }
                // sortowanie kolorów
                colors = sort(colors);

                x = colors[0].length;
                // ustalenie œredniej wartoœci pikseli 
                if(x%2 != 0){
                    med[0] = colors[0][(x-1)/2];
                    med[1] = colors[1][(x-1)/2];
                    med[2] = colors[2][(x-1)/2];
                } else {
                    med[0] = (colors[0][x/2] + colors[0][(x/2)-1]) / 2;
                    med[1] = (colors[1][x/2] + colors[1][(x/2)-1]) / 2;
                    med[2] = (colors[2][x/2] + colors[2][(x/2)-1]) / 2;
                }

                // stworzenie nowego koloru i ustawienie nowej wartoœci piksela
                Color c = new Color(med[0]/255.0, med[1]/255.0, med[2]/255.0, 1);
                pw.setColor(i, j, c);
            }
        }
        return image;
    }
    
    // metoda do stworzenia obrazu czarno-bia³ego
    public WritableImage toGrayScale() {
    	int r, g, b;
    	// wczytywana jest wartoœæ ka¿dego piksela 
    	for(int i=0; i<imageColors.length; i++) {
    		for(int j=0; j<imageColors[0].length; j++) {
    			r = (int)(imageColors[i][j].getRed()*255);
    			g = (int)(imageColors[i][j].getGreen()*255);
    			b = (int)(imageColors[i][j].getBlue()*255);
    			
    			// ustalenie wartoœci w skali szaroœci nastêpuje poprzez
    			// dodanie wartoœci RGB oraz podzielenie ich przez 3
    			float grey = (float) ((r+g+b)/3.0);
    			
    			// piksel w szarym kolorze jest wstawiany do obrazu
    			pw.setColor(i, j, new Color(grey/255.0, grey/255.0, grey/255.0, 1));
    		}
    	}
    	return image;
    }

    // metoda do sortowania tablicy z pikselami wykorzystywana przy filtrze medianowym
    private int[][] sort(int[][] arr){
    	// dla ka¿dego elementu w tablicy wywo³ywany jest Quicksort
        for (int i = 0; i < arr.length; i++) {
            quicksort(0, arr[i].length-1, arr[i]);
        }

        return arr;
    }
    
    // metoda s³u¿¹ca do dodawania wartoœci pikseli
    public WritableImage add(int a) {
    	int r, g, b;
    	
    	// ka¿demu pikselowi do ka¿dej sk³adowej dodawana jest wartoœæ a podana jako parametr metody
    	for(int i=0; i<imageColors.length; i++) {
    		for(int j=0; j<imageColors[0].length; j++) {
    			r=(int)(imageColors[i][j].getRed()*255)+a;
    			g=(int)(imageColors[i][j].getGreen()*255)+a;
    			b=(int)(imageColors[i][j].getBlue()*255)+a;
    			// sprawdzenie czy wartoœci nie wychodz¹ poza skalê
    			if(r>255) {
    				r=255;
    			}
    			if(g>255) {
    				g=255;
    			}
    			if(b>255) {
    				b=255;
    			}
    			
    			// wstawienie nowej wartoœci piksela do obrazu
    			pw.setColor(i, j, new Color(r/255.0, g/255.0, b/255.0, 1));
    		}
    	}
    	return image;
    }
    
    
    // metoda do odejmowania wartoœci od piksela
    public WritableImage minus(int a) {
    	int r, g, b;
    	// od ka¿dego piksela odejmowana jest wartoœæ podana w parametrze funkcji
    	for(int i=0; i<imageColors.length; i++) {
    		for(int j=0; j<imageColors[0].length; j++) {
    			r=(int)(imageColors[i][j].getRed()*255)-a;
    			g=(int)(imageColors[i][j].getGreen()*255)-a;
    			b=(int)(imageColors[i][j].getBlue()*255)-a;
    			
    			// sprawdzenie czy odjêcie wartoœci nie spowodowa³o 
    			// pojawienia siê ujemnych wartoœci
    			if(r<0) {
    				r=0;
    			}
    			if(g<0) {
    				g=0;
    			}
    			if(b<0) {
    				b=0;
    			}
    			
    			// wstawienie nowej wartoœci piksela do obrazu
    			pw.setColor(i, j, new Color(r/255.0, g/255.0, b/255.0, 1));
    		}
    	}
    	return image;
    }
    
    // metoda do mno¿enia 
    public WritableImage multiply(int a) {
    	int r, g, b;
    	// wartoœæ ka¿dego piksela jest mno¿ona przez wartoœæ podan¹ w parametrze metody
    	for(int i=0; i<imageColors.length; i++) {
    		for(int j=0; j<imageColors[0].length; j++) {
    			r=(int)(imageColors[i][j].getRed()*255)*a;
    			g=(int)(imageColors[i][j].getGreen()*255)*a;
    			b=(int)(imageColors[i][j].getBlue()*255)*a;
    			
    			// sprawdzenie czy odjêcie wartoœci nie spowodowa³o 
    			// pojawienia siê wartoœci wychodz¹cych poza zakres 0-255
    			if(r>255) {
    				r=255;
    			}
    			if(g>255) {
    				g=255;
    			}
    			if(b>255) {
    				b=255;
    			}
    			
    			// wstawienie nowej wartoœci piksela do obrazu
    			pw.setColor(i, j, new Color(r/255.0, g/255.0, b/255.0, 1));
    		}
    	}
    	return image;
    }
    
    // metoda do dzielenia wartoœci pikseli
    public WritableImage divide(int a) {
    	int r, g, b;
    	// dla ka¿dego piksela ustanawiana jest jego nowa wartoœæ
    	// poprzez podzielenie przez liczbê podan¹ w parametrze metody
    	for(int i=0; i<imageColors.length; i++) {
    		for(int j=0; j<imageColors[0].length; j++) {
    			r=(int)(imageColors[i][j].getRed()*255)/a;
    			g=(int)(imageColors[i][j].getGreen()*255)/a;
    			b=(int)(imageColors[i][j].getBlue()*255)/a;
    			
    			// sprawdzenie czy nowe wartoœci mieszcz¹ siê w odpowiednim przedziale
    			if(r<0) {
    				r=0;
    			}
    			if(g<0) {
    				g=0;
    			}
    			if(b<0) {
    				b=0;
    			}
    			
    			// wstawienie nowej wartoœci piksela do obrazu
    			pw.setColor(i, j, new Color(r/255.0, g/255.0, b/255.0, 1));
    		}
    	}
    	return image;
    }

    // implementacja algorytmu sortuj¹cego Quicksort
    private void quicksort(int low, int high, int[] numbers) {
        int i = low, j = high;
        int pivot = numbers[low + (high-low)/2];

        while (i <= j) {

            while (numbers[i] < pivot) {
                i++;
            }
            while (numbers[j] > pivot) {
                j--;
            }

            if (i <= j) {
                exchange(i, j, numbers);
                i++;
                j--;
            }
        }
        // rekurencyjne wywo³anie quicksortu
        if (low < j)
            quicksort(low, j, numbers);
        if (i < high)
            quicksort(i, high, numbers);
    }

    // metoda do zamiany miejscami dwóch liczb
    // na potrzeby Quicksortu
    private void exchange(int i, int j, int[] numbers) {
        int temp = numbers[i];
        numbers[i] = numbers[j];
        numbers[j] = temp;
    }

}
