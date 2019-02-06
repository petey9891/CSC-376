import java.io.*;

public interface Messenger {
    void close() throws IOException;
//    DataInputStream getInputStream() throws IOException;
//    DataOutputStream getOutputStream() throws IOException;
    BufferedReader getInputStream() throws IOException;
    PrintWriter getOutputStream() throws IOException;
}
