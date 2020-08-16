package nts.uk.screen.at.ws.kmr.kmr004.a;

import nts.arc.layer.ws.WebService;
import nts.uk.screen.at.app.reservation.ReservationConfirmationListDto;
import nts.uk.screen.at.app.reservation.ReservationConfirmationListScreenQuery;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.LoginUserContext;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("screen/at/record/reservation-conf-list")
@Produces("application/json")
public class ReservationConfirmationListWebService extends WebService {

   @Inject
    private ReservationConfirmationListScreenQuery reservationConfirmationListScreenQuery;

    @POST
    @Path("start")
    public ReservationConfirmationListDto getOperationDistinction() {
        LoginUserContext user = AppContexts.user();

        // 予約確認一覧
        return reservationConfirmationListScreenQuery.getReservationConfirmationListStartupInfo(user.companyId());
    }
}