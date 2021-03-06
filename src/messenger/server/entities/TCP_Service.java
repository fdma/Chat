
package messenger.server.entities;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author smoranbl
 */
public class TCP_Service extends Thread{
    private ServerSocket socketServidor;
    private Socket clientSocket;
    private final ArrayList<Connection> storeConnections = new <Connection>ArrayList();
    
    //Constructor del TCP_Service, crea el socket del servidor.
    public TCP_Service(){
        this.createSocketServidor();        
    }
    
    //Crea el socket del servidor.
    private void createSocketServidor(){
        try {
            socketServidor = new ServerSocket(5000);
        } catch (IOException ex) {
            Logger.getLogger(TCP_Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Metodo que espera una conexion y la inicializa.
    private void newClient() throws IOException{
        System.out.println("Esperando conexion..");
        clientSocket = socketServidor.accept();
        Connection newClient = new Connection(this, clientSocket);
        checkClientName(newClient);     
        newClient.start();
        storeConnections.add(newClient);        
        System.out.println("Conectado: " + newClient.getClientName());        
    }
    
    //Espera a que el cliente envie su nombre para editar el nombre de la conexion.
    public void checkClientName(Connection newClient){
        String name = null;
        
        while(name == null){
            name = newClient.getFlow().inString();
            if(name != null)
                newClient.setClientName(name);                            
        }
    }
    
    //Envia un mensaje a todas las conexiones.
    public void sendMenssage(String menssage){
        for(int i=0; i<storeConnections.size(); i++)
            storeConnections.get(i).getFlow().outString(menssage);
    }
    
    //Lanza un hilo con un bucle en el que se espera una conexion.
    @Override
    public void run() {
        while(true){
            try {
                newClient();
            } catch (IOException ex) {
                Logger.getLogger(TCP_Service.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }    
}
