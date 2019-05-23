package jmri.implementation;

import java.util.Date;
import javax.annotation.Nonnull;
import javax.swing.Timer;
import jmri.InstanceManager;
import jmri.Light;
import jmri.NamedBeanHandle;
import jmri.Sensor;
import jmri.Timebase;
import jmri.Turnout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Each LightControl object is linked to a specific Light, and provides one of
 * the controls available for switching the Light ON/OFF in response to time or
 * events occurring on the layout.
 * <p>
 * Each LightControl holds the information for one control of the parent Light.
 * <p>
 * Each Light may have as many controls as desired by the user. It is the user's
 * responsibility to ensure that the various control mechanisms do not conflict
 * with one another.
 * <p>
 * Available control types are those defined in the Light.java interface.
 * Control types: SENSOR_CONTROL FAST_CLOCK_CONTROL TURNOUT_STATUS_CONTROL
 * TIMED_ON_CONTROL TWO_SENSOR_CONTROL
 *
 * <hr>
 * This file is part of JMRI.
 * <p>
 * JMRI is free software; you can redistribute it and/or modify it under the
 * terms of version 2 of the GNU General Public License as published by the Free
 * Software Foundation. See the "COPYING" file for a copy of this license.
 * <p>
 * JMRI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * @author Dave Duchamp Copyright (C) 2010
 */
public class LightControl {

    /**
     * Main constructor methods
     */
    public LightControl() {
    }

    public LightControl(jmri.Light l) {
        _parentLight = l;
    }

    // instance variables - saved with Light in configuration file
    private int _controlType = Light.NO_CONTROL;    // control type
    private String _controlSensorName = "";   // controlling Sensor if SENSOR_CONTROL
    protected int _controlSensorSense = Sensor.ACTIVE;  // sense of Sensor for Light ON
    private int _fastClockOnHour = 0;         // on Hour if FAST_CLOCK_CONTROL
    private int _fastClockOnMin = 0;          // on Minute if FAST_CLOCK_CONTROL
    private int _fastClockOffHour = 0;        // off Hour if FAST_CLOCK_CONTROL
    private int _fastClockOffMin = 0;         // off Minute if FAST_CLOCK_CONTROL
    private String _controlTurnoutName = "";  // turnout whose status is shown if TURNOUT_STATUS_CONTROL
    private int _turnoutState = Turnout.CLOSED;  // turnout state corresponding to this Light ON
    private String _timedSensorName = "";     // trigger Sensor if TIMED_ON_CONTROL
    protected int _timeOnDuration = 0;          // duration (milliseconds) if TIMED_ON_CONTROL
    private String _controlSensor2Name = ""; // second controlling sensor if TWO_SENSOR_CONTROL

    /*
     * Create a New LightControl from existing,
     * for use when editing a LightControl
     *
     * @param lc the LightControl to be copied
     */
    public LightControl(@Nonnull LightControl lc) {
        this._controlType = lc._controlType;
        this._controlSensorName = lc._controlSensorName;
        this._controlSensorSense = lc._controlSensorSense;
        this._fastClockOnHour = lc._fastClockOnHour;
        this._fastClockOnMin = lc._fastClockOnMin;
        this._fastClockOffHour = lc._fastClockOffHour;
        this._fastClockOffMin = lc._fastClockOffMin;
        this._controlTurnoutName = lc._controlTurnoutName;
        this._turnoutState = lc._turnoutState;
        this._timedSensorName = lc._timedSensorName;
        this._timeOnDuration = lc._timeOnDuration;
        this._controlSensor2Name = lc._controlSensor2Name;
    }
    
