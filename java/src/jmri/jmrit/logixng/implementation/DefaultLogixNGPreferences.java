package jmri.jmrit.logixng.implementation;

import java.util.prefs.Preferences;

import jmri.beans.PreferencesBean;
import jmri.jmrit.logixng.LogixNGPreferences;
import jmri.jmrit.logixng.MaleSocket.ErrorHandlingType;
import jmri.profile.ProfileManager;
import jmri.profile.ProfileUtils;

/**
 * Preferences for LogixNG
 *
 * @author Daniel Bergqvist Copyright 2018
 */
public final class DefaultLogixNGPreferences extends PreferencesBean implements LogixNGPreferences {

    public static final String START_LOGIXNG_ON_LOAD = "startLogixNGOnStartup";
    public static final String USE_GENERIC_FEMALE_SOCKETS = "useGenericFemaleSockets";
    public static final String INSTALL_DEBUGGER = "installDebugger";
    public static final String SHOW_SYSTEM_USER_NAMES = "showSystemUserNames";
    public static final String ERROR_HANDLING_TYPE = "errorHandlingType";
    public static final String TREE_EDITOR_HIGHLIGHT_ROW = "treeEditorHighlightRow";
    public static final String SHOW_SYSTEM_NAMES = "showSystemNames";

    private boolean _startLogixNGOnLoad = true;
    private boolean _showSystemUserNames = false;
    private boolean _installDebugger = true;
    private ErrorHandlingType _errorHandlingType = ErrorHandlingType.ShowDialogBox;
    private boolean _treeEditorHighlightRow = false;
    private boolean _showSystemNames = false;


    public DefaultLogixNGPreferences() {
        super(ProfileManager.getDefault().getActiveProfile());
//        System.out.format("LogixNG preferences%n");
        Preferences sharedPreferences = ProfileUtils.getPreferences(
                super.getProfile(), this.getClass(), true);
        this.readPreferences(sharedPreferences);
    }

    private void readPreferences(Preferences sharedPreferences) {
        _startLogixNGOnLoad = sharedPreferences.getBoolean(START_LOGIXNG_ON_LOAD, _startLogixNGOnLoad);
        _installDebugger = sharedPreferences.getBoolean(INSTALL_DEBUGGER, _installDebugger);
        _showSystemUserNames = sharedPreferences.getBoolean(SHOW_SYSTEM_USER_NAMES, _showSystemUserNames);
        _errorHandlingType = ErrorHandlingType.valueOf(
                sharedPreferences.get(ERROR_HANDLING_TYPE, _errorHandlingType.name()));
        _treeEditorHighlightRow = sharedPreferences.getBoolean(TREE_EDITOR_HIGHLIGHT_ROW, _treeEditorHighlightRow);
        _showSystemNames = sharedPreferences.getBoolean(SHOW_SYSTEM_NAMES, _showSystemNames);

        setIsDirty(false);
    }

    @Override
    public boolean compareValuesDifferent(LogixNGPreferences prefs) {
        if (getStartLogixNGOnStartup() != prefs.getStartLogixNGOnStartup()) {
            return true;
        }
        if (getInstallDebugger() != prefs.getInstallDebugger()) {
            return true;
        }
        if (getShowSystemUserNames() != prefs.getShowSystemUserNames()) {
            return true;
        }
        if (getTreeEditorHighlightRow() != prefs.getTreeEditorHighlightRow()) {
            return true;
        }
        if (getShowSystemNames() != prefs.getShowSystemNames()) {
            return true;
        }
        return (getErrorHandlingType() != prefs.getErrorHandlingType());
    }

    @Override
    public void apply(LogixNGPreferences prefs) {
        setStartLogixNGOnStartup(prefs.getStartLogixNGOnStartup());
        setInstallDebugger(prefs.getInstallDebugger());
        setShowSystemUserNames(prefs.getShowSystemUserNames());
        this.setErrorHandlingType(prefs.getErrorHandlingType());
        setTreeEditorHighlightRow(prefs.getTreeEditorHighlightRow());
        setShowSystemNames(prefs.getShowSystemNames());
    }

    @Override
    public void save() {
        Preferences sharedPreferences = ProfileUtils.getPreferences(this.getProfile(), this.getClass(), true);
        sharedPreferences.putBoolean(START_LOGIXNG_ON_LOAD, this.getStartLogixNGOnStartup());
        sharedPreferences.putBoolean(INSTALL_DEBUGGER, this.getInstallDebugger());
        sharedPreferences.putBoolean(SHOW_SYSTEM_USER_NAMES, this.getShowSystemUserNames());
        sharedPreferences.put(ERROR_HANDLING_TYPE, this.getErrorHandlingType().name());
        sharedPreferences.putBoolean(TREE_EDITOR_HIGHLIGHT_ROW, this.getTreeEditorHighlightRow());
        sharedPreferences.putBoolean(SHOW_SYSTEM_NAMES, this.getShowSystemNames());
        setIsDirty(false);
    }

    @Override
    public void setStartLogixNGOnStartup(boolean value) {
        _startLogixNGOnLoad = value;
        setIsDirty(true);
    }

    @Override
    public boolean getStartLogixNGOnStartup() {
        return _startLogixNGOnLoad;
    }

    @Override
    public void setShowSystemUserNames(boolean value) {
        _showSystemUserNames = value;
        setIsDirty(true);
    }

    @Override
    public boolean getShowSystemUserNames() {
        return _showSystemUserNames;
    }

    @Override
    public void setInstallDebugger(boolean value) {
        _installDebugger = value;
        setIsDirty(true);
    }

    @Override
    public boolean getInstallDebugger() {
        return _installDebugger;
    }

    @Override
    public void setErrorHandlingType(ErrorHandlingType type) {
        _errorHandlingType = type;
        setIsDirty(true);
    }

    @Override
    public ErrorHandlingType getErrorHandlingType() {
        return _errorHandlingType;
    }

    @Override
    public void setTreeEditorHighlightRow(boolean value) {
        _treeEditorHighlightRow = value;
        setIsDirty(true);
    }

    @Override
    public boolean getTreeEditorHighlightRow() {
        return _treeEditorHighlightRow;
    }

    @Override
    public void setShowSystemNames(boolean value) {
        _showSystemNames = value;
        setIsDirty(true);
    }

    @Override
    public boolean getShowSystemNames() {
        return _showSystemNames;
    }

//    private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LogixNGPreferences.class);
}
