package truckerboys.otto.clock;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;

import truckerboys.otto.driver.User;
import truckerboys.otto.planner.IRegulationHandler;
import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.ChangedRouteEvent;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.IView;
import truckerboys.otto.utils.eventhandler.events.SetChosenStopEvent;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * The presenter class of the clock that acts as a bridge between the view and model.
 */
public class ClockPresenter  implements IView, IEventListener {
    private ClockModel model;
    private ClockView view;
    private Handler updateHandler;
    private Runnable update;

    public ClockPresenter(TripPlanner tripPlanner, IRegulationHandler regulationHandler, User user){
        model = new ClockModel(tripPlanner, regulationHandler, user);
        view = new ClockView();

        EventTruck.getInstance().subscribe(this);

        updateHandler = new Handler(Looper.getMainLooper());
        update = new Runnable(){
            public void run(){
                update();
            }
        };
        updateHandler.postDelayed(update, 1000);

    }
    /**
     * Updates the model and view.
     */
    public void update() {
        if(model.getRoute()!=null) {
            model.update();
            view.setTimeLeft(model.getTimeLeft());
            view.updateUI();
        }
        updateHandler.postDelayed(update, 5000);
    }

    @Override
    public Fragment getFragment() {
        return view;
    }

    @Override
    public String getName() {
        return "Clock";
    }

    @Override
    public void performEvent(Event event) {
        if(event.isType(ChangedRouteEvent.class)){
            model.processChangedRoute();
            view.setRecommendedStop(model.getRecommendedStop());
            view.setAltStops(model.getAltStops());
            view.setNextDestination(model.getNextDestination());
        }

        if(event.isType(SetChosenStopEvent.class)){
            if(model.setChosenStop(((SetChosenStopEvent)event).getStop())){
                //No exceptions, the chosen stop was set
            }else{
                view.displayToast("No connection available");
            }
        }
    }
}