    /*
     * Test if a LightControl is equal to this one
     *
     * @param o the LightControl object to be checked
     * @return True if the LightControl is equal, else false
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) {
            return true;
        }
        if (!(o instanceof LightControl)) {
            return false;
        }
        LightControl that = (LightControl) o;
        if (that._controlType != this._controlType) return false;
        
        switch(_controlType) {
            case Light.NO_CONTROL : 
                return true;
            case Light.SENSOR_CONTROL : 
                if (! that._controlSensorName.equals(this._controlSensorName)) return false;
                if (that._controlSensorSense != this._controlSensorSense) return false;
                return true;
            case Light.FAST_CLOCK_CONTROL : 
                if (that._fastClockOnHour != this._fastClockOnHour) return false;
                if (that._fastClockOnMin != this._fastClockOnMin) return false;
                if (that._fastClockOffHour != this._fastClockOffHour) return false;
                if (that._fastClockOffMin != this._fastClockOffMin) return false;
                return true;
            case Light.TURNOUT_STATUS_CONTROL : 
                if (! that._controlTurnoutName.equals(this._controlTurnoutName)) return false;
                if (that._turnoutState != this._turnoutState) return false;
                return true;
            case Light.TIMED_ON_CONTROL : 
                if (! that._timedSensorName.equals(this._timedSensorName)) return false;
                if (that._timeOnDuration != this._timeOnDuration) return false;
                return true;
            case Light.TWO_SENSOR_CONTROL : 
                if (! that._controlSensorName.equals(this._controlSensorName)) return false;
                if (that._controlSensorSense != this._controlSensorSense) return false;
                if (! that._controlSensor2Name.equals(this._controlSensor2Name)) return false;
                return true;
            default:
                // unexpected _controlType value
                jmri.util.Log4JUtil.warnOnce(log, "Unexpected _controlType = {}", _controlType);
                return true; // since _controlType matches
        }
    }
    
    @Override
    public int hashCode() {
        // matches with equals() by contract
        return _controlType;
    }

    /*
     * Get the control type used by the Control
     *
     * @return the Control Type, eg. FAST_CLOCK_CONTROL
     */
    public int getControlType() {
        return _controlType;
    }

    /*
     * Set the control type used by the Control
     * Does NOT update any changelisteners
     *
     * @param type the Control Type, eg. FAST_CLOCK_CONTROL
     */
    public void setControlType(int type) {
        _controlType = type;
    }

    /*
     * Set Sensor 1 used by the 1 Sensor and 2 Sensor Control
     * Does NOT update any changelisteners
     *
     * @param type the Sensor name
     */
    public void setControlSensorName(String sensorName) {
        _controlSensorName = sensorName;
    }

    /*
     * Get the Sensor State used by the 1 Sensor Control
     *
     * @return Sensor.ACTIVE or Sensor.INACTIVE
     */
    public int getControlSensorSense() {
        return _controlSensorSense;
    }

    /*
     * Get the Sensor 1 name for 1 and 2 Sensor Control Types.
     *
     * @return  If a Sensor is registered, returns the Sensor.getName()
     *          else the Sensor Name as set by #setControlSensorName
     */
    public String getControlSensorName() {
        if (_namedControlSensor != null) {
            return _namedControlSensor.getName();
        }
        return _controlSensorName;
    }

    /*
     * Set the Sensor State used by the Control
     * Does NOT update any changelisteners
     *
     * @param sense The state to react to, eg. Sensor.ACTIVE or Sensor.INACTIVE
     */
    public void setControlSensorSense(int sense) {
        _controlSensorSense = sense;
    }

    /*
     * Get the Fast Clock On Hour.
     *
     * @return  On Hour value
     */
    public int getFastClockOnHour() {
        return _fastClockOnHour;
    }

    /*
     * Get the Fast Clock On Minute.
     *
     * @return  On Minute value
     */
    public int getFastClockOnMin() {
        return _fastClockOnMin;
    }

    /*
     * Get the Fast Clock Off Hour.
     *
     * @return  Off Hour value
     */
    public int getFastClockOffHour() {
        return _fastClockOffHour;
    }

    /*
     * Get the Fast Clock Off Minute.
     *
     * @return  Off Minute value
     */
    public int getFastClockOffMin() {
        return _fastClockOffMin;
    }

