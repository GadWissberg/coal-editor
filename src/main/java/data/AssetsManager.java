package data;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class AssetsManager {
    private static HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();

    public static BufferedImage getImage(String location) throws IOException {
        BufferedImage bufferedImage = images.get(location);
        if (bufferedImage == null) {
            bufferedImage = ImageIO.read(new File(location));
            images.put(location, bufferedImage);
        }
        return bufferedImage;
    }

    public static BufferedImage getImageColorized(String location, Color color) throws IOException {
        String assetName = location + "_" + color.toString();
        BufferedImage bufferedImage = images.get(assetName);
        if (bufferedImage == null) {
            bufferedImage = loadAndColorizeImage(location, color, assetName);
        }
        return bufferedImage;
    }

    private static BufferedImage loadAndColorizeImage(String location, Color color, String assetName) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(location));
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        colorizeImage(color, bufferedImage, result);
        images.put(assetName, result);
        return result;
    }

    private static void colorizeImage(Color color, BufferedImage bufferedImage, BufferedImage result) {
        Graphics2D g = result.createGraphics();
        g.drawImage(bufferedImage, 0, 0, null);
        g.setComposite(AlphaComposite.SrcAtop);
        g.setColor(color);
        g.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        g.dispose();
    }
}
