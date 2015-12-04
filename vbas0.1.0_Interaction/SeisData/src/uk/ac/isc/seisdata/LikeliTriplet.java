
package uk.ac.isc.seisdata;

/**
 * This is for future likelihood view, representing the likelihood values based on distance, magnitude and time.
 * @author hui
 */
public class LikeliTriplet {
    
    private Double distanceLike;
    
    private Double magnitudeLike;
    
    private Double timeLike;
    
    public LikeliTriplet(Double dist, Double mag, Double time)
    {
        this.distanceLike = dist;
        this.magnitudeLike = mag;
        this.timeLike = time;
    }
    
    public void setDistLike(Double distLike)
    {
        this.distanceLike = distLike;
    }
    
    public void setMagLike(Double magLike)
    {
        this.magnitudeLike = magLike;
    }
    
    public void setTimeLike(Double timeLike)
    {
        this.timeLike = timeLike;
    }
    
    public double getDistLike()
    {
        return this.distanceLike;
    
    }
    
    public double getMagLike()
    {
        return this.magnitudeLike;
    }
    
    public double getTimeLike()
    {
        return this.timeLike;
    }
    
    public Double getWeightedLike()
    {
        Double weightLike = 0.0;
        
        if(distanceLike != null)
        {
            weightLike += 0.33 * distanceLike;
        }
        
        if(magnitudeLike != null)
        {
            weightLike += 0.33 * magnitudeLike;
        }
        
        if(timeLike != null)
        {
            weightLike += 0.33 * timeLike;
        }
        
        return weightLike;
    }
    
}
