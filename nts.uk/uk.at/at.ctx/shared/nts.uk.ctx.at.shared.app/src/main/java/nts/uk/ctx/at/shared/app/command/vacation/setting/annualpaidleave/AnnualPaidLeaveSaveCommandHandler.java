/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.app.command.vacation.setting.annualpaidleave;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.app.command.vacation.setting.retentionyearly.RetentionYearlySaveCommand;
import nts.uk.ctx.at.shared.app.command.vacation.setting.retentionyearly.RetentionYearlySaveCommandHandler;
import nts.uk.ctx.at.shared.app.command.vacation.setting.retentionyearly.dto.RetentionYearlyDto;
import nts.uk.ctx.at.shared.app.command.vacation.setting.retentionyearly.dto.UpperLimitSettingDto;
import nts.uk.ctx.at.shared.app.find.vacation.setting.retentionyearly.RetentionYearlyFinder;
import nts.uk.ctx.at.shared.app.find.vacation.setting.retentionyearly.dto.RetentionYearlyFindDto;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.TimeAnnualRoundProcesCla;
import nts.uk.ctx.at.shared.dom.vacation.setting.TimeDigestiveUnit;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSettingDomainEvent;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPriority;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.DisplayDivision;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.ManageAnnualSettingDomainEvent;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.MaxDayReference;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.RoundProcessingClassification;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.TimeAnnualSettingDomainEvent;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.RetentionYearlySettingDomainEvent;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class AnnualPaidLeaveUpateCommandHandler.
 */
@Stateless
public class AnnualPaidLeaveSaveCommandHandler extends CommandHandler<AnnualPaidLeaveSaveCommand> {

    /** The Constant TRUE. */
    private static final boolean TRUE = true;

    /** The bit true. */
    private static final int BIT_TRUE = 1;

    /** The bit false. */
    private static final int BIT_FALSE = 0;

    /** The annual repo. */
    @Inject
    private AnnualPaidLeaveSettingRepository annualRepo;

    /** The finder. */
	@Inject
	private RetentionYearlyFinder finder;

	/** The save. */
	@Inject
	private RetentionYearlySaveCommandHandler save;

