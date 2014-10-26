
package MemristorsTANT;

import DecisionFunction.DecisionFnMethod1;
import DecisionFunction.DecisionFnMethod2String;
import DecisionFunction.DecisionFnMethod2;
import DecisionFunction.DecisionFnMethod3;
import MemristorsSOP.DiagramSOP;
import java.util.*;


/**
 *
 * @author Anika Raghuvanshi
 */

/*******************************************************************************
 Internal representation
 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00    //position number
  R  A  B  C  D  E  F  G  H  I  J  K  L  M  N  O    //position variable(first bit is reserved)
 11 11 11 01 11 10 11 11 11 11 11 11 11 11 11 11    //position values-this shows C!E or Ce
*******************************************************************************/
public class CircuitTANT {
    private String iEquation;
    private String minterm[];
    private List<Integer> onSet = new ArrayList<>();
    private List<Integer> offSet = new ArrayList<>();
    private List<Integer> dontCareSet = new ArrayList<>();
    private List<Integer> minTerms = new ArrayList<>();
    private List<Integer> primeImplicants = new ArrayList<>();
    
    private String kMap4[] = {"abcd","abcD","abCD","abCd",
                              "aBcd","aBcD","aBCD","aBCd",
                              "ABcd","ABcD","ABCD","ABCd",
                              "Abcd","AbcD","AbCD","AbCd"};
    private int dimension;
    private List<Integer> kernel = new ArrayList<>();
    private int totalPulses = 0;
    
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
        System.out.println(" This is the on-set. " + onSet.size() + " elements." );
        for (int i=0; i<onSet.size(); i++) {
            System.out.print(kernelToString(onSet.get(i)) + " ");
        }
        System.out.println();
        
        System.out.println(" This is the off-set. "+ offSet.size() + " elements." );        
        for (int i=0; i<offSet.size(); i++) {
            System.out.print(kernelToString(offSet.get(i)) + " ");
        }  
        System.out.println(); 
        
        System.out.println(" This is the dont Care set. "+ dontCareSet.size() + " elements." );
        for (int i=0; i<dontCareSet.size(); i++) {
            System.out.print(kernelToString(dontCareSet.get(i)) + " ");
        }
        System.out.println();
   

        System.out.println(" This is the prime implicants set. "+ primeImplicants.size() + " elements." );
        for (int i=0; i<primeImplicants.size(); i++) {
            System.out.print(kernelToString(primeImplicants.get(i)) + " ");
        }
        System.out.println();
        
        /*
        System.out.println(" This is the kernel set. "+ kernel.size() + " elements." );
        for (int i=0; i<kernel.size(); i++) {
            System.out.print(kernelToString(kernel.get(i)) + " ");
        }
        System.out.println();
        */
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
        dimension= (int) Math.pow(2, numVars);
        for (int i=0; i<stmts.size(); i++) {
            int element = binaryToElement(stmts.get(i));
            onSet.add(element);
        }

