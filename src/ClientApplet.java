import java.applet.Applet;
import java.awt.BorderLayout;

public class ClientApplet extends Applet
{
    private static final long serialVersionUID = 5508193444476109464L;

    @Override
    public void init()
    {
        setLayout(new BorderLayout());
        add(new Client("10.8.14.112", 5000), BorderLayout.CENTER);
    }
}