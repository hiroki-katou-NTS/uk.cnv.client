package nts.uk.ctx.at.record.dom.remainingnumber.specialleave.export;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.company.AffCompanyHistImport;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata.ComplileInPeriodOfSpecialLeaveParam;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata.GrantPeriodAtr;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata.InPeriodOfSpecialLeaveResultInfor;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata.SpecialLeaveAggregatePeriodWork;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata.SpecialLeaveGrantWork;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata.SpecialLeaveInfo;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata.SpecialLeaveLapsedWork;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.InterimRemainMngMode;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.ConfirmLeavePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.InterimRemain;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.RemainType;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialholidaymng.interim.InterimSpecialHolidayMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.basicinfo.SpecialLeaveBasicInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.grantremainingdata.SpecialLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service.InforSpecialLeaveOfEmployeeSevice;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service.SpecialHolidayInterimMngData;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagement;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums.UseAtr;
import nts.uk.ctx.at.shared.dom.specialholiday.export.NextSpecialLeaveGrant;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.残数管理.残数管理.特別休暇管理.Export
 *
 */
public class SpecialLeaveManagementService {
	/**
	 * RequestList273 期間内の特別休暇残を集計する
	 * @return
	 */
	public static InPeriodOfSpecialLeaveResultInfor complileInPeriodOfSpecialLeave(
			RequireM5 require,
			//AggregateMonthlyRecordServiceProc.RequireM8 require,
			CacheCarrier cacheCarrier,
			ComplileInPeriodOfSpecialLeaveParam param) {

		// 特別休暇の集計結果情報
		InPeriodOfSpecialLeaveResultInfor outputData = new InPeriodOfSpecialLeaveResultInfor();

		// 「休暇の集計期間から入社前、退職後を除く」を実行する
		DatePeriod aggrPeriod = ConfirmLeavePeriod.sumPeriod(require, cacheCarrier, param.getComplileDate(), param.getSid());
				if (aggrPeriod == null) return outputData;

		// パラメータ月次モード・その他モード
		InterimRemainMngMode interimRemainMngMode = InterimRemainMngMode.of(param.isMode());

		List<InterimSpecialHolidayMng> overwriteInterim = new ArrayList<>();
		param.getInterimSpecialData().stream().forEach(c-> {
			overwriteInterim.add(c);
		});

		// 集計開始日時点の特休情報を作成
		SpecialLeaveInfo specialLeaveInfo = createInfoAsOfPeriodStart(
						require,
						cacheCarrier,
						param.getCid(),
						param.getSid(),
						param.getComplileDate(),
						interimRemainMngMode,
						Optional.of(param.isOverwriteFlg()),
						overwriteInterim,
						Optional.empty(),
						param.getSpecialLeaveCode());


		// 次回特別休暇付与日を計算
//		CalcNextSpecialLeaveGrantDate calcNextSpecialLeaveGrantDate
//			= new CalcNextSpecialLeaveGrantDate();
		List<NextSpecialLeaveGrant> nextSpecialLeaveGrantList
			= CalcNextSpecialLeaveGrantDate.algorithm(
				require,
				cacheCarrier,
				param.getCid(),
				param.getSid(),
				param.getSpecialLeaveCode(),
				Optional.of(param.getComplileDate()));

		// 特別休暇集計期間を作成
		List<SpecialLeaveAggregatePeriodWork> aggregateWork
			= createAggregatePeriod(
					require,
					cacheCarrier,
					param.getCid(),
					param.getSid(),
					param.getSpecialLeaveCode(),
					nextSpecialLeaveGrantList,
					aggrPeriod);

		// アルゴリズム「特別休暇暫定管理データを取得する」を実行する
		SpecialHolidayInterimMngData specialHolidayInterimMngData
			= specialHolidayData(require, param);

		// 特別休暇集計期間でループ
		for (val aggregatePeriodWork : aggregateWork){

			// 特休の消滅・付与・消化
			outputData = specialLeaveInfo.lapsedGrantDigest(
					require,
					param.getCid(),
					param.getSid(),
					aggregatePeriodWork,
					specialHolidayInterimMngData,
					param.getSpecialLeaveCode(),
					outputData);
		}

		// 【渡すパラメータ】 特別休暇情報　←　特別休暇の集計結果．特別休暇情報（期間終了日時点）
		SpecialLeaveInfo specialLeaveInfoEnd = outputData.getAsOfPeriodEnd();

		// マイナス分の特別休暇付与残数を1レコードにまとめる
		Optional<SpecialLeaveGrantRemainingData> remainingShortageData
			= specialLeaveInfoEnd.createLeaveGrantRemainingShortageData();

		// 特別休暇不足分として作成した特別休暇付与データを削除する
		outputData.deleteShortageRemainData();

		// 特別休暇(期間終了日時点)に残数不足の付与残数データを追加
		if ( remainingShortageData.isPresent() ) {
			specialLeaveInfoEnd.getGrantRemainingDataList().add(remainingShortageData.get());
		}

		return outputData;
	}

