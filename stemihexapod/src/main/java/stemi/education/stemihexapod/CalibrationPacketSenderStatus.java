package stemi.education.stemihexapod;

/**
 * Created by Mario on 15/04/2017.
 */

interface CalibrationPacketSenderStatus {
    void calibrationConnectionLost();
    void calibrationConnectionActive();
}
