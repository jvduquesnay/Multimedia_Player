import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class VideoPlayer {

    // default state - video playing
    static int isPause = 1;

    public static void main(String[] args) {
        File file = new File("./InputVideo.rgb"); // name of the RGB video file
        int width = 480; // width of the video frames
        int height = 270; // height of the video frames

        int frameWidth = 860; // window width
        int frameHeight = 540; // window height

        int fps = 30; // frames per second of the video
        int numFrames = 6276; // number of frames in the video

        // create the JFrame and JLabel to display the video
        JFrame frame = new JFrame("Video Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setSize(new Dimension(frameWidth, frameHeight));
        frame.setVisible(true);

        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(width, height));
        frame.add(label);

        // left container displaying video structure
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea(20, 20);
        JScrollPane scrollableTextArea = new JScrollPane(textArea);

        scrollableTextArea.setBounds(0, 0, frameWidth / 3, frameHeight);
        scrollableTextArea.setPreferredSize(new Dimension(frameWidth / 3, frameHeight));

        scrollableTextArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


        // right container displaying video playing
        JPanel rightPanel = new JPanel();
        rightPanel.setBounds(0,0, 2 * frameWidth / 3, frameHeight);
        rightPanel.setPreferredSize(new Dimension(2 * frameWidth / 3, frameHeight));
        rightPanel.setLayout(new BorderLayout());

        JLabel labImage = new JLabel();
        labImage.setBounds(0,0, frameWidth / 3, 2 * frameHeight/3);
        labImage.setPreferredSize(new Dimension(frameWidth / 3, 2 * frameHeight/3));
        labImage.setOpaque(true);

        // bottom container displaying interactive buttons to play, pause, stop
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());

        JButton jb1 = new JButton("PLAY");
        JButton jb2 = new JButton("PAUSE");
        JButton jb3 = new JButton("STOP");

        // action listener for play event
        // anonymous action listener replaced with lambda
        jb1.addActionListener(e -> isPause = 1);

        // action listener for pause event
        jb2.addActionListener(e -> isPause = 0);

        // action listener for stop event
        jb3.addActionListener(e -> isPause = -1);

        bottomPanel.add(jb1);
        bottomPanel.add(jb2);
        bottomPanel.add(jb3);

        // right container style
        rightPanel.add(labImage, BorderLayout.NORTH);
        rightPanel.add(bottomPanel, BorderLayout.CENTER);
        rightPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        rightPanel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        // left container style
        leftPanel.add(scrollableTextArea, BorderLayout.WEST);
        leftPanel.add(rightPanel, BorderLayout.EAST);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(leftPanel);
        frame.setVisible(true);



        // read the video file and display each frame
        int i = 0;
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            FileChannel channel = raf.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(width * height * 3);

            do {

                try {
                    Thread.sleep(1000 / fps);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // if video is stopped repaint frame to empty and re-position video start
                if (isPause == -1) {

                    labImage.setIcon(null);
                    frame.validate();
                    frame.repaint();

                    try {
                        channel.position(0);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    continue;
                }

                // if video is paused repaint frame to empty and re-position video start
                if (isPause == 0)
                    continue;


                buffer.clear();
                try {
                    channel.read(buffer);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                buffer.rewind();
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int r = buffer.get() & 0xff;
                        int g = buffer.get() & 0xff;
                        int b = buffer.get() & 0xff;
                        int rgb = (r << 16) | (g << 8) | b;
                        image.setRGB(x, y, rgb);
                    }
                }
                labImage.setIcon(new ImageIcon(image));
                frame.validate();
                frame.repaint();

            } while (i < numFrames);
            /*if (isPause == -1 ) {
                try {
                    channel.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    raf.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
