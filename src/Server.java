import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;


public class Server
{
    public static final int DEFAULT_PORT = 5000;

    // The socket we'll use for accepting new connections
    private ServerSocket serverSocket;

    // A mapping from sockets to output streams.
    private final Hashtable<Socket, ObjectOutputStream> outputStreams = new Hashtable<Socket, ObjectOutputStream>();


    // Constructor.
    public Server() throws IOException
    {
        this(DEFAULT_PORT);
    }


    // Constructor.
    public Server(final int port) throws IOException
    {
        listen(port);
    }


    private void listen(final int port) throws IOException
    {
        // Create the ServerSocket
        serverSocket = new ServerSocket(port);
        System.out.println("Starting server at address " + getIPAddress());
        System.out.println("Listening on " + serverSocket);

        // Infinite loop to accept connections
        while (true)
        {
            // Grab the next incoming connection
            final Socket s = serverSocket.accept();
            System.out.println("Connection from " + s);

            // Create an ObjectOutputStream for sending to the clients
            final ObjectOutputStream outStream = new ObjectOutputStream(s
                            .getOutputStream());
            
            // Save this stream so we don't need to make it again
            outputStreams.put(s, outStream);
            
            // Create a new thread for this connection.
            new ServerThread(this, s);
        }
    }


    /**************************************************************** 
     * Broadcast a message to all clients (utility routine)
     * 
     * @param message The message to broadcast.
     */
    void sendToAll(final String message)
    {
        // We synchronize on this because another thread might be
        // calling removeConnection() and this would screw us up
        // as we tried to walk through the list
        synchronized (outputStreams)
        {
        	// Grab the list of output streams.
        	final Enumeration<ObjectOutputStream> e = outputStreams.elements();

        	// For each client ...
            while (e.hasMoreElements())
            {
                // ... get the output stream ...
                final ObjectOutputStream outStream = e.nextElement();
                // ... and send the message
                try
                {
                    outStream.writeObject(message);
                }
                catch (final IOException ie)
                {
                    System.out.println(ie);
                }
            }
        }
    }


    /****************************************************************
     * Remove a socket and its corresponding output stream from our
     * list.
     * 
     * @param s The socket to be removed.
     */
    void removeConnection(final Socket s)
    {
        // Synchronize so we don't mess up sendToAll() while it walks
        // down the list of all output streams
        synchronized (outputStreams)
        {
            System.out.println("Removing connection to " + s);
            
            // Remove it from our list
            outputStreams.remove(s);
            
            // Make sure it's closed
            try
            {
                s.close();
            }
            catch (final IOException ie)
            {
                System.out.println("Error closing " + s);
                ie.printStackTrace();
            }
        }
    }


    private String getIPAddress()
    {
        String ip = "";
        try
        {
            final InetAddress inetAddress = InetAddress.getLocalHost();
            ip = inetAddress.getHostAddress();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        return ip;
    }


    static public void main(final String args[]) throws Exception
    {
        // Get the port # from the command line
        // final int port = Integer.parseInt(args[0]);
        // Create a Server object, which will automatically begin
        // accepting connections.
        //new Server(port);
    	
    	// To keep it simple for class, let's forget the command line
    	// and just use the default port.
        new Server();
    }
}
