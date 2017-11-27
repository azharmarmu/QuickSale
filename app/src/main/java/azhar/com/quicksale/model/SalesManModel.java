package azhar.com.quicksale.model;

/**
 * Created by azharuddin on 24/7/17.
 */

public class SalesManModel {
    private String name, phone;

    public SalesManModel(String phone, String name) {
        this.phone = phone;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
