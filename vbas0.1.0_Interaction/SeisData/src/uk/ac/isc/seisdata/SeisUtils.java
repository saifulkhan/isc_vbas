
package uk.ac.isc.seisdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Utilities to calculate distance, angels, magnitude groups, depth bands, azimuth etc.
 * @author hui
 */
public final class SeisUtils {
    
    private final static double flattening = 0.00335281;
    private final static double f = (1.0 - flattening)*(1.0-flattening);
    private final static double deg2rad = 3.14159 / 180.0;
    private final static double rad2deg = 180.0 / 3.14159;
    
    /**
     * given two coordinate, calculate their distance
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return 
     */
    public static double DeltaFromLatLon(double lat1, double lon1, double lat2, double lon2)
    {
        double geoc_lat1, clat1, slat1;
        double geoc_lat2, clat2, slat2;
        double rdlon, cdlon;
        double cdel;
        double delta;
        
        if (Math.abs(lat1 - lat2) < 0.001 && Math.abs(lon1 - lon2) < 0.001)
        {
            delta = 0;          
        }
        else
        {
            geoc_lat1 = Math.atan(f*Math.tan(deg2rad*lat1));
            clat1 = Math.cos(geoc_lat1);
            slat1 = Math.sin(geoc_lat1);
            
            geoc_lat2 = Math.atan(f*Math.tan(deg2rad*lat2));
            clat2 = Math.cos(geoc_lat2);
            slat2 = Math.sin(geoc_lat2);
            
            rdlon = deg2rad * (lon2 - lon1);
            cdlon = Math.cos(rdlon);
            
            cdel = slat1 * slat2 + clat1 * clat2 * cdlon;
            cdel = (cdel<1.0) ? cdel : 1.0;
            cdel = (cdel>-1.0)? cdel : -1.0;
            
            delta = rad2deg * Math.acos(cdel);
        }
        
       return delta; 
    }
    
    /**
     * Given two coordinates, calculate the azimuthal angel 
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return 
     */
    public static double AzimuthFromLatLon(double lat1, double lon1, double lat2, double lon2)
    {
        double geoc_lat1, clat1, slat1;
        double geoc_lat2, clat2, slat2;
        double rdlon, cdlon, sdlon;
        double yazi, xazi;
        double azi;
        
        if (Math.abs(lat1 - lat2) < 0.001 && Math.abs(lon1 - lon2) < 0.001)
        {
            azi = 0;
        }
        else
        {
            geoc_lat1 = Math.atan(f*Math.tan(deg2rad*lat1));
            clat1 = Math.cos(geoc_lat1);
            slat1 = Math.sin(geoc_lat1);
            
            geoc_lat2 = Math.atan(f*Math.tan(deg2rad*lat2));
            clat2 = Math.cos(geoc_lat2);
            slat2 = Math.sin(geoc_lat2);
            
            rdlon = deg2rad * (lon2 - lon1);
            cdlon = Math.cos(rdlon);
            sdlon = Math.sin(rdlon);
            
            yazi = sdlon * clat2;
            xazi = clat1 * slat2 - slat1 * clat2 * cdlon;
            
            azi = rad2deg * Math.atan2(yazi,xazi);
        }
               
        if (azi < 0.0) azi += 360.0;
        return azi;
    }

    /**
     * Give one coordinate, distance, and azimuth, calculate the latitude of the other coordinate 
     * @param lat1
     * @param lon1
     * @param azi
     * @param delta
     * @return 
     */
    public static double LatFromAziDelta(double lat1, double lon1, double azi, double delta)
    {
        double sclat1, csclat1;
        double geoc_lat1;
        double lat2;
                
        geoc_lat1 = Math.atan(f*Math.tan(deg2rad*lat1));
        sclat1 = Math.sin(geoc_lat1)*Math.cos(deg2rad*delta);
        csclat1 = Math.cos(geoc_lat1)*Math.sin(deg2rad*delta)*Math.cos(deg2rad*azi);
        
        lat2 = Math.atan(Math.tan(Math.asin(sclat1 + csclat1))/f)*rad2deg;
        
        return lat2;
    }
    