	/**
	 * 集計開始日時点の特別休暇情報を作成
	 * @param require
	 * @param cacheCarrier
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param aggrPeriod 集計期間（開始日、終了日）
	 * @param mode 実績のみ参照区分
	 * @param isOverWriteOpt 上書きフラグ
	 * @param forOverWriteListOpt　上書き用の暫定管理データ
	 * @param inPeriodOfSpecialLeaveResultInfor 前回の特別休暇の集計結果
	 * @param specialLeaveCode 特別休暇コード
	 * @return 特休情報
	 */
	private static SpecialLeaveInfo createInfoAsOfPeriodStart(
			RequireM5 require,
			CacheCarrier cacheCarrier,
			String companyId,
			String employeeId,
			DatePeriod aggrPeriod,
			InterimRemainMngMode mode,
			Optional<Boolean> isOverWriteOpt,
			List<InterimSpecialHolidayMng> forOverWriteListOpt,
			Optional<InPeriodOfSpecialLeaveResultInfor> inPeriodOfSpecialLeaveResultInfor,
			int specialLeaveCode){

		SpecialLeaveInfo emptyInfo = new SpecialLeaveInfo();
		emptyInfo.setYmd(aggrPeriod.start());

		// 集計開始日時点の前回の特休の集計結果が存在するかチェック
		// 「前回の特休情報」を確認　（前回の特休の集計結果．特休情報（期間終了日の翌日開始時点））
		SpecialLeaveInfo prevSpecialLeaveInfo
			= inPeriodOfSpecialLeaveResultInfor.map(c -> c.getAsOfStartNextDayOfPeriodEnd()).orElse(null);

		// 「開始日」と「特休情報．年月日」を比較
		boolean isSameInfo = false;
		if (prevSpecialLeaveInfo != null){
			if (aggrPeriod.start().equals(prevSpecialLeaveInfo.getYmd())){
				isSameInfo = true;
			}
		}
		if (isSameInfo){
			// 特別休暇付与残数データをもとに特別休暇情報を作成
			// 「前回の特休情報」を取得　→　取得内容をもとに特休情報を作成
			return createInfoFromRemainingData(
					companyId,
					employeeId,
					aggrPeriod.start(),
					prevSpecialLeaveInfo.getGrantRemainingDataList());
		}

		// 休暇残数を計算する締め開始日を取得する
		Optional<GeneralDate> closureStartOpt = Optional.empty();
		{
			// 最新の締め終了日翌日を取得する
			Optional<ClosureStatusManagement> sttMng
				= require.latestClosureStatusManagement(employeeId);
			if (sttMng.isPresent()){
				// 締め開始日
				GeneralDate closureStart = sttMng.get().getPeriod().end();
				if (closureStart.before(GeneralDate.max())){
					closureStart = closureStart.addDays(1);
				}
				closureStartOpt = Optional.of(closureStart);
			}
			else {
				//　社員に対応する締め開始日を取得する
				closureStartOpt = GetClosureStartForEmployee.algorithm(
						require, cacheCarrier, employeeId);
			}
		}

		List<InterimSpecialHolidayMng> interimRemainList
			= new ArrayList<InterimSpecialHolidayMng>();
		if ( !forOverWriteListOpt.isEmpty() ){
			forOverWriteListOpt.forEach(c-> interimRemainList.add(c));
		}

		// 社員の特別休暇情報を取得
		if ( closureStartOpt.isPresent() ){
			List<SpecialLeaveGrantRemainingData> specialLeaveGrantRemainingDataList
				= getEmpSpecialLeaveInfo(
						require,
						cacheCarrier,
						companyId,
						employeeId,
						aggrPeriod,
						mode,
						isOverWriteOpt,
						Optional.ofNullable(interimRemainList),
						inPeriodOfSpecialLeaveResultInfor,
						specialLeaveCode,
						closureStartOpt.get());

		// 特別休暇付与残数データをもとに特別休暇情報を作成
			SpecialLeaveInfo specialLeaveInfo
				= createInfoFromRemainingData(
					companyId,
					employeeId,
					closureStartOpt.get(),
					specialLeaveGrantRemainingDataList);

			return specialLeaveInfo;
		}

		return null;
	}

