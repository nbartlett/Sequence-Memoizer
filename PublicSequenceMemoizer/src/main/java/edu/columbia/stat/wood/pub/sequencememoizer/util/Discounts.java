/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.columbia.stat.wood.pub.sequencememoizer.util;

import java.io.Serializable;

/**
 * Discount parameter wrapper for sequence memoizer.
 * 
 * @author nicholasbartlett
 */
public class Discounts implements Serializable{

    static final long serialVersionUID = 1;

    private double[] discounts, logDiscounts, discountGradient;
    private double alpha, alphaGradient;

    /**
     * Sets the discount parameters.
     *
     * @param initialDiscounts unique discount parameters
     * @param dInfinity discount between restaurant of depth (initialDiscounts.length -1) and restaurants infinitely deep
     */
    public Discounts(double[] initialDiscounts, double dInfinity){
        if(dInfinity <= 0.0 || dInfinity >= 1.0){
            throw new IllegalArgumentException("dInfinity must be in the interval (0.0,1.0)");
        }

        alpha = Math.log(dInfinity) / (Math.log(dInfinity) + Math.log(initialDiscounts[initialDiscounts.length-1]));
        discounts = initialDiscounts;
        logDiscounts = new double[initialDiscounts.length];
        discountGradient = new double[initialDiscounts.length];

        fillLogDiscounts();
    }

    /**
     * Gets the discount for a given index.  Index must be in [0, length()).
     *
     * @param index index of desired discount
     * @return discount value
     */
    public double get(int index){
        if(index >= discounts.length){
            throw new IllegalArgumentException("Must only get discounts with an index in [0, length())");
        }
        return discounts[index];
    }

    /**
     * Sets discount for a given index.  Index must be in [0, length()).
     * @param index index of desired discount
     * @param value value to set chosen discount
     */
    public void set(int index, double value){
        if(index >= discounts.length){
            throw new IllegalArgumentException("Must only set discounts with an index in [0, length())");
        }
        discounts[index] = value;
    }

    /**
     * Gets the infinite discount.
     *
     * @return infinite discount
     */
    public double getdInfinity(){
        return Math.pow(discounts[discounts.length-1],alpha / (1 - alpha));
    }

    /**
     * Sets infinite discount.
     *
     * @param value value to set infinite discount to
     */
    public void setDInfinity(double value){
        alpha = Math.log(value) / (Math.log(value) + Math.log(discounts[discounts.length-1]));
    }

    /**
     * Gets discount given the specified parent and current depth.
     *
     * @param parentDepth parent depth
     * @param depth current depth
     * @return calculated discount
     */
    public double get(int parentDepth, int depth){
        double logDiscount;
        int d;

        if(parentDepth >= depth && (parentDepth != 0 && depth !=0)){
            throw new IllegalArgumentException("parent depth (" + parentDepth + ") " +
                 "must be less than depth of this restaurant (" + depth + ")");
        }

        d = parentDepth + 1;
        logDiscount = 0.0;
        
        if(depth == 0){
            logDiscount = logDiscounts[0];
        } else {
            while(d <= depth && d < discounts.length - 1){
                logDiscount += logDiscounts[d++];
            }

            if(depth >= discounts.length - 1){
                logDiscount += logDiscounts[discounts.length - 1] * Math.pow(alpha, (double) d - (double) discounts.length + 1.0) * (1.0 - Math.pow(alpha, (double) depth - (double) d + 1.0)) / (1.0 - alpha);
            }
        }

        return Math.exp(logDiscount);
    }

    /**
     * Gets number of unique discount parameters.
     *
     * @return number of unique discount parameters
     */
    public int length(){
        return discounts.length;
    }

    /**
     * Sets all the values of the discount gradient vector to 0.0.
     */
    public void clearGradient(){
        for(int i = 0; i < discountGradient.length; i++){
            discountGradient[i] = 0.0;
        }
        alphaGradient = 0.0;
    }

    /**
     * Adds a factor to discount gradient.  Method assumes that information is provided
     * as you go UP the tree.
     *
     * @param parentDepth parent depth
     * @param depth current depth
     * @param typeTables number of tables of type inserted
     * @param customers number of total customers in node
     * @param tables number of total tables in node
     * @param pp predictive probability of type in parent node
     * @param discount discount in node
     * @param multFactor factor by which to down-weight gradient update
     */
    public void updateGradient(int parentDepth, int depth, int typeTables, int customers, int tables, double pp, double discount, double multFactor) {
        double derivLogDa, derivLogDd;
        int d;

        if (customers > 0) {
            d = parentDepth + 1;
            derivLogDd = 0.0;
            derivLogDa = 0.0;

            if (depth == 0) {
                derivLogDd = 1.0 / discounts[0];
                discountGradient[0] += (((double) tables * pp - (double) typeTables) * discount * derivLogDd / (double) customers) * multFactor;
            } else {
                while (d <= depth && d < discounts.length - 1) {
                    derivLogDd = 1.0 / discounts[d];
                    discountGradient[d] += (((double) tables * pp - (double) typeTables) * discount * derivLogDd / (double) customers) * multFactor;
                    d++;
                }

                if (depth >= discounts.length - 1) {
                    double a, b;

                    a = (double) d - (double) discounts.length + 1.0;
                    b = (double) depth - (double) d + 1.0;

                    derivLogDd = Math.pow(alpha, a) * (1.0 - Math.pow(alpha, b)) / (1.0 - alpha) / discounts[discounts.length - 1];
                    discountGradient[discounts.length - 1] += (((double) tables * pp - (double) typeTables) * discount * derivLogDd / (double) customers) * multFactor;

                    derivLogDa = logDiscounts[discounts.length - 1] * ((a * Math.pow(alpha, a - 1) - (a + b) * Math.pow(alpha, a + b - 1)) / (1.0 - alpha) + (Math.pow(alpha, a) - Math.pow(alpha, a + b)) / (1.0 - alpha) / (1.0 - alpha));
                    alphaGradient += (((double) tables * pp - (double) typeTables) * discount * derivLogDa / (double) customers) * multFactor;
                }
            }
        }
    }

    /**
     * Using gradient information, step the discounts.  Then clears gradient information.
     *
     * @param eps governs step size
     * @param p predictive probability of type prior to insertion into the model
     */
    public void stepDiscounts(double eps, double p) {
        double proposal;

        p = p < .05 ? .05 : p;

        for(int i = 0; i < discountGradient.length; i++){
            proposal = discounts[i] + eps * discountGradient[i] / p;

            if(proposal > 1.0){
                discounts[i] = 1.0;
            } else if(proposal < 0.0){
                discounts[i] = 0.00000001;
            } else {
                discounts[i] = proposal;
            }
        }

        proposal = alpha + eps * alphaGradient / p;
        if(proposal >= 1.0){
            proposal = alpha + (1.0 - alpha) / 2.0;
        } else if (proposal <= 0.0){
            proposal = alpha / 2.0;
        }

        if(proposal < 1.0 && proposal > 0.0){
            alpha = proposal;
        }

        clearGradient();
        fillLogDiscounts();
    }

    private void fillLogDiscounts(){
        int discount;

        discount = 0;
        for(double disc : discounts){
            logDiscounts[discount++] = Math.log(disc);
        }
    }

    /**
     * Method for printing discount values.
     */
    public void print(){
        System.out.print("[" + discounts[0]);
        for(int i = 1; i<discounts.length; i++){
            System.out.print(", " + discounts[i]);
        }
        System.out.println("]");
        System.out.println("The infinite discount is = " + Math.pow(discounts[discounts.length-1],alpha / (1 - alpha)));
    }
}
