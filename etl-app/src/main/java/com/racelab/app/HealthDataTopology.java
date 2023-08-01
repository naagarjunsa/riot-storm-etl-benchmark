package com.racelab.app;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;


public class HealthDataTopology {

    public static void run() {
        String inputFile = "/home/centos/riot_storm/data.log";

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("healthDataReader", new HealthDataSpout(inputFile));
        builder.setBolt("serializer", new SenmlSerializerBolt()).shuffleGrouping("healthDataReader");
        builder.setBolt("rangeFilter", new RangeFilterBolt()).shuffleGrouping("serializer");
        builder.setBolt("bloomFilter", new BloomFilterBolt()).shuffleGrouping("rangeFilter");
        builder.setBolt("interpolation", new InterpolationBolt()).shuffleGrouping("bloomFilter");
        builder.setBolt("annotate", new AnnotationBolt()).shuffleGrouping("interpolation");

        try {
            Config config = new Config();
            //config.registerMetricsConsumer(org.apache.storm.metric.LoggingMetricsConsumer.class, 1);
            LocalCluster cluster = new LocalCluster();

            cluster.submitTopology("HealthDataTopology", config, builder.createTopology());

            Thread.sleep(15000);
            cluster.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}