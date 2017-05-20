package stemi.education.stemihexapod;

/**
 * @author Mario
 * @version 1.1
 * <p>
 * Copyright (C) 2017 Mario Loncar. All rights reserved.
 */


/**
 * Class for controlling STEMI Hexapod robot
 */

public class Hexapod implements PacketSenderStatus, CalibrationPacketSenderStatus {
    Packet currentPacket;
    PacketSender packetSender;
    CalibrationPacket calibrationPacket;
    CalibrationPacketSender calibrationPacketSender;
    String ipAddress;
    String defaultIP = "192.168.4.1";
    int defaultPort = 80;
    int port;
    private byte[] slidersArray = {50, 25, 0, 0, 0, 50, 0, 0, 0, 0, 0};
    private boolean calibrationModeEnabled = false;
    private byte[] initialCalibrationData = new byte[18];
    public HexapodStatus hexapodStatus = new HexapodStatus() {
        @Override
        public void connectionStatus(boolean isConnected) {
        }
    };

    @Override
    public void calibrationConnectionLost() {
        hexapodStatus.connectionStatus(false);
    }

    @Override
    public void calibrationConnectionActive() {
        hexapodStatus.connectionStatus(true);
    }

    @Override
    public void connectionLost() {
        hexapodStatus.connectionStatus(false);
    }

    @Override
    public void connectionActive() {
        hexapodStatus.connectionStatus(true);
    }


    /**
     * Initializes default connection with IP address: 192.168.4.1 and port: 80
     */
    public Hexapod() {
        this.ipAddress = this.defaultIP;
        this.port = this.defaultPort;
        this.currentPacket = new Packet();
    }

    /**
     * Initializes default connection with IP address: 192.168.4.1 and port: 80. Includes calibration mode.
     *
     * @param withCalibrationMode Takes true or false if calibration mode should be enabled.
     */
    public Hexapod(boolean withCalibrationMode) {
        this.calibrationModeEnabled = withCalibrationMode;
        this.ipAddress = this.defaultIP;
        this.port = this.defaultPort;
        if (calibrationModeEnabled) {
            this.calibrationPacket = new CalibrationPacket();
        } else {
            this.currentPacket = new Packet();
        }

    }

    /**
     * Initializes connection with custom IP address and port.
     *
     * @param ip   Takes given IP Address (default: 192.168.4.1)
     * @param port Takes given port (default: 80)
     */
    public Hexapod(String ip, int port) {
        this.ipAddress = ip;
        this.port = port;
        this.currentPacket = new Packet();
    }

    /**
     * Sets new IP address. By default this is set to 192.168.4.1
     *
     * @param newIpAddress Takes given IP address
     */
    public void setIpAddress(String newIpAddress) {
        this.ipAddress = newIpAddress;
    }

    /**
     * Establish connection with Hexapod. After connection is established, it sends new packet every 100 ms.
     */
    public void connect() {
        if (calibrationModeEnabled) {
            calibrationPacketSender = new CalibrationPacketSender(this);
            calibrationPacketSender.calibrationPacketSenderStatus = this;
            calibrationPacketSender.enterCalibrationMode(new EnterCalibrationCallback() {
                @Override
                public void onEnteredCalibration(boolean entered) {
                    if (entered) {
                        initialCalibrationData = calibrationPacket.legsValues;
                    }
                }
            });

        } else {
            packetSender = new PacketSender(this);
            packetSender.packetSenderStatus = this;
            packetSender.startSendingData();
        }
    }

    /**
     * Establish connection with Hexapod with completion callback. After connection is established, it sends new packet every 100 ms.
     */
    public void connectWithCompletion(final ConnectingCompleteCallback connectingCompleteCallback) {
        if (calibrationModeEnabled) {
            calibrationPacketSender = new CalibrationPacketSender(this);
            calibrationPacketSender.calibrationPacketSenderStatus = this;
            calibrationPacketSender.enterCalibrationMode(new EnterCalibrationCallback() {
                @Override
                public void onEnteredCalibration(boolean entered) {
                    if (entered) {
                        initialCalibrationData = calibrationPacket.legsValues;
                        connectingCompleteCallback.onConnectingComplete(true);
                    }
                }
            });

        } else {
            packetSender = new PacketSender(this);
            packetSender.packetSenderStatus = this;
            packetSender.startSendingData();
            connectingCompleteCallback.onConnectingComplete(true);
        }
    }


    /**
     * Stop sending data to Hexapod and closes connection.
     */
    public void disconnect() {
        if (calibrationModeEnabled) {
            calibrationPacketSender.stopSendingData();
        } else {
            packetSender.stopSendingData();
        }

    }

