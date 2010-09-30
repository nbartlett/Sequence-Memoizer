
/*
 * Copyright © 2010 by The Trustees of Columbia University in the City of New York. All rights reserved.
 */

package edu.columbia.stat.wood.pub.sequencememoizer.util;

import edu.columbia.stat.wood.pub.sequencememoizer.IntSequenceMemoizer;
import edu.columbia.stat.wood.pub.sequencememoizer.util.IntSequence.IntSeqNode;
import edu.columbia.stat.wood.pub.sequencememoizer.IntSequenceMemoizer.SeatReturn;
import edu.columbia.stat.wood.pub.util.IntMap;
import edu.columbia.stat.wood.pub.util.SeatingArranger;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Node object used in int sequence memoizer.
 * @author nicholasbartlett
 */
public class IntRestaurant extends IntMap<IntRestaurant> implements Serializable{

    static final long serialVersionUID = 1;

    /**
     * Types observed this node.
     */
    public int[] types;

    /**
     * Counts of number of types and the number of tables (using the Chinese Restaurant metaphor)
     * at which they are sitting.
     */
    public int[] customersAndTables;

    /**
     * Total number of observations seen in this node.
     */
    public int customers;
    
    /**
     * Total number of tables in this node.
     */
    public int tables;

    /**
     * Offset between edge node start and the start of the edge label.
     */
    public int edgeStart;

    /**
     * Length of the edge.
     */
    public int edgeLength;

    /**
     * Number of leaf nodes below and including this node.
     */
    public int numLeafNodesAtOrBelow;

    /**
     * Parent node.
     */
    public IntRestaurant parent;

    /**
     * Node of linked list where this edge starts.
     */
    public IntSeqNode edgeNode;

    /**
     * Static count of total instantiated restaurants.
     */
    public static int count = 0;

    public IntRestaurant(IntRestaurant parent, int edgeStart,  int edgeLength, IntSeqNode edgeNode,  int numLeafNodesAtOrBelow) {
        this.parent = parent;
        this.edgeStart = edgeStart;
        this.edgeLength = edgeLength;
        this.edgeNode = edgeNode;
        this.numLeafNodesAtOrBelow = numLeafNodesAtOrBelow;

        if(edgeNode != null){
            edgeNode.add(this);
        }
        customers = 0;
        tables = 0;
        count++;
    }

    public void setTableConfig(int[] types, int[] customersAndTables, int customers, int tables) {
        this.types = types;
        this.customersAndTables = customersAndTables;
        this.customers = customers;
        this.tables = tables;
    }

    public double getPP(int type, double p, double discount, SeatReturn sr) {
        int index, tc, tt, tci, tti;

        index = getIndex(type);

        assert types[index] == type;

        tci = 2 * index;
        tti = tci + 1;

        tc = customersAndTables[tci];
        tt = customersAndTables[tti];

        sr.set(false, tt, customers, tables);
        p -= ((double) tc - (double) tt * discount) / (double) customers;

        return p * (double) customers / ((double) tables * discount);
    }
    
    public double seat(int type, double p, double discount, SeatReturn sr) {
        if (customers == 0) {
            sr.set(true, 0, customers, tables);

            types = new int[]{type};
            customersAndTables = new int[]{1, 1};
            customers++;
            tables++;
        } else if (type > types[types.length - 1]) {
            p *= (double) customers / ((double) tables * discount);
            sr.set(true, 0, customers, tables);

            insertNewType(type, types.length);
            customers++;
            tables++;
        } else {
            int index;

            index = getIndex(type);
            if (types[index] != type) {
                p *= (double) customers / ((double) tables * discount);
                sr.set(true, 0, customers, tables);

                insertNewType(type, index);
                customers++;
                tables++;
            } else {
                double numerator, denominator;
                int tci, tti, tc, tt;

                tci = index * 2;
                tti = tci + 1;

                tc = customersAndTables[tci];
                tt = customersAndTables[tti];

                numerator = (double) tc - (double) tt * discount;

                p -= numerator / (double) customers;
                p *= (double) customers / ((double) tables * discount);

                denominator = numerator + (double) tables * discount * p;

                if (numerator / denominator > IntSequenceMemoizer.RNG.nextDouble()) {
                    sr.set(false, customersAndTables[tti], customers, tables);

                    customersAndTables[tci]++;
                    customers++;
                } else {
                    sr.set(true, customersAndTables[tti], customers, tables);

                    customersAndTables[tci]++;
                    customersAndTables[tti]++;
                    customers++;
                    tables++;
                }
            }
        }

        return p;
    }

