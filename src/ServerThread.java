import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/********************************************************************
 * This class runs a server-side thread that does nothing more than
 * listen for messages to arrive and then rebroadcast them to all
 * the clients.
 */
public class ServerThread extends Thread
{
    // The Server that spawned us
    private final Server server;

    // The Socket connected to our client
    private final Socket socket;

    // Constructor.
    public ServerThread(final Server server, final Socket socket)
    {
        // Save the parameters
        this.server = server;
        this.socket = socket;
        // Start up the thread
        start();
    }


    // This runs in a separate thread when start() is called in the
    // constructor.
    @Override
    public void run()
    {
        try
        {
            final ObjectInputStream inStream = new ObjectInputStream(socket
                            .getInputStream());
            while (true)
            {
                final String message = (String) inStream.readObject();
                System.out.println("Sending " + message);
                server.sendToAll(message);
            }
        }
        catch (final EOFException ie)
        {
        }
        catch (final IOException ie)
        {
            ie.printStackTrace();
        }
        catch (final ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        finally
        {
            server.removeConnection(socket);
        }
    }
}