    /**
     * Give one coordinate, distance, and azimuth, calculate the longitude of the other coordinate 
     * @param lat1
     * @param lon1
     * @param azi
     * @param delta
     * @return 
     */
    public static double LonFromAziDelta(double lat1, double lon1, double azi, double delta)
    {
        double geoc_lat1,geoc_lat2;
        double ssclat1,csslat2; 
        double lat2, lon2;
        double tan2;
        
        geoc_lat1 = Math.atan(f*Math.tan(deg2rad*lat1));
        lat2 = LatFromAziDelta(lat1, lon1, azi, delta);
        geoc_lat2 = Math.atan(f*Math.tan(deg2rad*lat2));
        
        ssclat1 = Math.sin(deg2rad*azi)*Math.sin(deg2rad*delta)*Math.cos(geoc_lat1);
        csslat2 = Math.cos(deg2rad*delta)-Math.sin(geoc_lat1)*Math.sin(geoc_lat2);
        tan2 = Math.atan2(ssclat1,csslat2);
        
        lon2 = lon1 + rad2deg * tan2;
        
        return lon2;
    }
    
    /**
     * 
     * @return different phase clusters 
     */
    public static String[][] getGroupedPhaseTypes() 
    {
        String[][] ptypes = {{"Pg","Pb","Pn","Sg","Sb","Sn"}, //crustal
                             {"P","S","Pdif","PP","SS","PcP","ScP","ScS","PcS"}, //mantal
                             {"PKPdf","PKPbc","PKPab","PKIKP","PKP","PKPpre"}, //core
                             {"pP","sP","pPKPdf","pPKPbc","pPKPab"}}; //depth
        return ptypes;
    }
       
    /**
     * 
     * @return the phase groups
     */
    public static HashMap<String, String> getGroupMagnitudeTypes() 
    {
        HashMap<String, String> magTypes = new HashMap<String,String>();
        
        magTypes.put("mbLg", "local");
        magTypes.put("MbLg", "local");
        magTypes.put("Md", "local");
        magTypes.put("MD", "local");
        magTypes.put("ml", "local");
        magTypes.put("Ml", "local");
        magTypes.put("ML", "local");
        magTypes.put("MLSn", "local");
        magTypes.put("MLv", "local");
        magTypes.put("MN", "local");
        magTypes.put("MJMA", "local");
        magTypes.put("mb", "mb");
        magTypes.put("mB", "mb");
        magTypes.put("Mb", "mb");
        magTypes.put("MB", "mb");
        magTypes.put("mb1", "mb");
        magTypes.put("mb1mx", "mb");
        magTypes.put("mbtmp", "mb");
        magTypes.put("ms", "MS");
        magTypes.put("Ms", "MS");
        magTypes.put("MS", "MS");
        magTypes.put("Ms1", "MS");  
        magTypes.put("Ms1mx", "MS");
        magTypes.put("Ms7", "MS");
        magTypes.put("Mw", "MW");
        magTypes.put("MW", "MW");
        
        return magTypes;
    }
        
    /**
     * Original depth bands
     * @param depth
     * @return 
     */
    public static int getOldDepthBand(int depth) {
        
        int depthBand;
        
        if(depth>=0 && depth<20.1)
            {
                depthBand = 0;
            }
            else if(depth<35.1)
            {
                depthBand = 1;
            }
            else if(depth<70.1)
            {
                depthBand = 2;
            }
            else if(depth<120.1)
            {
                depthBand = 3;
            }
            else if(depth<160.1)
            {
                depthBand = 4;
            }
            else if(depth<250.1)
            {
                depthBand = 5;
            }
            else if(depth<350.1)
            {
                depthBand = 6;
            }
            else if(depth<500.1)
            {
                depthBand = 7;
            }
            else
            {
                depthBand = 8;
            }
        
        return depthBand;
    }
        
