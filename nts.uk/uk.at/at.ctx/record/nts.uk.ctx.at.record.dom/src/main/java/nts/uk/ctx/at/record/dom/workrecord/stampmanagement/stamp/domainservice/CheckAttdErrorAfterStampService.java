package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.stamp.application.ErrorInformationApplication;
import nts.uk.ctx.at.record.dom.stamp.application.StampPromptApplication;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErAlApplication;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonSettings;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.PortalStampSettings;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampButton;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampSettingPerson;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.settingforsmartphone.SettingsSmartphoneStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosurePeriod;

/**
 * DS : 打刻後の日別勤怠エラー情報を確認する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.打刻管理.打刻.打刻後の日別勤怠エラー情報を確認する
 * 
 * @author tutk
 *
 */
public class CheckAttdErrorAfterStampService {
	/**
	 * 	[1] 取得する
	 * @param require
	 * @param employeeId
	 * @param pageNo
	 * @param buttonDisNo
	 * @return
	 */
	public static List<DailyAttdErrorInfo> get(Require require, String employeeId, StampMeans stampMeans,
			StampButton stampBtn) {
		
		// if not [prv-5] エラー確認する必要があるか(require, 打刻手段, 打刻ボタン)
		if (!needCheckError(require, stampMeans, stampBtn)) {
			// return List.emptyList
			return Collections.emptyList();
		}

		// $申請促すエラーリスト = [prv-1] 申請促す設定を取得する()
		List<ErrorInformationApplication> listStampRecordDis = getSettingPromptApp(require);
		// if $申請促すエラーリスト.isEmpty()
		if (listStampRecordDis.isEmpty()) {
			// return List.emptyList
			return Collections.emptyList();
		}
		//	$期間 = [prv-2] エラーを確認対象期間を取得する(require, 社員ID)
		Optional<DatePeriod> period = getErrorCheckPeriod(require, employeeId);
		//	if $期間.isEmpty()
		if(!period.isPresent()) {
			//return List.emptyList
			return Collections.emptyList();
		}
		//$社員のエラーリスト = [prv-3] 指定期間の日別実績エラーを取得する(社員ID, $期間)
		List<EmployeeDailyPerError> listEmployeeDailyPerError = getDailyErrorByPeriod(require, employeeId, period.get());
		//	return $申請促すエラーリスト:	 map [prv-4] 日別勤怠エラー情報を作成する(require, $, $社員のエラーリスト)
		return listStampRecordDis.stream()
				.map(x -> createDailyErrorInfo(require, x, listEmployeeDailyPerError))
				.filter(x-> x.isPresent())
				.map(x-> x.get())
				.collect(Collectors.toList());
	}
	
	/**
	 * [prv-1] 申請促す設定を取得する
	 * 
	 * @param require
	 * @return List<ErrorInformationApplication>: List<申請促すエラー情報>
	 */
	private static List<ErrorInformationApplication>  getSettingPromptApp(Require require) {
		//	$打刻の申請促す設定 = require.打刻の申請促す設定を取得する()
		Optional<StampPromptApplication> data =  require.getStampSet();
		//	if $打刻の申請促す設定.isEmpty()
		if(!data.isPresent()) {
			//	return List.emptyList
			return Collections.emptyList();
		}
		//return $打刻の申請促す設定.申請促すエラーリストを取得する()
		return data.get().getErrorListApply();
	}
	
	/**
	 * [prv-2] エラーを確認対象期間を取得する
	 * 
	 * @param require
	 * @param employeeId
	 * @return
	 */
	private static Optional<DatePeriod> getErrorCheckPeriod(Require require, String employeeId) {

		GeneralDate baseDate = GeneralDate.today();

		DatePeriod datePeriod = require.findClosurePeriod(employeeId, baseDate);
		
		GeneralDate dateCompare = datePeriod.start().addMonths(2);
		
		if(dateCompare.afterOrEquals(baseDate)) {
			return Optional.of(datePeriod.newSpan(datePeriod.start(), baseDate));
		}
		
		Optional<ClosurePeriod> opt =  require.getClosurePeriod( employeeId, baseDate);
		return  opt.isPresent()?Optional.of(opt.get().getPeriod()):Optional.empty();

	}
	
