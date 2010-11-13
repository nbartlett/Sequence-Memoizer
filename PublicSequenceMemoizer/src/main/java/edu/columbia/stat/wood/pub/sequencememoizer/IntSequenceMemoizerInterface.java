/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.columbia.stat.wood.pub.sequencememoizer;

import edu.columbia.stat.wood.pub.sequencememoizer.util.IntDiscreteDistribution;

/**
 * Sequence memoizer interface for int types.
 * @author nicholasbartlett
 */
public interface IntSequenceMemoizerInterface {

    /**
     * Indicates to the model that you wish to start a new sequence.  The first
     * time continueSequence is used after this the assumption is that the type
     * is being seen with empty context.
     */
    public void newSequence();

    /**
     * Continue the current sequence by incorporating the new observation.
     * @param observation
     * @return log predictive probability of observation prior to incorporating the observation into the model
     */
    public double continueSequence(int observation);

    /**
     * Continue the sequence with the sequence of observations supplied.  This is
     * the same as looping over the method call continueSequence for each observation.
     * @param observations
     * @return the sum of the log predictive probabilities of each observation prior to incorporating it into the model
     */
    public double continueSequence(int[] observations);

    /**
     * Generate some number of samples given a specified context.
     * @param context context, arranged most distant to the left, most recent to the right
     * @param numSamples number of samples desired
     * @return samples from the predictive distribution in the specified context
     */
    public int[] generate(int[] context, int numSamples);

    /**
     * Generate a sequence from the predictive model, starting with the specified context.
     * @param context starting context
     * @param sequenceLength length of sequence
     * @return sampled sequence of specified length
     */
    public int[] generateSequence(int[] context, int sequenceLength);

    /**
     * Get the predictive distribution in a specified context.
     * @param context
     * @return discrete distribution over integer types
     */
    public IntDiscreteDistribution predictiveDistribution(int[] context);

    /**
     * Get the predictive probability of a given type in a given context.
     * @param context
     * @param type
     * @return probability of type in the specified context
     */
    public double predictiveProbability(int[] context, int type);

    /**
     * Gets the log probability of a sequence given the current state of the model.
     * @param context context prior to the start of the sequence to score
     * @param sequence
     * @return log probability of sequence in specified context.
     */
    public double sequenceProbability(int[] context , int[] sequence);

    /**
     * Sample the parameters in the model.  The seating arrangements are sampled
     * using Gibbs sampling while the discount parameters use a Gibbs-Metropolis step.
     * @param numSweeps number of sweeps for the Gibbs sampler
     * @return joint log probability of model and data
     */
    public double sample(int numSweeps);

    /**
     * Sample only the seating arrangements.
     * @param numSweeps
     */
    public void sampleSeatingArrangements(int numSweeps);

    /**
     * Sample only the discounts.
     * @param numSweeps
     * @return joint log probability of model and data
     */
    public double sampleDiscounts(int numSweeps);

    /**
     * Get the joint log probability of model and data.
     * @return joint log probability of model and data
     */
    public double score();

    /**
     * Get the parameters of this model.
     * @return parameters of this model
     */
    public IntSequenceMemoizerParameters getParameters();

}