    /**
     * Moves Hexapod forward with max power.
     */
    public void goForward() {
        stopMoving();
        currentPacket.power = 100;
    }

    /**
     * Moves Hexapod backwards with max power.
     */
    public void goBackward() {
        stopMoving();
        currentPacket.power = 100;
        currentPacket.angle = 90;
    }

    /**
     * Moves Hexapod left with max power.
     */
    public void goLeft() {
        stopMoving();
        currentPacket.power = 100;
        currentPacket.angle = 210;
    }

    /**
     * Moves Hexapod right with max power.
     */
    public void goRight() {
        stopMoving();
        currentPacket.power = 100;
        currentPacket.angle = 45;
    }

    /**
     * Rotate Hexapod left with max power.
     */
    public void turnLeft() {
        stopMoving();
        currentPacket.rotation = 156;
    }

    /**
     * Rotate Hexapod right with max power.
     */
    public void turnRight() {
        stopMoving();
        currentPacket.rotation = 100;
    }

    /**
     * Turns orientation mode on and tilt Hexapod forward.
     */
    public void tiltForward() {
        setOrientationMode();
        currentPacket.accelerometerX = 226;
    }

    /**
     * Turns orientation mode on and tilt Hexapod backwards.
     */
    public void tiltBackward() {
        setOrientationMode();
        currentPacket.accelerometerX = 30;
    }

    /**
     * Turns orientation mode on and tilt Hexapod left.
     */
    public void tiltLeft() {
        setOrientationMode();
        currentPacket.accelerometerY = 226;
    }

    /**
     * Turns orientation mode on and tilt Hexapod right.
     */
    public void tiltRight() {
        setOrientationMode();
        currentPacket.accelerometerY = 30;
    }

    /**
     * Sets parameters for moving Hexapod with custom Joystick. This is intended for moving the Hexapod: forward, backward, left and right.
     * <p>
     * It is proposed for user to use a circular joystick!
     * <p>
     * angle values: Because Byte values are only positive numbers from 0 to 255, Hexapod gets angle as shown:
     * For angle 0 - 180 you can use 0-90 (original divided by 2)
     * For angle 180 - 360 you can use 166-255 (this can be represented like value from -180 to 0. Logic is same: 255 + (original devided by 2))
     *
     * @param power Takes values for movement speed (Values must be: 0-100)
     * @param angle Takes values for angle of moving (Values can be: 0-255, look at the description!)
     */
    public void setJoystickParameters(int power, int angle) {
        currentPacket.power = power;
        currentPacket.angle = angle;
    }

    /**
     * Sets parameters for moving Hexapod with custom Joystick. This is intended for rotating the Hexapod left and right.
     * <p>
     * It is proposed for user to use a linear (left to right) joystick!
     * <p>
     * angle values: Because Byte values are only positive numbers from 0 to 255, Hexapod gets rotation as shown:
     * For rotate to right you can use values 0 - 100
     * For rotate to left you can use 255-156 (this can be represented like value from 0 to -100 as 255 - position)
     *
     * @param rotation Takes values for rotation speed (Values must be: 0-255, look at the description!)
     */
    public void setJoystickParameters(int rotation) {
        currentPacket.rotation = rotation;
    }

    /**
     * Sets parameters for tilding Hexapod in X direction.
     * <p>
     * This value must be max 40!
     * <p>
     * x values: Because Byte values are only positive numbers from 0 to 255, Hexapod gets x rotation as shown:
     * For tilt forward you can use values 0 - 216 (this can be represented like value from 0 to -100 as 255 - position)
     * For tilt backward you can use 0 - 100.
     *
     * @param x Takes values for X tilting (Values must be: 0-255, look at the description!)
     */
    public void setAccelerometerX(int x) {
        currentPacket.accelerometerX = x;
    }

    /**
     * Sets parameters for tilding Hexapod in Y direction.
     * <p>
     * This value must be max 40!
     * <p>
     * y values: because Byte values are only positive numbers from 0 to 255, Hexapod gets y rotation as shown:
     * For tilt left you can use values 0 - 216 (this can be represented like value from 0 to -100 as 255 - position.)
     * For tilt right you can use 0 - 100.
     *
     * @param y Takes values for Y tilting (Values must be: 0-255, look at the description!)
     */
    public void setAccelerometerY(int y) {
        currentPacket.accelerometerY = y;
    }

