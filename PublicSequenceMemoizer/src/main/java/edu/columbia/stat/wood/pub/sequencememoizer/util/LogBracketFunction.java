/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.columbia.stat.wood.pub.sequencememoizer.util;

/**
 * Class to provide static method to get the log of the bracket function often
 * used when discussing the score of a seating arrangement for the Chinese restaurant
 * process.  The bracket function is [arg]_subScript^superScript = (arg)*(arg + subScript)*
 * (arg + 2*subScript)*...*(arg + (superScript - 1)*subScript)
 * @author nicholasbartlett
 */
public class LogBracketFunction {

    /**
     * Gets the log of the bracket function with the given arguments.
     * @param arg
     * @param superScript
     * @param subScript
     * @return log of the bracket function
     */
    public static double logBracketFunction(double arg, int superScript, double subScript){
        double lbf = 0.0;

        for(int i = 0; i < superScript; i++){
            lbf += Math.log(arg);
            arg += subScript;
        }

        return lbf;
    }
}
