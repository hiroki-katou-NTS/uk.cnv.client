package nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonthDayHolder.Difference;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.adapter.employee.AffComHistItemShareImport;
import nts.uk.ctx.at.shared.dom.adapter.employee.AffCompanyHistSharedImport;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeRecordImport;
import nts.uk.ctx.at.shared.dom.adapter.employee.SClsHistImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.AnnualLeaveEmpBasicInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.basicinfo.SpecialLeaveBasicInfo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums.UseAtr;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;
import nts.uk.ctx.at.shared.dom.specialholiday.grantcondition.AgeBaseYear;
import nts.uk.ctx.at.shared.dom.specialholiday.grantcondition.SpecialLeaveRestriction;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.ElapseYear;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantDate;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantDateTbl;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.TypeTime;
import nts.uk.shr.com.time.calendar.MonthDay;

public class InforSpecialLeaveOfEmployeeSevice {

	/**
	 * RequestList373  社員の特別休暇情報を取得する
	 * @param cid
	 * @param sid
	 * @param specialLeaveCode
	 * @param complileDate
	 * @return
	 */
	public static InforSpecialLeaveOfEmployee getInforSpecialLeaveOfEmployee(RequireM4 require, CacheCarrier cacheCarrier,
			String cid, String sid, int specialLeaveCode,DatePeriod complileDate,SpecialHoliday specialHoliday) {

		InforSpecialLeaveOfEmployee outputData = new InforSpecialLeaveOfEmployee(
				InforStatus.NOTUSE, Optional.empty(), new ArrayList<>(), false);

		//ドメインモデル「特別休暇基本情報」を取得する
		Optional<SpecialLeaveBasicInfo> optBasicInfor = require.specialLeaveBasicInfo(sid, specialLeaveCode, UseAtr.USE);
		if(!optBasicInfor.isPresent()) {
			return outputData;
		}
		if(specialHoliday == null) {
			//ドメインモデル「特別休暇」を取得する
			Optional<SpecialHoliday> optSpecialHoliday = require.specialHoliday(cid, specialLeaveCode);
			if(!optSpecialHoliday.isPresent()) {
				return outputData;
			}
			specialHoliday = optSpecialHoliday.get();
		}

		SpecialLeaveBasicInfo leaverBasicInfo = optBasicInfor.get();
		// 付与日数情報を取得する
		GrantDaysInforByDates grantDayInfors = getGrantDays(require, cacheCarrier, cid, sid,
				complileDate, specialHoliday, leaverBasicInfo);
		// 「付与日数一覧」の件数をチェックする
		int upLimiDays = specialHoliday.getGrantRegular().getLimitCarryoverDays();

		if(grantDayInfors == null || grantDayInfors.getNextSpecialLeaveGrant().isEmpty()) {
			// 状態：「付与なし」を返す
			return new InforSpecialLeaveOfEmployee(InforStatus.NOTGRANT, Optional.of(upLimiDays),
					new ArrayList<>(), false);
		} else {
			// 期限を取得する
			List<SpecialHolidayInfor> getDeadlineInfo = getDeadlineInfo(grantDayInfors, specialHoliday);

			return new InforSpecialLeaveOfEmployee(InforStatus.GRANTED, Optional.of(upLimiDays),
					getDeadlineInfo, false);
		}
			//return new InforSpecialLeaveOfEmployee();
	}

	/**
	 * 付与日数情報を取得する
	 * @param employeeId
	 * @param period
	 * @return
	 */
	public static GrantDaysInforByDates getGrantDays(RequireM3 require, CacheCarrier cacheCarrier,String cid, String employeeId,
			DatePeriod period, SpecialHoliday speHoliday, SpecialLeaveBasicInfo leaveBasicInfo) {
		GeneralDate grantDate = GeneralDate.today();
		//取得しているドメインモデル「特別休暇．付与情報．付与基準日」をチェックする
		if(speHoliday.getGrantRegular().getGrantDate().isPresent() ){
			if( speHoliday.getGrantRegular().getGrantDate().get() == GrantDate.EMP_GRANT_DATE) {
				//社員ID（List）と指定期間から所属会社履歴項目を取得
				List<String> lstEmp = new ArrayList<>();
				lstEmp.add(employeeId);
				List<AffCompanyHistSharedImport> getAffCompanyHistByEmployee = require.employeeAffiliatedCompanyHistories(
						cacheCarrier, lstEmp, period);

				for (AffCompanyHistSharedImport affCompanyHistSharedImport : getAffCompanyHistByEmployee) {
					List<AffComHistItemShareImport> lstAffComHistItem = affCompanyHistSharedImport.getLstAffComHistItem();
					for (AffComHistItemShareImport affComHistItemShareImport : lstAffComHistItem) {
						grantDate = affComHistItemShareImport.getDatePeriod().start();
						break;
					}
				}
			} else if (speHoliday.getGrantRegular().getGrantDate().get().equals(GrantDate.GRANT_BASE_HOLIDAY)){
				//ドメインモデル「年休社員基本情報」を取得する
				Optional<AnnualLeaveEmpBasicInfo> annualLeaveEmpBasicInfo = require.employeeAnnualLeaveBasicInfo(employeeId);
				if(!annualLeaveEmpBasicInfo.isPresent()) {
					return new GrantDaysInforByDates(Optional.ofNullable(grantDate), Collections.emptyList());
				}
				//所得したドメインモデル「年休社員基本情報．付与ルール．付与基準日」をパラメータ「付与基準日」にセットする
				grantDate = annualLeaveEmpBasicInfo.get().getGrantRule().getGrantStandardDate();

			} else {
				//取得している「特別休暇基本情報．付与設定．付与基準日」をパラメータ「付与基準日」にセットする
				grantDate = leaveBasicInfo.getGrantSetting().getGrantDate();
			}
		}

		//取得している「特別休暇．付与情報．付与するタイミングの種類」をチェックする
		if(speHoliday.getGrantRegular().getTypeTime() == TypeTime.GRANT_SPECIFY_DATE) {
			//固定の付与日一覧を求める
			return askGrantDays(require, cacheCarrier, cid, employeeId, period, grantDate, speHoliday, leaveBasicInfo);
		} else {
			// 要修正 jinno　前川さんに確認
//			//テーブルに基づいた付与日数一覧を求める
//			return askGrantdaysFromtable(require, cacheCarrier,cid, employeeId, period, grantDate, leaveBasicInfo, speHoliday);
			return new GrantDaysInforByDates();
		}

	}