        //Add elements to offSet[], if not covered by onSet
        int totVars = (int) Math.pow(2, numVars);
        for (int i=0; i< totVars; i++) {
            int x = i | (int) totVars;
            String s = Integer.toBinaryString(x).substring(1);
            int element = binaryToElement(s);
            
            // Now check if this value is not in OnSet
            boolean found = false;
            for (int j=0; j<onSet.size(); j++) {
                if (isXContainedInY(element, onSet.get(j))) {
                    found = true;
                }
            }
            if (!found) {
                offSet.add(element);
            }
//            if (found) {
//                minTerms.add(element);
//            } else {
//                offSet.add(element);
//            }
        }
        //printMap();
    }
    
    public void setPLX (int numVars, List<String> stmts0, List<String> stmts1) {
        // Add elements to onSet and offSet
        dimension= (int) Math.pow(2, numVars);
        for (int i=0; i<stmts0.size(); i++) {
            int element = binaryToElement(stmts0.get(i));
            offSet.add(element);
        }
        for (int i=0; i<stmts1.size(); i++) {
            int element = binaryToElement(stmts1.get(i));
            onSet.add(element);
        }
        printMap();
    }
    
    private int kernelLength (Integer k){
        int val = k.intValue();
        int size = 0;
        //count bits
        for (int i=0; i<32; i++) {
            if (((val >>> i) & 0x1) == 0) {
                size++;
            }
        }
        return size;
    }
    
    private int zeroCount (Integer k){
        int val = k.intValue();
        int size = 0;
        //count zeros
        for (int i=0; i<32; i+=2) {
            if (((val >>> i) & 0x1) == 0) {
                size++;
            }
        }
        return size;
    }
    
    private boolean isPositiveKernel (Integer k){        
        // Bitwise OR bytes with 10101010 (0xAA)
        // this changes positive variables (01) DBits to 11
        Integer temp = k | 0xAAAAAAAA;
        if (temp == 0xFFFFFFFF) {
            //System.out.println("Kernel "+ kernelToString(k) + " is positive.");
            return true;
        }
        //System.out.println("Kernel " + kernelToString(k) + " is NOT positive.");
        return false;
    }
    
    private void createImplicants() { 
        for(int i:onSet) {
            findPrimeImplicants(i);
        } 
    }
    
    private boolean findPrimeImplicants(int variable) {
        if(!validKernel(variable)) {return false;} // return false if variable is NOT a validKernel
        int flag = 0;
        //goes through 2 bits at a time along the entire variable
        for(int mask = 0x30000000; mask!=0; mask=mask>>>2) { 
            if((variable|mask) == variable) continue;
            if(findPrimeImplicants(variable|mask)) {flag++;} //call recursively for each subset       
        }
        
        //checks that a larger kernel was not added to primeImplicant 
        //list and that variable is not already in list
        if(flag == 0 && !primeImplicants.contains(variable)) { 
            primeImplicants.add(variable);
        }
        return true;
    } 
    
     /**
     * Group all Implicants with same Positive Kernels together 
     * For example: 
     *      ABef cDe ABgh Dghj abD Cde ghk 
     * will be grouped into 4 groups as below
     *      ABef, ABgh (AB as common positive kernel
     *      cDE Dghj abD (D as common postive kernel)
     *      Cde (only one implicant with C)
     *      ghk (no positive kernel)
     */
    
    private ArrayList<ArrayList<Integer>> groupPrimeImplicants () {
        ArrayList<ArrayList<Integer>> implicantGroups = new ArrayList<>();
              
        for(int i: primeImplicants) {
            int positiveKernel = i|(0x55555555); //OR with mask of 010101...
            int flag = 0;
            for(ArrayList<Integer> a: implicantGroups) {
                int element = a.get(0);
                if((element|(0x55555555)) == positiveKernel) {
                    a.add(i);
                    flag++;
                }
            }
            if(flag == 0) {
                ArrayList<Integer> newPositiveGroup = new ArrayList<>();
                newPositiveGroup.add(positiveKernel);
                implicantGroups.add(newPositiveGroup);
            }            
        }
        
        return implicantGroups;
        
    }

     /**
     * Take a group of Implicants from the list, and generate a TANT term 
     * Create a new list with these TANT terms
     */
    
    private ArrayList<ArrayList<Integer>> createTANTterms 
            (ArrayList<ArrayList<Integer>> implicantGroups) {
        
        ArrayList<ArrayList<Integer>> TANTterms = new ArrayList<>();
        
        for(ArrayList<Integer> a:implicantGroups) {
            int element = a.get(0);
            int positiveKernel = (element|(0x55555555));
            ArrayList<Integer> TANTgroup = new ArrayList<>();
            TANTgroup.add(positiveKernel);
                        
            //find which terms are part of offSet
            ArrayList<Integer> zeroTerms = new ArrayList<>();
            for(int i:offSet) {
                if(isXContainedInY(i, positiveKernel)) {
                    zeroTerms.add(i);
                }
            }

            while(zeroTerms.size() > 0) {
                //out of the terms in offset, how many zeros are contained in the
                //term with the most zeros (ie. if zeroTerms contained 0011 and
                //0111, numZeros would equal 2 at the end of the loop
                int numZeros = 0;
                for (int i : zeroTerms) {
                    int zeroCount = zeroCount(i);
                    if (zeroCount > numZeros) {
                        numZeros = zeroCount;
                    }
                }

                //Find all terms in zeroTerms which contain this amount of zeros.
                //For these terms, change the zeros (10 bit) to a 11 bit and add it
                //to the TANT group which already contains the positive kernel
                for (int i : zeroTerms) {
                    int zeroCount = zeroCount(i);
                    //System.out.println(kernelToString(i) + " " + zeroCount);
                    if (zeroCount == numZeros) {
                        TANTgroup.add(i | 0x55555555);
                    }
                }
                
                // Now, remove the terms from zeroTerms, if it is covered by the
                // tail factors that were just added
                
                for (int i = zeroTerms.size() - 1; i >= 0; i--) {
                    for (int j=1; j<TANTgroup.size(); j++) { // start from 1
                        if (isXContainedInY(zeroTerms.get(i), TANTgroup.get(j))) {
                            zeroTerms.remove(i);
                            break;
                        }
                    }                        
                }
            }
            
            TANTterms.add(TANTgroup);
        }
        // Iterate through implicantGroups
        // for each group -
        //    first component of the term is Postive kernel of the group
        //    identify the T-Implicants (negative kernels) 
              
        
        return TANTterms;
        
    }
    
    public boolean validKernel(int kern) { //is one kernel valid or not? return true or false
        for(int i=0; i<offSet.size(); i++) {
            if(isXContainedInY(offSet.get(i),kern)) {
                //System.out.println("not valid kernel");
                return false; 
            }
        }
        //System.out.println("valid kernel");
        return true;
    }
    
    private ArrayList<ArrayList<Integer>> coveringTable (List<Integer> minterms, List<Integer> implicants) {
        ArrayList<ArrayList<Integer>> terms = new ArrayList<>();
        
        for (int i=0; i<minterms.size(); i++) {
            ArrayList<Integer> term = new ArrayList<>();
            for (int j=0; j<implicants.size(); j++) {
                if (isXContainedInY(minterms.get(i), implicants.get(j))) {
                    term.add(j+1); //add the index of the implicant to term 
                }
            }
            terms.add(term);
        }
        return terms;
    }
    
    public void evaluateCircuit(boolean batchMode) {        
        createImplicants();
        printMap();

        ArrayList<ArrayList<Integer>> implicantGroups = groupPrimeImplicants();        
        System.out.println("Groups formed : " + implicantGroups.size());
        System.out.println(implicantGroups);
        
        ArrayList<ArrayList<Integer>> tantTerms = 
                createTANTterms(implicantGroups);
        
        // print TANT terms here for debugging
        for (ArrayList<Integer> term: tantTerms) {
            System.out.print("(" );
                    for (Integer i:term) {
                        System.out.print(kernelToString(i) + ", ");
                    }
            System.out.println(") + " );
        }
                     
        DiagramTANT diag = new DiagramTANT((int) Math.sqrt(dimension), batchMode);
        
        for (ArrayList<Integer> term: tantTerms) {
            diag.addHead(kernelToString(term.get(0)));
            for (int i=1; i<term.size(); i++) {
                diag.addTail(kernelToString(term.get(i)));
            }                    
            diag.addOR();
        }
        
        //diag.print();
        totalPulses = diag.getTotalPulses();
    }
    
    public int getTotalPulses() { 
        return totalPulses; 
    }
}
