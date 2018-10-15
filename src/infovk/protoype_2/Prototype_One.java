package infovk.protoype_2;

import java.awt.*;

public class Prototype_One extends SimpleRobot {
    public Prototype_One() {
        behavior = new Behavior_One(this);
    }

    @Override
    public void run() {
        setBodyColor(Color.BLACK);
        setGunColor(Color.GREEN);
        setRadarColor(Color.BLACK);
        setScanColor(Color.RED);
        setBulletColor(Color.WHITE);

        super.run();
    }
}
