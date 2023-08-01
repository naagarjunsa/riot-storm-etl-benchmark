import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BloomFilter {

    private static final int[] seeds = {31, 131, 1313, 13131, 131313, 1313131, 13131313};

    // Source: https://www.partow.net/programming/hashfunctions/#BKDRHashFunction
    private static int BKDRHash(String str, int seed) {
        int hash = 0;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash * seed) + str.charAt(i);
        }
        return hash;
    }


    private static boolean bloomFilter(Map<String, Double> m) {
        Set<Integer> filter = new HashSet<>(Arrays.asList(
            0xcd3cc008, 0x1de69e09, 0x6e907c0a, 0xbf3a5a0b, 0xfe4380c,  0x608e160d,
            0xb137f40e, 0x1e1d20f,  0x528bb010, 0x1c7f29f6, 0x602196bc, 0x2473c5fd,
            0xe8c5f53e, 0xad18247f, 0x716a53c0, 0x35bc8301, 0xfa0eb242, 0xbe60e183,
            0x82b310c4, 0x3486424e, 0xe620bac2, 0xb288ecc3, 0x7ef11ec4, 0x4b5950c5,
            0x17c182c6, 0xe429b4c7, 0xb091e6c8, 0x7cfa18c9, 0x49624aca, 0xefb75612,
            0xfff044e4, 0x77dfba5,  0xf0bb266,  0x16996927, 0x1e271fe8, 0x25b4d6a9,
            0x2d428d6a, 0x34d0442b, 0x3c5dfaec, 0xc6bf15be, 0xd079a012, 0xef8c2713,
            0xe9eae14,  0x2db13515, 0x4cc3bc16, 0x6bd64317, 0x8ae8ca18, 0xa9fb5119,
            0xc90dd81a, 0xb56eae32, 0x8be0a84,  0xba820745, 0x6c460406, 0x1e0a00c7,
            0xcfcdfd88, 0x8191fa49, 0x3355f70a, 0xe519f3cb, 0x96ddf08c, 0x41200b7e,
            0xfa6e4352, 0xddcb5e53, 0xc1287954, 0xa4859455, 0x87e2af56, 0x6b3fca57,
            0x4e9ce558, 0x31fa0059, 0x15571b5a, 0x6d6f6ab2
        ));

        for (int seed : seeds) {
            int id = m.get("source_id").intValue();
            String source = id + "_mHealth_subject";
            int hash = BKDRHash(source, seed);
            if (!filter.contains(hash)) {
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

        boolean result = bloomFilter(input_map);
        System.out.println(result);
    }
}
