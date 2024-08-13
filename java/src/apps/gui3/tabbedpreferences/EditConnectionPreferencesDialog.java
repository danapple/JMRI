package apps.gui3.tabbedpreferences;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import jmri.*;
import jmri.swing.PreferencesPanel;
import jmri.util.swing.JmriJOptionPane;

import apps.gui3.tabbedpreferences.Bundle;

/**
 * Provide a Connection preferences dialog.
 * <p>
 * References the status of an {@link EditConnectionPreferences} object that
 * is created (via new()) as part of this constructor.
 *
 * @author Kevin Dickerson Copyright 2010
 */
public final class EditConnectionPreferencesDialog extends JDialog implements WindowListener {

    final EditConnectionPreferences editConnectionPreferences;
    boolean restartProgram = false;

    @Override
    public String getTitle() {
        return editConnectionPreferences.getTitle();
    }

    public boolean isMultipleInstances() {
        return true;
    }

    /**
     * Displays a dialog for editing the connections.
     *
     * @return true if the program should restart, false if the program should quit.
     */
    public static boolean showDialog() {
        if (! InstanceManager.getDefault(PermissionManager.class)
                .checkPermission(PermissionsSystemAdmin.PERMISSION_EDIT_PREFERENCES)) {
            return false;
        }
        EditConnectionPreferencesDialog dialog = new EditConnectionPreferencesDialog();
        SwingUtilities.updateComponentTreeUI(dialog);  // hack because sometimes this was created before L&F was set?
        dialog.pack();
        dialog.setBounds(
                (int) ( jmri.util.JmriJFrame.getScreenDimensions().get(0).getBounds().getSize().getWidth() * 0.10 ),
                (int) ( jmri.util.JmriJFrame.getScreenDimensions().get(0).getBounds().getSize().getHeight() * 0.10 ),
                (int) ( jmri.util.JmriJFrame.getScreenDimensions().get(0).getBounds().getSize().getWidth() * 0.80 ),
                (int) (jmri.util.JmriJFrame.getScreenDimensions().get(0).getBounds().getSize().getHeight()* 0.80 ));
        dialog.setVisible(true);
        return dialog.restartProgram;
    }

    public EditConnectionPreferencesDialog() {
        super();
        setModal(true);
        editConnectionPreferences = new EditConnectionPreferences(this);
        editConnectionPreferences.init();
        add(editConnectionPreferences);
//        addHelpMenu("package.apps.TabbedPreferences", true); // NOI18N
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(EditConnectionPreferencesDialog.this);
    }

    public void gotoPreferenceItem(String item, String sub) {
        editConnectionPreferences.gotoPreferenceItem(item, sub);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        ShutDownManager sdm = InstanceManager.getDefault(ShutDownManager.class);
        if (!editConnectionPreferences.isPreferencesValid() && !sdm.isShuttingDown()) {
            for (PreferencesPanel panel : editConnectionPreferences.getPreferencesPanels().values()) {
                if (!panel.isPreferencesValid()) {
                    switch (JmriJOptionPane.showConfirmDialog(this,
                            Bundle.getMessage("InvalidPreferencesMessage", panel.getTabbedPreferencesTitle()),
                            Bundle.getMessage("InvalidPreferencesTitle"),
                            JmriJOptionPane.YES_NO_OPTION,
                            JmriJOptionPane.ERROR_MESSAGE)) {
                        case JmriJOptionPane.YES_OPTION:
                            // abort window closing and return to broken preferences
                            editConnectionPreferences.gotoPreferenceItem(panel.getPreferencesItem(), panel.getTabbedPreferencesTitle());
                            return;
                        default:
                            // do nothing
                            break;
                    }
                }
            }
        }
        if (editConnectionPreferences.isDirty()) {
            switch (JmriJOptionPane.showConfirmDialog(this,
                    Bundle.getMessage("UnsavedChangesMessage", editConnectionPreferences.getTitle()), // NOI18N
                    Bundle.getMessage("UnsavedChangesTitle"), // NOI18N
                    JmriJOptionPane.YES_NO_CANCEL_OPTION,
                    JmriJOptionPane.QUESTION_MESSAGE)) {
                case JmriJOptionPane.YES_OPTION:
                    // save preferences
                    editConnectionPreferences.savePressed(editConnectionPreferences.invokeSaveOptions());
                    break;
                case JmriJOptionPane.NO_OPTION:
                    // do nothing
                    break;
                case JmriJOptionPane.CANCEL_OPTION:
                default:
                    // abort window closing
                    return;
            }
        }
        this.setVisible(false);
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
