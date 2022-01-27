import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class VideoChatServer
{
    public static  void main(String[] args) throws IOException, ClassNotFoundException
    {

        streamVideoServer();
    }


    public static JFrame streamVideoServer() throws IOException, ClassNotFoundException
    {
        ServerSocket server = new ServerSocket(1200);
        System.out.println("waiting...");
        Socket socket = server.accept();
        System.out.println("connected");
        ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());



        JLabel videoChatLabel = new JLabel();
        JFrame videoChatFrame = new JFrame("Server");
        videoChatFrame.setSize(450, 700);
        videoChatFrame.setLocation(250, 100);
        videoChatFrame.setDefaultCloseOperation(videoChatFrame.EXIT_ON_CLOSE);
        videoChatLabel = new JLabel();
        videoChatLabel.setSize(450, 700);
        videoChatLabel.setVisible(true);
        videoChatFrame.add(videoChatLabel);
        videoChatFrame.setVisible(true);


        while(true)
        {
            videoChatLabel.setIcon((ImageIcon)objIn.readObject());
        }
    }
}
