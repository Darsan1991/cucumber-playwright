package utils;

public class ColorUtils {
    
    public static boolean isRed(float r,float g,float b)
    {
        if (Math.max(Math.max(r,g),b) > 1) {
            r = r/255f;
            g = g/255f;
            b = b/255f;
        }
        return r>0.5f && g<0.5f && b<0.5f;
    }

    public static boolean isGreen(float r,float g,float b) {
        if (Math.max(Math.max(r,g),b) > 1) {
            r = r/255f;
            g = g/255f;
            b = b/255f;
        }
        return r<0.5f && g>0.5f && b<0.5f;
    }
}
