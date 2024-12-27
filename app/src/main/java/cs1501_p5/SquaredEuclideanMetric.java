package cs1501_p5;

public class SquaredEuclideanMetric implements DistanceMetric_Inter {

    @Override
    public double colorDistance(Pixel p1, Pixel p2) {
        int r1 = p1.getRed(), g1 = p1.getGreen(), b1 = p1.getBlue();
        int r2 = p2.getRed(), g2 = p2.getGreen(), b2 = p2.getBlue();

        int distance = (r1-r2)*(r1-r2) + (g1-g2)*(g1-g2) + (b1-b2)*(b1-b2);
        return distance;
    }

    public static void main(String[] args) {
        Pixel p1 = new Pixel(10, 15, 20);
        Pixel p2 = new Pixel(20, 25, 30);
        DistanceMetric_Inter dm = new SquaredEuclideanMetric();

        if (300 != dm.colorDistance(p1, p2)) System.out.println("Incorrect distance from squared Euclidean");
        if (300 != dm.colorDistance(p2, p1)) System.out.println("Flipping the order of pixels should not impact the result of squared Euclidean");
        System.out.println("Successful!");
    }
}
