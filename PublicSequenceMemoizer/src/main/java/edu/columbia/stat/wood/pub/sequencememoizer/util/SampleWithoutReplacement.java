/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.columbia.stat.wood.pub.sequencememoizer.util;

/**
 * Class to provide static method to return a random ordering of the integers 1 - n.
 * @author nicholasbartlett
 */
public class SampleWithoutReplacement {

    /**
     * Method to return a random ordering of the integers 0 - (n-1).
     * @param n
     * @param rng random number generator to be used
     * @return array of length n with the integers 0 - (n-1) in random order
     */
    public static int[] sampleWithoutReplacement(int n, MersenneTwisterFast rng) {
        int[] randomOrder = new int[n];
        double nn = n;
        int randomIndex, currentValue;

        //fill return array
        for(int i = 0; i < n; i++){
            randomOrder[i] = i;
        }
        
        for(int i = 0; i < n - 1; i++){
            //keep current value
            currentValue = randomOrder[i];

            //get random index
            randomIndex = i + (int) (rng.nextDouble() * (nn - i));

            //switch value with value at random index
            randomOrder[i] = randomOrder[randomIndex];
            randomOrder[randomIndex] = currentValue;
        }

        return randomOrder;
    }
}
