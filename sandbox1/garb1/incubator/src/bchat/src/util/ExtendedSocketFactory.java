package bchat.src.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class ExtendedSocketFactory {
    private byte[] byteSocketAddress;
    private InetAddress inetSocketAddress;
    private InetSocketAddress bindSocketAddress;
    private StringTokenizer addressTokenizer;
    private String ipAddressDelimiters;
    private byte ipTriad;
    private Socket socket;
    private ServerSocket serverSocket;

    public ExtendedSocketFactory() {
        byteSocketAddress = new byte[4];
        ipAddressDelimiters = ".";
    }

    protected byte stringToByte(String number) {
        int length = number.length();
        byte[] byteTriad = new byte[length];
        int triadStorage;
        byte byteStorage = 0;

        byteTriad = number.getBytes();
        if (length == 3) {
            triadStorage = (byteTriad[0] - 48) * 100 + (byteTriad[1] - 48) * 10 +
                    (byteTriad[2] - 48);
            byteStorage = (byte) triadStorage;
            return byteStorage;
        } else if (length == 2) {
            triadStorage = (byteTriad[0] - 48) * 10 + (byteTriad[1] - 48);
            byteStorage = (byte) triadStorage;
            return byteStorage;
        } else if (length == 1) {
            triadStorage = (byteTriad[0] - 48);
            byteStorage = (byte) triadStorage;
            return byteStorage;
        }
        return byteStorage;
    }

    public ServerSocket createServerSocket(String hostIP, int port) {
        addressTokenizer = new StringTokenizer(hostIP, ipAddressDelimiters);
        for (int i = 0; i < 4; i++) {
            ipTriad = stringToByte(addressTokenizer.nextToken());
            byteSocketAddress[i] = ipTriad;
        }
        try {
            inetSocketAddress = InetAddress.getByAddress(byteSocketAddress);
            serverSocket = new ServerSocket();
            bindSocketAddress = new InetSocketAddress(inetSocketAddress, port);
            serverSocket.bind(bindSocketAddress, 50);
        } catch (IOException ie) {
            System.err.println("Could not listen on port: " + port);
            System.exit(1);
        }
        return serverSocket;
    }

    public Socket createSocket(String hostIP, int port) {
        addressTokenizer = new StringTokenizer(hostIP, ipAddressDelimiters);
        for (int i = 0; i < 4; i++) {
            ipTriad = stringToByte(addressTokenizer.nextToken());
            byteSocketAddress[i] = ipTriad;
        }
        try {
            inetSocketAddress = InetAddress.getByAddress(byteSocketAddress);
            socket = new Socket(inetSocketAddress, port);
        } catch (IOException ie) {
            System.err.println("Could not listen on port:" + port);
            System.exit(1);
        } /*catch (UnknownHostException uhe) {
      System.out.println("Unknown host!!");
	  System.exit(0);
	}*/
        return socket;
    }
}