	/**
	 * 固定の付与日一覧を求める
	 * @param sid
	 * @param period
	 * @param grantDate
	 * @return
	 */
	public static GrantDaysInforByDates askGrantDays(RequireM2 require, CacheCarrier cacheCarrier, String cid,
			String sid, DatePeriod period, GeneralDate grantDate, SpecialHoliday speHoliday,
			SpecialLeaveBasicInfo leaveBasicInfo) {

		// 要修正 jinno
//		List<GrantDaysInfor> lstOutput = new ArrayList<>();
//		//パラメータ「付与基準日」をパラメータ「比較年月日」にセットする
//		GrantTime grantTime = speHoliday.getGrantRegular().getTypeTime();
//
//		//取得している「特別休暇．付与情報．固定付与日．周期」
//		int interval = grantTime.getFixGrantDate().getInterval().v();
//		//取得している「特別休暇．付与情報．固定付与日．周期」をチェックする
//		if(interval == 0) {
//			return null;
//		}
//		double grantDays = 0;
//		//　◆特別休暇基本情報．適用設定≠所定の条件を適用する　の場合
//		//　　付与日数　←　特別休暇基本情報．付与設定．付与日数
//		if(leaveBasicInfo.getApplicationSet() != SpecialLeaveAppSetting.PRESCRIBED) {
//			grantDays = leaveBasicInfo.getGrantSetting().getGrantDays().isPresent() && leaveBasicInfo.getGrantSetting().getGrantDays().get().v() != null
//					? leaveBasicInfo.getGrantSetting().getGrantDays().get().v() : 0;
//		}
//		//　◆特別休暇基本情報．適用設定＝所定の条件を適用する　の場合
//		// 　　付与日数　←　ドメインモデル「特別休暇．付与情報．固定付与日．固定付与日数」
//
//		else {
//			grantDays = speHoliday.getGrantRegular().getGrantTime().getFixGrantDate().getGrantDays().v();
//		}
//		GrantDaysInforByDates outputData = new GrantDaysInforByDates(grantDate,new ArrayList<>());
//		//パラメータ「期間」に一致する付与日数を生成する
//		for(GeneralDate loopDate = grantDate; loopDate.beforeOrEquals(period.end());) {
//			//パラメータ「比較年月日」とパラメータ「期間」を比較する
//			if(period.start().beforeOrEquals(loopDate)
//					&& loopDate.beforeOrEquals(period.end())) {//「期間．開始日」≦「比較年月日」≦「期間．終了日」
//				//利用条件をチェックする
//				ErrorFlg checkUser = checkUse(require, cacheCarrier, cid, sid, loopDate, speHoliday);
//				if(checkUser.isAgeError()
//						|| checkUser.isClassError()
//						|| checkUser.isEmploymentError()
//						|| checkUser.isGenderError()) {
//					//パラメータ「付与日数一覧」を追加する
//					GrantDaysInfor outPut = new GrantDaysInfor(loopDate, Optional.of(checkUser), 0);
//					lstOutput.add(outPut);
//				} else {
//					GrantDaysInfor outPut = new GrantDaysInfor(loopDate, Optional.empty(), grantDays);
//					lstOutput.add(outPut);
//				}
//			}
//			loopDate = loopDate.addYears(interval);
//			outputData.setGrantDate(loopDate);
//		}
//		outputData.setLstGrantDaysInfor(lstOutput);
		GrantDaysInforByDates outputData = new GrantDaysInforByDates();
		return outputData;
	}

