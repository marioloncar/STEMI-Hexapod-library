package stemi.education.stemihexapod;

/**
 * Hexapod connection status
 */

public interface HexapodStatus {

    /**
     * Check if app is connected to STEMI Hexapod.
     * returns True if stemi is connected and sending data. False if it is not connected or not sending data.
     */
    void connectionStatus(boolean isConnected);
}
