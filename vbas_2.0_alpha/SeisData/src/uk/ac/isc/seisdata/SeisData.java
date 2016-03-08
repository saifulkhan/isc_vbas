package uk.ac.isc.seisdata;

/**
 * Observer Design Pattern: Interface for the seismicity data to inherit
 */
public interface SeisData {

    public static char TYPE_A = 'A';
    public static char TYPE_B = 'B';

    public void addChangeListener(SeisDataChangeListener listener);

    public void removeChangeListener(SeisDataChangeListener listener);

    public void fireSeisDataChanged();
}