	/**
	 * 利用条件をチェックする
	 * @param require
	 * @param cacheCarrier
	 * @param cid
	 * @param sid
	 * @param baseDate
	 * @param speHoliday
	 * @return
	 */
	public static ErrorFlg checkUse(RequireM2 require, CacheCarrier cacheCarrier, String cid, String sid,
			GeneralDate baseDate, SpecialHoliday speHoliday) {

		ErrorFlg outData = new ErrorFlg(false, false, false, false);
		SpecialLeaveRestriction specialLeaveRestric = speHoliday.getSpecialLeaveRestriction();
		//Imported(就業)「社員」を取得する
		EmployeeRecordImport empInfor = require.employeeFullInfo(cacheCarrier, sid);
		//取得しているドメインモデル「定期付与．特別休暇利用条件．性別条件」をチェックする
		if(specialLeaveRestric.isGenderRest()) {
			//性別が一致するかチェックする
			if(empInfor.getGender() != specialLeaveRestric.getGender().value) {
				//パラメータ「エラーフラグ．性別条件に一致しない」にTRUEをセットする
				outData.setGenderError(true);
			}
		}
		//取得しているドメインモデル「定期付与．特別休暇利用条件．雇用条件」をチェックする
		if(specialLeaveRestric.isRestEmp()) {
			//アルゴリズム「社員所属雇用履歴を取得」を実行する
			Optional<BsEmploymentHistoryImport> findEmploymentHistory = require.employmentHistory(cacheCarrier, cid, sid, baseDate);
			if(!findEmploymentHistory.isPresent()) {
				outData.setEmploymentError(true);
			} else {
				BsEmploymentHistoryImport lstEmployment = findEmploymentHistory.get();

				List<String> listEmp = speHoliday.getSpecialLeaveRestriction().getListEmp();
				if(listEmp != null && !listEmp.isEmpty()) {
					boolean isExit = false;
					if(listEmp.contains(lstEmployment.getEmploymentCode())) {
						isExit = true;
					}
					if(!isExit) {
						outData.setEmploymentError(true);
					}
				}
			}
		}
		//ドメインモデル「特別休暇利用条件」．分類条件をチェックする
		if(specialLeaveRestric.isRestrictionCls()) {
			//アルゴリズム「社員所属分類履歴を取得」を実行する
			List<String> lstSids = new ArrayList<>();
			lstSids.add(sid);
			List<SClsHistImport> lstClass = require.employeeClassificationHistoires(cacheCarrier, cid, lstSids, new DatePeriod(baseDate, baseDate));
			if(lstClass.isEmpty()) {
				outData.setClassError(true);
			}
			//取得した分類コードが取得しているドメインモデル「定期付与．特別休暇利用条件．分類一覧」に存在するかチェックする
			List<String> listClassSpe = speHoliday.getSpecialLeaveRestriction().getListCls();
			if(listClassSpe != null && !listClassSpe.isEmpty()) {
				boolean isExit = false;
				for (SClsHistImport classData : lstClass) {
					if(listClassSpe.contains(classData.getClassificationCode())) {
						isExit = true;
						break;
					}
				}
				if(!isExit) {
					outData.setClassError(true);
				}
			}
		}
		//ドメインモデル「特別休暇利用条件」．年齢条件をチェックする
		if(specialLeaveRestric.isAgeLimit()) {
			GeneralDate ageBase = baseDate;
			//年齢基準日を求める
			MonthDay ageBaseDate = specialLeaveRestric.getAgeStandard().getAgeBaseDate();
			int year = 0;
			if(specialLeaveRestric.getAgeStandard().getAgeCriteriaCls() == AgeBaseYear.THIS_YEAR) {
				//年齢基準日 = パラメータ「基準日．年」 + ドメインモデル「定期付与．特別休暇利用条件．年齢基準．年齢基準日」
				year = ageBaseDate != null ? ageBase.year() : 0;
			} else {
				//年齢基準日 = パラメータ「基準日．年」 の翌年 + ドメインモデル「定期付与．特別休暇利用条件．年齢基準．年齢基準日」
				year = ageBaseDate != null ? ageBase.year() + 1 : 0;
			}
			if(year != 0
					&& ageBaseDate.getMonth() != 0
					&& ageBaseDate.getDay() != 0) {
				ageBase = GeneralDate.ymd(year, ageBaseDate.getMonth(), ageBaseDate.getDay());
			}
			//求めた「年齢基準日」時点の年齢を求める
			Difference difYMD = ageBase.differenceFrom(empInfor.getBirthDay());
			//求めた「年齢」が年齢条件に一致するかチェックする
			if(specialLeaveRestric.getAgeRange().getAgeLowerLimit().v() > difYMD.years()
					|| specialLeaveRestric.getAgeRange().getAgeHigherLimit().v() < difYMD.years()) {
				outData.setAgeError(true);
			}
		}

		return outData;
	}

//	/**
//	 * テーブルに基づいた付与日数一覧を求める
//	 * @param cid　会社ID
//	 * @param sid　社員ID
//	 * @param period　期間
//	 * @param granDate　付与基準日
//	 * @param basicInfor　特別休暇基本情報
//	 * @param speHoliday　特別休暇
//	 * @return　付与日数一覧
//	 */
//	public static GrantDaysInforByDates askGrantdaysFromtable(
//			RequireM1 require,
//			CacheCarrier cacheCarrier,
//			String cid,
//			String sid,
//			DatePeriod period,
//			GeneralDate granDate,
//			SpecialLeaveBasicInfo basicInfor,
//			SpecialHoliday speHoliday) {
//
//		// ドメインモデル「特別休暇付与経過年数テーブル」を取得する ooooo 要require対応
//		List<ElapseYear> elapseYearList = new ArrayList<ElapseYear>();
//
//		List<NextSpecialLeaveGrant> lstOutput = new ArrayList<>();
//		GeneralDate outputDate = null;
//
//		if ( elapseYearList.isEmpty() ){ // 取得できなかった場合
//			return new GrantDaysInforByDates(Optional.ofNullable(outputDate), lstOutput);
//		}
//		ElapseYear elapseYear = elapseYearList.get(0);
//
//		// ドメインモデル「特別休暇付与日数テーブル」を取得する ooooo 要require対応
//		// 【条件】
//		// ・会社ID：パラメータ「会社ID」
//		// ・特別休暇コード：パラメータ「特別休暇コード」
//		List<GrantDateTbl> grantDateTblList = new ArrayList<GrantDateTbl>();
//
//		// ◆特別休暇基本情報．適用設定≠所定の条件を適用する　の場合
//		// 取得している「特別休暇基本情報．付与設定．付与テーブルコード」　
//		if(basicInfor.getApplicationSet() != SpecialLeaveAppSetting.PRESCRIBED
//				&& basicInfor.getGrantSetting().getGrantTable().isPresent()) {
//
//			grantDateTblList = grantDateTblList.stream()
//				.filter(c->c.getGrantDateCode().v().equals(speHoliday.getSpecialHolidayCode().v()))
//				.collect(Collectors.toList());
//		}
//		// ◆特別休暇基本情報．適用設定＝所定の条件を適用する　の場合
//		// 規定のテーブルとする＝TRUE
//		else {
//			grantDateTblList = grantDateTblList.stream()
//					.filter(c->c.isSpecified())
//					.collect(Collectors.toList());
//		}
//
//		if ( grantDateTblList.isEmpty() ){ // 取得できなかった場合
//			return new GrantDaysInforByDates(Optional.ofNullable(outputDate), lstOutput);
//		}
//
//		// パラメータ「付与基準日」がNULLかどうかチェックする
//		if ( granDate == null ){ // 要Optional対応
//			return new GrantDaysInforByDates(Optional.ofNullable(outputDate), lstOutput);
//		}
//
//		GrantDateTbl grantDateTbl = grantDateTblList.get(0);
//
//		// 経過年数に対する付与日数
//		List<GrantElapseYearMonth> grantElapseYearMonthList
//			= grantDateTbl.getElapseYear();
//
//		// ソート
//		// 付与回数　ASC
//		grantElapseYearMonthList
//			= grantElapseYearMonthList
//				.stream()
//				.sorted((a,b)->Integer.compare(a.getElapseNo(),b.getElapseNo()))
//				.collect(Collectors.toList());
//
//		// 経過年数テーブル
//		List<ElapseYearMonthTbl> elapseYearMonthTblList = elapseYear.getElapseYearMonthTblList();
//
//		// 付与日を求める
//		GeneralDate grantDateTmp = GeneralDate.localDate(granDate.localDate());
//
//		// 「期間．終了日」＞=「付与日」の間 ループ
//		for(GrantElapseYearMonth grantElapseYearMonth : grantElapseYearMonthList){
//
//			// 経過年数（付与回数が同じもの）
//			List<ElapseYearMonthTbl> elapseYearMonthTblListTmp
//				= elapseYearMonthTblList.stream()
//				.filter(d->d.getElapseNo() == grantElapseYearMonth.getElapseNo())
//				.collect(Collectors.toList());
//
//			// 付与日数
//			double grantDays = 0;
//
//			// 【経過年数が設定されている間】
//			if ( !elapseYearMonthTblListTmp.isEmpty() ){
//				ElapseYearMonthTbl elapseYearMonthTbl = elapseYearMonthTblListTmp.get(0);
//
//				// 付与日←パラメータ「付与基準日」＋特別休暇付与経過年数テーブル．経過年数テーブル．経過年数
//				// ※ループ毎に経過年数を大きいものに変更する
//				grantDateTmp = grantDateTmp
//						.addYears(elapseYearMonthTbl.getElapseYearMonth().getYear())
//						.addMonths(elapseYearMonthTbl.getElapseYearMonth().getMonth());
//				// 付与日数
//				grantDays = grantElapseYearMonth.getGrantedDays().v();
//			}
//			// 【経過年数が設定されていない】
//			else {
//
//				// 【経過年数の設定がないかつ付与周期が設定されている場合】
//				// 付与日←付与日＋特別休暇付与経過年数テーブル．テーブル以降の付与周期．付与周期
//				if ( elapseYear.getGrantCycleAfterTbl().isPresent() ){
//					grantDateTmp = grantDateTmp
//							.addYears(elapseYear.getGrantCycleAfterTbl().get().getElapseYearMonth().getYear())
//							.addMonths(elapseYear.getGrantCycleAfterTbl().get().getElapseYearMonth().getMonth());
//				}
//				// 【経過年数の設定がないかつ付与周期が設定されていない場合】
//				else {
//					// 付与日←付与日＋1年
//					grantDateTmp = grantDateTmp.addYears(1);
//				}
//
//				// 付与日数
//				if ( grantDateTbl.getGrantedDays().isPresent() ){
//					grantDays = grantDateTbl.getGrantedDays().get().v();
//				}
//
//				// テーブル以降の固定付与をおこなうをチェック
//				// 【ループを抜ける条件】
//				// 経過年数の設定がないかつ「特別休暇付与経過年数テーブル」．テーブル以降の固定付与をおこなうが「チェックなし」場合はループを抜ける
//				if ( !elapseYear.isFixedAssign() ){
//					break;
//				}
//			}
//
//			// 利用条件をチェックする
//			ErrorFlg checkUser = checkUse(require, cacheCarrier, cid, sid, grantDateTmp, speHoliday);
//			if(checkUser.isAgeError() //エラーがあるとき
//					|| checkUser.isClassError()
//					|| checkUser.isEmploymentError()
//					|| checkUser.isGenderError()) {
////				// パラメータ「付与日数一覧」を追加する
////				GrantDaysInfor outPut = new GrantDaysInfor(grantDateTmp, Optional.of(checkUser), 0);
////				lstOutput.add(outPut);
//
//			} else { // 利用可能
//				//　パラメータ「付与日数一覧」を追加する
//
//				//　【追加する項目】
//				//　・年月日←パラメータ「付与日」
//				//　・回数←期間中に付与された回数
//				int count = lstOutput.size();
//				count++;
//				//　・付与日数　←　
//				//　【処理中の経過年数が存在する場合】
//				//　処理中の「経過年数テーブル．付与回数」に対応する「付与日数」
//				//　【処理中の経過年数が存在しない場合】
//				//　「テーブル以降付与日数.付与日数」
//				NextSpecialLeaveGrant outPut = new NextSpecialLeaveGrant();
//				outPut.setGrantDate(grantDateTmp);
//				outPut.setGrantDays(new GrantDays(grantDays));
//				outPut.setTimes(new GrantNum(count));
//				lstOutput.add(outPut);
//			}
//		}
//
//		return new GrantDaysInforByDates(Optional.ofNullable(outputDate), lstOutput);
//	}