    /*
     * Set a Fast Clock LightControl Schedule.
     *
     * @param onHour Hour the Light should switch On
     * @param onMin Minute the Light should switch On
     * @param offHour Hour the Light should switch Off
     * @param offMin Minute the Light should switch Off     * 
     */
    public void setFastClockControlSchedule(int onHour, int onMin, int offHour, int offMin) {
        _fastClockOnHour = onHour;
        _fastClockOnMin = onMin;
        _fastClockOffHour = offHour;
        _fastClockOffMin = offMin;
    }

    /*
     * Get the LightControl Turnout Name.
     *
     * @return  The Turnout name
     */
    public String getControlTurnoutName() {
        return _controlTurnoutName;
    }

    /*
     * Set the Turnout used by the Control
     * Does NOT update any changelisteners
     *
     * @param turnoutName The Turnout name
     */
    public void setControlTurnout(String turnoutName) {
        _controlTurnoutName = turnoutName;
    }

    /*
     * Get the LightControl Turnout Name.
     *
     * @return  The Turnout name
     */
    public int getControlTurnoutState() {
        return _turnoutState;
    }

    /*
     * Set the Turnout State used by the Control
     * Does NOT update any changelisteners
     *
     * @param state Turnout state to act on, eg. Turnout.CLOSED or Turnout.THROWN
     */
    public void setControlTurnoutState(int state) {
        _turnoutState = state;
    }

    /*
     * Get the Timed On Trigger Sensor name.
     *
     * @return  If a Sensor is registered, returns the Sensor.getName()
     *          else the Sensor Name as set by #setControlTimedOnSensorName
     */
    public String getControlTimedOnSensorName() {
        if (_namedTimedControlSensor != null) {
            return _namedTimedControlSensor.getName();
        }
        return _timedSensorName;
    }

    /*
     * Set Sensor used by the Timed On Control
     * Does NOT update any changelisteners
     *
     * @param sensorName the Sensor name to be used for the On Trigger
     */
    public void setControlTimedOnSensorName(String sensorName) {
        _timedSensorName = sensorName;
    }

    /*
     * Get the Timed On Control Duration
     *
     * @return duration in ms
     */
    public int getTimedOnDuration() {
        return _timeOnDuration;
    }

    /*
     * Set Duration used by the Timed On Control
     * Does NOT update any changeListeners
     *
     * @param duration in ms following the Sensor On Trigger
     */
    public void setTimedOnDuration(int duration) {
        _timeOnDuration = duration;
    }

    /*
     * Get the Second Sensor name.
     * as used in the 2 Sensor Control Group.
     *
     * @return  If a 2nd Sensor is registered, returns the Sensor.getName()
     *          else the 2nd Sensor Name as set by #setControlSensor2Name
     */
    public String getControlSensor2Name() {
        if (_namedControlSensor2 != null) {
            return _namedControlSensor2.getName();
        }
        return _controlSensor2Name;
    }

    /*
     * Set Sensor 2 used by the 2 Sensor Control
     * Does NOT update any changelisteners
     *
     * @param type the Sensor 2 name
     */
    public void setControlSensor2Name(String sensorName) {
        _controlSensor2Name = sensorName;
    }

    /*
     * Set Light to control
     * Does NOT update any changelisteners
     *
     * @param l the Light object to control
     */
    public void setParentLight(Light l) {
        _parentLight = l;
    }

    // operational instance variables - not saved between runs
    protected Light _parentLight = null;        // Light that is being controlled   
    private boolean _active = false;
    protected NamedBeanHandle<Sensor> _namedControlSensor = null;
    private java.beans.PropertyChangeListener _sensorListener = null;
    protected NamedBeanHandle<Sensor> _namedControlSensor2 = null;
    private java.beans.PropertyChangeListener _sensor2Listener = null;
    private java.beans.PropertyChangeListener _timebaseListener = null;
    protected Timebase _clock = null;
    protected int _timeOn = 0;
    protected int _timeOff = 0;
    protected Turnout _controlTurnout = null;
    private java.beans.PropertyChangeListener _turnoutListener = null;
    protected NamedBeanHandle<Sensor> _namedTimedControlSensor = null;
    private java.beans.PropertyChangeListener _timedSensorListener = null;
    protected Timer _timedControlTimer = null;
    protected java.awt.event.ActionListener _timedControlListener = null;
    private boolean _lightOnTimerActive = false;

