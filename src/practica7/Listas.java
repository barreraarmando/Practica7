/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica7;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface Listas extends Remote {
    Objetos obtenerLista() throws RemoteException;
}
