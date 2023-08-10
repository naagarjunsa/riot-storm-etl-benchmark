package com.racelab.app;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

public class StatDataSpout extends BaseRichSpout {
    private SpoutOutputCollector collector;
    private BufferedReader reader;
    private String fileName;
    private boolean completed;
    private double id;

    public StatDataSpout(String fileName) {
        this.fileName = fileName;
        id = 0;
    }

    @Override
    public void open(Map<String, Object> conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        try {
            reader = new BufferedReader(new FileReader(this.fileName));
            completed = false;
        } catch (Exception e) {
            throw new RuntimeException("Error opening file: " + e.getMessage());
        }
    }

    @Override
    public void nextTuple() {
        if(!completed){
            try {
                id += 1;
                String line = reader.readLine();
                if (line != null) {
                    this.collector.emit(new Values(line));
                    System.out.println("LATENCY_RIOT_SOURCE : " + id + " : " + System.nanoTime());
                } else {
                    completed = true;
                    reader.close();
                }
            } catch (Exception e) {
                throw new RuntimeException("Error reading file: " + e.getMessage());
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("statDataString"));
    }

    @Override
    public void close() {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
