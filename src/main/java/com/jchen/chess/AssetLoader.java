package com.jchen.chess;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

public class AssetLoader {
    private static final HashMap<String, BufferedImage> images = new HashMap<>();

    public static BufferedImage getImage(String name) {
        if (images.containsKey(name))
            return images.get(name);
        try {
            String path = AssetLoader.class.getClassLoader().getResource(name).getPath();
            return images.put(name, ImageIO.read(new File(path)));
        } catch (Exception e) {
            e.printStackTrace();
            return new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        }
    }
}
