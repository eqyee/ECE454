package edu.wisc.ece.uiapp;

import android.util.Log;


/**
 * Created by CCS on 10/18/2015.
 */
public class AlgorithmsDet {


    final static int ARRAY_SIZE = 360;
    static boolean isInside = false;


    static LineObject[] LINESTORAGE;
    static int pointer = 0;
    //Creates a line object with all necessary data
    public static class LineObject{
        public long curr_time;
        public float step_count;
        public LineObject(float step_count){
            this.curr_time = System.currentTimeMillis();
            this.step_count = step_count;

        }
    }
    //Adds an object to           our LineStorage array
    public static int inLine(){

        int mistakes = 1;
        int runner = pointer;
        int sec_runner = pointer -1;
        int total = 0;
        int mistakes_in_row = 0;
        if (MainActivity.inside != true){
            while( sec_runner != -1 && LINESTORAGE[sec_runner] != null && sec_runner != pointer){
                sec_runner -= 1;
                runner -= 1;
                if (runner < 0){
                    runner = ARRAY_SIZE-1;
                }
                if (sec_runner < 0){
                    sec_runner = ARRAY_SIZE-1;
                }
                try {
                    if (LINESTORAGE[runner].step_count - LINESTORAGE[sec_runner].step_count < 4) {

                        total += 1;
                        mistakes_in_row = 0;

                    }
                    else{
                        Log.d("Mistake!", Integer.toString(mistakes_in_row));
                        total += 1;
                        mistakes += 1;
                        mistakes_in_row += 1;
                    }
                }
                catch (NullPointerException e){
                    break;
                }

                if (total > 6 && total/mistakes < 3 || mistakes_in_row > 2){
                    break;
                }


            }

        }



        return total;

    }

    public static void createLineObject(){
        LineObject temp = new LineObject(StepCount.stepCount);
        addLineObject(temp);
    }
    public static void addLineObject(LineObject line){
        if (pointer == ARRAY_SIZE){
            pointer = 0;
            LINESTORAGE[pointer] = line;
        }
        else{
            LINESTORAGE[pointer] = line;
            pointer += 1;

        }
    }

}
