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

        // wczytanie warto�ci pikseli w obrazie
        for (int j = 0; j < imageColors.length; j++) {
            for (int k = 0; k < imageColors[0].length; k++) {
                Color c = pr.getColor(j, k);

                imageColors[j][k] = c;
            }
        }
    }

    // metoda nak�adaj�ca filtr
    // w parametrze podawana jest tablica z mask�
    public WritableImage Convolution(int[][] kernel) {
        int w = (int)image.getWidth(), h = (int)image.getHeight();
        int sumR = 0, sumG = 0, sumB = 0;
        int margin = (kernel.length-1)/2;

        for (int i = margin; i < w - margin; i++) {
            for (int j = margin; j < h - margin; j++) {

            	// mno�enie warto�ci poszczeg�lnych kolor�w
            	// przez warto�ci z maski
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        sumR += (imageColors[i+k-margin][j+l-margin].getRed()*255) * kernel[k][l];
                        sumG += (imageColors[i+k-margin][j+l-margin].getGreen()*255) * kernel[k][l];
                        sumB += (imageColors[i+k-margin][j+l-margin].getBlue()*255) * kernel[k][l];
                    }
                }
                
                // zabezpieczenie przed posiadaniem warto�ci mniejszych od 0 i wi�kszych od 255
                sumR = Math.min(Math.max(sumR,0),255);
                sumB = Math.min(Math.max(sumB,0),255);
                sumG = Math.min(Math.max(sumR,0),255);

                // stworzenie nowego koloru
                Color c = new Color(sumR/255.0, sumG/255.0, sumB/255.0, 1);

                //ustawienie warto�ci pocz�tkowych w zmiennych przechowuj�cych nowy kolor
                sumR = sumG = sumB = 0;

                // ustawienie nowej warto�ci 
                pw.setColor(i, j, c);

            }
        }
        return image;
    }

    // metoda nak�adaj�ca filtr medianowy
    public WritableImage Mediana(int size){
        int w = (int)image.getWidth(), h = (int)image.getHeight();
        int med[] = new int[3];
        int[][] colors = new int[3][size*size];
        int margin = (size-1)/2;

        for (int i = margin; i < w - margin; i++) {
            for (int j = margin; j < h - margin; j++) {
                int x = 0;
                // wczytanie warto�ci pikseli 
                for (int k = 0; k < size; k++) {
                    for (int l = 0; l < size; l++) {
                        colors[0][x] = (int)(imageColors[i+k-margin][j+l-margin].getRed()*255);
                        colors[1][x] = (int)(imageColors[i+k-margin][j+l-margin].getGreen()*255);
                        colors[2][x] = (int)(imageColors[i+k-margin][j+l-margin].getBlue()*255);
                        x++;
                    }
                }
                // sortowanie kolor�w
                colors = sort(colors);

                x = colors[0].length;
                // ustalenie �redniej warto�ci pikseli 
                if(x%2 != 0){
                    med[0] = colors[0][(x-1)/2];
                    med[1] = colors[1][(x-1)/2];
                    med[2] = colors[2][(x-1)/2];
                } else {
                    med[0] = (colors[0][x/2] + colors[0][(x/2)-1]) / 2;
                    med[1] = (colors[1][x/2] + colors[1][(x/2)-1]) / 2;
                    med[2] = (colors[2][x/2] + colors[2][(x/2)-1]) / 2;
                }

                // stworzenie nowego koloru i ustawienie nowej warto�ci piksela
                Color c = new Color(med[0]/255.0, med[1]/255.0, med[2]/255.0, 1);
                pw.setColor(i, j, c);
            }
        }
        return image;
    }
    
    // metoda do stworzenia obrazu czarno-bia�ego
    public WritableImage toGrayScale() {
    	int r, g, b;
    	// wczytywana jest warto�� ka�dego piksela 
    	for(int i=0; i<imageColors.length; i++) {
    		for(int j=0; j<imageColors[0].length; j++) {
    			r = (int)(imageColors[i][j].getRed()*255);
    			g = (int)(imageColors[i][j].getGreen()*255);
    			b = (int)(imageColors[i][j].getBlue()*255);
    			
    			// ustalenie warto�ci w skali szaro�ci nast�puje poprzez
    			// dodanie warto�ci RGB oraz podzielenie ich przez 3
    			float grey = (float) ((r+g+b)/3.0);
    			
    			// piksel w szarym kolorze jest wstawiany do obrazu
    			pw.setColor(i, j, new Color(grey/255.0, grey/255.0, grey/255.0, 1));
    		}
    	}
    	return image;
    }

    // metoda do sortowania tablicy z pikselami wykorzystywana przy filtrze medianowym
    private int[][] sort(int[][] arr){
    	// dla ka�dego elementu w tablicy wywo�ywany jest Quicksort
        for (int i = 0; i < arr.length; i++) {
            quicksort(0, arr[i].length-1, arr[i]);
        }

        return arr;
    }
    
    // metoda s�u��ca do dodawania warto�ci pikseli
    public WritableImage add(int a) {
    	int r, g, b;
    	
    	// ka�demu pikselowi do ka�dej sk�adowej dodawana jest warto�� a podana jako parametr metody
    	for(int i=0; i<imageColors.length; i++) {
    		for(int j=0; j<imageColors[0].length; j++) {
    			r=(int)(imageColors[i][j].getRed()*255)+a;
    			g=(int)(imageColors[i][j].getGreen()*255)+a;
    			b=(int)(imageColors[i][j].getBlue()*255)+a;
    			// sprawdzenie czy warto�ci nie wychodz� poza skal�
    			if(r>255) {
    				r=255;
    			}
    			if(g>255) {
    				g=255;
    			}
    			if(b>255) {
    				b=255;
    			}
    			
    			// wstawienie nowej warto�ci piksela do obrazu
    			pw.setColor(i, j, new Color(r/255.0, g/255.0, b/255.0, 1));
    		}
    	}
    	return image;
    }
    
    
    // metoda do odejmowania warto�ci od piksela
    public WritableImage minus(int a) {
    	int r, g, b;
    	// od ka�dego piksela odejmowana jest warto�� podana w parametrze funkcji
    	for(int i=0; i<imageColors.length; i++) {
    		for(int j=0; j<imageColors[0].length; j++) {
    			r=(int)(imageColors[i][j].getRed()*255)-a;
    			g=(int)(imageColors[i][j].getGreen()*255)-a;
    			b=(int)(imageColors[i][j].getBlue()*255)-a;
    			
    			// sprawdzenie czy odj�cie warto�ci nie spowodowa�o 
    			// pojawienia si� ujemnych warto�ci
    			if(r<0) {
    				r=0;
    			}
    			if(g<0) {
    				g=0;
    			}
    			if(b<0) {
    				b=0;
    			}
    			
    			// wstawienie nowej warto�ci piksela do obrazu
    			pw.setColor(i, j, new Color(r/255.0, g/255.0, b/255.0, 1));
    		}
    	}
    	return image;
    }
    
    // metoda do mno�enia 
    public WritableImage multiply(int a) {
    	int r, g, b;
    	// warto�� ka�dego piksela jest mno�ona przez warto�� podan� w parametrze metody
    	for(int i=0; i<imageColors.length; i++) {
    		for(int j=0; j<imageColors[0].length; j++) {
    			r=(int)(imageColors[i][j].getRed()*255)*a;
    			g=(int)(imageColors[i][j].getGreen()*255)*a;
    			b=(int)(imageColors[i][j].getBlue()*255)*a;
    			
    			// sprawdzenie czy odj�cie warto�ci nie spowodowa�o 
    			// pojawienia si� warto�ci wychodz�cych poza zakres 0-255
    			if(r>255) {
    				r=255;
    			}
    			if(g>255) {
    				g=255;
    			}
    			if(b>255) {
    				b=255;
    			}
    			
    			// wstawienie nowej warto�ci piksela do obrazu
    			pw.setColor(i, j, new Color(r/255.0, g/255.0, b/255.0, 1));
    		}
    	}
    	return image;
    }
    
    // metoda do dzielenia warto�ci pikseli
    public WritableImage divide(int a) {
    	int r, g, b;
    	// dla ka�dego piksela ustanawiana jest jego nowa warto��
    	// poprzez podzielenie przez liczb� podan� w parametrze metody
    	for(int i=0; i<imageColors.length; i++) {
    		for(int j=0; j<imageColors[0].length; j++) {
    			r=(int)(imageColors[i][j].getRed()*255)/a;
    			g=(int)(imageColors[i][j].getGreen()*255)/a;
    			b=(int)(imageColors[i][j].getBlue()*255)/a;
    			
    			// sprawdzenie czy nowe warto�ci mieszcz� si� w odpowiednim przedziale
    			if(r<0) {
    				r=0;
    			}
    			if(g<0) {
    				g=0;
    			}
    			if(b<0) {
    				b=0;
    			}
    			
    			// wstawienie nowej warto�ci piksela do obrazu
    			pw.setColor(i, j, new Color(r/255.0, g/255.0, b/255.0, 1));
    		}
    	}
    	return image;
    }

    // implementacja algorytmu sortuj�cego Quicksort
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
        // rekurencyjne wywo�anie quicksortu
        if (low < j)
            quicksort(low, j, numbers);
        if (i < high)
            quicksort(i, high, numbers);
    }

    // metoda do zamiany miejscami dw�ch liczb
    // na potrzeby Quicksortu
    private void exchange(int i, int j, int[] numbers) {
        int temp = numbers[i];
        numbers[i] = numbers[j];
        numbers[j] = temp;
    }

}