	/**
	 * 社員の特別休暇情報を取得
	 * @param require
	 * @param cacheCarrier
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param aggrPeriod 集計期間（開始日、終了日）
	 * @param mode 実績のみ参照区分
	 * @param isOverWriteOpt 上書きフラグ
	 * @param forOverWriteListOpt　上書き用の暫定管理データ
	 * @param inPeriodOfSpecialLeaveResultInfor 前回の特別休暇の集計結果
	 * @param specialLeaveCode 特別休暇コード
	 * @param closureStart 締め開始日
	 * @return 特休情報
	 */
	private static List<SpecialLeaveGrantRemainingData> getEmpSpecialLeaveInfo(
			RequireM5 require,
			CacheCarrier cacheCarrier,
			String companyId,
			String employeeId,
			DatePeriod aggrPeriod,
			InterimRemainMngMode mode,
			Optional<Boolean> isOverWriteOpt,
			Optional<List<InterimSpecialHolidayMng>> forOverWriteListOpt,
			Optional<InPeriodOfSpecialLeaveResultInfor> inPeriodOfSpecialLeaveResultInfor,
			int specialLeaveCode,
			GeneralDate closureStart){

		List<SpecialLeaveGrantRemainingData> lstSpeData = new ArrayList<SpecialLeaveGrantRemainingData>();

		// 取得した締め開始日とパラメータ「集計開始日」を比較

		// 締め開始日>=パラメータ「集計開始日」
		if ( closureStart.afterOrEquals(aggrPeriod.start()) ){

			// ドメインモデル「特別休暇付与残数データ」を取得
			// 【条件】
			// 社員ID=パラメータ「社員ID」
			// 特別休暇コード = パラメータ「特別休暇コード」
			// 付与日<=締め開始日
			// 期限日>=締め開始日
			// 期限切れ状態=使用可能

			lstSpeData = require.specialLeaveGrantRemainingData(employeeId, specialLeaveCode,
						aggrPeriod, LeaveExpirationStatus.AVAILABLE);

			return lstSpeData;

		}
		// 締め開始日<パラメータ「集計開始日」
		else if ( closureStart.before(aggrPeriod.start()) ){

			// 開始日までの特別休暇残数を計算

			// パラメータ作成
			ComplileInPeriodOfSpecialLeaveParam paramStart
				= new ComplileInPeriodOfSpecialLeaveParam();

			// 会社ID←パラメータ「会社ID」
			paramStart.setCid(companyId);

			// 社員ID←パラメータ「社員ID」
			paramStart.setSid(employeeId);

			// 集計開始日←取得した「締め開始日」
			// 集計終了日←パラメータ「集計開始日」の前日
			paramStart.setComplileDate(new DatePeriod(closureStart, closureStart.addDays(-1)));

			// 実績のみ参照区分←パラメータ「実績のみ参照区分」
			paramStart.setMode(mode.equals(InterimRemainMngMode.MONTHLY));

			// 基準日←パラメータ「集計開始日」の前日
			paramStart.setBaseDate(closureStart.addDays(-1));

			// 上書きフラグ←パラメータ「上書きフラグ」
			boolean overwriteFlg = false;
			if ( isOverWriteOpt.isPresent() ){
				overwriteFlg = isOverWriteOpt.get();
			}
			paramStart.setOverwriteFlg(overwriteFlg);

			// List<上書き用の暫定管理データ>←パラメータ「List<上書き用の暫定管理データ>」
			if ( forOverWriteListOpt.isPresent() ){
				paramStart.setInterimSpecialData(forOverWriteListOpt.get());
			}

//			// 前回の特別休暇の集計結果←NULL
//			paramStart.setOptBeforeResult(Optional.empty());

			// 特別休暇コード
			paramStart.setSpecialLeaveCode(specialLeaveCode);

			// 特別休暇情報(期間終了日の翌日開始時点)の付与残数データから特別休暇付与残数データを作成
			InPeriodOfSpecialLeaveResultInfor inPeriodOfSpecialLeaveResultInforStart
				= complileInPeriodOfSpecialLeave(
					require, cacheCarrier, paramStart);

			lstSpeData = inPeriodOfSpecialLeaveResultInforStart
					.getAsOfStartNextDayOfPeriodEnd()
					.getGrantRemainingDataList();

			return lstSpeData;
		}
		return lstSpeData;
	}

