package cs1501_p5;

public class CircularHueMetric implements DistanceMetric_Inter{

    @Override
    public double colorDistance(Pixel p1, Pixel p2) {
        int largerNum = p1.getHue(), smallerNum = p2.getHue();

        if (p1.getHue() < p2.getHue()) {
            largerNum = p2.getHue();
            smallerNum = p1.getHue();
        }
        // Check if distance is over 180
        int distance = largerNum - smallerNum;

        if (distance > 180)
            distance = smallerNum + (360 - largerNum);
        
        return distance;
    }
    
    public static void main(String[] args) {
        Pixel p1 = new Pixel(200, 68, 0);
        Pixel p2 = new Pixel(215, 0, 70);
        DistanceMetric_Inter dm = new CircularHueMetric();

        if (40 != dm.colorDistance(p1, p2)) System.out.println("Incorrect distance from circular hue");
        if (40 != dm.colorDistance(p2, p1)) System.out.println("Flipping the order of pixels should not impact the result of circular hue");
        System.out.println("Successful!");
    }
}
