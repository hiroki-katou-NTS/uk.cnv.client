package nts.uk.ctx.at.record.infra.entity.managecompanyagreedhours;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import nts.uk.ctx.at.record.dom.agreementbasicsettings.AgreementsMultipleMonthsAverage;
import nts.uk.ctx.at.record.dom.agreementbasicsettings.AgreementsOneMonth;
import nts.uk.ctx.at.record.dom.agreementbasicsettings.AgreementsOneYear;
import nts.uk.ctx.at.record.dom.standardtime.AgreementTimeOfClassification;
import nts.uk.ctx.at.record.dom.standardtime.AgreementTimeOfCompany;
import nts.uk.ctx.at.record.dom.standardtime.BasicAgreementSetting;
import nts.uk.ctx.at.record.dom.standardtime.enums.LaborSystemtAtr;
import nts.uk.ctx.at.record.dom.standardtime.enums.TimeOverLimitType;
import nts.uk.ctx.at.shared.dom.monthlyattdcal.agreementresult.AgreementOneMonthTime;
import nts.uk.ctx.at.shared.dom.monthlyattdcal.agreementresult.AgreementOneYearTime;
import nts.uk.ctx.at.shared.dom.monthlyattdcal.agreementresult.hourspermonth.ErrorTimeInMonth;
import nts.uk.ctx.at.shared.dom.monthlyattdcal.agreementresult.hourspermonth.OneMonthTime;
import nts.uk.ctx.at.shared.dom.monthlyattdcal.agreementresult.hoursperyear.ErrorTimeInYear;
import nts.uk.ctx.at.shared.dom.monthlyattdcal.agreementresult.hoursperyear.OneYearTime;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * entity: 雇用３６協定時間
 */
