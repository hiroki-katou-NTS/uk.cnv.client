package nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service.SpecialLeaveError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.ClosureStatus;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.SpecialHolidayRemainData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.SpecialLeavaRemainTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.SpecialLeaveGrantUseDay;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.SpecialLeaveRemainDay;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.SpecialLeaveUnDigestion;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * 特休の集計結果
 * @author masaaki_jinno
 *
 */
@AllArgsConstructor
@Getter
@Setter
public class InPeriodOfSpecialLeaveResultInfor {

	/** 特休情報（期間終了日時点） */
	private SpecialLeaveInfo asOfPeriodEnd;
	/** 特休情報（期間終了日の翌日開始時点） */
	private SpecialLeaveInfo asOfStartNextDayOfPeriodEnd;
	/** 特休情報（付与時点） */
	private Optional<List<SpecialLeaveInfo>> asOfGrant;
	/** 特休情報（消滅） */
	private Optional<List<SpecialLeaveInfo>> lapsed;
	/** 特休エラー情報 */
	private List<SpecialLeaveError> specialLeaveErrors;

	/**
	 * コンストラクタ
	 */
	public InPeriodOfSpecialLeaveResultInfor(){
		this.asOfPeriodEnd = new SpecialLeaveInfo();
		this.asOfStartNextDayOfPeriodEnd = new SpecialLeaveInfo();
		this.asOfGrant = Optional.empty();
		this.lapsed = Optional.empty();
		this.specialLeaveErrors = new ArrayList<>();
	}

	/**
	 * ファクトリー
	 * @param asOfPeriodEnd 特休情報（期間終了日時点）
	 * @param asOfStartNextDayOfPeriodEnd 特休情報（期間終了日の翌日開始時点）
	 * @param asOfGrant 特休情報（付与時点）
	 * @param lapsed 特休情報（消滅）
	 * @param specialLeaveErrors 特休エラー情報
	 * @return 特休の集計結果
	 */
	public static InPeriodOfSpecialLeaveResultInfor of(
			SpecialLeaveInfo asOfPeriodEnd,
			SpecialLeaveInfo asOfStartNextDayOfPeriodEnd,
			Optional<List<SpecialLeaveInfo>> asOfGrant,
			Optional<List<SpecialLeaveInfo>> lapsed,
			List<SpecialLeaveError> specialLeaveErrors){

		InPeriodOfSpecialLeaveResultInfor domain = new InPeriodOfSpecialLeaveResultInfor();
		domain.asOfPeriodEnd = asOfPeriodEnd;
		domain.asOfStartNextDayOfPeriodEnd = asOfStartNextDayOfPeriodEnd;
		domain.asOfGrant = asOfGrant;
		domain.lapsed = lapsed;
		domain.specialLeaveErrors = specialLeaveErrors;
		return domain;
	}

	/**
	 * 特休エラー情報の追加
	 * @param error 特休エラー情報
	 */
	public void addError(SpecialLeaveError error){

		if (this.specialLeaveErrors.contains(error)) return;
		this.specialLeaveErrors.add(error);
	}

	public List<nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service.SpecialLeaveError> getErrorlistSharedClass(){
		List<nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service.SpecialLeaveError> result = new ArrayList<>();

		specialLeaveErrors.stream().forEach(c->
			result.add(EnumAdaptor.valueOf(c.value, nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service.SpecialLeaveError.class)));

		return result;
	}

