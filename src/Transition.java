public class Transition {

    private State source;
    private State target;
    private Env condition;
    private Decision decision;

    public Transition() {
    }

    public Transition(State source, State target) {
        this.source = source;
        this.target = target;
    }

    public Transition(State source, State target, Env condition, Decision decision) {
        this.source = source;
        this.target = target;
        this.condition = condition;
        this.decision = decision;
    }

    public State getSource() {
        return source;
    }

    public void setSource(State source) {
        this.source = source;
    }

    public State getTarget() {
        return target;
    }

    public void setTarget(State target) {
        this.target = target;
    }

    public Env getCondition() {
        return condition;
    }

    public void setCondition(Env condition) {
        this.condition = condition;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    //todo 大改
    public String conditionPrint() {
        StringBuffer sb = new StringBuffer("condition ");
        sb.append("f0_speed==").append("Speed::").append(condition.getF0_speed());
        sb.append(" && f0_acc==").append("Acceleration::").append(condition.getF0_acc());
//        sb.append(" && f0_lane==").append("Lane::").append(condition.getF0_lane());
        sb.append(" && f0_intension==").append("Direction::").append(condition.getF0_intension());
        sb.append(" && b0_speed==").append("Speed::").append(condition.getB0_speed());
        sb.append(" && b0_acc==").append("Acceleration::").append(condition.getB0_acc());
//        sb.append(" && b0_lane==").append("Lane::").append(condition.getB0_lane());
        sb.append(" && b0_intension==").append("Direction::").append(condition.getB0_intension());
        sb.append(" && f1_speed==").append("Speed::").append(condition.getF1_speed());
        sb.append(" && f1_acc==").append("Acceleration::").append(condition.getF1_acc());
//        sb.append(" && f1_lane==").append("Lane::").append(condition.getF1_lane());
        sb.append(" && f1_intension==").append("Direction::").append(condition.getF1_intension());
        sb.append(" && weather==").append("Weather::").append(condition.getWeather());

        return sb.toString();
    }

    public String actionPrint() {
        StringBuffer sb = new StringBuffer("action $decision(");
        sb.append("Speed::").append(decision.getDecision_speed());
        sb.append(",");
        sb.append("Direction::").append(decision.getDecision_angle());
        sb.append(")");

        return sb.toString();
    }


}
