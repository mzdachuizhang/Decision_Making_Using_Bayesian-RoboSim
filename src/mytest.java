import smile.*;
import smile.Network;

/*
    This function is used to test the the jsmile
 */
public class mytest {

    public static void run() {
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

        //System.out.println("-------java.library.path:"+System.getProperty("java.library.path"));
        Network net = new Network();
        // load the network created by bayesNetwork
        net.readFile("bayesNetwork.xdsl");

        //网络中环境信息的设置
        //System.out.println("Setting f0_speed = "+env.getF0_speed());
        changeEvidenceAndUpdate(net, "f0_speed", "low");

//        changeEvidenceAndUpdate(net, "f0_speed", env.getF0_speed());
//        changeEvidenceAndUpdate(net, "f0_acc", env.getF0_acc());
//        changeEvidenceAndUpdate(net, "f0_lane", env.getF0_lane());
//        changeEvidenceAndUpdate(net, "f0_intension", env.getF0_intension());
//        changeEvidenceAndUpdate(net, "b0_speed", env.getB0_speed());
//        changeEvidenceAndUpdate(net, "b0_acc", env.getB0_acc());
//        changeEvidenceAndUpdate(net, "b0_lane", env.getB0_lane());
//        changeEvidenceAndUpdate(net, "b0_intension", env.getB0_intension());
//        changeEvidenceAndUpdate(net, "f1_speed", env.getF1_speed());
//        changeEvidenceAndUpdate(net, "f1_acc", env.getF1_acc());
//        changeEvidenceAndUpdate(net, "f1_lane", env.getF1_lane());
//        changeEvidenceAndUpdate(net, "f1_intension", env.getF1_intension());
//        changeEvidenceAndUpdate(net, "weather", env.getWeather());

        //System.out.println(net.getAllNodeIds());
        //System.out.println(net.getAllNodes());
        /*
        for (String node :net.getAllNodeIds()) {
            System.out.println(node);
        }

         */
        //net.getAllNodeIds();

        System.out.println(printPosteriors(net, "dec_lateral"));
        System.out.println(printPosteriors(net, "dec_longti"));


    }

    private static void changeEvidenceAndUpdate(
            Network net, String nodeId, String outcomeId) {
        if (outcomeId!=null) {
            net.setEvidence(nodeId, outcomeId);
        } else {
            net.clearEvidence(nodeId);
        }

        net.updateBeliefs();

//        for (String node :net.getAllNodeIds()) {
//            System.out.println(node);
//            printPosteriors(net, node);
//        }


    }

    private static String printPosteriors(Network net, String nodeId) {
        //todo
        //String nodeId = net.getNodeId(nodeHandle);
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



    public static void main(String[] args) {
        run();
    }
}
