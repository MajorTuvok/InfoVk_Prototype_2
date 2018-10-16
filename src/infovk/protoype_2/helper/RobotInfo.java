package infovk.protoype_2.helper;

import robocode.Robot;

public final class RobotInfo {
    private final double mHeading;
    private final Point mPos;
    private final double mVelocity;

    private RobotInfo(double x, double y, double heading, double velocity) {
        this(new Point(x, y), heading, velocity);
    }

    private RobotInfo(Point pos, double heading, double velocity) {
        mPos = pos;
        mHeading = heading;
        mVelocity = velocity;
    }

    public static RobotInfo getInstance(Robot robot) {
        return new RobotInfo(robot.getX(), robot.getY(), robot.getHeading(), robot.getVelocity());
    }

    public static RobotInfo getInstance(Point pos, double heading, double velocity) {
        return new RobotInfo(pos, heading, velocity);
    }

    public static RobotInfo getInstance(double x, double y, double heading, double velocity) {
        return new RobotInfo(x, y, heading, velocity);
    }

    public static RobotInfo copyOf(RobotInfo other) {
        return new RobotInfo(other.getX(), other.getY(), other.getHeading(), other.getVelocity());
    }

    public double getX() {
        return mPos.getX();
    }

    public double getY() {
        return mPos.getY();
    }

    public Point getPos() {
        return mPos;
    }

    public double getHeading() {
        return mHeading;
    }

    public double getVelocity() {
        return mVelocity;
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
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RobotInfo)) return false;

        RobotInfo robotInfo = (RobotInfo) o;

        if (Double.compare(robotInfo.getHeading(), getHeading()) != 0) return false;
        if (Double.compare(robotInfo.getVelocity(), getVelocity()) != 0) return false;
        return getPos() != null ? getPos().equals(robotInfo.getPos()) : robotInfo.getPos() == null;
    }

    @Override
    public String toString() {
        return "RobotInfo{" +
                "mHeading=" + mHeading +
                ", mPos=" + mPos +
                ", mVelocity=" + mVelocity +
                '}';
    }
}