    /**
     * The new depth bands
     * @param depth
     * @return 
     */
    public static int getNewDepthBand(int depth) {
        
        int depthBand;
        
        if(depth>=0 && depth<=15)
            {
                depthBand = 0;
            }
            else if(depth<=35)
            {
                depthBand = 1;
            }
            else if(depth<=70)
            {
                depthBand = 2;
            }
            else if(depth<=160)
            {
                depthBand = 3;
            }
            else if(depth<=250)
            {
                depthBand = 4;
            }
            else if(depth<=500)
            {
                depthBand = 5;
            }
            else if(depth<=700)
            {
                depthBand = 6;
            }
            else
            {
                depthBand = 7;
            }
        
        return depthBand;
    }
    
    /**
     * Utility function to caculate how many agencies adn stations reporting P, S, PKP and pP type of phase.
     * @param pList
     * @return 
     */
    public static ArrayList<Integer> calculatePhasesSumm(ArrayList<Phase> pList)
    {
        Integer PA, PS, SA, SS, PKPA, PKPS, pPA, pPS;
        int pCount = 0, pPCount = 0;
        //need tmp Array to save the agencies and stations which have been counted
        ArrayList<String> PAgencies = new ArrayList<String>();
        ArrayList<String> SAgencies = new ArrayList<String>();
        ArrayList<String> PKPAgencies = new ArrayList<String>();
        ArrayList<String> PStations = new ArrayList<String>();
        ArrayList<String> SStations = new ArrayList<String>();
        ArrayList<String> PKPStations = new ArrayList<String>();
        ArrayList<String> pPAgencies = new ArrayList<String>();
        ArrayList<String> pPStations = new ArrayList<String>();
        
        ArrayList<Integer> phasesSumm = new ArrayList<Integer>();
        
        for(Phase p:pList)
        {
            String phaseType = p.getIscPhaseType();
            String phaseAgency = p.getReportAgency();
            String phaseStation = p.getReportStation();
            
            if(phaseType!=null)
            {
                //add PAgency and PStation
                if(phaseType.charAt(0)=='P' && phaseType.length()==1)
                {
                    if(!PAgencies.contains(phaseAgency))
                    {
                        PAgencies.add(phaseAgency);
                    }
                    if(!PStations.contains(phaseStation))
                    {
                        PStations.add(phaseStation);
                    }
                    //for getting depth phase agency
                    pCount = 1;
                }
                else if(phaseType.charAt(0)=='P' && phaseType.length()==2)
                {
                    if(!PAgencies.contains(phaseAgency))
                    {
                        PAgencies.add(phaseAgency);
                    }
                    if(!PStations.contains(phaseStation))
                    {
                        PStations.add(phaseStation);
                    }                    
                }
                
                //add SAgency and SStation
                if(phaseType.charAt(0)=='S' && phaseType.length()<=2)
                {
                    if(!SAgencies.contains(phaseAgency))
                    {
                        SAgencies.add(phaseAgency);
                    }
                    if(!SStations.contains(phaseStation))
                    {
                        SStations.add(phaseStation);
                    }
                }
                
                //add PKPAgency and PKPStation                
                if(phaseType.length()>=3)
                {
                    if("PKP".equals(phaseType.substring(0,3)))
                    {
                        //System.out.println(phaseType.substring(0,3));
                        if(!PKPAgencies.contains(phaseAgency))
                        {
                            PKPAgencies.add(phaseAgency);
                        }
                        if(!PKPStations.contains(phaseStation))
                        {
                            PKPStations.add(phaseStation);
                        }
                    }
                }
                
                if(phaseType.length()==2 && "pP".equals(phaseType))
                {
                    pPCount = 1;
                }
                
                if(pCount == 1 && pPCount == 1)
                {
                    if(!pPAgencies.contains(phaseAgency))
                    {
                        pPAgencies.add(phaseAgency);
                    }
                    if(!pPStations.contains(phaseStation))
                    {
                        pPStations.add(phaseStation);
                    }
                    
                    pCount = 0;
                    pPCount = 0;
                }
            }
        }
        
        PA = PAgencies.size();
        phasesSumm.add(PA);
        PS = PStations.size();
        phasesSumm.add(PS);
        SA = SAgencies.size();
        phasesSumm.add(SA);
        SS = SStations.size();
        phasesSumm.add(SS);
        PKPA = PKPAgencies.size();
        phasesSumm.add(PKPA);
        PKPS = PKPStations.size();
        phasesSumm.add(PKPS);
        pPA = pPAgencies.size();
        phasesSumm.add(pPA);
        pPS = pPStations.size();
        phasesSumm.add(pPS);
                
        return phasesSumm;
    }

