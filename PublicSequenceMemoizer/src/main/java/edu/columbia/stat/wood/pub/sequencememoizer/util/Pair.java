/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.columbia.stat.wood.pub.sequencememoizer.util;

/**
 * Container class for two objects.
 * @author nicholasbartlett
 */
public class Pair<F,S> {
    private F first;
    private S second;

    /**
     * @param first first object
     * @param second second object
     */
    public Pair(F first, S second){
        this.first = first;
        this.second = second;
    }

    /**
     * Gets the first object.
     * @return first object
     */
    public F first(){
        return first;
    }

    /**
     * Gets the second object.
     * @return second object
     */
    public S second(){
        return second;
    }

    /**
     * Overrides the equals method to be true if both objects match.
     * @param o comparison object
     * @return true if equal, else false
     */
    @Override
    public boolean equals(Object o){
        if(o == null){
            return false;
        } else if (o.getClass() != this.getClass()){
            return false;
        } else if (((Pair) o).first().equals(first) && ((Pair) o).second().equals(second)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Overrides hashcode method to make both objects utilized in the hashcode creation.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (this.first != null ? this.first.hashCode() : 0);
        hash = 41 * hash + (this.second != null ? this.second.hashCode() : 0);
        return hash;
    }
}
