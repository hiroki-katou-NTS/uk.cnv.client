/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.app.command.vacation.setting.annualpaidleave;

import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.TimeAnnualRoundProcesCla;
import nts.uk.ctx.at.shared.dom.vacation.setting.TimeDigestiveUnit;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AcquisitionSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualNumberDay;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSettingGetMemento;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPriority;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.ContractTimeRound;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.DayTimeAnnualLeave;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.HalfDayManage;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.ManageAnnualSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.ManageAnnualSettingGetMemento;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.MaxDayReference;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.MaxTimeDay;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.RemainingNumberSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.RetentionYear;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.RoundProcessingClassification;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.TimeAnnualLeaveTimeDay;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.TimeAnnualMaxDay;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.TimeAnnualSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.TimeAnnualSettingGetMemento;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.YearLyOfNumberDays;
import nts.uk.ctx.at.shared.dom.workingcondition.LaborContractTime;

/**
 * The Class AnnualPaidLeaveSaveCommand.
 */
@Setter
@Getter
public class AnnualPaidLeaveSaveCommand {

    /** The annual manage. */
    private Integer annualManage;

    /** The add attendance day. */
    private Integer addAttendanceDay;

    /** The max manage semi vacation. */
    private Integer maxManageSemiVacation;

    /** The max number semi vacation. */
    private Integer maxNumberSemiVacation;

    /** The max number company. */
    private Integer maxNumberCompany;

    /** The max remaining day. */
    private Double maxRemainingDay;

    /** The number year retain. */
    private Integer numberYearRetain;

    /** The annual priority. */
    private Integer annualPriority;

    /** The remaining number display. */
    private Integer remainingNumberDisplay;

    /** The next grant day display. */
    private Integer nextGrantDayDisplay;

    /** The time manage type. */
    private Integer timeManageType;

    /** The time unit. */
    private Integer timeUnit;

    /** The manage max day vacation. */
    private Integer manageMaxDayVacation;

    /** The reference. */
    private Integer reference;

    /** The max time day. */
    private Integer maxTimeDay;

    /** The is enough time one day. */
    private Integer roundProcessClassific;

    /** The yearly of date. */
    private Double yearlyOfDays;

    /** The round processing classification. */
    private Integer roundProcessCla;
    
    private int timeOfDayReference;
    
    private Integer uniformTime;
    
    private Integer contractTimeRound;

    /**
     * To domain.
     *
     * @param companyId the company id
     * @return the annual paid leave setting
     */
    public AnnualPaidLeaveSetting toDomain(String companyId) {
        return new AnnualPaidLeaveSetting(new AnnualPaidLeaveSettingGetMementoImpl(companyId, this));
    }

    /**
     * The Class AnnualPaidLeaveSettingGetMementoImpl.
     */
    private class AnnualPaidLeaveSettingGetMementoImpl implements AnnualPaidLeaveSettingGetMemento {

        /** The company id. */
        private String companyId;

        /** The command. */
        private AnnualPaidLeaveSaveCommand command;

