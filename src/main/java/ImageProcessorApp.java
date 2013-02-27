/**
 * User: v.hudnitsky
 * Date: 21.02.13
 * Time: 16:20
 */

import com.jhlabs.image.AbstractBufferedImageOp;
import filters.*;
import process.CreateBWImage;
import process.CreateGreyLevel;
import process.JPEGCompression;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ImageProcessorApp extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private ImageProcessor dj;
    public static Container container;
    private JFileChooser fileChooser = null;
    private File file = null;
    private JSlider sliceSlider;
    private final GlowFilter glowFilter = new GlowFilter(3.0f);
    private final BoxBlurFilter boxBlurFilter = new BoxBlurFilter(20,20,3);
    private final GaussianFilter gaussianFilter = new GaussianFilter(7);
    private final LensBlurFilter lensBlurFilter = new LensBlurFilter(10,2,192,10,5);
    private final MotionBlurFilter motionBlurFilter = new MotionBlurFilter(5,8,6,6,7,false);
    private final SmartBlurFilter smartBlurFilter = new SmartBlurFilter(6,6,11);
    private final UnsharpFilter unsharpFilter = new UnsharpFilter(2,3);
    private final VariableBlurFilter variableBlurFilter = new VariableBlurFilter(5,5,8);
    private final MotionBlurOp motionBlurOp = new MotionBlurOp(5,5,8,4);

    public ImageProcessorApp() {
        setTitle("Image Processing");
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH) / 2, (kit.getScreenSize().height - HEIGHT) / 2);
        setExtendedState(MAXIMIZED_BOTH);
        setSize(WIDTH, HEIGHT);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        container = this.getContentPane();
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("Файл");
        final JMenu filterMenu = new JMenu("Filters");
        Action addFileAction = new AbstractAction("Загрузить") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showOpenDialog(ImageProcessorApp.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        file = fileChooser.getSelectedFile();
                        repaint(file.getPath());
                        filterMenu.enable();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Action ProcessBWAction = new AbstractAction("Black-white mode") {
            public void actionPerformed(ActionEvent event) {
                try {
                    file = CreateBWImage.createBWImage(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                repaint(file.getPath());
            }
        };
        Action ProcessGreyAction = new AbstractAction("Grey color mode") {
            public void actionPerformed(ActionEvent event) {
                try {
                    file = CreateGreyLevel.createGreyLevel(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                repaint(file.getPath());
            }
        };
        Action ProcessCompressAction = new AbstractAction("Compress Image") {
            public void actionPerformed(ActionEvent event) {
                try {
                    JPEGCompression.jpegCompression(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        List<AbstractBufferedImageOp> filterList = new ArrayList<AbstractBufferedImageOp>();
        filterList.add(glowFilter);
        filterList.add(boxBlurFilter);
        filterList.add(gaussianFilter);
        filterList.add(lensBlurFilter);
        filterList.add(motionBlurFilter);
        filterList.add(smartBlurFilter);
        filterList.add(unsharpFilter);
        filterList.add(variableBlurFilter);
        Iterator<AbstractBufferedImageOp> filterIterator = filterList.iterator();
        while(filterIterator.hasNext()){
            final AbstractBufferedImageOp filter = filterIterator.next();
            Action FilterAction = new AbstractAction(filter.toString()) {
                public void actionPerformed(ActionEvent event) {
                    try {
                        useFilter(filter);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            };
            filterMenu.add(FilterAction);
        }

        menuBar.add(fileMenu);
        menuBar.add(filterMenu);
        filterMenu.disable();
        fileMenu.add(addFileAction);
        fileMenu.add(ProcessBWAction);
        fileMenu.add(ProcessGreyAction);
        fileMenu.add(ProcessCompressAction);
    }

    public void useFilter(AbstractBufferedImageOp filter) throws IOException {

        BufferedImage image = ImageIO.read(file);
        BufferedImage imageResult = null;
        imageResult = filter.filter(image, imageResult);
        ioFile(imageResult);
    }
    private void ioFile(BufferedImage imageResult) throws IOException {
        File resultFile = new File("filtered" + file.getName());
        ImageIO.write(imageResult, "png", resultFile);
        repaint(resultFile.getPath());
    }

    private void repaint(String path){
        container.removeAll();
        container.repaint();
        build(path);
        validate();
        repaint();
    }

    public static void main(String[] args) {
        ImageProcessorApp frame = new ImageProcessorApp();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void build(String path) {
        PlanarImage image = JAI.create("fileload", path);
        dj = new ImageProcessor(image);
        Container contentPane = getContentPane();
        contentPane.add(new JScrollPane(dj), BorderLayout.CENTER);
    }
}
