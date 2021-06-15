/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica7;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Rodrigo
 */
public class Objetos implements Serializable {
    ArrayList archivos;
    
    public Objetos(ArrayList archivos){
        this.archivos = archivos;
    }
    
    public ArrayList getLista(){
        return archivos;
    }
}
