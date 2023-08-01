package com.racelab.app;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.tuple.Fields;

import java.util.HashMap;
import java.util.Map;
import java.sql.Timestamp;

public class AnnotationBolt extends BaseRichBolt {
    OutputCollector collector;
    

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        HashMap<String, Double> inputMap = (HashMap<String, Double>) input.getValueByField("healthDataMapInterpolated");

        String res_string = inputMap.toString();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        res_string += "\ttimestamp: ";
        res_string += timestamp.toString();

        this.collector.emit(input, new Values(res_string));
        this.collector.ack(input);
        System.out.println("LATENCY_RIOT_ANNOT : " + inputMap.get("source_id") + " : " + System.nanoTime());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("healthDataAnnotated"));
    }
}
