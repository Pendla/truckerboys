package truckerboys.otto.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Switch;

import truckerboys.otto.R;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.SettingsChangedEvent;
import truckerboys.otto.utils.eventhandler.events.SoundChangedEvent;
import utils.IView;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * This view knows about its the presenter, which
 * causes a cyclic dependency. This is typically
 * a bad solution, but as for now it'll have to do,
 * since android is build in a horrific way that mostly
 * resemble a scene from the classic flick Nightmare on
 * Elm street more than proper java coding.
 */

public class SettingsView extends Fragment implements IView, IEventListener {
    private View rootView;
    private Switch soundSwitch;
    private Switch displaySwitch;
    private SettingsPresenter presenter;

    public static final String SETTINGS = "Settings_file";

    public SettingsView(){ }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        EventTruck.getInstance().subscribe(this);

        presenter = new SettingsPresenter(getActivity().getSharedPreferences(SETTINGS, 0));

        // Creates switches from the fragment
        soundSwitch = (Switch) rootView.findViewById(R.id.soundSwitch);
        displaySwitch = (Switch) rootView.findViewById(R.id.displaySwitch);

        // Sets listeners in presenter
        // TODO Remove unit switch in final design
        presenter.setListeners(soundSwitch, displaySwitch, (Switch) rootView.findViewById(R.id.unitSwitch));


        // Restores preferences for settings in presenter
        presenter.restorePreferences();

        int ringerMode = ((AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE)).getRingerMode();

        // If ringerMode is set on Normal (1) set sound as true
        boolean sound = (ringerMode == AudioManager.RINGER_MODE_NORMAL);


        // Sets switch as checked if sound is on
        if(soundSwitch != null) {

            soundSwitch.setChecked(sound);
        }

        update(sound, presenter.isDisplayActive());
        return rootView;

    }


    /**
     * Update the switches with settings loaded from shared preference file
     * @param sound on/off
     * @param displayAlive on/off
     */
    public void update(boolean sound, boolean displayAlive) {
        soundSwitch.setChecked(sound);
        displaySwitch.setChecked(displayAlive);

        performEvent(new SoundChangedEvent(sound));
    }

    @Override
    public Fragment getFragment(){
        return this;
    }

    @Override
    public String getName() {
        return "Settings";
    }

    @Override
    public void performEvent(Event event) {

        if(event.isType(SoundChangedEvent.class)) {

            // Checks if sound is on or off and sets systems sound based on this
            if (((SoundChangedEvent)event).getSound()) {
                ((AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE)).setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            } else {
                ((AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE)).setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
        }

        if(event.isType(SettingsChangedEvent.class)) {

            // Loads settings file
            SharedPreferences settings = getActivity().getSharedPreferences(SETTINGS, 0);

            // Keeps display alive or not based on settings
            if(settings.getBoolean("displayAlive", true)) {
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            } else {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            }

        }
    }
}