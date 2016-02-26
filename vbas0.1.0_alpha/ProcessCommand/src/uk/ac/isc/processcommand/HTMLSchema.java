/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.isc.processcommand;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.table.AbstractTableModel;
import uk.ac.isc.seisdata.Global;

/**
 * TODO: the phase table and hypocentre table code are repetitive. Try
 * AbstractTableModel
 */
public class HTMLSchema {

    private static final String[] tables = {"Hypocentre Table", "Phase Table"};
    private static final String[] views = {"Hypocentre Overview", "Hypocentre Depthview"};

    private final AbstractTableModel hModel;
    private final AbstractTableModel pModel;
    private final File htmlReport;

    FileWriter fileWritter = null;
    BufferedWriter bufferedWriter = null;

    public HTMLSchema(File htmlReport, AbstractTableModel hModel, AbstractTableModel pModel) {

        this.htmlReport = htmlReport;
        this.hModel = hModel;
        this.pModel = pModel;
    }

    public void generateHTMLSchema() {
        Global.logDebug("Here...");

        try {
            // if the file doesnt exists, then create it
            if (!htmlReport.exists()) {
                htmlReport.createNewFile();
            }

            fileWritter = new FileWriter(htmlReport, false);
            bufferedWriter = new BufferedWriter(fileWritter);

            /*
             * Write the table data first. 
             */
            /*
             * Hypocentre table
             */
            bufferedWriter.write("<div>");
            bufferedWriter.newLine();
            bufferedWriter.write("<h2>Hypocentre Table</h2>");
            bufferedWriter.newLine();

            bufferedWriter.write("<table>");
            bufferedWriter.newLine();

            // write column names
            bufferedWriter.write("<tr>");
            bufferedWriter.newLine();
            for (int c = 0; c < this.hModel.getColumnCount(); ++c) {
                bufferedWriter.write("<th>");
                bufferedWriter.write(this.hModel.getColumnName(c));
                bufferedWriter.write("</th>");
            }
            bufferedWriter.write("</tr>");
            bufferedWriter.newLine();

            // write rows or column data 
            for (int r = 0; r < this.hModel.getRowCount(); ++r) {
                bufferedWriter.write("<tr>");
                for (int c = 0; c < this.hModel.getColumnCount(); ++c) {
                    bufferedWriter.write("<td>");
                    //Global.logDebug(model.getValueAt(r, c) == null ? "" : model.getValueAt(r, c).toString());
                    bufferedWriter.write(this.hModel.getValueAt(r, c) == null ? "" : this.hModel.getValueAt(r, c).toString());
                    bufferedWriter.write("</td>");
                }
            }

            bufferedWriter.write("</table>");
            bufferedWriter.newLine();
            bufferedWriter.write("</div>");
            bufferedWriter.newLine();

            /*
             * Phase table
             */
            bufferedWriter.write("<div>");
            bufferedWriter.newLine();
            bufferedWriter.write("<h2>Phase Table</h2>");
            bufferedWriter.newLine();

            bufferedWriter.write("<table>");
            bufferedWriter.newLine();

            // write column names
            bufferedWriter.write("<tr>");
            bufferedWriter.newLine();
            for (int c = 0; c < this.pModel.getColumnCount(); ++c) {
                bufferedWriter.write("<th>");
                bufferedWriter.write(this.pModel.getColumnName(c));
                bufferedWriter.write("</th>");
            }
            bufferedWriter.write("</tr>");
            bufferedWriter.newLine();

            // write rows or column data 
            for (int r = 0; r < this.pModel.getRowCount(); ++r) {
                bufferedWriter.write("<tr>");
                for (int c = 0; c < this.pModel.getColumnCount(); ++c) {
                    bufferedWriter.write("<td>");
                    //Global.logDebug(model.getValueAt(r, c) == null ? "" : model.getValueAt(r, c).toString());
                    bufferedWriter.write(this.pModel.getValueAt(r, c) == null ? "" : this.pModel.getValueAt(r, c).toString());
                    bufferedWriter.write("</td>");
                }
            }

            bufferedWriter.write("</table>");
            bufferedWriter.newLine();
            bufferedWriter.write("</div>");
            bufferedWriter.newLine();


            /*
             * Write the images (just name and structure) 
             */
            for (String view : views) {
                Global.logDebug(view);

                bufferedWriter.write("<div>");
                bufferedWriter.newLine();
                bufferedWriter.write("<h2> " + view + " </h2>");
                bufferedWriter.newLine();
                bufferedWriter.write("<img src=\"" + view + ".png\" "
                        + "alt=\"" + view + "\" >");
                bufferedWriter.newLine();
                bufferedWriter.write("</div>");
                bufferedWriter.newLine();
            }

            //bufferedWriter.close();
            //fileWritter.close();
            Global.logDebug("Complete...");

        } catch (IOException e) {
            Global.logSevere("Error writing to html file.");
            e.printStackTrace();
        } finally {

            try {
                if (bufferedWriter != null) {
                    //bufferedWriter.flush();
                    bufferedWriter.close();
                }
                /*if (fileWritter != null) {
                    //fileWritter.flush();
                    fileWritter.close();
                }*/

            } catch (IOException e) {
                Global.logSevere("Error releasing resources.");
                e.printStackTrace();
            }
        }

    }

    public static String[] getViews() {
        return views;
    }

}