    private void insertNewType(int type, int index) {
        int[] newTypes;
        int[] newCustomersAndTables;
        int l;

        l = types.length;
        newTypes = new int[l + 1];
        newCustomersAndTables = new int[2 * l + 2];

        System.arraycopy(types, 0, newTypes, 0, index);
        System.arraycopy(customersAndTables, 0, newCustomersAndTables, 0, index * 2);

        newTypes[index] = type;
        newCustomersAndTables[2 * index] = 1;
        newCustomersAndTables[2 * index + 1] = 1;

        System.arraycopy(types, index, newTypes, index + 1, l - index);
        System.arraycopy(customersAndTables, index * 2, newCustomersAndTables, index * 2 + 2, 2 * l - 2 * index);

        types = newTypes;
        customersAndTables = newCustomersAndTables;
    }

    public IntRestaurant fragmentForInsertion(IntRestaurant irParent, int irEdgeStart, int irEdgeLength, IntSeqNode irEdgeNode, double discount, double irDiscount) {
        double fragDiscount, fragConcentration, numerator, denominator;
        IntRestaurant intermediateRestaurant;
        int[] irTypes;
        int[] irCustomersAndTables, tsa;
        int l, tci, tti, fc, ft, tc, tt, irc, irt;

        customers = 0;
        tables = 0;

        intermediateRestaurant = new IntRestaurant(irParent, irEdgeStart, irEdgeLength, irEdgeNode, numLeafNodesAtOrBelow);

        if (types == null) {
            edgeLength -= irEdgeLength;
            parent = intermediateRestaurant;
            return intermediateRestaurant;
        }

        fragDiscount = discount / irDiscount;
        fragConcentration = -1 * discount;

        l = types.length;
        irTypes = new int[l];
        System.arraycopy(types, 0, irTypes, 0, l);
        irCustomersAndTables = new int[2 * l];

        irc = 0;
        irt = 0;

        for (int typeIndex = 0; typeIndex < l; typeIndex++) {
            tci = 2 * typeIndex;
            tti = tci + 1;
            tc = customersAndTables[tci];
            tt = customersAndTables[tti];

            tsa = SeatingArranger.getSeatingArrangement(tc, tt, discount);

            fc = 0;
            ft = 0;
            for (int tableSize : tsa) {
                fc++;
                ft++;

                numerator = 1.0 - fragDiscount;
                denominator = 1.0 + fragConcentration;
                for (int customer = 1; customer < tableSize; customer++) {
                    if (numerator / denominator > IntSequenceMemoizer.RNG.nextDouble()) {
                        fc++;
                        numerator += 1.0;
                        denominator += 1.0;
                    } else {
                        fc++;
                        ft++;
                        numerator += 1.0 - fragDiscount;
                        denominator += 1.0;
                    }
                }
            }

            irc += ft;
            irt += tt;

            irCustomersAndTables[tci] = ft;
            irCustomersAndTables[tti] = tt;

            customers += fc;
            tables += ft;

            customersAndTables[tci] = fc;
            customersAndTables[tti] = ft;
        }

        intermediateRestaurant.setTableConfig(irTypes, irCustomersAndTables, irc, irt);
        parent = intermediateRestaurant;
        edgeStart += irEdgeLength;
        edgeLength -= irEdgeLength;

        return intermediateRestaurant;
    }