    protected jmri.NamedBeanHandleManager nbhm = jmri.InstanceManager.getDefault(jmri.NamedBeanHandleManager.class);

    /**
     * Activates a Light Control by control type. This method tests the control
     * type, and set up a control mechanism, appropriate for the control type.
     * Adds PropertyChangeListeners to Sensors / Turnouts / Fast Clock as necessary
     */
    public void activateLightControl() {
        // skip if Light Control is already active
        if (!_active) {
            // activate according to control type
            switch (_controlType) {
                case Light.SENSOR_CONTROL:
                    _namedControlSensor = null;
                    if (_controlSensorName.length() > 0) {
                        Sensor sen = InstanceManager.sensorManagerInstance().
                                provideSensor(_controlSensorName);
                        _namedControlSensor = nbhm.getNamedBeanHandle(_controlSensorName, sen);
                    }
                    if (_namedControlSensor != null) {
                        // if sensor state is currently known, set light accordingly
                        int kState = _namedControlSensor.getBean().getKnownState();
                        if (kState == Sensor.ACTIVE) {
                            if (_controlSensorSense == Sensor.ACTIVE) {
                                // Turn light on
                                _parentLight.setState(Light.ON);
                            } else {
                                // Turn light off
                                _parentLight.setState(Light.OFF);
                            }
                        } else if (kState == Sensor.INACTIVE) {
                            if (_controlSensorSense == Sensor.INACTIVE) {
                                // Turn light on
                                _parentLight.setState(Light.ON);
                            } else {
                                // Turn light off
                                _parentLight.setState(Light.OFF);
                            }
                        }

                        // listen for change in sensor state
                        _namedControlSensor.getBean().addPropertyChangeListener(_sensorListener
                                = new java.beans.PropertyChangeListener() {
                                    @Override
                                    public void propertyChange(java.beans.PropertyChangeEvent e) {
                                        if (!_parentLight.getEnabled()) {
                                            return;  // ignore property change if user disabled Light
                                        }
                                        if (e.getPropertyName().equals("KnownState")) {
                                            int now = _namedControlSensor.getBean().getKnownState();
                                            if (now == Sensor.ACTIVE) {
                                                if (_controlSensorSense == Sensor.ACTIVE) {
                                                    // Turn light on
                                                    _parentLight.setState(Light.ON);
                                                } else {
                                                    // Turn light off
                                                    _parentLight.setState(Light.OFF);
                                                }
                                            } else if (now == Sensor.INACTIVE) {
                                                if (_controlSensorSense == Sensor.INACTIVE) {
                                                    // Turn light on
                                                    _parentLight.setState(Light.ON);
                                                } else {
                                                    // Turn light off
                                                    _parentLight.setState(Light.OFF);
                                                }
                                            }
                                        }
                                    }
                                }, _controlSensorName, "Light Control " + _parentLight.getDisplayName());
                        _active = true;
                    } else {
                        // control sensor does not exist
                        log.error("Light " + _parentLight.getSystemName()
                                + " is linked to a Sensor that does not exist: " + _controlSensorName);
                        return;
                    }
                    break;

                case Light.FAST_CLOCK_CONTROL:
                    if (_clock == null) {
                        _clock = InstanceManager.getDefault(jmri.Timebase.class);
                    }
                    // set up time as minutes in a day
                    _timeOn = _fastClockOnHour * 60 + _fastClockOnMin;
                    _timeOff = _fastClockOffHour * 60 + _fastClockOffMin;
                    // initialize light based on current fast time
                    updateClockControlLight();
                    // set up to listen for time changes on a minute basis
                    _clock.addMinuteChangeListener(_timebaseListener
                            = new java.beans.PropertyChangeListener() {
                                @Override
                                public void propertyChange(java.beans.PropertyChangeEvent e) {
                                    if (_parentLight.getEnabled()) {  // don't change light if not enabled
                                        // update control if light is enabled
                                        updateClockControlLight();
                                    }
                                }
                            });
                    _active = true;
                    break;
                case Light.TURNOUT_STATUS_CONTROL:
                    try {
                        _controlTurnout = InstanceManager.turnoutManagerInstance().
                                provideTurnout(_controlTurnoutName);
                    } catch (IllegalArgumentException e) {
                        // control turnout does not exist
                        log.error("Light " + _parentLight.getSystemName()
                                + " is linked to a Turnout that does not exist: " + _controlSensorName);
                        return;
                    }
                    
                    // set light based on current turnout state if known
                    int tState = _controlTurnout.getKnownState();
                    if (tState == Turnout.CLOSED) {
                        if (_turnoutState == Turnout.CLOSED) {
                            // Turn light on
                            _parentLight.setState(Light.ON);
                        } else {
                            // Turn light off
                            _parentLight.setState(Light.OFF);
                        }
                    } else if (tState == Turnout.THROWN) {
                        if (_turnoutState == Turnout.THROWN) {
                            // Turn light on
                            _parentLight.setState(Light.ON);
                        } else {
                            // Turn light off
                            _parentLight.setState(Light.OFF);
                        }
                    }

                    // listen for change in turnout state
                    _controlTurnout.addPropertyChangeListener(_turnoutListener
                            = new java.beans.PropertyChangeListener() {
                                @Override
                                public void propertyChange(java.beans.PropertyChangeEvent e) {
                                    if (!_parentLight.getEnabled()) {
                                        return;  // ignore property change if user disabled light
                                    }
                                    if (e.getPropertyName().equals("KnownState")) {
                                        int now = _controlTurnout.getKnownState();
                                        if (now == Turnout.CLOSED) {
                                            if (_turnoutState == Turnout.CLOSED) {
                                                // Turn light on
                                                _parentLight.setState(Light.ON);
                                            } else {
                                                // Turn light off
                                                _parentLight.setState(Light.OFF);
                                            }
                                        } else if (now == Turnout.THROWN) {
                                            if (_turnoutState == Turnout.THROWN) {
                                                // Turn light on
                                                _parentLight.setState(Light.ON);
                                            } else {
                                                // Turn light off
                                                _parentLight.setState(Light.OFF);
                                            }
                                        }
                                    }
                                }
                            });
                    _active = true;
                    break;
                case Light.TIMED_ON_CONTROL:
                    if (_timedSensorName.length() > 0) {
                        Sensor sen = InstanceManager.sensorManagerInstance().
                                provideSensor(_timedSensorName);
                        _namedTimedControlSensor = nbhm.getNamedBeanHandle(_timedSensorName, sen);
                    }
                    if (_namedTimedControlSensor != null) {
                        // set initial state off
                        _parentLight.setState(Light.OFF);
                        // listen for change in timed control sensor state
                        _namedTimedControlSensor.getBean().addPropertyChangeListener(_timedSensorListener
                                = new java.beans.PropertyChangeListener() {
                                    @Override
                                    public void propertyChange(java.beans.PropertyChangeEvent e) {
                                        if (!_parentLight.getEnabled()) {
                                            return;  // ignore property change if user disabled light
                                        }
                                        if (e.getPropertyName().equals("KnownState")) {
                                            int now = _namedTimedControlSensor.getBean().getKnownState();
                                            if (!_lightOnTimerActive) {
                                                if (now == Sensor.ACTIVE) {
                                                    // Turn light on
                                                    _parentLight.setState(Light.ON);
                                                    // Create a timer if one does not exist
                                                    if (_timedControlTimer == null) {
                                                        _timedControlListener = new TimeLight();
                                                        _timedControlTimer = new Timer(_timeOnDuration,
                                                                _timedControlListener);
                                                    }
                                                    // Start the Timer to turn the light OFF
                                                    _lightOnTimerActive = true;
                                                    _timedControlTimer.start();
                                                }
                                            }
                                        }
                                    }
                                }, _timedSensorName, "Light Control " + _parentLight.getDisplayName());
                        _active = true;
                    } else {
                        // timed control sensor does not exist
                        log.error("Light " + _parentLight.getSystemName()
                                + " is linked to a Sensor that does not exist: " + _timedSensorName);
                        return;
                    }
                    break;
                case Light.TWO_SENSOR_CONTROL:
                    _namedControlSensor = null;
                    _namedControlSensor2 = null;
                    if (_controlSensorName.length() > 0) {
                        Sensor sen = InstanceManager.sensorManagerInstance().
                                provideSensor(_controlSensorName);
                        _namedControlSensor = nbhm.getNamedBeanHandle(_controlSensorName, sen);
                    }
                    if (_controlSensor2Name.length() > 0) {
                        Sensor sen = InstanceManager.sensorManagerInstance().
                                provideSensor(_controlSensor2Name);
                        _namedControlSensor2 = nbhm.getNamedBeanHandle(_controlSensor2Name, sen);
                    }
                    if ((_namedControlSensor != null) && (_namedControlSensor2 != null)) {
                        // if sensor state is currently known, set light accordingly
                        int kState = _namedControlSensor.getBean().getKnownState();
                        int kState2 = _namedControlSensor2.getBean().getKnownState();
                        if (_controlSensorSense == Sensor.ACTIVE) {
                            if ((kState == Sensor.ACTIVE) || (kState2 == Sensor.ACTIVE)) {
                                // Turn light on
                                _parentLight.setState(Light.ON);
                            } else {
                                // Turn light off
                                _parentLight.setState(Light.OFF);
                            }
                        } else if (_controlSensorSense == Sensor.INACTIVE) {
                            if ((kState == Sensor.INACTIVE) || (kState2 == Sensor.INACTIVE)) {
                                // Turn light on
                                _parentLight.setState(Light.ON);
                            } else {
                                // Turn light off
                                _parentLight.setState(Light.OFF);
                            }
                        }

                        // listen for change in sensor states
                        _namedControlSensor.getBean().addPropertyChangeListener(_sensorListener
                                = new java.beans.PropertyChangeListener() {
                                    @Override
                                    public void propertyChange(java.beans.PropertyChangeEvent e) {
                                        twoSensorChanged(e);
                                    }
                                }, _controlSensorName, "Light Control " + _parentLight.getDisplayName());
                        _namedControlSensor2.getBean().addPropertyChangeListener(_sensor2Listener
                                = new java.beans.PropertyChangeListener() {
                                    @Override
                                    public void propertyChange(java.beans.PropertyChangeEvent e) {
                                        twoSensorChanged(e);
                                    }
                                }, _controlSensor2Name, "Light Control " + _parentLight.getDisplayName());
                        _active = true;
                    } else {
                        // at least one control sensor does not exist
                        log.error("Light " + _parentLight.getSystemName()
                                + " is linked to a Sensor that does not exist: ");
                        return;
                    }
                    break;
                default:
                    log.warn("Unexpected control type when activating Light: " + _parentLight.getSystemName());
            }
        }
    }

