package com.racelab.app;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.tuple.Fields;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class StorageBolt extends BaseRichBolt {
    OutputCollector collector;
    private FileWriter writer;
    private String fileName = "/home/centos/riot_storm/results.txt";

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        try {
            writer = new FileWriter(fileName);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void execute(Tuple input) {
        HashMap<String, Double> inputMap = (HashMap<String, Double>) input.getValueByField("healthDataAnnotated");

        try {
            writer.write(inputMap.toString());
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.collector.emit(input, new Values(inputMap.toString()));
        
        this.collector.ack(input);
        System.out.println("LATENCY_RIOT_STORAGE : " + inputMap.get("source_id") + " : " + System.nanoTime());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("healthDataMapStored"));
    }

    @Override
    public void cleanup() {
        try {
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
