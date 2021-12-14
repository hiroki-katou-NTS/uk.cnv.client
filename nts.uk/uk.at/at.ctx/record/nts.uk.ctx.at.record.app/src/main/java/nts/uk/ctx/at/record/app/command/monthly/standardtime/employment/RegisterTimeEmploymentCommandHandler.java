package nts.uk.ctx.at.record.app.command.monthly.standardtime.employment;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.AgreementTimeOfEmployment;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.Employment36HoursRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.enums.LaborSystemtAtr;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.limitrule.AgreementMultiMonthAvg;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.AgreementOneMonthTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.OneMonthErrorAlarmTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.OneMonthTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.oneyear.AgreementOneYearTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.oneyear.OneYearErrorAlarmTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.oneyear.OneYearTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.timesetting.AgreementOneMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.timesetting.AgreementOneYear;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.timesetting.AgreementOverMaxTimes;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.timesetting.BasicAgreementSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Optional;

@Stateless
public class RegisterTimeEmploymentCommandHandler extends CommandHandler<RegisterTimeEmploymentCommand> {

    @Inject
    private Employment36HoursRepository repo;

    @Override
    protected void handle(CommandHandlerContext<RegisterTimeEmploymentCommand> context) {
        RegisterTimeEmploymentCommand command = context.getCommand();

        val errorTimeInMonth = OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(command.getErrorOneMonth())
                , new AgreementOneMonthTime(command.getAlarmOneMonth()));
        AgreementOneMonthTime upperLimitTime = new AgreementOneMonthTime(command.getLimitOneMonth());

        val basicSettingMonth = OneMonthTime.createWithCheck(errorTimeInMonth, upperLimitTime);

        val errorTimeInMonthUpper = OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(command.getErrorTwoMonths())
                , new AgreementOneMonthTime(command.getAlarmTwoMonths()));
        val upperLimitTimeMonthUpper = new AgreementOneMonthTime(command.getLimitTwoMonths());
        val upperLimitDueToSpecialProvisionsMonth = OneMonthTime.createWithCheck(errorTimeInMonthUpper, upperLimitTimeMonthUpper);

        val basicYearSetting = OneYearErrorAlarmTime.of(new AgreementOneYearTime(command.getErrorOneYear())
                , new AgreementOneYearTime(command.getAlarmOneYear()));

        val errorTimeInYear = OneYearErrorAlarmTime.of(new AgreementOneYearTime(command.getErrorTwoYear())
                , new AgreementOneYearTime(command.getAlarmTwoYear()));
        val upperLimitYear = new AgreementOneYearTime(command.getLimitOneYear());
        val specialYearSetting = OneYearTime.createWithCheck(errorTimeInYear, upperLimitYear);

        val multiMonthAvg = OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(command.getUpperMonthAverageError())
                , new AgreementOneMonthTime(command.getUpperMonthAverageAlarm()));

        BasicAgreementSetting basicAgreementSetting = new BasicAgreementSetting(
                new AgreementOneMonth(basicSettingMonth, upperLimitDueToSpecialProvisionsMonth),
                new AgreementOneYear(basicYearSetting,specialYearSetting),
                new AgreementMultiMonthAvg(multiMonthAvg),
                EnumAdaptor.valueOf(command.getOverMaxTimes(), AgreementOverMaxTimes.class));

        Optional<AgreementTimeOfEmployment> agreementTimeOfEmployment = this.repo.getByCidAndCd(AppContexts.user().companyId(),
                command.getEmploymentCD(),EnumAdaptor.valueOf(command.getLaborSystemAtr(),LaborSystemtAtr.class));

        if (agreementTimeOfEmployment.isPresent()) {
            AgreementTimeOfEmployment agreementTimeOfEmployment1 = new AgreementTimeOfEmployment(AppContexts.user().companyId(),
                    EnumAdaptor.valueOf(command.getLaborSystemAtr(), LaborSystemtAtr.class), new EmploymentCode(command.getEmploymentCD()), basicAgreementSetting);
            repo.update(agreementTimeOfEmployment1);
        } else {

            AgreementTimeOfEmployment agreementTimeOfEmployment1 = new AgreementTimeOfEmployment(AppContexts.user().companyId(),
                    EnumAdaptor.valueOf(command.getLaborSystemAtr(), LaborSystemtAtr.class), new EmploymentCode(command.getEmploymentCD()), basicAgreementSetting);

            repo.insert(agreementTimeOfEmployment1);
        }
    }
}