	/**
	 * 特休集計期間を作成
	 * @param require
	 * @param cacheCarrier
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param nextSpecialLeaveGrantList 次回特休付与リスト
	 * @param aggrPeriod 期間
	 * @return 特休集計期間WORKリスト
	 */
	private static List<SpecialLeaveAggregatePeriodWork> createAggregatePeriod(
			RequireM5 require,
			CacheCarrier cacheCarrier,
			String companyId,
			String employeeId,
			int specialLeaveCode,
			List<NextSpecialLeaveGrant> nextSpecialLeaveGrantList,
			DatePeriod aggrPeriod){

		// 期間終了日翌日
		GeneralDate nextDayOfPeriodEnd = aggrPeriod.end();
		if (nextDayOfPeriodEnd.before(GeneralDate.max())){
			nextDayOfPeriodEnd = nextDayOfPeriodEnd.addDays(1);
		}

		// 消滅日で期間を区切る

		// ドメインモデル「特別休暇付与残数データ」を取得
		List<SpecialLeaveGrantRemainingData> lstSpeData
			= require.specialLeaveGrantRemainingData(
				employeeId, specialLeaveCode,
				aggrPeriod, LeaveExpirationStatus.AVAILABLE);

		// Comparator作成　期限日、付与日
		Comparator<SpecialLeaveGrantRemainingData> comparator =
		  Comparator.comparing(SpecialLeaveGrantRemainingData::getDeadline).thenComparing(SpecialLeaveGrantRemainingData::getGrantDate);

		// ソート処理 　期限日、付与日
		List<SpecialLeaveGrantRemainingData> sortedLstSpeData
			= lstSpeData.stream().sorted(comparator).collect(Collectors.toList());

		// 処理単位分割日リスト
		Map<GeneralDate, SpecialLeaveDividedDayEachProcess> dividedDayMap
			= new HashMap<GeneralDate, SpecialLeaveDividedDayEachProcess>();

		for( SpecialLeaveGrantRemainingData c : sortedLstSpeData ){

			// 期限日
			val deadline = c.getDeadline();

			// 期限日>=開始日 && 期限日<=終了日 が処理対象
			if (!aggrPeriod.contains(deadline)) continue;

			// 消滅情報WORKを作成
			SpecialLeaveLapsedWork specialLeaveLapsedWork = new SpecialLeaveLapsedWork();
			// 消滅情報WORK.期間の開始日に消滅するかどうか←true
			specialLeaveLapsedWork.setLapsedAtr(true);

			// 年月日←期限日の翌日
			GeneralDate nextDayOfDeadLine = deadline;
			if (deadline.before(GeneralDate.max())){
				nextDayOfDeadLine = deadline.addDays(1);
			}

			// ※既に同じ年月日がある場合は、追加せずに消滅情報WORKのみセット
			if ( dividedDayMap.containsKey(nextDayOfDeadLine)){
				SpecialLeaveDividedDayEachProcess specialLeaveDividedDayEachProcess
					= dividedDayMap.get(nextDayOfDeadLine);
				if ( specialLeaveDividedDayEachProcess != null ){
					specialLeaveDividedDayEachProcess.setLapsedWork(specialLeaveLapsedWork);
				}
			} else {
				SpecialLeaveDividedDayEachProcess specialLeaveDividedDayEachProcess
					= new SpecialLeaveDividedDayEachProcess(nextDayOfDeadLine);
				specialLeaveDividedDayEachProcess.setLapsedWork(specialLeaveLapsedWork);
				// リストへ追加
				dividedDayMap.put(nextDayOfDeadLine, specialLeaveDividedDayEachProcess);
			}

		};

		// 付与日で期間を区切る ----------------------------

		// パラメータ「List<次回年休付与>」を取得
		// 【条件】
		// 付与年月日>=パラメータ「開始日」の翌日
		// 付与年月日<=パラメータ「終了日」の翌日
		GeneralDate nextDayStartTmp = aggrPeriod.start();
		if (nextDayStartTmp.before(GeneralDate.max())){
			nextDayStartTmp = nextDayStartTmp.addDays(1);
		}
		final GeneralDate nextDayStart = nextDayStartTmp;

		//期間終了日の翌日を求める
		GeneralDate nextDayEndTmp = aggrPeriod.end();
		if (nextDayEndTmp.before(GeneralDate.max())){
			nextDayEndTmp = nextDayEndTmp.addDays(1);
		}

		final GeneralDate nextDayEnd = nextDayEndTmp;
		List<NextSpecialLeaveGrant> nextSpecialLeaveGrantList_period
			= nextSpecialLeaveGrantList.stream()
				.filter(c -> c.getGrantDate().afterOrEquals(nextDayStart))
				.filter(c -> c.getGrantDate().beforeOrEquals(nextDayEnd))
				.collect(Collectors.toList());

		// 付与情報WORKを作成
		//int grantNumber = 1; // 期間中、何回目の付与かをカウント
		nextSpecialLeaveGrantList_period
			.forEach(c -> {
				SpecialLeaveGrantWork specialLeaveGrantWork = new SpecialLeaveGrantWork();

				// 付与情報WORK.期間の開始日に付与があるかどうか←true
				specialLeaveGrantWork.setGrantAtr(true);
				// 付与情報WORK.特休付与←次回特別休暇付与
				specialLeaveGrantWork.setSpecialLeaveGrant(Optional.of(c));

				// 年月日←次回特別休暇付与．付与年月日
				if ( dividedDayMap.containsKey(c.getGrantDate())){ // すでに追加されているとき
					SpecialLeaveDividedDayEachProcess specialLeaveDividedDayEachProcess
						= dividedDayMap.get(c.getGrantDate());
					specialLeaveDividedDayEachProcess.setGrantWork(specialLeaveGrantWork);
				} else {
					SpecialLeaveDividedDayEachProcess specialLeaveDividedDayEachProcess
						= new SpecialLeaveDividedDayEachProcess(c.getGrantDate());
					specialLeaveDividedDayEachProcess.setGrantWork(specialLeaveGrantWork);
					dividedDayMap.put(c.getGrantDate(), specialLeaveDividedDayEachProcess);
				}
			});

//		// 終了日の翌日で期間を区切る  ----------------------------
//		GeneralDate nextEndDate = aggrPeriod.end().addDays(1);

		// 終了日の処理単位分割日を取得
		// 終了日の期間かどうか　←true
		ArrayList<SpecialLeaveDividedDayEachProcess> sortedList
			= new ArrayList<SpecialLeaveDividedDayEachProcess>();

		GeneralDate date = nextDayEndTmp;

		dividedDayMap.forEach((key, val)->{
			// 【条件】 年月日！＝パラメータ「終了日」の翌日
			if ( !val.getYmd().equals(date) ){
				sortedList.add(val);
			}
		});

		// リストの中で年月日が一番大きい処理単位分割日の終了日の期間かどうか = true
		sortedList.sort((a,b)->b.getYmd().compareTo(a.getYmd())); // 降順
		if (0 < sortedList.size()) {
			sortedList.get(0).getEndDay().setPeriodEndAtr(true);
		}


		// 条件　件数＝０
		// 処理単位分割日に終了日翌日を追加
		// 処理単位分割日.年月日 = 終了日の翌日
		// 終了日の翌日情報WORK.終了日の翌日かどうか = true
		// ※既に同じ年月日がある場合は、追加せずに終了日の翌日の情報のみセット
		if ( dividedDayMap.containsKey(nextDayEndTmp)){
			SpecialLeaveDividedDayEachProcess specialLeaveDividedDayEachProcess
				= dividedDayMap.get(nextDayEndTmp);
			if ( specialLeaveDividedDayEachProcess != null ){
				specialLeaveDividedDayEachProcess.getEndDay().setNextPeriodEndAtr(true);
			}
		} else {
			SpecialLeaveDividedDayEachProcess specialLeaveDividedDayEachProcess
				= new SpecialLeaveDividedDayEachProcess(nextDayEndTmp);
			specialLeaveDividedDayEachProcess.getEndDay().setNextPeriodEndAtr(true);
			// リストへ追加
			dividedDayMap.put(nextDayEndTmp, specialLeaveDividedDayEachProcess);
		}

		// 付与前と付与後の期間を区別する   ----------------------------

		// List<処理単位分割日>をソート
		// 【ソート】
		// 年月日　昇順
		val specialLeaveGrantList4 = new ArrayList<SpecialLeaveDividedDayEachProcess>();
		dividedDayMap.forEach((key, val)->{
			specialLeaveGrantList4.add(val);
		});
		specialLeaveGrantList4.sort((a,b)->a.getYmd().compareTo(b.getYmd()));

		// 付与前か付与後か = 付与前
		GrantPeriodAtr afterGrant = GrantPeriodAtr.BEFORE_GRANT;
		for(SpecialLeaveDividedDayEachProcess c: specialLeaveGrantList4){
			if (c.getGrantWork().isGrantAtr()){ // 付与フラグ
				afterGrant = GrantPeriodAtr.AFTER_GRANT;
			}
			c.setGrantPeriodAtr(afterGrant);
		}

		// 特別休暇集計WORK作成 ----------------------------

		// 「処理単位分割日」をソート
		List<SpecialLeaveDividedDayEachProcess> dividedDayList = new ArrayList<>();
		dividedDayList.addAll(dividedDayMap.values());
		dividedDayList.sort((a, b) -> a.getYmd().compareTo(b.getYmd()));

		// ループ1つ前の情報を保持
		Optional<SpecialLeaveDividedDayEachProcess> specialLeaveDividedDayEachProcess_pre = Optional.empty();

		// 処理単位分割日でループ
		boolean isFirst = true;
		GeneralDate preYmd = null;


		if (dividedDayList.size() <= 0)
			return new ArrayList<>();

		List<SpecialLeaveAggregatePeriodWork> aggregatePeriodWorks = new ArrayList<>();
		SpecialLeaveAggregatePeriodWork firstPeriod = new SpecialLeaveAggregatePeriodWork(
				new DatePeriod(aggrPeriod.start(),dividedDayList.get(0).getYmd().addDays(-1)));
		aggregatePeriodWorks.add(firstPeriod);

		for( SpecialLeaveDividedDayEachProcess c : dividedDayList ){

			if ( isFirst ){
				isFirst = false;
				specialLeaveDividedDayEachProcess_pre = Optional.of(c);
				preYmd = c.getYmd();
				continue;
			}

			new SpecialLeaveLapsedWork();

			// 期間．開始日←「処理単位分割日．年月日」
			// 期間．終了日←次の「処理単位分割日．年月日」の前日
			// 　　　　※次の処理単位分割日がない場合、パラメータ「終了日」の翌日
			// 消滅←「処理単位分割日.消滅情報WORK」
			// 付与←「処理単位分割日.付与情報WORK」
			// 終了日←「処理単位分割日.終了日の翌日情報WORK」
			// 付与前か付与後か←「処理単位分割日.付与前、付与後の期間区分」
			SpecialLeaveAggregatePeriodWork specialLeaveAggregatePeriodWork
				= SpecialLeaveAggregatePeriodWork.of(
					new DatePeriod(preYmd, c.getYmd().addDays(-1)),
//					specialLeaveDividedDayEachProcess_pre.get().isDayBeforePeriodEnd(),
//					specialLeaveDividedDayEachProcess_pre.get().isNextDayAfterPeriodEnd(),
//					specialLeaveDividedDayEachProcess_pre.get().isAfterGrant(),
					specialLeaveDividedDayEachProcess_pre.get().getEndDay(),
					specialLeaveDividedDayEachProcess_pre.get().getLapsedWork(),
					specialLeaveDividedDayEachProcess_pre.get().getGrantWork(),
					specialLeaveDividedDayEachProcess_pre.get().getGrantPeriodAtr());

			aggregatePeriodWorks.add(specialLeaveAggregatePeriodWork);

			specialLeaveDividedDayEachProcess_pre = Optional.of(c);
			preYmd = c.getYmd();
		}

		// 次の処理単位分割日がない場合、パラメータ「終了日」の翌日

		// 「特別休暇集計期間WORK」を作成
		SpecialLeaveLapsedWork specialLeaveLapsedWork
			= new SpecialLeaveLapsedWork();
		specialLeaveLapsedWork.setLapsedAtr(false);

		// 期間．開始日←最後の「処理単位分割日．年月日」
		// 期間．終了日←パラメータ「終了日」の翌日
		SpecialLeaveAggregatePeriodWork specialLeaveAggregatePeriodWork
		= SpecialLeaveAggregatePeriodWork.of(
			new DatePeriod(preYmd, nextDayOfPeriodEnd),
			specialLeaveDividedDayEachProcess_pre.get().getEndDay(),
			specialLeaveDividedDayEachProcess_pre.get().getLapsedWork(),
			specialLeaveDividedDayEachProcess_pre.get().getGrantWork(),
			specialLeaveDividedDayEachProcess_pre.get().getGrantPeriodAtr());

		aggregatePeriodWorks.add(specialLeaveAggregatePeriodWork);


		for(SpecialLeaveAggregatePeriodWork work : aggregatePeriodWorks) {
			if(work.getPeriod().contains(aggrPeriod.end()))
				work.getEndDay().setPeriodEndAtr(true);
			if(work.getPeriod().contains(aggrPeriod.end().addDays(1)))
				work.getEndDay().setNextPeriodEndAtr(true);
		}

		return aggregatePeriodWorks;
	}


