/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica7;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.io.RandomAccessFile;
import static practica7.anuncio.pto;

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
    String nombreA="---";
    String md5Buscar;
    String archivoEnviar="";
    
    public buscador (int puerto){
        this.pto = puerto;
    }
    
    public void run() {
        try{
            escuchar();
        }catch(Exception e){e.printStackTrace();}
    }
    
    public void escuchar(){
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
            try {
            java.rmi.registry.LocateRegistry.createRegistry(pto); 
            System.out.println("RMI registry ready.");
            } catch (Exception e) {
                   System.out.println("Exception starting RMI registry:");
                   e.printStackTrace();
            }//catch
            while(true){
                DatagramPacket p = new DatagramPacket(new byte[65535],65535);
                cl.receive(p);
                String mensaje = new String(p.getData(),0,p.getLength());
                if (mensaje.length()>4){
                    if(mensaje.contains("MD5:")){
                        md5Buscar = mensaje.substring(4);
                        buscarMd5();
                    }
                    else if (!nombreA.equals(mensaje)){
                        nombreA = mensaje;
                        buscar();
                    }
                }
        } 
        }catch(Exception e){}
    }
    
    public void buscar(){
	try {
            System.setProperty("java.rmi.server.codebase","http://8.25.100.18/clases/"); ///file:///f:\\redes2\\RMI\\RMI2
	    RMI obj = new RMI(nombreA);
            System.out.println("Regreso arraylist con: ");
            System.out.println(obj.archivos);
	    Listas stub = (Listas) UnicastRemoteObject.exportObject(obj, 0);

	    // Bind the remote object's stub in the registry
	    Registry registry = LocateRegistry.getRegistry(pto);
	    registry.bind("Listas", stub);

	    System.err.println("Servidor listo...");
	} catch (Exception e) {
	    System.err.println("Server exception: " + e.toString());
	    e.printStackTrace();
	}
    }
    
    public void buscarMd5(){
        System.out.println("Buscare el md5: "+md5Buscar);
        String ruta1 = "C:\\Users\\Rodrigo\\Documents\\Clases en linea\\Ap. Redes\\Practica7\\Practica7\\src\\archivos\\";
        String ruta2 = "C:\\Users\\Rodrigo\\Documents\\Clases en linea\\Ap. Redes\\Practica7\\Practica7\\src\\canciones\\";
        String md5A = "";
        File dir1 = new File(ruta1);
        File dir2 = new File(ruta2);
        String [] archivos1 = dir1.list();
        String [] archivos2 = dir2.list();
        MD5Checksum objMd5 = new MD5Checksum();
        try{
            if(archivos1 != null){
                for (String archivo:archivos1){
                    md5A = MD5Checksum.getMD5Checksum(ruta1+archivo);
                    if (md5A.equals(md5Buscar)){
                        archivoEnviar = ruta1+archivo;
                        enviarBandera();
                        enviarArchivo();
                    }
                }
            }
            if(archivos2 != null){
                for (String archivo:archivos2){
                    md5A = MD5Checksum.getMD5Checksum(ruta2+archivo);
                    if (md5A.equals(md5Buscar)){
                        archivoEnviar = ruta2+archivo;
                        enviarBandera();
                        enviarArchivo();
                    }
                }
            }
        }catch(Exception e){}
    }
    
    public void enviarBandera(){
        InetAddress gpo=null;
        try{
            MulticastSocket s= new MulticastSocket(pto);
            s.setReuseAddress(true);
            s.setTimeToLive(255);
            String msj = "TRUE";
            byte[] b = msj.getBytes();
            try{
                gpo = InetAddress.getByName("228.1.1.1");
            }catch(UnknownHostException u){
                System.err.println("Direccion no valida");
            }//catch
            s.joinGroup(gpo);
            DatagramPacket p = new DatagramPacket(b,b.length,gpo,9876);
            s.send(p);
            System.out.println("Tengo archivo buscado y envio bandera");
        }catch(Exception e){
            
        }//catch
    }
    
    public void enviarArchivo(){
        try{
            ServerSocket s = new ServerSocket(pto+100);
            s.setReuseAddress(true);
            File archivo = new File(archivoEnviar);
            Socket cl = s.accept();
            System.out.println("Cliente conectado desde "+cl.getInetAddress()+":"+cl.getPort());
            BufferedReader br = new BufferedReader(new InputStreamReader(cl.getInputStream()));
            int div = Integer.parseInt(br.readLine());
            int num = Integer.parseInt(br.readLine());
            System.out.println("El archivo se mandara en: "+div+" partes y me toca la num: "+num);
            String path = archivo.getAbsolutePath();
            String nombre = archivo.getName();
            long tam = (archivo.length())/div;
            System.out.println("Tamano parcial: "+(int)tam);
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
            DataInputStream dis = new DataInputStream(new FileInputStream(path));
            RandomAccessFile raf = new RandomAccessFile(archivo, "r");
            dos.writeUTF(nombre);
            dos.flush();
            dos.writeLong(tam);
            dos.flush();
            long enviados = 0;
            int inicio = (num-1)*(int)tam;
            System.out.println("Iniciare en el byte: "+inicio);
            int l=0,porcentaje=0;
            raf.seek(inicio);
            while(enviados<tam){
                byte[] b = new byte[500];
                l=raf.read(b);
                dos.write(b,0,l);
                dos.flush();
                enviados = enviados + l;
                porcentaje = (int)((enviados*100)/tam);
                System.out.print("\rEnviado el "+porcentaje+" % de mi parte");
            }//while
            System.out.println("\nArchivo enviado..");
            dis.close();
            raf.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

class RMI implements Listas{
    ArrayList archivos = new ArrayList();
    ArrayList md5 = new ArrayList();
    String nombreA;
    
    public RMI(String nombreA) throws Exception{
        this.nombreA = nombreA;
        buscarArchivo();
    }
    
    public void buscarArchivo() throws Exception{
        System.out.println("Buscare archivos de nombre: "+nombreA);
        String ruta1 = "C:\\Users\\Rodrigo\\Documents\\Clases en linea\\Ap. Redes\\Practica7\\Practica7\\src\\archivos\\";
        String ruta2 = "C:\\Users\\Rodrigo\\Documents\\Clases en linea\\Ap. Redes\\Practica7\\Practica7\\src\\canciones\\";
        File dir1 = new File(ruta1);
        File dir2 = new File(ruta2);
        String [] archivos1 = dir1.list();
        String [] archivos2 = dir2.list();
        MD5Checksum objMd5 = new MD5Checksum();
        if(archivos1 != null){
            for (String archivo:archivos1){
                if (archivo.contains(nombreA)){
                    archivos.add(archivo);
                    md5.add(objMd5.getMD5Checksum(ruta1+archivo));
                }
            }
        }
        if(archivos2 != null){
            for (String archivo:archivos2){
                if (archivo.contains(nombreA)){
                    archivos.add(archivo);
                    md5.add(objMd5.getMD5Checksum(ruta2+archivo));
                }
            }
        }
    }
    
    public Objetos obtenerLista(){
        Objetos obj = new Objetos(archivos, md5);
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
