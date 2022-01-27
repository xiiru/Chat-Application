import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import javax.sound.sampled.*;
import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;



public class Client implements ActionListener
{

    //Swing components
    static JFrame chatWindow = new JFrame();
    JPanel header;
    JTextArea typeArea;
    JButton sendButton;
    static JPanel messageSpace;
    static ServerSocket serverSocket;
    static Socket socket;
    static DataInputStream dataIn;
    static DataOutputStream dataOut;
    Boolean typing;
    static Box vertical = Box.createVerticalBox();
    final File[] fileToSend = new File[1];
    static ArrayList<MyFile> myFiles = new ArrayList<>();
    static int fileId = 0;


    //Constructor
    Client()
    {
        //CREATING HEADER
        header = new JPanel();
        //nullify system layout
        header.setLayout(null);
        //set RGB colour code
        header.setBackground(new Color(0, 0, 0));
        //set position of header within window
        header.setBounds(0, 0, 450, 70);
        //add element on frame
        chatWindow.add(header);


        //SETTING PROFILE PICTURE OF CLIENTS
        //get image from local disk
        ImageIcon clientIconIntermediate = new ImageIcon(ClassLoader.getSystemResource("chatApplicationIcons/clientIcon.png"));
        //set size of image icon
        Image clientIconSize = clientIconIntermediate.getImage().getScaledInstance(60, 60, Image.SCALE_DEFAULT);
        ImageIcon clientIcon = new ImageIcon(clientIconSize);
        //set image in label
        JLabel clientIconLabel = new JLabel(clientIcon);
        //create custom layout
        clientIconLabel.setBounds(20, 5, 60, 60);
        //add element on frame
        header.add(clientIconLabel);
        clientIconLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                //set frame to view profile picture
                JFrame pfpFrame = new JFrame("Client Profile Picture");
                //set size of frame
                pfpFrame.setSize(350, 350);
                //get image from local disk
                ImageIcon clientIconIntermediate = new ImageIcon(ClassLoader.getSystemResource("chatApplicationIcons/clientIcon.png"));
                //set size of image icon
                Image clientIconSize = clientIconIntermediate.getImage().getScaledInstance(300, 300, Image.SCALE_DEFAULT);
                //set rescaled image to new icon
                ImageIcon clientIcon = new ImageIcon(clientIconSize);
                //create label to hold icon
                JLabel pfpLabel = new JLabel(clientIcon);
                //set size of label
                pfpLabel.setSize(350, 350);
                //set pfpFrame as visible
                pfpFrame.setVisible(true);
                //set pfpLabel as visible
                pfpLabel.setVisible(true);
                //add pfpLabel to pfpFrame
                pfpFrame.add(pfpLabel);
            }
        });


        //SETTING SERVER PROFILE NAME
        JLabel name = new JLabel("Client");
        //set position of name
        name.setBounds(90, 15, 100, 18);
        //set font
        name.setFont(new Font("SANS SERIF", Font.BOLD, 18));
        //set font colour/foreground
        name.setForeground(Color.WHITE);
        //add element on frame
        header.add(name);


        //SETTING ACTIVITY STATUS
        JLabel activityStatus = new JLabel("Active");
        //set position of name
        activityStatus.setBounds(90, 35, 100, 20);
        //set font
        activityStatus.setFont(new Font("SANS SERIF", Font.PLAIN, 15));
        //set font colour/foreground
        activityStatus.setForeground(Color.WHITE);
        //add element on frame
        header.add(activityStatus);
        //set activity status as "Active now"
        Timer t = new Timer(1, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                if(!typing)
                {
                    //if user is not typing set activity status as "Active now"
                    activityStatus.setText("Active now");
                }
            }
        });
        //change activity status with a delay of 1 second
        t.setInitialDelay(1000);


        //SETTING EXIT ICON OF SERVER
        //get image from local disk
        ImageIcon exitIconIntermediate = new ImageIcon(ClassLoader.getSystemResource("chatApplicationIcons/exitIcon.png"));
        //set size of image icon
        Image exitIconSize = exitIconIntermediate.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT);
        ImageIcon exitIcon = new ImageIcon(exitIconSize);
        //set image in label
        JButton exitIconButton = new JButton(exitIcon);
        //create custom layout
        exitIconButton.setBounds(390, 20, 25, 25);
        //set background colour of button
        exitIconButton.setBackground(new Color(0,0,0));
        //set button borders to null to remove border
        exitIconButton.setBorder(null);
        //add element on frame
        header.add(exitIconButton);
        //close chat window when more icon is clicked
        exitIconButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent ae)
            {
                System.exit(0);
            }
        });


        //SETTING LINK FILE ICON OF SERVER
        //get image from local disk
        ImageIcon linkIconIntermediate = new ImageIcon(ClassLoader.getSystemResource("chatApplicationIcons/linkIcon.png"));
        //set size of image icon
        Image linkIconSize = linkIconIntermediate.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT);
        ImageIcon linkIcon = new ImageIcon(linkIconSize);
        //set image in label
        JButton linkIconButton = new JButton(linkIcon);
        //create custom layout
        linkIconButton.setBounds(340, 20, 25, 25);
        //change background colour
        linkIconButton.setBackground(new Color(0,0,0));
        //set button borders to null to remove border
        linkIconButton.setBorder(null);
        //add element on frame
        header.add(linkIconButton);
        //close window when more icon is clicked
        linkIconButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //create file chooser to open dialog box to choose file
                JFileChooser linkFile = new JFileChooser();
                //set title of dialog box
                linkFile.setDialogTitle("Choose file to send");
                //show dialog box if file is chosen
                if(linkFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                {
                    //get selected file
                    fileToSend[0] = linkFile.getSelectedFile();
                    //typeArea.setText(fileToSend[0].getName());
                    typeArea.setText("File Sent! Click to preview and download file!");
                }
                try
                {
                    //create input stream into the file you want to send
                    FileInputStream fileIn = new FileInputStream(fileToSend[0].getAbsolutePath());
                    //create socket connection to connect with server
                    Socket socket = new Socket("127.0.0.1", 1234);
                    //create output stream to write to the server over the socket connection
                    DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
                    //get the name of the file you want to send and store in filename
                    String fileName = fileToSend[0].getName();
                    //convert the name of the file into an array of bytes to be sent to server
                    byte[] fileNameBytes = fileName.getBytes();
                    //create a byte array the size of the file so don't send too little or too much data to the server
                    byte[] fileBytes = new byte[(int)fileToSend[0].length()];
                    //put the contents of the file into the array of bytes to be sent so these bytes can be sent to server
                    fileIn.read(fileBytes);
                    //send the length of name of the file so server knows when to stop reading
                    dataOut.writeInt(fileNameBytes.length);
                    //send the file name
                    dataOut.write(fileNameBytes);
                    //send the length of the byte array so the server knows when to stop reading
                    dataOut.writeInt(fileBytes.length);
                    //send the actual file
                    dataOut.write(fileBytes);
                }
                catch(Exception le)
                {}
            }
        });


        //SETTING TEXT TO SPEECH ICON OF SERVER
        //get image from local disk
        ImageIcon textToSpeechIconIntermediate = new ImageIcon(ClassLoader.getSystemResource("chatApplicationIcons/textToSpeechIcon.png"));
        //set size of image icon
        Image textToSpeechIconSize = textToSpeechIconIntermediate.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT);
        ImageIcon textToSpeechIcon = new ImageIcon(textToSpeechIconSize);
        //set image in label
        JButton textToSpeechIconButton = new JButton(textToSpeechIcon);
        //create custom layout
        textToSpeechIconButton.setBounds(290, 20, 25, 25);
        //set background colour for textTOSpeechButton
        textToSpeechIconButton.setBackground(new Color(0,0,0));
        //set button borders to null to remove border
        textToSpeechIconButton.setBorder(null);
        //add element on frame
        header.add(textToSpeechIconButton);
        textToSpeechIconButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                try
                {
                    // Set property as Kevin Dictionary
                    System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us" + ".cmu_us_kal.KevinVoiceDirectory");
                    // Register Engine
                    Central.registerEngineCentral("com.sun.speech.freetts" + ".jsapi.FreeTTSEngineCentral");
                    // Create a Synthesizer
                    Synthesizer synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));
                    // Allocate synthesizer
                    synthesizer.allocate();
                    // Resume Synthesizer
                    synthesizer.resume();
                    // Speaks the given text
                    synthesizer.speakPlainText(typeArea.getText(), null);
                    // until the queue is empty.
                    synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
                    // Deallocate the Synthesizer.
                    synthesizer.deallocate();
                }
                catch (Exception e)
                {}
            }
        });


        //SET RECORD BUTTON OF CLIENT
        ImageIcon recordIconIntermediate = new ImageIcon(ClassLoader.getSystemResource("chatApplicationIcons/recordIcon.png"));
        //set size of image icon
        Image recordIconSize = recordIconIntermediate.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT);
        ImageIcon recordIcon = new ImageIcon(recordIconSize);
        //set image in label
        JButton recordIconButton = new JButton(recordIcon);
        //create custom layout
        recordIconButton.setBounds(240, 20, 25, 25);
        recordIconButton.setBackground(new Color(0,0,0));
        //set button borders to null to remove border
        recordIconButton.setBorder(null);
        //add element on frame
        header.add(recordIconButton);
        recordIconButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                try
                {
                    AudioFormat format = new AudioFormat(16000, 8, 2, true, true);
                    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                    if (!AudioSystem.isLineSupported(info)) {
                        System.out.println("Line is not supported");
                    }
                    final TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
                    targetDataLine.open();
                    System.out.println("Start recording");
                    activityStatus.setText("Recording...");
                    targetDataLine.start();

                    Thread stopper = new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            AudioInputStream audioStream = new AudioInputStream(targetDataLine);
                            File wavFile = new File("C:\\Users\\User\\IdeaProjects\\ChatApplication\\AudioRecordings\\recording");
                            try {
                                AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, wavFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    stopper.start();
                    Thread.sleep(10000);
                    targetDataLine.stop();
                    targetDataLine.close();
                    System.out.println("Recorded");
                    activityStatus.setText("Active now");
                }
                catch(Exception e)
                {}
            }
        });




        //SETTING TEXT AREA TO TYPE MESSAGE
        typeArea = new JTextArea();
        //set position of text area
        typeArea.setBounds(5, 655, 310, 40);
        //set font
        typeArea.setFont(new Font("SANS SERIF", Font.PLAIN, 16));
        //add text area to chat window
        chatWindow.add(typeArea);
        //if typeArea is used set activity status to "typing..."
        typeArea.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent ke)
            {
                //if key pressed for typing message change activity status to "typing..."
                activityStatus.setText("Typing...");
                t.stop();
                typing = true;
            }

            @Override
            public void keyReleased(KeyEvent ke)
            {
                typing = false;
                if(!t.isRunning())
                {
                    t.start();
                }
            }
        });


        //SETTING SEND BUTTON TO SEND MESSAGE
        sendButton = new JButton("Send");
        //set position of button on chat window
        sendButton.setBounds(320, 655, 123, 40);
        //set send button background colour
        sendButton.setBackground(new Color(0, 0, 0));
        //set send button foreground colour
        sendButton.setForeground(Color.WHITE);
        //set font
        sendButton.setFont(new Font("SANS SERIF", Font.PLAIN, 16));
        //set button borders to null to remove border
        sendButton.setBorder(null);
        //append message to messageSpace once sendButton is clicked
        sendButton.addActionListener(this::actionPerformed);
        //add send button to chat window
        chatWindow.add(sendButton);


        //CREATE SPACE FOR MESSAGES TO APPEAR ON CHAT WINDOW
        messageSpace = new JPanel();
        //set font
        messageSpace.setFont(new Font("SANS SERIF", Font.PLAIN, 16));


        //CREATE SCROLLBAR FOR CHAT WINDOW
        JScrollPane scrollBar = new JScrollPane(messageSpace);
        //set position of scrollbar on chat window
        scrollBar.setBounds(5, 75, 440, 570);
        //change scrollbar to empty border
        scrollBar.setBorder(BorderFactory.createEmptyBorder());
        chatWindow.add(scrollBar);
        //change styles of scrollbar
        ScrollBarUI ui = new BasicScrollBarUI()
        {
            //change style for lower arrow of scrollbar
            protected JButton createDecreaseButton(int orientation)
            {
                JButton button = super.createDecreaseButton(orientation);
                //set background colour
                button.setBackground(new Color(0,0,0));
                //set foreground colour
                button.setForeground(Color.WHITE);
                //set thumb colour
                this.thumbColor = new Color(0,0,0);
                return button;
            }
            //change style for upper arrow of scrollbar
            protected JButton createIncreaseButton(int orientation)
            {
                JButton button = super.createIncreaseButton(orientation);
                //set background colour
                button.setBackground(new Color(0,0,0));
                //set foreground colour
                button.setForeground(Color.WHITE);
                //set thumb colour
                this.thumbColor = new Color(0,0,0);
                return button;
            }
        };
        scrollBar.getVerticalScrollBar().setUI(ui);


        //FORMATTING CHAT WINDOW - JFRAME
        //nullify system layouts
        chatWindow.setLayout(null);
        chatWindow.setSize(450, 700);
        //set location on screen
        chatWindow.setLocation(750, 100);
        //set background colour of chat window
        chatWindow.getContentPane().setBackground(Color.WHITE);
        //set title bar as false
        chatWindow.setUndecorated(true);
        //default false
        chatWindow.setVisible(true);


    }


    //TRIGGER EVENT WHEN sendButton IS CLICKED
    public void  actionPerformed(ActionEvent ae)
    {
        try
        {
            //get message typed in typeArea
            String message = typeArea.getText();
            //call method to save texts in text file(.txt)
            saveTextsToFile(message);
            //send to formatLabel function to format the label displaying messages
            JPanel messageBackground = formatLabel(message);
            messageSpace.setLayout(new BorderLayout());
            //set user output messages to right
            JPanel rightSide = new JPanel(new BorderLayout());
            //set messagePanel to right side
            rightSide.add(messageBackground, BorderLayout.LINE_END);
            //add vertical flow to message space
            vertical.add(rightSide);
            vertical.add(Box.createVerticalStrut(15));
            //add right to vertical flow
            messageSpace.add(vertical, BorderLayout.PAGE_START);
            dataOut.writeUTF(message);
            typeArea.setText("");
        }
        catch (Exception e)
        {}
    }


    //FUNCTION TO SAVE TEXTS IN A FILE - AUTO APPENDING
    public void saveTextsToFile(String texts)
    {

            try (FileWriter f = new FileWriter("ChatLog.txt", true);
                 PrintWriter p = new PrintWriter(new BufferedWriter(f));) {
                p.println("Client: " + texts);
            } catch (Exception e) {
            }
    }


    //FUNCTION TO FORMAT MESSAGE INTO SPEECH BUBBLE AND ALIGN TO CHAT WINDOW
    public static JPanel formatLabel(String message) throws IOException, ClassNotFoundException
    {
        //create JPanel to be background for displaying messages
        JPanel messagePanel = new JPanel();
        //set layout
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        //create speech bubble
        JLabel speechBubble = new JLabel("<html><p style =\"width: 150px\">"+message+"</p></html>");
        //set font
        speechBubble.setFont(new Font("SANS SERIF", Font.PLAIN, 16));
        //set speech bubble background colour
        speechBubble.setBackground(new Color(148, 204, 102));
        //set speechBubble to visible
        speechBubble.setOpaque(true);
        //set empty border around speech bubble
        speechBubble.setBorder(new EmptyBorder(15,15,15,50));

        //SETTING TIME STAMP
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        JLabel timeStamp = new JLabel();
        timeStamp.setText(sdf.format(cal.getTime()));
        //add speechBubble to messagePanel
        messagePanel.add(speechBubble);
        //add timeStamp to messagePanel
        messagePanel.add(timeStamp);
        return messagePanel;
    }


    //FUNCTION TO GET FILE EXTENSION
    public static String getFileExtension(String fileName)
    {
        int i = fileName.lastIndexOf('.');
        if(i > 0)
        {
            return fileName.substring(i+1);
        }
        else
        {
            return "No extension found";
        }
    }


    public static MouseListener getMyMouseListener()
    {
        return new MouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                JPanel panel = (JPanel) e.getSource();
                int fileId = Integer.parseInt(panel.getName());
                for(MyFile myFile : myFiles)
                {
                    if(myFile.getId() == fileId)
                    {
                        JFrame previewWindow = createFrame(myFile.getName(), myFile.getData(), myFile.getFileExtension());
                        previewWindow.setVisible(true);
                    }
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        };
    }


    //CREATE PREVIEW/DOWNLOAD FRAME
    public static JFrame createFrame(String fileName, byte[] fileData, String fileExtension)
    {
        //create and set name for frame
        JFrame downloadFrame = new JFrame("Downloader");
        //set size for frame
        downloadFrame.setSize(400, 400);
        //create panel to hold swing components
        JPanel downloadPanel = new JPanel();
        //set box layout
        downloadPanel.setLayout(new BoxLayout(downloadPanel, BoxLayout.Y_AXIS));
        //create label
        JLabel downloadTitle = new JLabel("Download files");
        //set alignment of label
        downloadTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        //set font of the label
        downloadTitle.setFont(new Font("SANS SERIF", Font.BOLD, 25));
        //set location of label
        downloadTitle.setBorder(new EmptyBorder(20, 0, 10, 0));

        //create prompt label to ask user if they want to download file
        JLabel prompt = new JLabel("Are you sure you want to download " + fileName + "?");
        //set font of label
        prompt.setFont(new Font("SANS SERIF", Font.PLAIN, 20));
        //set location of label
        prompt.setBorder(new EmptyBorder(20, 0, 20, 0));
        //set alignment of label
        prompt.setAlignmentX(Component.CENTER_ALIGNMENT);

        //button option to download : YES/NO
        JButton yesOption = new JButton("Yes");
        yesOption.setPreferredSize(new Dimension(150, 75));
        //set button borders to null to remove border
        yesOption.setBorder(null);
        yesOption.setBackground(new Color(255, 255, 255));
        yesOption.setFont(new Font("SANS SERIF", Font.PLAIN, 20));

        JButton noOption = new JButton("No");
        noOption.setPreferredSize(new Dimension(150, 75));
        //set button borders to null to remove border
        noOption.setBorder(null);
        noOption.setBackground(new Color(255, 255, 255));
        noOption.setFont(new Font("SANS SERIF", Font.PLAIN, 20));

        //label to hold preview of file
        JLabel fileContent = new JLabel();
        //set alignment of label
        fileContent.setAlignmentX(Component.CENTER_ALIGNMENT);

        //panel to hold option buttons
        JPanel options = new JPanel();
        //set location of panel
        options.setBorder(new EmptyBorder(20, 0, 10, 0));
        //add yes option to panel
        options.add(yesOption);
        //add no option to panel
        options.add(noOption);

        //if file is a text file display its contents
        if(fileExtension.equalsIgnoreCase("txt"))
        {
            fileContent.setText("<html>" + new String(fileData) + "</html>");
        }
        //if not text file make it an image
        else
        {
            fileContent.setIcon(new ImageIcon(fileData));
        }
        //Yes option to download file
        yesOption.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                //create file with its name
                File fileToDownload = new File(fileName);
                try
                {
                    //create a stream to write data to file
                    FileOutputStream fileOut = new FileOutputStream(fileToDownload);
                    //write the actual file data to the file
                    fileOut.write(fileData);
                    //close stream
                    fileOut.close();
                    //close frame after user chooses option
                    downloadFrame.dispose();
                }
                catch(Exception e)
                {}
            }
        });
        //No option to not download file
        noOption.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //close frame after user chooses option
                downloadFrame.dispose();
            }
        });
        //add swing components to frame
        downloadPanel.add(downloadTitle);
        downloadPanel.add(prompt);
        downloadPanel.add(fileContent);
        downloadPanel.add(options);
        downloadFrame.add(downloadPanel);
        return downloadFrame;
    }


    //METHOD TO SHOW FILES ON CHAT WINDOW
    public static void showFilesClient() throws IOException, ClassNotFoundException
    {
        Socket filesocket = serverSocket.accept();
        //stream to receive data from client
        DataInputStream dataIn = new DataInputStream(filesocket.getInputStream());
        //read size of file name to know when to stop reading
        int fileNameLength = dataIn.readInt();
        //if file exists
        if (fileNameLength > 0) {
            //Byte array to hold name of file
            byte[] fileNameBytes = new byte[fileNameLength];
            //read from the input stream into the byte array
            dataIn.readFully(fileNameBytes, 0, fileNameBytes.length);
            //create file name from the byte array
            String fileName = new String(fileNameBytes);
            //read how much data to expect for the actual content of the file
            int fileContentLength = dataIn.readInt();
            //if file exists
            if (fileContentLength > 0) {
                //array to hold file data
                byte[] fileContentBytes = new byte[fileContentLength];
                //read from the input stream into fileContentBytes array
                dataIn.readFully(fileContentBytes, 0, fileContentBytes.length);
                //panel to hold the picture and file name
                JPanel filePanel = new JPanel();
                //set layout
                filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.X_AXIS));
                //set file name
                JLabel fileNameLabel = new JLabel(fileName);
                //set font
                fileNameLabel.setFont(new Font("SANS SERIF", Font.PLAIN, 16));
                //set location of label
                fileNameLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
                if (getFileExtension(fileName).equalsIgnoreCase("txt")) {
                    filePanel.setName((String.valueOf(fileId)));
                    //filePanel = formatLabel(fileName);
                    filePanel.addMouseListener(getMyMouseListener());
                    filePanel.add(fileNameLabel);
                    //set speech bubble background colour
                    fileNameLabel.setBackground(new Color(247, 175, 136));
                    //set speechBubble to visible
                    fileNameLabel.setOpaque(true);
                    //set empty border around speech bubble
                    fileNameLabel.setBorder(new EmptyBorder(15, 15, 15, 50));
                    //set user output messages to right
                    JPanel rightSide = new JPanel(new BorderLayout());
                    //set messagePanel to right side
                    rightSide.add(filePanel, BorderLayout.LINE_END);
                    //add vertical flow to message space
                    vertical.add(rightSide);
                    vertical.add(Box.createVerticalStrut(15));
                    //add right to vertical flow
                    messageSpace.add(vertical, BorderLayout.PAGE_START);
                    messageSpace.add(filePanel);
                    JPanel left = new JPanel(new BorderLayout());
                    left.add(filePanel, BorderLayout.LINE_START);
                    vertical.add(left);
                    //vertical.add(messageBackground);
                    chatWindow.validate();
                } else {
                    filePanel.setName((String.valueOf(fileId)));
                    //filePanel = formatLabel(fileName);
                    filePanel.addMouseListener(getMyMouseListener());
                    filePanel.add(fileNameLabel);
                    //set speech bubble background colour
                    fileNameLabel.setBackground(new Color(247, 175, 136));
                    //set speechBubble to visible
                    fileNameLabel.setOpaque(true);
                    //set empty border around speech bubble
                    fileNameLabel.setBorder(new EmptyBorder(15, 15, 15, 50));
                    //set user output messages to right
                    JPanel rightSide = new JPanel(new BorderLayout());
                    //set messagePanel to right side
                    rightSide.add(filePanel, BorderLayout.LINE_END);
                    //add vertical flow to message space
                    vertical.add(rightSide);
                    //add right to vertical flow
                    messageSpace.add(vertical, BorderLayout.PAGE_START);
                    messageSpace.add(filePanel);
                    //vertical.add(messageBackground);
                    JPanel left = new JPanel(new BorderLayout());
                    left.add(filePanel, BorderLayout.LINE_START);
                    vertical.add(left);
                    vertical.add(Box.createVerticalStrut(15));
                    chatWindow.validate();
                }
                myFiles.add(new MyFile(fileId, fileName, fileContentBytes, getFileExtension(fileName)));
                fileId++;
            }
        }
    }


    //MAIN
    public static void main(String[] args) throws IOException, ClassNotFoundException
    {
        //creating object
        new Client().chatWindow.setVisible(true);

        socket = new Socket("127.0.0.1", 1234);
        dataIn = new DataInputStream(socket.getInputStream());
        dataOut = new DataOutputStream(socket.getOutputStream());
        serverSocket = new ServerSocket(4321);

        //string to hold input message
        String messageInput = "";


        while(true)
        {

            while (true)
            {
                messageSpace.setLayout(new BorderLayout());
                messageInput = dataIn.readUTF();
                JPanel messageBackground = formatLabel(messageInput);
                JPanel leftSide = new JPanel(new BorderLayout());
                leftSide.add(messageBackground, BorderLayout.LINE_START);
                vertical.add(leftSide);
                //method called to show message on panel
                vertical.add(Box.createVerticalStrut(15));
                messageSpace.add(vertical, BorderLayout.PAGE_START);
                chatWindow.validate();

                showFilesClient();
            }
        }
    }
}