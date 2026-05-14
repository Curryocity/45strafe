import java.util.ArrayList;
public class FourtyFivePointOne {
    public static final float[] SIN_TABLE = new float[65536];

    static {
        for (int i = 0; i < 65536; ++i) {
            SIN_TABLE[i] = (float) Math.sin(i * Math.PI * 2.0 / 65536.0);
        }
    }

    public static float sin(float value) {
        return SIN_TABLE[(int) (value * 10430.378F) & 65535];
    }

    public static float cos(float value) {
        return SIN_TABLE[(int) (value * 10430.378F + 16384.0F) & 65535];
    }
    
    public static double vec45(float yaw){
        float angle = yaw * (float)Math.PI / 180.0F;
        float cos = cos(angle);
        float sin = sin(angle);
        double zvec = (float)(Math.abs(cos) + Math.abs(sin));
        return zvec;
    }

    public static void main(String[] args) {
        final int n = Integer.MAX_VALUE;
        final float delta = 1f;
        final float start = 45f;
        final float end = 45f + 90.0f * n;
        ArrayList<Float> rangePairs = new ArrayList<>();

        double globalMax = 0;

        for(float angle = start; angle <= end; angle += 90f){
            float lb = angle - delta;
            float ub = angle + delta;

            double maxVec = 0;
            float l = lb;
            float r = ub;

            float ubRad =  ub * (float)Math.PI / 180.0F;
            if(ubRad * 10430.378F > Integer.MAX_VALUE){
                System.out.println("Ends with index overflow on " + lb + " deg to " + ub + " deg scan");
                break;
            } 

            for(float cur = lb; cur < ub; cur = Math.nextUp(cur)){
                double vec = vec45(cur);
                
                if(vec > maxVec){
                    rangePairs.clear();
                    rangePairs.add(cur);
                    maxVec = vec;
                    l = cur;
                } else if(vec == maxVec){
                    if(Float.isNaN(l)){
                        l = cur;
                        rangePairs.add(l);
                    }
                } else {
                    if (!Float.isNaN(l)) 
                        rangePairs.add(r);
                    l = Float.NaN;
                }
                r = cur;
            }

            if (!Float.isNaN(l)) {
                rangePairs.add(r);
            }

            
            if (maxVec >= globalMax){
                for(int i = 0; i < rangePairs.size(); i += 2){
                    System.out.println("Range: " + rangePairs.get(i) + " to " + rangePairs.get(i + 1));
                }
                System.out.println("Gives the maximum of abs(sin) + abs(cos): " + maxVec + "\n");
                globalMax = maxVec;
            }
        }
    }
}