/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.columbia.stat.wood.pub.sequencememoizer.util;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Implementation of a discrete distribution over an integer valued state space.
 * @author nicholasbartlett
 */
public class IntHashMapDiscreteDistribution implements IntDiscreteDistribution, Serializable {

    static final long serialVersionUID = 1;

    private TIntObjectHashMap<MutableDouble> mostOfPDF;
    private IntDiscreteDistribution baseDistribution;
    private double multFactor;

    /**
     * Creates a discrete distribution over an integer valued state space.  The contract here is
     * that mostOfPDF will sum to a number less than or equal to 1.0.  The double multFacor should
     * be equal to the difference between the sum of the values of most of PDF and 1.0.  The distribution
     * created is such that the probability of type a is mostOfPDF.get(a).value() + multFactor * baseDistribution.probability(a).
     * If a is not in mostOfPDF then the probability of a is multFactor * baseDistribution.probability(a).
     * @param mostOfPDF
     * @param baseDistribution
     * @param multFactor
     */
    public IntHashMapDiscreteDistribution(TIntObjectHashMap<MutableDouble> mostOfPDF, IntDiscreteDistribution baseDistribution, double multFactor){
        this.mostOfPDF = mostOfPDF;
        this.baseDistribution = baseDistribution;
        this.multFactor = multFactor;
    }

    /**
     * Gets the probability of a certain type in this distribution.
     * @param type
     * @return probability of type
     */
    public double probability(int type) {
        MutableDouble md = (MutableDouble) mostOfPDF.get(type);
        if(md != null) {
            return md.value() + multFactor * baseDistribution.probability(type);
        } else {
            return multFactor * baseDistribution.probability(type);
        }
    }

    /**
     * Gets an iterator over Integer Double pairs for this distribution.
     * @return iterator
     */
    public Iterator<Pair<Integer, Double>> iterator() {
        return new Iter();
    }

    private class SecondIterator implements Iterator<Pair<Integer,Double>>{
        
        private Iterator<Pair<Integer,Double>> iter = baseDistribution.iterator();
        private Pair<Integer,Double> next = iter.next();
        private boolean returned = false;

        public boolean hasNext() {
            if(returned){
                if(iter.hasNext()){
                    next = iter.next();
                    returned = false;
                } else {
                    return false;
                }
            }

            int key = next.first();
            MutableDouble value = mostOfPDF.get(key);
            while(value != null){
                if(iter.hasNext()){
                    next = iter.next();
                } else {
                    return false;
                }

                key = next.first();
                value = mostOfPDF.get(key);
            }
            
            return true;
        }

        public Pair<Integer, Double> next() {
            if(hasNext()){
                returned  = true;
                return new Pair(next.first(), multFactor * next.second());
            } else {
                return null;
            }
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    private class FirstIterator implements Iterator<Pair<Integer,Double>>{

        TIntObjectIterator<MutableDouble> iter = mostOfPDF.iterator();


        public boolean hasNext() {
            return iter.hasNext();
        }

        public Pair<Integer, Double> next() {
            if(iter.hasNext()){
                iter.advance();

                int type = iter.key();
                
                return new Pair(type, iter.value().value() + multFactor * baseDistribution.probability(type));
            } else {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported");
        }

    }

    private class Iter implements Iterator<Pair<Integer, Double>>{

        Iterator<Pair<Integer,Double>> iter = new FirstIterator();
        public boolean secondIterator = false;
        
        public boolean hasNext() {
            if(iter.hasNext()){
                return true;
            } else {
                if(!secondIterator){
                    iter = new SecondIterator();
                    secondIterator = true;
                    return iter.hasNext();
                } else {
                    return false;
                }
            }
        }

        public Pair<Integer, Double> next() {
            if(hasNext()){
                return iter.next();
            } else {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
