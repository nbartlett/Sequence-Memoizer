/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.columbia.stat.wood.pub.sequencememoizer.util;

import java.io.Serializable;

/**
 * Array based map for native int keys.
 * @author nicholasbartlett
 */
public class IntMap<E> implements Serializable{

    static final long serialVersionUID = 1;

    private int[] keys;
    private E[] values;

    /**
     * Indicates if this map is empty.
     * @return true if empty, else false
     */
    public boolean isEmpty(){
        return keys == null;
    }

    /**
     * Returns the number of mapped keys in the map.
     * @return size of map
     */
    public int size(){
        if(keys != null){
            return keys.length;
        } else {
            return 0;
        }
    }

    /**
     * Gets the value associated with the key.
     * @param key
     * @return value associated with key, null if no value is found
     */
    public E get(int key){
        if(keys == null || key > keys[keys.length - 1]){
            return null;
        } else {
            int index;

            index = getIndex(key);

            if(keys[index] == key){
                return values[index];
            } else {
                return null;
            }
        }
    }

    /**
     * Associates a value to a key in the map.
     * @param key
     * @param value
     * @return Object previously assigned to key or null if no value was previously assigned
     */
    public E put(int key, E value){
        if(keys == null){
            keys = new int[]{key};
            values = (E[]) new Object[]{value};

            return null;
        } else if(key > keys[keys.length-1]){
            int[] newKeys;
            int l;
            E[] newValues;

            l = keys.length;
            newKeys = new int[l + 1];
            newValues = (E[]) new Object[l + 1];

            System.arraycopy(keys, 0, newKeys, 0, l);
            System.arraycopy(values, 0, newValues, 0, l);
            newKeys[l] = key;
            newValues[l] = value;

            keys = newKeys;
            values = newValues;

            return null;
        } else {
            int index;

            index = getIndex(key);

            if(keys[index] == key){
                E returnValue;

                returnValue = values[index];
                values[index] = value;

                return returnValue;
            } else {
                int[] newKeys;
                int l;
                E[] newValues;

                l = keys.length;
                newKeys = new int[l + 1];
                newValues = (E[]) new Object[l + 1];

                System.arraycopy(keys, 0, newKeys, 0, index);
                System.arraycopy(values, 0, newValues, 0, index);
                newKeys[index] = key;
                newValues[index] = value;
                System.arraycopy(keys, index, newKeys,index + 1, l - index);
                System.arraycopy(values, index, newValues, index + 1, l-index);

                keys = newKeys;
                values = newValues;
                return null;
            }
        }
    }

    /**
     * Removes a key and its associated value from the map.
     * @param key key to remove
     */
    public void remove(int key){
        if(keys == null){
            throw new IllegalArgumentException("Key to remove is not in map");
        } else if (keys.length == 1){
            if(key == keys[0]){
                 keys = null;
                 values = null;
            } else {
                throw new IllegalArgumentException("Key to remove is not in map");
            }
        } else if(key > keys[keys.length-1]){
            throw new IllegalArgumentException("Key to remove is not in map");
        } else {
            int index;

            index = getIndex(key);
            if(key != keys[index]){
                throw new IllegalArgumentException("Key to remove is not in map");
            } else {
                int[] newKeys;
                E[] newValues;
                int l;

                l = keys.length;
                newKeys = new int[l - 1];
                newValues = (E[]) new Object[l - 1];

                System.arraycopy(keys, 0, newKeys, 0, index);
                System.arraycopy(keys, index + 1, newKeys, index, l - index - 1);
                System.arraycopy(values, 0, newValues, 0, index);
                System.arraycopy(values, index + 1, newValues, index, l-index -1);

                keys = newKeys;
                values = newValues;
            }
        }
    }

    private int getIndex(int key){
        int l, r, midPoint;

        l = 0;
        r = keys.length - 1;

        assert key <= keys[keys.length-1];

        while(l < r){
            midPoint = (l + r) / 2;
            if(key > keys[midPoint]){
                l = midPoint + 1;
            } else {
                r = midPoint;
            }
        }

        return l;
    }

    /**
     * Gets mapped keys.
     * @return int array of keys
     */
    public int[] keys(){
        return keys;
    }

    /**
     * Gets values in the map.
     * @return Object array of values.
     */
    public Object[] values(){
        return values;
    }

    /**
     * Allows the underlying arrays to be set.  The key array is assumed (not checked)
     * to be sorted lowest to highest and the value array should be in corresponding order.
     * @param keys keys to use
     * @param values values to use
     */
    public void set(int[] keys, E[] values){
        assert checkSet(keys, values);

        this.keys = keys;
        this.values = values;
    }

    private boolean checkSet(int[] keys, E[] values){

        for(int i = 0; i < keys.length-1; i++){
            if(keys[i] >= keys[i + 1]){
                return false;
            }
        }

        return keys.length == values.length;
    }

    /**
     * Utility method to print the underlying arrays.
     */
    public void print(){
        if(values == null){
            System.out.println("Keys : " + keys);
            System.out.println("Values : " + values);
            return;
        }

        System.out.print("Keys : [" + keys[0]);
        for(int i = 1; i < keys.length; i++){
            System.out.print(", " + keys[i]);
        }
        System.out.println("]");

        System.out.print("Values : [" + values[0]);
        for(int i = 1; i < values.length; i++){
            System.out.print(", " + values[i]);
        }
        System.out.println("]");
    }
}