    /**
     * Internal routine for handling sensor changes
     * for the 2 Sensor Control Type
     */
    protected void twoSensorChanged(java.beans.PropertyChangeEvent e) {
        if (!_parentLight.getEnabled()) {
            return;  // ignore property change if user disabled Light
        }
        if (e.getPropertyName().equals("KnownState")) {
            int kState = _namedControlSensor.getBean().getKnownState();
            int kState2 = _namedControlSensor2.getBean().getKnownState();
            if (_controlSensorSense == Sensor.ACTIVE) {
                if ((kState == Sensor.ACTIVE) || (kState2 == Sensor.ACTIVE)) {
                    // Turn light on
                    _parentLight.setState(Light.ON);
                } else {
                    // Turn light off
                    _parentLight.setState(Light.OFF);
                }
            } else if (_controlSensorSense == Sensor.INACTIVE) {
                if ((kState == Sensor.INACTIVE) || (kState2 == Sensor.INACTIVE)) {
                    // Turn light on
                    _parentLight.setState(Light.ON);
                } else {
                    // Turn light off
                    _parentLight.setState(Light.OFF);
                }
            }
        }
    }

    /**
     * Updates the status of a Light under FAST_CLOCK_CONTROL. This method is
     * called every FastClock minute.
     */
    @SuppressWarnings("deprecation")
    private void updateClockControlLight() {
        if (_clock != null) {
            Date now = _clock.getTime();
            int timeNow = now.getHours() * 60 + now.getMinutes();
            int state = _parentLight.getState();
            if (_timeOn <= _timeOff) {
                // on and off the same day
                if ((timeNow < _timeOn) || (timeNow >= _timeOff)) {
                    // Light should be OFF
                    if (state == Light.ON) {
                        _parentLight.setState(Light.OFF);
                    }
                } else {
                    // Light should be ON
                    if (state == Light.OFF) {
                        _parentLight.setState(Light.ON);
                    }
                }
            } else {
                // on and off - different days
                if ((timeNow >= _timeOn) || (timeNow < _timeOff)) {
                    // Light should be ON
                    if (state == Light.OFF) {
                        _parentLight.setState(Light.ON);
                    }
                } else {
                    // Light should be OFF
                    if (state == Light.ON) {
                        _parentLight.setState(Light.OFF);
                    }
                }
            }
        }
    }

