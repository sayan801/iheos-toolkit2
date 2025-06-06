package gov.nist.toolkit.xdstools2.client.widgets.buttons;

import com.google.gwt.user.client.ui.Image;
import gov.nist.toolkit.xdstools2.client.util.ClientFactoryImpl;

/**
 * Created by Diane Azais local on 11/29/2015.
 */
public class RefreshButton extends IconButton {
    private Image REFRESH_ICON = new Image(ClientFactoryImpl.getIconsResources().getRefreshIconBlack36());


    RefreshButton(String _tooltip){
       super(ButtonType.REFRESH_BUTTON, _tooltip);
        setIcon();
    }

    @Override
    protected void setIcon() {
        getElement().appendChild(REFRESH_ICON.getElement());
    }

}
