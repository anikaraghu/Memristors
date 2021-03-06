//main method
package MemristorsTANT;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Anika Raghuvanshi
 */
public class MemristorsTANT {

    boolean batchMode = false;

    public static void main(String[] args) throws IOException {
        MemristorsTANT m = new MemristorsTANT();
        m.m_main(args);
    }    
    
    public void m_main(String[] args) throws IOException {
        Scanner reader = new Scanner(System.in);
        
        if (args.length > 0) {
            batchMode = true;
            String fname = args[0];
            if (fname.endsWith(".txt") || fname.endsWith(".TXT") ) {
                readKMapFile(fname);
            }
            else if (fname.endsWith(".pla") || fname.endsWith(".PLA")) {
                readPLAFile(fname);
            }
            else if (fname.endsWith(".plx") || fname.endsWith(".PLX")) {
                readPLXFile(fname);
            }   
            return;
        }        
        
        String equation;       
        while(true) {
            System.out.print("Enter logic equation, or Filename (*.txt, *.pla, *.plx) : ");
            equation = reader.nextLine();
         
            if (equation.isEmpty()) {
                break;
            }
            else if (equation.endsWith(".txt") || equation.endsWith(".TXT") ) {
                readKMapFile(equation);
            }
            else if (equation.endsWith(".pla") || equation.endsWith(".PLA")) {
                readPLAFile(equation);
            }
            else if (equation.endsWith(".plx") || equation.endsWith(".PLX")) {
                readPLXFile(equation);
            }   
            else {
                CircuitTANT circuit = new CircuitTANT();            
                circuit.setEquation(equation);
                circuit.evaluateCircuit(batchMode);
            }   
        }
    }
    
    public void readKMapFile(String fname) throws IOException {
         Scanner fReader = new Scanner(new File(fname));
         
         int dimension = fReader.nextInt();
         int kValues[] = new int [dimension*dimension];
         for(int i=0; i<dimension*dimension; i++) {
             kValues[i] = fReader.nextInt();
         }
         CircuitTANT circuit = new CircuitTANT();            
         circuit.setKMap(dimension,kValues);
         circuit.evaluateCircuit(batchMode);
    }
  
    public void readPLAFile(String fname) throws IOException {
         Scanner fReader = new Scanner(new File(fname));
         int numVars = 0;
         int numStmts = 0;
         List<String> stmts = new ArrayList<>();
         
         while (fReader.hasNext()) {
            String line = fReader.nextLine();

            if (line.startsWith("#") || line.startsWith(".type")) {
                continue;          
            }
            else if (line.startsWith(".i")) {
                numVars = Integer.parseInt(line.substring(3));           
            }
            else if (line.startsWith(".o")) {
                if(line.endsWith("1")!=true) {return;} 
            }
            else if (line.startsWith(".p")) {
                numStmts = Integer.parseInt(line.substring(3));
            }
            else if (line.startsWith(".e")) {
                break;
            }
            else {stmts.add(line.substring(0, numVars));} 
         }
         
         CircuitTANT circuit = new CircuitTANT();   
         circuit.setPLA(numVars, stmts);
         circuit.evaluateCircuit(batchMode);

         System.out.println(fname + ", " + numVars + ", " +
                 numStmts + ", " + circuit.getTotalPulses()); 
       
    }
    
    public void readPLXFile(String fname) throws IOException {
         Scanner fReader = new Scanner(new File(fname));
         int numVars = 0;
         int numStmts = 0;
         List<String> stmts0 = new ArrayList<>();
         List<String> stmts1 = new ArrayList<>();         
       
         while (fReader.hasNext()) {
            String line = fReader.nextLine();

            if (line.startsWith("#") || line.startsWith(".type")) {
                continue;          
            }
            else if (line.startsWith(".i")) {
                numVars = Integer.parseInt(line.substring(3));           
            }
            else if (line.startsWith(".o")) {
                if(line.endsWith("1")!=true) {return;} 
            }
            else if (line.startsWith(".p")) {
                numStmts = Integer.parseInt(line.substring(3));
            }
            else if (line.startsWith(".e")) {
                break;
            }
            else if (line.endsWith("0")) {
                stmts0.add(line.substring(0, numVars));
            } 
            else {
                stmts1.add(line.substring(0, numVars));
            }
         }       
        
         CircuitTANT circuit = new CircuitTANT();   
         circuit.setPLX(numVars, stmts0, stmts1);
         circuit.evaluateCircuit(batchMode);

         System.out.println(fname + ", " + numVars + ", " +
                 numStmts + ", " + circuit.getTotalPulses()); 
       
    }
}
