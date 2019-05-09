package shared;

import java.io.Serializable;

public class FileData implements Serializable {

    byte[] dataArray;
    String errorMessage = "";
    String filename;
    int bytesRead;
    boolean EOF;

    private static final long serialVersionUID = 103115L;

    public FileData(byte[] dataArray, String errorMessage, String filename, int bytesRead, boolean EOF) {
        this.dataArray = dataArray;
        this.errorMessage = errorMessage;
        this.filename = filename;
        this.bytesRead = bytesRead;
        this.EOF = EOF;
    }

    public byte[] getDataArray() {
        return dataArray;
    }

    public void setDataArray(byte[] dataArray) {
        this.dataArray = dataArray;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getBytesRead() {
        return bytesRead;
    }

    public void setBytesRead(int bytesRead) {
        this.bytesRead = bytesRead;
    }

    public boolean isEOF() {
        return EOF;
    }

    public void setEOF(boolean EOF) {
        this.EOF = EOF;
    }
}
