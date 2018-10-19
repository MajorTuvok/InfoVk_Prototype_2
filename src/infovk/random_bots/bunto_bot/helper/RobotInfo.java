package infovk.random_bots.bunto_bot.helper;

import robocode.Robot;

public final class RobotInfo {
    private final double mHeading;
    private final Vector2D mPos;
    private final double mVelocity;
    private final String mName;

    private RobotInfo(String name, double x, double y, double heading, double velocity) {
        this(new Vector2D(x, y), name, heading, velocity);
    }

    private RobotInfo(Vector2D pos, String name, double heading, double velocity) {
        mPos = pos;
        mHeading = heading;
        mVelocity = velocity;
        mName = name;
    }

    public static RobotInfo getInstance(Robot robot) {
        return new RobotInfo(robot.getName(), robot.getX(), robot.getY(), robot.getHeading(), robot.getVelocity());
    }

    public static RobotInfo getInstance(Vector2D pos, String name, double heading, double velocity) {
        return new RobotInfo(pos, name, heading, velocity);
    }

    public static RobotInfo getInstance(String name, double x, double y, double heading, double velocity) {
        return new RobotInfo(name, x, y, heading, velocity);
    }

    public static RobotInfo copyOf(RobotInfo other) {
        return new RobotInfo(other.getPos(), other.getName(), other.getHeading(), other.getVelocity());
    }

    public double getX() {
        return mPos.getX();
    }

    public double getY() {
        return mPos.getY();
    }

    public Vector2D getPos() {
        return mPos;
    }

    public double getHeading() {
        return mHeading;
    }

    public double getVelocity() {
        return mVelocity;
    }

    public String getName() {
        return mName;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(getHeading());
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (getPos() != null ? getPos().hashCode() : 0);
        temp = Double.doubleToLongBits(getVelocity());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RobotInfo)) return false;

        RobotInfo robotInfo = (RobotInfo) o;

        if (Double.compare(robotInfo.getHeading(), getHeading()) != 0) return false;
        if (Double.compare(robotInfo.getVelocity(), getVelocity()) != 0) return false;
        if (getPos() != null ? !getPos().equals(robotInfo.getPos()) : robotInfo.getPos() != null) return false;
        return getName() != null ? getName().equals(robotInfo.getName()) : robotInfo.getName() == null;
    }

    @Override
    public String toString() {
        return "RobotInfo{" +
                "heading=" + mHeading +
                ", pos=" + mPos +
                ", velocity=" + mVelocity +
                ", name='" + mName + '\'' +
                '}';
    }
}
