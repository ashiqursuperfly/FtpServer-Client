package shared;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class LsData implements Serializable{

    private static final long serialVersionUID = 103117L;
    ArrayList<String> data;

    public LsData(ArrayList<String> data) {
        this.data = data;
    }

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        data = data;
    }
}
