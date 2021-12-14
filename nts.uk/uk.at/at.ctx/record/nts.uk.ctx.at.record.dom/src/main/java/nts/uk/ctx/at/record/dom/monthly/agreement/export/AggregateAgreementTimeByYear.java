package nts.uk.ctx.at.record.dom.monthly.agreement.export;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.Year;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfManagePeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfYear;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeYear;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.oneyear.AgreementOneYearTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.oneyear.OneYearTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.setting.AgreementOperationSetting;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.timesetting.BasicAgreementSettingForCalc;
import nts.uk.shr.com.context.AppContexts;

/** 指定する年度の時間をもとに36協定時間を集計する */
public class AggregateAgreementTimeByYear {

	/** [No.684]指定する年度の時間をもとに36協定時間を集計する */
	public static AgreementTimeYear aggregate(RequireM1 require, String sid,
			GeneralDate baseDate, Year year, Map<YearMonth, AttendanceTimeMonth> agreementTimes) {
		
		/** 集計の年月期間を取得する */
		val aggregatePeriod = getAggregatePeriod(require, year);
		if (!aggregatePeriod.isPresent()) {
			return new AgreementTimeYear();
		}
		
		/** パラメータ。36協定時間からMap＜年月、36協定年間時間（Temporary）＞に変換する */
		val yearDatas = agreementTimes.entrySet().stream()
										.collect(Collectors.toMap(at -> at.getKey(), 
																	at -> createYearData(at.getValue())));
		/** 36協定年間時間を取得する */
		return aggregate(require, sid, baseDate, year, aggregatePeriod.get(), yearDatas);
	}
	
	/** 36協定年間時間を取得する */
	public static AgreementTimeYear aggregate(RequireM3 require, String sid,
			GeneralDate baseDate, Year year, YearMonthPeriod ymPeriod,
			Map<YearMonth, AgreementTimeYearTemporary> agreementTimes) {
		
		/** 対象者の３６協定年間設定を取得する */
		val agreementSet = getAgreementSetting(require, sid, baseDate, year);
		val oneYearSet = agreementSet.getBasicSetting().getOneYear();
		
		/** 36協定年間時間（Temporary）を作成する */
		AgreementTimeYearTemporary yearTime = AgreementTimeYearTemporary.builder().build();
		
		/** 年月期間をループする */
		YearMonth current = ymPeriod.start();
		while(current.lessThanOrEqualTo(ymPeriod.end())) {
			
			/** 処理中の年月の36協定時間を取得する */
			val agreementTime = getAgreementTime(require, sid, current, agreementTimes);
			
			/** 36協定年間時間（Temporary）に加算する */
			yearTime.add(agreementTime);
			
			current = current.addMonths(1);
		}
		
		/** 36協定時間状態を確認する */
		val state = agreementSet.checkForOneYear(yearTime.getAgreementTime(), yearTime.getLegalLimitTime());
		
		/** 36協定年間時間を作成して返す */
		return AgreementTimeYear.of(AgreementTimeOfYear.of(
											new AgreementOneYearTime(yearTime.getLegalLimitTime().valueAsMinutes()), 
											oneYearSet.getSpecConditionLimit()),
									AgreementTimeOfYear.of(
											new AgreementOneYearTime(yearTime.getAgreementTime().valueAsMinutes()), 
											OneYearTime.createWithNoUpperLimit(oneYearSet.getBasic())),
									state);
	}
	
	/** 処理中の年月の36協定時間を取得する */
	private static AgreementTimeYearTemporary getAgreementTime(
			RequireM5 require, String sid, YearMonth ym,
			Map<YearMonth, AgreementTimeYearTemporary> agreementTimes) {
		
		/** パラメータ。36協定時間に年月のデータが存在するかをチェックする */
		if (agreementTimes.containsKey(ym)) {
			
			/** パラメータ。36協定時間から36協定時間を取得する */
			return agreementTimes.get(ym);
		}
		
		/** 「管理期間の36協定時間」を取得する */
		return require.agreementTimeOfManagePeriod(sid, ym)
				.map(at -> createYearData(at))
				.orElseGet(() -> createYearData(new AttendanceTimeMonth(0)));
	}
	
	/** 対象者の３６協定年間設定を取得する */
	private static BasicAgreementSettingForCalc getAgreementSetting(RequireM4 require, String sid, GeneralDate baseDate, Year year) {
		
		/** ログイン会社ID */
		val cid = AppContexts.user().companyId();
		
		/**36協定基本設定を取得する */
		return require.basicAgreementSetting(cid, sid, baseDate, year);
	}
	
	/** 36協定年間時間（Temporary）を作成する */
	private static AgreementTimeYearTemporary createYearData(AttendanceTimeMonth agreementTime) {
		
		return AgreementTimeYearTemporary.builder()
				.agreementTime(new AgreementOneYearTime(agreementTime.valueAsMinutes()))
				.build();
	}
	
	/** 36協定年間時間（Temporary）を作成する */
	private static AgreementTimeYearTemporary createYearData(AgreementTimeOfManagePeriod agreementTime) {
		
		return AgreementTimeYearTemporary.map(agreementTime);
	}
	
	/** 集計の年月期間を取得する */
	public static Optional<YearMonthPeriod> getAggregatePeriod(RequireM2 require, Year year) {
		
		/** ログイン会社ID */
		val cid = AppContexts.user().companyId();
		
		/** ○ドメインモデル「３６協定運用設定」を取得 */
		val agreementSetting = require.agreementOperationSetting(cid);
		
		return agreementSetting.map(as -> {

			/** 年月期間を計算する */
			val startYM = YearMonth.of(year.v(), as.getStartingMonth().getMonth());
			val endYM = startYM.addMonths(11);
			
			/** 年月期間を返す */
			return new YearMonthPeriod(startYM, endYM);
		});
	}

	public static interface RequireM5 {
		
		Optional<AgreementTimeOfManagePeriod> agreementTimeOfManagePeriod(String sid, YearMonth ym);
	}
	
	public static interface RequireM4 {
		
		BasicAgreementSettingForCalc basicAgreementSetting(String cid, String sid, GeneralDate baseDate, Year year);
	}
	
	public static interface RequireM3 extends RequireM4, RequireM5 {
		
	}

	public static interface RequireM2 {
		
		Optional<AgreementOperationSetting> agreementOperationSetting(String cid);
	}
	
	public static interface RequireM1 extends RequireM2, RequireM3 {
		
	}
}
