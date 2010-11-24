/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.columbia.stat.wood.pub.sequencememoizer;

import edu.columbia.stat.wood.pub.sequencememoizer.util.Discounts;
import edu.columbia.stat.wood.pub.sequencememoizer.util.IntRestaurant;
import edu.columbia.stat.wood.pub.sequencememoizer.util.IntSamplingNode;
import edu.columbia.stat.wood.pub.sequencememoizer.util.IntSequence;
import edu.columbia.stat.wood.pub.sequencememoizer.util.IntSequence.BackwardsIterator;
import edu.columbia.stat.wood.pub.sequencememoizer.util.IntSequence.IntSeqNode;
import edu.columbia.stat.wood.pub.sequencememoizer.util.DoubleStack;
import edu.columbia.stat.wood.pub.sequencememoizer.util.IntDiscreteDistribution;
import edu.columbia.stat.wood.pub.sequencememoizer.util.IntHashMapDiscreteDistribution;
import edu.columbia.stat.wood.pub.sequencememoizer.util.IntUniformDiscreteDistribution;
import edu.columbia.stat.wood.pub.sequencememoizer.util.LogBracketFunction;
import edu.columbia.stat.wood.pub.sequencememoizer.util.LogGeneralizedSterlingNumbers;
import edu.columbia.stat.wood.pub.sequencememoizer.util.MersenneTwisterFast;
import edu.columbia.stat.wood.pub.sequencememoizer.util.MutableDouble;
import edu.columbia.stat.wood.pub.sequencememoizer.util.MutableLong;
import edu.columbia.stat.wood.pub.sequencememoizer.util.Pair;
import edu.columbia.stat.wood.pub.sequencememoizer.util.SeatingArranger;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;

/**
 * Implementation of a sequence memoizer which takes int types.
 * @author nicholasbartlett
 */
public class IntSequenceMemoizer implements IntSequenceMemoizerInterface, Serializable {

    static final long serialVersionUID = 1 ;

    /**
     * Random number generator used for the stochastic elements of the model.
     */
    public static MersenneTwisterFast RNG;

    private int depth, rDepth, trueDepth;
    private IntRestaurant ecr;
    private IntSequence is;
    private Discounts discounts;
    private DoubleStack ds;
    private TIntObjectHashMap<MutableDouble> mostOfPDF;
    private SeatReturn sr;
    private IntDiscreteDistribution baseDistribution;
    private NewKey newKey = new NewKey();
    private long maxNumberRestaurants, maxSequenceLength, seed;
    private MutableLong restCount;

    public int maxCustomersInRestaurant;

    /**
     * Constructor initiating the model with the specified parameters.
     * @param parameters parameters for the model
     */
    public IntSequenceMemoizer(IntSequenceMemoizerParameters parameters) {
        RNG = new MersenneTwisterFast(parameters.seed);
        trueDepth = parameters.depth;
        depth = 0;
        rDepth = 0;
        restCount = new MutableLong(0);
        ecr = new IntRestaurant(null, 0, 0, null, 1, restCount);
        is = new IntSequence(1024);
        discounts = new Discounts(parameters.discounts, parameters.infiniteDiscount);
        ds = new DoubleStack();
        sr = new SeatReturn();
        baseDistribution = parameters.baseDistribution;
        maxNumberRestaurants = parameters.maxNumberRestaurants;
        maxSequenceLength = parameters.maxSequenceLength;
        seed = parameters.seed;

        SeatingArranger.rng = RNG;
    }

    /**
     * Null constructor which uses all the default parameters and an alphabet
     * size of 256.
     */
    public IntSequenceMemoizer(){
        this(new IntSequenceMemoizerParameters(256));
    }
    
    /**
     * {@inheritDoc}
     */
    public void newSequence() {
        depth = 0;
    }

