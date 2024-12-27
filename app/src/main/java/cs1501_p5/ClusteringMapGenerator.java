package cs1501_p5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClusteringMapGenerator implements ColorMapGenerator_Inter{
    DistanceMetric_Inter metric;

    // Constructor
    public ClusteringMapGenerator(DistanceMetric_Inter metric) {
        this.metric = metric;
    }

    @Override
    public Pixel[] generateColorPalette(Pixel[][] pixelArray, int numColors) {
        Pixel[] palette = new Pixel[numColors];
        palette[0] = pixelArray[0][0];

        for (int i = 1; i < numColors; i++) {
            double maxDis = -1;  // max distance to nearest centroid of all pixels
            Pixel newCentroid = null;

            // Find the pixel with highest distance to it's nearest centroid
            for (Pixel[] pixelRow : pixelArray) {
                for (Pixel pixel : pixelRow) {
                    double minDis = Double.MAX_VALUE;   // distance to nearest centroid of a pixel
                    boolean isCentroid = false;
                    // Find the distance to the nearest centroid
                    for (Pixel centroid : palette) {
                        if (centroid == null) break;
                        if (pixel.equals(centroid)) {
                            isCentroid = true;
                            break;
                        }
                        double distance = metric.colorDistance(centroid, pixel);
                        if (distance < minDis) minDis = distance;
                    }

                    // If the pixel is already a centroid, skip
                    if (isCentroid) continue;

                    // If the pixel has the highest distance to the nearest centroid
                    if (minDis > maxDis) {
                        maxDis = minDis;
                        newCentroid = pixel;
                    }

                    // If the distances tie, choose the pixel with higher RGB value
                    if (minDis == maxDis && newCentroid!= null) {
                        int pixelColor = (pixel.getRed() << 16) | 
                                (pixel.getGreen() << 8) | 
                                (pixel.getBlue() << 0);
                        int centroidColor = (newCentroid.getRed() << 16) | 
                                (newCentroid.getGreen() << 8) | 
                                (newCentroid.getBlue() << 0);

                        if (pixelColor > centroidColor)
                            newCentroid = pixel;
                    }
                }
            }
            palette[i] = newCentroid; // Assign new centroid
        }

        return palette;
    }

    @Override
    public Map<Pixel, Pixel> generateColorMap(Pixel[][] pixelArray, Pixel[] initialColorPalette) {
        Map<Pixel, Pixel> colorMap = new HashMap<Pixel, Pixel>();
        HashMap<Pixel, ArrayList<Pixel>> clusters = new HashMap<Pixel, ArrayList<Pixel>>();

        // Add centroids to clusters data structure
        for (Pixel centroid : initialColorPalette) {
            clusters.put(centroid, new ArrayList<Pixel>());
        }

        // Keep doing clustering if new assignments occur
        boolean assignmentOccur = true;
        int maxIteration = 128;
        int count = 0;
        while (assignmentOccur && count < maxIteration) {
            assignmentOccur = false;
            // Go through each pixel to assign them to their centroids
            for (Pixel[] pixelRow : pixelArray) {
                for (Pixel pixel : pixelRow) {
                    double minDis = Double.MAX_VALUE;   // distance to nearest centroid of a pixel
                    Pixel newCentroid = null;
                    // Find the distance to the nearest centroid
                    for (Pixel centroid : initialColorPalette) {
                        if (centroid == null) break;

                        double distance = metric.colorDistance(centroid, pixel);

                        if (distance < minDis) {
                            minDis = distance;
                            newCentroid = centroid;
                        }
                    }
                    // Check for assignment to new centroid
                    Pixel currentCentroid = colorMap.get(pixel);
                    if (!newCentroid.equals(currentCentroid)) {
                        assignmentOccur = true;     // Assignment occurs

                        // Put the new centroid to the colormap
                        colorMap.put(pixel, newCentroid);
                        // Remove the pixel from the previous cluster
                        ArrayList<Pixel> oldCentroidPixels = clusters.get(currentCentroid);
                        if (oldCentroidPixels != null)
                            oldCentroidPixels.remove(pixel);

                        // Add the pixel to the new cluster
                        ArrayList<Pixel> newCentroidPixels = clusters.get(newCentroid);
                        if (newCentroidPixels != null)
                            newCentroidPixels.add(pixel);
                    }
                }
            }

            if (!assignmentOccur) break;

            // Centroids relocation
            for (int i = 0; i < initialColorPalette.length; i++) {
                ArrayList<Pixel> pixelList = clusters.get(initialColorPalette[i]);
                if (pixelList == null || pixelList.size() == 0) break;

                double averageRed = 0;
                double averageGreen = 0;
                double averageBlue = 0;

                for (Pixel pixel : pixelList) {
                    averageRed += pixel.getRed();
                    averageGreen += pixel.getGreen();
                    averageBlue += pixel.getBlue();
                }
                // Remove the old centroid location from the clusters
                clusters.remove(initialColorPalette[i]);
                // Calculate the new location
                averageRed = averageRed / pixelList.size();
                averageGreen = averageGreen / pixelList.size();
                averageBlue = averageBlue / pixelList.size();
                // Move the centroid to the new location
                initialColorPalette[i] = new Pixel((int)averageRed, (int)averageGreen, (int)averageBlue);
                // Reassign each pixel to the new centroid object
                for (Pixel pixel : pixelList)
                    colorMap.put(pixel, initialColorPalette[i]);
                // Add the new moved centroid to the clusters
                clusters.put(initialColorPalette[i], pixelList);
            }
            count++;
        }
        
        return colorMap;
    }
}
