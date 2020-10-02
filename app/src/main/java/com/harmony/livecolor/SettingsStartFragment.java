package com.harmony.livecolor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class SettingsStartFragment extends  Fragment{

    private SettingsStartFragment.OnFragmentInteractionListener mListener;

    ConstraintLayout constraintLayoutGeneral;
    ConstraintLayout constraintLayoutAppearance;
    ConstraintLayout constraintLayoutCOTD;
    ConstraintLayout constraintLayoutCredit;
//    ImageButton imageButtonReset;
//    RotateAnimation rotate;
    private WeakReference<Activity> mActivity;

    public SettingsStartFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SettingsStartFragment newInstance() {
        SettingsStartFragment fragment = new SettingsStartFragment();
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
        final View rootView = inflater.inflate(R.layout.fragment_settings_start, container, false);

        // handles customized accent
//        customAccent(rootView.findViewById(R.id.constraintLayoutSettings));

        constraintLayoutGeneral = rootView.findViewById(R.id.constraintLayoutGeneralOption);
        constraintLayoutAppearance = rootView.findViewById(R.id.constraintLayoutAppearanceOption);
        constraintLayoutCOTD = rootView.findViewById(R.id.constraintLayoutCOTDOption);
        constraintLayoutCredit = rootView.findViewById(R.id.constraintLayoutCreditsOption);

       constraintLayoutGeneral.setOnClickListener(new View.OnClickListener() {
           @Override
           public  void onClick(View view){
               onClickGeneralOption(view);
           }
       });
       constraintLayoutAppearance.setOnClickListener(new View.OnClickListener() {
           @Override
           public  void onClick(View view){
               onClickAppearanceOption(view);
           }
       });
       constraintLayoutCOTD.setOnClickListener(new View.OnClickListener() {
           @Override
           public  void onClick(View view){
               onClickCOTDOption(view);
           }
       });
       constraintLayoutCredit.setOnClickListener(new View.OnClickListener() {
           @Override
           public  void onClick(View view){
               onClickCreditsOption(view);
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

    public void onClickGeneralOption(View view) {
        SettingsGeneralFragment nextFrag= new SettingsGeneralFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace( ((ViewGroup)getView().getParent()).getId(), nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }

    public void onClickAppearanceOption(View view) {
        SettingsAppearanceFragment nextFrag= new SettingsAppearanceFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace( ((ViewGroup)getView().getParent()).getId(), nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }

    public void onClickCOTDOption(View view) {
        SettingsCOTDFragment nextFrag= new SettingsCOTDFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace( ((ViewGroup)getView().getParent()).getId(), nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }

    public void onClickCreditsOption(View view) {
        Intent intent = new Intent(view.getContext(), CreditsActivity.class);
        startActivity(intent);
    }




//    /**
//     * CUSTOM ACCENT HANDLER
//     * changes colors of specific activity/fragment
//     *
//     *
//     * @param view view of root container
//     *
//     * @author Daniel
//     * takes a bit of elbow grease, and there maybe a better way to do this, but it works
//     */
//    public void customAccent(View view){
//        Switch switchDM = view.findViewById(R.id.switchDarkMode);
//        ToggleButton toggleButtonCotd = view.findViewById(R.id.toggleButtonCotd);
//        EditText editTextAccent = view.findViewById(R.id.editTextAccentHex);
//
//        int[][] states = new int[][] {
//
//                new int[] {-android.R.attr.state_enabled}, // disabled
//                new int[] {-android.R.attr.state_checked}, // unchecked
//                new int[] {-android.R.attr.state_selected}, // unselected
//                new int[] { android.R.attr.state_active}, // active
//                new int[] { android.R.attr.state_pressed}, // pressed
//                new int[] { android.R.attr.state_checked},  // checked
//                new int[] { android.R.attr.state_selected}, // selected
//                new int[] { android.R.attr.state_enabled} // enabled
//        };
//
//        int[] accent = new int[] {
//                Color.parseColor(AccentUtils.getAccent(view.getContext())),
//                Color.parseColor(AccentUtils.getAccent(view.getContext())),
//                Color.parseColor(AccentUtils.getAccent(view.getContext())),
//                Color.parseColor(AccentUtils.getAccent(view.getContext())),
//                Color.parseColor(AccentUtils.getAccent(view.getContext())),
//                Color.parseColor(AccentUtils.getAccent(view.getContext())),
//                Color.parseColor(AccentUtils.getAccent(view.getContext())),
//                Color.parseColor(AccentUtils.getAccent(view.getContext()))
//        };
//
//        ColorStateList  accentList = new ColorStateList(states, accent);
//
//      switchDM.setThumbTintList(accentList);
//      switchDM.setBackgroundTintList(accentList);
//      switchDM.setTrackTintList(accentList);
//      editTextAccent.setTextColor(accentList);
//      editTextAccent.setCompoundDrawableTintList(accentList);
//      editTextAccent.setHintTextColor(accentList);
//      editTextAccent.setForegroundTintList(accentList);
//      editTextAccent.setBackgroundTintList(accentList);
//
//    }

}

