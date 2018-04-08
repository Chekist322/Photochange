package photochange.tosya.photochange.model;

public class RegisterModel {
    private long timeInMillisRegister;
    private String zone;

    RegisterModel() {

    }

    public RegisterModel(long timeInMillisRegister, String zone) {
        this.timeInMillisRegister = timeInMillisRegister;
        this.zone = zone;
    }

    public long getTimeInMillisRegister() {
        return timeInMillisRegister;
    }

    public void setTimeInMillisRegister(long timeInMillisRegister) {
        this.timeInMillisRegister = timeInMillisRegister;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }
}
