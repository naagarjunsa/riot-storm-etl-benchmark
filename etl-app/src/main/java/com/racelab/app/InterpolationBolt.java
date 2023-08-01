package com.racelab.app;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseStatefulBolt;
import org.apache.storm.state.KeyValueState;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.tuple.Fields;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

public class InterpolationBolt extends BaseStatefulBolt<KeyValueState<String, Double>> {
    List<String> fields;
    private KeyValueState<String, Double> globalStateMap;
    private OutputCollector collector;

    public void initState(KeyValueState<String,Double> globalStateMap) {
        this.globalStateMap = globalStateMap;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;

        fields = Arrays.asList(
            "acc_chest_x",
            "acc_chest_y",
            "acc_chest_z",
            "ecg_1",
            "ecg_2",
            "acc_ankle_x",
            "acc_ankle_y",
            "acc_ankle_z",
            "acc_arm_x",
            "acc_arm_y",
            "acc_arm_z"
        );

    }

    @Override
    public void execute(Tuple input) {
        HashMap<String, Double> inputMap = (HashMap<String, Double>) input.getValueByField("healthDataMapBloomFiltered");
        Double count = this.globalStateMap.get("count", 0.0);

        for(String field: fields) {
            Double avg_val = this.globalStateMap.get(field, 0.0);

            if(inputMap.containsKey(field)) {
                //update the global state
                avg_val = (avg_val * (count-1) + inputMap.get(field)) * count;
            }
            else {
                // add the average value to the state and update global state
                inputMap.put(field, avg_val);
                avg_val = (avg_val * (count-1) + avg_val) * count;
            }
            this.globalStateMap.put(field, avg_val);
        }
        this.globalStateMap.put("count", count+1);

        this.collector.emit(input, new Values(inputMap));
        this.collector.ack(input);
        System.out.println("LATENCY_RIOT_INTER : " + inputMap.get("source_id") + " : " + System.nanoTime());

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("healthDataMapInterpolated"));
    }

}
