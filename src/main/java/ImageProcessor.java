/**
 * User: v.hudnitsky
 * Date: 21.02.13
 * Time: 16:21
 */
import com.sun.media.jai.widget.DisplayJAI;

import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;
import java.awt.event.MouseEvent;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
public class ImageProcessor extends DisplayJAI
{
    private StringBuffer pixelInfo; // The pixel information (formatted in a StringBuffer).
    private double[] dpixel; // The pixel information as an array of doubles.
    private int[] ipixel; // The pixel information as an array of integers.
    private boolean isDoubleType; // Indicates which of the above arrays we will use.
    private RandomIter readIterator; // A RandomIter that allow us to get the data on a single pixel.
    private boolean isIndexed; // True if the image has a indexed color model.
    private short[][] lutData; // Will contain the look-up table data if isIndexed is true.
    protected int width, height; // The dimensions of the image

    static
    {
        System.setProperty("com.sun.media.jai.disableMediaLib", "true");
    }
    public ImageProcessor(RenderedImage image)
    {
        super(image);
        readIterator = RandomIterFactory.create(image, null);
        width = image.getWidth();
        height = image.getHeight();
        int dataType = image.getSampleModel().getDataType();
        switch (dataType)
        {
            case DataBuffer.TYPE_BYTE:
            case DataBuffer.TYPE_SHORT:
            case DataBuffer.TYPE_USHORT:
            case DataBuffer.TYPE_INT:
                isDoubleType = false;
                break;
            case DataBuffer.TYPE_FLOAT:
            case DataBuffer.TYPE_DOUBLE:
                isDoubleType = true;
                break;
        }
        if (isDoubleType)
            dpixel = new double[image.getSampleModel().getNumBands()];
        else
            ipixel = new int[image.getSampleModel().getNumBands()];
        isIndexed = (image.getColorModel() instanceof IndexColorModel);
        if (isIndexed)
        {
            IndexColorModel icm = (IndexColorModel) image.getColorModel();
            int mapSize = icm.getMapSize();
            byte[][] templutData = new byte[3][mapSize];
            icm.getReds(templutData[0]);
            icm.getGreens(templutData[1]);
            icm.getBlues(templutData[2]);
            lutData = new short[3][mapSize];
            for (int entry = 0; entry < mapSize; entry++)
            {
                lutData[0][entry] = templutData[0][entry] > 0 ?
                        templutData[0][entry] : (short) (templutData[0][entry] + 256);
                lutData[1][entry] = templutData[1][entry] > 0 ?
                        templutData[1][entry] : (short) (templutData[1][entry] + 256);
                lutData[2][entry] = templutData[2][entry] > 0 ?
                        templutData[2][entry] : (short) (templutData[2][entry] + 256);
            }
        }
        addMouseMotionListener(this);
        pixelInfo = new StringBuffer(50);
    }

    public void mouseMoved(MouseEvent me)
    {
        pixelInfo.setLength(0);
        int x = me.getX();
        int y = me.getY();
        if ((x >= width) || (y >= height))
        {
            pixelInfo.append("No data!");
            return;
        }
        if (isDoubleType)
        {
            pixelInfo.append("(floating-point data) ");
            readIterator.getPixel(me.getX(), me.getY(), dpixel);
            for (int b = 0; b < dpixel.length; b++)
                pixelInfo.append(dpixel[b]+",");
            pixelInfo = pixelInfo.deleteCharAt(pixelInfo.length() - 1);
        }
        else
        {
            if (isIndexed)
            {
                pixelInfo.append("(integer data with colormap) ");
                readIterator.getPixel(me.getX(), me.getY(), ipixel);
                pixelInfo.append("Index: "+ipixel[0]);
                pixelInfo.append(" RGB:"+lutData[0][ipixel[0]]+","+lutData[1][ipixel[0]]+","+
                        lutData[2][ipixel[0]]);
            }
            else
            {
                pixelInfo.append("(integer data) ");
                readIterator.getPixel(me.getX(), me.getY(), ipixel);
                for (int b = 0; b < ipixel.length; b++)
                    pixelInfo.append(ipixel[b] + ",");
                pixelInfo = pixelInfo.deleteCharAt(pixelInfo.length() - 1);
            }
        }
    }

    public String getPixelInfo()
    {
        return pixelInfo.toString();
    }

}
