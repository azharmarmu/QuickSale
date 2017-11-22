package azhar.com.quicksale.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by azharuddin on 24/7/17.
 */

public class BillModel implements Serializable {

    private String name;
    private HashMap<String, Object> billMap;

    public BillModel(String name, HashMap<String, Object> billMap) {
        this.name = name;
        this.billMap = billMap;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Object> getBillMap() {
        return billMap;
    }
}
