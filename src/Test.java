import smile.Network;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Test {

    HashSet<State> StateSet = new HashSet<>();
    HashSet<State> JunctionSet = new HashSet<>();
    HashSet<Transition> TransitionSet = new HashSet<>();
    HashSet<Transition> TransitionCompareSet = new HashSet<>();
    HashMap<State,String> StateIDMap = new HashMap<>();
    State state, next_state;
    Env env;
    Decision decision;

    State init = new State(true);

    public Test() {
    }

    public void work(Data data, Boolean isTeamFirst) {
        state = new State();

        if (isTeamFirst) { //data是一组数据中的第一条，表示此场景刚开始
            //System.out.println("[work]: isTeamFirst");

            state = data.ego_state;
            if (!StateSet.contains(state)) {
                //System.out.println("[work]: !StateSet.contains(state)");
                String temp_id = StateSet.size()+1+"";
                StateIDMap.put(state, temp_id);
                state.setS_id(temp_id);
                StateSet.add(state);
            }
            else {
                state.setS_id(StateIDMap.get(state));
            }

            Transition trans0 = new Transition(init, state, null, null);
            if (!TransitionSet.contains(trans0)) {
                TransitionSet.add(trans0);
            }
        }
        else {
            //System.out.println("[work]: isTeamFirst false");
            state = next_state;
            //System.out.println("[work]: state.getEgo_speed():"+state.getEgo_speed());
        }

        next_state = new State();

        env = data.env;
        decision = Bayesian(state, env);
        next_state = count(state, decision);
        if (!StateSet.contains(next_state)) {
            /*
            if (!next_state.isSafe()) {
                //TODO 回溯修改贝叶斯网络，重新做决策
                // ？哪些节点可能更新（跑案例结果 结合案例→ 环境改变影响决策概率
                // 决策出错时的安全验证结果 （贝叶斯算法错？ or 贝叶斯转RoboSim错？）
            }
            else {
             */
            String temp_id = StateSet.size()+1+"";
            StateIDMap.put(next_state, temp_id);
            next_state.setS_id(temp_id);
//            next_state.setS_id(StateSet.size()+1+"");
            StateSet.add(next_state);
        }
        else {
            next_state.setS_id(StateIDMap.get(next_state));
        }


        Transition trans = new Transition(state, next_state, env, decision);
        if (TransitionCompareSet.contains(trans)) {
            return;
        } else {
            TransitionCompareSet.add(trans);
        }

//        State j = new State(state.getS_id()+"_"+next_state.getS_id(), true);
        State j = new State(JunctionSet.size()+1+"", true);
        //System.out.println("[work]: new j_id = "+j.getS_id());
        JunctionSet.add(j);
        Transition trans1 = new Transition(state, j, env, decision);
        Transition trans2 = new Transition(j, next_state, env, decision);
        if (!TransitionSet.contains(trans1)) {
            TransitionSet.add(trans1);
        }
        if (!TransitionSet.contains(trans2)) {
            TransitionSet.add(trans2);
        }

    }


    //TODO 贝叶斯算法部分
    // 贝叶斯网络 选概率最大的迁移 做决策 →？并不一定安全
    public Decision Bayesian(State state, Env env) {
        //System.out.println("[Bayesian]: state.getEgo_speed():"+state.getEgo_speed());

        Decision decision = run(state, env);

        return decision;
    }

    //todo 改成本项目
    public static Decision run(State state, Env env) {

        Network net = new Network();
        // load the network created by bayesNetwork
        net.readFile("bayesNetwork.xdsl");

        //System.out.println("[run]: state.getEgo_speed():"+state.getEgo_speed());

        //网络中信息的设置
        changeEvidenceAndUpdate(net, "ego_speed", state.getEgo_speed());
        changeEvidenceAndUpdate(net, "ego_direc", state.getEgo_angle());

        changeEvidenceAndUpdate(net, "ego_lane",env.getEgo_lane());
        changeEvidenceAndUpdate(net, "rel_b0",env.getRel_b0());
        changeEvidenceAndUpdate(net, "rel_f0",env.getRel_f0());
        changeEvidenceAndUpdate(net, "rel_f1",env.getRel_f1());
        changeEvidenceAndUpdate(net, "is_safe_b0",env.getIs_safe_b0());
        changeEvidenceAndUpdate(net, "is_safe_f0",env.getIs_safe_f0());
        changeEvidenceAndUpdate(net, "is_safe_f1",env.getIs_safe_f1());

        changeEvidenceAndUpdate(net, "f0_speed", env.getF0_speed());
        changeEvidenceAndUpdate(net, "f0_acc", env.getF0_acc());
        //changeEvidenceAndUpdate(net, "f0_lane", env.getF0_lane());
        changeEvidenceAndUpdate(net, "f0_intension", env.getF0_intension());
        changeEvidenceAndUpdate(net, "b0_speed", env.getB0_speed());
        changeEvidenceAndUpdate(net, "b0_acc", env.getB0_acc());
        //changeEvidenceAndUpdate(net, "b0_lane", env.getB0_lane());
        changeEvidenceAndUpdate(net, "b0_intension", env.getB0_intension());
        changeEvidenceAndUpdate(net, "f1_speed", env.getF1_speed());
        changeEvidenceAndUpdate(net, "f1_acc", env.getF1_acc());
        //changeEvidenceAndUpdate(net, "f1_lane", env.getF1_lane());
        changeEvidenceAndUpdate(net, "f1_intension", env.getF1_intension());
        changeEvidenceAndUpdate(net, "weather", env.getWeather());

        String decision_speed = printPosteriors(net, "dec_longti");
        String decision_angle = printPosteriors(net, "dec_lateral");
        Decision decision = new Decision(decision_speed, decision_angle);
        return decision;
    }

    private static void changeEvidenceAndUpdate(
            Network net, String nodeId, String outcomeId) {
        if (outcomeId!=null) {
            net.setEvidence(nodeId, outcomeId);
        } else {
            net.clearEvidence(nodeId);
        }

        net.updateBeliefs();

    }

    private static String printPosteriors(Network net, String nodeId) {
        //todo
        int nodeHandle = net.getNode(nodeId);

        double[] posteriors = net.getNodeValue(nodeHandle);

        double maxValue = 0.0;
        String maxLabel = "";

        for (int i = 0; i < posteriors.length; i ++) {
//            System.out.printf("P(%s=%s)=%f\n",
//                    nodeId,
//                    net.getOutcomeId(nodeHandle, i),
//                    posteriors[i]);
            if (posteriors[i] > maxValue) {
                maxValue = posteriors[i];
                maxLabel = net.getOutcomeId(nodeHandle, i);
            }
        }
        return maxLabel;

    }


    //TODO 下一个状态
    public State count(State state, Decision decision) {
        String dec_speed = decision.getDecision_speed();
        String dec_angle = decision.getDecision_angle();
        String ego_speed = state.getEgo_speed();
        String next_speed="", next_angle=dec_angle;

        //System.out.println("[count]: ego_speed=="+ego_speed+" dec_speed=="+dec_speed);

        //speed
        if (ego_speed.equals("stop")) {
            if (dec_speed.equals("keep")) {
                next_speed = "stop";
            } else if (dec_speed.equals("acc")) {
                next_speed = "low";
            } else if (dec_speed.equals("dec")) {
                next_speed = "stop";
            }
        }
        else if (ego_speed.equals("low")) {
            if (dec_speed.equals("keep")) {
                next_speed = "low";
            } else if (dec_speed.equals("acc")) {
                next_speed = "mid";
            } else if (dec_speed.equals("dec")) {
                next_speed = "stop";
            }
        }
        else if (ego_speed.equals("mid")) {
            if (dec_speed.equals("keep")) {
                next_speed = "mid";
            } else if (dec_speed.equals("acc")) {
                next_speed = "mid";
            } else if (dec_speed.equals("dec")) {
                next_speed = "low";
            }
        }
        else if (ego_speed.equals("fast")) {
            if (dec_speed.equals("keep")) {
                next_speed = "fast";
            } else if (dec_speed.equals("acc")) {
                next_speed = "fast";
            } else if (dec_speed.equals("dec")) {
                next_speed = "mid";
            }
        }

        State next_state = new State();
        next_state.setEgo_speed(next_speed);
        next_state.setEgo_angle(next_angle);

        //System.out.println("[count]: next_state.getEgo_speed():"+next_state.getEgo_speed());


        return next_state;
    }


    public static void main(String[] args) {
        // The license need to be updated after 2022-02-23
        new smile.License(
                "SMILE LICENSE 6bc07a0d 3773c5d1 49bc60e1 " +
                        "THIS IS AN ACADEMIC LICENSE AND CAN BE USED " +
                        "SOLELY FOR ACADEMIC RESEARCH AND TEACHING, " +
                        "AS DEFINED IN THE BAYESFUSION ACADEMIC " +
                        "SOFTWARE LICENSING AGREEMENT. " +
                        "Serial #: 1bvw5ogpa6wgbm4u29dulcuhj " +
                        "Issued for: ZHANG MINGZHUO (645694711@qq.com) " +
                        "Academic institution: East China Normal University " +
                        "Valid until: 2022-02-23 " +
                        "Issued by BayesFusion activation server",
                new byte[] {
                        -44,119,113,-115,55,-32,-87,33,42,-62,-78,-42,-107,-7,126,-118,
                        -72,-93,4,-58,123,-119,-7,-119,114,-52,113,94,-17,53,51,18,
                        65,99,95,15,12,64,-88,5,107,81,-60,104,85,12,56,125,
                        -40,-121,-113,-24,84,31,45,19,-45,-90,-113,34,-106,-124,-120,31
                }
        );

        Test test = new Test();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("Data.csv"));
            reader.readLine(); reader.readLine(); //表头标题信息处理
            String line;
            String data_id="";
            Boolean isTeamFirst=false;
            while ((line=reader.readLine()) != null) {
                String[] item = line.split(",");
                if (item[0].equals(""))
                    break;

                if (!data_id.equals(item[0])) {
                    isTeamFirst = true;
                    data_id = item[0];
                    //System.out.println("______new data_id:"+data_id);
                }

                State ego_state = new State();
                ego_state.setEgo_speed(item[1]);
                //System.out.println(item[1]);
                ego_state.setEgo_angle(item[2]);

                Env env = new Env();
                env.setEgo_lane(item[3]);
                env.setRel_f0(item[4]);
                env.setRel_b0(item[5]);
                env.setRel_f1(item[6]);
                env.setIs_safe_f0(item[7]);
                env.setIs_safe_f1(item[8]);
                env.setIs_safe_b0(item[9]);
                env.setF0_speed(item[10]);
                env.setF0_acc(item[11]);
                env.setF0_lane(item[12]);
                env.setF0_intension(item[13]);
                env.setB0_speed(item[14]);
                env.setB0_acc(item[15]);
                env.setB0_lane(item[16]);
                env.setB0_intension(item[17]);
                env.setF1_speed(item[18]);
                env.setF1_acc(item[19]);
                env.setF1_lane(item[20]);
                env.setF1_intension(item[21]);
                env.setWeather(item[22]);

                Data data = new Data(data_id, ego_state, env);
                test.work(data, isTeamFirst);

                isTeamFirst = false;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //Output 生成RoboSim模型文本
        //todo 【改】
        try {
            File f = new File("output.txt");
            FileWriter fw = new FileWriter(f);
            fw.write("");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            pw.println("\tinitial i0");

            // 声明所有junc
            for (State st : test.JunctionSet) {
                if (st.getJunc())
                    pw.println("\tjunction j_"+st.getS_id());
            }
            // 声明所有state
            for (State st : test.StateSet) {
                if (!st.getJunc())
//                    pw.println("\tState s_"+st.getS_id()+" {"+ st.getEgo_speed() + st.getEgo_angle() +"}");
                    pw.println("\tState s_"+st.getS_id()+" { }");
            }

            //init -> state

            for (Transition trans : test.TransitionSet) {
                if (trans.getSource().getInit()){
                    String s_id = trans.getTarget().getS_id();
//                    System.out.println("[main:print]: init->s_id trans: "+s_id);

                    pw.println("\ttransition i0_s_"+s_id+" {\n" +
                            "\t\tfrom i0\n" +
                            "\t\tto s_"+s_id+"\n\t}");
                }
            }

            //s_1 -> j_1_2
            for (Transition trans : test.TransitionSet) {
                if (trans.getTarget().getJunc()) {
                    String id = trans.getSource().getS_id();
                    String j_id = trans.getTarget().getS_id();
                    pw.println("\ttransition s_"+id+"_to_j_"+j_id+" {\n" +
                            "\t\tfrom s_"+id+"\n" +
                            "\t\tto j_"+j_id+"\n" +
                            "\t\t"+trans.conditionPrint()+"\n" +
                            "\t\t"+trans.actionPrint()+"\n\t}");
                }
            }

            //j_1_2 -> s_2
            for (Transition trans : test.TransitionSet) {
                if (trans.getSource().getJunc()) {
                    String j_id = trans.getSource().getS_id();
                    String id = trans.getTarget().getS_id();
                    pw.println("\ttransition j_"+j_id+"_to_s_"+id+" {\n" +
                            "\t\tfrom j_"+j_id+"\n" +
                            "\t\tto s_"+id+"\n" +
                            "\t\texec\n\t}");
                }
            }

            pw.flush();
            pw.close();

//            StateSet.clear();
//            TransitionSet.clear();


        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}

class Data {
    String data_id;
    State ego_state;
    Env env;

    public Data(String data_id, State ego_state, Env env) {
        this.data_id = data_id;
        this.ego_state = ego_state;
        this.env = env;
    }
}