    /**
     * {@inheritDoc}
     */
    public double continueSequence(int type) {
        IntRestaurant r;
        int index;
        double p;

        while (is.length() > maxSequenceLength - 1) {
            is.shorten(restCount);
        }

        while (restCount.value() > maxNumberRestaurants - 2) {
            deleteRandomRestaurant();
        }

        r = getWithInsertion();
        
        index = ds.index();
        p = predictiveProbability(r, type);

        ds.setIndex(index);
        seatAndUpdateDiscount(type, r, p);

        is.append(type);

        if(depth < trueDepth){
            depth++;
        }

        if(baseDistribution.getClass().equals(IntUniformDiscreteDistribution.class)){
            double bp = baseDistribution.probability(type);
            if(bp > 0.0){
                double as = 1.0 / bp ;
                double minP = 6.0 / (double) Integer.MAX_VALUE;
                p = (p + minP) / (1.0 + as * minP);
            }
        }

        return Math.log(p);
    }

    /**
     * {@inheritDoc}
     */
    public double continueSequence(int[] types) {
        double logLik = 0.0;

        for(int type : types){
            logLik += continueSequence(type);
        }

        return logLik;
    }

    /**
     * {@inheritDoc}
     */
    public int[] generate(int[] context, int numSamples) {
        IntDiscreteDistribution dist;
        Iterator<Pair<Integer, Double>> iter;
        double r, cuSum;
        int[] samples;
        Pair<Integer, Double> pair;

        dist = predictiveDistribution(context);
        samples = new int[numSamples];

        for (int i = 0; i < numSamples; i++) {
            iter = dist.iterator();

            r = RNG.nextDouble();
            cuSum = 0.0;
            while (true) {
                pair = iter.next();
                cuSum += pair.second().doubleValue();
                if (cuSum > r) {
                    break;
                }
            }

            samples[i] = pair.first().intValue();
        }

        return samples;
    }

    /**
     * {@inheritDoc}
     */
    public int[] generateSequence(int[] context, int sequenceLength) {
        int[] fullSequence, c, r;
        int index;

        if (context == null) {
            context = new int[0];
        }

        fullSequence = new int[context.length + sequenceLength];
        index = context.length;
        System.arraycopy(context, 0, fullSequence, 0, index);

        for (int i = 0; i < sequenceLength; i++) {
            c = new int[index];
            System.arraycopy(fullSequence, 0, c, 0, index);
            fullSequence[index++] = generate(c, 1)[0];
        }

        assert index == fullSequence.length;

        r = new int[sequenceLength];
        System.arraycopy(fullSequence, context.length, r, 0, sequenceLength);

        return r;
    }

    /**
     * {@inheritDoc}
     */
    public IntDiscreteDistribution predictiveDistribution(int[] context) {
        IntRestaurant r = getWithoutInsertion(context);
        double multFactor = fillMostOfPDF(r);
        
        return new IntHashMapDiscreteDistribution(mostOfPDF, baseDistribution,multFactor);
    }

    /**
     * {@inheritDoc}
     */
    public double predictiveProbability(int[] context, int token) {
        if (context == null) {
            context = new int[0];
        }
        return predictiveProbability(getWithoutInsertion(context), token);
    }

    /**
     * {@inheritDoc}
     */
    public double sequenceProbability(int[] context, int[] sequence) {
        int[] fullSequence, c;
        int index, l;
        double logLik;

        if (context == null) {
            context = new int[0];
        }

        fullSequence = new int[context.length + sequence.length];
        System.arraycopy(context, 0, fullSequence, 0, context.length);
        System.arraycopy(sequence, 0, fullSequence, context.length, sequence.length);

        index = context.length;

        logLik = 0.0;
        for (int i = 0; i < sequence.length; i++) {
            l = index < trueDepth ? index : trueDepth;
            c = new int[l];
            System.arraycopy(fullSequence, index - l, c, 0, l);
            logLik += Math.log(predictiveProbability(c, fullSequence[index]));
            index++;
        }

        return logLik;
    }

