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
import java.util.Random;
import java.util.List;
import java.util.Arrays;
import java.lang.Math;



public class RegressionBolt extends BaseStatefulBolt<KeyValueState<String, Double>> {
    List<String> fields;
    private KeyValueState<String, Double> globalStateMap;
    private OutputCollector collector;
    private double dt = 1e-2;
    private double T = 5e-2;
    private double decay_factor = Math.exp(-dt/T);
    RegressionData r;

    double noise() {
        Random rand = new Random();
        double random_num = rand.nextDouble();
        return (random_num-0.5) * 0.1;
    }

    public Double[] regression(double new_x, double new_y, String field) {
        
        Double[] res = {0.0, 0.0};
        synchronized (this) {

        double num = this.globalStateMap.get(field+".num", 0.0);
        double x = this.globalStateMap.get(field+".x", 0.0);
        double y = this.globalStateMap.get(field+".y", 0.0);
        double xx = this.globalStateMap.get(field+".xx", 0.0);
        double xy = this.globalStateMap.get(field+".xy", 0.0);

        num *= decay_factor;
        x *= decay_factor;
        y *= decay_factor;
        xx *= decay_factor;
        xy *= decay_factor;

        num += 1;
        x += new_x;
        y += new_y;
        xx += new_x * new_x;
        xy += new_x * new_y;

        double det = num * xx - Math.pow(r.x, 2);
        if(det > 1e-10) {
            res[0] = (r.xx * r.xy * r.x)/det;
            res[1] = (r.xy*r.num - r.x*r.y)/det;
        }

        this.globalStateMap.put(field+".x", x);
        this.globalStateMap.put(field+".y", y);
        this.globalStateMap.put(field+".xx", xx);
        this.globalStateMap.put(field+".xy", xy);
        this.globalStateMap.put(field+".num", num);
    }
        return res;
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
            this.globalStateMap.put(field+".x", 0.0);
            this.globalStateMap.put(field+".y", 0.0);
            this.globalStateMap.put(field+".xx", 0.0);
            this.globalStateMap.put(field+".xy", 0.0);
            this.globalStateMap.put(field+".num", 0.0);
        }



    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        r = new RegressionData();
    }

    @Override
    public void execute(Tuple input) {
        HashMap<String, Double> inputMap = (HashMap<String, Double>) input.getValueByField("statDataMapFiltered");
        HashMap<String, Double> outputMap = new HashMap<String, Double>();
        Integer i;
        double x = inputMap.get("source_id");
        for(i=0; i < this.fields.size();i++) {
            Double[] res = regression(x, inputMap.get(this.fields.get(i)), this.fields.get(i));
            outputMap.put(this.fields.get(i)+"intercept", res[0]);
            outputMap.put(this.fields.get(i)+"slope", res[0]);
        }

        this.collector.emit(input, new Values(outputMap));
        this.collector.ack(input);
        System.out.println("LATENCY_RIOT_REGR : " + inputMap.get("source_id") + " : " + System.nanoTime());

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("statDataMapRegressed"));
    }

}