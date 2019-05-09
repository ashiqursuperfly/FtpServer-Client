package shared;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class LsData implements Serializable{

    ArrayList<String> data;

    private static final long serialVersionUID = 103117L;


    public LsData(ArrayList<String> data) {
        this.data = data;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        data = data;
    }
}
