package jmri.jmrit.operations.automation;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import jmri.jmrit.operations.OperationsTestCase;
import jmri.util.JUnitUtil;
import jmri.util.JmriJFrame;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class AutomationsTableFrameActionTest extends OperationsTestCase {

    @Test
    public void testCTor() {
        AutomationsTableFrameAction t = new AutomationsTableFrameAction();
        Assert.assertNotNull("exists",t);
    }
    
    @Test
    public void testAction() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        AutomationsTableFrameAction a = new AutomationsTableFrameAction();
        Assert.assertNotNull("exists", a);
        
        a.actionPerformed(new ActionEvent(this, 0, null));
        
        JmriJFrame f = JmriJFrame.getFrame(Bundle.getMessage("TitleAutomationsTableFrame"));
        Assert.assertNotNull("exists", f);
        JUnitUtil.dispose(f);
    }

    // private final static Logger log = LoggerFactory.getLogger(AutomationsTableFrameActionTest.class);

}
