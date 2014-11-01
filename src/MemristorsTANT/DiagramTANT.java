
package MemristorsTANT;

import java.awt.BorderLayout;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Anika Raghuvanshi
 */
public class DiagramTANT {
    private List<String> steps = new ArrayList<>();
    private String equation = "";
    private List<String> level3loops = new ArrayList<>();
    
    CircuitPanelTANT p;
    boolean batchMode;
    int pulseCount = 0;
    
    public  DiagramTANT (int numVars, boolean batchMode) {
        this.batchMode = batchMode;
        
        if (!batchMode) {
            JFrame j = new JFrame("MemristorCircuit");

            p = new CircuitPanelTANT();
            p.setVariables(numVars);
            
            JTextArea t1 = new JTextArea(1, 10000);
            p.add(t1);
            JTextArea t2 = new JTextArea(1000, 1);
            p.add(t2);

            JScrollPane s = new JScrollPane(p);

            j.add(s, BorderLayout.CENTER);
            
            //j.add(p);
            j.setSize(p.getHorizontalSize()+100,700);
            j.setVisible(true);
            
            //this.add(scrollFrame);

        }
    }

    public void setL3loop(String vars) {
        if (!level3loops.contains(vars)) {
            level3loops.add(vars);
            pulseCount += vars.length()+1;
            if (!batchMode) {p.setL3loop(vars);}
        }
    }
        
    public void addHead(String vars) {
        equation += vars;
        pulseCount += vars.length()+1;
        if (!batchMode) {p.addHead(vars);}
    }

    public void addTail(String vars) {
        equation += ".";
        equation += vars.toLowerCase();
        if (!batchMode) {p.addTail(vars.toLowerCase());}
    }
    
    public void addOR() {
        equation += "+";
        pulseCount++;
        if (!batchMode) {p.addOR();}   
    }
    
    public void print(){
        System.out.println(equation);
    }
    
    public int getTotalPulses() {
        return pulseCount;
    }
}
