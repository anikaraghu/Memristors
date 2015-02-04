
package MemristorsMV2;

import MemristorsMV2.*;
import java.util.*;

/**
 *
 * @author Anika Raghuvanshi
 */

/*******************************************************************************
 Internal representation
 08   07   06   05   04   03   02   01   00    //position number
  R    A    B    C    D    E    F    G    H    //
 00 0100 1000 0001 0000 0000 0000 0000 0000    //position values-this shows A1B0C3
*******************************************************************************/
public class CircuitMV2 {
    private String iEquation;
    private String minterm[];
    private List<Integer> onSet = new ArrayList<>();
    private List<Integer> offSet = new ArrayList<>();  
    private List<Integer> onSetMV = new ArrayList<>();
    private List<Integer> offSetMV = new ArrayList<>();  
    private String kMap4[] = {"abcd","abcD","abCD","abCd",
                              "aBcd","aBcD","aBCD","aBCd",
                              "ABcd","ABcD","ABCD","ABCd",
                              "Abcd","AbcD","AbCD","AbCd"};
    private int dimension;
    private int totalPulses = 0;
    private int[] randomizedVars;
    private int kernelNumber = 0;
    private int numVars;
    private int numMultiVars;
    
    public CircuitMV2(int numVars) {
        this.numVars = numVars;
        numVars += numVars%2; //if numVars is odd, add 1 so the pairing will match up later
        //create variable array to be randomized and later mapped to MV variables
        randomizedVars = new int[numVars]; 
        for (int i=0; i<numVars; i++) {
            randomizedVars[i] = i;
        } //later: add randomization
        numMultiVars = randomizedVars.length;
    }
    
    //given the position variable, returns the position number
    private int varPosition(char c) {
        int position = 14 - (c - 'A'); 
        return position;
    }
    
    //reverse of previous method
    private char positionToVar(int x, int pos) {
        char var;
        if(getVarValueAtPosition(x, pos) == 1) {
            var = (char) ('A' + (char) (14 - pos));
        }
        else if(getVarValueAtPosition(x, pos) == 2) {
            var = (char)('a' + (char) (14 - pos));
        }
        else {var = 0;}
        return var;
    }
    
    //sets a bit equal to 01
    private int setPositiveValue(int x, int position) {
        return (x & ~(2 << position * 2));
    }
    
    private int setPositiveValue(int x, char c) {
        int position = varPosition(c);
        int element = setPositiveValue(x, position);
        return element;
    }
    
    //sets a bit equal to 10 assuming the initial values of the bits are 11
    private int setNegativeValue(int x, int position) {
        return (x & ~(1 << position * 2));
    }
    
    private int setNegativeValue(int x, char c) {
        int position = varPosition(c);
        int element = setNegativeValue(x, position);
        return element;
    }
    
    //returns the value(1, 2, or 3) of a bit given its position number
    private int getVarValueAtPosition (int x, int pos) {
        int mask = 3 << (pos *2);
        int value = (x & mask) >> (pos *2);
        return value;
    }
    
    //returns the value(1, 2, or 3) of a bit given its position variable
    private int getVarValue (int x, char c) {
        int pos = varPosition(c);
        return getVarValueAtPosition(x, pos);
    }
    
    //returns true if x is contained in y
    private boolean isXContainedInY (int x, int y) {
        if((x|y) == y) {return true;}
        return false;
    }
    
    private int binaryToElement(String s) {
        int element = 0xFFFFFFFF; //creates 32-bit int with all 1'
        char temp[] = s.toCharArray();
        for (int j=0; j<temp.length; j++) {
            int position = (14 - j);
            if (temp[j] == '0') {
                element = setNegativeValue(element, position);
            }
            else if (temp[j] == '1') {
                element = setPositiveValue(element, position);                  
            }
        }
        return element;
    }
    
    private int binaryToMVElement(int binary) {
        int element = 0;
        int i=0;
        int n1, n2, bit1, bit2, temp;
        while (i<randomizedVars.length) {
            n1 = randomizedVars[i];
            n2 = randomizedVars[i+1];
            n1 = randomizedVars.length - 1 - n1; //switch bit order
            n2 = randomizedVars.length - 1 - n2;
            bit2 = ((1<<n2) & binary) >> n2; //extract second bit
            bit1 = (((1<<n1) & binary) >> n1) << 1; //extract first bit
            temp = bit1 | bit2;
            temp = (0b1000)>> temp; //convert binary to multivariable bit
            element = (element << 4) | temp; //shifts bits over, new pair takes rightmost four bits
            i+=2;
        }
        return element;
    }
    
