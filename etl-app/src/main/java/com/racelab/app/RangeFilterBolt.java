package com.racelab.app;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.tuple.Fields;

import java.util.HashMap;
import java.util.Map;

class Range {
    double min;
    double max;

    public Range(double min_val, double max_val) {
        min = min_val;
        max = max_val;
    }
}


public class RangeFilterBolt extends BaseRichBolt {
    private HashMap<String, Range> ranges;
    OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        ranges = new HashMap<>();
        ranges.put("acc_chest_x", new Range(-13.931, 4.123));
        ranges.put("acc_chest_y", new Range(-4.6376, 5.2361));
        ranges.put("acc_chest_z", new Range(-8.1881, 7.8786));
        ranges.put("ecg_1", new Range(-4.9314, 6.1371));
        ranges.put("ecg_2", new Range(-6.786, 6.6604));
        ranges.put("acc_ankle_x", new Range(-5.0006, 8.1472));
        ranges.put("acc_ankle_y", new Range(-14.303, 1.5909));
        ranges.put("acc_ankle_z", new Range(-8.6234, 8.6958));
        ranges.put("acc_arm_x", new Range(-9.824, 5.5778));
        ranges.put("acc_arm_y", new Range(-10.059, 8.506));
        ranges.put("acc_arm_z", new Range(-6.6739, 9.5725));
    }

    @Override
    public void execute(Tuple input) {
        HashMap<String, Double> inputMap = (HashMap<String, Double>) input.getValueByField("healthDataMap");

        boolean result = this.rangeFilter(inputMap);
        if(!result) {
            this.collector.emit(new Values(inputMap));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("healthDataMapRangeFiltered"));
    }

    private boolean rangeFilter(HashMap<String, Double> inputMap) {
        for (String key : ranges.keySet()) {
            Range validRange = ranges.get(key);
            double val = inputMap.get(key);
            if (val < validRange.min || val > validRange.max) {
                return true;
            }
        }
        return false;
    }
}
