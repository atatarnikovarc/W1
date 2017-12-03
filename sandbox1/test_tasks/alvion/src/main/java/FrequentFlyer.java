/**
 * Created by atatarnikov on 02.07.17.
 */
public class FrequentFlyer {

    public FrequentFlyer() {}

    public static FrequentFlyer withInitialBalanceOf(float balance) {
        return new FrequentFlyer();
    }

    public Status getStatus() { return null; }

    public float getBalance() { return 0; }

    public FrequentFlyer flies(int i) { return new FrequentFlyer(); }

    public void kilometers() {}
}
