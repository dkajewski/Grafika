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

        for (int j = 0; j < imageColors.length; j++) {
            for (int k = 0; k < imageColors[0].length; k++) {
                Color c = pr.getColor(j, k);

                imageColors[j][k] = c;
            }
        }
    }

    public WritableImage Convolution(int[][] kernel) {
        int w = (int)image.getWidth(), h = (int)image.getHeight();
        int sumR = 0, sumG = 0, sumB = 0;
        int margin = (kernel.length-1)/2;

        for (int i = margin; i < w - margin; i++) {
            for (int j = margin; j < h - margin; j++) {

                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        sumR += (imageColors[i+k-margin][j+l-margin].getRed()*255) * kernel[k][l];
                        sumG += (imageColors[i+k-margin][j+l-margin].getGreen()*255) * kernel[k][l];
                        sumB += (imageColors[i+k-margin][j+l-margin].getBlue()*255) * kernel[k][l];
                    }
                }

                sumR = Math.min(Math.max(sumR,0),255);
                sumB = Math.min(Math.max(sumB,0),255);
                sumG = Math.min(Math.max(sumR,0),255);

                Color c = new Color(sumR/255.0, sumG/255.0, sumB/255.0, 1);

                sumR = sumG = sumB = 0;

                pw.setColor(i, j, c);

            }
        }
        return image;
    }

    public WritableImage Mediana(int size){
        int w = (int)image.getWidth(), h = (int)image.getHeight();
        int med[] = new int[3];
        int[][] colors = new int[3][size*size];
        int margin = (size-1)/2;

        for (int i = margin; i < w - margin; i++) {
            for (int j = margin; j < h - margin; j++) {
                int x = 0;
                for (int k = 0; k < size; k++) {
                    for (int l = 0; l < size; l++) {
                        colors[0][x] = (int)(imageColors[i+k-margin][j+l-margin].getRed()*255);
                        colors[1][x] = (int)(imageColors[i+k-margin][j+l-margin].getGreen()*255);
                        colors[2][x] = (int)(imageColors[i+k-margin][j+l-margin].getBlue()*255);
                        x++;
                    }
                }
                colors = sort(colors);

                x = colors[0].length;
                if(x%2 != 0){
                    med[0] = colors[0][(x-1)/2];
                    med[1] = colors[1][(x-1)/2];
                    med[2] = colors[2][(x-1)/2];
                } else {
                    med[0] = (colors[0][x/2] + colors[0][(x/2)-1]) / 2;
                    med[1] = (colors[1][x/2] + colors[1][(x/2)-1]) / 2;
                    med[2] = (colors[2][x/2] + colors[2][(x/2)-1]) / 2;
                }

                Color c = new Color(med[0]/255.0, med[1]/255.0, med[2]/255.0, 1);
                pw.setColor(i, j, c);
            }

        }
        return image;
    }

    private int[][] sort(int[][] arr){

        for (int i = 0; i < arr.length; i++) {
            quicksort(0, arr[i].length-1, arr[i]);
        }

        return arr;
    }

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
        // Recursion
        if (low < j)
            quicksort(low, j, numbers);
        if (i < high)
            quicksort(i, high, numbers);
    }

    private void exchange(int i, int j, int[] numbers) {
        int temp = numbers[i];
        numbers[i] = numbers[j];
        numbers[j] = temp;
    }

}