	/**
	 * テーブルに基づいた付与日数一覧を求める  元のソースコード
	 * @param cid　会社ID
	 * @param sid　社員ID
	 * @param period　期間
	 * @param granDate　年月日
	 * @param basicInfor　特別休暇基本情報
	 * @return　付与日数一覧
	 */
	/*
	public static GrantDaysInforByDates askGrantdaysFromtable(
			RequireM1 require,
			CacheCarrier cacheCarrier,
			String cid,
			String sid,
			DatePeriod period,
			GeneralDate granDate,
			SpecialLeaveBasicInfo basicInfor,
			SpecialHoliday speHoliday) {

		List<GrantDaysInfor> lstOutput = new ArrayList<>();
		Optional<GrantDateTbl> optGrantDateTbl = Optional.empty();
		GeneralDate outputDate = null;
		List<ElapseYear> elapseYear = new ArrayList<>();

		//◆特別休暇基本情報．適用設定≠所定の条件を適用する　の場合
		//取得している「特別休暇基本情報．付与設定．付与テーブルコード」　
		if(basicInfor.getApplicationSet() != SpecialLeaveAppSetting.PRESCRIBED
				&& basicInfor.getGrantSetting().getGrantTable().isPresent()) {
			elapseYear = require.elapseYear(cid, speHoliday.getSpecialHolidayCode().v(),
					basicInfor.getGrantSetting().getGrantTable().get().v());
		}
		//◆特別休暇基本情報．適用設定＝所定の条件を適用する　の場合
		//規定のテーブルとする＝TRUE
		else {
			optGrantDateTbl = require.grantDateTbl(cid, basicInfor.getSpecialLeaveCode().v());

			if(optGrantDateTbl.isPresent()) {
				elapseYear = require.elapseYear(cid, basicInfor.getSpecialLeaveCode().v(),
						optGrantDateTbl.get().getGrantDateCode().v());
			}
		}
		//※処理中の「特別休暇付与テーブル．経過年数に対する付与日数．経過年数」を次へ更新
		if(elapseYear.isEmpty()) {
			return new GrantDaysInforByDates(outputDate, lstOutput);
		}
		int lastYear = granDate.year();
		for (ElapseYear yearData : elapseYear) {
			//パラメータ「比較年月日」に取得したドメインモデル「特別休暇付与テーブル．経過年数に対する付与日数．経過年数」を加算する
			GeneralDate granDateTmp = granDate.addYears(yearData.getYears().v());
			granDateTmp = granDateTmp.addMonths(yearData.getMonths().v());
			outputDate = granDateTmp;
			//最後の経過年数 = 処理中の「特別休暇付与テーブル．経過年数．年数
			lastYear = granDateTmp.year();
			//パラメータ「比較年月日」とパラメータ「期間」を比較する
			if(period.start().beforeOrEquals(granDateTmp)
					&& period.end().afterOrEquals(granDateTmp)) {
				//利用条件をチェックする
				ErrorFlg errorFlg = checkUse(require, cacheCarrier, cid, sid, granDateTmp, speHoliday);
				if(errorFlg.isAgeError()
						|| errorFlg.isClassError()
						|| errorFlg.isEmploymentError()
						|| errorFlg.isGenderError()) {
					GrantDaysInfor output = new GrantDaysInfor(granDateTmp, Optional.of(errorFlg), 0);
					lstOutput.add(output);
				} else {
					GrantDaysInfor output = new GrantDaysInfor(granDateTmp, Optional.empty(), yearData.getGrantedDays().v());
					lstOutput.add(output);
				}
			} else if(period.end().before(granDateTmp)) {
				break;
			}
		}
		//パラメータ「付与日数一覧」の件数= 0 AND
		//ドメインモデル「特別休暇付与テーブル」．テーブル以降の固定付与をおこなうが「チェックある」
		if(lstOutput.isEmpty()
				&& (optGrantDateTbl.isPresent() && optGrantDateTbl.get().isSpecified())) {
			GeneralDate grantDateNext = GeneralDate.ymd(lastYear + 1, granDate.month(), granDate.day());
			for(GeneralDate loopDate = grantDateNext; loopDate.beforeOrEquals(period.end());) {
				//「期間．開始日」≦「比較年月日」≦「期間．終了日」
				if(period.start().beforeOrEquals(loopDate)
						&& loopDate.beforeOrEquals(period.end())) {
					//利用条件をチェックする
					ErrorFlg errorFlg = checkUse(require, cacheCarrier, cid, sid, loopDate, speHoliday);
					if(errorFlg.isAgeError()
							|| errorFlg.isClassError()
							|| errorFlg.isEmploymentError()
							|| errorFlg.isGenderError()) {
						GrantDaysInfor output = new GrantDaysInfor(loopDate, Optional.of(errorFlg), 0);
						lstOutput.add(output);
					} else {
						GrantDaysInfor output = new GrantDaysInfor(loopDate, Optional.empty(), optGrantDateTbl.get().getNumberOfDays());
						lstOutput.add(output);
					}
				}
				loopDate = loopDate.addYears(1);
				outputDate = loopDate;
			}
		}

		return new GrantDaysInforByDates(outputDate, lstOutput);
	}
	*/

