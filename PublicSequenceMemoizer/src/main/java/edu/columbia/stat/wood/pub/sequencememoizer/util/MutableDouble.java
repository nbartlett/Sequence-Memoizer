/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.columbia.stat.wood.pub.sequencememoizer.util;

/**
 * Container class for double which can be set and manipulated.
 * @author nicholasbartlett
 */
public class MutableDouble {

    private double value;

    /**
     * Instantiates the object with a given value.
     * @param d double value to assign object
     */
    public MutableDouble(double d){
        value = d;
    }

    /**
     * Sets the underlying value of the object.
     * @param d double value to assign object
     */
    public void set(double d){
        value = d;
    }

    /**
     * Gets the value of the object.
     * @return double value of the object
     */
    public double value(){
        return value;
    }

    /**
     * Does a *= to the underlying value.
     * @param d value to multiply by
     */
    public void timesEquals(double d){
        value *= d;
    }

    /**
     * Does a += to the underlying value.
     * @param d value to add to the underlying value
     */
    public void plusEquals(double d){
        value += d;
    }

    /**
     * Overrides the hash code to only reflect the underlying double value.
     * @return int hash code
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
        return hash;
    }

    /**
     * Overrides the equals method to only reflect the underlying double value.
     * @param o object to compare to
     * @return true if object is of class MutableDouble and the underlying values are equal, else false
     */
    @Override
    public boolean equals(Object o){
        if(o == null){
            return false;
        } else if(o.getClass() != this.getClass()){
            return false;
        } else if (((MutableDouble) o).value() == value) {
            return true;
        } else {
            return false;
        }
    }
}
