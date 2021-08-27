

public class State {
    // ego_state
    private String s_id;
    private Boolean isInit=false;
    private Boolean isJunc=false;
    private String ego_speed;
    private String ego_angle;

    /*
    //移到env 不打印
    private String ego_lane;
    private String rel_b0;
    private String rel_f0;
    private String rel_f1;
    private String is_safe_b0;
    private String is_safe_f0;
    private String is_safe_f1;

     */


    public State() {
    }

    public State(Boolean isInit) {
        s_id = "initial";
        this.isInit = isInit;
    }

    public State(String s_id, Boolean isJunc) {
        this.s_id = s_id;
        this.isJunc = isJunc;
    }

    public Boolean getInit() {
        return isInit;
    }

    public void setInit(Boolean init) {
        isInit = init;
    }

    public Boolean getJunc() {
        return isJunc;
    }

    public void setJunc(Boolean junc) {
        isJunc = junc;
    }

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public String getEgo_speed() {
        return ego_speed;
    }

    public void setEgo_speed(String ego_speed) {
        this.ego_speed = ego_speed;
    }

    public String getEgo_angle() {
        return ego_angle;
    }

    public void setEgo_angle(String ego_angle) {
        this.ego_angle = ego_angle;
    }



}
