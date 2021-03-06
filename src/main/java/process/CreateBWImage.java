package process;
/**
 * User: v.hudnitsky
 * Date: 21.02.13
 * Time: 15:33
 */

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CreateBWImage {
    public static File createBWImage(File file) throws IOException {
        BufferedImage input = ImageIO.read(file);
        BufferedImage im = new BufferedImage(
                input.getWidth(), input.getHeight(),
                        BufferedImage.TYPE_BYTE_BINARY
                );
        Graphics2D g2d = im.createGraphics();
        g2d.drawImage(input, 0, 0, null);
        File tempFile = File.createTempFile("BWFile", "png");
        ImageIO.write(im, "PNG", tempFile);
        return tempFile;
    }
}
