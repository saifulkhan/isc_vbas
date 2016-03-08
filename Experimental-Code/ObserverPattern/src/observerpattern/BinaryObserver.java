 
package observerpattern;

 
public class BinaryObserver extends Observer {

    public BinaryObserver(Subject subject){
      this.subject = subject;
      this.subject.attach(this);
   }
    
    @Override
    public void update() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.println( "Binary String: " + Integer.toBinaryString( subject.getState() ) );     
    }
    
}
