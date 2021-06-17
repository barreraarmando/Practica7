/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import static practica7.Receptor.recibir;


class Receptor extends Thread{
    static ArrayList servidores = new ArrayList();
    static ArrayList servidoresDescargar = new ArrayList();
    
    public void run() {
        try{
            recibir();
        }catch(Exception e){e.printStackTrace();}
    }
    
    public static void recibir(){
        InetAddress gpo=null;
        int act=-1;
        
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
                
                int puerto = p.getPort();
                if (puerto!=9876 && !servidores.contains(puerto)){
                    servidores.add(puerto);
                    System.out.println("Servidor disponible en el puerto: "+puerto);
                }
                if (mensaje.equals("TRUE")){
                    System.out.println("El servidor: "+p.getPort()+" tiene el archivo");
                    servidoresDescargar.add(p.getPort());
                }
//                if (act<servidores.size()){
//                    actualizarServidores();
//                }
                act = servidores.size();
        } 
        }catch(Exception e){}
    }
    
//    public static void actualizarServidores(){
//        
//    }
    
}

class RMIC extends Thread{
    ArrayList resultadosA = new ArrayList();
    ArrayList resultadosM = new ArrayList();
    
    public RMIC(int pto){
        try {	
            Thread.sleep(1000);
            Registry registry = LocateRegistry.getRegistry(pto);	

            Listas stub = (Listas) registry.lookup("Listas");
            Objetos obj = stub.obtenerLista();
            resultadosA = obj.archivos;
            resultadosM = obj.md5;
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
   
}

class Descarga extends Thread{;
    int div;
    int puerto;
    int num;
    
    public Descarga(int puerto, int div, int num){
        this.puerto = puerto;
        this.div=div;
        this.num=num;
    }
            
    public void run() {
        try{
            solicitarDescarga();
        }catch(Exception e){e.printStackTrace();}
    }
    
    public void solicitarDescarga(){
        System.out.println("Contactando con el servidor: "+puerto);
        String dir="";
        InetAddress host = null;
        File f = new File("");
        String ruta = f.getAbsolutePath();
        String carpeta="downloads";
        String ruta_archivos = ruta+"\\"+carpeta+"\\";
        System.out.println("Ruta: "+ruta_archivos);
        File f2 = new File(ruta_archivos);
        f2.mkdirs();
        f2.setWritable(true);
        try{
            //System.out.println("Escribe la direccion del servidor:");
            dir = "localhost";
            host = InetAddress.getByName(dir);
            Socket cl = new Socket(host,(puerto+100));
            System.out.println("Conexion con el servidor "+dir+":"+puerto+" establecida\n"); 
            PrintWriter pwRed = new PrintWriter(new OutputStreamWriter(cl.getOutputStream()));
            pwRed.println(div);
            pwRed.flush();
            pwRed.println(num);
            pwRed.flush();
            
            DataInputStream dis = new DataInputStream(cl.getInputStream());
            String nombre = dis.readUTF();
            long tam = dis.readLong()/div;
            System.out.println("Comienza descarga del archivo "+nombre+" de "+tam+" bytes\n");
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(ruta_archivos+"\\"+nombre));
            RandomAccessFile raf = new RandomAccessFile(ruta_archivos+"\\"+nombre, "rw");
            long recibidos=0;
            int l=0, porcentaje=0;
            int inicio = (num-1)*(int)tam;
            raf.seek(inicio);
            while(recibidos<tam){
                byte[] b = new byte[65000];
                l = dis.read(b);
                raf.write(b,0,l);
                recibidos = recibidos + l;
                porcentaje = (int)((recibidos*100)/tam);
                System.out.print("\rRecibido el "+ porcentaje +" % del archivo");
            }//while
            System.out.println("\nArchivo recibido..\n");
            dos.close();
            raf.close();
        }catch(Exception u){
            u.printStackTrace();
        }//catch
 
    }
}

public class Cliente {
    
    public static void mandarCadena(String nombreA){
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
        ArrayList servidores = new ArrayList();
        ArrayList servidoresD = new ArrayList();
        ArrayList resultadosA = new ArrayList();
        ArrayList resultadosM = new ArrayList();
        
        Scanner leer = new Scanner(System.in);
        Receptor objReceptor = new Receptor();
        objReceptor.start();
        boolean flag = true;
        
        while(flag){
            System.out.println("Introduzca el nombre del archivo que busca: ");
            archivo = leer.next();
            mandarCadena(archivo);
            servidores = Receptor.servidores;
            for (int i = 0; i < servidores.size(); i++) {
                System.out.println("Del servidor en el puerto: "+servidores.get(i));
                RMIC objC = new RMIC((int) servidores.get(i));
                resultadosA.addAll(objC.resultadosA);
                resultadosM.addAll(objC.resultadosM);
            }
            for (int j = 0; j < resultadosA.size(); j++) {
                System.out.println((j)+".-"+resultadosA.get(j));
                System.out.println("Su md5: "+resultadosM.get(j));
            }
            System.out.println("Escriba el numero de la opcion que desea descargar");
            int opcion = Integer.parseInt(leer.next());
            String cadenaEnviar = "MD5:"+resultadosM.get(opcion);
            mandarCadena(cadenaEnviar);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            servidoresD = Receptor.servidoresDescargar;
            int div = servidoresD.size();
            for (int j = 0; j < servidoresD.size(); j++) {
                Descarga objD = new Descarga((int) servidoresD.get(j), div, j+1);
                objD.start();
            }
            try{
                Thread.sleep(3000);
            }catch(Exception e){}
            System.out.println("Desea buscar otro archivo? S/N");
            String cadena = leer.next();
            if(!cadena.equals("S"))
                flag=false;
        }
        System.exit(0);
        //objReceptor.stop();
    }
}
