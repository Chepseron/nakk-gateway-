/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nakk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 *
 * @author amon.sabul
 */
public class Reports {

    public static void main(String[] args) {
        String input = "dont delete";
        System.out.println(input);
        int i = 1;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String line = br.readLine();
            br.close();
            String infile = "C:\\Users\\amon.sabul\\Desktop\\smsbet.log.2019_1_3";
            System.out.println("Skip:" + line);
            String lineToSkip = line;
            StringBuffer newContent = new StringBuffer();
            BufferedReader file = new BufferedReader(new FileReader(infile));
            while ((line = file.readLine()) != null) {
                if (line.trim().indexOf(lineToSkip) != -1) {
                    newContent.append(i + " " + line);
                    newContent.append("\n");
                    i++;
                }
            }
            i = i-1;
            newContent.append("Total Messages "+i);
            
            
            file.close();
            FileWriter removeLine = new FileWriter(infile);
            BufferedWriter change = new BufferedWriter(removeLine);
            PrintWriter replace = new PrintWriter(change);
            replace.write(newContent.toString());
            replace.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
