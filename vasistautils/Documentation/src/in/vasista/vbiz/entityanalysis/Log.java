/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package in.vasista.vbiz.entityanalysis;

/**
 *
 * @author sindukur
 */
public class Log {

public static void LogInfoMessage(String msg){
    System.out.println("INFO:"+msg);
}
public static void LogDebugMessage(String msg){
    System.out.println("DEBUG"+msg);
}
public static void LogErrorMessage(String msg){
    System.out.println("ERROR"+msg);
}
}
