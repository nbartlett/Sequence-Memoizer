/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.columbia.stat.wood.pub.sequencememoizer.util;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Uniform discrete distribution over the integers in [leftType, rightType).
 * @author nicholasbartlett
 */

public class IntUniformDiscreteDistribution implements IntDiscreteDistribution, Serializable {
    static final long serialVersionUID = 1;

    private int leftType, rightType;
    private double p;

    /**
     * Creates a uniform discrete distribution over the integers [0, alphabetSize).
     * @param alphabetSize
     */
    public IntUniformDiscreteDistribution(int alphabetSize){
        leftType = 0;
        rightType = alphabetSize - 1;
        p = 1.0 / (double) alphabetSize;
    }

    /**
     * Creates a uniform discrete distribution over the integers [leftType, rightType].
     * @param leftType
     * @param rightType
     */
    public IntUniformDiscreteDistribution(int leftType, int rightType){
        this.leftType = leftType;
        this.rightType = rightType;
        p = 1.0 / (double) ((long) rightType - (long) leftType + (long) 1);
    }

    /**
     * Gets the probability of an integer type.
     * @param type
     * @return probability of type
     */
    public double probability(int type) {
        if(type <= rightType && type >= leftType){
            return p;
        } else {
            return 0.0;
        }
    }

    /**
     * Gets an iterator over Integer Double pairs such that the Double value is
     * the probability of the integer type in this discrete distribution.
     * @return iterator
     */
    public Iterator<Pair<Integer, Double>> iterator() {
        return new UniformIterator();
    }

    private class UniformIterator implements Iterator<Pair<Integer, Double>> {
        int type = leftType;
        
        public boolean hasNext() {
            return type <= rightType;
        }

        public Pair<Integer, Double> next() {
            return new Pair(type++, p);
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
