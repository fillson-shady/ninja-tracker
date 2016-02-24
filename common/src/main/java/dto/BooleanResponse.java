package dto;

public class BooleanResponse {

    public BooleanResponse(boolean value) {
        this.value = value;
    }

    private boolean value;

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