	/**
	 * 	[prv-3] 指定期間の日別実績エラーを取得する
	 * @param require
	 * @param employeeId
	 * @param period
	 * @return
	 */
	private static List<EmployeeDailyPerError> getDailyErrorByPeriod(Require require, String employeeId,DatePeriod period) {
		return require.findByPeriodOrderByYmd(employeeId, period);
	}

	/**
	 * [prv-4] 日別勤怠エラー情報を作成する
	 * 
	 * @param require
	 * @param ErrorInformationApplication : 申請促すエラー情報
	 * @param List<EmployeeDailyPerError> : 社員のエラーリスト
	 * @return DailyAttdErrorInfo : Optional<日別勤怠エラー情報>
	 */
	private static Optional<DailyAttdErrorInfo> createDailyErrorInfo(Require require,
			ErrorInformationApplication errorInforApp, List<EmployeeDailyPerError> listEmployeeDailyPerError) {
		
		
		// $対象エラー = 社員のエラーリスト : filter $エラーコード in 申請促すエラー情報.エラーコードリスト :
		// anyMatch $エラーコード = $.勤務実績のエラーアラームコード sort $.処理年月日 DESC fisrt
		
		Optional<EmployeeDailyPerError> targetErrorOpt = listEmployeeDailyPerError.stream().filter(x -> filterErrorCode(x, errorInforApp))
				.sorted(Comparator.comparing(EmployeeDailyPerError::getDate).reversed())
				.findFirst();
		
		//	if $対象エラー.isEmpty()
		if(!targetErrorOpt.isPresent()) {
			//return Optional.empty
			return Optional.empty();
		}
		EmployeeDailyPerError targetError = targetErrorOpt.get();
		//	$申請設定 = require.エラー発生時に呼び出す申請一覧を取得する($対象エラー.勤務実績のエラーアラームコード)
		Optional<ErAlApplication> appSetOpt = require.getAllErAlAppByEralCode(targetError.getErrorAlarmWorkRecordCode().v());
		
		//	if $申請設定.isEmpty()
		if(!appSetOpt.isPresent()) {
			//	return 日別勤怠エラー情報#日別勤怠エラー情報(申請促すエラー情報.エラー種類, 申請促すエラー情報.促すメッセージ, $対象エラー.処理年月日, emptyList())		
			return Optional.ofNullable(new DailyAttdErrorInfo(
					errorInforApp.getCheckErrorType(),
					errorInforApp.getPromptingMessage().map(msg -> msg).orElse(null), 
					targetError.getDate(),
					Collections.emptyList()));
		}
		
		//	return 日別勤怠エラー情報#日別勤怠エラー情報(申請促すエラー情報.エラー種類, 申請促すエラー情報.促すメッセージ, $対象エラー.処理年月日, $申請設定.呼び出す申請一覧)	
		
		return Optional.ofNullable(new DailyAttdErrorInfo(
				errorInforApp.getCheckErrorType(),
				errorInforApp.getPromptingMessage().map(msg -> msg).orElse(null), 
				targetError.getDate(),
				appSetOpt.map(appSet -> appSet.getAppType()).orElse(Collections.emptyList())));

	}

	private static boolean filterErrorCode(EmployeeDailyPerError dailyError, ErrorInformationApplication errorInforApp) {
		
		return errorInforApp.getErrorAlarmCode().stream()
				.filter(x -> x.v().equals(dailyError.getErrorAlarmWorkRecordCode().v())).findFirst().isPresent();
	}

