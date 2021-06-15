/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica7;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

class anuncio extends Thread{
    static int pto;
    
    public anuncio (int puerto){
        this.pto = puerto;
    }
    
    public void run() {
        try{
            anunciar();
        }catch(Exception e){e.printStackTrace();}
    }
    
    private static void anunciar() throws IOException {
        InetAddress gpo=null;
        try{
            MulticastSocket s= new MulticastSocket(pto);
            s.setReuseAddress(true);
            s.setTimeToLive(255);
            String msj = Integer.toString(pto);
            byte[] b = msj.getBytes();
            try{
                gpo = InetAddress.getByName("228.1.1.1");
            }catch(UnknownHostException u){
                System.err.println("Direccion no valida");
            }//catch
            s.joinGroup(gpo);
            for(;;){
                DatagramPacket p = new DatagramPacket(b,b.length,gpo,9876);
                s.send(p);
                System.out.println("Servidor disponible en el puerto: "+pto);
                try{
                    Thread.sleep(5000);
                }catch(InterruptedException ie){}
            }//for
        }catch(Exception e){
            
        }//catch
    }
    
}

class buscador extends Thread{
    
    static int pto;
    String nombreA;
    
    public buscador (int puerto){
        this.pto = puerto;
    }
    
    public void run() {
        try{
            this.nombreA = escuchar();
            buscar();
        }catch(Exception e){e.printStackTrace();}
    }
    
    public String escuchar(){
        String nombre="";
        InetAddress gpo=null;
        boolean flag=false;
        
        try{
            MulticastSocket cl= new MulticastSocket(9876);
            try{
                    gpo = InetAddress.getByName("228.1.1.1");
                }catch(UnknownHostException u){
                    System.err.println("Direccion no valida");
                }//catch
            cl.joinGroup(gpo);
            while(!flag){
                DatagramPacket p = new DatagramPacket(new byte[65535],65535);
                cl.receive(p);
                String mensaje = new String(p.getData(),0,p.getLength());
                if (mensaje.length()>4){
                    nombre = mensaje;
                    flag = true;
                }
        } 
        }catch(Exception e){}
        
        return nombre;
    }
    
    public void buscar(){
        try {
            java.rmi.registry.LocateRegistry.createRegistry(pto); 
            System.out.println("RMI registry ready.");
	  } catch (Exception e) {
		 System.out.println("Exception starting RMI registry:");
		 e.printStackTrace();
	  }//catch
	
	try {
            System.setProperty("java.rmi.server.codebase","file:///C:\\Users\\Rodrigo\\Documents\\Clases en linea\\Ap. Redes\\Practica7\\Practica7"); ///file:///f:\\redes2\\RMI\\RMI2
	    RMI obj = new RMI(nombreA);
            System.out.println("Regreso arraylist con: ");
            System.out.println(obj.archivos);
	    Listas stub = (Listas) UnicastRemoteObject.exportObject(obj, 0);

	    // Bind the remote object's stub in the registry
	    Registry registry = LocateRegistry.getRegistry();
	    registry.bind("Listas", stub);

	    System.err.println("Servidor listo...");
	} catch (Exception e) {
	    System.err.println("Server exception: " + e.toString());
	    e.printStackTrace();
	}
    }
}

class RMI implements Listas{
    ArrayList archivos = new ArrayList();
    String nombreA;
    
    public RMI(String nombreA){
        this.nombreA = nombreA;
        this.archivos = buscarArchivo();
    }
    
    public ArrayList buscarArchivo(){
        ArrayList resultado = new ArrayList();
        System.out.println("Buscare archivos de nombre: "+nombreA);
        
        
        return resultado;
    }
    
    public Objetos obtenerLista(){
        Objetos obj = new Objetos(archivos);
        return obj;
    }
}

public class Servidor {
    
    public static void main(String[] args) {
        int puerto = 8001;
        new anuncio(puerto).start();
        new buscador(puerto).start();
    }
    
}
