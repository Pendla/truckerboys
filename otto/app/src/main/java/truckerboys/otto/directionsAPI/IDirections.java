package truckerboys.otto.directionsAPI;

import org.joda.time.Duration;

import java.util.List;

import truckerboys.otto.utils.exceptions.InvalidRequestException;
import truckerboys.otto.utils.exceptions.NoConnectionException;
import truckerboys.otto.utils.positions.MapLocation;

/**
 * Interface to make project work with multiple mapAPIs
 */
public interface IDirections {
    /**
     * Creates a new route
     *
     * @param currentPosition position of the device
     * @param finalDestination the location that will end the route
     * @param preferences      route requirements
     * @param checkpoint       locations that the route needs to go to before the final destination
     * @return a new route
     */
    public Route getRoute(MapLocation currentPosition, MapLocation finalDestination, RoutePreferences preferences, List<MapLocation> checkpoint) throws NoConnectionException, InvalidRequestException;

    /**
     * Creates a new route
     *
     * @param currentPosition position of the device
     * @param finalDestination the location that will end the route
     * @param preferences      route requirements
     * @return a new route
     */
    public Route getRoute(MapLocation currentPosition, MapLocation finalDestination, RoutePreferences preferences) throws NoConnectionException, InvalidRequestException ;

    /**
     * Creates a new route
     *
     * @param currentPosition position of the device
     * @param finalDestination the location that will end the route
     * @param checkpoint       locations that the route needs to go to before the final destination
     * @return a new route
     */
    public Route getRoute(MapLocation currentPosition, MapLocation finalDestination, List<MapLocation> checkpoint) throws NoConnectionException, InvalidRequestException ;

    /**
     * Creates a new route
     *
     * @param currentPosition position of the device
     * @param finalDestination the location that will end the route
     * @return a new route
     */
    public Route getRoute(MapLocation currentPosition, MapLocation finalDestination) throws NoConnectionException, InvalidRequestException ;

    /**
     * Get Estimated Time of Arrival to specified location without checkpoints
     *
     * @param currentPosition current position of the device
     * @param finalDestination that Estimated Time of Arrival is needed upon
     * @return Estimated Time of Arrival to target location without checkpoints
     */
    public Duration getETA(MapLocation currentPosition, MapLocation finalDestination) throws NoConnectionException, InvalidRequestException ;
}