    /**
     * Deactivates a LightControl by control type. This method tests the control
     * type, and deactivates the control mechanism, appropriate for the control
     * type.
     */
    public void deactivateLightControl() {
        // skip if Light Control is not active
        if (_active) {
            // deactivate according to control type
            switch (_controlType) {
                case Light.SENSOR_CONTROL:
                    if (_sensorListener != null) {
                        _namedControlSensor.getBean().removePropertyChangeListener(_sensorListener);
                        _sensorListener = null;
                    }
                    break;
                case Light.FAST_CLOCK_CONTROL:
                    if ((_clock != null) && (_timebaseListener != null)) {
                        _clock.removeMinuteChangeListener(_timebaseListener);
                        _timebaseListener = null;
                    }
                    break;
                case Light.TURNOUT_STATUS_CONTROL:
                    if (_turnoutListener != null) {
                        _controlTurnout.removePropertyChangeListener(_turnoutListener);
                        _turnoutListener = null;
                    }
                    break;
                case Light.TIMED_ON_CONTROL:
                    if (_timedSensorListener != null) {
                        _namedTimedControlSensor.getBean().removePropertyChangeListener(_timedSensorListener);
                        _timedSensorListener = null;
                    }
                    if (_lightOnTimerActive) {
                        _timedControlTimer.stop();
                        _lightOnTimerActive = false;
                    }
                    if (_timedControlTimer != null) {
                        if (_timedControlListener != null) {
                            _timedControlTimer.removeActionListener(_timedControlListener);
                            _timedControlListener = null;
                        }
                        _timedControlTimer = null;
                    }
                    break;
                case Light.TWO_SENSOR_CONTROL:
                    if (_sensorListener != null) {
                        _namedControlSensor.getBean().removePropertyChangeListener(_sensorListener);
                        _sensorListener = null;
                    }
                    if (_sensor2Listener != null) {
                        _namedControlSensor2.getBean().removePropertyChangeListener(_sensor2Listener);
                        _sensor2Listener = null;
                    }
                    break;
                default:
                    log.warn("Unexpected control type when activating Light: " + _parentLight.getSystemName());
            }
            _active = false;
        }
    }

    /**
     * Class for defining ActionListener for TIMED_ON_CONTROL
     */
    class TimeLight implements java.awt.event.ActionListener {

        @Override
        public void actionPerformed(java.awt.event.ActionEvent event) {
            // Turn Light OFF
            _parentLight.setState(Light.OFF);
            // Turn Timer OFF
            _timedControlTimer.stop();
            _lightOnTimerActive = false;
        }
    }

    private final static Logger log = LoggerFactory.getLogger(LightControl.class);
}
