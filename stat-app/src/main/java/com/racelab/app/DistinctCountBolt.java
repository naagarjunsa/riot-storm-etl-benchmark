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
import java.lang.Math;

public class DistinctCountBolt extends BaseStatefulBolt<KeyValueState<String, Double>> {
    List<String> fields;
    private KeyValueState<String, Double> globalStateMap;
    private OutputCollector collector;
    private  double magic_num = 0.79402;
    private  int bucket_param = 5;
    private  int num_buckets = 1 << bucket_param;
    
    private int trailing_zeros(int n) {
        int bits = 0;

        if(n != 0) {
            while ((n&1) == 0){
                ++bits;
                n >>=1;
            }
        }
        return bits;
    }

    private int distinctCount(HashMap<String, Double> inputMap){

        int hash_value = inputMap.toString().hashCode();

        Integer bucket_id = hash_value & (num_buckets-1);
        String bucket_key = bucket_id.toString();
        double bucket_value = Math.max(this.globalStateMap.get(bucket_key, 0.0), (double)(trailing_zeros(hash_value >> bucket_param)));
        this.globalStateMap.put(bucket_key, bucket_value);

        double sum_max_zeros = 0;
        Integer i =0;
        for(i=0;i<num_buckets;i++){
            sum_max_zeros += this.globalStateMap.get(i.toString());
        }
        return (int)(magic_num * num_buckets * Math.pow(2, sum_max_zeros/num_buckets));
    }

    public void initState(KeyValueState<String,Double> globalStateMap) {
        this.globalStateMap = globalStateMap;
        Integer i;
        for(i = 0; i<this.num_buckets; i++){
            this.globalStateMap.put(i.toString(), 0.0); 
        }
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        HashMap<String, Double> inputMap = (HashMap<String, Double>) input.getValueByField("statDataMap");

        int output = distinctCount(inputMap);

        this.collector.emit(input, new Values(output));
        this.collector.ack(input);
        System.out.println("LATENCY_RIOT_COUNT : " + inputMap.get("source_id") + " : " + System.nanoTime());

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("statDataCounted"));
    }

}