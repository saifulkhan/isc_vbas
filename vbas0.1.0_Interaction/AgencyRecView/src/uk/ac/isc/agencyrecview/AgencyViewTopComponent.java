
package uk.ac.isc.agencyrecview;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import uk.ac.isc.eventscontrolview.EventsControlViewTopComponent;
import uk.ac.isc.seisdata.LikeliTriplet;
import uk.ac.isc.seisdata.Phase;
import uk.ac.isc.seisdata.PhasesList;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisDataDAO;

/**
 * Top component which displays three tables of agencies, they are reported and expected agencies, unexpected agencies
 * and missed agencies
 * This is bit messy, the view was proposed but not been tested as the data is not ready, we can only get some artificial data 
 * need write renderers for showing different things in the tables
 */
@ConvertAsProperties(
        dtd = "-//uk.ac.isc.agencyrecview//AgencyRecView//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "AgencyViewTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "properties", openAtStartup = true)
@ActionID(category = "Window", id = "uk.ac.isc.agencyrecview.AgencyRecViewTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_AgencyRecViewAction",
        preferredID = "AgencyViewTopComponent"
)
@Messages({
    "CTL_AgencyRecViewAction=AgencyRecView",
    "CTL_AgencyRecViewTopComponent=AgencyView Window",
    "HINT_AgencyRecViewTopComponent=This is a AgencyRecView window"
})
public final class AgencyViewTopComponent extends TopComponent implements SeisDataChangeListener {

    //here is the reference of phase data
    private final PhasesList phasesList;

    //save all the name of reporting agencies
    private final HashSet<String> reportedAgency = new HashSet<String>();
    
    //a map to keep agency likelihood
    private final HashMap<String, LikeliTriplet> agencyLikelihood = new HashMap<String,LikeliTriplet>();
    
    //the likelihood for all teh reported agencies
    private Map<String, LikeliTriplet> completeList;
    
    //the likelihood of expected agencies but not in the report list
    private Map<String, LikeliTriplet> expectList;
    
    private HashSet<String> newSet; 
            
    private Integer evid;
    //get control window to retrieve data
    private final TopComponent tc = WindowManager.getDefault().findTopComponent("EventsControlViewTopComponent");
        
    //panes for structure
    private final JSplitPane leftRightPane;
    
    private final JSplitPane topBottomPane;
    
    private final JScrollPane leftScrollPane;
            
    private final JScrollPane topScrollPane;
    
    private final JScrollPane bottomScrollPane;
    
    private final JTable normalTable;
    
    private final JTable expectTable;
    
    private final JTable unexpectTable;
    
    private DefaultTableModel model1;
    
    private DefaultTableModel model2;
    
    private DefaultTableModel model3;
    
    private final BackgroundRenderer renderer1 = new BackgroundRenderer();
    
    private final String[] columnName = {"Agency", "Dist. Poss.", "Mag. Poss.", "Time Poss.","Expectation"};
        
