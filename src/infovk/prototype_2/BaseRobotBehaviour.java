package infovk.prototype_2;

public abstract class BaseRobotBehaviour<T extends RobotBase> extends SimpleRobotBehavior<T> {

    public BaseRobotBehaviour(T robot) {
        super(robot);
    }


}
