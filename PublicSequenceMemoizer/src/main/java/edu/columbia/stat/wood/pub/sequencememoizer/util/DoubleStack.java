/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.columbia.stat.wood.pub.sequencememoizer.util;

import java.io.Serializable;

/**
 * Stack object for double native types.
 * @author nicholasbartlett
 */

public class DoubleStack implements Serializable{

    static final long serialVersionUID = 1;

    private double[] stack;
    private int index, l;

    /**
     * Initializes the stack to have a capacity of 1024.  The implementation is array based
     * but the underlying array will never shrink so should not be used in memory intensive situations.
     */
    public DoubleStack(){
        stack = new double[1024];
        l = 1024;
        index = -1;
    }

    /**
     * Checks to see if there is a next element.
     * @return true of there is a next element, else false.
     */
    public boolean hasNext(){
        return index > -1;
    }

    /**
     * Gets the next element but leaves it on the stack.  This operation will
     * fail if !hasNext().
     * @return next double
     */
    public double peak(){
        return stack[index];
    }

    /**
     * Pushes a double onto the stack.
     * @param d double to push
     */
    public void push(double d){
        if(index == l-1){
            double[] newStack;

            newStack = new double[l + 1024];
            System.arraycopy(stack, 0, newStack, 0, l);
            l += 1024;
            stack = newStack;
        }
        
        stack[++index] = d;
    }

    /**
     * Pops a double off the stack.
     * @return next double
     */
    public double pop(){
        return stack[index--];
    }

    /**
     * Gets the index for the underlying array of the top of the stack. This is the index of the double
     * that would be returned by calling pop() or peak();
     * @return index
     */
    public int index(){
        return index;
    }

    /**
     * Allows you to arbitrarily set the index.  Since pop() does not actually
     * remove elements from the stack, it only changes the index, this is useful
     * if you want to traverse the stack multiple times.
     * @param index value to set underlying index to
     */
    public void setIndex(int index){
        this.index = index;
    }

    /**
     * Utility method allowing you to print the underlying array.
     */
    public void print(){
        System.out.print("[" + stack[0]);
        for(int i = 1; i <= index; i++){
            System.out.print(", " + stack[i]);
        }
        System.out.println("]");
    }
}
