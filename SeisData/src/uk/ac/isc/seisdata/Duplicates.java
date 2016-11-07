 
package uk.ac.isc.seisdata;

 
public class Duplicates {
     private final String ownsta;
     private final String ownphase;
     private final String ownresidual;    // + - value
     private final int owndelta;          // 0 - 180 degree    
     private final String dupphase;
     private final String dupresidual;     
     private final int dupdelta;
     private final int dupevid;
     private final String dupready;

    public Duplicates(
            String ownsta, 
            String ownphase, 
            String ownresidual, 
            int owndelta, 
            String dupphase, 
            String dupresidual, 
            int dupdelta, 
            int dupevid, 
            String dupready
    ) {
        this.ownsta = ownsta;
        this.ownphase = ownphase;
        this.ownresidual = ownresidual;
        this.owndelta = owndelta;
        this.dupphase = dupphase;
        this.dupresidual = dupresidual;
        this.dupdelta = dupdelta;
        this.dupevid = dupevid;
        this.dupready = dupready;
    }

    public String getOwnsta() {
        return ownsta;
    }

    public String getOwnphase() {
        return ownphase;
    }

    public String getOwnresidual() {
        return ownresidual;
    }

    public int getOwndelta() {
        return owndelta;
    }

    public String getDupphase() {
        return dupphase;
    }

    public String getDupresidual() {
        return dupresidual;
    }

    public int getDupdelta() {
        return dupdelta;
    }

    public int getDupevid() {
        return dupevid;
    }

    public String getDupready() {
        return dupready;
    }

    @Override
    public String toString() {
        return "Duplicates{" 
                + "ownsta=" + ownsta 
                + ", ownphase=" + ownphase 
                + ", ownresidual=" + ownresidual 
                + ", owndelta=" + owndelta 
                + ", dupphase=" + dupphase 
                + ", dupresidual=" + dupresidual 
                + ", dupdelta=" + dupdelta 
                + ", dupevid=" + dupevid 
                + ", dupready=" + dupready + '}';
    }
     
}
