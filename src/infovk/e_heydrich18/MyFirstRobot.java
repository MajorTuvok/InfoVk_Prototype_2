package infovk.e_heydrich18;

public class MyFirstRobot extends SimpleRobot {
    public MyFirstRobot() {
        behavior = new MyFirstBehavior(this);
    }
}
