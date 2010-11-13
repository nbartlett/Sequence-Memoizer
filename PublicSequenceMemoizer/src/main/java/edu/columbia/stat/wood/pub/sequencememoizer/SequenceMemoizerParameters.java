/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.columbia.stat.wood.pub.sequencememoizer;

/**
 * A container object for the parameters of a sequence memoizer. For depths >= discounts.length, the discount is
 * discounts[discounts.length -1]^(alpha ^ (depth - discounts.length + 1)).  Alpha is set such that the product of discounts
 * for depths = discounts.length through infinity converges to the specified infinite discount.  This prevents the
 * product of the discounts from converging to 0.0 and helps prevent the overdeterminism seen in some other parameterizations
 * of the model.
 *
 * @author nicholasbartlett
 */
public class SequenceMemoizerParameters {

    /**
     * Unique discount parameters for depth in [0 , discounts.length).
     */
    public double[] discounts;

    /**
     * The product of the discounts for depths of discounts.length through infinity will converge to this infinite discount.
     * In more interpretable terms this means that if conditional distribution R is at depth discounts.length-1 then the prior indicates that
     * restaurants at near infinite depth which share the same recent context of length discounts.length-1 will follow a Pitman-Yor
     * distribution centered at R with discount infiniteDiscount and concentration 0.0.
     */
    public double infiniteDiscount;

    /**
     * Max depth of model.
     */
    public int depth;

    /**
     * Seed used for random number generation.
     */
    public long seed;

    /**
     * Max allowable instantiated restaurants in the model.
     */
    public long maxNumberRestaurants;

    /**
     * Max allowable customers in any given restaurant in the model.
     */
    public long maxCustomersInRestaurant;

    /**
     * This value must be greater than or equal to 1024, but we suggest much higher,
     * probably something like maxNumberRestaurants * 10.  The construction of the tree
     * which makes up the major data structure for the model
     * requires that edges index into an underlying sequence.  This number provides
     * a maximum allowable length for this underlying sequence.  The shortening of this
     * sequence may (likely will) require that some restaurants are deleted from the tree, though
     * if this is set appropriately high and the number of restaurants are limited, it is likely
     * that all of the deletion will happen randomly and not as a result of shortening the underlying
     * sequence.
     */
    public long maxSequenceLength;

    /**
     * Constructor allowing all of the fields to be specified as arguments.
     * @param discounts
     * @param infiniteDiscount
     * @param depth
     * @param seed
     * @param maxNumberRestaurants
     * @param maxSequenceLength
     * @param maxCustomersInRestaurant
     */
    public SequenceMemoizerParameters(double[] discounts, double infiniteDiscount, int depth, long seed, long maxNumberRestaurants, long maxSequenceLength, long maxCustomersInRestaurant) {
        this.discounts = discounts;
        this.infiniteDiscount = infiniteDiscount;
        this.depth = depth;
        this.seed = seed;
        this.maxNumberRestaurants = maxNumberRestaurants;
        this.maxSequenceLength = maxSequenceLength;
        this.maxCustomersInRestaurant = maxCustomersInRestaurant;
        checkParameters();
    }

    /**
     * Constructor allowing for some of the parameters to be specified.  Default values
     * are used for all omitted variables and are set as discounts = discounts = {0.5, 0.7, 0.8, 0.82, 0.84, 0.88, 0.91, 0.92, 0.93, 0.94, 0.95},
     * infiniteDiscount = 0.5, depth = 1023, seed = 3, maxNumberRestaurants = Long.MAX_VALUE, maxSequenceLength = Long.MAX_VALUE,
     * maxCustomersInRestaurant = Long.MAX_VALUE
     * @param depth
     * @param maxNumberRestaurants
     * @param maxSequenceLength
     */
    public SequenceMemoizerParameters(int depth, long maxNumberRestaurants, long maxSequenceLength){
        this.discounts = new double[]{0.5, 0.7, 0.8, 0.82, 0.84, 0.88, 0.91, 0.92, 0.93, 0.94, 0.95};
        this.infiniteDiscount = 0.5;
        this.depth = depth;
        this.seed = 3;
        this.maxNumberRestaurants = maxNumberRestaurants;
        this.maxSequenceLength = maxSequenceLength;
        this.maxCustomersInRestaurant = Long.MAX_VALUE;
        checkParameters();
    }

    /**
     * Constructor allowing for some of the parameters to be specified.  Default values
     * are used for all omitted variables and are set as discounts = discounts = {0.5, 0.7, 0.8, 0.82, 0.84, 0.88, 0.91, 0.92, 0.93, 0.94, 0.95},
     * infiniteDiscount = 0.5, depth = 1023, seed = 3, maxNumberRestaurants = Long.MAX_VALUE, maxSequenceLength = Long.MAX_VALUE
     * maxCustomersInRestaurant = Long.MAX_VALUE
     * @param depth
     */
    public SequenceMemoizerParameters(int depth){
        this(depth, Long.MAX_VALUE, Long.MAX_VALUE);
    }

    /**
     * Constructor allowing for some of the parameters to be specified.  Default values
     * are used for all omitted variables and are set as discounts = discounts = {0.5, 0.7, 0.8, 0.82, 0.84, 0.88, 0.91, 0.92, 0.93, 0.94, 0.95},
     * infiniteDiscount = 0.5, depth = 1023, seed = 3, maxNumberRestaurants = Long.MAX_VALUE, maxSequenceLength = Long.MAX_VALUE
     * maxCustomersInRestaurant = Long.MAX_VALUE
     * @param maxNumberRestaurants
     * @param maxSequenceLength
     */
    public SequenceMemoizerParameters(long maxNumberRestaurants, long maxSequenceLength){
        this(1023,maxNumberRestaurants, maxSequenceLength);
    }

    /**
     * Constructor allowing for some of the parameters to be specified.  Default values
     * are used for all omitted variables and are set as discounts = discounts = {0.5, 0.7, 0.8, 0.82, 0.84, 0.88, 0.91, 0.92, 0.93, 0.94, 0.95},
     * infiniteDiscount = 0.5, depth = 1023, seed = 3, maxNumberRestaurants = Long.MAX_VALUE, maxSequenceLength = Long.MAX_VALUE
     * maxCustomersInRestaurant = Long.MAX_VALUE
     */
    public SequenceMemoizerParameters() {
        this(1023);
    }

    private void checkParameters(){

        for(double d : discounts){
            if(d <= 0.0 || d >= 1.0){
                throw new IllegalArgumentException("Discounts must be in (0.0, 1.0)");
            }
        }

        if(infiniteDiscount <= 0.0 || infiniteDiscount >= 1.0){
            throw new IllegalArgumentException("Infinite discount must be in (0.0, 1.0)");
        }

        if(depth < 0){
            throw new IllegalArgumentException("Depth must be greater than or equal to 0");
        }

        if(maxNumberRestaurants < 1){
            throw new IllegalArgumentException("maxNumberRestaurants must be greater than or equal to one");
        }

        if(maxSequenceLength < 1024){
            throw new IllegalArgumentException("maxSequenceLength must be greater than or equal to 1024.  There is "
                    + "very little harm, in terms of memory, done by setting this to be quite large.");
        }

        if(maxCustomersInRestaurant <= 0){
            throw new IllegalArgumentException("maxCustomersInRestaurant must be > 0");
        }
    }
}
