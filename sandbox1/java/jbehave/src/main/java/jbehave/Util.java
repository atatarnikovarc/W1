package jbehave;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;

public class Util
{
    public static String executeSSHCommand(String host, String user, String passwd, String command) {
        try{
            JSch jsch=new JSch();
            Session session=jsch.getSession(user, host, 22);
            session.setPassword(passwd);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(30000);

            Channel channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);

            InputStream in=channel.getInputStream();

            channel.connect();

            byte[] tmp=new byte[1024];
            StringBuffer response = new StringBuffer();
            while(true){
                while(in.available()>0){
                    int i=in.read(tmp, 0, 1024);
                    if(i<0)break;
                    String btmp = new String(tmp, 0, i);
                    response.append(btmp);
                }
                if(channel.isClosed()){
                    if(in.available()>0) continue;
                    System.out.println("exit-status: "+channel.getExitStatus());
                    break;
                }
                try{Thread.sleep(1000);}catch(Exception ee){}
            }

            channel.disconnect();
            session.disconnect();
            return response.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