    /*public void setEquation(String equation){
        iEquation = equation;  
        minterm = iEquation.split("\\+");
       
        for(int i=0; i<minterm.length; i++) {
            onSet.add(mintermToInt(minterm[i]));
        }
    }
    
    private int mintermToInt(String minterm) {
        int element = 0xFFFFFFFF;
        char temp[] = minterm.toCharArray(); //separates the characters
        for(int j=0; j<temp.length; j++) {
            if(temp[j] >= 'A' && temp[j] <= 'O'){
                setPositiveValue(element, temp[j]);
            }
            if(temp[j] >= 'a' && temp[j] <= 'o'){
                setNegativeValue(element, temp[j]);
            }
         }
         return element;
    }*/
    
    private String kernelToString(Integer element) {
        String strVal = "";
        String temp;
        char variable = 'Z';
        int field;
        while(element > 0) {
            field = element & 0b1111; //extracts last 4 bits
            if(field == 0b1111) {
                temp = "";
            } else { //the following code should print X(013) if X = 1101
                temp = variable + "(";
                if((field & 0b1000) == 0b1000) {temp += "0";}
                if((field & 0b0100) == 0b0100) {temp += "1";}
                if((field & 0b0010) == 0b0010) {temp += "2";}
                if((field & 0b0001) == 0b0001) {temp += "3";}
                temp += ")";
                strVal = temp + strVal;
            }
            variable--; //goes from Z to Y to X, etc.
            element = element >> 4; //on to the next variable
        }
        return strVal;
    }        
    
    private int kernelPulseCount(int element) {
        //if the pattern has one zero, e.g. 1101 =X(013), it needs one pulse
        //if the pattern has two zeros, e.g. 1001 = (X03), it is an AND of
        //   two primitives e.g. X(013) & X(023), so it will need 2 pulses
        //if the pattern has three zeros, e.g. 0001, then it is an AND of 
        //   three primitives e.g. X(013) & X(023) & X(123), so it will need
        //   3 pulses.
        //also to note a pattern of 1111 would mean that the MVvariable is 
        //   not present in expression, so it is 0 pulses
        //so counting the zeros in the kernels gives the accurate count of pulses
        //leading zeros need to be ignored in the integer
        //Only inspect the rightmost numMultiVars*4 bits, and count the zeros
        int count = 0;
        for (int i=0; i<numMultiVars*4; i++) {
            if ((element & 0b1) == 0) {count++;}
            element = element >> 1;
        }
        return count;
    }
    
    /*public void printEquation() {
        for (int i=0; i<minterm.length; i++) {
            System.out.println(minterm[i]);
        }
    }*/
    
    public void printMap() {
        System.out.print("OnSet " + onSetMV.size() + " elements : " );
        for (int i=0; i<onSetMV.size(); i++) {
            System.out.print(kernelToString(onSetMV.get(i)) + " ");
            //System.out.println(Integer.toBinaryString(onSet.get(i)));
        }
        System.out.println();
        
        System.out.print("Off-set. " + offSetMV.size() + " elements: " );
        for (int i=0; i<offSetMV.size(); i++) {
            System.out.print(kernelToString(offSetMV.get(i)) + " ");
            //System.out.println(Integer.toBinaryString(offSet.get(i)));
        }
        System.out.println();
}
    
    /*public void setKMap(int numVars, int kValues[]) {
        dimension = numVars*numVars;
        for(int i=0; i<dimension; i++) {
            if(kValues[i] == 0) {
                offSet.add(mintermToInt(kMap4[i])); //creates offSet array
            }
            if(kValues[i] == 1) {
                onSet.add(mintermToInt(kMap4[i])); //creates onSet array
            }
        }
    }*/
    
    public void setPLA (int numVars, List<String> stmts) {
        //Add elements to OnSet
        //dimension = numVars * numVars;
        dimension = (int) Math.pow(2, numVars);
        for (int i=0; i<stmts.size(); i++) {
            int element = binaryToElement(stmts.get(i));
            onSet.add(element);
        }        
                
        //Add elements to offSet[], if not covered by onSet
        for (int i=0; i<dimension; i++) {
            int x = i | (int) Math.pow(2, numVars);
            String s = Integer.toBinaryString(x).substring(1);
            int element = binaryToElement(s);
            
            // Now check if this value is not in OnSet
            boolean found = false;
            for (int j=0; j<onSet.size(); j++) {
                if (isXContainedInY(element, onSet.get(j))) {
                    found = true;
                }
            }
            int MVelement = binaryToMVElement(i);
            if (found) {
                onSetMV.add(MVelement);
            } else {
                offSetMV.add(MVelement);
            }
        }
        //printMap();
    }
    