	/**
	 * 特休付与残数データから特休情報を作成
	 * @param cId 会社ID
	 * @param employeeId 社員ID
	 * @param ymd 年月日
	 * @param grantRemainingDataList 付与残数データリスト
	 * @return
	 */
	private static SpecialLeaveInfo createInfoFromRemainingData(
			String cId,
			String employeeId,
			GeneralDate ymd,
			List<SpecialLeaveGrantRemainingData> grantRemainingDataList
			){

		SpecialLeaveInfo specialLeaveInfo = new SpecialLeaveInfo();

		Optional<List<SpecialLeaveGrantRemainingData>> grantRemainingDataListOpt
			= Optional.ofNullable(grantRemainingDataList);

		// 特別休暇情報．年月日←パラメータ「年月日」
//		returnInfo.setYmd(aggrPeriod.start());
		specialLeaveInfo.setYmd(ymd);

		// 残数．特別休暇(マイナスあり)をクリア。（Listの要素数を０にする）
		specialLeaveInfo.getRemainingNumber().getSpecialLeaveWithMinus().clear();

		// 特休情報．特休付与情報　←　パラメータ「付与残数データ」
		List<SpecialLeaveGrantRemainingData> targetDatas = new ArrayList<>();
		if ( grantRemainingDataListOpt.isPresent() ) {
			for (val grantRemainingData : grantRemainingDataList){
				if (grantRemainingData.getExpirationStatus() == LeaveExpirationStatus.EXPIRED) continue;
				targetDatas.add(grantRemainingData);
				employeeId = grantRemainingData.getEmployeeId();
			}
		}
		targetDatas.sort((a, b) -> a.getGrantDate().compareTo(b.getGrantDate()));
		specialLeaveInfo.setGrantRemainingDataList(targetDatas);

//		// 特休情報．上限データ　←　パラメータ「上限データ」
//		if (!maxDataOpt.isPresent()) {
//			Integer intNull = null;
//			SpecialLeaveMaxData emptyMaxData = SpecialLeaveMaxData.createFromJavaType(
//					employeeId, intNull, intNull, intNull, intNull);
//			returnInfo.setMaxData(emptyMaxData);
//		}
//		else {
//			returnInfo.setMaxData(maxDataOpt.get());
//		}

		// 特休情報残数を更新
		specialLeaveInfo.updateRemainingNumber(false);

		// 特休情報を返す
		return specialLeaveInfo;
	}

