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

/**
 *
 * @author Rodrigo
 */
public class Cliente {
    public static void main(String args[]){
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
                System.out.println("Servidor disponible en el puerto: "+p.getPort());
        } 
        }catch(Exception e){}
    }
}