    /**
     * {@inheritDoc}
     */
    public double sample(int numSweeps){
        for(int i = 0; i < numSweeps - 1; i++){
            sampleSeatingArrangements(1);
            sampleDiscounts(1);
        }

        sampleSeatingArrangements(1);
        return sampleDiscounts(1);
    }

    /**
     * {@inheritDoc}
     */
    public void sampleSeatingArrangements(int numSweeps) {
        for (int i = 0; i < numSweeps; i++) {
            sampleSeatingArrangements(ecr, null, 0);
        }
    }

    /**
     * {@inheritDoc}
     */
    public double sampleDiscounts(int numSweeps) {
        if (numSweeps > 0) {
            double score = 0.0;
            for (int i = 0; i < numSweeps; i++) {
                score = sampleDiscounts(0.07);
            }
            return score;
        } else {
            return score();
        }
    }

    private void sampleSeatingArrangements(IntRestaurant r, IntSamplingNode parentisn, int d) {
        IntSamplingNode isn;
        double discount;
        int tci, tti, c, t;

        discount = discounts.get(d - r.edgeLength, d);
        isn = new IntSamplingNode(parentisn, discount, baseDistribution);

        tci = 0;
        tti = 1;
        for (int type : r.types) {
            c = r.customersAndTables[tci];
            t = r.customersAndTables[tti];

            isn.setTypeSeatingArrangement(type, SeatingArranger.getSeatingArrangement(c, t, discount), c, t);

            tci += 2;
            tti += 2;
        }

        if (!r.isEmpty()) {
            for (Object child : r.values()) {
                sampleSeatingArrangements((IntRestaurant) child, isn, d + ((IntRestaurant) child).edgeLength);
            }
        }

        isn.sample();
        isn.fillRestaurant(r);
    }

    private double sampleDiscounts(double stdProposal) {
        double logLik, pLogLik, currentValue, proposal;
        boolean accept;

        logLik = score();

        for (int dIndex = 0; dIndex < discounts.length(); dIndex++) {
            currentValue = discounts.get(dIndex);
            proposal = currentValue + stdProposal * RNG.nextGaussian();

            if (proposal > 0.0 && proposal < 1.0) {
                discounts.set(dIndex, proposal);
                pLogLik = score();

                accept = RNG.nextDouble() < Math.exp(pLogLik - logLik);
                if (accept) {
                    logLik = pLogLik;
                } else {
                    discounts.set(dIndex, currentValue);
                }
            }
        }

        currentValue = discounts.getdInfinity();
        proposal = currentValue + stdProposal * RNG.nextGaussian();

        if (proposal > 0.0 && proposal < 1.0) {
            discounts.setDInfinity(proposal);
            pLogLik = score();

            accept = RNG.nextDouble() < Math.exp(pLogLik - logLik);
            if (accept) {
                logLik = pLogLik;
            } else {
                discounts.setDInfinity(currentValue);
            }
        }

        return logLik;
    }

    /**
     * {@inheritDoc}
     */
    public double score() {
        double logLik;
        int tti;

        logLik = 0.0;
        tti = 1;
        for (int i = 0; i < ecr.types.length; i++) {
            logLik += ecr.customersAndTables[tti] * Math.log(baseDistribution.probability(ecr.types[i]));
            tti += 2;
        }

        return logLik += score(ecr, 0);
    }

    private double score(IntRestaurant r, int restaurantDepth) {
        double logLik, discount;
        LogGeneralizedSterlingNumbers lgsn;
        int tci, tti;

        logLik = 0.0;
        if (!r.isEmpty()) {
            for (Object child : r.values()) {
                logLik += score((IntRestaurant) child, restaurantDepth + ((IntRestaurant) child).edgeLength);
            }
        }

        discount = discounts.get(restaurantDepth - r.edgeLength, restaurantDepth);
        lgsn = new LogGeneralizedSterlingNumbers(discount);

        logLik += LogBracketFunction.logBracketFunction(discount, r.tables - 1, discount);
        logLik -= LogBracketFunction.logBracketFunction(1, r.customers - 1, 1.0);

        tci = 0;
        tti = 1;
        for (int i = 0; i < r.types.length; i++) {
            logLik += lgsn.get(r.customersAndTables[tci], r.customersAndTables[tti]);
            tci += 2;
            tti += 2;
        }

        return logLik;
    }