	/**
	 * [prv-5] エラー確認する必要があるか
	 * 
	 * @param require
	 * @param stampMeans 
	 * @param pageNo
	 * @param buttonDisNo
	 * @return boolean
	 */
	private static boolean needCheckError(Require require, StampMeans stampMeans, StampButton stampButton) {

		// if 打刻手段 <> 個人打刻 AND 打刻手段 <> ポータル打刻 AND 打刻手段 <> スマホ打刻
		if (!stampMeans.equals(StampMeans.INDIVITION) && !stampMeans.equals(StampMeans.PORTAL)
				&& !stampMeans.equals(StampMeans.SMART_PHONE)) {
			return false;
		}

		Optional<ButtonSettings> btnSet = Optional.ofNullable(null);

		// if 打刻手段 == 個人打刻
		if (stampMeans.equals(StampMeans.INDIVITION)) {
			// $打刻設定 = require.個人利用の打刻設定を取得する()
			btnSet = require.getStampSetPer().map(set -> set.getButtonSet(stampButton))
					.orElse(Optional.ofNullable(null));
		}
		// if 打刻手段 == ポータル打刻
		if (stampMeans.equals(StampMeans.PORTAL)) {
			// $打刻設定 = require.ポータルの打刻設定を取得する()
			btnSet = require.getSettingPortal()
					.map(set -> set.getDetailButtonSettings(stampButton.getButtonPositionNo()))
					.orElse(Optional.ofNullable(null));
		}
		// if 打刻手段 == スマホ打刻
		if (stampMeans.equals(StampMeans.SMART_PHONE)) {
			// $打刻設定 = require.スマホ打刻の打刻設定を取得する()
			btnSet = require.getSettingSmartPhone().map(set -> set.getDetailButtonSettings(stampButton))
					.orElse(Optional.ofNullable(null));
		}
		// if $ボタン詳細設定.isEmpty()
		if (!btnSet.isPresent()) {
			// return false
			return false;
		}

		// $ボタン詳細設定 = $打刻設定.ボタン詳細設定を取得する (打刻ボタン)
		// if $ボタン詳細設定.isEmpty()
		// làm ở bước trên rồi nhá

		// if $ボタン詳細設定.ボタン種類.打刻種類.empty
		if (!btnSet.get().getButtonType().getStampType().isPresent()) {
		// return false
			return false;
		}
		//	return $ボタン詳細設定.ボタン種類.打刻種類.時刻変更区分.打刻後のエラー確認する必要があるか()	
		return btnSet.get().getButtonType().getStampType().get().getChangeClockArt().checkWorkingOut();
	}

	public static interface Require {
		/**
		 * [R-1] 打刻の申請促す設定を取得する StamPromptAppRepository
		 * 
		 * @param companyId
		 * @return
		 */
		Optional<StampPromptApplication> getStampSet();

		/**
		 * [R-2] 当月の締め期間を取得する ClosureService
		 * 
		 * @param employeeId
		 * @param baseDate
		 * @return
		 */
		DatePeriod findClosurePeriod(String employeeId, GeneralDate baseDate);

		/**
		 * [R-3] システム日付を含む締め期間を取得する FindClosureDateService(tham khao)
		 * 
		 * @param employeeId
		 * @param baseDate
		 * @return
		 */
		Optional<ClosurePeriod> getClosurePeriod(String employeeId, GeneralDate baseDate);

		/**
		 * [R-4] エラー発生時に呼び出す申請一覧を取得する ErAlApplicationRepository
		 * 
		 * @param companyID
		 * @param errorAlarmCode
		 * @return
		 */
		Optional<ErAlApplication> getAllErAlAppByEralCode(String errorAlarmCode);

		/**
		 * [R-5] 社員の日別実績エラー一覧を取得する EmployeeDailyPerErrorRepository
		 * 
		 * @param employeeId
		 * @param datePeriod
		 * @return
		 */
		List<EmployeeDailyPerError> findByPeriodOrderByYmd(String employeeId, DatePeriod datePeriod);

		/**
		 * [R-6] 個人利用の打刻設定を取得する StampSetPerRepository
		 * 
		 * @param companyId
		 * @return
		 */
		Optional<StampSettingPerson> getStampSetPer();

		/**
		 * [R-7] スマホ打刻の打刻設定を取得する
		 * 
		 * @param
		 * @return
		 */
		Optional<SettingsSmartphoneStamp> getSettingSmartPhone();

		/**
		 * [R-8] ポータルの打刻設定を取得する
		 * 
		 * @param
		 * @return
		 */
		Optional<PortalStampSettings> getSettingPortal();

	}
}
