package process;
/**
 * User: v.hudnitsky
 * Date: 21.02.13
 * Time: 18:38
 */
import com.sun.media.jai.widget.DisplayJAI;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.Iterator;
public class JPEGCompression {
    public static File jpegCompression(File file) throws IOException {
        BufferedImage i = ImageIO.read(file);
        compressAndShow(i, 0.5f,file.getName());
        return file;
    }

    public static void compressAndShow(BufferedImage image, float quality,String name) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("jpeg");
        if (!writers.hasNext()) throw new IllegalStateException("No writers found");
        ImageWriter writer = (ImageWriter) writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(32768);
        ImageOutputStream ios = ImageIO.createImageOutputStream(bos);
        writer.setOutput(ios);
        writer.write(null, new IIOImage(image, null, null), param);
        ios.flush();
        ByteArrayInputStream in = new ByteArrayInputStream(bos.toByteArray());
        RenderedImage out = ImageIO.read(in);
        int size = bos.toByteArray().length;
        showImage("Compressed to " + quality + ": " + size + " bytes", out);
        FileOutputStream fileOutputStream = new FileOutputStream("compressed"+name);
        fileOutputStream.write(bos.toByteArray());
    }

    private static void showImage(String title, RenderedImage image) {
        JFrame f = new JFrame(title);
        f.getContentPane().add(new DisplayJAI(image));
        f.pack();
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

}