    /**
     * Stops Hexapod by setting power, angle and rotation to 0.
     */
    public void stopMoving() {
        currentPacket.power = 0;
        currentPacket.angle = 0;
        currentPacket.rotation = 0;
    }

    /**
     * Resets all Hexapod moving and tilt values to 0.
     */
    public void resetMovingParameters() {
        currentPacket.power = 0;
        currentPacket.angle = 0;
        currentPacket.rotation = 0;
        currentPacket.staticTilt = 0;
        currentPacket.movingTilt = 0;
        currentPacket.onOff = 1;
        currentPacket.accelerometerX = 0;
        currentPacket.accelerometerY = 0;
    }

    /**
     * In this mode, Hexapod can move forward, backwards, left and right, and it can rotate itself to left and right.
     * Accelerometer is off.
     */
    public void setMovementMode() {
        currentPacket.staticTilt = 0;
        currentPacket.movingTilt = 0;
    }

    /**
     * In this mode, Hexapod can tilt backward, forward, left and right, and rotate left and right by accelerometer and joystick in place without moving.
     * Accelerometer is on.
     */
    public void setRotationMode() {
        currentPacket.staticTilt = 1;
        currentPacket.movingTilt = 0;
    }

    /**
     * This is combination of rotation and movement mode, Hexapod can move forward, backward, left and right, and it can rotate itself to left and right.
     * Furthermore the Hexapod can tilt forward, backward, left and right by accelerometer.
     * Accelerometer is on.
     */
    public void setOrientationMode() {
        currentPacket.staticTilt = 0;
        currentPacket.movingTilt = 1;
    }


    /**
     * Puts Hexapod in standby.
     */
    public void turnOn() {
        currentPacket.onOff = 1;
    }

    /**
     * Puts Hexapod out from standby.
     */
    public void turnOff() {
        currentPacket.onOff = 0;
    }

    /**
     * Gets Hexapod standby status.
     */
    public boolean isInStandby() {
        return currentPacket.onOff == 0 ? true : false;
    }

    /**
     * Set Hexapod height.
     *
     * @param height This value can be from 0 to 100.
     */
    public void setHeight(int height) {
        currentPacket.height = height;
    }

    /**
     * Set Hexapod walking style.
     *
     * @param walkingStyle This value can be TRIPOD_GAIT, TRIPOD_GAIT_ANGLED, TRIPOD_GAIT_STAR or WAVE_GAIT.
     */
    public void setWalkingStyle(WalkingStyle walkingStyle) {
        int walkingStyleValue;
        switch (walkingStyle.ordinal()) {
            case 0:
                walkingStyleValue = 30;
                break;
            case 1:
                walkingStyleValue = 60;
                break;
            case 2:
                walkingStyleValue = 80;
                break;
            case 3:
                walkingStyleValue = 100;
                break;
            default:
                walkingStyleValue = 30;
        }
        currentPacket.walkingStyle = walkingStyleValue;
    }

    /**
     * Sets calibration value at given leg index.
     *
     * @param value Value of Hexapod motor.
     * @param index Index of Hexapod motor.
     */
    public void setCalibrationValue(byte value, int index) {
        if (value >= 0 && value <= 100) {
            calibrationPacket.legsValues[index] = value;
        } else {
            throw new IndexOutOfBoundsException("Value out of bounds");
        }

    }

    /**
     * Increase calibration value at given leg index.
     *
     * @param index Takes leg index.
     */
    public void increaseCalibrationValueAtIndex(int index) {
        if (calibrationPacket.legsValues[index] < 100) {
            calibrationPacket.legsValues[index]++;
        }
    }

    /**
     * Decrease calibration value at given leg index.
     *
     * @param index Takes leg index.
     */
    public void decreaseCalibrationValueAtIndex(int index) {
        if (calibrationPacket.legsValues[index] > 0) {
            calibrationPacket.legsValues[index]--;
        }
    }

    /**
     * Writes new calibration values to Hexapod.
     *
     * @throws InterruptedException
     */
    public void writeDataToHexapod(SavedCalibrationCallback savedCalibrationCallback) throws InterruptedException {
        calibrationPacketSender.stopSendingData();
        Thread.sleep(500);
        calibrationPacket.writeToHexapod = CalibrationPacket.WriteData.Yes.ordinal();
        calibrationPacketSender.sendOnePacket();
        Thread.sleep(1000);
        savedCalibrationCallback.onSavedData(true);
    }

    /**
     * Get calibration data saved on Hexapod.
     *
     * @return initial array of calibration values stored on Hexapod.
     */
    public byte[] fetchDataFromHexapod() {
        return initialCalibrationData;
    }

}