	/**
	 * 特別休暇暫定データを取得する
	 * @param require
	 * @param param
	 * @return 特別休暇暫定データ
	 */
	private static SpecialHolidayInterimMngData specialHolidayData(
		RequireM2 require, ComplileInPeriodOfSpecialLeaveParam param) {

		List<InterimSpecialHolidayMng> lstOutput = new ArrayList<>();

		// パラメータ「実績のみ参照区分」をチェック
		if(param.isMode()) { // 月次モード
			// 月次モードの場合、あらかじめ残数処理の外側で集計期間全部の
			// 暫定Dを作っておくので、ここでは作らない
		} else {
			// ドメインモデル「特別休暇暫定データ」を取得する
			List<InterimRemain> lstInterimMngTmp
				= require.interimRemains(
					param.getSid(),
					param.getComplileDate(),
					RemainType.SPECIAL);

			lstInterimMngTmp.stream().forEach(a -> {

				List<InterimSpecialHolidayMng> lstSpecialData
					= require.interimSpecialHolidayMng(a.getRemainManaID())
						.stream().filter(x -> x.getSpecialHolidayCode() == param.getSpecialLeaveCode())
						.collect(Collectors.toList());
				if(!lstSpecialData.isEmpty()) {
					lstSpecialData.forEach(specialData->{
						specialData.setParentValue(a);
					});
					lstOutput.addAll(lstSpecialData);
				}
			});
		}

		List<InterimSpecialHolidayMng> speHolidayMngTempCreate = new ArrayList<>(lstOutput);

		//INPUT．上書きフラグをチェックする
		if(param.isOverwriteFlg()) {

			// パラメータの「暫定管理データ」をループ
			for (InterimSpecialHolidayMng interimRemain : param.getInterimSpecialData()) {
				// パラメータの「暫定管理データ」と比較して、
				// 以下の項目が全て同じ「特別休暇暫定管理データ」がある場合、重複したものは上書きする
				// ・社員ID
				// ・対象日
				// ・残数種類
				Optional<InterimSpecialHolidayMng> speMngReplace
					= speHolidayMngTempCreate.stream()
						.filter(x -> x.getSID().equals(interimRemain.getSID()))
						.filter(x -> x.getYmd().equals(interimRemain.getYmd()))
						.filter(x -> x.getRemainType().equals(interimRemain.getRemainType()))
						.findFirst();

				if(speMngReplace.isPresent()) {
					//重複する暫定データが取得できた場合は上書きする
					speMngReplace.get().set(interimRemain);
				}
				else {
					// 重複がない「暫定管理データ」の場合、特別休暇暫定管理データのListに追加
					lstOutput.add(interimRemain);
				}
			}
		}
		return new SpecialHolidayInterimMngData(lstOutput);
	}

