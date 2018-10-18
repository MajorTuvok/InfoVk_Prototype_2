package infovk.prototype_2;

public enum BehaviourType {
    DEFAULT {
        @Override
        public BaseRobotBehaviour<RobotBase> createBehaviour(RobotBase robot) {
            return new BaseRobotBehaviour<RobotBase>(robot) {
                @Override
                protected void start() {

                }

                @Override
                protected void loop() {

                }
            };
        }
    };

    public abstract BaseRobotBehaviour<RobotBase> createBehaviour(RobotBase robot);
}
