
package uk.ac.isc.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedSet;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.event.DateSelectionEvent;
import org.jdesktop.swingx.event.DateSelectionListener;
import uk.ac.isc.data.SeisEventList;

/**
 * This is the one to pick up a day in the calendar and update linked panel to show
 * the hour view for picking the exact time
 * @author hui
 */
public class DateHourPickerPanel extends JXDatePicker {
    
    /** reference to the data model*/
    private SeisEventList seList;
    
    /** variable to store the selected day*/
    Date currDate;
    
    Date selectDay;
    
    public DateHourPickerPanel(Date currDate)
    {
        this.setDate(currDate);
        this.currDate = currDate;
        
        if(selectDay == null)
        {
            selectDay = currDate;
        }
        
        this.setFormats(new SimpleDateFormat("dd-MM-yyyy"));

        this.getMonthView().getSelectionModel().addDateSelectionListener( new DateSelectionListener() {
            @Override
            public void valueChanged(DateSelectionEvent e) {
                SortedSet<Date> selection = e.getSelection();
                selectDay = selection.first();      
            }

        });
    }
    
    public void setSeList(SeisEventList seList)
    {
        this.seList = seList;
    }
    
    public Date getSelectDay()
    {
        return selectDay;
    }
    
    /*@Override
    public JPanel getLinkPanel() {
        super.getLinkPanel();
        
        if(selectDay == null && currDate == null)
        {
            hourPanel = createTimePanel(new Date());
        }
        
        return hourPanel;
    }*/
    
}