	/**
	 * 期限を取得する
	 */
	public static List<SpecialHolidayInfor> getDeadlineInfo(
			GrantDaysInforByDates grantDaysInfor,
			SpecialHoliday specialHoliday) {

//		TimeLimitSpecification timeSpecifyMethod = specialHoliday.getGrantPeriodic().getTimeSpecifyMethod();

		// 要修正 jinno
//		TimeLimitSpecification timeSpecifyMethod;
//		TypeTime typeTime = specialHoliday.getGrantRegular().getTypeTime();
//
//		if (typeTime.equals(TypeTime.GRANT_SPECIFY_DATE) ) { // 指定日付与
//			if ( specialHoliday.getGrantRegular().getFixGrantDate().isPresent()) {
//				timeSpecifyMethod = specialHoliday.getGrantRegular().getFixGrantDate().get().getGrantPeriodic().getTimeSpecifyMethod();
//			}
//		} else if (typeTime.equals(TypeTime.REFER_GRANT_DATE_TBL) ) { // 指定日付与
//			if ( specialHoliday.getGrantRegular().getGrantPeriodic().isPresent() ) {
//				timeSpecifyMethod = specialHoliday.getGrantRegular().getGrantPeriodic().get().getTimeSpecifyMethod();
//			}
//		}
//
//		List<SpecialHolidayInfor> lstOutput = new ArrayList<>();
//
//		//取得している「特別休暇．期限情報．期限指定方法」をチェックする
//		if(timeSpecifyMethod == TimeLimitSpecification.INDEFINITE_PERIOD) {
//			//パラメータ「付与日数一覧」を元にパラメータ「特別休暇情報一覧」を生成する
//			grantDaysInfor.getNextSpecialLeaveGrant().stream().forEach(x -> {
//				SpecialHolidayInfor output = new SpecialHolidayInfor(x, Optional.empty());
//				lstOutput.add(output);
//			});
//		} else if (timeSpecifyMethod == TimeLimitSpecification.AVAILABLE_GRANT_DATE_DESIGNATE) {
//			//期限日　←　パラメータ「付与日数一覧．年月日」＋取得している「定期付与．付与日（定期）．特別休暇の期限」の「年数」「月数」の年月日
//			SpecialVacationDeadline speDeadline = specialHoliday.getGrantPeriodic().getExpirationDate();
//			grantDaysInfor.getNextSpecialLeaveGrant().stream().forEach(x -> {
//				GeneralDate dealineDate = x.getYmd().addYears(speDeadline.getYears().v());
//				dealineDate = dealineDate.addMonths(speDeadline.getMonths().v());
//				dealineDate = dealineDate.addDays(-1);
//				SpecialHolidayInfor output = new SpecialHolidayInfor(x, Optional.of(dealineDate));
//				lstOutput.add(output);
//			});
//		} else if (timeSpecifyMethod == TimeLimitSpecification.AVAILABLE_UNTIL_NEXT_GRANT_DATE) {
//			//パラメータ「付与日数一覧．年月日」の次の「付与日数一覧．年月日」
//			//　※最後の処理で次の「付与日数一覧．年月日」が存在しない場合 パラメータ「期間外次回付与日」をセット
//			//List<GrantDaysInfor> lstGrantDaysInfor
//			int i = 1;
//			for (GrantDaysInfor daysInfor : grantDaysInfor.getLstGrantDaysInfor()) {
//				SpecialHolidayInfor output = new SpecialHolidayInfor();
//				if(i == grantDaysInfor.getLstGrantDaysInfor().size()) {
//					output = new SpecialHolidayInfor(daysInfor, grantDaysInfor.getGrantDate() != null
//							? Optional.of(grantDaysInfor.getGrantDate().get().addDays(-1)) : Optional.empty());
//				} else {
//					GrantDaysInfor nextInfor = grantDaysInfor.getLstGrantDaysInfor().get(i);
//					output = new SpecialHolidayInfor(daysInfor, Optional.of(nextInfor.getYmd().addDays(-1)));
//				}
//				lstOutput.add(output);
//				i += 1;
//			}
//		} else {
//			grantDaysInfor.getLstGrantDaysInfor().stream().forEach(x -> {
//				//付与日　←　パラメータ「付与日数一覧．年月日」の次の「特別休暇．期限情報．使用可能期間．開始日」
//				AvailabilityPeriod period = specialHoliday.getGrantPeriodic().getAvailabilityPeriod();
//				GeneralDate ymd = GeneralDate.ymd(x.getYmd().year(), period.getStartDate().getMonth(), period.getStartDate().getDay());
//				if(ymd.after(x.getYmd())) {
//					x.setYmd(ymd);
//				}
//				//パラメータ「付与日数一覧．年月日」の次の「特別休暇．期限情報．使用可能期間．終了日」
//				GeneralDate ymdDealine = GeneralDate.ymd(x.getYmd().year(), period.getEndDate().getMonth(), period.getEndDate().getDay());
//				if(x.getYmd().after(ymdDealine)) {
//					ymdDealine = ymdDealine.addYears(1);
//				}
//				SpecialHolidayInfor output = new SpecialHolidayInfor(x, Optional.of(ymdDealine));
//				lstOutput.add(output);
//			});
//		}
		List<SpecialHolidayInfor> lstOutput = new ArrayList<>();
		return lstOutput;
	}