    /**
     * for getting first and second azimuthal angel
     * @param phasesList
     * @return 
     */
    public static ArrayList<Integer> calculateAzSumm(ArrayList<Phase> phasesList) {
        
        ArrayList<Integer> retArray = new ArrayList<Integer>();
        
        ArrayList<Double> azimuths = new ArrayList<Double>();
        
        for(Phase p:phasesList)
        {
            Double tmp = p.getAzimuth();
            azimuths.add(tmp);
        }
        Collections.sort(azimuths);
        
        Integer fst_azi = 0, sec_azi = 0, miss_cut = 0, startgap1 = 0, startgap2 = 0;
        double azi1 = 0.0, azi2 = 0.0;
        if(azimuths.size()<3)
        {
            fst_azi = -1;
            sec_azi = -1;
        }
        else
        {
            for(int i = 0; i<azimuths.size();i++)
            {
                if(i == azimuths.size()-1)
                {
                    azi1 = azimuths.get(0)-(azimuths.get(i)-360.0);
                    if((int)azi1>fst_azi)
                    {
                        fst_azi = (int)azi1;
                        startgap1 = azimuths.get(i).intValue();
                    }
                    
                    azi2 = azimuths.get(1)-(azimuths.get(i)-360.0);
                    if((int)azi2>sec_azi)
                    {
                        sec_azi = (int)azi2;
                        startgap2 = azimuths.get(i).intValue();
                        miss_cut = 0;
                    }
                }
                else if(i==azimuths.size()-2)
                {
                    azi1 = azimuths.get(i+1)-azimuths.get(i);
                    if((int)azi1>fst_azi)
                    {
                        fst_azi = (int)azi1;
                        startgap1 = azimuths.get(i).intValue();
                    }
                    
                    azi2 = azimuths.get(0)-(azimuths.get(i)-360.0);
                    if((int)azi2>sec_azi)
                    {
                        sec_azi = (int)azi2;
                        startgap2 = azimuths.get(i).intValue();
                        miss_cut = (azimuths.size()-1);
                    }
                }
                else
                {
                    azi1 = azimuths.get(i+1)-azimuths.get(i);
                    if((int)azi1>fst_azi)
                    {
                        fst_azi = (int)azi1;
                        startgap1 = azimuths.get(i).intValue();
                    }
                    
                    azi2 = azimuths.get(i+2)-azimuths.get(i);
                    if((int)azi2>sec_azi)
                    {
                        sec_azi = (int)azi2;
                        startgap2 = azimuths.get(i).intValue();
                        miss_cut = i+1;
                    }
                }
            }
        }
        
        if(fst_azi!=-1)
        {
            fst_azi = 360-fst_azi;
        }
        
        if(sec_azi!=-1)
        {
            sec_azi = 360-sec_azi;
        }
        
        retArray.add(fst_azi);
        retArray.add(sec_azi);
        retArray.add(startgap1);
        retArray.add(startgap2);
        
        return retArray;
    }
    
    
}