    /**
     * {@inheritDoc}
     */
    public IntSequenceMemoizerParameters getParameters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private IntRestaurant getWithInsertion() {
        int el;
        double discount;
        IntRestaurant r, c, nc;
        int key;
        BackwardsIterator bi;

        assert ds.index() == -1;

        r = ecr;
        rDepth = 0;
        bi = is.backwardsIterator();

        ds.push(discounts.get(0));
        while (rDepth < depth && bi.hasNext()) {
            key = bi.peek();
            c = r.get(key);

            if (c == null) {
                el = bi.available(depth - rDepth);
                ds.push(discounts.get(rDepth, rDepth + el));
                c = new IntRestaurant(r, bi.ind, el, bi.node, 1, restCount);

                if (!r.isEmpty()) {
                    r.incrementLeafNodeCount();
                }

                r.put(key, c);

                rDepth += el;

                return c;
            } else {
                int currentEdgeStart = bi.ind;
                IntSeqNode currentNode = bi.node;

                newKey.setNull();
                int overlap = bi.overlap(c.edgeNode, c.edgeStart, c.edgeLength, newKey);

                assert overlap > 0;

                if (overlap == c.edgeLength) {
                    ds.push(discounts.get(rDepth, rDepth + overlap));
                    rDepth += overlap;

                    c.edgeNode.remove(c);
                    c.edgeNode = currentNode;
                    c.edgeNode.add(c);
                    c.edgeStart = currentEdgeStart;
                    r = c;
                } else {
                    discount = discounts.get(rDepth, rDepth + overlap);
                    ds.push(discount);

                    nc = c.fragmentForInsertion(r, currentEdgeStart, overlap, currentNode, discounts.get(rDepth, rDepth + c.edgeLength), discount, restCount);
                    rDepth += overlap;

                    r.put(key, nc);
                    if (!newKey.isNull()) {
                        nc.put(newKey.value(), c);
                        if (c.edgeStart >= is.blockSize()) {
                            c.edgeNode.remove(c);
                            while(c.edgeStart >= is.blockSize()){
                                c.edgeStart -= is.blockSize();
                                c.edgeNode = c.edgeNode.previous();
                            }
                            c.edgeNode.add(c);
                            assert newKey.value() == c.edgeNode.intChunk()[c.edgeStart] ;
                        }
                    } else {
                        c.edgeNode.remove(c);
                    }

                    r = nc;
                }
            }
        }

        return r;
    }

    private IntRestaurant getWithoutInsertion(int[] context) {
        int index;
        double discount;
        IntRestaurant r, c;
        int key;

        assert ds.index() == -1;

        r = ecr;
        rDepth = 0;
        index = context.length - 1;

        ds.push(discounts.get(0));
        while (rDepth < trueDepth && index > -1) {
            key = context[index];
            c = r.get(key);

            if (c == null) {
                return r;
            } else {
                int overlap = is.overlap(c.edgeNode, c.edgeStart, c.edgeLength, context, index);

                assert overlap > 0;

                index -= overlap;

                if (overlap == c.edgeLength) {
                    ds.push(discounts.get(rDepth, rDepth + overlap));
                    rDepth += overlap;
                    r = c;
                } else {
                    discount = discounts.get(rDepth, rDepth + overlap);
                    ds.push(discount);

                    c = c.fragmentForPrediction(r, discounts.get(rDepth, rDepth + c.edgeLength), discount, restCount);

                    rDepth += overlap;

                    return c;
                }
            }
        }

        return r;
    }

