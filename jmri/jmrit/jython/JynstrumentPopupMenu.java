package jmri.jmrit.jython;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class JynstrumentPopupMenu extends JPopupMenu {
	private static final ResourceBundle jythonBundle = ResourceBundle.getBundle("jmri/jmrit/jython/JythonBundle");
	
	Jynstrument jynstrument; // The jynstrument itself
	
	public JynstrumentPopupMenu(Jynstrument it) {
		super(it.getName());
		jynstrument = it;
		initMenu();
		it.addMouseListener(new MouseAdapter(){
    		public void mousePressed(MouseEvent e) {
    	        maybeShowPopup(e);
    	    }
    	    public void mouseReleased(MouseEvent e) {
    	        maybeShowPopup(e);
    	    }
    	    private void maybeShowPopup(MouseEvent e) {
    	    	if (! (e.getComponent() instanceof Jynstrument)) {
    	    		return;
    	    	}
	        	Jynstrument it = (Jynstrument) e.getComponent();
    	        if (e.isPopupTrigger()) {
    	        	it.getPopUpMenu().show(e.getComponent(),e.getX(), e.getY());
    	        }
    	    }
    	});		
	}
	
	private void initMenu() {
		// Quit option
		JMenuItem quitMenuItem = new JMenuItem(jythonBundle.getString("JynstrumentPopupMenuQuit"));
		quitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jynstrument.exit();
				jynstrument = null;
			} 
		} );
		add(quitMenuItem);  		
		// Edit option
/*		JMenuItem editMenuItem = new JMenuItem(jythonBundle.getString("JynstrumentPopupMenuEdit"));
		editMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//			if (!java.awt.Desktop.isDesktopSupported()) //TODO: Need Java 6
//				    return;
				log.debug("Not implemented");
			} 
		} );
		editMenuItem.setEnabled(false);
		add(editMenuItem);  		
		// Reload option
		JMenuItem reloadMenuItem = new JMenuItem(jythonBundle.getString("JynstrumentPopupMenuReload"));
		reloadMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.debug("Not implemented"); // TODO
			} 
		} );
		reloadMenuItem.setEnabled(false);
		add(reloadMenuItem);*/  		
		// Debug option
		add(new jmri.jmrit.jython.JythonWindow(jythonBundle.getString("JynstrumentPopupMenuDebug")));
		// A separator to differentiate Jynstrument private menu items
		addSeparator();
	}

    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JynstrumentPopupMenu.class.getName());
}

