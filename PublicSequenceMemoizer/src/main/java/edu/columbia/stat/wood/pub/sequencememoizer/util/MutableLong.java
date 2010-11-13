/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.columbia.stat.wood.pub.sequencememoizer.util;

/**
 *
 * @author nicholasbartlett
 */
public class MutableLong {

    static final long serialVersionUID = 1;

    private long l;

    /**
     * @param value value of underlying long
     */
    public MutableLong(long value){
        l = value;
    }

    /**
     * Allows the underlying value to be set.
     * @param value long value to assign this object
     */
    public void set(long value){
        l = value;
    }

    /**
     * Gets the long value of this object.
     * @return long value of this object
     */
    public long value(){
        return l;
    }

    /**
     * Increments the underlying value by 1.
     */
    public void increment(){
        l++;
    }

    /**
     * Decrements the underlying value by 1.
     */
    public void decrement(){
        l--;
    }


    /**
     * Overrides the equals() method to only reflect the underlying long value.
     * @param o object to compare to
     * @return true if equal, else false
     */
    @Override
    public boolean equals(Object o){
        if(o == null){
            return false;
        } else if(o.getClass() != this.getClass()){
            return false;
        } else if(((MutableInt) o).value() == l){
            return true;
        } else {
            return false;
        }
    }

   /**
     * Overrides hash code to only reflect underlying long value.
     * @return hash code
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (int) (this.l ^ (this.l >>> 32));
        return hash;
    }
}
