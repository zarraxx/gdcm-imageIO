package com.oritsh.imageIO.test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: zarra
 * Date: 13-11-11
 * Time: 下午9:17
 * Copyright Shanghai Orient Rain Information Technology Co.,Ltd.
 */
public class TestHelper {
    public static String libName = "charls-native";

    public static String MR2_UNC_JLS = "mr2_unc.jls";

    public static String LENA8B_JLS = "lena8b.jls";

    public static InputStream lenaJLSInputStream(){
        return getResource(LENA8B_JLS);
    }

    public static InputStream getResource(String name){
        InputStream in = TestHelper.class.getClassLoader().getResourceAsStream(name);
        return in;
    }

    public static void saveImage(BufferedImage image ,String name) throws IOException {
        String filename = String.format("target/%s.png",name);
        ImageIO.write(image, "png", new File(filename));
    }
}
