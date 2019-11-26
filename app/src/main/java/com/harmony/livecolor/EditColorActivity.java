package com.harmony.livecolor;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.airbnb.lottie.LottieAnimationView;
import java.util.zip.Inflater;

import static android.graphics.Color.RGBToHSV;
import static android.graphics.Color.parseColor;

public class EditColorActivity extends AppCompatActivity {
    int colorValue;
    SeekBar seekRed, seekGreen, seekBlue;
    static TextView colorNNView;
    String name, hex, rgb, hsv;
    private boolean isButtonClicked = false;
    private boolean isButtonClickedNew = false;
    ImageButton saveNC;
    ToggleButton simpleToggleButton;
    Boolean ToggleButtonState;
    private int m_Text = 0;
    String colorNameT;
    ScaleAnimation scaleAnimation;
    ColorDatabase colorDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_color);
        colorDB = new ColorDatabase(this);


        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        colorNNView = findViewById(R.id.colorNN);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        saveNC = findViewById(R.id.saveNewColor);

        simpleToggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        ToggleButtonState = simpleToggleButton.isChecked();

        SharedPreferences preferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String colorString = preferences.getString("colorString","Default");
        colorNameT = preferences.getString("colorName","Default");
        colorValue = Integer.parseInt(colorString);
        if (intent.getExtras() != null) {
            Log.d("EditColorActivity", "BUNDLE!!");
            colorValue = bundle.getInt("colorValue");
            Log.d("EditColorActivity", "BUNDLE getting color: " + colorValue);
        }
        int RV = Color.red(colorValue);
        int GV = Color.green(colorValue);
        int BV = Color.blue(colorValue);

        // UPDATE VALUES
        ImageView colorD = (ImageView) findViewById(R.id.colorShow);
        colorD.setBackgroundColor(colorValue);
        ImageView colorNewS = (ImageView) findViewById(R.id.colorNewShow);
        colorNewS.setBackgroundColor(colorValue);


        seekRed = findViewById(R.id.seekBarRed);
        seekRed.setProgress(RV);
        seekGreen = findViewById(R.id.seekBarGreen);
        seekGreen.setProgress(GV);
        seekBlue = findViewById(R.id.seekBarBlue);
        seekBlue.setProgress(BV);

        TextView colorNameView = findViewById(R.id.colorN);
        colorNameView.setText(colorNameT);
        //When you press edit color on a saved color, the name is incorrect. This should fix it...
        //Actually doesn't work. onCreate isn't called when that happens or something? TODO fix this.
        //final double viewWidthPercentOfScreen = 0.80;
        //final float maxFontSize = 30;
        //colorNameGetter.updateViewWithColorName(colorNameView, colorValue, viewWidthPercentOfScreen, maxFontSize);

        TextView colorNameN = findViewById(R.id.colorNN);
        //colorNameN.setText(colorNameT);
        updateColorName();
        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        updateText(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress());

        simpleToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.startAnimation(scaleAnimation);
                if (isChecked) {
                    // The toggle is enabled: HSV mode
                    int[] newHSVValues = convertRGBtoHSV(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress()); // Convert the RGB values into the HSV values for the seekbars
                    updateSeekbarsHSV(newHSVValues[0],newHSVValues[1],newHSVValues[2]); // Set the seekbars to their new values
                    updateText(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress()); // Update the text to reflect the new values

                } else {
                    // The toggle is disabled: RGB mode
                    int[] newRGBValues = convertHSVtoRGB(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress()); //Convert teh HSV values to RGB values for the seekbars
                    updateSeekbarsRGB(newRGBValues[0], newRGBValues[1], newRGBValues[2]); // Set the seekbars to their new values
                    updateText(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress()); // Update the text to reflect the new values
                }
            }
        });

        seekRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                updateText(progress, seekGreen.getProgress(), seekBlue.getProgress());
                updateColorNewInput(progress, seekGreen.getProgress(), seekBlue.getProgress());
            }
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateText(progressChangedValue, seekGreen.getProgress(), seekBlue.getProgress());
                updateColorNewInput(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress());
                updateColorName();
                resetBookmark();
            }
        });

        seekGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                updateText(seekRed.getProgress(), progress, seekBlue.getProgress());
                updateColorNewInput(seekRed.getProgress(), progress, seekBlue.getProgress());
            }
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateText(seekRed.getProgress(), progressChangedValue, seekBlue.getProgress());
                updateColorNewInput(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress());
                updateColorName();
                resetBookmark();
            }
        });

        seekBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                updateText(seekRed.getProgress(), seekGreen.getProgress(), progress);
                updateColorNewInput(seekRed.getProgress(), seekGreen.getProgress(), progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateText(seekRed.getProgress(), seekGreen.getProgress(), progressChangedValue);
                updateColorNewInput(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress());
                updateColorName();
                resetBookmark();
            }
        });

        final RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setInterpolator(new LinearInterpolator());

        final ImageButton reset = findViewById(R.id.resetColor);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToggleButtonState = simpleToggleButton.isChecked();
                reset.startAnimation(rotate);
                if(!ToggleButtonState){
                    updateSeekbarsRGB(Color.red(colorValue), Color.green(colorValue), Color.blue(colorValue));
                    updateText(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress());
                } else {
                    int[] newHSVValues = convertRGBtoHSV(Color.red(colorValue), Color.green(colorValue), Color.blue(colorValue));
                    updateSeekbarsHSV(newHSVValues[0], newHSVValues[1], newHSVValues[2]);
                    updateText(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress());
                }

                updateColorNewInput(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress());
                TextView colorNameN = findViewById(R.id.colorNN);
                colorNameN.setText(colorNameT);
                resetBookmark();
            }
        });

        final ImageButton backB = findViewById(R.id.backBut);
        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backB.startAnimation(scaleAnimation);
                finish();
            }
        });

        TextView redText = findViewById(R.id.textRorH);
        redText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EditColorActivity.this);
                ToggleButtonState = simpleToggleButton.isChecked();
                if(ToggleButtonState){
                    builder.setTitle("Input a value for Hue in the range (0,360):");
                } else{
                    builder.setTitle("Input a value for Red in the range (0,255):");
                }

                // Set up the input
                final EditText input = new EditText(EditColorActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = Integer.parseInt(input.getText().toString());
                        seekRed.setProgress(m_Text);
                        updateColorName();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        TextView greenText = findViewById(R.id.textGorS);
        greenText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EditColorActivity.this);
                ToggleButtonState = simpleToggleButton.isChecked();
                if(ToggleButtonState){
                    builder.setTitle("Input a value for Saturation in the range (0,100):");
                } else{
                    builder.setTitle("Input a value for Green in the range (0,255):");
                }

                // Set up the input
                final EditText input = new EditText(EditColorActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = Integer.parseInt(input.getText().toString());
                        seekGreen.setProgress(m_Text);
                        updateColorName();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        TextView blueText = findViewById(R.id.textRorH);
        blueText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EditColorActivity.this);
                ToggleButtonState = simpleToggleButton.isChecked();
                if(ToggleButtonState){
                    builder.setTitle("Input a value for Value in the range (0,100):");
                } else{
                    builder.setTitle("Input a value for Blue in the range (0,255):");
                }

                // Set up the input
                final EditText input = new EditText(EditColorActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = Integer.parseInt(input.getText().toString());
                        seekBlue.setProgress(m_Text);
                        updateColorName();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        // TODO: ANDREW put your code here
        // |
        // |
        // V
        saveNC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(scaleAnimation);
                // Code for making the save button change color when clicked
                //saveNC.playAnimation();
                isButtonClickedNew = !isButtonClickedNew;
                saveNC.setImageResource(isButtonClickedNew ? R.drawable.bookmark_selected : R.drawable.ic_action_name);
                if(isButtonClickedNew){
                    ToggleButtonState = simpleToggleButton.isChecked();
                    int colorI = 0;
                    if(ToggleButtonState) {
                        int hue = seekRed.getProgress();
                        int sat = seekGreen.getProgress();
                        int val = seekBlue.getProgress();
                        updateColorName();
                        hsv = String.format("(%1$d, %2$d, %3$d)",hue,sat,val);
                        int[] newRGBValues = convertHSVtoRGB(hue, sat, val);
                        colorI = getIntFromColor(newRGBValues[0], newRGBValues[1], newRGBValues[2]);
                        rgb = String.format("(%1$d, %2$d, %3$d)",newRGBValues[0],newRGBValues[1],newRGBValues[2]);
                        hex = String.format( "#%02X%02X%02X", newRGBValues[0], newRGBValues[1], newRGBValues[2] );
                        colorDB.addColorInfoData(name, hex, rgb, hsv);
                    } else {
                        colorI = getIntFromColor(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress());
                        int red = seekRed.getProgress();
                        int green = seekGreen.getProgress();
                        int blue = seekBlue.getProgress();
                        updateColorName();
                        rgb = String.format("(%1$d, %2$d, %3$d)", red, green, blue);
                        hex = String.format( "#%02X%02X%02X", red, green, blue);
                        int[] hue = convertRGBtoHSV(red,green,blue);
                        hsv = String.format("(%1$d, %2$d, %3$d)",hue[0],hue[1],hue[2]);
                        colorDB.addColorInfoData(name, hex, rgb, hsv);

                    }
                    saveNC.setColorFilter(colorI);
                }else{
                    saveNC.setColorFilter(null);
                }

            }
        });

    }

    // Resets the "save" button for the new color to the "unsaved" state
    public void resetBookmark(){
        if(isButtonClickedNew){
            saveNC.setImageResource(R.drawable.ic_action_name);
            saveNC.setColorFilter(null);
            isButtonClickedNew = false;
        }
    }

    // Updates the seekbars to the HSV values
    public void updateSeekbarsHSV(int hue, int saturation, int value){
        seekRed.setMax(360);
        seekRed.setProgress(hue);
        seekGreen.setMax(100);
        seekGreen.setProgress(saturation);
        seekBlue.setMax(100);
        seekBlue.setProgress(value);
    }

    // Updates the seekbars to the RGB values
    public void updateSeekbarsRGB(int red, int green, int blue){
        seekRed.setMax(255);
        seekRed.setProgress(red);
        seekGreen.setMax(255);
        seekGreen.setProgress(green);
        seekBlue.setMax(255);
        seekBlue.setProgress(blue);
    }

    //A generic updateText function that checks for HSV or RGB state, & takes in the three ints of the seekbars
    public void updateText(int updateR, int updateG, int updateB){
        TextView A = (TextView) findViewById(R.id.textRorH);
        TextView B = (TextView) findViewById(R.id.textGorS);
        TextView C = (TextView) findViewById(R.id.textBorV);
        ToggleButtonState = simpleToggleButton.isChecked();
        if(!ToggleButtonState){
            String fullRedText = String.format("Red: %1$d", updateR);
            A.setText(fullRedText);
            String fullGreenText = String.format("Green: %1$d", updateG);
            B.setText(fullGreenText);
            String fullBlueText = String.format("Blue: %1$d", updateB);
            C.setText(fullBlueText);
        } else {
            String fullHueText = String.format("Hue: %1$d", updateR);
            A.setText(fullHueText);
            String fullSaturationText = String.format("Saturation: %1$d", updateG);
            B.setText(fullSaturationText);
            String fullValueText = String.format("Value: %1$d", updateB);
            C.setText(fullValueText);
        }
    }

    public int[] convertRGBtoHSV(int red, int green, int blue){
        float[] hsvArray = new float[3];
        RGBToHSV(red,green,blue,hsvArray);
        int[] convertedHSVForSeekbars = new int[3];
        convertedHSVForSeekbars[0] = Math.round(hsvArray[0]);
        convertedHSVForSeekbars[1] = Math.round((hsvArray[1])*100);
        convertedHSVForSeekbars[2] = Math.round((hsvArray[2])*100);
        return convertedHSVForSeekbars;
    }

    // Converts the current values in HSV to RGB and stores them in RV, GV, BV
    public static int[] convertHSVtoRGB(int hue, int saturation, int value){
        float[] hsv = new float[3];
        hsv[0] = hue;
        hsv[1] = ((float) saturation) / 100;
        hsv[2] = ((float) value) / 100;
        int outputColor = Color.HSVToColor(hsv);
        int[] newRGBValues = new int[3];
        newRGBValues[0] = Color.red(outputColor);
        newRGBValues[1] = Color.green(outputColor);
        newRGBValues[2] = Color.blue(outputColor);
        return newRGBValues;
    }

    public void updateColorNewInput(int redOrHue, int greenOrSat, int blueOrVal){
        ImageView colorNewS = (ImageView) findViewById(R.id.colorNewShow);

        //Get the current state of the toggle button - RGB or HSV
        ToggleButtonState = simpleToggleButton.isChecked();

        int colorI = 0;
        if(ToggleButtonState){
            // In HSV mode
            int[] getRGBValue = convertHSVtoRGB(redOrHue, greenOrSat, blueOrVal);
            colorI = getIntFromColor(getRGBValue[0], getRGBValue[1], getRGBValue[2]);
        }else {
            colorI = getIntFromColor(redOrHue, greenOrSat, blueOrVal);
        }

        colorNewS.setBackgroundColor(colorI);

        //updateColorPicker();
    }

    public void updateColorName(){
        ToggleButtonState = simpleToggleButton.isChecked();

        int redOrHue = seekRed.getProgress();
        int greenOrSat = seekGreen.getProgress();
        int blueOrValue = seekBlue.getProgress();

        int colorI = 0;
        if(ToggleButtonState){
            int[] getRGBValues = convertHSVtoRGB(redOrHue, greenOrSat, blueOrValue);
            colorI = getIntFromColor(getRGBValues[0], getRGBValues[1], getRGBValues[2]);
        } else {
            colorI = getIntFromColor(redOrHue, greenOrSat, blueOrValue);
        }

        EditColorActivity.colorNNView = this.findViewById(R.id.colorNN);
        //colorNameGetter cng = new colorNameGetter();
        final double viewWidthPercentOfScreen = 0.80;
        final float maxFontSize = 30;
        colorNameGetter.updateViewWithColorName(colorNNView, colorI, viewWidthPercentOfScreen, maxFontSize);
        //cng.execute(colorI);
        //TODO this probably won't work,
        // the name won't be updated by the time this code runs.
        // This is where it gets the text to save if you save the color.
        //I think the easiest way to solve this would be to just call colorNameGetter for each
        //  saved color, since those already are having problems with names going to multiple lines.
        name = colorNNView.getText().toString();
    }

    public void updateColorPicker(){
        //TODO: Decide if returning new color to color picker or keeping the old
    }

    public int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }
}
