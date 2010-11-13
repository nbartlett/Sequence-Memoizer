/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.columbia.stat.wood.pub.sequencememoizer.util;

import edu.columbia.stat.wood.pub.sequencememoizer.IntSequenceMemoizer;
import edu.columbia.stat.wood.pub.sequencememoizer.util.IntSequence.IntSeqNode;
import edu.columbia.stat.wood.pub.sequencememoizer.IntSequenceMemoizer.SeatReturn;
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

    public IntRestaurant(IntRestaurant parent, int edgeStart,  int edgeLength, IntSeqNode edgeNode,  int numLeafNodesAtOrBelow, MutableLong restCount) {
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
        restCount.increment();
    }

    public void setTableConfig(int[] types, int[] customersAndTables, int customers, int tables) {
        this.types = types;
        this.customersAndTables = customersAndTables;
        this.customers = customers;
        this.tables = tables;
    }

    public double getPP(int type, double p, double discount, SeatReturn sr) {
        int index, tc, tt, tci, tti;


        if(type > types[types.length -1]){
            tt = 0;
            tc = 0;
        } else {
            index = getIndex(type);
            if(types[index] == type){
                tci = 2 * index;
                tti = tci + 1;

                tc = customersAndTables[tci];
                tt = customersAndTables[tti];
            } else {
                tt = 0;
                tc = 0;
            }
        }
        
        sr.set(false, tt, customers, tables);
        p -= ((double) tc - (double) tt * discount) / (double) customers;

        return p * (double) customers / ((double) tables * discount);
    }

    public void deleteCustomers(int nDelete, double discount) {
        int[] c = new int[types.length];

        for (int t = 0; t < types.length; t++) {
            c[t] = customersAndTables[2 * t];
        }

        int[] toDelete = SampleMultinomial.deleteCustomersAtRandom(nDelete, c, customers, IntSequenceMemoizer.RNG);
        int number_zeros = 0;
        for (int t = 0; t < types.length; t++) {
            if (toDelete[t] > 0) {
                if(toDelete[t] == customersAndTables[2*t]){
                    customers -= toDelete[t];
                    tables -= customersAndTables[2 * t + 1];

                    customersAndTables[2 * t] = 0;
                    customersAndTables[2 * t + 1] = 0;

                    number_zeros++;
                } else {

                    int[] sa = SeatingArranger.getSeatingArrangement(customersAndTables[2 * t], customersAndTables[2 * t + 1], discount);
                    int[] cToDelete = SampleMultinomial.deleteCustomersAtRandom(toDelete[t], sa, customersAndTables[2 * t], IntSequenceMemoizer.RNG);

                    customersAndTables[2 * t] -= toDelete[t];
                    customers -= toDelete[t];

                    for (int i = 0; i < sa.length; i++) {
                        if (sa[i] == cToDelete[i]) {
                            tables--;
                            customersAndTables[2 * t + 1]--;
                        }
                    }
                    assert customersAndTables[2*t] >= customersAndTables[2*t +1];
                }
            }
        }

        if(number_zeros > 0){
            int[] new_types = new int[types.length - number_zeros];
            int[] new_customersAndTables = new int[customersAndTables.length - 2 * number_zeros];

            int j = 0, k = 0;
            for(int i = 0; i < types.length; i++){
                if(customersAndTables[2*i] > 0){
                    new_types[j++] = types[i];
                    new_customersAndTables[k++] = customersAndTables[2*i];
                    new_customersAndTables[k++] = customersAndTables[2*i + 1];
                    if(new_customersAndTables[k-2] < new_customersAndTables[k-1] || new_customersAndTables[k-2] == 0 || new_customersAndTables[k-1] == 0){
                        throw new RuntimeException("new_customersAndTables[k-1] = " + new_customersAndTables[k-1] + ", new_customersAndTables[k-2] = " + new_customersAndTables[k-2]);
                    }
                }
            }

            types = new_types;
            customersAndTables = new_customersAndTables;
        }
    }
    
    public double seat(int type, double p, double discount, SeatReturn sr, IntSequenceMemoizer sm) {
                
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

        if (customers >= sm.maxCustomersInRestaurant) {
            deleteCustomers((int) (sm.maxCustomersInRestaurant * .1),discount);
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

    public IntRestaurant fragmentForInsertion(IntRestaurant irParent, int irEdgeStart, int irEdgeLength, IntSeqNode irEdgeNode, double discount, double irDiscount, MutableLong restCount) {
        double fragDiscount, fragConcentration, numerator, denominator;
        IntRestaurant intermediateRestaurant;
        int[] irTypes;
        int[] irCustomersAndTables, tsa;
        int l, tci, tti, fc, ft, tc, tt, irc, irt;

        customers = 0;
        tables = 0;

        intermediateRestaurant = new IntRestaurant(irParent, irEdgeStart, irEdgeLength, irEdgeNode, numLeafNodesAtOrBelow, restCount);

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

    public IntRestaurant fragmentForPrediction(IntRestaurant irParent, double discount, double irDiscount, MutableLong restCount){
        IntRestaurant intermediateRestaurant;
        double fragDiscount, fragConcentration, numerator, denominator;
        int[] irTypes;
        int[] irCustomersAndTables, tsa;
        int l, irc, irt, tci, tti, tc, tt, fc, ft;

        intermediateRestaurant = new IntRestaurant(irParent, 0, 0, null, 0, restCount);
        restCount.decrement();

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

    public final void removeFromTree(MutableLong restCount){
        parent.remove(edgeNode.intChunk()[edgeStart]);
        if(!parent.isEmpty()){
            parent.decrementLeafNodeCount();
        }
        restCount.decrement();
    }

    public final void removeFromTreeAndEdgeNode(MutableLong restCount){
        edgeNode.remove(this);
        parent.remove(edgeNode.intChunk()[edgeStart]);
        if(!parent.isEmpty()){
            parent.decrementLeafNodeCount();
        }
        restCount.decrement();
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
