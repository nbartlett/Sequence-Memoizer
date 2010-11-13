/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.columbia.stat.wood.pub.sequencememoizer.util;

/**
 *
 * @author nicholasbartlett
 */
public class SampleMultinomial {

    public static int[] sampleMultinomial(int n, double[] weights, MersenneTwisterFast rng){
        double tw = 0.0;
        for(double w : weights){
            if(w < 0.0){
                throw new IllegalArgumentException("weights must be non negative");
            }
            tw += w;
        }

        for(int i = 0; i < weights.length; i++){
            weights[i] /= tw;
        }

        int[] sample = new int[weights.length];
        double r, cuSum;
        int l = weights.length;
        for(int i = 0; i < n; i++){
            r = rng.nextDouble();
            cuSum = 0.0;
            for(int j = 0; j < l; j++){
                cuSum += weights[j];
                if(cuSum > r){
                    sample[j]++;
                    break;
                }
            }
        }
        
        return sample;
    }

    public static int[] deleteCustomersAtRandom(int nDelete, int[] cc, int customers, MersenneTwisterFast rng){
        if(nDelete > customers){
            throw new IllegalArgumentException("nDelete must be <= customers");
        }

        int[] c = new int[cc.length];
        System.arraycopy(cc,0,c,0,cc.length);

        double r, cuSum;
        int[] sample = new int[c.length];
        int l = c.length;
        for(int n = 0; n < nDelete; n++){
            r = rng.nextDouble();
            cuSum = 0.0;
            for(int i = 0; i < l; i++){
                cuSum += (double) c[i] / (double) customers;
                if(cuSum > r){
                    sample[i]++;
                    c[i]--;
                    customers--;
                    break;
                }
                assert i != (l-1);
            }
        }

        assert checkSample(sample, cc, nDelete);

        return sample;
    }

    public static boolean checkSample(int[] sample, int[] cc, int nDelete){
        int c = 0;
        assert sample.length == cc.length;
        for(int i = 0; i < cc.length; i++){
            c += sample[i];
            assert sample[i] <= cc[i];
        }
        return c == nDelete;
    }

    /*public static void main(String[] args){
        int[] dcr = deleteCustomersAtRandom(15, new int[]{20,40,30,60},150, new MersenneTwisterFast(1));
        for(int i : dcr){
            System.out.print(", " + i);
        }
        System.out.println();
    }*/
}