    //convert onSet to onSetMV and offset to offSetMV
    /*private void createMVSet (int numVars) {
        numVars += numVars%2; //if numVars is odd, add 1 so the pairing will match up later
        //create variable array to be randomized and later mapped to MV variables
        int[] randomizedVars = new int[numVars]; 
        for (int i:randomizedVars) {randomizedVars[i] = i;} //later: add randomization
        for (int element:onSet) {
            
        }
    }
    
    private int kernelLength (Integer k){
        int val = k.intValue();
        int size = 0;
        //count zeros
        for (int i=0; i<32; i++) {
            if (((val >>> i) & 0x1) == 0) {
                size++;
            }
        }
        return size;
    }*/
    
    public int getNextKernel(){
        int kernel = 0;
        //mask represents all possible states of a multivariable (eg. X(0), X(13), etc.)
        int[] mask = {0b1111,0b0111,0b1011,0b1101,0b1001,0b0101,0b0011,0b0001};
        if(kernelNumber >= Math.pow(8, numMultiVars)) {return 0;}
        int index;
        for(int i=0; i<numMultiVars; i++) {
            index = ((kernelNumber >> 3*i) & 0b111); 
            kernel = (kernel | (mask[index] << 4*i));
        }
        kernelNumber++;
        return kernel;
    }
    
    /*public void sortEssentialImplicants() {
        //parse through onSet array and see which kernels cover the onSet
        for(int i=0; i<onSet.size(); i++) {
            int count = 0;
            int kIndex = 0;
            for (int k=0; k<kernel.size(); k++) {
                if(isXContainedInY(onSet.get(i), kernel.get(k))) {
                    count++;
                    kIndex = k;
                }
            }
            if (count == 1) {
                //This implies that this onSet element is covered by one
                //and only one kernel, which is at index kIndex
                //Hence it is an Essential Prime Implicant
                //Move kernel kIndex to top
                Integer tmp = kernel.get(kIndex);
                kernel.remove(kIndex);
                kernel.add(0, tmp);
            }
        }
    }*/
    
    public boolean validKernel(int kern) { //is one kernel valid or not? return true or false
        for(int i=0; i<offSetMV.size(); i++) {
            if((offSetMV.get(i) | kern) == kern) {
                return false; 
            }
        }
        return true;
    }
    
    public boolean containedInOnset(int kern) { //is one kernel valid or not? return true or false
        for(int i=0; i<onSet.size(); i++) {
            if((onSet.get(i) | kern) == kern) {
                return true; 
            }
        }
        return false;
    }
    
    public void evaluateCircuit(boolean batchMode) {
        //printMap();
        int kernel;
        Diagram diag = new Diagram((int) Math.sqrt(dimension), batchMode);
        //5 pulses are needed for a single-value to multi-value decoder
        //all decoders can work simultaneously, so only 5 pulses are needed
        //in total for all decoders
        totalPulses = 5;
        while (!onSetMV.isEmpty()) {
            while ((kernel=getNextKernel()) > 0) {                
                boolean flag = false;
                if (validKernel(kernel) == true) {
                    //Remove the elements from onSetMV
                    for(int j=onSetMV.size()-1; j >= 0; j--) {
                        if(isXContainedInY(onSetMV.get(j), kernel)) {
                            onSetMV.remove(j);//remove onSet[j] from list
                            flag = true;
                        }
                    } 
                    if(flag == true) {
                        diag.addIMPLY();
                        diag.addNAND(kernelToString(kernel)); 
                        totalPulses += kernelPulseCount(kernel) + 1;
                        //System.out.println(kernelToString(kernel));
                    }
                }
            }
            if(onSetMV.isEmpty()) {break;} //we are done
            
            //negate the cube by switching onSet and offSet arrays
            List<Integer> temp = new ArrayList<>(onSetMV);            
            onSetMV.clear();
            onSetMV.addAll(offSetMV);
            offSetMV.clear();
            offSetMV.addAll(temp);
            diag.addNOT();
            totalPulses++; //1 pulse for a NOT gate
            kernelNumber = 0;
        }
        
        diag.print();
        //totalPulses = diag.getTotalPulses();
    }
    
    public int getTotalPulses() { 
        return totalPulses; 
    }
}