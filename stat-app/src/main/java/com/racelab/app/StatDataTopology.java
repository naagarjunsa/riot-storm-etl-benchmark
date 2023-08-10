package com.racelab.app;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;

public class StatDataTopology {

    public void run() {
        String inputFile = "/home/centos/riot_storm/data.log";

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("statDataReader", new StatDataSpout(inputFile));
        builder.setBolt("serializer", new StatSenmlSerializerBolt()).shuffleGrouping("statDataReader");
        builder.setBolt("kalmanFilter", new KalmanFilterBolt()).shuffleGrouping("serializer");
        builder.setBolt("average", new AverageBolt()).shuffleGrouping("serializer");
        builder.setBolt("distinctCount", new DistinctCountBolt()).shuffleGrouping("serializer");
        builder.setBolt("regression", new RegressionBolt()).shuffleGrouping("kalmanFilter");
        builder.setBolt("storage", new StatStorageBolt()).shuffleGrouping("regression")
                                                    .shuffleGrouping("distinctCount")
                                                    .shuffleGrouping("average");


        try {
            Config config = new Config();
            LocalCluster cluster = new LocalCluster();

            cluster.submitTopology("StatDataToplogy", config, builder.createTopology());

            Thread.sleep(15000);
            cluster.shutdown();
            cluster.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}