        /**
         * Instantiates a new annual paid leave setting get memento impl.
         *
         * @param companyId the company id
         * @param command the command
         */
        public AnnualPaidLeaveSettingGetMementoImpl(String companyId, AnnualPaidLeaveSaveCommand command) {
            this.companyId = companyId;
            this.command = command;
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
         * AnnualPaidLeaveSettingGetMemento#getCompanyId()
         */
        @Override
        public String getCompanyId() {
            return this.companyId;
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
         * AnnualPaidLeaveSettingGetMemento#getYearManageType()
         */
        @Override
        public ManageDistinct getYearManageType() {
            return ManageDistinct.valueOf(command.annualManage);
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
         * AnnualPaidLeaveSettingGetMemento#getAcquisitionSetting()
         */
        @Override
        public AcquisitionSetting getAcquisitionSetting() {
            AcquisitionSetting setting = AcquisitionSetting.builder()
                    .annualPriority(AnnualPriority.valueOf(this.command.annualPriority))
                    .build();
            return setting;
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
         * AnnualPaidLeaveSettingGetMemento#getManageAnnualSetting()
         */
        @Override
        public ManageAnnualSetting getManageAnnualSetting() {
            return new ManageAnnualSetting(new ManageAnnualSettingGetMementoImpl(this.companyId, this.command));
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
         * AnnualPaidLeaveSettingGetMemento#getTimeSetting()
         */
        @Override
        public TimeAnnualSetting getTimeSetting() {
            return new TimeAnnualSetting(new TimeVacationSettingGetMementoIpml(this.companyId, this.command));
        }
    }

    /**
     * The Class ManageAnnualSettingGetMementoImpl.
     */
    private class ManageAnnualSettingGetMementoImpl implements ManageAnnualSettingGetMemento {

        /** The company id. */
        private String companyId;

        /** The command. */
        private AnnualPaidLeaveSaveCommand command;

        /**
         * Instantiates a new manage annual setting get memento impl.
         *
         * @param companyId the company id
         * @param command the command
         */
        public ManageAnnualSettingGetMementoImpl(String companyId, AnnualPaidLeaveSaveCommand command) {
            this.companyId = companyId;
            this.command = command;
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
         * ManageAnnualSettingGetMemento#getCompanyId()
         */
        @Override
        public String getCompanyId() {
            return this.companyId;
        }

        @Override
        public HalfDayManage getHalfDayManage() {
            HalfDayManage halfDay = HalfDayManage.builder()
                    .manageType(ManageDistinct.valueOf(this.command.maxManageSemiVacation))
                    .reference(MaxDayReference.valueOf(this.command.maxNumberSemiVacation))
                    .maxNumberUniformCompany(new AnnualNumberDay(this.command.maxNumberCompany))
                    .roundProcesCla(RoundProcessingClassification.valueOf(this.command.roundProcessCla))
                    .build();
            return halfDay;
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
         * ManageAnnualSettingGetMemento#getRemainingNumberSetting()
         */
        @Override
        public RemainingNumberSetting getRemainingNumberSetting() {
            RemainingNumberSetting remain = RemainingNumberSetting.builder()
                    .retentionYear(new RetentionYear(this.command.numberYearRetain))
//                    .remainingDayMaxNumber(this.command.maxRemainingDay != null ? new MaxRemainingDay(
//                            this.command.maxRemainingDay) : null)
                    .build();
            return remain;
        }
        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
         * ManageAnnualSettingGetMemento#getDisplaySetting()
         */
//        @Override
//        public DisplaySetting getDisplaySetting() {
//            DisplaySetting display = DisplaySetting.builder()
//                    .nextGrantDayDisplay(DisplayDivision.valueOf(this.command.nextGrantDayDisplay))
//                    .remainingNumberDisplay(DisplayDivision.valueOf(this.command.remainingNumberDisplay))
//                    .build();
//            return display;
//        }

		@Override
		public YearLyOfNumberDays getYearLyOfDays() {
			return this.command.yearlyOfDays == null ? null : new YearLyOfNumberDays(this.command.yearlyOfDays);
		}
    }

    /**
     * The Class TimeVacationSettingGetMementoIpml.
     */
    private class TimeVacationSettingGetMementoIpml implements TimeAnnualSettingGetMemento {

        /** The company id. */
        private String companyId;

        /** The command. */
        private AnnualPaidLeaveSaveCommand command;

        /**
         * Instantiates a new time vacation setting get memento ipml.
         *
         * @param companyId the company id
         * @param command the command
         */
        public TimeVacationSettingGetMementoIpml(String companyId, AnnualPaidLeaveSaveCommand command) {
            this.companyId = companyId;
            this.command = command;
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
         * TimeVacationSettingGetMemento#getCompanyId()
         */
        @Override
        public String getCompanyId() {
            return this.companyId;
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
         * TimeVacationSettingGetMemento#getTimeManageType()
         */
        @Override
        public ManageDistinct getTimeManageType() {
            return ManageDistinct.valueOf(this.command.timeManageType);
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
         * TimeVacationSettingGetMemento#getTimeUnit()
         */
        @Override
        public TimeDigestiveUnit getTimeUnit() {
            return TimeDigestiveUnit.valueOf(this.command.timeUnit);
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
         * TimeVacationSettingGetMemento#getMaxYearDayLeave()
         */
        @Override
        public TimeAnnualMaxDay getMaxYearDayLeave() {
            TimeAnnualMaxDay timeMaxDay = TimeAnnualMaxDay.builder()
                    .manageType(ManageDistinct.valueOf(this.command.manageMaxDayVacation))
                    .reference(MaxDayReference.valueOf(this.command.reference))
                    .maxNumberUniformCompany(new MaxTimeDay(this.command.maxTimeDay))
                    .build();
            return timeMaxDay;
        }

        /*
         * (non-Javadoc)
         * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
         * TimeAnnualSettingGetMemento#GetRoundProcessClassific()
         */
		@Override
		public TimeAnnualRoundProcesCla GetRoundProcessClassific() {
			return TimeAnnualRoundProcesCla.valueOf(this.command.roundProcessClassific);
		}

//        /*
//         * (non-Javadoc)
//         *
//         * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
//         * TimeVacationSettingGetMemento#isEnoughTimeOneDay()
//         */
//        @Override
//        public boolean isEnoughTimeOneDay() {
//            return false;
//        }
        /*
         * (non-Javadoc)
         * 
         * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
         * TimeVacationSettingGetMemento#isEnoughTimeOneDay()
         */

		@Override
		public TimeAnnualLeaveTimeDay getTimeAnnualLeaveTimeDay() {
			TimeAnnualLeaveTimeDay data = new TimeAnnualLeaveTimeDay(
					DayTimeAnnualLeave.valueOf( this.command.timeOfDayReference),
					Optional.ofNullable(this.command.uniformTime == null ? null : new LaborContractTime(this.command.uniformTime)),
					Optional.ofNullable(this.command.contractTimeRound == null ? null : ContractTimeRound.valueOf(this.command.contractTimeRound)   ));
			
			return data;
		}		
    }
}
