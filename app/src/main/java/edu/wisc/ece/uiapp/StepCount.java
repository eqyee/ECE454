package edu.wisc.ece.uiapp;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by CCS on 10/18/2015.
 */
public class StepCount implements SensorEventListener {
    public static float stepCount;
    private final Context context;
    public SensorManager sensorManager;

    public StepCount(Context context){
        this.context = context;
        initializeSensor(context);

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            stepCount = event.values[0];
            Log.d("STEPCOUNT", Float.toString(stepCount));
        }

    }

    public void initializeSensor(Context context){
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }
}
