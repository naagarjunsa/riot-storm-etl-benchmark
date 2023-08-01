import java.util.HashMap;

class Range {
    double min;
    double max;

    public Range(double min_val, double max_val) {
        min = min_val;
        max = max_val;
    }
}

public class RangeFilter {

    public static boolean rangeFilter(HashMap<String, Double> input_map) {

        HashMap<String, Range> ranges = new HashMap<>();
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


        for (String key : ranges.keySet()) {
            Range validRange = ranges.get(key);
            double val = input_map.get(key);
            if (val < validRange.min || val > validRange.max) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        String input = "source_id~1\tacc_chest_x~-9.7321\tacc_chest_y~0.24326\tacc_chest_z~0.28505\tecg_1~-0.59027\tecg_2~-0.38514\tacc_ankle_x~2.2135\tacc_ankle_y~-9.6887\tacc_ankle_z~0.43353\tgyro_ankle_x~0.076067\tgyro_ankle_y~-0.83114\tgyro_ankle_z~-0.69155\tmag_ankle_x~-0.73095\tmag_ankle_y~0.55777\tmag_ankle_z~0.29764\tacc_arm_x~-8.8318\tacc_arm_y~-4.109\tacc_arm_z~0.096632\tgyro_arm_x~-0.42745\tgyro_arm_y~-1.0164\tgyro_arm_z~0.019397\tmag_arm_x~-0.19549\tmag_arm_y~-1.5952\tmag_arm_z~3.6222\tlabel~0";
        HashMap<String, Double> input_map = new HashMap<> ();
        String[] entries = input.split("\t");

        for(String e : entries) {
            String[] kvPair = e.split("~");
            input_map.put(kvPair[0], Double.parseDouble(kvPair[1]));
        }

        boolean result = rangeFilter(input_map);
        System.out.println(result);
    }
}