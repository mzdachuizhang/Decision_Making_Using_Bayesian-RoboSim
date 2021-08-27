public class Decision {
    
    private String decision_speed; //=longti
    private String decision_angle; //=lateral

    public Decision() {
    }

    public Decision(String decision_speed, String decision_angle) {
        this.decision_speed = decision_speed;
        this.decision_angle = decision_angle;
    }

    public String getDecision_speed() {
        return decision_speed;
    }

    public void setDecision_speed(String decision_speed) {
        this.decision_speed = decision_speed;
    }

    public String getDecision_angle() {
        return decision_angle;
    }

    public void setDecision_angle(String decision_angle) {
        this.decision_angle = decision_angle;
    }
}
