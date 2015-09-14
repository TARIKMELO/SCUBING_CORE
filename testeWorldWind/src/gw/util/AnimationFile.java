/*
 * $Id: AnimationFile.java 4 2008-07-21 17:57:48Z od $
 * 
 * This file is a part of the GeoWind package, a library for visualizing
 * data from GeoTools in Nasa WorldWind.
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 *     1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software
 *     in a product, an acknowledgment in the product documentation would be
 *     appreciated but is not required.
 * 
 *     2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 * 
 *     3. This notice may not be removed or altered from any source
 *     distribution.
 */
package gw.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Ian Overgard
 */
public class AnimationFile {

    String[] columns;
    String[] columnTypes;
    ArrayList<Timestep> animationData ;
    
    public class Timestep {
        public String timestamp;
        public ArrayList<String[]> data = new ArrayList<String[]>();
        
        public Timestep(String[] tokens) {
            timestamp = tokens[0];
            read(tokens);
        }
        
        public boolean dateMatches(String date) {
            return timestamp.equals(date);
        }
        
        public void read(String[] tokens) { 
            data.add(tokens);
        }
        
        public String[] match(int attrCol, String attrValue){
            for(int i=0;i<data.size(); i++) {
                String[] line = (String[])data.get(i);
                if(line[attrCol].equals(attrValue))
                    return line;
            }
            return null;
        }
    
    }
    
    public Timestep getTimestep(int i) { 
        return animationData.get(i);
    }
    
    public int getTimestepCount() {
        return animationData.size();
    }
    
    
    public AnimationFile(String filename) throws Exception{
        animationData = new ArrayList<Timestep>();
        boolean headerRead = false;
        boolean typesRead = false;
        
        File file = new File(filename);
        Scanner scanner = new Scanner(file);
        while( scanner.hasNextLine() ) {
            String line = scanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            if(line.startsWith("#")) {
                continue;
            }
            if(!headerRead) {
                columns = processHeader(lineScanner);
                headerRead = true;
                continue;
            }
            if(!typesRead) {
                columnTypes = processTypes(lineScanner);
                typesRead = true;
                continue;
            }
            
            processLine(lineScanner);
        }
    }
    
    private String[] tokens(Scanner lineScanner) {
        String[] out;
        ArrayList<String> strings = new ArrayList<String>();
        while(lineScanner.hasNext()) {
            String next = lineScanner.next();
            strings.add(next);
        }
        
        out = new String[strings.size()];
        for(int i=0; i<strings.size(); i++)  out[i] = strings.get(i);
        return out;
    }
    
    private String[] processHeader(Scanner lineScanner) {
        return tokens(lineScanner);
    }
    
    private String[] processTypes(Scanner lineScanner) {
        return tokens(lineScanner);
    }
    
    private Timestep currentTimestep() {
        if(animationData.size() == 0 ) 
            return null;
        return animationData.get(animationData.size()-1);
    }
    
    private void processLine(Scanner lineScanner) {
        String[] tokens = tokens(lineScanner);
        if(currentTimestep() == null) {
            this.animationData.add(new Timestep(tokens));
        }
        else if(currentTimestep().dateMatches(tokens[0])) {
            currentTimestep().read(tokens);
        }
        else {
            this.animationData.add(new Timestep(tokens));
        }
    }
    
    public double getMinDouble(int colIndex) {
        double min = Double.MAX_VALUE;
        for(int i=0; i<this.getTimestepCount(); i++) {
            for(String[] line : this.getTimestep(i).data ) {
                double d = Double.parseDouble(line[colIndex]);
                if(d < min)
                    min = d;
            }
        }
        return min;
    }
    
    
    public double getMaxDouble(int colIndex) {
        double max = Double.MIN_VALUE;
        for(int i=0; i<this.getTimestepCount(); i++) {
            for(String[] line : this.getTimestep(i).data ) {
                double d = Double.parseDouble(line[colIndex]);
                if(d > max)
                    max = d;
            }
        }
        return max;
    }
    
}
