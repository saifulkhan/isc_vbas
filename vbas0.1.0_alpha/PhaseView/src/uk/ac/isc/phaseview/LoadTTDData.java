package uk.ac.isc.phaseview;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import org.jfree.data.time.Second;
import org.openide.util.Exceptions;
import uk.ac.isc.seisdata.Global;

public class LoadTTDData {

    private final static DuplicateUnorderTimeSeriesCollection ttdData = new DuplicateUnorderTimeSeriesCollection();

    /**
     * helper function to call perl for calculating ttd curves based on evid
     */
    public static DuplicateUnorderTimeSeriesCollection loadTTDData(Integer evid, File perlScript) {
        Global.logDebug("Here...");

        ttdData.removeAllSeries();
        ArrayList<TTDTriplet> ttdList = new ArrayList<TTDTriplet>();

        //Execute the Perl script to get the theoretical time curves.
        //String commandLine = "perl /export/home/james/VBAS/TT/ttimes.pl " + evid.toString();
        String perlCommand = "perl" + " " + perlScript + " " + evid.toString();
        Global.logDebug("Execute: " + perlCommand);

        //execute the perl script and read the data into ttdList
        try {

            Process proc = Runtime.getRuntime().exec(perlCommand);

            BufferedInputStream in = new BufferedInputStream(proc.getInputStream());
            Scanner bscanner = new Scanner(in);

            while (bscanner.hasNextLine()) {
                String temp = bscanner.nextLine();
                TTDTriplet tempTriplet = new TTDTriplet(temp);
                ttdList.add(tempTriplet);
                //System.out.println(tempTriplet);
                //if(!pnameList.contains(tempTriplet.getPhaseType()))
                //{
                //    pnameList.add(tempTriplet.getPhaseType());
                //}
            }
            proc.waitFor();

        } catch (IOException ioe) {
            System.out.println("Exception: " + ioe.toString());
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }

        //iterate the ttdlist and put them into different seriers based on their phase types
        //return time series collections
        DuplicateUnorderTimeSeries dts = null;
        for (int i = 0; i < ttdList.size(); i++) {
            if (i == 0) {
                dts = new DuplicateUnorderTimeSeries(ttdList.get(0).getPhaseType());
                dts.add(new Second(ttdList.get(0).getArrivalTime()), ttdList.get(0).getDelta());
            } else if (i == ttdList.size() - 1) {
                ttdData.addSeries(dts);
            } else {
                if (ttdList.get(i).getPhaseType().equals(ttdList.get(i - 1).getPhaseType())) {
                    dts.add(new Second(ttdList.get(i).getArrivalTime()), ttdList.get(i).getDelta());
                } else {
                    ttdData.addSeries(dts);
                    dts = new DuplicateUnorderTimeSeries(ttdList.get(i).getPhaseType());
                    dts.add(new Second(ttdList.get(i).getArrivalTime()), ttdList.get(i).getDelta());
                }
            }
        }

        return ttdData;

    }
}
