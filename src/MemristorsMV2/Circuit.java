
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
public class Circuit {
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
    private List<Integer> kernel = new ArrayList<>();
    private int totalPulses = 0;
    private int[] randomizedVars;
    
    public Circuit(int numVars) {
        numVars += numVars%2; //if numVars is odd, add 1 so the pairing will match up later
        //create variable array to be randomized and later mapped to MV variables
        randomizedVars = new int[numVars]; 
        for (int i=0; i<numVars; i++) {
            randomizedVars[i] = i;
        } //later: add randomization
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
        int binary = Integer.parseInt(s,2); //convert (ie. '010' to the binary integer 010)
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
    
    public void setEquation(String equation){
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
    }
    
    private String kernelToString(Integer element) {
        String strVal = "";
        for (int position=14; position >=0; position--) {
            if(positionToVar(element, position) != 0) {
                strVal = strVal + positionToVar(element, position);
            }
        }
        return strVal;
    }        
        
    public void printEquation() {
        for (int i=0; i<minterm.length; i++) {
            System.out.println(minterm[i]);
        }
    }
    
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
        System.out.print("Kernel set. " + kernel.size() + " elements: " );
        for (int i=0; i<kernel.size(); i++) {
            System.out.print(kernelToString(kernel.get(i)) + " ");
            //System.out.println(Integer.toBinaryString(kernel.get(i)));
        }
        System.out.println();
}
    
    public void setKMap(int numVars, int kValues[]) {
        dimension = numVars*numVars;
        for(int i=0; i<dimension; i++) {
            if(kValues[i] == 0) {
                offSet.add(mintermToInt(kMap4[i])); //creates offSet array
            }
            if(kValues[i] == 1) {
                onSet.add(mintermToInt(kMap4[i])); //creates onSet array
            }
        }
    }
    
    public void setPLA (int numVars, List<String> stmts) {
        // Add elements to OnSet
        //dimension= numVars*numVars;
        dimension = (int) Math.pow(2, numVars);
        for (int i=0; i<stmts.size(); i++) {
            int element = binaryToElement(stmts.get(i));
            onSetMV.add(element);
        }        

        //Add elements to offSet[], if not covered by onSet
        for (int i=0; i<dimension; i++) {
            int x = i | (int) Math.pow(2, numVars);
            String s = Integer.toBinaryString(x).substring(1);
            int element = binaryToElement(s);
            
            // Now check if this value is not in OnSet
            boolean found = false;
            for (int j=0; j<onSetMV.size(); j++) {
                if (isXContainedInY(element, onSetMV.get(j))) {
                    found = true;
                }
            }
            if (!found) {
                offSetMV.add(element);
            }
        }
        printMap();
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
    */
    
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
    }
    
    public void findKernels() {
        List<Integer> tmpKernel = new ArrayList<>();
        
        for(int i=0; i<onSet.size(); i++) {
            Integer temp = onSet.get(i) | 0x55555555; //OR's bits with 01 01 01... which changes negative variables to dont cares
            if (!tmpKernel.contains(temp)) {
                tmpKernel.add(temp);
            }   
        }
        int maxsize = 0;
        for (int i=0; i<tmpKernel.size(); i++) {
            if (kernelLength(tmpKernel.get(i)) > maxsize) {
                maxsize = kernelLength(tmpKernel.get(i));
            }
        }
        kernel.clear();
        // Now set the kernel elements in sorted order
        for (int len=1; len<= maxsize; len++) {
            for (int j=0; j<tmpKernel.size(); j++) {
                if (kernelLength(tmpKernel.get(j)) == len) {
                    kernel.add(tmpKernel.get(j));
                }
            }
        }
        sortEssentialImplicants();
    }
    
    public void sortEssentialImplicants() {
        // parse through onSet array and see which kernels cover the onSet
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
                // This implies that this onSet element is covered by one
                // and only one kernel, which is at index kIndex
                // Hence it is an Essential Prime Implicant
                // Move kernel kIndex to top
                Integer tmp = kernel.get(kIndex);
                kernel.remove(kIndex);
                kernel.add(0, tmp);
            }
        }
    }
    
    public boolean validKernel(int kern) { //is one kernel valid or not? return true or false
        for(int i=0; i<offSet.size(); i++) {
            if((offSet.get(i) | kern) == kern) {
                return false; 
            }
        }
        return true;
    }
    
    public void evaluateCircuit(boolean batchMode) {
        Diagram diag = new Diagram((int) Math.sqrt(dimension), batchMode);
        for(int i=0; i<dimension; i++) {
            findKernels();
            //printMap();
            
            //System.out.println(kernel.size() + " kernels found.");
            for(int j=0; j<kernel.size(); j++) {
                //System.out.println("Checking kernel "+ kernelToString(kernel.get(j)));
                if (validKernel(kernel.get(j)) == true) {
                    //System.out.println("Add IMPLY Gate to open input");
                    //System.out.println("Add NAND(" + kernelToString(kernel.get(j)) + ") to O-input of IMPLY Gate");
                    diag.addIMPLY();
                    diag.addNAND(kernelToString(kernel.get(j)));
                    
                    // Remove the elements from onSet
                    for(int k=onSet.size()-1; k>=0; k--) {
                        if(isXContainedInY(onSet.get(k), kernel.get(j))) {
                            //System.out.println("Removing from onset "+kernelToString(onSet.get(k)));
                            onSet.remove(k);//remove onSet[k] from list
                        }
                    }
                    
                    // Now also remove the additional subset kernels
                    for(int k=kernel.size()-1; k>j; k--){
                        if(isXContainedInY(kernel.get(k), kernel.get(j))) {
                            //System.out.println("Removing kernel " + kernelToString(kernel.get(k)));                          
                            kernel.remove(k);//remove kernel[k] from list
                        }                        
                    }                    
                }
            }

            if(onSet.isEmpty()) {break;} // We are done
            
            //negate the cube by switching onSet and offSet arrays
            List<Integer> temp = new ArrayList<>(onSet);            
            onSet.clear();
            onSet.addAll(offSet);
            offSet.clear();
            offSet.addAll(temp);
            
            if (!onSet.isEmpty()) {
                //System.out.println("Add NOT to open input");
                //printMap();
                diag.addNOT();
            }
        }
        diag.print();
        totalPulses = diag.getTotalPulses();
    }
    
    public int getTotalPulses() { 
        return totalPulses; 
    }
    
}