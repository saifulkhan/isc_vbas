package uk.ac.isc.phaseview;

import org.jfree.chart.util.ParamChecks;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

/**
 * As the orignial timeseries does not allow duplicated data and we do have many
 * duplicates, this class is to extend that class for allowing duplicates
 */

public class DuplicateUnorderTimeSeries extends TimeSeries {

    //bad encapsulation in the base class so I have to  redefine the boundary
    private double minY;

    private double maxY;

    //for time range
    private double minX;

    private double maxX;

    public DuplicateUnorderTimeSeries(Comparable name) {
        super(name);
        this.minY = Double.NaN;
        this.maxY = Double.NaN;
        this.minX = Double.NaN;
        this.maxX = Double.NaN;
    }

    @Override
    public double getMinY() {
        return this.minY;
    }

    @Override
    public double getMaxY() {
        return this.maxY;
    }

    public long getMinX() {
        return (long) this.minX;
    }

    public long getMaxX() {
        return (long) this.maxX;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public void setMinX(long minX) {
        this.minX = minX;
    }

    public void setMaxX(long maxX) {
        this.maxX = maxX;
    }

    //actually, we just allow to add duplicate data
    @Override
    public void add(TimeSeriesDataItem item, boolean notify) {
        ParamChecks.nullNotPermitted(item, "item");
        item = (TimeSeriesDataItem) item.clone();
        Class c = item.getPeriod().getClass();
        if (this.timePeriodClass == null) {
            this.timePeriodClass = c;
        } else if (!this.timePeriodClass.equals(c)) {
            StringBuilder b = new StringBuilder();
            b.append("You are trying to add data where the time period class ");
            b.append("is ");
            b.append(item.getPeriod().getClass().getName());
            b.append(", but the TimeSeries is expecting an instance of ");
            b.append(this.timePeriodClass.getName());
            b.append(".");
            throw new SeriesException(b.toString());
        }

        // make the change (no matter if it is a duplicate time period)...
        boolean added;
        int count = getItemCount();
        if (count == 0) {
            this.data.add(item);
            added = true;
        } else {
            //RegularTimePeriod last = getTimePeriod(getItemCount() - 1);
            //if (item.getPeriod().compareTo(last) > 0) {
            this.data.add(item);
            added = true;
            //}
            //else {
            //    int index = Collections.binarySearch(this.data, item);
            //    if (index < 0) {
            //        this.data.add(-index - 1, item);
            //        added = true;
            //    }
            //    else {
            //        StringBuffer b = new StringBuffer();
            //        b.append("You are attempting to add an observation for ");
            //        b.append("the time period ");
            //        b.append(item.getPeriod().toString());
            //        b.append(" but the series already contains an observation");
            //        b.append(" for that time period. Duplicates are not ");
            //        b.append("permitted.  Try using the addOrUpdate() method.");
            //        throw new SeriesException(b.toString());
            //   }
            //}
        }
        if (added) {
            updateBoundsForAddedItem(item);
            // check if this addition will exceed the maximum item count...

            removeAgedItems(false);  // remove old items if necessary, but
            // don't notify anyone, because that
            // happens next anyway...
            if (notify) {
                fireSeriesChanged();
            }
        }

    }

    private void updateBoundsForAddedItem(TimeSeriesDataItem item) {
        Number yN = item.getValue();
        double x = (double) item.getPeriod().getFirstMillisecond();
        if (item.getValue() != null) {
            double y = yN.doubleValue();
            this.minY = minIgnoreNaN(this.minY, y);
            this.maxY = maxIgnoreNaN(this.maxY, y);
            this.minX = minIgnoreNaN(this.minX, x);
            this.maxX = maxIgnoreNaN(this.maxX, x);
        }
    }

    private double minIgnoreNaN(double a, double b) {
        if (Double.isNaN(a)) {
            return b;
        }
        if (Double.isNaN(b)) {
            return a;
        }
        return Math.min(a, b);
    }

    private double maxIgnoreNaN(double a, double b) {
        if (Double.isNaN(a)) {
            return b;
        }
        if (Double.isNaN(b)) {
            return a;
        } else {
            return Math.max(a, b);
        }
    }

}
