package gov.nist.toolkit.xdstools2.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * SimResource thrown when there is a change in the list of favorite PIDs (creation, deletion, update).
 * @see FavoritePidsUpdatedEventHandler
 * Created by onh2 on 9/27/16.
 */
public class FavoritePidsUpdatedEvent extends GwtEvent<FavoritePidsUpdatedEvent.FavoritePidsUpdatedEventHandler> {
    public static final Type<FavoritePidsUpdatedEventHandler> TYPE = new Type<>();
    @Override
    public Type<FavoritePidsUpdatedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FavoritePidsUpdatedEventHandler handler) {
        handler.onFavPidsUpdate();
    }

    /**
     * SimResource handler interface to use when there is an update in the list of favorite PIDs.
     * @see FavoritePidsUpdatedEvent
     */
    public interface FavoritePidsUpdatedEventHandler extends EventHandler {
        /**
         * Actions to be executed on the class that catches the event when there is
         * an update in the list of favorite PIDs (creation, deletion, modification).
         */
        void onFavPidsUpdate();
    }
}
