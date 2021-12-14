package nts.uk.ctx.at.record.app.command.monthly.standardtime.classification;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.ClassificationCode;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.AgreementTimeOfClassification;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.Classification36AgreementTimeRepository;
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
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Optional;

@Stateless
public class RegisterTimeClassificationCommandHandler extends CommandHandler<RegisterTimeClassificationCommand> {

    @Inject
    private Classification36AgreementTimeRepository repo;

    @Override
    protected void handle(CommandHandlerContext<RegisterTimeClassificationCommand> context) {
        RegisterTimeClassificationCommand command = context.getCommand();

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


        Optional<AgreementTimeOfClassification> agreementTimeOfClassification = this.repo.getByCidAndClassificationCode(AppContexts.user().companyId(),
                command.getClassificationCode(),EnumAdaptor.valueOf(command.getLaborSystemAtr(),LaborSystemtAtr.class));

        if (agreementTimeOfClassification.isPresent()) {
            AgreementTimeOfClassification timeOfClassification = new AgreementTimeOfClassification(AppContexts.user().companyId(),
                    EnumAdaptor.valueOf(command.getLaborSystemAtr(), LaborSystemtAtr.class), new ClassificationCode(command.getClassificationCode()), basicAgreementSetting);
            repo.update(timeOfClassification);
        } else {

            AgreementTimeOfClassification timeOfClassification = new AgreementTimeOfClassification(AppContexts.user().companyId(),
                    EnumAdaptor.valueOf(command.getLaborSystemAtr(), LaborSystemtAtr.class), new ClassificationCode(command.getClassificationCode()), basicAgreementSetting);

            repo.insert(timeOfClassification);
        }
    }
}
