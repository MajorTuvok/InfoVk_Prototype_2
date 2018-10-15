package infovk.protoype_2;

public class MyFirstRobot extends SimpleRobot {
    public MyFirstRobot() {
        behavior = new MyFirstBehavior(this);
    }
}
