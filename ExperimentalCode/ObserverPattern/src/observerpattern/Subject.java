
/*
* Subject is an object having methods to attach and detach observers to a client object. 
* We have created an abstract class Observer and a concrete class Subject that is extending class Observer.
*/

package observerpattern;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    private List <Observer> observers = new ArrayList<Observer>();
    private int state;


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        notifyAllObserver();
    }

    private void notifyAllObserver() {
         for (Observer o : observers) {
             o.update();
         }
    }
    
    public void attach(Observer o) {
        observers.add(o);
    }
}
