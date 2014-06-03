package tarea;
/* ChatServer.java */
import java.net.ServerSocket;
import java.net.Socket;
//import java.nio.charset.Charset;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
//import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Hashtable;
//import java.util.Properties;
//import java.util.StringTokenizer;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;

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
    
    /*private static void HandleRequest(Socket s) 
    { 
    	BufferedReader in;
    	PrintWriter out;
    	String request;

    		try 
    		{
    			 			
    			String webServerAddress = s.getInetAddress().toString();
    			System.out.println("New Connection:" + webServerAddress);
    			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    	        System.out.println(s.getInputStream());

    			request = in.readLine();

    			StringTokenizer st = new StringTokenizer(request);
    			//ESTO (method) DEFINE SI ES GET O POST
    			String method = st.nextToken();
    	        System.out.println("bla");

    	        String uri = decodePercent(st.nextToken());
    	        Properties parms = new Properties();
            int qmi = uri.indexOf('?');
            if (qmi >= 0) 
            {
            	decodeParms(uri.substring(qmi + 1), parms);
            	uri = decodePercent(uri.substring(0, qmi));
            }
    	        
            Properties header = new Properties();
    			
    			if (st.hasMoreTokens()) 
    			{
    		        String line = in.readLine();
    		          while (line.trim().length() > 0) 
    		          {
    		        	  int p = line.indexOf(':');
    		        	  header.put(line.substring(0, p).trim().toLowerCase(), line.substring(p + 1).trim());
    		        	  line = in.readLine();
    		          }
    		    }
    			
    			//Si es POST entra, en caso contrario (GET) hace solo el resto
    			if (method.equalsIgnoreCase("POST")) 
    			{
    				long size = 0x7FFFFFFFFFFFFFFFl;
    		         
    		        String contentLength = header.getProperty("content-length");
    		        
    		        if (contentLength != null)
    		              size = Integer.parseInt(contentLength);
    		        

    			String postLine = "";
    			char buf[] = new char[512];
    			int read = in.read(buf);
    			while (read >= 0 && size > 0 && !postLine.endsWith("\r\n")) 
    			{
    			size -= read;
    			postLine += String.valueOf(buf, 0, read);
    			if (size > 0)
    				read = in.read(buf);
    		        }
    		         
    				postLine = postLine.trim();
    		          
    				decodeParms(postLine, parms);
    				
    				guardar(parms);
    				String name = parms.getProperty("Name");
    				System.out.println(name);
    		    }
    	        
    	        
    			
    			System.out.println("--- Client request: " + request);

    			out = new PrintWriter(s.getOutputStream(), true);
    			
    			//el metodo es GET
    			if (uri.equals("/"))
    			{	
    				
    				out.println
    				(
    						"<html>"+
    								"<head >"+
    									"<meta http-equiv='Content-Type' content='text/html; charset=utf-8'/>"+
    									"<title>Contactos</title>"+
    								"</head>"+
    						"<body>"+
    						"<center>"+
    						"<h1>Contactos</h1>"+
    						"<table style = \"width:300px\" border= \"1\" align= \"center\" "+
    							"<tr>"+
    								"<td>Nombre</td>"+
    								"<td>IP</td>"+
    								"<td>Puerto</td>"+
    							"</tr>"						
    				);
    				leer(out);
    				out.println
    				(
    						"</table>"+
    						"<form action= '/ingresar'>"+
    					    "<input type='submit' value='Agregar Contacto'>"+
    					    "</form>"+
    						"</center>"+
    						"</body>"+
    						"</html>"
    				);		
    				
    			}
    			else if (uri.equals("/ingresar"))
    			{
    				InputStream archivo = new FileInputStream ("form.html");
    	 			String form = IOUtils.toString(archivo, "UTF-8");
    				out.println(form);
    			}
    			out.flush();
    			out.close();
    			s.close();
    		} 
    		catch (IOException e) 
    		{ 
    			System.out.println("Failed respond to client request: " + e.getMessage());
    		} catch (InterruptedException e) {
    		e.printStackTrace();
    	} 
    		finally 
    		{ 
    			if (s != null) 
    			{ 
    				try 
    				{ 
    					s.close();
    				} 
    				catch (IOException e) 
    				{ 
    					e.printStackTrace();
    				} 
    			} 
    		} 
    		return;
    	}

    private static String decodePercent(String str) {
          try {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < str.length(); i++) {
              char c = str.charAt(i);
              switch (c) {
              case '+':
                sb.append(' ');
                break;
              case '%':
                sb.append((char) Integer.parseInt(str.substring(i + 1, i + 3), 16));
                i += 2;
                break;
              default:
                sb.append(c);
                break;
              }
            }
            return new String(sb.toString().getBytes());
          } catch (Exception e) {
            
          }
    	return str;
    }

    private static void decodeParms(String parms, Properties p) throws InterruptedException {
          if (parms == null)
            return;

          StringTokenizer st = new StringTokenizer(parms, "&");
          while (st.hasMoreTokens()) {
            String e = st.nextToken();
            int sep = e.indexOf('=');
            if (sep >= 0)
              p.put(decodePercent(e.substring(0, sep)).trim(), decodePercent(e.substring(sep + 1)));
          }
    }

    public static void guardar(Properties contactos) {
    	try {

    		String name = contactos.getProperty("Name");
    		if((contactos.getProperty("Name")).equals("")){
    			name = "-";
    		}
    		
    		String ip = contactos.getProperty("Aipi");
    		if((contactos.getProperty("Aipi")).equals("")){
    			ip = "-";
    		}
    		
    		String port = contactos.getProperty("Port");
    		if((contactos.getProperty("Port")).equals("")){
    			port = "-";
    		}
    		
    		String escribe = new String (name + "," + ip + "," + port + "\n");

    		File file = new File("contactos.txt");
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

    		System.out.println("Nombre agregado...");
    		
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

    public static void leer(PrintWriter out) {
    	 	InputStream fis = null;
    		BufferedReader br;
    		String line;
    		String[] partes;

    		try {
    			
    		
    			//para verificar si no hay contactos
    			File file = new File("contactos.txt");
    			if (!file.exists()) {
    				out.println
     				(
     						"<tr>"+
     								"<td>No hay contactos</td>"+
     						"</tr>"	
     				);	
    			}
    			else 
    			{
    				fis = new FileInputStream("contactos.txt");
    				
    				br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
    				//imprimir
    				while ((line = br.readLine()) != null) {
    					partes = line.split(",");
    					out.println
    	 				(
    	 						"<tr>"+
    	 								"<td>"+ partes[0] +"</td>"+
    	 								"<td>"+ partes[1] +"</td>"+
    	 								"<td>"+ partes[2] +"</td>"+
    	 						"</tr>"	
    	 				);
    					
    				}
    				br.close();
    				br = null;
    				fis = null;
    			}
    		
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    }*/
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
            ClientConn c = nicks.get(recipient);
            c.sendMsg(nick + ": " + msg);
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
        } else {
            return msg_INVALID;
        }
    }
}
 
class ClientConn implements Runnable {
    @SuppressWarnings("unused")
	private Socket client;
    private BufferedReader in = null;
    private PrintWriter out = null;
 
    ClientConn(Socket client) {
        this.client = client;
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
                response = protocol.process(msg);
                out.println("SERVER: " + response);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
 
    public void sendMsg(String msg) {
        out.println(msg);
    }
}