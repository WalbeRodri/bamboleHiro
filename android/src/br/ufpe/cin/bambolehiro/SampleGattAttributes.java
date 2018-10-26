package br.ufpe.cin.bambolehiro;

import java.util.HashMap;
/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    // public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    // public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    // Bambole Services
    public static String BAMBOLE_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String BAMBOLE_DATA = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String SERVICE_BT_NAME = "00001800-0000-1000-8000-00805f9b34fb";
    // public static String BT_ADDRESS = "00:15:83:00:CA:AD";

    static {
        // Sample Services.
        attributes.put(BAMBOLE_SERVICE, "Bambole Service");
        attributes.put(SERVICE_BT_NAME, "Device Name");
        attributes.put(BAMBOLE_DATA, "Bambole BT Data");
    }
    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
