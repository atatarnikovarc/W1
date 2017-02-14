package bchat.src.model;

public class ClientInfo {
    private String nickName;

    public ClientInfo(String name, String ipAddr) {
        nickName = name;
        String ip = ipAddr;
    }

    public String getName() {
        return nickName;
    }
}