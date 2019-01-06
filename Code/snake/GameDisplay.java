package snake;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author steven
 */
public class GameDisplay {

    private JFrame frame;
    private GameState current;
    private int nrRows;
    private int width;
    private int height;
    private int nrPlayers;
    private int cellSize = 15;
    private Graphics2D bufferGraphics;
    private Graphics2D screenGraphics;
    BufferedImage bufferImage;
    BufferedImage screenImage;
    static Color[] colors = {new Color(50, 100, 200), new Color(200, 100, 50), new Color(200, 200, 50), new Color(100, 100, 50)};

    public GameDisplay(GameState state) throws Exception {
        width = state.getWidth() * cellSize;
        height = state.getHeight() * cellSize;
        nrRows = state.getHeight();
        nrPlayers = state.getNrPlayers();
        current=state;
        if (nrPlayers > colors.length) {
            throw new Exception("number of players not supported");
        }
        initFrame();
        updateState();
    }

    private void initFrame() {

        bufferImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        screenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        bufferGraphics = bufferImage.createGraphics();
        screenGraphics = screenImage.createGraphics();

        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        bufferGraphics.addRenderingHints(hints);

        ImageIcon icon = new ImageIcon(screenImage);
        JLabel draw = new JLabel(icon);
        frame = new JFrame();
        frame.setContentPane(draw);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Snake");
        frame.pack();
        frame.requestFocusInWindow();
        frame.setVisible(true);
    }

    public void updateState() {        
        for (int i = 0; i < current.getHeight(); i++) {
            for (int j = 0; j < current.getWidth(); j++) {
                bufferGraphics.setColor(new Color(0, 0, 0));
                bufferGraphics.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
            }
        }
        for (int i = 0; i < nrPlayers; i++) {
            drawPlayer(current.getPlayerX(i), current.getPlayerY(i), colors[i]);
        }
        drawTarget(current.getTargetX(),current.getTargetY());
        screenGraphics.drawImage(bufferImage, 0, 0, null);
        frame.repaint();

    }

    private void drawPlayer(List<Integer> x, List<Integer> y, Color col) {
        for (int i = 0; i < x.size(); i++) {
            bufferGraphics.setColor(col);
            bufferGraphics.fillRect(x.get(i) * cellSize, y.get(i) * cellSize, cellSize, cellSize);
        }
    }

    private void drawTarget(int x, int y) {        
            bufferGraphics.setColor(new Color(250,250,0));
            bufferGraphics.fillOval(x*cellSize, y*cellSize,cellSize,cellSize);
        
    }
    
    public void addKeyListener(KeyListener list) {
        frame.addKeyListener(list);
    }

}
