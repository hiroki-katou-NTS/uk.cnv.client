package nts.uk.ctx.pr.core.ws.wageprovision.formula;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.pr.core.app.command.wageprovision.formula.*;
import nts.uk.ctx.pr.core.app.find.wageprovision.formula.*;
import nts.uk.ctx.pr.core.dom.wageprovision.formula.FormulaElementDto;
import nts.uk.ctx.pr.core.dom.wageprovision.formula.MasterUseDto;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Path("ctx/pr/core/wageprovision/formula")
@Produces("application/json")
public class FormulaWebservice extends WebService {

    @Inject
    private FormulaFinder formulaFinder;

    @Inject
    private BasicCalculationFormulaFinder basicCalculationFormulaFinder;

    @Inject
    private BasicFormulaSettingFinder basicFormulaSettingFinder;

    @Inject
    private DetailFormulaSettingFinder detailFormulaSettingFinder;

    @Inject
    private AddFormulaCommandHandler addFormulaCommandHandler;

    @Inject
    private UpdateFormulaSettingCommandHandler updateFormulaSettingCommandHandler;

    @Inject
    private AddFormulaHistoryCommandHandler addFormulaHistoryCommandHandler;

    @Inject
    private UpdateFormulaHistoryCommandHandler updateFormulaHistoryCommandHandler;

    @Inject
    private RemoveFormulaHistoryCommandHandler removeFormulaHistoryCommandHandler;

    @POST
    @Path("getAllFormula")
    public List<FormulaDto> getAllFormula() {
        return formulaFinder.getAllFormulaAndHistory();
    }

    @POST
    @Path("getFormulaSettingByHistoryID")
    public FormulaSettingDto getFormulaSettingByHistoryID(FormulaSearchDto setting) {
        List<MasterUseDto> masterUseList = Collections.emptyList();
        if (!setting.withSetting){
            masterUseList = formulaFinder.getMasterUseInfo(setting.masterUse);
            return new FormulaSettingDto(null, null, null, masterUseList);
        }
        BasicFormulaSettingDto basicFormulaSettingDto = basicFormulaSettingFinder.getBasicFormulaSettingByHistoryID(setting.historyID);
        masterUseList = basicFormulaSettingDto == null ? Collections.emptyList() : formulaFinder.getMasterUseInfo(basicFormulaSettingDto.getMasterUse());
        return new FormulaSettingDto(basicFormulaSettingDto, detailFormulaSettingFinder.getDetailFormulaSettingByHistoryID(setting.historyID), basicCalculationFormulaFinder.getBasicCalculationFormulaByHistoryID(setting.historyID), masterUseList);
    }

    @POST
    @Path("addFormula")
    public void addFormula(FormulaCommand command) {
        command.updateHistoryIdentifier();
        addFormulaCommandHandler.handle(command);
    }

    @POST
    @Path("updateFormulaSetting")
    public void updateFormulaSetting(FormulaCommand command) {
        command.updateHistoryIdentifier();
        updateFormulaSettingCommandHandler.handle(command);
    }

    @POST
    @Path("addFormulaHistory")
    public void addHistory(FormulaCommand command) {
        command.updateHistoryIdentifier();
        addFormulaHistoryCommandHandler.handle(command);
    }

    @POST
    @Path("editFormulaHistory")
    public void editFormulaHistory(FormulaCommand command) {
        updateFormulaHistoryCommandHandler.handle(command);
    }

    @POST
    @Path("deleteFormulaHistory")
    public void deleteFormulaHistory(FormulaCommand command) {
        removeFormulaHistoryCommandHandler.handle(command);
    }

    @POST
    @Path("getMasterUseInfo/{masterUseClassification}")
    public List<MasterUseDto> getMasterUseInfo(@PathParam("masterUseClassification")int masterUseClassification) {
        return formulaFinder.getMasterUseInfo(masterUseClassification);
    }

    @POST
    @Path("getFormulaElement/{yearMonth}")
    public Map<String, List<FormulaElementDto>> getFormulaElement(@PathParam("yearMonth") int yearMonth) {
        return formulaFinder.getFormulaElement(yearMonth);
    }
}