    /*
     * (non-Javadoc)
     *
     * @see
     * nts.arc.layer.app.command.CommandHandler#handle(nts.arc.layer.app.command
     * .CommandHandlerContext)
     */
    @Override
    protected void handle(CommandHandlerContext<AnnualPaidLeaveSaveCommand> context) {
        AnnualPaidLeaveSaveCommand command = context.getCommand();

        String companyId = AppContexts.user().companyId();
        AnnualPaidLeaveSetting domain = this.annualRepo.findByCompanyId(companyId);

        // Check field enable/disable
        this.validateField(command, domain);

        AnnualPaidLeaveSetting setting = command.toDomain(companyId);
        if (domain != null) {
            this.annualRepo.update(setting);
        } else {
            this.annualRepo.add(setting);
        }

        //get ManagementCategory from DB
        int yearManageTypeDB = domain != null ? domain.getYearManageType().value : -1;
       //check managementCategory change
        boolean annualManage = command.getAnnualManage() != yearManageTypeDB;
        boolean manage = command.getAnnualManage() == ManageDistinct.YES.value;

        if (annualManage) {
            val annualPaidLeaveSettingEvent = new AnnualPaidLeaveSettingDomainEvent(manage);
            annualPaidLeaveSettingEvent.toBePublished();

            if(manage){
            	 boolean addAttendanceDay = command.getAddAttendanceDay() == ManageDistinct.YES.value;
            	 val retentionYearlySettingEvent = new RetentionYearlySettingDomainEvent(addAttendanceDay);
                 retentionYearlySettingEvent.toBePublished();

                 //get timeManageType from DB
                 int timeManageTypeDB = domain != null ? domain.getTimeSetting().getTimeManageType().value : -1;
                 //check timeManageType change
                 boolean flatTimeManageType = command.getTimeManageType() != timeManageTypeDB;
                 if(flatTimeManageType){
                	 boolean flatManage = command.getTimeManageType() == ManageDistinct.YES.value;
                	 val timeAnnualSettingEvent = new TimeAnnualSettingDomainEvent(flatManage);
                     timeAnnualSettingEvent.toBePublished();
                 }

                 //get timeManageType from DB
                /* int maxManageSemiVacationDB = domain != null ? domain.getManageAnnualSetting().getHalfDayManage().getManageType().value : -1;
                 //check timeManageType change
                 boolean maxManageSemiVacation = command.getMaxManageSemiVacation() != maxManageSemiVacationDB;*/
                 if(command.getMaxManageSemiVacation().intValue() == 1){
                	 boolean flatManage = command.getMaxManageSemiVacation() == ManageDistinct.YES.value;
                	 val manageAnnualSettingEvent = new ManageAnnualSettingDomainEvent(flatManage);
                     manageAnnualSettingEvent.toBePublished();
                 }
            }
            else {
            	RetentionYearlyFindDto findDto = finder.findById();
            	RetentionYearlyDto saveDto = new RetentionYearlyDto();
            	UpperLimitSettingDto upperLimitDto = new UpperLimitSettingDto();
            	if (findDto == null) {
            		upperLimitDto.setMaxDaysCumulation(0);
                	upperLimitDto.setRetentionYearsAmount(99);
                	saveDto.setLeaveAsWorkDays(false);
            	}
            	else {
	            	upperLimitDto.setMaxDaysCumulation(findDto.getUpperLimitSetting().getMaxDaysCumulation());
	            	upperLimitDto.setRetentionYearsAmount(findDto.getUpperLimitSetting().getRetentionYearsAmount());
	            	saveDto.setLeaveAsWorkDays(findDto.getLeaveAsWorkDays());
            	}
            	saveDto.setUpperLimitSettingDto(upperLimitDto);
            	saveDto.setManagementCategory(0);
            	RetentionYearlySaveCommand saveCommand = new RetentionYearlySaveCommand();
            	saveCommand.setRetentionYearly(saveDto);
            	save.handle(saveCommand);
            }
        }
        if(manage){
        	 //get timeManageType from DB
            int timeManageTypeDB = domain != null ? domain.getTimeSetting().getTimeManageType().value : -1;
            //check timeManageType change
            boolean flatTimeManageType = command.getTimeManageType() != timeManageTypeDB;
            if(flatTimeManageType){
           	 boolean flatManage = command.getTimeManageType() == ManageDistinct.YES.value;
           	 val timeAnnualSettingEvent = new TimeAnnualSettingDomainEvent(flatManage);
                timeAnnualSettingEvent.toBePublished();
            }

            //get timeManageType from DB
           // maxManageSemiVacationDB = domain != null ? domain.getManageAnnualSetting().getHalfDayManage().getManageType().value : -1;
            //check timeManageType change
          //  boolean maxManageSemiVacation = command.getMaxManageSemiVacation() != maxManageSemiVacationDB;
            if(command.getMaxManageSemiVacation().intValue() == 1){
           	 boolean flatManage = command.getMaxManageSemiVacation() == ManageDistinct.YES.value;
           	 val manageAnnualSettingEvent = new ManageAnnualSettingDomainEvent(flatManage);
                manageAnnualSettingEvent.toBePublished();
            }
        }
    }

