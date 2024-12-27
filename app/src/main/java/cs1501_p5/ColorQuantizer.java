package cs1501_p5;

import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class ColorQuantizer implements ColorQuantizer_Inter{
    private Pixel[][] pixelArray;
    private Pixel[][] newPixelArray;
    private ColorMapGenerator_Inter gen;

    // Constructors
    public ColorQuantizer(Pixel[][] pixelArray, ColorMapGenerator_Inter gen) {
        this.pixelArray = pixelArray;
        this.gen = gen;

        if (pixelArray == null || pixelArray.length == 0 || pixelArray[0].length == 0)
            newPixelArray = pixelArray;
        else
            newPixelArray = new Pixel[pixelArray.length][pixelArray[0].length];
    }
    
    public ColorQuantizer(String bmpFilename, ColorMapGenerator_Inter gen) {
        this.gen = gen;
        try {
            File file = new File(bmpFilename);
            BufferedImage image = ImageIO.read(file);
            pixelArray = Util.convertBitmapToPixelMatrix(image);
            newPixelArray = new Pixel[pixelArray.length][pixelArray[0].length];
        } catch (Exception e) {
            System.out.println("Cannot read image");
        }
    }

    @Override
    public Pixel[][] quantizeTo2DArray(int numColors) {
        if (pixelArray == null || pixelArray.length == 0 || pixelArray[0].length == 0)
            return pixelArray;
        if (numColors == 0) return pixelArray;

        Pixel[] palette = gen.generateColorPalette(pixelArray, numColors);
        Map<Pixel, Pixel> colorMap = gen.generateColorMap(pixelArray, palette);
        // Construct the new pixel map
        for (int x = 0; x < pixelArray.length; x++)
            for (int y = 0; y < pixelArray[0].length; y++)
                newPixelArray[x][y] = colorMap.get(pixelArray[x][y]);

        return newPixelArray;
    }

    @Override
    public void quantizeToBMP(String fileName, int numColors) {
        newPixelArray = quantizeTo2DArray(numColors);
        Util.savePixelMatrixToBitmap(fileName, newPixelArray);
    }
    
    // Test
    public static void main(String[] args) {
        Pixel[][] stripedArr = new Pixel[][]{
            {new Pixel(5, 5, 5), new Pixel(5, 5, 5), new Pixel(5, 5, 5)},
            {new Pixel(50, 50, 50), new Pixel(50, 50, 50), new Pixel(50, 50, 50)},
            {new Pixel(100, 100, 100), new Pixel(100, 100, 100), new Pixel(100, 100, 100)},
            {new Pixel(150, 150, 150), new Pixel(150, 150, 150), new Pixel(150, 150, 150)},
            {new Pixel(200, 200, 200), new Pixel(200, 200, 200), new Pixel(200, 200, 200)},
            {new Pixel(250, 250, 250), new Pixel(250, 250, 250), new Pixel(250, 250, 250)}
        };
        DistanceMetric_Inter dm = new SquaredEuclideanMetric();
        ColorMapGenerator_Inter generator = new ClusteringMapGenerator(dm);
        ColorQuantizer cq = new ColorQuantizer(stripedArr, generator);

        // Check for 1 color
        // Pixel[][] result = cq.quantizeTo2DArray(1);
        // Pixel single_expected = new Pixel(125, 125, 125);

        // if (stripedArr.length != result.length) System.out.println("Incorrect number of rows in quantized pixels");
        // if (stripedArr[0].length != result[0].length) System.out.println("Incorrect number of columns in quantized pixels");
        // for (int row = 0; row < stripedArr.length; row++) {
        //     for (int col = 0; col < stripedArr[0].length; col++) {
        //         if (!single_expected.equals(result[row][col])) System.out.println("Incorrectly quantized pixel");
        //     }
        // }

        // Check for 4 colors
        Pixel[][] result = cq.quantizeTo2DArray(0);
        Pixel[] expectedMappings = new Pixel[]{
      			new Pixel(27, 27, 27),
      			new Pixel(125, 125, 125),
      			new Pixel(200, 200, 200),
      			new Pixel(250, 250, 250)
    		};

    	int expected = 0;
        for (int row = 0; row < stripedArr.length; row++) {
            for (int col = 0; col < stripedArr[0].length; col++) {
        				switch (row) {
          					case 0:
          					case 1:
            						expected = 0;
            						break;
          					case 2:
          					case 3:
            						expected = 1;
            						break;
          					case 4:
            						expected = 2;
            						break;
          					default:
            						expected = 3;
        				}
                if (!expectedMappings[expected].equals(result[row][col])) System.out.println("A pixel was mapped to the incorrect reduced color in Clustering");
            }
        }


        System.out.println("Successful");
    }
}