@Entity
@Table(name = "KSRMT_36AGR_MGT_CMP")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Ksrmt36AgrMgtCmp extends UkJpaEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    public Ksrmt36AgrMgtCmpPk ksrmt36AgrMgtCmpPk;

    /**
     * 排他バージョン
     */
    @Column(name = "EXCLUS_VER")
    private int exclusVer;
    /**
     * 契約コード
     */
    @Column(name = "CONTRACT_CD")
    public String contractCD;
    /**
     * 基本設定の1ヶ月アラーム時間
     * 会社３６協定時間.３６協定基本設定.1ヶ月.基本設定.エラーアラーム時間
     */
    @Column(name = "BASIC_M_AL_TIME")
    public double basicMAllTime;
    /**
     * 基本設定の1ヶ月エラー時間
     * 会社３６協定時間.３６協定基本設定.1ヶ月.基本設定.エラーアラーム時間
     */
    @Column(name = "BASIC_M_ER_TIME")
    public double basicMArlTime;
    /**
     * 基本設定の1ヶ月上限時間
     * 会社３６協定時間.３６協定基本設定.1ヶ月.基本設定
     */
    @Column(name = "BASIC_M_LIMIT_TIME")
    public double basicMLimitTime;
    /**
     * 特例設定の1ヶ月アラーム時間
     * 会社３６協定時間.３６協定基本設定.1ヶ月.特例条項による上限.エラーアラーム時間
     */
    @Column(name = "SP_M_AL_TIME")
    public double spMAlTime;
    /**
     * 特例設定の1ヶ月エラー時間
     * 会社３６協定時間.３６協定基本設定.1ヶ月.特例条項による上限.エラーアラーム時間
     */
    @Column(name = "SP_M_ER_TIME")
    public double spMErTime;
    /**
     * 特例設定の1ヶ月上限時間
     * 会社３６協定時間.３６協定基本設定.1ヶ月.特例条項による上限
     */
    @Column(name = "SP_M_LIMIT_TIME")
    public double spMLimitTime;
    /**
     * 基本設定の1年間アラーム時間
     * 会社３６協定時間.３６協定基本設定.1年間.基本設定.エラーアラーム時間
     */
    @Column(name = "BASIC_Y_AL_TIME")
    public double basisYAlTime;
    /**
     * 基本設定の1年間エラー時間
     * 会社３６協定時間.３６協定基本設定.1年間.基本設定.エラーアラーム時間
     */
    @Column(name = "BASIC_Y_ER_TIME")
    public double basisYErTime;
    /**
     * 基本設定の1年間上限時間
     * 会社３６協定時間.３６協定基本設定.1年間.基本設定
     */
    @Column(name = "BASIC_Y_LIMIT_TIME")
    public double basisYLimitTime;
    /**
     * 特例設定の1年間アラーム時間
     * 会社３６協定時間.３６協定基本設定.1年間.特例条項による上限.エラーアラーム時間
     */
    @Column(name = "SP_Y_AL_TIME")
    public double spYAlTime;
    /**
     * 特例設定の1年間エラー時間
     * 会社３６協定時間.３６協定基本設定.1年間.特例条項による上限.エラーアラーム時間
     */
    @Column(name = "SP_Y_ER_TIME")
    public double spYErlTime;
    /**
     * 特例設定の1年間上限時間
     * 会社３６協定時間.３６協定基本設定.1年間.特例条項による上限
     */
    @Column(name = "SP_Y_LIMIT_TIME")
    public double spYLimitTime;
    /**
     * 複数月平均のアラーム時間
     * 会社３６協定時間.３６協定基本設定.複数月平均.複数月平均
     */
    @Column(name = "MULTI_M_AVG_AL_TIME")
    public double multiMAvgAlTime;
    /**
     * 複数月平均のエラー時間
     * 会社３６協定時間.３６協定基本設定.複数月平均.複数月平均
     */
    @Column(name = "MULTI_M_AVG_ER_TIME")
    public double multiMAvgErTime;
    /**
     * 超過上限回数
     * 会社３６協定時間.３６協定基本設定
     */
    @Column(name = "UPPER_LIMIT_CNT")
    public int upperLimitCnt;

    @Override
    protected Object getKey() {
        return this.ksrmt36AgrMgtCmpPk;
    }

    public static Ksrmt36AgrMgtCmp toEntity(AgreementTimeOfCompany domain){
        val contractCD = AppContexts.user().contractCode();
        return new Ksrmt36AgrMgtCmp(
                new Ksrmt36AgrMgtCmpPk(domain.getCompanyId(),domain.getLaborSystemAtr().value),
                1,
                contractCD,
                // BASIC_M_AL_TIME->会社３６協定時間.３６協定基本設定.1ヶ月.基本設定.エラーアラーム時間
                domain.getBasicAgreementSetting().getOneMonth().getBasicSetting().getErrorTimeInMonth().getErrorTime().v(),

                // basicMArlTime->会社３６協定時間.３６協定基本設定.1ヶ月.基本設定.エラーアラーム時間
                domain.getBasicAgreementSetting().getOneMonth().getBasicSetting().getErrorTimeInMonth().getAlarmTime().v(),

                // BASIC_M_LIMIT_TIME->会社３６協定時間.３６協定基本設定.1ヶ月.基本設定
                domain.getBasicAgreementSetting().getOneMonth().getBasicSetting().getUpperLimitTime().v(),

                // SP_M_AL_TIME->会社３６協定時間.３６協定基本設定.1ヶ月.特例条項による上限.エラーアラーム時間
                domain.getBasicAgreementSetting().getOneMonth().getUpperLimitDueToSpecialProvisions().getErrorTimeInMonth().getErrorTime().v(),

                // SP_M_ER_TIME->会社３６協定時間.３６協定基本設定.1ヶ月.特例条項による上限.エラーアラーム時間
                domain.getBasicAgreementSetting().getOneMonth().getUpperLimitDueToSpecialProvisions().getErrorTimeInMonth().getAlarmTime().v(),

                //SP_M_LIMIT_TIME->会社３６協定時間.３６協定基本設定.1ヶ月.特例条項による上限
                domain.getBasicAgreementSetting().getOneMonth().getUpperLimitDueToSpecialProvisions().getUpperLimitTime().v(),

                //BASIC_Y_AL_TIME->会社３６協定時間.３６協定基本設定.1年間.基本設定.エラーアラーム時間
                domain.getBasicAgreementSetting().getOneYear().getBasicSetting().getErrorTimeInYear().getErrorTime().v(),

                // BASIC_Y_ER_TIME->会社３６協定時間.３６協定基本設定.1年間.基本設定.エラーアラーム時間
                domain.getBasicAgreementSetting().getOneYear().getBasicSetting().getErrorTimeInYear().getAlarmTime().v(),

                // BASIC_Y_LIMIT_TIME->会社３６協定時間.３６協定基本設定.1年間.基本設定
                domain.getBasicAgreementSetting().getOneYear().getBasicSetting().getUpperLimitTime().v(),

                // SP_Y_AL_TIME->会社３６協定時間.３６協定基本設定.1年間.特例条項による上限.エラーアラーム時間
                domain.getBasicAgreementSetting().getOneYear().getUpperLimitDueToSpecialProvisions().getErrorTimeInYear().getErrorTime().v(),

                // SP_Y_ER_TIME-> 会社３６協定時間.３６協定基本設定.1年間.特例条項による上限.エラーアラーム時間
                domain.getBasicAgreementSetting().getOneYear().getUpperLimitDueToSpecialProvisions().getErrorTimeInYear().getAlarmTime().v(),

                // SP_Y_LIMIT_TIME->会社３６協定時間.３６協定基本設定.1年間.特例条項による上限
                domain.getBasicAgreementSetting().getOneYear().getUpperLimitDueToSpecialProvisions().getUpperLimitTime().v(),

                // MULTI_M_AVG_AL_TIME-> 会社３６協定時間.３６協定基本設定.複数月平均.複数月平均
                domain.getBasicAgreementSetting().getMultipleMonthsAverage().getErrorTimeInMonth().getErrorTime().v(),

                // MULTI_M_AVG_ER_TIME-> 会社３６協定時間.３６協定基本設定.複数月平均.複数月平均
                domain.getBasicAgreementSetting().getMultipleMonthsAverage().getErrorTimeInMonth().getAlarmTime().v(),

                // UPPER_LIMIT_CNT->会社３６協定時間.３６協定基本設定
                domain.getBasicAgreementSetting().getNumberTimesOverLimitType().value
        );
    }

    public static AgreementTimeOfCompany toDomain(Ksrmt36AgrMgtCmp entity){
        val companyId = entity.getKsrmt36AgrMgtCmpPk().getCompanyID();

        val laborSystemAtr =  LaborSystemtAtr.valueOf(entity.getKsrmt36AgrMgtCmpPk().getLaborSystemAtr());
        val  errorTimeInMonth = new ErrorTimeInMonth(new AgreementOneMonthTime((int)entity.getBasicMArlTime())
                ,new AgreementOneMonthTime((int)entity.getBasicMAllTime()));
        val upperLimitTime = new AgreementOneMonthTime((int)entity.getBasicMLimitTime());
        val basicSettingMonth = new OneMonthTime(errorTimeInMonth,upperLimitTime);

        val errorTimeInMonthUpper =  new ErrorTimeInMonth(new AgreementOneMonthTime((int)entity.getSpMErTime())
                ,new AgreementOneMonthTime((int)entity.getSpMAlTime()));
        val upperLimitTimeMonthUpper = new AgreementOneMonthTime((int)entity.getSpYLimitTime());
        val upperLimitDueToSpecialProvisionsMonth = new OneMonthTime(errorTimeInMonthUpper,upperLimitTimeMonthUpper);

        val  errorTimeInYear = new ErrorTimeInYear(new AgreementOneYearTime((int)entity.getBasicMArlTime())
                ,new AgreementOneYearTime((int)entity.getBasisYAlTime()));
        val upperLimitYear = new AgreementOneYearTime((int)entity.getBasisYLimitTime());
        val basicSettingYear = new OneYearTime(errorTimeInYear,upperLimitYear);

        val errorTimeInYearUpper =  new ErrorTimeInYear(new AgreementOneYearTime((int)entity.getSpYErlTime())
                ,new AgreementOneYearTime((int)entity.getSpYAlTime()));
        val upperLimitTimeYearUpper = new AgreementOneYearTime((int)entity.getSpYLimitTime());
        val upperLimitDueToSpecialProvisionsYear = new OneYearTime(errorTimeInYearUpper,upperLimitTimeYearUpper);


        val numberTimesOverLimitType =   TimeOverLimitType.valueOf(entity.getUpperLimitCnt());
        val basicAgreementSetting = new BasicAgreementSetting(
                new AgreementsOneMonth(basicSettingMonth,upperLimitDueToSpecialProvisionsMonth) ,
                new AgreementsOneYear(basicSettingYear,upperLimitDueToSpecialProvisionsYear),
                new AgreementsMultipleMonthsAverage(errorTimeInMonth),numberTimesOverLimitType
        );
        return new AgreementTimeOfCompany(companyId,laborSystemAtr,basicAgreementSetting);


    }
}