	/**
	 * đối ứng cho cps003
	 * 期限を取得する
	 */
	public static Map<String, List<SpecialHolidayInfor>> getDeadlineInfo(List<GrantDaysInforByDates> grantDaysInfors,
			SpecialHoliday specialHoliday) {
		Map<String, List<SpecialHolidayInfor>> result = new HashMap<>();

		// 要修正 jinno 前川さんに設計依頼
//		TimeLimitSpecification timeSpecifyMethod = specialHoliday.getGrantPeriodic().getTimeSpecifyMethod();
//		grantDaysInfors.stream().forEach(c ->{
//			List<SpecialHolidayInfor> lstOutput = new ArrayList<>();
//			//取得している「特別休暇．期限情報．期限指定方法」をチェックする
//			if(timeSpecifyMethod == TimeLimitSpecification.INDEFINITE_PERIOD) {
//				//パラメータ「付与日数一覧」を元にパラメータ「特別休暇情報一覧」を生成する
//				c.getLstGrantDaysInfor().stream().forEach(x -> {
//					SpecialHolidayInfor output = new SpecialHolidayInfor(x, Optional.empty());
//					lstOutput.add(output);
//				});
//			} else if (timeSpecifyMethod == TimeLimitSpecification.AVAILABLE_GRANT_DATE_DESIGNATE) {
//				//期限日　←　パラメータ「付与日数一覧．年月日」＋取得している「定期付与．付与日（定期）．特別休暇の期限」の「年数」「月数」の年月日
//				SpecialVacationDeadline speDeadline = specialHoliday.getGrantPeriodic().getExpirationDate();
//				c.getLstGrantDaysInfor().stream().forEach(x -> {
//					GeneralDate dealineDate = x.getYmd().addYears(speDeadline.getYears().v());
//					dealineDate = dealineDate.addMonths(speDeadline.getMonths().v());
//					dealineDate = dealineDate.addDays(-1);
//					SpecialHolidayInfor output = new SpecialHolidayInfor(x, Optional.of(dealineDate));
//					lstOutput.add(output);
//				});
//			} else if (timeSpecifyMethod == TimeLimitSpecification.AVAILABLE_UNTIL_NEXT_GRANT_DATE) {
//				//パラメータ「付与日数一覧．年月日」の次の「付与日数一覧．年月日」
//				//　※最後の処理で次の「付与日数一覧．年月日」が存在しない場合 パラメータ「期間外次回付与日」をセット
//				//List<GrantDaysInfor> lstGrantDaysInfor
//				int i = 1;
//				for (NextSpecialLeaveGrant daysInfor : c.getLstGrantDaysInfor()) {
//					SpecialHolidayInfor output = new SpecialHolidayInfor();
//					if(i == c.getLstGrantDaysInfor().size()) {
//						output = new SpecialHolidayInfor(daysInfor, c.getGrantDate() != null
//								? Optional.of(c.getGrantDate().addDays(-1)) : Optional.empty());
//					} else {
//						GrantDaysInfor nextInfor = c.getLstGrantDaysInfor().get(i);
//						output = new SpecialHolidayInfor(daysInfor, Optional.of(nextInfor.getYmd().addDays(-1)));
//					}
//					lstOutput.add(output);
//					i += 1;
//				}
//			} else {
//				c.getLstGrantDaysInfor().stream().forEach(x -> {
//					//付与日　←　パラメータ「付与日数一覧．年月日」の次の「特別休暇．期限情報．使用可能期間．開始日」
//					AvailabilityPeriod period = specialHoliday.getGrantPeriodic().getAvailabilityPeriod();
//					GeneralDate ymd = GeneralDate.ymd(x.getYmd().year(), period.getStartDate().getMonth(), period.getStartDate().getDay());
//					if(ymd.after(x.getYmd())) {
//						x.setYmd(ymd);
//					}
//					//パラメータ「付与日数一覧．年月日」の次の「特別休暇．期限情報．使用可能期間．終了日」
//					GeneralDate ymdDealine = GeneralDate.ymd(x.getYmd().year(), period.getEndDate().getMonth(), period.getEndDate().getDay());
//					if(x.getYmd().after(ymdDealine)) {
//						ymdDealine = ymdDealine.addYears(1);
//					}
//					SpecialHolidayInfor output = new SpecialHolidayInfor(x, Optional.of(ymdDealine));
//					lstOutput.add(output);
//				});
//			}
//			result.put(c.getSid(), lstOutput);
//		});
		return result;
	}

