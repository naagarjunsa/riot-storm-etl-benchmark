import java.util.ArrayList;
import java.util.List;

public class SenmlSerializer {

    public static HashMap<String, Double> serializeSenml(String senml) {
        String serialized = "";

        // Remove cruft
        String senmlClean = senml.substring(2, senml.length() - 2);

        HashMap<String, Double> senml_map = new HashMap<String, Double>();
        // Extract entries
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

            senml_map.add(key, Double.parseDouble(value));
            
        }

        return senml_map;
    }

    public static void main(String[] args) {
        String senml = "[{\"n\": \"source_id\",\"t\": \"int\",\"v\": 1},{\"n\": \"acc_chest_x\",\"t\": \"double\",\"u\": \"m/s^2\",\"v\": -9.7321},{\"n\": \"acc_chest_y\",\"t\": \"double\",\"u\": \"m/s^2\",\"v\": 0.24326},{\"n\": \"acc_chest_z\",\"t\": \"double\",\"u\": \"m/s^2\",\"v\": 0.28505},{\"n\": \"ecg_1\",\"t\": \"double\",\"u\": \"mV\",\"v\": -0.59027},{\"n\": \"ecg_2\",\"t\": \"double\",\"u\": \"mV\",\"v\": -0.38514},{\"n\": \"acc_ankle_x\",\"t\": \"double\",\"u\": \"m/s^2\",\"v\": 2.2135},{\"n\": \"acc_ankle_y\",\"t\": \"double\",\"u\": \"m/s^2\",\"v\": -9.6887},{\"n\": \"acc_ankle_z\",\"t\": \"double\",\"u\": \"m/s^2\",\"v\": 0.43353},{\"n\": \"gyro_ankle_x\",\"t\": \"double\",\"u\": \"deg/s\",\"v\": 0.076067},{\"n\": \"gyro_ankle_y\",\"t\": \"double\",\"u\": \"deg/s\",\"v\": -0.83114},{\"n\": \"gyro_ankle_z\",\"t\": \"double\",\"u\": \"deg/s\",\"v\": -0.69155},{\"n\": \"mag_ankle_x\",\"t\": \"double\",\"u\": \"tesla\",\"v\": -0.73095},{\"n\": \"mag_ankle_y\",\"t\": \"double\",\"u\": \"tesla\",\"v\": 0.55777},{\"n\": \"mag_ankle_z\",\"t\": \"double\",\"u\": \"tesla\",\"v\": 0.29764},{\"n\": \"acc_arm_x\",\"t\": \"double\",\"u\": \"m/s^2\",\"v\": -8.8318},{\"n\": \"acc_arm_y\",\"t\": \"double\",\"u\": \"m/s^2\",\"v\": -4.109},{\"n\": \"acc_arm_z\",\"t\": \"double\",\"u\": \"m/s^2\",\"v\": 0.096632},{\"n\": \"gyro_arm_x\",\"t\": \"double\",\"u\": \"deg/s\",\"v\": -0.42745},{\"n\": \"gyro_arm_y\",\"t\": \"double\",\"u\": \"deg/s\",\"v\": -1.0164},{\"n\": \"gyro_arm_z\",\"t\": \"double\",\"u\": \"deg/s\",\"v\": 0.019397},{\"n\": \"mag_arm_x\",\"t\": \"double\",\"u\": \"tesla\",\"v\": -0.19549},{\"n\": \"mag_arm_y\",\"t\": \"double\",\"u\": \"tesla\",\"v\": -1.5952},{\"n\": \"mag_arm_z\",\"t\": \"double\",\"u\": \"tesla\",\"v\": 3.6222},{\"n\": \"label\",\"t\": \"int\",\"v\": 0}]";
        String serializedSenml = serializeSenml(senml);
        System.out.println(serializedSenml);
    }
}