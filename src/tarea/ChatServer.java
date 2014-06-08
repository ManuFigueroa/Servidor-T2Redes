package tarea;
/* ChatServer.java */
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
//import java.nio.charset.Charset;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
//import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Hashtable;
//import java.util.Properties;
//import java.util.StringTokenizer;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//import java.util.Properties;

//import org.apache.commons.io.IOUtils;
 
public class ChatServer {

    private static int port = 8080; /* port to listen on */
    //private static final int fNumberOfThreads = 100;
	//private static final Executor fThreadPool = Executors.newFixedThreadPool(fNumberOfThreads);

 
    @SuppressWarnings("resource")
	public static void main (String[] args) throws IOException {
 
        ServerSocket server = null;
        try {
            server = new ServerSocket(port); /* start listening on the port */
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.err.println(e);
            System.exit(1);
        }
 
        Socket client = null;
        while(true) {
            try {
                client = server.accept();
                
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.err.println(e);
                System.exit(1);
            }
            /* start a new thread to handle this client */
            Thread t = new Thread(new ClientConn(client));
            t.start();
        }
    }

}


 
class ChatServerProtocol {
    private String nick;
    private ClientConn conn;
 
    /* a hash table from user nicks to the corresponding connections */
    private static Hashtable<String, ClientConn> nicks = 
        new Hashtable<String, ClientConn>();
 
    private static final String msg_OK = "OK";
    private static final String msg_NICK_IN_USE = "NICK IN USE";
    private static final String msg_SPECIFY_NICK = "SPECIFY NICK";
    private static final String msg_INVALID = "INVALID COMMAND";
    private static final String msg_SEND_FAILED = "FAILED TO SEND";
 
    /**
     * Adds a nick to the hash table 
     * returns false if the nick is already in the table, true otherwise
     */
    private static boolean add_nick(String nick, ClientConn c) {
        if (nicks.containsKey(nick)) {
            return false;
        } else {
            nicks.put(nick, c);
            return true;
        }
    }
 
    public ChatServerProtocol(ClientConn c) {
        nick = null;
        conn = c;
    }
 
    private void log(String msg) {
        System.err.println(msg);
    }
 
    public boolean isAuthenticated() {
        return ! (nick == null);
    }
 
    /**
     * Implements the authentication protocol.
     * This consists of checking that the message starts with the NICK command
     * and that the nick following it is not already in use.
     * returns: 
     *  msg_OK if authenticated
     *  msg_NICK_IN_USE if the specified nick is already in use
     *  msg_SPECIFY_NICK if the message does not start with the NICK command 
     */
    private String authenticate(String msg) {
        if(msg.startsWith("NICK")) {
            String tryNick = msg.substring(5);
            if(add_nick(tryNick, this.conn)) {
                log("Nick " + tryNick + " joined.");
                this.nick = tryNick;
                return msg_OK;
            } else {
                return msg_NICK_IN_USE;
            }
        } else {
            return msg_SPECIFY_NICK;
        }
    }
 
    /**
     * Send a message to another user.
     * @recepient contains the recepient's nick
     * @msg contains the message to send
     * return true if the nick is registered in the hash, false otherwise
     */
    private boolean sendMsg(String recipient, String msg) {
        if (nicks.containsKey(recipient)) {
            //ClientConn c = nicks.get(recipient);
            guardar(msg,nick,recipient);
            guardar(msg,nick,nick);
            //c.sendMsg(nick + ": " + msg);
            return true;
        } else {
            return false;
        }
    }
 