    public AgencyViewTopComponent() {
        initComponents();
        setName(Bundle.CTL_AgencyRecViewTopComponent());
        setToolTipText(Bundle.HINT_AgencyRecViewTopComponent());

        phasesList = ((EventsControlViewTopComponent) tc).getControlPanel().getPhasesList();
        evid = ((EventsControlViewTopComponent) tc).getControlPanel().getSelectedSeisEvent().getEvid();
        
        //get recommended list, sometime the recommendation list is empty, maybe will be sorted in future from database side
        SeisDataDAO.retrieveAgencyLikelihood(agencyLikelihood, evid);
        
        //get reported list
        for(Phase phase:phasesList.getPhases())
        {
            reportedAgency.add(phase.getReportAgency());
        }
        
        //get the ordered complete list
        completeList = new TreeMap<String,LikeliTriplet>();/*(new Comparator<Object>(){

            @Override
            public int compare(Object o1, Object o2) {
              return ((LikeliTriplet)o1).getWeightedLike().compareTo(((LikeliTriplet)o1).getWeightedLike());
            }

        });*/
        //completeList.putAll(agencyLikelihood);
        if(agencyLikelihood.size()>0)
        {
            for(Map.Entry<String, LikeliTriplet> entry: agencyLikelihood.entrySet())
            {
            if(reportedAgency.contains(entry.getKey()))
            {
                completeList.put(entry.getKey(), entry.getValue());
            }
            }
        }
        else //make some artificial data at this stage, need be re-written in future
        {
            for(String s:reportedAgency)
            {
                completeList.put(s, new LikeliTriplet(1.0,1.0,1.0));
            }
        }
                
        Object[][] dataTable1 = new Object[completeList.size()][5];
        int i = 0;
        for(Map.Entry<String, LikeliTriplet> entry: completeList.entrySet())
        {
            dataTable1[i][0] = entry.getKey();
            dataTable1[i][1] = entry.getValue().getDistLike();
            dataTable1[i][2] = entry.getValue().getMagLike();
            dataTable1[i][3] = entry.getValue().getTimeLike();
            if(!reportedAgency.contains(entry.getKey()))
            {
                dataTable1[i][4] = 2;
            }
            else
            {
                dataTable1[i][4] = 1;
            }
            //model1.addRow(dataTable1[i]);
            i++;
        }
        model1 = new DefaultTableModel(dataTable1,columnName);
        normalTable = new JTable(model1);

        //set column width and height
        normalTable.setRowHeight(60);
        normalTable.getColumnModel().getColumn(1).setResizable(false);
        normalTable.getColumnModel().getColumn(1).setMinWidth(60);
        normalTable.getColumnModel().getColumn(1).setMaxWidth(60);
        normalTable.getColumnModel().getColumn(2).setResizable(false);
        normalTable.getColumnModel().getColumn(2).setMinWidth(60);
        normalTable.getColumnModel().getColumn(2).setMaxWidth(60);
        normalTable.getColumnModel().getColumn(3).setResizable(false);
        normalTable.getColumnModel().getColumn(3).setMinWidth(60);
        normalTable.getColumnModel().getColumn(3).setMaxWidth(60);

        normalTable.getColumnModel().getColumn(4).setMinWidth(0);
        normalTable.getColumnModel().getColumn(4).setMaxWidth(0);

        //renderer1.setHorizontalAlignment(JLabel.CENTER);
        //renderer1.setVerticalAlignment(JLabel.CENTER);
        
        //normalTable.getColumnModel().getColumn(0).setCellRenderer(renderer1);
        
        //normalTable.getColumnModel().getColumn(1).setCellRenderer(new PictureRenderer());
        //normalTable.getColumnModel().getColumn(2).setCellRenderer(new PictureRenderer());
        //normalTable.getColumnModel().getColumn(3).setCellRenderer(new PictureRenderer());
        
        //get the expectation table
        expectList = new TreeMap<String, LikeliTriplet>();
        for(Map.Entry<String, LikeliTriplet> entry: agencyLikelihood.entrySet())
        {
            if(!reportedAgency.contains(entry.getKey()))
            {
                expectList.put(entry.getKey(), entry.getValue());
            }
        }
        
        Object[][] dataTable2 = new Object[5][5];
        i = 0;
        for(Map.Entry<String, LikeliTriplet> entry: expectList.entrySet())
        {
            dataTable2[i][0] = entry.getKey();
            dataTable2[i][1] = entry.getValue().getDistLike();
            dataTable2[i][2] = entry.getValue().getMagLike();
            dataTable2[i][3] = entry.getValue().getTimeLike();
            dataTable2[i][4] = 2;
            //model2.addRow(dataTable2[i]);
            i++;
            if(i>4)
            {
                break;
            }
        }
                
        model2 = new DefaultTableModel(dataTable2,columnName);
        expectTable = new JTable(model2);
        
        if(expectList.size()>0)
        {
        //set column width and height
        expectTable.setRowHeight(60);
        expectTable.getColumnModel().getColumn(1).setResizable(false);
        expectTable.getColumnModel().getColumn(1).setMinWidth(60);
        expectTable.getColumnModel().getColumn(1).setMaxWidth(60);
        expectTable.getColumnModel().getColumn(2).setResizable(false);
        expectTable.getColumnModel().getColumn(2).setMinWidth(60);
        expectTable.getColumnModel().getColumn(2).setMaxWidth(60);
        expectTable.getColumnModel().getColumn(3).setResizable(false);
        expectTable.getColumnModel().getColumn(3).setMinWidth(60);
        expectTable.getColumnModel().getColumn(3).setMaxWidth(60);

        expectTable.getColumnModel().getColumn(4).setMinWidth(0);
        expectTable.getColumnModel().getColumn(4).setMaxWidth(0);
        
        //BackgroundRenderer renderer1 = new BackgroundRenderer();
        //renderer1.setHorizontalAlignment(JLabel.CENTER);
        //renderer1.setVerticalAlignment(JLabel.CENTER);
        //expectTable.getColumnModel().getColumn(0).setCellRenderer(renderer1);
        
        //expectTable.getColumnModel().getColumn(1).setCellRenderer(new PictureRenderer());
        //expectTable.getColumnModel().getColumn(2).setCellRenderer(new PictureRenderer());
        //expectTable.getColumnModel().getColumn(3).setCellRenderer(new PictureRenderer());
        }
        //get values for the unexpected table
        newSet = new HashSet<String>();
        for(String agency: reportedAgency)
        {
            if(!agencyLikelihood.containsKey(agency))
            {
                newSet.add(agency);
            }
        }
        
        Object[][] dataTable3 = new Object[newSet.size()][5];
        i = 0;
        
        for(String s:newSet)
        {
            dataTable3[i][0] = s;
            dataTable3[i][1] = 0.0;
            dataTable3[i][2] = 0.0;
            dataTable3[i][3] = 0.0;
            dataTable3[i][4] = 3;
            //model3.addRow(dataTable3[i]);
        }
        model3 = new DefaultTableModel(dataTable3,columnName);
        unexpectTable = new JTable(model3);
        
        //set column width and height
        if(newSet.size()>0)
        {
        unexpectTable.setRowHeight(60);
        unexpectTable.getColumnModel().getColumn(1).setResizable(false);
        unexpectTable.getColumnModel().getColumn(1).setMinWidth(60);
        unexpectTable.getColumnModel().getColumn(1).setMaxWidth(60);
        unexpectTable.getColumnModel().getColumn(2).setResizable(false);
        unexpectTable.getColumnModel().getColumn(2).setMinWidth(60);
        unexpectTable.getColumnModel().getColumn(2).setMaxWidth(60);
        unexpectTable.getColumnModel().getColumn(3).setResizable(false);
        unexpectTable.getColumnModel().getColumn(3).setMinWidth(60);
        unexpectTable.getColumnModel().getColumn(3).setMaxWidth(60);

        unexpectTable.getColumnModel().getColumn(4).setMinWidth(0);
        unexpectTable.getColumnModel().getColumn(4).setMaxWidth(0);
        
        //BackgroundRenderer renderer1 = new BackgroundRenderer();
        //renderer1.setHorizontalAlignment(JLabel.CENTER);
        //renderer1.setVerticalAlignment(JLabel.CENTER);
        //unexpectTable.getColumnModel().getColumn(0).setCellRenderer(renderer1);
        
        //unexpectTable.getColumnModel().getColumn(1).setCellRenderer(new PictureRenderer());
        //unexpectTable.getColumnModel().getColumn(2).setCellRenderer(new PictureRenderer());
        //unexpectTable.getColumnModel().getColumn(3).setCellRenderer(new PictureRenderer());
        }
        leftScrollPane = new JScrollPane(normalTable);
        topScrollPane = new JScrollPane(expectTable);
        bottomScrollPane = new JScrollPane(unexpectTable);
        
        topBottomPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,topScrollPane,bottomScrollPane);
        topBottomPane.setResizeWeight(0.5);
        
        leftRightPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftScrollPane,topBottomPane);
        leftRightPane.setResizeWeight(0.5);
        
        this.setLayout(new BorderLayout());
        this.add(leftRightPane,BorderLayout.CENTER);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
        phasesList.addChangeListener(this);
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
        phasesList.removeChangeListener(this);
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    //if data changes, update the three tables
    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {
        
        evid = ((EventsControlViewTopComponent) tc).getControlPanel().getSelectedSeisEvent().getEvid();
        
        agencyLikelihood.clear();
        SeisDataDAO.retrieveAgencyLikelihood(agencyLikelihood, evid);
        
         //get the ordered complete list
        completeList = new TreeMap<String,LikeliTriplet>();/*(new Comparator<Object>(){

            @Override
            public int compare(Object o1, Object o2) {
              return ((LikeliTriplet)o1).getWeightedLike().compareTo(((LikeliTriplet)o1).getWeightedLike());
            }

        });*/
        //completeList.putAll(agencyLikelihood);
        for(Map.Entry<String, LikeliTriplet> entry: agencyLikelihood.entrySet())
        {
            if(reportedAgency.contains(entry.getKey()))
            {
                completeList.put(entry.getKey(), entry.getValue());
            }
        }
        
        reportedAgency.clear();
        for(Phase phase:phasesList.getPhases())
        {
            reportedAgency.add(phase.getReportAgency());
        }
            
        expectList = new TreeMap<String, LikeliTriplet>();
        for(Map.Entry<String, LikeliTriplet> entry: agencyLikelihood.entrySet())
        {
            if(!reportedAgency.contains(entry.getKey()))
            {
                expectList.put(entry.getKey(), entry.getValue());
            }
        }
        
        if(expectList.size()>0)
        {
        //set column width and height
        expectTable.setRowHeight(60);
        expectTable.getColumnModel().getColumn(1).setResizable(false);
        expectTable.getColumnModel().getColumn(1).setMinWidth(60);
        expectTable.getColumnModel().getColumn(1).setMaxWidth(60);
        expectTable.getColumnModel().getColumn(2).setResizable(false);
        expectTable.getColumnModel().getColumn(2).setMinWidth(60);
        expectTable.getColumnModel().getColumn(2).setMaxWidth(60);
        expectTable.getColumnModel().getColumn(3).setResizable(false);
        expectTable.getColumnModel().getColumn(3).setMinWidth(60);
        expectTable.getColumnModel().getColumn(3).setMaxWidth(60);

        expectTable.getColumnModel().getColumn(4).setMinWidth(0);
        expectTable.getColumnModel().getColumn(4).setMaxWidth(0);
        
        //BackgroundRenderer renderer1 = new BackgroundRenderer();
        //renderer1.setHorizontalAlignment(JLabel.CENTER);
        //renderer1.setVerticalAlignment(JLabel.CENTER);
        //expectTable.getColumnModel().getColumn(0).setCellRenderer(renderer1);
        
        //expectTable.getColumnModel().getColumn(1).setCellRenderer(new PictureRenderer());
        //expectTable.getColumnModel().getColumn(2).setCellRenderer(new PictureRenderer());
        //expectTable.getColumnModel().getColumn(3).setCellRenderer(new PictureRenderer());
        }
                
        newSet = new HashSet<String>();
        for(String agency: reportedAgency)
        {
            if(!agencyLikelihood.containsKey(agency))
            {
                newSet.add(agency);
            }
        }
        
                //set column width and height
        if(newSet.size()>0)
        {
        unexpectTable.setRowHeight(60);
        unexpectTable.getColumnModel().getColumn(1).setResizable(false);
        unexpectTable.getColumnModel().getColumn(1).setMinWidth(60);
        unexpectTable.getColumnModel().getColumn(1).setMaxWidth(60);
        unexpectTable.getColumnModel().getColumn(2).setResizable(false);
        unexpectTable.getColumnModel().getColumn(2).setMinWidth(60);
        unexpectTable.getColumnModel().getColumn(2).setMaxWidth(60);
        unexpectTable.getColumnModel().getColumn(3).setResizable(false);
        unexpectTable.getColumnModel().getColumn(3).setMinWidth(60);
        unexpectTable.getColumnModel().getColumn(3).setMaxWidth(60);

        unexpectTable.getColumnModel().getColumn(4).setMinWidth(0);
        unexpectTable.getColumnModel().getColumn(4).setMaxWidth(0);
        
        //BackgroundRenderer renderer1 = new BackgroundRenderer();
        //renderer1.setHorizontalAlignment(JLabel.CENTER);
        //renderer1.setVerticalAlignment(JLabel.CENTER);
        //unexpectTable.getColumnModel().getColumn(0).setCellRenderer(renderer1);
        
        //unexpectTable.getColumnModel().getColumn(1).setCellRenderer(new PictureRenderer());
        //unexpectTable.getColumnModel().getColumn(2).setCellRenderer(new PictureRenderer());
        //unexpectTable.getColumnModel().getColumn(3).setCellRenderer(new PictureRenderer());
        }
        
        //update model
        model1.setRowCount(0);
        Object[][] dataTable1 = new Object[completeList.size()][5];
        int i = 0;
        for(Map.Entry<String, LikeliTriplet> entry: completeList.entrySet())
        {
            dataTable1[i][0] = entry.getKey();
            dataTable1[i][1] = entry.getValue().getDistLike();
            dataTable1[i][2] = entry.getValue().getMagLike();
            dataTable1[i][3] = entry.getValue().getTimeLike();
            if(!reportedAgency.contains(entry.getKey()))
            {
                dataTable1[i][4] = 2;
            }
            else
            {
                dataTable1[i][4] = 1;
            }
            model1.addRow(dataTable1[i]);
            i++;
        }
        
        model2.setRowCount(0);
        Object[][] dataTable2 = new Object[5][5];
        i = 0;
        for(Map.Entry<String, LikeliTriplet> entry: expectList.entrySet())
        {
            dataTable2[i][0] = entry.getKey();
            dataTable2[i][1] = entry.getValue().getDistLike();
            dataTable2[i][2] = entry.getValue().getMagLike();
            dataTable2[i][3] = entry.getValue().getTimeLike();
            dataTable2[i][4] = 2;
            model2.addRow(dataTable2[i]);
            i++;
            if(i>4)
            {
                break;
            }
        }
        
        model3.setRowCount(0);
        Object[][] dataTable3 = new Object[newSet.size()][5];
        i = 0;
        
        for(String s:newSet)
        {
            dataTable3[i][0] = s;
            dataTable3[i][1] = 0.0;
            dataTable3[i][2] = 0.0;
            dataTable3[i][3] = 0.0;
            dataTable3[i][4] = 3;
            model3.addRow(dataTable3[i]);
        }
        
        model1.fireTableDataChanged();
        model2.fireTableDataChanged();
        model3.fireTableDataChanged();
        
        this.repaint();
    }
}
