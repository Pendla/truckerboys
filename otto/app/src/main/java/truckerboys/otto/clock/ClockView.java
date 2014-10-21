package truckerboys.otto.clock;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.Iterator;

import truckerboys.otto.R;
import truckerboys.otto.planner.TimeLeft;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.events.SetChosenStopEvent;
import truckerboys.otto.utils.positions.RouteLocation;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * <p/>
 * The viewclass for the clock that handles the UI.
 * It also runs the timer for the clock.
 */
public class ClockView extends Fragment {
    View rootView;

    ViewSwitcher viewSwitcher;

    TextView timeLeft, timeLeftExtended, recStopETA, firstAltStopETA, secAltStopETA, thirdAltStopETA,
            recStopName, firstAltStopName, secAltStopName, thirdAltStopName, recStopTitle;
    ImageView recStopImage, firstAltStopImage, secAltStopImage, thirdAltStopImage, backButton;
    RelativeLayout firstAltStopClick, secAltStopClick, thirdAltStopClick;
    LinearLayout alternativeStopsButton;

    RouteLocation recStop, firstAltStop, secAltStop, thirdAltStop, nextDestination;

    ArrayList<RouteLocation> altStops = new ArrayList<RouteLocation>();

    Boolean variablesSet = false;
    String timeL, timeLE, timeLEPrefix = "Extended time: ";