    public IntRestaurant fragmentForPrediction(IntRestaurant irParent, double discount, double irDiscount){
        IntRestaurant intermediateRestaurant;
        double fragDiscount, fragConcentration, numerator, denominator;
        int[] irTypes;
        int[] irCustomersAndTables, tsa;
        int l, irc, irt, tci, tti, tc, tt, fc, ft;

        intermediateRestaurant = new IntRestaurant(irParent, 0, 0, null, 0);
        count--;

        if(types != null){
            fragDiscount = discount / irDiscount;
            fragConcentration = -1 * discount;

            l = types.length;
            irTypes = new int[l];
            System.arraycopy(types, 0, irTypes, 0, l);
            irCustomersAndTables = new int[2 * l];

            irc = 0;
            irt = 0;
            for (int typeIndex = 0; typeIndex < l; typeIndex++) {
                tci = 2 * typeIndex;
                tti = tci + 1;
                tc = customersAndTables[tci];
                tt = customersAndTables[tti];

                tsa = SeatingArranger.getSeatingArrangement(tc, tt, discount);

                fc = 0;
                ft = 0;
                for (int tableSize : tsa) {
                    fc++;
                    ft++;

                    numerator = 1.0 - fragDiscount;
                    denominator = 1.0 + fragConcentration;
                    for (int customer = 1; customer < tableSize; customer++) {
                        if (numerator / denominator > IntSequenceMemoizer.RNG.nextDouble()) {
                            fc++;
                            numerator += 1.0;
                            denominator += 1.0;
                        } else {
                            fc++;
                            ft++;
                            numerator += 1.0 - fragDiscount;
                            denominator += 1.0;
                        }
                    }
                }

                irc += ft;
                irt += tt;

                irCustomersAndTables[tci] = ft;
                irCustomersAndTables[tti] = tt;
            }

            intermediateRestaurant.setTableConfig(irTypes, irCustomersAndTables, irc, irt);
        }
        return intermediateRestaurant;
    }

    public int getIndex(int type) {
        int l, r, midPoint;

        assert type <= types[types.length - 1];

        l = 0;
        r = types.length - 1;

        while (l < r) {
            midPoint = (l + r) / 2;
            if (type > types[midPoint]) {
                l = midPoint + 1;
            } else {
                r = midPoint;
            }
        }
        return l;
    }

    public final void removeFromTree(){
        parent.remove(edgeNode.intChunk()[edgeStart]);
        if(!parent.isEmpty()){
            parent.decrementLeafNodeCount();
        }
        count--;
    }

    public final void removeFromTreeAndEdgeNode(){
        edgeNode.remove(this);
        parent.remove(edgeNode.intChunk()[edgeStart]);
        if(!parent.isEmpty()){
            parent.decrementLeafNodeCount();
        }
        count--;
    }

    public void incrementLeafNodeCount(){
        numLeafNodesAtOrBelow++;
        if(parent != null) parent.incrementLeafNodeCount();
    }

    public void decrementLeafNodeCount(){
        numLeafNodesAtOrBelow--;
        if(parent != null) parent.decrementLeafNodeCount();
    }

    private void writeObject(ObjectOutputStream out) throws IOException{
        out.writeObject(types);
        out.writeObject(customersAndTables);
        out.writeInt(customers);
        out.writeInt(tables);
        out.writeInt(edgeStart);
        out.writeInt(edgeLength);
        out.writeInt(numLeafNodesAtOrBelow);
        out.writeObject(parent);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        types = (int[]) in.readObject();
        customersAndTables = (int[]) in.readObject();
        customers = in.readInt();
        tables = in.readInt();
        edgeStart = in.readInt();
        edgeLength = in.readInt();
        numLeafNodesAtOrBelow = in.readInt();
        parent = (IntRestaurant) in.readObject();
    }
}