    private void seatAndUpdateDiscount(int type, IntRestaurant r, double p) {
        double discount, multFactor;
        double keepP;

        keepP = p;

        sr.seatInParent = true;
        multFactor = 1.0;
        while (ds.hasNext() && sr.seatInParent) {
            discount = ds.pop();
            p = r.seat(type, p, discount, sr, this);

            discounts.updateGradient(rDepth - r.edgeLength, rDepth, sr.typeTables, sr.customers, sr.tables, p, discount, multFactor);
            if (sr.customers > 0) {
                multFactor *= (double) sr.tables * discount / (double) sr.customers;
            }

            rDepth -= r.edgeLength;
            r = r.parent;
        }

        while (ds.hasNext()) {
            discount = ds.pop();
            p = r.getPP(type, p, discount, sr);

            discounts.updateGradient(rDepth - r.edgeLength, rDepth, sr.typeTables, sr.customers, sr.tables, p, discount, multFactor);
            multFactor *= (double) sr.tables * discount / (double) sr.customers;

            rDepth -= r.edgeLength;
            r = r.parent;
        }

        discounts.stepDiscounts(0.0001, keepP);

        assert rDepth == 0;
        assert r == null;
    }

    private double predictiveProbability(IntRestaurant r, int type) {
        double discount, multFactor, p;
        int index, tci, tti;

        multFactor = 1.0;
        p = 0.0;
        while (ds.hasNext()) {
            discount = ds.pop();
            if (r.customers > 0) {
                if (type <= r.types[r.types.length - 1]) {
                    index = r.getIndex(type);
                    if (r.types[index] == type) {
                        tci = 2 * index;
                        tti = tci + 1;
                        p += multFactor * ((double) r.customersAndTables[tci] - (double) r.customersAndTables[tti] * discount) / (double) r.customers;
                    }
                }
                multFactor *= (double) r.tables * discount / (r.customers);
            }

            r = r.parent;
        }

        p += multFactor * baseDistribution.probability(type);

        return p;
    }

    private double fillMostOfPDF(IntRestaurant r) {
        int tci, tti;
        double multFactor, discount, customers;
        int[] types;
        int[] cAndT;
        MutableDouble md;

        multFactor = 1.0;
        mostOfPDF = new TIntObjectHashMap<MutableDouble>(2 * ecr.types.length, (float) 0.25);

        while (ds.hasNext()) {
            discount = ds.pop();

            if (r.customers > 0) {
                types = r.types;
                cAndT = r.customersAndTables;
                customers = r.customers;

                tci = 0;
                tti = 1;

                for(int key : types){

                    md = mostOfPDF.get(key);

                    if(md == null){
                        mostOfPDF.put(key, new MutableDouble(multFactor * ((double) cAndT[tci] - discount * (double) cAndT[tti]) / customers));
                    } else {
                        md.plusEquals(multFactor * ((double) cAndT[tci] - discount * (double) cAndT[tti]) / customers);
                    }

                    tci += 2;
                    tti += 2;
                }

                multFactor *= discount * r.tables / r.customers;
            }

            r = r.parent;
        }

        return multFactor;
    }

    private IntRestaurant getRandomLeafNode() {
        IntRestaurant r = ecr;
        double totalWeight, cuSum, rand;

        while (!r.isEmpty()) {
            totalWeight = r.numLeafNodesAtOrBelow;
            rand = RNG.nextDouble();
            cuSum = 0.0;
            for (Object child : r.values()) {
                cuSum += (double) ((IntRestaurant) child).numLeafNodesAtOrBelow / totalWeight;
                if (cuSum > rand) {
                    r = (IntRestaurant) child;
                    break;
                }
            }
        }

        return r;
    }

    private void deleteRandomRestaurant() {
        getRandomLeafNode().removeFromTreeAndEdgeNode(restCount);
    }

    public class SeatReturn {

        public boolean seatInParent;
        public int typeTables, customers, tables;

        public void set(boolean seatInParent, int typeTables, int customers, int tables) {
            this.seatInParent = seatInParent;
            this.typeTables = typeTables;
            this.customers = customers;
            this.tables = tables;
        }
    }

