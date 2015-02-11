//main program for Two Working Memristor Method
package MemristorsMV2;
import MemristorsMV2.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Anika Raghuvanshi
 */
public class MemristorsMV2 {

    boolean batchMode = false;

    public static void main(String[] args) throws IOException {
        MemristorsMV2 m = new MemristorsMV2();
        m.m_main(args);
    }    
    
    public void m_main(String[] args) throws IOException {
        Scanner reader = new Scanner(System.in);
        
        //if program is run in command line, does not print ISD notation
        if (args.length > 0) {
            batchMode = true;
            readPLAFile(args[0]);
            return;
        }        
        String equation;
        while(true) {
            System.out.print("Enter logic equation, or Filename (*.txt, *.pla) : ");
            equation = reader.nextLine();
            if (equation.isEmpty()) {
                break;
            }
            /*else if (equation.endsWith(".txt")) {
                readKMapFile(equation);
            }*/
            else if (equation.endsWith(".pla")) {
                readPLAFile(equation);
            }            
            /*else {
                CircuitMV2 circuit = new CircuitMV2();
                circuit.setEquation(equation);
                circuit.evaluateCircuit(batchMode);
            }  
            */
        }
    }
    
    /*public void readKMapFile(String fname) throws IOException {
         Scanner fReader = new Scanner(new File(fname));
         
         int dimension = fReader.nextInt();
         int kValues[] = new int [dimension*dimension];
         for(int i=0; i<dimension*dimension; i++) {
             kValues[i] = fReader.nextInt();
         }
         CircuitMV2 circuit = new CircuitMV2(dimension);            
         circuit.setKMap(dimension,kValues);
         circuit.evaluateCircuit(batchMode);
    }*/    
  
    public void readPLAFile(String fname) throws IOException {
         Scanner fReader = new Scanner(new File(fname));
         int numVars = 0;
         int numStmts = 0;
         boolean odd = false;
         List<String> stmts = new ArrayList<>();
         
         while (fReader.hasNext()) {
            String line = fReader.nextLine();

            if (line.startsWith("#") || line.startsWith(".type")) {
                continue;          
            }
            else if (line.startsWith(".i")) {
                numVars = Integer.parseInt(line.substring(3)); 
                if(numVars % 2 == 1) {odd = true;}
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
            else {
                //if numVars is odd, add an additional variable so the multi-valued
                //variable pairing will work out
                if(odd) {
                    String temp = line.substring(0, numVars) + '-';
                    stmts.add(temp);
                } else {
                    stmts.add(line.substring(0, numVars));
                }
                
            } 
         }
         
         if(odd) {numVars += 1;}
         CircuitMV2 circuit = new CircuitMV2(numVars);   
         circuit.setPLA(numVars, stmts);
         circuit.evaluateCircuit(batchMode);

         //System.out.println(fname + ", " + circuit.getTotalPulses()); 
         System.out.println(circuit.getTotalPulses());
         
         /* System.out.println(
                 fname + ", "
                 + numVars + ", "
                 + numStmts + ", " 
                 + circ.getTotalPulses());  */  
    }
}
