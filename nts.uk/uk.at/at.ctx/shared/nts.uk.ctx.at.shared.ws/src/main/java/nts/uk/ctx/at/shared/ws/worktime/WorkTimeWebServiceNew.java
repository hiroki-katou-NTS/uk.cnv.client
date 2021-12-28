/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.ws.worktime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import lombok.val;
import nts.arc.layer.ws.WebService;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.app.command.worktime.common.WorkTimeCommandFinder;
import nts.uk.ctx.at.shared.app.command.worktime.common.WorkTimeCommonDeleteCommand;
import nts.uk.ctx.at.shared.app.command.worktime.common.WorkTimeCommonDeleteCommandHandler;
import nts.uk.ctx.at.shared.app.command.worktime.difftimeset.DiffTimeWorkSettingSaveCommand;
import nts.uk.ctx.at.shared.app.command.worktime.difftimeset.DiffTimeWorkSettingSaveCommandHandler;
import nts.uk.ctx.at.shared.app.command.worktime.fixedset.FixedWorkSettingSaveCommand;
import nts.uk.ctx.at.shared.app.command.worktime.fixedset.FixedWorkSettingSaveCommandHandler;
import nts.uk.ctx.at.shared.app.command.worktime.flexset.FlexWorkSettingSaveCommand;
import nts.uk.ctx.at.shared.app.command.worktime.flexset.FlexWorkSettingSaveCommandHandler;
import nts.uk.ctx.at.shared.app.command.worktime.flowset.FlowWorkSettingSaveCommand;
import nts.uk.ctx.at.shared.app.command.worktime.flowset.FlowWorkSettingSaveCommandHandler;
import nts.uk.ctx.at.shared.app.find.breaktime.dto.BreakTimeDayDto;
import nts.uk.ctx.at.shared.app.find.worktime.WorkTimeSettingInfoFinder;
import nts.uk.ctx.at.shared.app.find.worktime.dto.WorkTimeDto;
import nts.uk.ctx.at.shared.app.find.worktime.dto.WorkTimeLanguageDto;
import nts.uk.ctx.at.shared.app.find.worktime.dto.WorkTimeSettingInfoDto;
import nts.uk.ctx.at.shared.app.find.worktime.worktimeset.WorkTimeSettingFinder;
import nts.uk.ctx.at.shared.app.find.worktime.worktimeset.dto.SimpleWorkTimeSettingDto;
import nts.uk.ctx.at.shared.app.find.worktime.worktimeset.dto.WorkTimeResultDto;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingCondition;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

/**
 * The Class WorkTimeWebServiceNew.
 */

// Create by THANHNC
@Path("at/shared/worktimesetting")
@Produces("application/json")
public class WorkTimeWebServiceNew extends WebService {

    /**
     * The work time set finder.
     */
    @Inject
    private WorkTimeSettingFinder workTimeSetFinder;

    /**
     * The fixed handler.
     */
    @Inject
    private FixedWorkSettingSaveCommandHandler fixedHandler;

    /**
     * The diff time handler.
     */
    @Inject
    private DiffTimeWorkSettingSaveCommandHandler diffTimeHandler;

    /**
     * The flow handler.
     */
    @Inject
    private FlowWorkSettingSaveCommandHandler flowHandler;

    /**
     * The flex handler.
     */
    @Inject
    private FlexWorkSettingSaveCommandHandler flexHandler;

    /**
     * The work time setting info finder.
     */
    @Inject
    private WorkTimeSettingInfoFinder workTimeSettingInfoFinder;

    /**
     * The work time common delete command handler.
     */
    @Inject
    private WorkTimeCommonDeleteCommandHandler workTimeCommonDeleteCommandHandler;

    /**
     * Find all by Cid.
     *
     * @return the list
     */
    @POST
    @Path("findAll")
    public List<WorkTimeDto> findAll() {
        return this.workTimeSetFinder.findAll();
    }

    /**
     * Find by codes.
     *
     * @param codes the codes
     * @return the list
     */
    @POST
    @Path("findByCodes")
    public List<WorkTimeDto> findByCodes(List<String> codes) {
        return this.workTimeSetFinder.findByCodes(codes);
    }

    /**
     * Find by codes.
     *
     * @param codes the codes
     * @return the list
     */
    @POST
    @Path("get_worktime_by_codes")
    public List<WorkTimeDto> getWorkTimeByCodes(getWorkTimeDto param) {
        return this.workTimeSetFinder.findByCodes(param.getWkTimeCodes());
    }

    /**
     * Find by codes.
     *
     * @param codes the codes
     * @return the list
     */
    @POST
    @Path("find_by_code_with_no_master")
    public List<WorkTimeDto> findByCodesWithNoMaster(List<String> codes) {
        return this.workTimeSetFinder.findByCodesWithNoMaster(codes);
    }