    public class NewKey{
        private int value;
        private boolean isNull;

        public NewKey(){
            isNull = true;
        }

        public boolean isNull(){
            return isNull;
        }

        public int value(){
            if(!isNull){
                return value;
            } else {
                throw new RuntimeException("This object is null");
            }
        }

        public void set(int i){
            value = i;
            isNull = false;
        }

        public void setNull(){
            isNull = true;
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException{
        out.writeObject(RNG);
        out.writeInt(depth);
        out.writeInt(trueDepth);
        out.writeObject(is);
        out.writeObject(discounts);
        out.writeObject(baseDistribution);
        out.writeLong(maxNumberRestaurants);
        out.writeLong(maxSequenceLength);
        out.writeLong(seed);
        out.writeObject(restCount);
        out.writeObject(ecr);

        writeEdgeNodeObjects(out);
    }

    private void writeEdgeNodeObjects(ObjectOutputStream out) throws IOException{
        if(!ecr.isEmpty()){
            for(Object c : ecr.values()){
                writeEdgeNodeObjects((IntRestaurant) c, out);
            }
        }
    }
 
    private void writeEdgeNodeObjects(IntRestaurant r, ObjectOutputStream out) throws IOException{
        if(!r.isEmpty()){
            for(Object c : r.values()){
                writeEdgeNodeObjects((IntRestaurant) c, out);
            }
        }

        out.writeInt(r.edgeNode.getIndex());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        RNG = (MersenneTwisterFast) in.readObject();
        depth = in.readInt();
        trueDepth = in.readInt();
        is = (IntSequence) in.readObject();
        discounts = (Discounts) in.readObject();
        baseDistribution = (IntDiscreteDistribution) in.readObject();
        maxNumberRestaurants = in.readLong();
        maxSequenceLength = in.readLong();
        seed = in.readLong();
        restCount = (MutableLong) in.readObject();

        ecr = (IntRestaurant) in.readObject();

        readEdgeNodeObjects(in);
        SeatingArranger.rng = RNG;

        rDepth = 0;
        ds = new DoubleStack();
        sr = new SeatReturn();
        newKey = new NewKey();
    }

    private void readEdgeNodeObjects(ObjectInputStream in) throws IOException{
        if(!ecr.isEmpty()){
            for(Object c : ecr.values()){
                readEdgeNodeObjects((IntRestaurant) c, in);
            }
        }
    }

    private void readEdgeNodeObjects(IntRestaurant r, ObjectInputStream in) throws IOException{
        if(!r.isEmpty()){
            for(Object c : r.values()){
                readEdgeNodeObjects((IntRestaurant) c, in);
            }
        }

        r.edgeNode = is.get(in.readInt());
    }

    public static void main(String[] args) throws FileNotFoundException, IOException{

        IntSequenceMemoizerParameters smp = new IntSequenceMemoizerParameters(100);
        IntSequenceMemoizer sm = new IntSequenceMemoizer(smp);
        sm.continueSequence(new int[]{97,98,99,44,97,98,99,44,97,98,99,44,97,98,99,44,97,98,99});

        sm.sample(100);

        int[] samp = sm.generate(new int[]{97,98}, 100);

        for(int i = 0; i < 100; i++){
            System.out.println(samp[i]);
        }



        /*BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File("/Users/nicholasbartlett/Desktop/train.txt")));
            String line;
            int index = 0;
            while((line = br.readLine()) != null){
                train[index++] = Integer.parseInt(line);
            }

            IntSequenceMemoizerParameters smp = new IntSequenceMemoizerParameters(57847);
            IntSequenceMemoizer sm = new IntSequenceMemoizer(smp);

            System.out.println(smp.discounts[0]);


            sm.continueSequence(train);
            System.out.println(sm.score());
            
            //sm.sample(1);
        } finally {
            br.close();
        }*/
    }
}
