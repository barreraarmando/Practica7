/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica7;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;


class Receptor extends Thread{
    
    public void run() {
        try{
            recibir();
        }catch(Exception e){e.printStackTrace();}
    }
    
    public static void recibir(){
        InetAddress gpo=null;
        
        try{
            MulticastSocket cl= new MulticastSocket(9876);
            try{
                    gpo = InetAddress.getByName("228.1.1.1");
                }catch(UnknownHostException u){
                    System.err.println("Direccion no valida");
                }//catch
            cl.joinGroup(gpo);
            while(true){
                DatagramPacket p = new DatagramPacket(new byte[65535],65535);
                cl.receive(p);
                String mensaje = new String(p.getData(),0,p.getLength());
                if (p.getPort()!=9876)
                    System.out.println("Servidor disponible en el puerto: "+p.getPort());
        } 
        }catch(Exception e){}
    }
    
}

class RMIC extends Thread{
    
    public RMIC(){
        ArrayList resultados = new ArrayList();
        
        try {	
        Registry registry = LocateRegistry.getRegistry(null);	

        Listas stub = (Listas) registry.lookup("Listas");
        Objetos obj = stub.obtenerLista();
        System.out.println(obj.archivos);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
   
}

public class Cliente {
    
    public static void mandarNombre(String nombreA){
        InetAddress gpo=null;
        try{
            MulticastSocket s= new MulticastSocket(9876);
            s.setReuseAddress(true);
            s.setTimeToLive(255);
            byte[] b = nombreA.getBytes();
            try{
                gpo = InetAddress.getByName("228.1.1.1");
            }catch(UnknownHostException u){
                System.err.println("Direccion no valida");
            }//catch
            s.joinGroup(gpo);
            DatagramPacket p = new DatagramPacket(b,b.length,gpo,9876);
            s.send(p);
        }catch(Exception e){
            
        }//catch
    }
    
    public static void main(String args[]){
        String archivo;
        
        Scanner leer = new Scanner(System.in);
        Receptor objReceptor = new Receptor();
        objReceptor.start();
        System.out.println("Introduzca el nombre del archivo que busca: ");
        archivo = leer.next();
        mandarNombre(archivo);
        //objReceptor.stop();
    }
}
