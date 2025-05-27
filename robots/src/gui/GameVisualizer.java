package gui;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class GameVisualizer extends JPanel {
    private final Timer m_timer = initTimer();
    private final RobotModel robotModel;
    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;
    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.001;

    public GameVisualizer() {
        robotModel = new RobotModel(100, 100, 0);

        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onModelUpdateEvent();
            }
        }, 0, 10);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setTargetPosition(e.getPoint());
                repaint();
            }
        });
        setDoubleBuffered(true);
    }

    private static Timer initTimer() {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    public RobotModel getRobotModel() {
        return robotModel;
    }

    protected void setTargetPosition(Point p) {
        m_targetPositionX = p.x;
        m_targetPositionY = p.y;
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    protected void onModelUpdateEvent() {
        double distance = distance(m_targetPositionX, m_targetPositionY, robotModel.getPositionX(), robotModel.getPositionY());
        if (distance < 0.5) {
            return;
        }
        double velocity = maxVelocity;
        double angleToTarget = angleTo(robotModel.getPositionX(), robotModel.getPositionY(), m_targetPositionX, m_targetPositionY);
        double angularVelocity = 0;
        if (angleToTarget > robotModel.getDirection()) {
            angularVelocity = maxAngularVelocity;
        }
        if (angleToTarget < robotModel.getDirection()) {
            angularVelocity = -maxAngularVelocity;
        }

        moveRobot(velocity, angularVelocity, 10);
    }

    private static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);
        double newX = robotModel.getPositionX() + velocity / angularVelocity *
                (Math.sin(robotModel.getDirection() + angularVelocity * duration) -
                        Math.sin(robotModel.getDirection()));
        if (!Double.isFinite(newX)) {
            newX = robotModel.getPositionX() + velocity * duration * Math.cos(robotModel.getDirection());
        }
        double newY = robotModel.getPositionY() - velocity / angularVelocity *
                (Math.cos(robotModel.getDirection() + angularVelocity * duration) -
                        Math.cos(robotModel.getDirection()));
        if (!Double.isFinite(newY)) {
            newY = robotModel.getPositionY() + velocity * duration * Math.sin(robotModel.getDirection());
        }
        // Проверка границ и изменение направления
        if (newX < 0 || newX > getWidth()) {
            robotModel.setDirection(Math.PI - robotModel.getDirection()); // Разворот по оси X
        }
        if (newY < 0 || newY > getHeight()) {
            robotModel.setDirection(2 * Math.PI - robotModel.getDirection()); // Разворот по оси Y
        }
        // Корректировка позиции, если вышли за границы
        if (newX < 0) newX = 0;
        if (newX > getWidth()) newX = getWidth();
        if (newY < 0) newY = 0;
        if (newY > getHeight()) newY = getHeight();
        robotModel.setPosition(newX, newY);
        double newDirection = asNormalizedRadians(robotModel.getDirection() + angularVelocity * duration);
        robotModel.setDirection(newDirection);
    }

    private static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    private static int round(double value) {
        return (int) (value + 0.5);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, round(robotModel.getPositionX()), round(robotModel.getPositionY()), robotModel.getDirection());
        drawTarget(g2d, m_targetPositionX, m_targetPositionY);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        int robotCenterX = round(robotModel.getPositionX());
        int robotCenterY = round(robotModel.getPositionY());
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(Color.PINK);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.RED);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}
