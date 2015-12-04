
package uk.ac.isc.data;

/**
 *
 * @author hui
 */
public class Analyst {
    
    int id;
    
    String name;
    
    String email;
    
    String position;
    
    public Analyst(int id, String name, String email)
    {
        this.id = id;
        this.name = name;
        this.email = email;
        this.position = null;
    }
    
    public Analyst(int id, String name, String email, String position)
    {
        this.id = id;
        this.name = name;
        this.email = email;
        this.position = position;
    }
    
    public Integer getID()
    {
        return this.id;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public String getEmail()
    {
        return this.email;
    }
    
    public void setPosition(String pos)
    {
        this.position = pos;
    }
    
    public String getPosition()
    {
        return this.position;
    }
    
    
}