	/**
	 * 指定期間の使用数を求める
	 * @param lstInterimData
	 * @param dateData
	 * @return
	 */
	private static double useDayFormGrant(List<InterimSpecialHolidayMng> lstInterimData,
			List<InterimRemain> lstInterimMng, DatePeriod dateData) {
		double outputData = 0;
		for (InterimSpecialHolidayMng interimMng : lstInterimData) {
			List<InterimRemain> optInterimMng = lstInterimMng.stream()
					.filter(z -> z.getRemainManaID().equals(interimMng.getSpecialHolidayId()))
					.collect(Collectors.toList());
			if(!optInterimMng.isEmpty()) {
				InterimRemain interimMngData = optInterimMng.get(0);
				//ループ中のドメインモデル「特別休暇暫定データ」．年月日とINPUT．開始日、終了日を比較する
				if(interimMngData.getYmd().afterOrEquals(dateData.start())
						&& interimMngData.getYmd().beforeOrEquals(dateData.end())) {
					//使用数 += ループ中のドメインモデル「特別休暇暫定データ」．特休使用
					outputData += interimMng.getUseDays().isPresent() ? interimMng.getUseDays().get().v() : 0;
				}
			}
		}
		return outputData;
	}

//	/**
//	 * 期限切れの管理データを期限切れに変更する
//	 * @param lstGrantData
//	 * @param baseDate
//	 * @return
//	 */
//	private static DataMngOfDeleteExpired unDigestedDay(List<SpecialLeaveGrantRemainingData> lstGrantData,
//			GeneralDate baseDate, boolean isMode) {
//		double unDisgesteDays = 0;
//		List<SpecialLeaveGrantRemainingData> lstTmp = new ArrayList<>(lstGrantData);
//		for (SpecialLeaveGrantRemainingData grantData : lstGrantData) {
//			//期限切れかチェックする
//			//・モードがその他
//			//INPUT．特別休暇付与残数データ．期限日 >= INPUT．集計終了日
//			//・モードが月次
//			//INPUT．特別休暇付与残数データ．期限日 > INPUT．集計終了日
//			if(grantData.getExpirationStatus() != LeaveExpirationStatus.AVAILABLE) {
//				continue;
//			}
//			if( (!isMode && !grantData.getDeadline().afterOrEquals(baseDate))
//					|| (isMode && !grantData.getDeadline().after(baseDate))) {
//				//未消化数+=「特別休暇数情報」．残数
//				unDisgesteDays += grantData.getDetails().getRemainingNumber().getDayNumberOfRemain().v();
//				//ループ中の「特別休暇付与残数データ」．期限切れ状態=期限切れ
//				lstTmp.remove(grantData);
//				grantData.setExpirationStatus(LeaveExpirationStatus.EXPIRED);
//				lstTmp.add(grantData);
//			}
//		}
//		//return new DataMngOfDeleteExpired(unDisgesteDays, lstTmp);
//		return new DataMngOfDeleteExpired(unDisgesteDays, lstTmp, null);
//	}

