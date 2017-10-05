import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JPanel implements Runnable, ActionListener
{
	private static final long serialVersionUID = 1L;
	
	// GUI components for the chat window
	private final JTextField txtEnterField = new JTextField();
    private final JTextArea txtChatArea = new JTextArea();

    // The socket connecting us to the server
    private Socket socket;

    // The streams we use to communicate with the server.
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;

    // Constructor
    public Client(final String host, final int port)
    {
    	// Tell the chat entry field to use this class
    	// as its event handler.
        txtEnterField.addActionListener(this);

        // Set up the screen
        setLayout(new BorderLayout());
        add(txtEnterField, BorderLayout.SOUTH);
        add(txtChatArea, BorderLayout.CENTER);

        // Connect to the server
        try
        {
            // Initiate the connection through a socket
            socket = new Socket(host, port);
            System.out.println("connected to " + socket);
            
            // Get the I/O streams from the socket.
            inStream = new ObjectInputStream(socket.getInputStream());
            outStream = new ObjectOutputStream(socket.getOutputStream());
            
            // Start a thread for receiving messages
            new Thread(this).start();
        }
        catch (final IOException ie)
        {
            System.out.println(ie);
        }
    }


    public void actionPerformed(final ActionEvent e)
    {
    	// Get the message the user entered.
    	String message = e.getActionCommand();
    	
        try
        {
            // Send it to the server and clear the input box.
            outStream.writeObject(message);
            txtEnterField.setText("");
        }
        catch (final IOException ie)
        {
            System.out.println(ie);
        }
    }


    public void run()
    {
        try
        {
            // Receive messages one-by-one, forever
            while (true)
            {
                // Get the next message
                final String message = (String) inStream.readObject();
                
                // Print it to our text window
                txtChatArea.append(message + "\n");
            }
        }
        catch (final IOException ie)
        {
            System.out.println(ie);
        }
        catch (final ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args)
    {
    	JFrame window = new JFrame("CIS355 Chat Client");
    	Client chatClient = new Client("10.8.69.230", 5000);
    	
    	window.setLayout(new BorderLayout());
    	window.add(chatClient, BorderLayout.CENTER);
    	window.setSize(200, 200);
    	window.setVisible(true);
    	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
