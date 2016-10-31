package uk.ac.isc.phaseview;

import java.util.List;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.data.Range;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * The same as DuplicateUnorderTimeSeries, we allow duplications in the
 * timeseriescollection
 *
 *  
 */
public class DuplicateUnorderTimeSeriesCollection extends TimeSeriesCollection implements LegendItemSource {

    @Override
    public Range getDomainBounds(boolean includeInterval) {
        Range result = null;
        for (int i = 0; i < super.getSeriesCount(); i++) {
            DuplicateUnorderTimeSeries series = (DuplicateUnorderTimeSeries) super.getSeries(i);
            Range r = null;
            r = new Range(series.getMinX(), series.getMaxX());
            result = Range.combineIgnoringNaN(result, r);
        }
        return result;
    }

    @Override
    public Range getDomainBounds(List visibleSeriesKeys, boolean includeInterval) {

        Range result = null;
        //Iterator iterator = visibleSeriesKeys.iterator();
        for (int i = 0; i < super.getSeriesCount(); i++) {
            DuplicateUnorderTimeSeries series = (DuplicateUnorderTimeSeries) super.getSeries(i);
            Range r = null;
            r = new Range(series.getMinX(), series.getMaxX());
            result = Range.combineIgnoringNaN(result, r);
        }
        return result;
    }

    @Override
    public LegendItemCollection getLegendItems() {

        LegendItemCollection result = new LegendItemCollection();

        int count = super.getSeriesCount();
        for (int datasetIndex = 0; datasetIndex < count; datasetIndex++) {

            if (super.getSeries(datasetIndex) != null) {
                String phaseType = (String) super.getSeries(datasetIndex).getKey();

                LegendItem item = new LegendItem(phaseType);

                result.add(item);
            }
        }
        return result;
    }
}
