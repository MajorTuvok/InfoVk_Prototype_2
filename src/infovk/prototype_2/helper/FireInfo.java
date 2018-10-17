package infovk.prototype_2.helper;

public final class FireInfo {
    private final double mPower;
    private final RobotInfo mTarget;
    private final long mTime;

    public FireInfo(long time, double power, RobotInfo target) {
        mPower = power;
        mTarget = target;
        mTime = time;
    }

    public double getPower() {
        return mPower;
    }

    public RobotInfo getTarget() {
        return mTarget;
    }

    public long getTime() {
        return mTime;
    }
}
