package shared;

import java.io.Serializable;

public class InfoData implements Serializable{

    private static final long serialVersionUID = 103116L;

    String information;



    public InfoData(String information) {
        this.information = information;
    }



    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }
}
