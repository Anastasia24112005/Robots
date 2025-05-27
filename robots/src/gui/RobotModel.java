package gui;
import java.util.Observable;

public class RobotModel extends Observable {
    private double positionX;
    private double positionY;
    private double direction;

    public RobotModel(double positionX, double positionY, double direction) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.direction = direction;
    }

    public void setPosition(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
        setChanged();
        notifyObservers();
    }

    public void setDirection(double direction) {
        this.direction = direction;
        setChanged();
        notifyObservers();
    }

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public double getDirection() {
        return direction;
    }
}
