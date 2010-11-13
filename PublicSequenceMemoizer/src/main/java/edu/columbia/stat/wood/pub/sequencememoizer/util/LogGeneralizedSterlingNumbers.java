/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.columbia.stat.wood.pub.sequencememoizer.util;

import java.util.HashMap;

/**
 * Class to provide method to get the log of a generalized sterling number.  The
 * methods are not static because the function is memoized and therefore a unique
 * object must be created for different discount values.
 * @author nicholasbartlett
 */
public class LogGeneralizedSterlingNumbers {
    private HashMap<Pair<Integer, Integer>, Double> lookup ;
    private double d;

    /**
     * @param discount discount value to be used when calculating the generalized sterling number
     */
    public LogGeneralizedSterlingNumbers(double discount){
        if(discount >= 1.0){
            d = .999;
        } else if (discount <= 0.0){
            d = .001;
        }  else {
            d = discount;
        }

        lookup = new HashMap<Pair<Integer,Integer>, Double>();
    }

    /**
     * Get the log of the generalized sterling number for a given number of customers and tables.
     * @param c customers
     * @param t tables
     * @return log generalized sterling number
     */
    public double get(int c, int t){
        if(c <= 0 || t <= 0 || c < t){
            return Double.NEGATIVE_INFINITY;
        } else if (c == t){
            return 0.0;
        } else {
            Double answer;
            Pair key;
        
            key = new Pair(c, t);
            answer = lookup.get(key);

            if(answer == null){
                answer = LogAdd.logAdd(get(c-1,t-1), Math.log(c - 1 - d * t) + get(c-1, t));
                lookup.put(key,answer);
            }
            
            return answer;
        }
    }
}
