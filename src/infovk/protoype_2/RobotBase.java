package infovk.protoype_2;

public class RobotBase extends SimpleRobot {
    private double energyPowerFactor;
    private double disFactor;
    public RobotBase() {
        setEnergyPowerFactor(50);
        setDisFactor(25);
    }

    protected double getEnergyPowerFactor() {
        return energyPowerFactor;
    }

    protected void setEnergyPowerFactor(double energyPowerFactor) {
        this.energyPowerFactor = energyPowerFactor;
    }

    protected double getDisFactor() {
        return disFactor;
    }

    protected void setDisFactor(double disFactor) {
        this.disFactor = disFactor;
    }

    @Override
    public void run() {

        if (behavior != null) {
            behavior.start();
        }
        start();
        execute();

        while (true) {
            if (behavior != null) {
                behavior.execute();
            }
            loop();
            execute();
        }
    }

    protected void setAdjustToTurns() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
    }

    protected void start() {

    }

    protected void loop() {

    }

    protected void fireRelativeToEnergy(double baseVal) {
        fire(Math.max(Math.min(baseVal * getEnergy() / 50, 3), 0.1));
    }

    protected void fireRelativeToEnergyAndDistance(double baseVal, double distance) {
        fireRelativeToEnergy(baseVal - (int) distance / disFactor);
    }
}
