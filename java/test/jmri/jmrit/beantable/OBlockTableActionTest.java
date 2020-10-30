package jmri.jmrit.beantable;

import java.awt.GraphicsEnvironment;

import jmri.InstanceManager;
import jmri.util.JUnitUtil;
import jmri.util.JmriJFrame;
import jmri.util.ThreadingUtil;

import jmri.util.gui.GuiLafPreferencesManager;
import org.junit.jupiter.api.*;
import org.junit.Assert;
import org.junit.Assume;
import org.netbeans.jemmy.QueueTool;

/**
 * Swing tests for the OBlock table.
 *
 * @author Pete Cressman Copyright 2016
 */
public class OBlockTableActionTest {

    @Test
    public void testInvoke() throws Exception {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        // use original _desktop interface
        InstanceManager.getDefault(GuiLafPreferencesManager.class).setOblockEditTabbed(false);

        // ask for the window to open
        OBlockTableAction a = new OBlockTableAction();
        a.actionPerformed(new java.awt.event.ActionEvent(a, 1, ""));

        // Find new table window by name
        JmriJFrame doc = JmriJFrame.getFrame(jmri.jmrit.beantable.oblock.Bundle.getMessage("TitleOBlocks"));
        Assert.assertNotNull("Occupancy window", doc);
        new QueueTool().waitEmpty();

        javax.swing.JDesktopPane dt = (javax.swing.JDesktopPane) doc.getContentPane();
        javax.swing.JInternalFrame[] fob = dt.getAllFrames();
        Assert.assertNotNull("OBlock window", fob);

        Assert.assertEquals(4, fob.length);
        new QueueTool().waitEmpty();
        // Ask to close add window
        ThreadingUtil.runOnGUI(() -> {
            doc.setVisible(false);
            JUnitUtil.dispose(doc);
        });
    }

    @BeforeEach
    public void setUp() throws Exception {
        JUnitUtil.setUp();
        jmri.util.JUnitUtil.resetProfileManager();
        jmri.util.JUnitUtil.initDefaultUserMessagePreferences();
    }

    @AfterEach
    public void tearDown() throws Exception {
        JUnitUtil.resetWindows(false,false);
        JUnitUtil.deregisterBlockManagerShutdownTask();
        JUnitUtil.tearDown();
    }
}
