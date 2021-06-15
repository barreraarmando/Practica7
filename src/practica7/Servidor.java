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

public class Servidor {

    public static void main(String[] args) {
        new anuncio(8000).start();
    }
    
}
