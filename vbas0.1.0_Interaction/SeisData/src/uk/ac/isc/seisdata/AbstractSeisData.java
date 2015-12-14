package uk.ac.isc.seisdata;

import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import javax.swing.event.EventListenerList;

/**
 * Abstract class that all the data models inherit so that they can add
 * ChangeListener
 *
 * @author hui
 */
public abstract class AbstractSeisData implements SeisData, Cloneable {

    // a list to keep all the observers
    private transient EventListenerList listenerList;

    protected AbstractSeisData() {
        this.listenerList = new EventListenerList();
    }

    /**
     * Add an object for notifying the change from the data
     *
     * @param listener
     */
    @Override
    public void addChangeListener(SeisDataChangeListener listener) {
        this.listenerList.add(SeisDataChangeListener.class, listener);
    }

    /**
     * Remove an object for notifying the data change
     *
     * @param listener
     */
    @Override
    public void removeChangeListener(SeisDataChangeListener listener) {
        this.listenerList.remove(SeisDataChangeListener.class, listener);
    }

    public boolean hasListener(EventListener listener) {
        List list = Arrays.asList(this.listenerList.getListenerList());
        return list.contains(listener);
    }

    /* 
     * Notify all the listeners (Hypocentre table, Phase table, etc.)
     */
    public void fireSeisDataChanged() {
        SeisDataChangeEvent event = new SeisDataChangeEvent(this, this);

        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == SeisDataChangeListener.class) {
                ((SeisDataChangeListener) listeners[i + 1]).SeisDataChanged(event);
            }
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AbstractSeisData clone = (AbstractSeisData) super.clone();
        clone.listenerList = new EventListenerList();
        return clone;
    }
}
