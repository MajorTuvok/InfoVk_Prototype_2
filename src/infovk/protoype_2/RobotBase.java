package infovk.protoype_2;

public class RobotBase extends SimpleRobot {
    private double energyPowerFactor;
    public RobotBase() {
        setEnergyPowerFactor(50);
    }

    public double getEnergyPowerFactor() {
        return energyPowerFactor;
    }

    public void setEnergyPowerFactor(double energyPowerFactor) {
        this.energyPowerFactor = energyPowerFactor;
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
        fire(baseVal * getEnergy() / 50);
    }
}