	/*
	 * 特別休暇月別残数データを作成
	 * @param employeeId 社員ID
	 * @param yearMonth 年月
	 * @param closureId 締めID
	 * @param closureDate 締め日付
	 * @param period 期間
	 * @param specialLeaveCode 特別休暇コード
	 * @param remainNoMinus 特別休暇の集計結果
	 * @return 特別休暇月別残数データ
	 */

//	/**
//	 * 実特別休暇
//	 */
//	private SpecialLeave actualSpecial;
//	/**
//	 * 特別休暇
//	 */
//	private SpecialLeave specialLeave;
//	/**
//	 * 付与区分
//	 */
//	private boolean grantAtr;
//	/**
//	 * 未消化数
//	 */
//	private SpecialLeaveUnDigestion unDegestionNumber;
//	/**
//	 * 特別休暇付与情報: 付与日数
//	 */
//	private Optional<SpecialLeaveGrantUseDay> grantDays;
	public SpecialHolidayRemainData createSpecialHolidayRemainData(
			String employeeId,
			YearMonth yearMonth,
			ClosureId closureId,
			ClosureDate closureDate,
			DatePeriod period,
			int specialLeaveCode,
			InPeriodOfSpecialLeaveResultInfor specialLeavResult){

		// 【項目移送】

		SpecialHolidayRemainData domain = new SpecialHolidayRemainData();

		// 社員ID ← パラメータ「社員ID」
		domain.setSid(employeeId);

		// 年月 ← パラメータ「年月」
		domain.setYm(yearMonth);

		// 締めID ← パラメータ「締めID」
		domain.setClosureId(closureId.value);

		// 締め期間 ← パラメータ「期間」
		domain.setClosurePeriod(period);

		// 締め処理状態 ← ”未締め”
		domain.setClosureStatus(ClosureStatus.UNTREATED); // 未締め

		// 締め日 ← パラメータ「締め日」
		domain.setClosureDate(closureDate);

		// 特別休暇コード ← パラメータ「特別休暇コード」
		domain.setSpecialHolidayCd(specialLeaveCode);

		// 特別休暇月別残数データ.特別休暇←特別休暇情報（期間終了日の期間終了日時点）.残数.特別休暇(マイナスなし)
		domain.setSpecialLeave(specialLeavResult.getAsOfPeriodEnd().getRemainingNumber().getSpecialLeaveNoMinus());

		//特別休暇月別残数データ.実特別休暇←特別休暇情報（期間終了日の期間終了日時点）.残数.特別休暇(マイナスあり)
		domain.setActualSpecial(specialLeavResult.getAsOfPeriodEnd().getRemainingNumber().getSpecialLeaveWithMinus());

		//特別休暇月別残数データ.未消化数　←　特別休暇情報（期間終了日の翌日開始時点）．残数．未消化数
		domain.setUnDegestionNumber(
				specialLeavResult.getAsOfStartNextDayOfPeriodEnd().getRemainingNumber().getSpecialLeaveUndigestNumber()
						.map(x -> new SpecialLeaveUnDigestion(new SpecialLeaveRemainDay(x.getDays().v()),
								x.getMinutes().map(y -> new SpecialLeavaRemainTime(y.v()))))
						.orElse(new SpecialLeaveUnDigestion(new SpecialLeaveRemainDay(0.0) , Optional.empty())));

		if(specialLeavResult.getAsOfStartNextDayOfPeriodEnd().getGrantDaysInfo().isPresent()) {
			// 付与区分 ← （特別休暇情報（期間終了日の翌日開始時点）．残数．付与情報が存在する場合）が存在する場合）true
			domain.setGrantAtr(true);
			// 特別休暇情報（期間終了日の翌日開始時点）．残数．付与情報

			domain.setGrantDays(Optional.of(new SpecialLeaveGrantUseDay(specialLeavResult.getAsOfStartNextDayOfPeriodEnd().getGrantDaysInfo().get().v())));
		}else {
			//付与区分 ← （特別休暇情報（期間終了日の翌日開始時点）．残数．付与情報が存在しない場合）false
			domain.setGrantAtr(false);
			//存在しない場合 optional.empty
			domain.setGrantDays(Optional.empty());
		}

		return domain;
	}

	/**
	 * 特別休暇不足分として作成した特別休暇付与データを削除する
	 */
	public void deleteShortageRemainData() {

		// 特別休暇情報(期間終了日時点)の不足分特別休暇残数データを削除
		asOfPeriodEnd.deleteDummy();

		// 特別休暇情報(期間終了日の翌日開始時点)の不足分付与残数データを削除
		asOfStartNextDayOfPeriodEnd.deleteDummy();

		// 特別休暇の集計結果．特別休暇情報(付与時点)を取得
		if ( asOfGrant.isPresent() ){
			// 取得した特別休暇情報(付与時点)でループ
			asOfGrant.get().forEach(info->{
				// 特別休暇情報(付与時点)の不足分付与残数データを削除
				info.deleteDummy();
			});
		}

		// 特別休暇の集計結果．特別休暇情報(消滅時点)を取得
		if ( lapsed.isPresent() ){
			// 取得した特別休暇情報(付与時点)でループ
			lapsed.get().forEach(info->{
				// 付与残数データから特別休暇不足分の特別休暇付与残数を削除
				info.deleteDummy();
			});
		}
	}

}
