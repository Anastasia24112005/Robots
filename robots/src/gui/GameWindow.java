package gui;
import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class GameWindow extends JInternalFrame implements WindowState {
    private final GameVisualizer m_visualizer;
    private final Preferences prefs = Preferences.userNodeForPackage(GameWindow.class);

    public GameWindow() {
        super("Игровое поле", true, true, true, true);
        setName("GameWindow");
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        load();
    }

    public RobotModel getRobotModel() {
        return m_visualizer.getRobotModel();
    }

    @Override
    public void save() {
        Rectangle bounds = getBounds();
        prefs.putInt("GameWindow_X", bounds.x);
        prefs.putInt("GameWindow_Y", bounds.y);
        prefs.putInt("GameWindow_WIDTH", bounds.width);
        prefs.putInt("GameWindow_HEIGHT", bounds.height);
    }

    @Override
    public void load() {
        int x = prefs.getInt("GameWindow_X", 10);
        int y = prefs.getInt("GameWindow_Y", 10);
        int width = prefs.getInt("GameWindow_WIDTH", 400);
        int height = prefs.getInt("GameWindow_HEIGHT", 400);
        setBounds(x, y, width, height);
    }
}
