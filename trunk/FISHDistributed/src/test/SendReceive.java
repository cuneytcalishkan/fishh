package test;

import java.net.*;

public class SendReceive {

    String group = "224.17.17.17";
    int port = 4311;

    public SendReceive(String[] args) {
        if (args.length > 0) {
            group = args[0];
        }
        try {
            if (args.length > 1) {
                Integer.parseInt(args[1]);
            }
        } catch (NumberFormatException e) {
            System.out.println("Usage: java SendReceive [host] [group]");
            System.exit(0);
        }
        try {
            MulticastSocket socket = new MulticastSocket(port);
            socket.joinGroup(InetAddress.getByName(group));
            byte[] buf = "Hello".getBytes();
            DatagramPacket d = new DatagramPacket(buf, buf.length,
                    InetAddress.getByName(group),
                    port);
            System.out.println(new String(buf));
            socket.send(d);
            buf = new byte[128];
            for (int i = buf.length - 1; i >= 0; i--) {
                buf[i] = 0;
            }
            d = new DatagramPacket(buf, buf.length);
            while (true) {
                socket.receive(d);
                System.out.println("source: " + d.getAddress()
                        + "; port: " + d.getPort() + "; data: "
                        + new String(d.getData()).trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SendReceive sendreceive = new SendReceive(args);
    }
}