	public static interface RequireM1 extends RequireM2 {

		Optional<ElapseYear> elapseYear(String companyId, int specialHolidayCode);

		Optional<GrantDateTbl> grantDateTbl(String companyId, int specialHolidayCode, String grantDateCode);

		List<GrantDateTbl> grantDateTbl(String companyId, int specialHolidayCode);
	}

	public static interface RequireM2 {

		EmployeeRecordImport employeeFullInfo(CacheCarrier cacheCarrier, String empId);

		EmployeeImport employeeInfo(CacheCarrier cacheCarrier, String empId);

		Optional<BsEmploymentHistoryImport> employmentHistory(CacheCarrier cacheCarrier, String companyId,
				String employeeId, GeneralDate baseDate);

		List<SClsHistImport> employeeClassificationHistoires(CacheCarrier cacheCarrier, String companyId,
				List<String> employeeIds, DatePeriod datePeriod);
	}

	public static interface RequireM3 extends RequireM1 {

		List<AffCompanyHistSharedImport> employeeAffiliatedCompanyHistories(CacheCarrier cacheCarrier,
				List<String> sids, DatePeriod datePeriod);

		Optional<AnnualLeaveEmpBasicInfo> employeeAnnualLeaveBasicInfo(String employeeId);
	}

	public static interface RequireM4 extends RequireM3 {

		Optional<SpecialLeaveBasicInfo> specialLeaveBasicInfo(String sid, int spLeaveCD, UseAtr use);
		List<SpecialHoliday> specialHoliday(String companyID);
		Optional<SpecialHoliday> specialHoliday(String companyID, int specialHolidayCD);
	}
}