    /**
     * Validate field.
     *
     * @param command the command
     * @param setttingDB the settting DB
     */
    private void validateField(AnnualPaidLeaveSaveCommand command, AnnualPaidLeaveSetting setttingDB) {
        boolean isAnnualManage = command.getAnnualManage() == ManageDistinct.YES.value;

        // ========= NOT MANAGE =========
        if (!isAnnualManage) {
            // Setting not existed
            if(setttingDB == null) {
                this.initValue(command);
                return;
            }
            // Manage Annual Setting
            command.setAddAttendanceDay(
                    setttingDB.getManageAnnualSetting().isWorkDayCalculate() == TRUE ? BIT_TRUE : BIT_FALSE);
			if (setttingDB.getManageAnnualSetting().getHalfDayManage().manageType == null) {
				command.setMaxManageSemiVacation(0);
			} else {
				command.setMaxManageSemiVacation(
						setttingDB.getManageAnnualSetting().getHalfDayManage().manageType.value);
			}
			if(setttingDB.getManageAnnualSetting().getHalfDayManage().reference == null) {
				command.setMaxNumberSemiVacation(0);	
			}else{
            command.setMaxNumberSemiVacation(setttingDB.getManageAnnualSetting().getHalfDayManage().reference.value);
            }
            if(setttingDB.getManageAnnualSetting().getHalfDayManage()
                    .maxNumberUniformCompany.v() == null){
            	command.setMaxNumberCompany(5);	
            }else{
            	command.setMaxNumberCompany(setttingDB.getManageAnnualSetting().getHalfDayManage()
                        .maxNumberUniformCompany.v());	
            }
            
 //           command.setMaxGrantDay(setttingDB.getManageAnnualSetting().getMaxGrantDay().v());
//            command.setMaxRemainingDay(setttingDB.getManageAnnualSetting().getRemainingNumberSetting()
//                    .remainingDayMaxNumber.v());
            command.setNumberYearRetain(setttingDB.getManageAnnualSetting().getRemainingNumberSetting()
                    .retentionYear.v());
            command.setAnnualPriority(setttingDB.getAcquisitionSetting().annualPriority.value);
//            command.setRemainingNumberDisplay(setttingDB.getManageAnnualSetting().getDisplaySetting()
//                    .remainingNumberDisplay.value);
//            command.setNextGrantDayDisplay(setttingDB.getManageAnnualSetting().getDisplaySetting()
//                    .nextGrantDayDisplay.value);
            if(setttingDB.getManageAnnualSetting().getRemainingNumberSetting()
                    .retentionYear.v() == null){
            	command.setNumberYearRetain(2);
            }else{
            command.setNumberYearRetain(setttingDB.getManageAnnualSetting().getRemainingNumberSetting()
                    .retentionYear.v());}
            command.setAnnualPriority(setttingDB.getAcquisitionSetting().annualPriority.value);
            command.setYearlyOfDays(setttingDB.getManageAnnualSetting().getYearlyOfNumberDays().v());
			if (setttingDB.getManageAnnualSetting().getHalfDayManage().roundProcesCla == null) {
				command.setRoundProcessCla(0);
			} else {
				command.setRoundProcessCla(setttingDB.getManageAnnualSetting().getHalfDayManage().roundProcesCla.value);
			}

            // Time Leave Setting
            command.setTimeManageType(setttingDB.getTimeSetting().getTimeManageType().value);
            command.setTimeUnit(setttingDB.getTimeSetting().getTimeUnit().value);
            command.setManageMaxDayVacation(setttingDB.getTimeSetting().getMaxYearDayLeave().manageType.value);
            command.setReference(setttingDB.getTimeSetting().getMaxYearDayLeave().reference.value);
            command.setMaxTimeDay(setttingDB.getTimeSetting().getMaxYearDayLeave().maxNumberUniformCompany.v());
            command.setRoundProcessClassific(setttingDB.getTimeSetting().getRoundProcessClassific().value);

            return;
        }

        // ========= MANAGE =========
        // Manage Annual Setting
        if (command.getMaxNumberSemiVacation() == MaxDayReference.ReferAnnualGrantTable.value) {
            command.setMaxNumberCompany(setttingDB.getManageAnnualSetting().getHalfDayManage()
                    .maxNumberUniformCompany.v());
        }
        // Time Leave Setting
        boolean isTimeManage = command.getTimeManageType() == ManageDistinct.YES.value;
        if (!isTimeManage) {
            command.setMaxTimeDay(setttingDB.getTimeSetting().getMaxYearDayLeave().maxNumberUniformCompany.v());
            return;
        }
        if (command.getReference() == MaxDayReference.ReferAnnualGrantTable.value) {
            command.setMaxTimeDay(setttingDB.getTimeSetting().getMaxYearDayLeave().maxNumberUniformCompany.v());
        }
    }

    /**
     * Inits the value.
     *
     * @param command the command
     */
    private void initValue(AnnualPaidLeaveSaveCommand command) {
        command.setAddAttendanceDay(ManageDistinct.YES.value);

        // Manage Annual Setting
        command.setMaxManageSemiVacation(ManageDistinct.YES.value);
        command.setMaxNumberSemiVacation(MaxDayReference.CompanyUniform.value);
        command.setMaxNumberCompany(null);
        // TODO: Check value default
        command.setMaxRemainingDay(null);
        command.setNumberYearRetain(2);
        command.setYearlyOfDays(null);
        command.setRoundProcessCla(RoundProcessingClassification.TruncateOnDay0.value);
        // =======
        command.setAnnualPriority(AnnualPriority.FIFO.value);
        command.setRemainingNumberDisplay(DisplayDivision.Indicate.value);
        command.setNextGrantDayDisplay(DisplayDivision.Indicate.value);

        // Time Leave Setting
        command.setTimeManageType(ManageDistinct.YES.value);
        command.setTimeUnit(TimeDigestiveUnit.OneMinute.value);
        command.setManageMaxDayVacation(ManageDistinct.YES.value);
        command.setReference(MaxDayReference.CompanyUniform.value);
        command.setMaxTimeDay(null);
        command.setRoundProcessClassific(TimeAnnualRoundProcesCla.TruncateOnDay0.value);
        //
        command.setUniformTime(0);
        command.setContractTimeRound(0);
    }
}
