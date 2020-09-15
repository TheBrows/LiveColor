package com.harmony.livecolor;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Color.RGBToHSV;
import static com.harmony.livecolor.UsefulFunctions.convertRGBtoHSV;

public class SettingsFragment  extends  Fragment{

    private SettingsFragment.OnFragmentInteractionListener mListener;

    TextView textViewGetToKnow;
    TextView textViewMeetTeam;
    Switch switchDarkMode;
    ToggleButton toggleButtonCotd;
    EditText editTextAccent;
    ImageButton imageButtonReset;
    ImageButton imageButtonResetImage;
    ImageButton imageButtonTodaysColor;
    RotateAnimation rotate;
    private WeakReference<Activity> mActivity;

    public SettingsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        //Bundle args = new Bundle();
        //args can be bundled and sent through here if needed
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (NightModeUtils.isNightModeEnabled(getContext())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            //if arguments are needed ever, use this to set them to static values in the class
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Lifecycles", "onCreateView: SettingsFragment created");

        //Set title on action bar to match current fragment
        getActivity().setTitle(
                getResources().getText(R.string.app_name) +
                        " - " + getResources().getText(R.string.title_settings)
        );

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        // handles customized accent
        customAccent(rootView.findViewById(R.id.constraintLayoutSettings));

        textViewGetToKnow = rootView.findViewById(R.id.textView11);
        textViewMeetTeam =  rootView.findViewById(R.id.textView13);
        switchDarkMode = rootView.findViewById(R.id.switchDarkMode);
        toggleButtonCotd = rootView.findViewById(R.id.toggleButtonCotd);
        editTextAccent = rootView.findViewById(R.id.editTextAccentHex);
        imageButtonReset = rootView.findViewById(R.id.imageButtonAccentReset);
        imageButtonResetImage = rootView.findViewById(R.id.imageButtonImageReset);
        imageButtonTodaysColor = rootView.findViewById(R.id.imageButtonTodaysColor);


        // show proper darkmode val
        switchDarkMode.setChecked(NightModeUtils.isDarkMode(getActivity()));

        // show proper Cotd val
        SharedPreferences preferences = this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        boolean isCotdEnabled = preferences.getBoolean("dialogCotd",true);
        toggleButtonCotd.setChecked(isCotdEnabled);

        // handle edit text interaction
        editTextAccent.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                afterTextChangedAccentHex(s);
                editTextAccent.setHint(AccentUtils.getAccent(getContext()));
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {}
        });
        // filter for proper hex (with #)
        InputFilter hexFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                Pattern pattern = Pattern.compile("^(#)?\\p{XDigit}+$");
                StringBuilder sb = new StringBuilder();
                if(source.length() > 7) return source.subSequence(start, end-1); // max 7 characters
                for (int i = start; i < end; i++) {
                    //Only allow characters "0123456789ABCDEF";
                    Matcher matcher = pattern.matcher(String.valueOf(source.charAt(i)));
                    if (!matcher.matches()) {
                        return source.subSequence(start, end);
                    }
                    //Add character to Strinbuilder
                    sb.append(source.charAt(i));
                }
                //Return text in UpperCase. if all good
                if (sb.toString().length() == 7 ){
                    if(sb.toString().matches("^(#)\\p{XDigit}+$")){
                        return  sb.toString().toUpperCase();
                    }else{
                        return "";
                    }
                }else{
                if (sb.toString().length() == 6 ){
                    if(sb.toString().matches("^\\p{XDigit}+$")){
                        return  sb.toString().toUpperCase();
                    }else{
                        return "";
                    }
                }
                }
                return  sb.toString().toUpperCase();
            }
        };
        editTextAccent.setFilters(new InputFilter[] { hexFilter });
        editTextAccent.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        // set hint to match accent
        editTextAccent.setHint(AccentUtils.getAccent(rootView.getContext()));
        // reset animation stuff
        rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setInterpolator(new LinearInterpolator());


        textViewGetToKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCredits(view);
            }
        });

        textViewMeetTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCredits(view);
            }
        });

        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                onCheckedChangedDarkMode(buttonView, isChecked);
            }
        });

        toggleButtonCotd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                onCheckChangedCotd(buttonView, isChecked);
            }
        });

        imageButtonReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                    onClickReset(view);
            }
        });

        imageButtonResetImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                onClickResetImage(view);
            }
        });

        imageButtonTodaysColor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d("SettingsFragment", "onClick: Today's Color button clicked");
                onClickTodaysColor();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {

        }
    }

    // https://stackoverflow.com/a/39588899
    // For Sprint 2 User Story 2.


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
             mListener = (OnFragmentInteractionListener) context;
        } else {
         //   throw new RuntimeException(context.toString()
         //           + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d("Lifecycles", "onViewCreated: View Created for Settings Fragment");
    }

    public void onClickCredits(View view) {
        Intent intent = new Intent(view.getContext(), CreditsActivity.class);
        startActivity(intent);
    }

    /**
     * DARK MODE SWITCH
     * switch toggle for dark mode
     *
     * @param buttonView view of switch
     * @param isChecked value
     *
     * @author Daniel
     */
    public void onCheckedChangedDarkMode(CompoundButton buttonView, boolean isChecked) {

        Log.d("DARK","night mode switch changed, current value: " + isChecked);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        preferences.edit().putString("fragStatic", "true").commit();

        NightModeUtils.setIsToogleEnabled(getContext(),isChecked);
        NightModeUtils.setIsNightModeEnabled(getContext(),isChecked);
        buttonView.setChecked(isChecked);
        mActivity = new WeakReference<Activity>(this.getActivity());
        mActivity.get().recreate();
    }

    /**
     * COTD TOGGLE
     * toggle button for Cotd
     *
     * @param buttonView view of switch
     * @param isChecked value
     *
     * @author Daniel
     */
    public void onCheckChangedCotd(CompoundButton buttonView, boolean isChecked){


        Log.d("COTD","COTD current value: " + isChecked);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        preferences.edit().putBoolean("dialogCotd", isChecked).commit();
//
//        NightModeUtils.setIsToogleEnabled(getContext(),isChecked);
//        NightModeUtils.setIsNightModeEnabled(getContext(),isChecked);

        buttonView.setChecked(isChecked);

    }

    /**
     * ACCENT RESET
     * reset button for custom accent pref
     *
     * @param view view of button
     *
     * @author Daniel
     */
    public void onClickReset(View view){
        // animation
        ImageButton reset =  (ImageButton) view;
        reset.startAnimation(rotate); // technically in there, but gets immediately cut off by the recreate
        // function
        AccentUtils.resetAccent(getContext());
        // this is such a hack to redraw
        SharedPreferences preferences = this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        preferences.edit().putString("fragStatic", "true").commit();
        mActivity = new WeakReference<Activity>(this.getActivity());
        mActivity.get().recreate();
    }

    /**
     * COLOR PICKER IMAGE RESET
     * resets the image shown on the color picker page to the original image (the livecolor logo)
     *
     * @param view is only needed to animate the button when clicked
     *
     * @author Paige
     */
    public void onClickResetImage(View view) {
        ImageButton reset = (ImageButton) view;
        reset.startAnimation(rotate);

        //clear shared pref of currently saved path
        SharedPreferences.Editor editor = getContext().getSharedPreferences("prefs", MODE_PRIVATE).edit();
        editor.putString("image", null);
        editor.apply();
    }

    /**
     * VIEW TODAYS COLOR
     * reopens the COTD dialog
     *
     * @author Paige
     */
    public void onClickTodaysColor() {
        Log.d("SettingsFragment", "onClickTodaysColor: we're in");
        ColorOTDayDialog cotdDialog = new ColorOTDayDialog(getContext(), true);
        cotdDialog.showColorOTD();
    }


    /**
     * AFTER TEXT CHANGED FOR CUSTOM ACCENT
     * decides what to do once the custom hex has been entered
     *
     * @param seq sequence entered into the editText view
     *
     * @author Daniel
     */
    public void afterTextChangedAccentHex(Editable seq){

        String accentColor = "";

        if (seq.toString().length() == 7 ){
            if(seq.toString().matches("^(#)\\p{XDigit}+$")){
                accentColor = seq.toString().toUpperCase();
            }else{
                accentColor = "";
            }
        }else{
            if (seq.toString().length() == 6 ){
                if(seq.toString().matches("^\\p{XDigit}+$")){
                    accentColor = "#"+seq.toString().toUpperCase();
                }else{
                    accentColor = "";
                }
            }else{
                accentColor = "";
            }
        }

        if (accentColor.length() > 0){

            AccentUtils.setAccent(getContext(), accentColor);
            seq.clear();
            // this is such a hack
            SharedPreferences preferences = this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
            preferences.edit().putString("fragStatic", "true").commit();
            mActivity = new WeakReference<Activity>(this.getActivity());
            mActivity.get().recreate();
        }else{

        }
    }


    /**
     * CUSTOM ACCENT HANDLER
     * changes colors of specific activity/fragment
     *
     *
     * @param view view of root container
     *
     * @author Daniel
     * takes a bit of elbow grease, and there maybe a better way to do this, but it works
     */
    public void customAccent(View view){
        Switch switchDM = view.findViewById(R.id.switchDarkMode);
        ToggleButton toggleButtonCotd = view.findViewById(R.id.toggleButtonCotd);
        EditText editTextAccent = view.findViewById(R.id.editTextAccentHex);

        int[][] states = new int[][] {

                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] {-android.R.attr.state_selected}, // unselected
                new int[] { android.R.attr.state_active}, // active
                new int[] { android.R.attr.state_pressed}, // pressed
                new int[] { android.R.attr.state_checked},  // checked
                new int[] { android.R.attr.state_selected}, // selected
                new int[] { android.R.attr.state_enabled} // enabled
        };

        int[] accent = new int[] {
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext()))
        };

        ColorStateList  accentList = new ColorStateList(states, accent);

      switchDM.setThumbTintList(accentList);
      switchDM.setBackgroundTintList(accentList);
      switchDM.setTrackTintList(accentList);
      editTextAccent.setTextColor(accentList);
      editTextAccent.setCompoundDrawableTintList(accentList);
      editTextAccent.setHintTextColor(accentList);
      editTextAccent.setForegroundTintList(accentList);
      editTextAccent.setBackgroundTintList(accentList);

    }

}

