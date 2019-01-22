# STEMI Hexapod

v1.1.2

Android library for easy STEMI Hexapod app programming

## Getting via jcenter

```groovy
implementation 'STEMI:stemihexapod:1.1.2'
```

## Examples

1. Create new instance of Hexapod.
```groovy
Hexapod hexapod = new Hexapod();
hexapod.connect();
hexapod.hexapodStatus = this;
```

2. Moving around straight line
```groovy
hexapod.goForward();
hexapod.goBackward();
hexapod.goLeft();
hexapod.goRight();
```

3. Moving around with circular joystick
```groovy
// left joystick
hexapod.setJoystickParameters(power, angle);
// right joystick
hexapod.setJoystickParameters(rotation);
```

4. Connecting in calibration mode
```groovy
Hexapod hexapod = new Hexapod(true);
```
5. Setting custom IP address
```groovy
hexapod.setIpAdddress(ipAddress);
```
