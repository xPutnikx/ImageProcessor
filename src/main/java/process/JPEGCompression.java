package process; /**
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
    /*
     * Application starting point, open an image and save it in JPEG with a
     * compression factor.
     */
//    public static void main(String[] args) throws IOException {
//        jpegCompression(new File("C:\\Users\\Public\\Pictures\\Sample Pictures\\Tulips.jpg"));
//    }
    public static File jpegCompression(File file) throws IOException {
        BufferedImage i = ImageIO.read(file);
//        showImage("Original Image", i);
        // Show results with different compression ratio.
        compressAndShow(i, 0.5f,file.getName());
        return file;
    }

    public static void compressAndShow(BufferedImage image, float quality,String name) throws IOException {
        // Get a ImageWriter for jpeg format.
        Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("jpeg");
        if (!writers.hasNext()) throw new IllegalStateException("No writers found");
        ImageWriter writer = (ImageWriter) writers.next();
        // Create the ImageWriteParam to compress the image.
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);
        // The output will be a ByteArrayOutputStream (in memory)
        ByteArrayOutputStream bos = new ByteArrayOutputStream(32768);
        ImageOutputStream ios = ImageIO.createImageOutputStream(bos);
        writer.setOutput(ios);
        writer.write(null, new IIOImage(image, null, null), param);
        ios.flush(); // otherwise the buffer size will be zero!
        // From the ByteArrayOutputStream create a RenderedImage.
        ByteArrayInputStream in = new ByteArrayInputStream(bos.toByteArray());
        RenderedImage out = ImageIO.read(in);
        int size = bos.toByteArray().length;
        showImage("Compressed to " + quality + ": " + size + " bytes", out);
        FileOutputStream fileOutputStream = new FileOutputStream("compressed"+name);
        fileOutputStream.write(bos.toByteArray());
        // Uncomment code below to save the compressed files.
//    File file = new File("compressed."+quality+".jpeg");
//    FileImageOutputStream output = new FileImageOutputStream(file);
//    writer.setOutput(output); writer.write(null, new IIOImage(image, null,null), param);
    }

    /*
     * This method just create a JFrame to display the image. Closing the window
     * will close the whole application.
     */
    private static void showImage(String title, RenderedImage image) {
        JFrame f = new JFrame(title);
        f.getContentPane().add(new DisplayJAI(image));
        f.pack();
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

}
