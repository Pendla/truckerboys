package truckerboys.otto.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import truckerboys.otto.R;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class HomeView extends Fragment {
    private View rootView;
    private HomePresenter presenter;
    private ImageButton newRouteButton;
    private ImageButton continueRouteButton;
    private ImageButton mapsButton;
    private ImageButton clockButton;
    private ImageButton statsButton;
    private ImageButton settingsButton;

    public HomeView(){
        presenter = new HomePresenter(this, new HomeModel());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Defines the buttons to be clicked on at the home screen
        defineButtons();

        return rootView;
    }

    public void defineButtons() {
        newRouteButton = (ImageButton) rootView.findViewById(R.id.newRouteButton);
        continueRouteButton = (ImageButton) rootView.findViewById(R.id.continueRouteButton);
        mapsButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
        clockButton = (ImageButton) rootView.findViewById(R.id.clockButton);
        statsButton = (ImageButton) rootView.findViewById(R.id.statsButton);
        settingsButton = (ImageButton) rootView.findViewById(R.id.settingsButton);

        asignListeners();
    }

    /**
     * Asigns listeners to the buttons and passes the actions to the presenter.
     */
    public void asignListeners() {

        newRouteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                presenter.newRouteButtonClicked(v);
            }
        });

        continueRouteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                presenter.contiueRouteButtonClicked(v);
            }
        });

        mapsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                presenter.mapButtonClicked(v);
            }
        });

        clockButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                presenter.clockButtonClicked(v);
            }
        });

        statsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                presenter.statsButtonClicked(v);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                presenter.settingsButtonClicked(v);
            }
        });

    }


}