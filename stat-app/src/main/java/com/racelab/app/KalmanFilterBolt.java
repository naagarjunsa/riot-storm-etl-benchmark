package com.racelab.app;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.task.OutputCollector;
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

public class KalmanFilterBolt extends BaseStatefulBolt<KeyValueState<String, Double>> {
    List<String> fields;
    private KeyValueState<String, Double> globalStateMap;
    private OutputCollector collector;
    private static float q_processNoise = 0.125f;
    private static float r_sensorNoise = 0.32f;

    public HashMap<String, Double> kalmanFilter(HashMap<String, Double> input) {

        HashMap<String, Double> estimates = new HashMap<String, Double>();

        for (String field : this.fields) {
            
            // Time update
            double x0 = this.globalStateMap.get("x0s"+field);
            double p0 = this.globalStateMap.get("p0s"+field);
            double z_measuredValue = input.get(field);

            double p1 = p0 + q_processNoise;

            // Measurement update
            double k_kalmanGain = p1 / (p1 + r_sensorNoise);
            double x1 = x0 + k_kalmanGain * (z_measuredValue - x0);
            p1 = (1 - k_kalmanGain) * p1;

            // Update values
            this.globalStateMap.put("x0s"+field, x1);
            this.globalStateMap.put("p0s"+field, p1);

            estimates.put(field, x1);
        }

        return estimates;
    }

    public void initState(KeyValueState<String,Double> globalStateMap) {
        this.globalStateMap = globalStateMap;
        this.fields = Arrays.asList(
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

        for(String field : this.fields) {
            this.globalStateMap.put("p0s"+field, 30.0);
            this.globalStateMap.put("x0s"+field, 0.0);
        }
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        HashMap<String, Double> inputMap = (HashMap<String, Double>) input.getValueByField("statDataMap");

        HashMap<String, Double> outputMap = kalmanFilter(inputMap);
        outputMap.put("source_id", inputMap.get("source_id"));
        this.collector.emit(input, new Values(outputMap));
        this.collector.ack(input);
        System.out.println("LATENCY_RIOT_KALMAN : " + inputMap.get("source_id") + " : " + System.nanoTime());

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("statDataMapFiltered"));
    }

}