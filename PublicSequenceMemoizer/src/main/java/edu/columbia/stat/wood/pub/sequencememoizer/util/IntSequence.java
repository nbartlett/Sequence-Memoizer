/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.columbia.stat.wood.pub.sequencememoizer.util;

import edu.columbia.stat.wood.pub.sequencememoizer.IntSequenceMemoizer.NewKey;
import gnu.trove.set.hash.THashSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

/**
 * Class which implements an int sequence as a linked list to facilitate easy
 * deletion of the start of the sequence.
 * @author nicholasbartlett
 */
public class IntSequence implements Serializable {

    static final long serialVersionUID = 1;

    private int nodeSize, index, length;
    private IntSeqNode first, last;

    /**
     * Constructor specifying the size of the int[] housed in each node of the linked list.
     * @param nodeSize
     */
    public IntSequence(int nodeSize) {
        this.nodeSize = nodeSize;

        first = new IntSeqNode(null, null, nodeSize);
        last = first;
        index = nodeSize - 1;
        length = 0;
    }

    /**
     * Gets the node size.
     * @return size of int[] in each node
     */
    public int blockSize() {
        return nodeSize;
    }

    /**
     * Appends the int i to the sequence.
     * @param i int to append
     */
    public void append(int i) {

        if (index < 0) {
            last.next = new IntSeqNode(last, null, nodeSize);
            last = last.next;
            index = nodeSize - 1;
        }

        length++;
        last.intChunk[index--] = i;
    }

    /**
     * Get total length of the sequence.
     * @return length
     */
    public int length() {
        return length;
    }

    /**
     * Shorten the sequence by deleting the earliest node and all the restaurant
     * nodes in the model which point to it.
     */
    public void shorten(MutableLong restCount) {
        for (IntRestaurant r : first) {
            r.removeFromTree(restCount);
        }

        length -= nodeSize;
        first = first.next;
        first.previous = null;
    }

    /**
     * Get an iterator object to traverse the sequence backwards.
     * @return iterator
     */
    public BackwardsIterator backwardsIterator() {
        return new BackwardsIterator();
    }

    /**
     * Get the overlap between a context, starting at a specified index, and the
     * sequence, starting at a specified index and node.
     * @param edgeNode node
     * @param edgeIndex index within node
     * @param edgeLength length of edge in model, overlap must be less than or equal to this number
     * @param context context we are comparing to
     * @param index index into context pointing to current context location
     * @return number of overlapping context elements
     */
    public int overlap(IntSeqNode edgeNode, int edgeIndex, int edgeLength, int[] context, int index) {
        int overlap = 0;
        while (edgeNode != null && overlap < edgeLength && index > -1 && edgeNode.intChunk[edgeIndex] == context[index]) {
            overlap++;
            index--;
            edgeIndex++;

            if (edgeIndex >= nodeSize) {
                edgeNode = edgeNode.previous;
                if (edgeNode == null) {
                    break;
                }
                edgeIndex = 0;
            }
        }

        return overlap;
    }

    /**
     * Gets a node object given its index.
     * @param ind index
     * @return the node object
     */
    public IntSeqNode get(int ind){
        IntSeqNode node = first;
        for(int i = 0; i < ind; i++){
            node = node.next;
        }
        return node;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        int nodes = 0;
        IntSeqNode node = first;
        while (node != null) {
            nodes++;
            node = node.next;
        }

        out.writeInt(nodes);
        out.writeInt(nodeSize);
        out.writeInt(index);
        out.writeInt(length);

        node = first;
        while (node != null) {
            THashSet<IntRestaurant> s = new THashSet<IntRestaurant>(node);
            out.writeObject(s);
            out.writeObject(node.intChunk);

            node = node.next;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int nodes = in.readInt();
        nodeSize = in.readInt();
        index = in.readInt();
        length = in.readInt();

        THashSet<IntRestaurant> s = (THashSet<IntRestaurant>) in.readObject();

        IntSeqNode nextNode;
        IntSeqNode node = new IntSeqNode(s);
        node.intChunk = (int[]) in.readObject();
        first = node;
        nodes--;
        while (nodes > 0) {
            s = (THashSet<IntRestaurant>) in.readObject();
            nextNode = new IntSeqNode(s);
            node.next = nextNode;
            nextNode.previous = node;
            nextNode.intChunk = (int[]) in.readObject();

            node = nextNode;
            nodes--;
        }
        last = node;
    }

    public class IntSeqNode extends THashSet<IntRestaurant> {

        private int[] intChunk;
        private IntSeqNode previous, next;

        public IntSeqNode(IntSeqNode previous, IntSeqNode next, int nodeSize) {
            this.previous = previous;
            this.next = next;

            intChunk = new int[nodeSize];
        }

        public IntSeqNode(Collection<IntRestaurant> collection){
            super(collection);
        }

        public IntSeqNode previous() {
            return previous;
        }

        public int[] intChunk() {
            return intChunk;
        }

        public int getIndex(){
            int ind = -1;
            IntSeqNode node = this;
            while(node != null){
                ind++;
                node = node.previous;
            }
            return ind;
        }
    }

    public class BackwardsIterator {

        public IntSeqNode node;
        public int ind;

        public BackwardsIterator() {
            node = last;
            ind = index + 1;
        }

        public int peek() {
            if (ind >= nodeSize) {
                node = node.previous;
                ind = 0;
            }
            return node.intChunk[ind];
        }

        public int next() {
            if (ind >= nodeSize) {
                node = node.previous;
                ind = 0;
            }
            return node.intChunk[ind++];
        }

        public boolean hasNext() {
            if (ind >= nodeSize) {
                node = node.previous;
                ind = 0;
            }

            return node != null;
        }

        public int overlap(IntSeqNode edgeNode, int edgeIndex, int edgeLength, NewKey newKey) {
            IntSeqNode ln;
            int overlap, li;

            ln = edgeNode;
            li = edgeIndex;

            overlap = 0;
            while (ln.intChunk[li] == node.intChunk[ind] && overlap < edgeLength) {
                li++;
                ind++;
                overlap++;

                if (li >= nodeSize) {
                    ln = ln.previous;
                    if (ln == null) {
                        break;
                    }
                    li = 0;
                }

                if (ind >= nodeSize) {
                    node = node.previous;
                    ind = 0;
                }
            }

            if (ln != null) {
                newKey.set(ln.intChunk[li]);
            } else {
                newKey.setNull();
            }

            return overlap;
        }

        public int available(int l) {
            int available = nodeSize - ind;

            if (available > l) {
                return l;
            } else {
                IntSeqNode n = node.previous;
                while (n != null) {
                    available += nodeSize;
                    if (available > l) {
                        return l;
                    }
                    n = n.previous;
                }

                return available;
            }
        }
    }
}
