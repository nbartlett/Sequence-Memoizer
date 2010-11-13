/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.columbia.stat.wood.pub.sequencememoizer.util;

import java.util.Iterator;

/**
 * Int discrete distribution interface.
 * @author nicholasbartlett
 */
public interface IntDiscreteDistribution {

    /**
     * Gets the probability of certain type.
     * @param type int type
     * @return probability of this type, this should be in [0, 1.0]
     */
    public double probability(int type);

    /**
     * Gets an iterator over Integer Double pairs such that the Double value is the
     * probability of the Integer value in the distribution.  The assumption is that
     * the iterator will iterate over the unique types in this distribution which
     * are given probability mass.  If we sum the Double values over all the elements
     * in the iterator the result should be 1.0.
     * @return iterator
     */
    public Iterator<Pair<Integer, Double>> iterator();
}
