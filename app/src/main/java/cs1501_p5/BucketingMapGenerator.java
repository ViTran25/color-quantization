package cs1501_p5;

import java.util.HashMap;
import java.util.Map;

public class BucketingMapGenerator implements ColorMapGenerator_Inter{
    final private double COLOR_24BIT = 16777216;

    @Override
    public Pixel[] generateColorPalette(Pixel[][] pixelArray, int numColors) {
        double bucketSize = COLOR_24BIT / numColors;
        Pixel[] palette = new Pixel[numColors];

        for (int i=0; i<numColors; i++) {
            int thisColor = (int) (i*bucketSize + bucketSize/2);
            int red = (thisColor >> 16);
            int green = (thisColor >> 8) & 0xFF;
            int blue = (thisColor) & 0xFF;
            palette[i] = new Pixel(red, green, blue);
        }
        return palette;
    }

    @Override
    public Map<Pixel, Pixel> generateColorMap(Pixel[][] pixelArray, Pixel[] initialColorPalette) {
        double bucketSize = COLOR_24BIT / initialColorPalette.length;
        Map<Pixel, Pixel> colorMap = new HashMap<Pixel, Pixel>();
        for (int i = 0; i < pixelArray.length; i++) {
            for (Pixel thisPixel : pixelArray[i]) {
                int thisColor = (thisPixel.getRed() << 16) | 
                                (thisPixel.getGreen() << 8) | 
                                (thisPixel.getBlue() << 0);
                
                int index = thisColor / (int)bucketSize;
                colorMap.put(thisPixel, initialColorPalette[index]);
            }
        }

        return colorMap;
    }

    public Pixel[][] quantizeTo2DArrayBucketing(Pixel[][] pixelArray, int numColors) {
        Pixel[][] newPixelArray = new Pixel[pixelArray.length][pixelArray[0].length];
        Pixel[] palette = generateColorPalette(pixelArray, numColors);
        Map<Pixel, Pixel> colorMap = generateColorMap(pixelArray, palette);
        // Construct the new pixel map
        for (int x = 0; x < pixelArray.length; x++)
            for (int y = 0; y < pixelArray[0].length; y++)
                newPixelArray[x][y] = colorMap.get(pixelArray[x][y]);

        return newPixelArray;
    }

    public static void main(String[] args) {
        Pixel[][] stripedArr = new Pixel[][]{
            {new Pixel(5, 5, 5), new Pixel(5, 5, 5), new Pixel(5, 5, 5)},
            {new Pixel(50, 50, 50), new Pixel(50, 50, 50), new Pixel(50, 50, 50)},
            {new Pixel(100, 100, 100), new Pixel(100, 100, 100), new Pixel(100, 100, 100)},
            {new Pixel(150, 150, 150), new Pixel(150, 150, 150), new Pixel(150, 150, 150)},
            {new Pixel(200, 200, 200), new Pixel(200, 200, 200), new Pixel(200, 200, 200)},
            {new Pixel(250, 250, 250), new Pixel(250, 250, 250), new Pixel(250, 250, 250)}
        };
        ColorMapGenerator_Inter generator = new BucketingMapGenerator();

        // Check with 7 colors that do not evenly divide 2^24
        Pixel[] result = generator.generateColorPalette(stripedArr, 7);
        Pixel[] expectedCT = new Pixel[]{
            new Pixel(18, 73, 36),
            new Pixel(54, 219, 109),
            new Pixel(91, 109, 182),
            new Pixel(128, 0, 0),
            new Pixel(164, 146, 73),
            new Pixel(201, 36, 146),
            new Pixel(237, 182, 219)
        };

        if (7 != result.length) System.out.println("Incorrect number of colors returned from Basic Bucketing generateColorPalette");
        for (int i = 0; i < expectedCT.length; i++) {
            if (!expectedCT[i].equals(result[i])) System.out.println("Incorrect color returned for palette of Bucketing");
        }
        System.out.println("Successful!");
    }
}