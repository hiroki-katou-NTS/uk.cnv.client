package nts.uk.ctx.at.schedule.ws.budget.premium;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.schedule.app.command.budget.premium.DeletePersonCostCalculationCommandHandler;
import nts.uk.ctx.at.schedule.app.command.budget.premium.RegisterLaborCalculationSettingCommandHandler;
import nts.uk.ctx.at.schedule.app.command.budget.premium.UpdateHistPersonCostCalculationCommandHandler;
import nts.uk.ctx.at.schedule.app.command.budget.premium.UpdatePremiumItemCommandHandler;
import nts.uk.ctx.at.schedule.app.command.budget.premium.command.*;
import nts.uk.ctx.at.schedule.app.find.budget.premium.PersonCostCalculationFinder;
import nts.uk.ctx.at.schedule.app.find.budget.premium.dto.HistAndPersonCostLastDto;
import nts.uk.ctx.at.schedule.app.find.budget.premium.dto.PersonCostCalDto;
import nts.uk.ctx.at.schedule.app.find.budget.premium.dto.PersonCostCalculationSettingDto;
import nts.uk.ctx.at.schedule.app.find.budget.premium.dto.PremiumItemDto;
import nts.uk.ctx.at.schedule.ws.budget.premium.language.HistoryDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.service.AttendanceNamePriniumDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.service.AttendanceTypePriServiceDto;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

/**
 * @author Doan Duy Hung
 */

@Path("at/schedule/budget/premium")
@Produces("application/json")
public class PersonCostCalculationWebService extends WebService {


    @Inject
    private PersonCostCalculationFinder personCostCalculationSettingFinder;


    @Inject
    private DeletePersonCostCalculationCommandHandler deletePersonCostCalculationSettingCommandHandler;

    @Inject
    private UpdatePremiumItemCommandHandler updatePremiumItemCommandHandler;


    @Inject
    private UpdateHistPersonCostCalculationCommandHandler updateHistCommandHandler;

    @Inject
    private RegisterLaborCalculationSettingCommandHandler registerLaborCalSettingCommandHandler;


    @POST
    @Path("findByHistoryID") // 1
    public PersonCostCalculationSettingDto findByHistoryID(HistoryDto historyID) {
        return this.personCostCalculationSettingFinder.findByHistoryID(historyID.getHistoryID());
    }

    @POST
    @Path("findPremiumItemByCompanyID") // 2
    public List<PremiumItemDto> findPremiumItemByCompanyID() {
        return this.personCostCalculationSettingFinder.findPremiumItemByCompanyID();
    }

    @POST
    @Path("updatePremiumItem")// 3
    public void update(List<UpdatePremiumItemCommand> command) {
        this.updatePremiumItemCommandHandler.handle(command);
    }

    @POST
    @Path("attendancePremiumItem")// 3
    public List<AttendanceTypePriServiceDto> findAttendanceType() {
        //人数：0
        return this.personCostCalculationSettingFinder.atTypes(0);
    }

    @POST
    @Path("attendancePremiumName") // 4
    public List<AttendanceNamePriniumDto> findAttendanceName(List<Integer> dailyAttendanceItemIds) {
        return this.personCostCalculationSettingFinder.atNames(dailyAttendanceItemIds);
    }

    @POST
    @Path("getByCIdAndLangId/{langId}") // 5
    public List<PremiumItemDto> findWorkTypeLanguage(@PathParam("langId") String langId) {
        return this.personCostCalculationSettingFinder.findWorkTypeLanguage(langId);
    }

    @POST
    @Path("findPersonCostCalculationByCompanyID")// 6
    public HistAndPersonCostLastDto getHistPersonCostCalculations() {
        return this.personCostCalculationSettingFinder.getHistPersonCost();
    }

    @POST
    @Path("findByHistory")// 7
    public PersonCostCalculationDto getHistPersonCost(PersonCostDto prams) {
        return this.personCostCalculationSettingFinder.getHistPersonCostByHistId(prams.getHistoryID());
    }

    @POST
    @Path("updatePersonCostCalculation") // 8
    public void updateHistPersonCalculation(UpdateHistPersonCostCalculationCommand command) {
        this.updateHistCommandHandler.handle(command);
    }

    @POST
    @Path("insertPersonCostCalculation") // 9
    public void registerLaborCalculationSetting(RegisterLaborCalculationSettingCommand command) {
        this.registerLaborCalSettingCommandHandler.handle(command);
    }

    @POST
    @Path("deletePersonCostCalculation") // 10
    public void deleteLaborCalculationSetting(DeleteLaborCalculationSettingCommand command) {
        this.deletePersonCostCalculationSettingCommandHandler.handle(command);
    }

    @POST
    @Path("getDatafull") // 10
    public List<PersonCostCalDto> findPersonCostCal() {
       return this.personCostCalculationSettingFinder.findPersonCostCal();
    }
}
