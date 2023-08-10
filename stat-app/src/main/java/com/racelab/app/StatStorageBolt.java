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


public class StatStorageBolt extends BaseRichBolt {
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
        HashMap<String, Double> inputCount = (HashMap<String, Double>) input.getValueByField("statDataCounted");
        HashMap<String, Double> inputRegression = (HashMap<String, Double>) input.getValueByField("statDataMapRegressed");
        HashMap<String, Double> inputAverage = (HashMap<String, Double>) input.getValueByField("statDataMapAveraged");
        String output = inputCount.toString() + "\n" + inputRegression.toString() + "\n" + inputAverage.toString() + "\n\n";
        try {
            writer.write(output);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.collector.emit(input, new Values(output));
        
        this.collector.ack(input);
        System.out.println("LATENCY_RIOT_STORAGE : " + inputAverage.get("source_id") + " : " + System.nanoTime());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("statDataMapStored"));
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