    public ClockView() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_clock, container, false);

        viewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.clockViewSwitcher);
        viewSwitcher.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right));
        viewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right));

        initiateVariables();

        return rootView;
    }

    /**
     * Initiates the UI components of the view.
     */
    private void initiateVariables() {
        timeLeft = (TextView) rootView.findViewById(R.id.clockETA);
        timeLeftExtended = (TextView) rootView.findViewById(R.id.clockETAExtended);

        recStopTitle = (TextView) rootView.findViewById(R.id.recStopTitle);

        recStopETA = (TextView) rootView.findViewById(R.id.recStopETA);
        firstAltStopETA = (TextView) rootView.findViewById(R.id.firstAltStopETA);
        secAltStopETA = (TextView) rootView.findViewById(R.id.secAltStopETA);
        thirdAltStopETA = (TextView) rootView.findViewById(R.id.thirdAltStopETA);

        recStopName = (TextView) rootView.findViewById(R.id.recStopName);
        firstAltStopName = (TextView) rootView.findViewById(R.id.firstAltStopName);
        secAltStopName = (TextView) rootView.findViewById(R.id.secAltStopName);
        thirdAltStopName = (TextView) rootView.findViewById(R.id.thirdAltStopName);

        recStopImage = (ImageView) rootView.findViewById(R.id.recStopImage);
        firstAltStopImage = (ImageView) rootView.findViewById(R.id.firstAltStopImage);
        secAltStopImage = (ImageView) rootView.findViewById(R.id.secAltStopImage);
        thirdAltStopImage = (ImageView) rootView.findViewById(R.id.thirdAltStopImage);

        firstAltStopClick = (RelativeLayout) rootView.findViewById(R.id.firstAltStop);
        secAltStopClick = (RelativeLayout) rootView.findViewById(R.id.secAltStop);
        thirdAltStopClick = (RelativeLayout) rootView.findViewById(R.id.thirdAltStop);

        alternativeStopsButton = (LinearLayout) rootView.findViewById(R.id.alternativesButton);
        backButton = (ImageView) rootView.findViewById(R.id.backButton);

        View.OnClickListener stopClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = view.getTag().toString();
                if (tag.equalsIgnoreCase("firstAltStop")) {
                    EventTruck.getInstance().newEvent(new SetChosenStopEvent(firstAltStop));
                }
                if (tag.equalsIgnoreCase("secAltStop")) {
                    EventTruck.getInstance().newEvent(new SetChosenStopEvent(secAltStop));
                }
                if(tag.equalsIgnoreCase("thirdAltStop")){
                    EventTruck.getInstance().newEvent(new SetChosenStopEvent(thirdAltStop));
                }
            }
        };

        firstAltStopClick.setOnClickListener(stopClickListener);
        secAltStopClick.setOnClickListener(stopClickListener);
        thirdAltStopClick.setOnClickListener(stopClickListener);

        Log.w("Null", "recStopETA is null? " + (recStopETA == null));
        alternativeStopsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewSwitcher.showNext();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewSwitcher.showPrevious();
            }
        });

        alternativeStopsButton.setVisibility(View.GONE);

        setStopUI(recStop, recStopETA, recStopName, recStopImage);
        setStopUI(firstAltStop, firstAltStopETA, firstAltStopName, firstAltStopImage);
        setStopUI(secAltStop, secAltStopETA, secAltStopName, secAltStopImage);
        setStopUI(thirdAltStop, thirdAltStopETA, thirdAltStopName, thirdAltStopImage);

        variablesSet = true;
    }

    /**
     * Set the time remaining until regulations are broken.
     *
     * @param timeLeft The remaining time
     */
    public void setTimeLeft(TimeLeft timeLeft) {
        if (variablesSet) {
            timeL = getTimeAsFormattedString(timeLeft.getTimeLeft());
            if (timeLeft.getExtendedTimeLeft().getMillis() > 0) {
                timeLE = timeLEPrefix + getTimeAsFormattedString(timeLeft.getExtendedTimeLeft());
            } else {
                timeLE = "0";
            }
            if(timeLeft.getTimeLeft().toStandardMinutes().isLessThan(Minutes.minutes(0))){
                this.timeLeft.setTextColor(getResources().getColor(R.color.violationred_color));
            }else if(timeLeft.getTimeLeft().toStandardMinutes().isLessThan(Minutes.minutes(15))){
                this.timeLeft.setTextColor(getResources().getColor(R.color.warningyellow_color));
            }
        }
    }

    /**
     * Sets the recommended stop
     *
     * @param stop The new recommended stop
     */
    public void setRecommendedStop(RouteLocation stop) {
        recStop = stop;
    }

    /**
     * Sets the alternative stops to the given stops
     *
     * @param altStops The list of the alternative stops
     */
    public void setAltStops(ArrayList<RouteLocation> altStops) {
        this.altStops = altStops;
        if(altStops!=null){
            Iterator it = altStops.iterator();
            if(it.hasNext()) {
                firstAltStop = (RouteLocation)it.next();
            }
            if(it.hasNext()) {
                secAltStop = (RouteLocation)it.next();
            }if(it.hasNext()) {
                thirdAltStop = (RouteLocation)it.next();
            }
        }
    }

    /**
     * Sets the next destination of the route
     * @param nextDestination The next destination
     */
    public void setNextDestination(RouteLocation nextDestination){
        this.nextDestination = nextDestination;
    }

    /**
     * Displays a message on the screen
     * @param message The message to be displayed
     */
    public void displayToast(String message){
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the UI
     */
    public void updateUI() {

        if (variablesSet) {
            Runnable updateUI = new Runnable() {
                public void run() {
                    setLabels();
                }
            };
            if(getActivity()!=null)
            getActivity().runOnUiThread(updateUI);
        }
    }

    /**
     * Sets the labels of the time until violation and the stops.
     */
    private void setLabels() {
        timeLeft.setText(timeL);
        if (timeLE.equalsIgnoreCase("0")) {
            timeLeftExtended.setVisibility(TextView.GONE);
        } else {
            timeLeftExtended.setText(timeLE);
            timeLeftExtended.setVisibility(TextView.VISIBLE);
        }
        ViewGroup v = (ViewGroup)recStopETA.getParent();
        if (nextDestination==null){
            v.setVisibility(ViewGroup.GONE);
            alternativeStopsButton.setVisibility(View.GONE);
        }else if(recStop == null){
            recStopETA.setText(getTimeAsFormattedString(nextDestination.getEta()));
            recStopName.setText(nextDestination.getAddress());
            recStopImage.setVisibility(TextView.GONE);
            v.setVisibility(ViewGroup.VISIBLE);
            alternativeStopsButton.setVisibility(View.VISIBLE);

        }else{
            setStopUI(recStop, recStopETA, recStopName, recStopImage);
        }

        setStopUI(firstAltStop, firstAltStopETA, firstAltStopName, firstAltStopImage);
        setStopUI(secAltStop, secAltStopETA, secAltStopName, secAltStopImage);
        setStopUI(thirdAltStop, thirdAltStopETA, thirdAltStopName, thirdAltStopImage);

        if(firstAltStop == null && secAltStop == null && thirdAltStop == null){
            alternativeStopsButton.setVisibility(View.GONE);
        }else{
            alternativeStopsButton.setVisibility(View.VISIBLE);
        }

    }

    /**
     * A help method to set the UI of the stops.
     *
     * @param stop  The stop to read from
     * @param eta   The TextView representing the ETA
     * @param name  The TextView representing the name
     * @param image The ImageView with the image
     */
    private void setStopUI(RouteLocation stop, TextView eta, TextView name, ImageView image) {
        ViewGroup v = (ViewGroup)eta.getParent();
        if (stop == null) {
            v.setVisibility(ViewGroup.GONE);
            return;
        }
        v.setVisibility(ViewGroup.VISIBLE);
        Duration time = new Duration(new Instant(), stop.getTimeOfArrival());
        if(time.getMillis()<0){
            time = Duration.ZERO;
        }
        eta.setText(getTimeAsFormattedString(time));
        if (stop.getType().contains("gas_station")) {
            name.setText(stop.getName());
            image.setImageResource(R.drawable.gasstation);
        } else if (stop.getType().isEmpty()) {
            name.setText(stop.getAddress());
            image.setImageResource(R.drawable.reststop);
        } else {
            name.setText(stop.getName());
            image.setImageResource(R.drawable.reststop);
        }
    }

    /**
     * Takes a duration and returns a formatted string as such "HH:MM"
     *
     * @param time The duration to format.
     * @return A formatted string in the form of "HH:MM"
     */
    private String getTimeAsFormattedString(Duration time) {

        Period period = time.toPeriod();
        PeriodFormatter minutesAndSeconds = new PeriodFormatterBuilder()
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendHours()
                .appendSeparator(":")
                .appendMinutes()
                .toFormatter();
        return minutesAndSeconds.print(period);
    }

}
