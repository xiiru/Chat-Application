import com.github.sarxos.webcam.Webcam;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class VideoChatClient
{
    static Socket socket;

    public static  void main(String[] args) throws IOException, ClassNotFoundException
    {

        streamVideoClient();

    }

    public static JFrame streamVideoClient() throws IOException, ClassNotFoundException
    {
        //Socket socket;
        Webcam webcam = Webcam.getDefault();
        webcam.open();
        socket = new Socket("127.0.0.1", 1200);
        BufferedImage br = webcam.getImage();
        ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
        ImageIcon ic = new ImageIcon(br);

        JFrame videoChatFrame = new JFrame("Client");
        videoChatFrame.setSize(450, 700);
        videoChatFrame.setLocation(750, 100);
        videoChatFrame.setDefaultCloseOperation(videoChatFrame.EXIT_ON_CLOSE);
        JLabel videoChatLabel = new JLabel();
        videoChatLabel.setSize(450, 700);
        videoChatLabel.setVisible(true);
        videoChatFrame.add(videoChatLabel);
        videoChatFrame.setVisible(true);

        while(true)
        {
            br = webcam.getImage();
            Image ibr = br.getScaledInstance(videoChatLabel.getWidth(), videoChatLabel.getHeight(),
                    Image.SCALE_SMOOTH);
            ic = new ImageIcon(ibr);
            objOut.writeObject(ic);
            videoChatLabel.setIcon(ic);
            objOut.flush();
        }
    }
}
