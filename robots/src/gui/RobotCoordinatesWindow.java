package gui;
import javax.swing.*;
import java.awt.*;
import java.util.Observer;
import java.util.Observable;

public class RobotCoordinatesWindow extends JInternalFrame implements Observer {
    private final RobotModel robotModel;
    private final JTextArea coordinatesTextArea;

    public RobotCoordinatesWindow(RobotModel robotModel) {
        super("Координаты робота", true, true, true, true);
        this.robotModel = robotModel;
        this.robotModel.addObserver(this);

        coordinatesTextArea = new JTextArea();
        coordinatesTextArea.setEditable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(coordinatesTextArea), BorderLayout.CENTER);
        getContentPane().add(panel);

        pack();
        setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        SwingUtilities.invokeLater(() -> {
            coordinatesTextArea.setText(String.format("X: %.2f, Y: %.2f, Direction: %.2f",
                    robotModel.getPositionX(), robotModel.getPositionY(), robotModel.getDirection()));
        });
    }
}
