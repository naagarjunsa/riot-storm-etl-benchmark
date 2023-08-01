package com.racelab.app;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.tuple.Fields;

import java.util.HashMap;
import java.util.Map;

public class SenmlSerializerBolt extends BaseRichBolt {
    private HashMap<String, Double> senmlMap;
    OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }


    public static HashMap<String, Double> serializeSenml(String senml) {
        String serialized = "";

        String senmlClean = senml.substring(2, senml.length() - 2);

        HashMap<String, Double> senml_map = new HashMap<String, Double>();

        String[] entries = senmlClean.split("\\},\\{");
        for (String e : entries) {
            String[] kvPairs = e.split(",");

            // Key
            String keyPair = kvPairs[0];
            keyPair = keyPair.substring(0, keyPair.length() - 1);
            String key = keyPair.substring(keyPair.indexOf(' ') + 2);

            // Value
            String valuePair = kvPairs[kvPairs.length - 1];
            String value = valuePair.substring(valuePair.indexOf(' ') + 1);

            senml_map.put(key, Double.parseDouble(value));
            
        }

        return senml_map;
    }


    @Override
    public void execute(Tuple input) {
        String senml = input.getStringByField("healthDataString");
        senmlMap = this.serializeSenml(senml);
        this.collector.emit(new Values(senmlMap));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("healthDataMap"));
    }
}