	public static interface RequireM1 {

		Optional<SpecialLeaveGrantRemainingData> specialLeaveGrantRemainingData(String specialId);

	}

	public static interface RequireM2 {

		List<InterimRemain> interimRemains(String employeeId, DatePeriod dateData, RemainType remainType);

		List<InterimSpecialHolidayMng> interimSpecialHolidayMng(String mngId);
	}

	public static interface RequireM3 extends LeaveRemainingNumber.RequireM3, InforSpecialLeaveOfEmployeeSevice.RequireM4 {

		/** 特別休暇付与残数データ */
		List<SpecialLeaveGrantRemainingData> specialLeaveGrantRemainingData(String sid, int specialLeaveCode,
				LeaveExpirationStatus expirationStatus,GeneralDate grantDate, GeneralDate deadlineDate);

		/**i 特別休暇付与残数データ */
		List<SpecialLeaveGrantRemainingData> specialLeaveGrantRemainingData(String sid, int speCode,
//				DatePeriod datePriod, GeneralDate startDate, LeaveExpirationStatus expirationStatus);
				DatePeriod datePriod, LeaveExpirationStatus expirationStatus);
	}

	public static interface RequireM4 extends InforSpecialLeaveOfEmployeeSevice.RequireM4, RequireM1 {

	}

	public static interface RequireM5 extends RequireM1, RequireM2, RequireM3, RequireM4,
		LeaveRemainingNumber.RequireM3,nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee.RequireM1, ConfirmLeavePeriod.Require
		{

		/** 特別休暇基本情報 */
		Optional<SpecialLeaveBasicInfo> specialLeaveBasicInfo(String sid, int spLeaveCD, UseAtr use);

		/** 所属会社履歴 */
		List<AffCompanyHistImport> listAffCompanyHistImport(List<String> listAppId, DatePeriod period);

		/** 締め状態管理 */
		Optional<ClosureStatusManagement> latestClosureStatusManagement(String employeeId);
	}

}
