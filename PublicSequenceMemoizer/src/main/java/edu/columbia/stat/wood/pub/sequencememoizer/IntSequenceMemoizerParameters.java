/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.columbia.stat.wood.pub.sequencememoizer;

import edu.columbia.stat.wood.pub.sequencememoizer.util.IntDiscreteDistribution;
import edu.columbia.stat.wood.pub.sequencememoizer.util.IntUniformDiscreteDistribution;

/**
 * Extension for int based Sequence Memoizer.
 * @author nicholasbartlett
 */
public class IntSequenceMemoizerParameters extends SequenceMemoizerParameters{

    /**
     * Base Distribution.
     */
    public IntDiscreteDistribution baseDistribution;

    /**
     * Constructor allowing all parameters to be set.
     * @param baseDistribution
     * @param discounts
     * @param infiniteDiscount
     * @param depth
     * @param seed
     * @param maxNumberRestaurants
     * @param maxSequenceLength
     */
    public IntSequenceMemoizerParameters(IntDiscreteDistribution baseDistribution, double[] discounts, double infiniteDiscount, int depth, long seed, long maxNumberRestaurants, long maxSequenceLength, long maxCustomersInRestaurant) {
        super(discounts, infiniteDiscount, depth, seed, maxNumberRestaurants, maxSequenceLength,maxCustomersInRestaurant);
        this.baseDistribution = baseDistribution;
    }

    /**
     * Constructor allowing some parameters to be set.  The default base distribution
     * is uniform over the types [0,alphabetSize).
     * @param depth
     * @param maxNumberRestaurants
     * @param maxSequenceLength
     * @param alphabetSize
     */
    public IntSequenceMemoizerParameters(int depth, long maxNumberRestaurants, long maxSequenceLength, int alphabetSize){
        super(depth, maxNumberRestaurants, maxSequenceLength);
        baseDistribution = new IntUniformDiscreteDistribution(alphabetSize);
    }

    /**
     * Constructor allowing some parameters to be set.  The default base distribution
     * is uniform over the types [0,alphabetSize).
     * @param depth
     * @param alphabetSize
     */
    public IntSequenceMemoizerParameters(int depth, int alphabetSize){
        super(depth, Long.MAX_VALUE, Long.MAX_VALUE);
        baseDistribution = new IntUniformDiscreteDistribution(alphabetSize);
    }

    /**
     * Constructor allowing some parameters to be set.  The default base distribution
     * is uniform over the types [0,alphabetSize).
     * @param alphabetSize
     */
    public IntSequenceMemoizerParameters(int alphabetSize) {
        super();
        baseDistribution = new IntUniformDiscreteDistribution(alphabetSize);
    }
}