    /**
     * Process a message coming from the client
     */
    public String process(String msg) {
        if (!isAuthenticated()) 
            return authenticate(msg);
 
        String[] msg_parts = msg.split(" ", 3);
        String msg_type = msg_parts[0];
 
        if(msg_type.equals("MSG")) {
            if(msg_parts.length < 3) return msg_INVALID;
            if(sendMsg(msg_parts[1], msg_parts[2])) return msg_OK;
            else return msg_SEND_FAILED;
        } 
        else if(msg_type.equals("PEDIR"))
        {
        	if (nicks.containsKey(nick)) {
	        	ClientConn c = nicks.get(nick);
	        	String algo = leer(nick);
	        	c.sendMsg(algo);
	        	return msg_OK;
        	}
        	else{
        		return msg_SEND_FAILED;
        	}
        	
        }
        else if (msg_type.equals("FILE")) {
        	 //DataOutputStream output;
        
        	String file;        	 
        	String recipient = msg_parts[1];
        	file = msg_parts[2];
        	
        	Transferir(file,recipient);
        	//System.out.println("archivo se llama: " + file);       	
        	
        	return msg_OK;
        }
        else{
            return msg_INVALID;
        }
    }
    
    private void Transferir(String file, String recipient) {
    	BufferedInputStream bis;
   	 	BufferedOutputStream bos;
	 
	 	byte[] receivedData;
	 	int in;
    	try {System.out.println("SOY: "+this.conn.getClient());
			 receivedData = new byte[1024];
			 bis = new BufferedInputStream(this.conn.getClient().getInputStream());
			 //DataInputStream dis = new DataInputStream(this.conn.getClient().getInputStream());
			 //Recibimos el nombre del fichero
			 //file = dis.readUTF();
			 //file = file.substring(file.indexOf('\\')+1,file.length());
			 //Para guardar fichero recibido
			 bos = new BufferedOutputStream(new FileOutputStream(file));
			 while ((in = bis.read(receivedData)) != -1){
			 bos.write(receivedData,0,in);
			 }
			 bos.close();
<<<<<<< HEAD
			 //dis.close();
			 
			 
			
=======
			 bis.close();		
>>>>>>> FETCH_HEAD
		} catch (IOException e) {
			e.printStackTrace();
		}
   	
	}

	public static void guardar(String Mensaje, String De, String Para) {
		try {
 
			String escribe = new String ("<p>"+De+ ": " + Mensaje + "</p>");
 
			File file = new File(Para+".txt");
			//file.createNewFile();
			
			if (!file.exists()) {
				file.createNewFile();
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(escribe);
				bw.close();
			}
			else {
				FileWriter fw = new FileWriter(file, true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(escribe);
				bw.close();
			}
 
			System.out.println("Mensaje guardado");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    public static String leer(String contacto) {
	 	InputStream fis = null;
		BufferedReader br;
		String linea;
		try {
			
			//para verificar si no hay contactos
			File file = new File(contacto+".txt");
			if (!file.exists()) {
				return "Sin mensajes"; 				
			}
			else 
			{
				fis = new FileInputStream(contacto+".txt");
				
				br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
				//imprimir
				linea = br.readLine();
				br.close();
				br = null;
				fis = null;
				return linea;
			}		
	} catch (IOException e) {
		e.printStackTrace();
	}
		return "Error al leer";
	
}
}
 
class ClientConn implements Runnable {
	private Socket client;
    private BufferedReader in = null;
    private PrintWriter out = null;
 
    ClientConn(Socket client) {
        this.setClient(client);
        try {
            /* obtain an input stream to this client ... */
            in = new BufferedReader(new InputStreamReader(
                        client.getInputStream()));
            /* ... and an output stream to the same client */
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println(e);
            return;
        }
    }
 
    public void run() {
        String msg, response;
        ChatServerProtocol protocol = new ChatServerProtocol(this);
        try {
            /* loop reading lines from the client which are processed 
             * according to our protocol and the resulting response is 
             * sent back to the client */
            while ((msg = in.readLine()) != null) {
            	System.out.println("Comando ingresado: "+msg);
            	response = protocol.process(msg);
                out.println("SERVER: " + response);
                System.out.println("SERVER: "+response+" Socket abierto: "+client);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
 
    public void sendMsg(String msg) {
        out.println(msg);
    }

	public Socket getClient() {
		return client;
	}

	public void setClient(Socket client) {
		this.client = client;
	}
    
}