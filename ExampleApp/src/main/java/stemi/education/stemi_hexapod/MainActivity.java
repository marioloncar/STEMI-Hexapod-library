package stemi.education.stemi_hexapod;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import stemi.education.stemihexapod.ConnectingCompleteCallback;
import stemi.education.stemihexapod.Hexapod;
import stemi.education.stemihexapod.HexapodStatus;

/**
 * ExampleApp
 *
 * Check out complete app here: https://github.com/marioloncar/STEMI-Android
 */

public class MainActivity extends AppCompatActivity implements HexapodStatus, View.OnClickListener {

    private Hexapod hexapod;
    private ToggleButton tBtnStandby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tBtnStandby = (ToggleButton) findViewById(R.id.tBtnStandby);
        tBtnStandby.setChecked(true);
        tBtnStandby.setSelected(true);

        hexapod = new Hexapod();
        hexapod.connect();
        hexapod.hexapodStatus = this;

    }

    @Override
    public void connectionStatus(boolean isConnected) {
        if (!isConnected) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Connection lost :(", Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnF:
                hexapod.goForward();
                break;
            case R.id.btnB:
                hexapod.goBackward();
                break;
            case R.id.btnL:
                hexapod.goLeft();
                break;
            case R.id.btnR:
                hexapod.goRight();
                break;
            case R.id.tBtnStandby:
                if (tBtnStandby.isSelected()) {
                    hexapod.turnOff();
                    tBtnStandby.setSelected(false);
                    tBtnStandby.setTextOff("Off");
                    tBtnStandby.setChecked(false);
                    hexapod.stopMoving();
                } else {
                    hexapod.turnOn();
                    tBtnStandby.setSelected(true);
                    tBtnStandby.setTextOn("On");
                    tBtnStandby.setChecked(true);
                }
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hexapod.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hexapod.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hexapod.disconnect();
    }
}
