package uk.ac.isc.textview;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import uk.ac.isc.seisdata.VBASLogger;

class PhaseTablePopupManager implements ActionListener {

    JTable table;
    JPopupMenu popupMenu;
    PhaseEditDialog phaseEditDialog;
    private ArrayList<PhaseEditData> phaseEditDataList = new ArrayList<PhaseEditData>();

    public PhaseTablePopupManager(JTable phaseTable) {
        table = phaseTable;
        setPopupMenuVisualAttributes();

        phaseEditDialog = new PhaseEditDialog();
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    /*
     * Menu item selected.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(VBASLogger.debugAt());

        // Selected row values
        int[] selectedRows = table.getSelectedRows();
        int[] selectedColumns = table.getSelectedColumns();

        phaseEditDataList.clear();

        //Issue #8 : Single Phase Edit
        if (selectedRows.length == 1) {
            int phaseId = (Integer) table.getValueAt(selectedRows[0], 14);
            String type = (String) table.getValueAt(selectedRows[0], 6);
            phaseEditDataList.add(new PhaseEditData(phaseId, type, true, false, null, false, "", null));
        } else {
            for (int i : selectedRows) {
                int phaseId = (Integer) table.getValueAt(i, 14);
                String type = (String) table.getValueAt(i, 6);
                phaseEditDataList.add(new PhaseEditData(phaseId, type, false, false, null, false, "", null));
            }
        }

        if ("Phase Edit..".equals(e.getActionCommand())) {

            phaseEditDialog.setLocationRelativeTo(table);
            phaseEditDialog.showPhaseEditDialog(phaseEditDataList);
        }

    }

    private void setPopupMenuVisualAttributes() {
        popupMenu = new JPopupMenu();

        JMenuItem menuItem_edit = new JMenuItem("Phase Edit..");
        /*menuItem_edit.setBackground(new Color(218, 83, 44));
         menuItem_edit.setForeground(Color.WHITE);*/
        menuItem_edit.setFont(new Font("Sans-serif", Font.PLAIN, 14));

        popupMenu.add(menuItem_edit);

        menuItem_edit.addActionListener(this);
    }

}