    /**
     * Find with condition.
     *
     * @param condition the condition
     * @return the list
     */
    @POST
    @Path("findwithcondition")
    public List<SimpleWorkTimeSettingDto> findWithCondition(WorkTimeSettingCondition condition) {
        return this.workTimeSetFinder.findWithCondition(condition);
    }

    /**
     * Find by time.
     *
     * @param command the command
     * @return the list
     */
    @POST
    @Path("findByTime")
    public List<WorkTimeDto> findByTime(WorkTimeCommandFinder command) {
        return this.workTimeSetFinder.findByTime(command.getCodelist(), command.getStartTime(), command.getEndTime());
    }

    /**
     * Find by work time code.
     *
     * @param workTimeCode the work time code
     * @return the work time dto
     */
    @POST
    @Path("findById/{workTimeCode}")
    public WorkTimeDto findByWorkTimeCode(@PathParam("workTimeCode") String workTimeCode) {
        return this.workTimeSetFinder.findById(workTimeCode);
    }

    /**
     * Save.
     *
     * @param command the command
     */
    @POST
    @Path("fixedset/save")
    public void save(FixedWorkSettingSaveCommand command) {
        this.fixedHandler.handle(command);
    }

    /**
     * Save.
     *
     * @param command the command
     */
    @POST
    @Path("flowset/save")
    public void save(FlowWorkSettingSaveCommand command) {
        this.flowHandler.handle(command);
    }

    /**
     * Save.
     *
     * @param command the command
     */
    @POST
    @Path("difftimeset/save")
    public void save(DiffTimeWorkSettingSaveCommand command) {
        this.diffTimeHandler.handle(command);
    }

    /**
     * Save.
     *
     * @param command the command
     */
    @POST
    @Path("flexset/save")
    public void save(FlexWorkSettingSaveCommand command) {
        this.flexHandler.handle(command);
    }

    /**
     * Find info by work time code.
     *
     * @param workTimeCode the work time code
     * @return the work time setting info dto
     */
    @POST
    @Path("findInfo/{workTimeCode}")
    public WorkTimeSettingInfoDto findInfoByWorkTimeCode(@PathParam("workTimeCode") String workTimeCode) {
        return this.workTimeSettingInfoFinder.find(workTimeCode);
    }

    /**
     * Removes the by work time code.
     *
     * @param command the command
     */
    @POST
    @Path("remove")
    public void removeByWorkTimeCode(WorkTimeCommonDeleteCommand command) {
        this.workTimeCommonDeleteCommandHandler.handle(command);
    }

    /**
     * Find break by codes.
     *
     * @param workTimeCode the work time code
     * @return the break time day dto
     */
    @POST
    @Path("findBreakByCodes/{workTimeCode}")
    public BreakTimeDayDto findBreakByCodes(@PathParam("workTimeCode") String workTimeCode) {
        return this.workTimeSettingInfoFinder.findModeMethod(workTimeCode);
    }

    @POST
    @Path("findWTLanguageByCidAndLangId/{langId}")
    public List<WorkTimeLanguageDto> findWTLanguageByCidAndLangId(@PathParam("langId") String langId) {
        return this.workTimeSettingInfoFinder.findWTLanguageByCidAndLangId(langId);
    }



    //----------------Update KDL001
    @POST
    @Path("findByCodeNew")
    public WorkTimeResultDto findByCodeNew(FindByCodeDto dto) {
        return this.workTimeSetFinder.findByCodeNew(dto.codes, dto.workPlaceId, dto.baseDate);
    }

    //----------------Update KDL003
    @POST
    @Path("findAllNew")
    public WorkTimeResultDto findAllNew(FindByCodeDto dto) {
        return this.workTimeSetFinder.findByCodeNew(dto.codes, dto.workPlaceId, dto.baseDate);
    }
    
  //----------------Update KDLS01
    @POST
    @Path("get_worktimes_kdls01")
    public List<WorkTimeDto> getWorktimesKdls01(FindByCodeMobileDto param) {
        return this.workTimeSetFinder.findByCodeKDLS01(param.codes, param.workTimeSelected, param.workPlaceId, param.referenceDate, param.display);
    }
}

@Value
class getWorkTimeDto {
    List<String> wkTimeCodes;
}
@AllArgsConstructor
@Getter
class FindByCodeDto{
    List<String> codes;
    String workPlaceId;
    String baseDate;
}

@AllArgsConstructor
@Getter
class FindByCodeMobileDto{
    List<String> codes;
    String workPlaceId;
    String referenceDate;
    String workTimeSelected;
    boolean display;
}
