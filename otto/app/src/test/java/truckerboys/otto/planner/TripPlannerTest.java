package truckerboys.otto.planner;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.TestCase;

import org.joda.time.Duration;

import truckerboys.otto.directionsAPI.GoogleDirections;
import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.driver.User;
import truckerboys.otto.placesAPI.GooglePlaces;
import truckerboys.otto.utils.positions.MapLocation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class TripPlannerTest extends TestCase {
    //Bergvagen 11 Molnlycke
    private MapLocation currentLocation = new MapLocation(new LatLng(57.6535522, 12.1244496));

    private final MapLocation malmo = new MapLocation(new LatLng(55.5708457, 13.0180405));
    private final MapLocation kiruna = new MapLocation(new LatLng(67.853702, 20.2564299));
    private final MapLocation stockholm = new MapLocation(new LatLng(59.3261419, 17.9875456));

    private User user = User.getInstance();
    private TripPlanner tripPlanner;

    private Route activeRoute;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        tripPlanner = new TripPlanner(new EURegulationHandler(), new GoogleDirections(), new GooglePlaces(), user);

    }

    @Test
    public void testUpdateRoute() throws Exception {
        fail("Test not implemented");
    }

    @Test
    public void testGestRoute() throws Exception {
        fail("Test not implemented");
    }

    @Test
    public void testSetChoosenStop() throws Exception {
        fail("Test not implemented");
    }

    @Test
    public void testSetNewRoute() throws Exception {
        tripPlanner.setNewRoute(currentLocation, kiruna, malmo,stockholm);
        activeRoute = tripPlanner.getRoute();

        //First checkpoint should not have an ETA over 4,5 hours
        assertFalse(activeRoute.getEtaToFirstCheckpoint().isLongerThan(Duration.standardMinutes(270)));

        //These are out of reach within one session
        assertFalse(activeRoute.getRecommendedStop().equalCoordinates(kiruna));
        assertFalse(activeRoute.getRecommendedStop().equalCoordinates(stockholm));

        assertFalse(activeRoute.getEta().isEqual(activeRoute.getEtaToFirstCheckpoint()));

        //Malmo is within reach
        tripPlanner.setNewRoute(currentLocation, malmo);
        activeRoute=tripPlanner.getRoute();

        assertTrue(activeRoute.getRecommendedStop().equalCoordinates(malmo));
        assertTrue(activeRoute.getEta().isEqual(activeRoute.getEtaToFirstCheckpoint()));
    }

    @Test
    public void testPassedCheckpoint() throws Exception{
        fail("Test not implemented");
    }
}