/**
 * User: v.hudnitsky
 * Date: 21.02.13
 * Time: 16:20
 */

import com.jhlabs.image.AbstractBufferedImageOp;
import filters.*;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DisplayJAIWithPixelInfoApp extends JFrame implements MouseMotionListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private JLabel label; // Will contain information on the pixels of the image being displayed.
    private DisplayJAIWithPixelInfo dj; // An instance of the display component.
    public static Container container;
    private JFileChooser fileChooser = null;
    private File file = null;
    // A JSlider to select the slice to be displayed.
    private JSlider sliceSlider;
    private final GlowFilter glowFilter = new GlowFilter();
    private final BoxBlurFilter boxBlurFilter = new BoxBlurFilter();
    private final GaussianFilter gaussianFilter = new GaussianFilter();
    private final LensBlurFilter lensBlurFilter = new LensBlurFilter();
    private final MotionBlurFilter motionBlurFilter = new MotionBlurFilter();
    private final RaysFilter raysFilter = new RaysFilter();
    private final ShadowFilter shadowFilter = new ShadowFilter();
    private final SmartBlurFilter smartBlurFilter = new SmartBlurFilter();
    private final UnsharpFilter unsharpFilter = new UnsharpFilter();
    private final VariableBlurFilter variableBlurFilter = new VariableBlurFilter();

    /**
     * The constructor of the class, which sets the frame appearance and creates an
     * instance of the display component.
     */
    public DisplayJAIWithPixelInfoApp() {
        setTitle("Image Processing");
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH) / 2, (kit.getScreenSize().height - HEIGHT) / 2);
        setExtendedState(MAXIMIZED_BOTH);
        setSize(WIDTH, HEIGHT);
        // Get the JFrame's ContentPane.
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        // Create an instance of DisplayJAIWithPixelInfo and adds it to the content pane.

        // Add a text label with the image information.
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
                if (fileChooser.showOpenDialog(DisplayJAIWithPixelInfoApp.this) == JFileChooser.APPROVE_OPTION) {
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
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                repaint(file.getPath());
            }
        };
        Action ProcessGreyAction = new AbstractAction("Grey color mode") {
            public void actionPerformed(ActionEvent event) {
                try {
                    file = CreateGreyLevel.createGreyLevel(file);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                repaint(file.getPath());
            }
        };
        Action ProcessCompressAction = new AbstractAction("Compress Image") {
            public void actionPerformed(ActionEvent event) {
                try {
                    JPEGCompression.jpegCompression(file);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        };
        List<AbstractBufferedImageOp> filterList = new ArrayList<AbstractBufferedImageOp>();
        filterList.add(glowFilter);
        filterList.add(boxBlurFilter); //not
        filterList.add(gaussianFilter); //not
        filterList.add(lensBlurFilter); //yes
        filterList.add(motionBlurFilter); //not
        filterList.add(raysFilter);   //not
        filterList.add(shadowFilter);    //not
        filterList.add(smartBlurFilter); //not
        filterList.add(unsharpFilter);   //not
        filterList.add(variableBlurFilter); //not
        Iterator<AbstractBufferedImageOp> filterIterator = filterList.iterator();
        while(filterIterator.hasNext()){
            final AbstractBufferedImageOp filter = filterIterator.next();
            Action FilterAction = new AbstractAction(filter.toString()) {
                public void actionPerformed(ActionEvent event) {
                    try {
                        useFilter(filter);
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
    public void useBoxBlurFilter() throws IOException {
        BoxBlurFilter boxBlurFilter = new BoxBlurFilter();
        BufferedImage image = ImageIO.read(file);
        BufferedImage imageResult = null;
        imageResult = boxBlurFilter.filter(image, imageResult);
        ioFile(imageResult);
    }
    private void repaint(String path){
        container.removeAll();
        container.repaint();
        build(path);
        validate();
        repaint();
    }
    /**
     * This method will not do anything - it is here just to keep the
     * MouseMotionListener interface happy.
     */
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public static void main(String[] args) {
        DisplayJAIWithPixelInfoApp frame = new DisplayJAIWithPixelInfoApp();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void build(String path) {
        PlanarImage image = JAI.create("fileload", path);
        dj = new DisplayJAIWithPixelInfo(image);
        Container contentPane = getContentPane();
        contentPane.add(new JScrollPane(dj), BorderLayout.CENTER);
        dj.addMouseMotionListener(this);
    }
}
