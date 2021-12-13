package nts.uk.ctx.at.shared.infra.entity.agreement.management;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.AgreementTimeOfEmployment;
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
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * entity: 雇用３６協定時間
 */
@Entity
@Table(name = "KSRMT_36AGR_MGT_EMP")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Ksrmt36AgrMgtEmp extends ContractUkJpaEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    public Ksrmt36AgrMgtEmpPk ksrmt36AgrMgtEmpPk;

    /**
     * 基本設定の1ヶ月アラーム時間
     * 雇用３６協定時間.３６協定基本設定.1ヶ月.基本設定.エラーアラーム時間
     */
    @Column(name = "BASIC_M_AL_TIME")
    public double basicMAllTime;
    /**
     * 基本設定の1ヶ月エラー時間
     * 雇用３６協定時間.３６協定基本設定.1ヶ月.基本設定.エラーアラーム時間
     */
    @Column(name = "BASIC_M_ER_TIME")
    public double basicMArlTime;
    /**
     * 基本設定の1ヶ月上限時間
     * 雇用３６協定時間.３６協定基本設定.1ヶ月.基本設定
     */
    @Column(name = "BASIC_M_LIMIT_TIME")
    public double basicMLimitTime;
    /**
     * 特例設定の1ヶ月アラーム時間
     * 雇用３６協定時間.３６協定基本設定.1ヶ月.特例条項による上限.エラーアラーム時間
     */
    @Column(name = "SP_M_AL_TIME")
    public double spMAlTime;
    /**
     * 特例設定の1ヶ月エラー時間
     * 雇用３６協定時間.３６協定基本設定.1ヶ月.特例条項による上限.エラーアラーム時間
     */
    @Column(name = "SP_M_ER_TIME")
    public double spMErTime;
    /**
     * 特例設定の1ヶ月上限時間
     * 雇用３６協定時間.３６協定基本設定.1ヶ月.特例条項による上限
     */
    @Column(name = "SP_M_LIMIT_TIME")
    public double spMLimitTime;
    /**
     * 基本設定の1年間アラーム時間
     * 雇用３６協定時間.３６協定基本設定.1年間.基本設定.エラーアラーム時間
     */
    @Column(name = "BASIC_Y_AL_TIME")
    public double basisYAlTime;
    /**
     * 基本設定の1年間エラー時間
     * 雇用３６協定時間.３６協定基本設定.1年間.基本設定.エラーアラーム時間
     */
    @Column(name = "BASIC_Y_ER_TIME")
    public double basisYErTime;
    /**
     * 特例設定の1年間アラーム時間
     * 雇用３６協定時間.３６協定基本設定.1年間.特例条項による上限.エラーアラーム時間
     */
    @Column(name = "SP_Y_AL_TIME")
    public double spYAlTime;
    /**
     * 特例設定の1年間エラー時間
     * 雇用３６協定時間.３６協定基本設定.1年間.特例条項による上限.エラーアラーム時間
     */
    @Column(name = "SP_Y_ER_TIME")
    public double spYErlTime;
    /**
     * 特例設定の1年間上限時間
     * 雇用３６協定時間.３６協定基本設定.1年間.特例条項による上限
     */
    @Column(name = "SP_Y_LIMIT_TIME")
    public double spYLimitTime;
    /**
     * 複数月平均のアラーム時間
     * 雇用３６協定時間.３６協定基本設定.複数月平均.複数月平均
     */
    @Column(name = "MULTI_M_AVG_AL_TIME")
    public double multiMAvgAlTime;
    /**
     * 複数月平均のエラー時間
     * 雇用３６協定時間.３６協定基本設定.複数月平均.複数月平均
     */
    @Column(name = "MULTI_M_AVG_ER_TIME")
    public double multiMAvgErTime;
    /**
     * 超過上限回数
     * 雇用３６協定時間.３６協定基本設定
     */
    @Column(name = "UPPER_LIMIT_CNT")
    public int upperLimitCnt;

    @Override
    protected Object getKey() {
        return this.ksrmt36AgrMgtEmpPk;
    }

    public static Ksrmt36AgrMgtEmp toEntity(AgreementTimeOfEmployment domain) {
        return new Ksrmt36AgrMgtEmp(
                new Ksrmt36AgrMgtEmpPk(domain.getCompanyId(),domain.getEmploymentCategoryCode().v(), BooleanUtils.toBoolean(domain.getLaborSystemAtr().value)),
                // basicMAllTime ->分類３６協定時間.３６協定基本設定.1ヶ月.基本設定.エラーアラーム時間
                domain.getSetting().getOneMonth().getBasic().getErAlTime().getAlarm().v(),

                // basicMArlTime->分類３６協定時間.３６協定基本設定.1ヶ月.基本設定.エラーアラーム時間
                domain.getSetting().getOneMonth().getBasic().getErAlTime().getError().v(),
                // BASIC_M_LIMIT_TIME->分類３６協定時間.３６協定基本設定.1ヶ月.基本設定
                domain.getSetting().getOneMonth().getBasic().getUpperLimit().v(),

                // SP_M_AL_TIME-> 分類３６協定時間.３６協定基本設定.1ヶ月.特例条項による上限.エラーアラーム時間
                domain.getSetting().getOneMonth().getSpecConditionLimit().getErAlTime().getAlarm().v(),

                // SP_M_ER_TIME->分類３６協定時間.３６協定基本設定.1ヶ月.特例条項による上限.エラーアラーム時間
                domain.getSetting().getOneMonth().getSpecConditionLimit().getErAlTime().getError().v(),

                // SP_M_LIMIT_TIME->分類３６協定時間.３６協定基本設定.1ヶ月.特例条項による上限
                domain.getSetting().getOneMonth().getSpecConditionLimit().getUpperLimit().v(),

                // BASIC_Y_AL_TIME->分類３６協定時間.３６協定基本設定.1年間.基本設定.エラーアラーム時間
                domain.getSetting().getOneYear().getBasic().getAlarm().v(),

                // BASIC_Y_ER_TIME->分類３６協定時間.３６協定基本設定.1年間.基本設定.エラーアラーム時間
                domain.getSetting().getOneYear().getBasic().getError().v(),

                // SP_Y_AL_TIME->分類３６協定時間.３６協定基本設定.1年間.特例条項による上限.エラーアラーム時間
                domain.getSetting().getOneYear().getSpecConditionLimit().getErAlTime().getAlarm().v(),

                // SP_Y_ER_TIME->分類３６協定時間.３６協定基本設定.1年間.特例条項による上限.エラーアラーム時間
                domain.getSetting().getOneYear().getSpecConditionLimit().getErAlTime().getError().v(),

                // SP_Y_LIMIT_TIME->分類３６協定時間.３６協定基本設定.1年間.特例条項による上限
                domain.getSetting().getOneYear().getSpecConditionLimit().getUpperLimit().v(),

                // MULTI_M_AVG_AL_TIME ->分類３６協定時間.３６協定基本設定.複数月平均.複数月平均
                domain.getSetting().getMultiMonth().getMultiMonthAvg().getAlarm().v(),

                // MULTI_M_AVG_ER_TIME->分類３６協定時間.３６協定基本設定.複数月平均.複数月平均
                domain.getSetting().getMultiMonth().getMultiMonthAvg().getError().v(),

                // UPPER_LIMIT_CNT->分類３６協定時間.３６協定基本設定
                domain.getSetting().getOverMaxTimes().value
        );
    }

    public static AgreementTimeOfEmployment toDomain(Ksrmt36AgrMgtEmp entity) {
        val companyId = entity.getKsrmt36AgrMgtEmpPk().getCompanyID();
        val laborSystemAtr = EnumAdaptor.valueOf(BooleanUtils.toInteger(entity.getKsrmt36AgrMgtEmpPk().isLaborSystemAtr()), LaborSystemtAtr.class);

        val erAlTime =  OneMonthErrorAlarmTime.of(new AgreementOneMonthTime((int) entity.getBasicMArlTime()), new AgreementOneMonthTime((int) entity.getBasicMAllTime()));
        val upperLimit = new AgreementOneMonthTime((int) entity.getBasicMLimitTime());

        val basic =  OneMonthTime.createWithCheck(erAlTime, upperLimit);

        val erAlTimeSp =  OneMonthErrorAlarmTime.of(new AgreementOneMonthTime((int) entity.getSpMErTime()), new AgreementOneMonthTime((int) entity.getSpMAlTime()));
        val upperLimitSp = new AgreementOneMonthTime((int) entity.getSpMLimitTime());
        val specConditionLimit =  OneMonthTime.createWithCheck(erAlTimeSp, upperLimitSp);

        val oneMonth = new AgreementOneMonth(basic, specConditionLimit);


        val basicY =  OneYearErrorAlarmTime.of(new AgreementOneYearTime((int) entity.getBasisYErTime()), new AgreementOneYearTime((int) entity.getBasisYAlTime()));
        val erAlTimeSpY =  OneYearErrorAlarmTime.of(new AgreementOneYearTime((int) entity.getSpYErlTime()), new AgreementOneYearTime((int) entity.getSpYAlTime()));

        val upperLimitSpY = new AgreementOneYearTime((int) entity.getSpYLimitTime());

        val specConditionLimitY =  OneYearTime.createWithCheck(erAlTimeSpY, upperLimitSpY);
        val oneYear = new AgreementOneYear(basicY, specConditionLimitY);


        val multiMonthAvg =  OneMonthErrorAlarmTime.of(new AgreementOneMonthTime((int) entity.getMultiMAvgErTime())
                , new AgreementOneMonthTime((int) entity.getMultiMAvgAlTime()));

        val multiMonth = new AgreementMultiMonthAvg(multiMonthAvg);
        val overMaxTimes = EnumAdaptor.valueOf(entity.getUpperLimitCnt(), AgreementOverMaxTimes.class);

        val basicAgreementSetting = new BasicAgreementSetting(oneMonth, oneYear, multiMonth, overMaxTimes);
        return new AgreementTimeOfEmployment(companyId, laborSystemAtr, new EmploymentCode(entity.ksrmt36AgrMgtEmpPk.employmentCode), basicAgreementSetting);

    }
}
