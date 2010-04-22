// NetworkDriverAdapter.java

package jmri.jmrix.ecos.networkdriver;

import jmri.jmrix.ConnectionStatus;
import jmri.jmrix.JmrixConfigPane;
import jmri.jmrix.ecos.*;

/*import java.io.*;
import java.net.*;
import java.util.Vector;*/

/**
 * Implements SerialPortAdapter for the ECOS system network connection.
 * <P>This connects
 * an ECOS command station via a telnet connection.
 * Normally controlled by the NetworkDriverFrame class.
 *
 * @author	Bob Jacobsen   Copyright (C) 2001, 2002, 2003, 2008
 * @version	$Revision: 1.11 $
 */
public class NetworkDriverAdapter extends EcosPortController implements jmri.jmrix.NetworkPortAdapter{

    public NetworkDriverAdapter() {
        super();
        adaptermemo = new jmri.jmrix.ecos.EcosSystemConnectionMemo();
    }
    /**
     * set up all of the other objects to operate with an ECOS command
     * station connected to this port
     */
    public void configure() {
        // connect to the traffic controller
        EcosTrafficController control = EcosTrafficController.instance();
        control.connectPort(this);
        adaptermemo.setEcosTrafficController(control);
        adaptermemo.configureManagers();
        
        jmri.jmrix.ecos.ActiveFlag.setActive();
    }


    public boolean status() {return opened;}

    // private control members
    private boolean opened = false;
    
    static public NetworkDriverAdapter instance() {
        if (mInstance == null) {
            mInstance = new NetworkDriverAdapter();
            mInstance.setPort(15471);
            mInstance.setManufacturer(jmri.jmrix.DCCManufacturerList.ESU);
        }
        return mInstance;
    }
    static NetworkDriverAdapter mInstance = null;
    
    //The following needs to be enabled once systemconnectionmemo has been correctly implemented
    //public SystemConnectionMemo getSystemConnectionMemo() { return adaptermemo; }
    
    //To be completed
    public void dispose(){
        adaptermemo.dispose();
        adaptermemo = null;
    }

    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(NetworkDriverAdapter.class.getName());

}
