package azhar.com.quicksale.model;

/**
 * Created by azharuddin on 24/7/17.
 */

public class CustomerModel {
    private String key, name, address, gst;

    public CustomerModel(String key, String name, String address, String gst) {
        this.key = key;
        this.name = name;
        this.address = address;
        this.gst = gst;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getGst() {
        return gst;
    }
}
