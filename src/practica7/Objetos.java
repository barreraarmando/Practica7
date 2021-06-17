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
    ArrayList md5;
    
    public Objetos(ArrayList archivos, ArrayList md5){
        this.archivos = archivos;
        this.md5 = md5;
    }
    
    public ArrayList getLista(){
        return archivos;
    }
}
