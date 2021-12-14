package nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.text.StringUtil;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.param.AggrResultOfAnnualLeave;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.DailyInterimRemainMngData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.AnnLeaEmpBasicInfoRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.AnnualLeaveEmpBasicInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.CalcNextAnnualLeaveGrantDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnLeaGrantRemDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveRemainHistRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveRemainingHistory;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveTimeRemainHistRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveTimeRemainingHistory;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.CreateInterimAnnualMngData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.InterimRemainMngMode;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.param.AnnualHolidayGrant;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.param.AnnualHolidayGrantInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.param.DailyInterimRemainMngDataAndFlg;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.param.ReferenceAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TempAnnualLeaveMngs;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TmpAnnualHolidayMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TempAnnualLeaveUsedNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TmpDailyLeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.LeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.AggregateMonthlyRecordService;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthlyRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSettingRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureHistory;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.export.NextAnnualLeaveGrant;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.calendar.date.ClosureDate;
@Stateless
public class GetAnnualHolidayGrantInforImpl implements GetAnnualHolidayGrantInfor{
	// 現在
	private static final int NOW = 0;
	// 1年経過時点
	private static final int AFTER_1_YEAR = 1;
	// １ 年以上前（過去）
	private static final int PAST = 2;
	// 以下
	private static final int UNDER = 0;
	// 以上
	private static final int OVER = 1;

	@Inject
	private GetPeriodFromPreviousToNextGrantDate periodGrantInfor;
	@Inject
	private AnnualLeaveRemainHistRepository annualRepo;
	@Inject
	private TmpAnnualHolidayMngRepository annualRepository;
	@Inject
	private AnnualLeaveTimeRemainHistRepository annTimeRemainHisRepo;
	/** 月別実績の勤怠時間 */
	@Inject
	private AttendanceTimeOfMonthlyRepository attendanceTimeOfMonthlyRepo;
	@Inject
	private AnnLeaGrantRemDataRepository annLeaRemRepo;
	@Inject
	private RecordDomRequireService requireService;
	@Inject
	private AnnLeaEmpBasicInfoRepository annLeaEmpBasicInfoRepository;
	@Inject
	private WorkingConditionItemRepository workingConditionItemRepository;
	@Inject
	private AnnualPaidLeaveSettingRepository annualPaidLeaveSettingRepository;

	/**
	 * 処理概要： 指定した年月の付与残数情報と、指定年月を含む前回付与日～次回付与日までの使用数を含めた残数を取得
	 * @param 会社ID - cid
	 * @param 社員ID - sid
	 * @param 参照先区分（実績のみ or 予定・申請含む） - referenceAtr (actual results only or schedule / application included)
	 * @param 指定年月 （対象期間区分が１年経過時点の場合、NULL） - ym(NULL when the target period classification is after 1 year) - print date
	 * @param 基準日- ymd Base date - Reference date - Designated date
	 * @param 対象期間区分（現在/１年経過時点/過去）
	 * @param １年経過用期間(From-To)
	 * @return <output>年休付与情報 - Annual leave grant information
	 * @return 抽出対象社員（true(def)、false） - Employees to be extracted (true (def), false)
	 */
	@Override
	public GetAnnualHolidayGrantInforDto getAnnGrantInfor(String cid, String sid, ReferenceAtr referenceAtr,
			YearMonth ym, GeneralDate ymd, Integer periodOutput, Optional<DatePeriod> fromTo, boolean doubletrack,
			boolean exCondition,int exConditionDays, int exComparison) {
		val require = requireService.createRequire();
		val cacheCarrier = new CacheCarrier();

		GetAnnualHolidayGrantInforDto getAnnualHolidayGrantInforDto = new GetAnnualHolidayGrantInforDto();

		// 抽出対象社員←true（対象社員である）
		getAnnualHolidayGrantInforDto.setEmployeeExtracted(true);
		//指定した月を基準に、前回付与日から次回付与日までの期間を取得 - 1 2 3
		Optional<GrantPeriodDto> optPeriod = periodGrantInfor.getPeriodGrantDate(cid, sid, ym, ymd, periodOutput, fromTo);
		if(!optPeriod.isPresent()) {
			getAnnualHolidayGrantInforDto.setAnnualHolidayGrantInfor(Optional.empty());
			return getAnnualHolidayGrantInforDto;
		}
		// 取得した期間
		DatePeriod period = optPeriod.get().getPeriod();
		AnnualHolidayGrantInfor output = new AnnualHolidayGrantInfor(new ArrayList<>(),period, optPeriod.get().getNextGrantDate().orElse(null), sid, Optional.empty());
		// 社員に対応する処理締めを取得する
		Closure closureOfEmp = ClosureService.getClosureDataByEmployee(require, cacheCarrier, sid, ymd);
		// 指定月の締め開始日を取得 - 3 4
		// 対象期間区分が１年経過時点の場合、指定月←取得した期間．開始日の年月部分
		YearMonth ymStartDateByClosure = periodOutput == AFTER_1_YEAR ? period.start().yearMonth() : ym;
		Optional<GeneralDate> optStartDate = this.getStartDateByClosure(sid, ymStartDateByClosure, closureOfEmp.getClosureMonth().getProcessingYm(), ymd);
		if(!optStartDate.isPresent()) {
			getAnnualHolidayGrantInforDto.setAnnualHolidayGrantInfor(Optional.ofNullable(output));
			return getAnnualHolidayGrantInforDto;
		}
		//<<Value Object>> 締め開始日- Closing start date
		GeneralDate startDate = optStartDate.get();
		// 期間内の年休使用明細を取得する - 4
		List<DailyInterimRemainMngDataAndFlg> lstUseInfor = this.lstRemainData(cid,
				sid,
				new DatePeriod(startDate, period.end()),
				referenceAtr);
		List<DailyInterimRemainMngData> lstRemainData = lstUseInfor.stream().map(x -> {
			return x.getData();
		}).collect(Collectors.toList());
		List<TempAnnualLeaveMngs> lstTmpAnnual = new ArrayList<>();
		for (DailyInterimRemainMngData remainMng : lstRemainData) {
			remainMng.getAnnualHolidayData().forEach(c->lstTmpAnnual.add(c));
			/*InterimRemain remainData = remainMng.getRecAbsData()
					.stream()
					.filter(x -> x.getRemainManaID().equals(annData.getRemainManaID()))
					.collect(Collectors.toList()).get(0);*/

		}

		//過去月集計モードを判断する
		boolean isPastMonth = this.determinePastMonth(periodOutput, ym, closureOfEmp);

		//期間中の年休残数を取得 - 5
		// 年月←INPUT．指定年 ------対象期間区分が１年経過時点の場合、年月←取得した期間．開始日の年月部分
		Optional<AggrResultOfAnnualLeave> optAnnualLeaveRemain = GetAnnLeaRemNumWithinPeriodProc
				.algorithm(require, cacheCarrier, cid,
					sid,
					period,
					InterimRemainMngMode.MONTHLY,
					startDate,
//					false,
					false,
					Optional.of(true),//上書きフラグ
					Optional.of(lstTmpAnnual), //上書き用の暫定年休管理データ
					Optional.empty(),//前回の年休の集計結果
//					Optional.of(true),//集計開始日を締め開始日とする
//					Optional.of(false), //不足分付与残数データ出力区分
//					Optional.of(isPastMonth),//過去月集計モード
//					Optional.of(ym), //年月
//					Optional.of(new DatePeriod(startDate, period.end())));
					Optional.of(isPastMonth),//過去月集計モード
					Optional.of(ymStartDateByClosure),
					Optional.empty()); //年月
		if(!optAnnualLeaveRemain.isPresent()) {
			getAnnualHolidayGrantInforDto.setAnnualHolidayGrantInfor(Optional.ofNullable(output));
			return getAnnualHolidayGrantInforDto;
		}
		AggrResultOfAnnualLeave annualLeaveRemain = optAnnualLeaveRemain.get();
		// 年休付与残数データを年休付与情報に変換
		List<AnnualLeaveGrantRemainingData> remainDataList = annualLeaveRemain.getAsOfPeriodEnd().getGrantRemainingNumberList()
				.stream().filter(data -> !StringUtil.isNullOrEmpty(data.getLeaveID(), false)).collect(Collectors.toList());
		output.setLstGrantInfor(remainDataList.stream()
				.map(AnnualHolidayGrant::fromData)
				.collect(Collectors.toList()));
		//指定年月の締め日を取得
		List<ClosureHistory> closureHistories = closureOfEmp.getClosureHistories().stream()
				.filter(x -> x.getStartYearMonth().lessThanOrEqualTo(ymStartDateByClosure) && x.getEndYearMonth().greaterThanOrEqualTo(ymStartDateByClosure))
				.collect(Collectors.toList());

		//ダブルトラック開始日を取得する
		Optional<GeneralDate> getDateDoubleTrack = this.getDoubleTrackStartDate(period.start(), output.getLstGrantInfor(), doubletrack);

		//指定月時点の使用数を計算 - 6
		List<AnnualLeaveGrantRemainingData> lstAnnRemainHis = this.lstRemainHistory(sid, remainDataList, getDateDoubleTrack.orElse(period.start()));
		if(!lstAnnRemainHis.isEmpty()) {
			List<AnnualHolidayGrant> lstAnnHolidayGrant = lstAnnRemainHis.stream()
					.map(AnnualHolidayGrant::fromData).collect(Collectors.toList());
			output.setLstGrantInfor(lstAnnHolidayGrant.stream()
					.filter(data -> Objects.nonNull(data.getYmd()))
					.sorted(Comparator.comparing(AnnualHolidayGrant::getYmd))
					.collect(Collectors.toList()));
		}

		//前回付与日～INPUT．指定年月の間で期限が切れた付与情報を取得 - 7
		GeneralDate dateInforFormPeriod = getDateDoubleTrack.isPresent() ? getDateDoubleTrack.get() : period.start();
		closureHistories.forEach(x -> {
			List<AnnualHolidayGrant> grantInforFormPeriod = this.grantInforFormPeriod(sid, ymStartDateByClosure, x.getClosureId(),
					x.getClosureDate(), new DatePeriod(dateInforFormPeriod, startDate.addDays(-1)), isPastMonth);
			if(!grantInforFormPeriod.isEmpty()) {
				output.getLstGrantInfor().addAll(grantInforFormPeriod);
			}
		});
		output.getLstGrantInfor().sort(Comparator.comparing(AnnualHolidayGrant::getYmd));

		// 対象期間区分をチェックする

		if(periodOutput == AFTER_1_YEAR) {
			//条件に合わない<OUTPUT>年休付与を削除する
			output.getLstGrantInfor().removeIf(i -> i.getYmd().before(dateInforFormPeriod) || i.getYmd().after(period.end()));
		}

		getAnnualHolidayGrantInforDto.setAnnualHolidayGrantInfor(Optional.of(output));
		//抽出条件_チェック(A5_7)をチェックする
		List<AnnualHolidayGrant> newGrant = getAnnualHolidayGrantInforDto.getAnnualHolidayGrantInfor()
				.map(AnnualHolidayGrantInfor::getLstGrantInfor).orElse(Collections.emptyList());
		if (exCondition) {
			//アルゴリズム「抽出条件での絞り込みを行う」を実行する
			boolean getByExtrac = this.getByExtractionConditions(sid, period.start(), newGrant, exConditionDays, exComparison);
			if (!getByExtrac) {
				getAnnualHolidayGrantInforDto.setAnnualHolidayGrantInfor(Optional.empty());
				getAnnualHolidayGrantInforDto.setEmployeeExtracted(getByExtrac);
			}
		}
		// 年休付与情報に 取得した期間、ダブルトラック開始日をセットする
		if (getAnnualHolidayGrantInforDto.getAnnualHolidayGrantInfor().isPresent()) {
			getAnnualHolidayGrantInforDto.getAnnualHolidayGrantInfor().get()
					.setDoubleTrackStartDate(getDateDoubleTrack);
		}

		//年休付与情報を返す
		return getAnnualHolidayGrantInforDto;
	}

	/**
	 * 指定月の締め開始日を取得
	 * @param sid
	 * @param ym 指定月
	 * @param processingYm 締めの当月
	 * @param ymd 基準日
	 * @return 締め開始日
	 */
	public Optional<GeneralDate> getStartDateByClosure(String sid, YearMonth ym, YearMonth processingYm, GeneralDate ymd) {
		val require = requireService.createRequire();
		val cacheCarrier = new CacheCarrier();

		//INPUT．指定月が当月かどうかを判断する
		if(ym.greaterThanOrEqualTo(processingYm)) {
			//社員に対応する締め期間を取得する
			DatePeriod period = ClosureService.findClosurePeriod(require, cacheCarrier, sid, ymd);
			return Optional.of(period.start());
		}
		//ドメインモデル「年休付与残数履歴データ」を取得
		List<AnnualLeaveRemainingHistory> lstAnn = annualRepo.getInfoBySidAndYM(sid, ym)
				.stream().sorted((a,b)
						-> GeneralDate.ymd(a.getYearMonth().year(), a.getYearMonth().month(), a.getClosureDate().getClosureDay().v())
						.compareTo(GeneralDate.ymd(b.getYearMonth().year(), b.getYearMonth().month(), b.getClosureDate().getClosureDay().v())))
				.collect(Collectors.toList());
		//指定した年月の期間を算出する
		if(lstAnn.isEmpty()) {
			return Optional.empty();
		}
		AnnualLeaveRemainingHistory annRemainHisInfor = lstAnn.get(0);
		DatePeriod datePeriodClosure = ClosureService.getClosurePeriod(require, annRemainHisInfor.getClosureId().value, annRemainHisInfor.getYearMonth());
		return Optional.ofNullable(datePeriodClosure.start());
	}

	@Override
	public List<DailyInterimRemainMngDataAndFlg> lstRemainData(String cid, String sid, DatePeriod datePeriod,
			ReferenceAtr referenceAtr) {
		val require = requireService.createRequire();
		val cacheCarrier = new CacheCarrier();

		List<DailyInterimRemainMngDataAndFlg> lstOutputData = new ArrayList<>();
		//社員に対応する締め開始日を取得する
		Optional<GeneralDate> startDateOpt = GetClosureStartForEmployee.algorithm(require, cacheCarrier, sid);
		if(!startDateOpt.isPresent()) {
			return lstOutputData;
		}
		GeneralDate startDate = startDateOpt.get();

		//参照先区分を判断
		if(referenceAtr == ReferenceAtr.RECORD) {
			//日別実績を取得 INPUT．期間．開始日 <= 年月日 <= INPUT．期間．終了日
			lstOutputData = this.getAnnualHolidayRemainData(require, cacheCarrier, cid, sid, datePeriod);
		} else {
			//INPUT．開始と締め開始日を比較
			if(datePeriod.start().before(startDate)) {
				//日別実績を取得 INPUT．期間．開始日 <= 年月日 <= 取得した締め開始日 ー 1日
				lstOutputData = this.getAnnualHolidayRemainData(require, cacheCarrier, cid, sid, new DatePeriod(datePeriod.start(), startDate.addDays(-1)));
			}
			//暫定年休管理データを取得 締め開始日 <= 対象日 < INPUT．期間．終了日
			List<TempAnnualLeaveMngs> lstTmpAnnual = annualRepository.getBySidPeriod(sid, new DatePeriod(startDate, datePeriod.end()));
			for (TempAnnualLeaveMngs x : lstTmpAnnual) {
				DailyInterimRemainMngData remainMng = DailyInterimRemainMngData.createEmpty(null);
				remainMng.setRecAbsData(Arrays.asList(x));
				remainMng.getAnnualHolidayData().add(x);
				DailyInterimRemainMngDataAndFlg outData = new DailyInterimRemainMngDataAndFlg(remainMng, false);
				lstOutputData.add(outData);
			}
		}

		// 年休フレックス補填分の暫定年休管理データを作成
		lstOutputData = getAnnualHolidayInterimFlexTime(sid, datePeriod, lstOutputData);

		lstOutputData = lstOutputData.stream().filter(x -> !x.getData().getAnnualHolidayData().isEmpty()).collect(Collectors.toList());
		return lstOutputData;
	}
	/**
	 * 年休フレックス補填分の暫定年休管理データを作成
	 * @param sid
	 * @param datePeriod
	 * @param lstOutputData
	 * @return
	 */
	private List<DailyInterimRemainMngDataAndFlg> getAnnualHolidayInterimFlexTime(String sid, DatePeriod datePeriod,
			List<DailyInterimRemainMngDataAndFlg> lstOutputData) {
		// 「月別実績の勤怠時間」を取得
		val attendanceTimes = this.attendanceTimeOfMonthlyRepo.findByPeriodIntoEndYmd(sid, datePeriod);
		List<DailyInterimRemainMngData> lstFlex = new ArrayList<>();
		for (val attendanceTime : attendanceTimes){

			// 月別実績の勤怠時間からフレックス補填の暫定年休管理データを作成する
			val compensFlexOpt = CreateInterimAnnualMngData.ofCompensFlex(
					attendanceTime, attendanceTime.getDatePeriod().end());
			if (compensFlexOpt.isPresent()) {
				lstFlex.add(compensFlexOpt.get());
			}
		}
		for (DailyInterimRemainMngData x : lstFlex) {
			if (x.getAnnualHolidayData().isEmpty()) continue;
			double usedDays = x.getAnnualHolidayData().stream()
					.filter(c->c.getUsedNumber().isUseDay())
					.map(c->c.getUsedNumber().getUsedDayNumber().map(mapper->mapper.v()).orElse(0.0))
					.findFirst().orElse(0.0);
			if(usedDays <= 0) {
				continue;
			}


			TempAnnualLeaveMngs annualInterim = x.getAnnualHolidayData().stream().filter(c->c.getUsedNumber().isUseDay()).findFirst().orElse(null);
			if(annualInterim==null) {
				continue;
			}
			if(usedDays <= 1.0) {
				lstOutputData.add(new DailyInterimRemainMngDataAndFlg(x, true));
				continue;
			}
			for(double i = 0; usedDays - i > 0; i++) {
				DailyInterimRemainMngData flexTmp = new DailyInterimRemainMngData();
				flexTmp.setRecAbsData(x.getRecAbsData());
				TempAnnualLeaveMngs annualInterimTmp = new TempAnnualLeaveMngs(
						annualInterim.getRemainManaID(),
						annualInterim.getSID(),
						annualInterim.getYmd(),
						annualInterim.getCreatorAtr(),
						annualInterim.getRemainType(),
						annualInterim.getWorkTypeCode(),
						annualInterim.getUsedNumber(),
						annualInterim.getAppTimeType()
						);
//				annualInterimTmp.setAnnualId(annualInterim.getAnnualId());
				annualInterimTmp.setWorkTypeCode(annualInterim.getWorkTypeCode());

				val usedNumber = new TempAnnualLeaveUsedNumber(
						Optional.of(new TmpDailyLeaveUsedDayNumber(usedDays - i >= 1 ? 1.0 : 0.5)),
						Optional.empty());
				annualInterimTmp.setUsedNumber(usedNumber);

				flexTmp.getAnnualHolidayData().add(annualInterimTmp);

				lstOutputData.add(new DailyInterimRemainMngDataAndFlg(flexTmp, true));
			}
		}
		return lstOutputData;
	}

	private List<DailyInterimRemainMngDataAndFlg> getAnnualHolidayRemainData(
			RecordDomRequireService.Require require, CacheCarrier cacheCarrier,
			String cid, String sid, DatePeriod datePeriod) {

		List<DailyInterimRemainMngDataAndFlg> lstOutputData = new ArrayList<>();

		//Workを考慮した月次処理用の暫定残数管理データを作成する
		List<DailyInterimRemainMngData> mapRemainData = AggregateMonthlyRecordService
				.mapInterimRemainData(require, cacheCarrier, cid, sid, datePeriod);
		if(mapRemainData != null) {
			for (DailyInterimRemainMngData y : mapRemainData) {
				if(!y.getAnnualHolidayData().isEmpty()) {
					DailyInterimRemainMngDataAndFlg outData = new DailyInterimRemainMngDataAndFlg(y, true);
					lstOutputData.add(outData);
				}
			}
		}
		return lstOutputData;
	}

	// 指定月時点の使用数を計算 - 6
	@Override
	public List<AnnualLeaveGrantRemainingData> lstRemainHistory(String sid,
			List<AnnualLeaveGrantRemainingData> lstAnnRemainHis, GeneralDate ymd) {
		// 付与時点の残数履歴データを取得
		List<AnnualLeaveTimeRemainingHistory> annTimeData = annTimeRemainHisRepo.findBySid(sid, ymd);
		if (annTimeData.isEmpty()) {
			return lstAnnRemainHis;
		}
		// 以下の条件で取得したドメインの中で一番大きな付与処理日を持つドメインを全て取得する。
		Map<DatePeriod, AnnualLeaveTimeRemainingHistory> histMap = annTimeData.stream()
				.collect(Collectors.groupingBy(hist -> new DatePeriod(hist.getGrantDate(), hist.getDeadline())))
				.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
						.max(Comparator.comparing(AnnualLeaveTimeRemainingHistory::getGrantProcessDate)).orElse(null)));
		// 付与残数データから使用数を消化する
		return lstAnnRemainHis.stream().map(data -> {
			AnnualLeaveTimeRemainingHistory tmp = histMap.get(new DatePeriod(data.getGrantDate(), data.getDeadline()));
			if (tmp == null) {
				return data;
			}
			List<LeaveGrantRemainingData> results = GetAnnualLeaveUsedNumberFromRemDataService
					.getAnnualLeaveGrantRemainingData(AppContexts.user().companyId(), sid,
							new ArrayList<>(Arrays.asList((LeaveGrantRemainingData) data)), tmp.getDetails().getUsedNumber(),
							new RequireImpl(workingConditionItemRepository, annualPaidLeaveSettingRepository));
			if (results.isEmpty()) {
				return null;
			}
			return (AnnualLeaveGrantRemainingData) results.get(0);
		}).filter(Objects::nonNull).map(data -> (AnnualLeaveGrantRemainingData) data).collect(Collectors.toList());
	}

	//前回付与日～INPUT．指定年月の間で期限が切れた付与情報を取得 - 7
	@Override
	public List<AnnualHolidayGrant> grantInforFormPeriod(String sid, YearMonth ym,  ClosureId closureID,
			ClosureDate closureDate, DatePeriod period, boolean isPastMonth) {
		List<AnnualLeaveGrantRemainingData> lstAnnLeaRem = new ArrayList<>();
		//INPUT．過去月集計モードを確認
		if (isPastMonth) {
			// ドメインモデル「年休付与残数履歴データ」を取得
			lstAnnLeaRem = annualRepo
					.getInfoByExpStatus(sid, ym, closureID, closureDate, LeaveExpirationStatus.EXPIRED, period)
					.stream()
					.map(AnnualLeaveGrantRemainingData::createFromHistory)
					.sorted(Comparator.comparing(AnnualLeaveGrantRemainingData::getGrantDate))
					.collect(Collectors.toList());
		} else {
			// ドメインモデル「年休付与残数データ」を取得
			lstAnnLeaRem = annLeaRemRepo.findByExpStatus(sid, LeaveExpirationStatus.EXPIRED, period)
					.stream()
					.sorted(Comparator.comparing(AnnualLeaveGrantRemainingData::getGrantDate))
					.collect(Collectors.toList());
		}
		// 指定月時点の使用数を計算指定月時点の使用数を計算 - 6
		List<AnnualLeaveGrantRemainingData> lstAnnRemainHis = this.lstRemainHistory(sid, lstAnnLeaRem, period.start());
		// 取得した年休付与残数データをOUTPUTのクラスに移送
		return lstAnnRemainHis.stream().map(AnnualHolidayGrant::fromData).collect(Collectors.toList());
	}

	/**
	 * ダブルトラック開始日を取得する
	 * @param 取得した期間．開始日(前回付与日) - Acquisition period. Start date (previous grant date)
	 * @param 年休付与(List) - Annual leave granted (List)
	 * @return ダブルトラック開始日 - double track start date
	 */
	private Optional<GeneralDate> getDoubleTrackStartDate(GeneralDate lastGrantDate, List<AnnualHolidayGrant> lstGrant, boolean doubletrack) {
		// A7_2（ダブルトラックの場合に、対象期間を広げで取得明細を表示する。）をチェックする - check A7_2 ( is doubleTrack )
		if(doubletrack) {
			// 期間開始日←前回付与日－１年＋１日 --- Period start date ← Last grant date - 1 year + 1 day
			GeneralDate startDate = lastGrantDate.addDays(+1).addYears(-1);
			// 期間終了日←前回付与日－１日--- Period end date ← Last grant date - 1 day
			GeneralDate endDate = lastGrantDate.addDays(-1);

			List<GeneralDate> generalDateGrant = lstGrant.stream().map(i -> i.getYmd()).sorted((a,b) -> a.compareTo(b)).collect(Collectors.toList());
			// 期間開始日 ≦ 付与日 ≦ 期間終了日に合致する付与日を求める - Period start date ≤ grant date ≤ find the grant date that matches the period end date
			List<GeneralDate> getDbTrackDate = new ArrayList<GeneralDate>();
			for (GeneralDate gd : generalDateGrant) {
				if (startDate.beforeOrEquals(gd) && gd.beforeOrEquals(endDate)) {
					getDbTrackDate.add(gd);
				}
			}

			if (getDbTrackDate.isEmpty()) {
				return Optional.empty();
			}

			// 合致した付与日の内、一番小さな日をダブルトラック開始日として返す - Returns the smallest of the matching grant dates as the double track start date
			// được hiểu là lấy ra cái ngày xác định nhỏ nhất . vì mục đích của đường đôi này tìm ra cái ngày nhỏ nhất để làm mốc start date, có thể check lai nếu sai
			return getDbTrackDate.stream().min(GeneralDate::compareTo);
		}
		return Optional.empty();

	}

	/**
	 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.残数管理.定期的に付与する休暇.年休管理.Export.Query.[NO.550]年休付与情報を取得.アルゴリズム.抽出条件での絞り込みを行う.抽出条件での絞り込みを行う
	 * @param employeeId - 社員ID
	 * @param lastGrantDate - 前回年休付与日
	 * @param lstHolidayGrant - 年休付与(List)
	 * @param exConditionDays
	 * @param exComparison
	 * @return 絞り込み結果 - true：対象社員である - false：対象社員ではない
	 */
	private boolean getByExtractionConditions(String employeeId, GeneralDate lastGrantDate, List<AnnualHolidayGrant> lstHolidayGrant, int exConditionDays, int exComparison ) {
		val require = requireService.createRequire();
		val cacheCarrier = new CacheCarrier();
		String companyId = AppContexts.user().companyId();
		// アルゴリズム「年休社員基本情報を取得する」を実行し、年休付与基準日を取得する
		Optional<AnnualLeaveEmpBasicInfo> annualLeaveEmpBasicInfo = annLeaEmpBasicInfoRepository.get(employeeId);
		if (!annualLeaveEmpBasicInfo.isPresent()) {
			// false：対象社員ではない - Not a target employee
			return false;
		}
		// 次回年休付与を計算 - Calculate the next annual leave grant
		List<NextAnnualLeaveGrant> annualLeaveGrant = CalcNextAnnualLeaveGrantDate.algorithm(
							require, cacheCarrier, companyId, employeeId, Optional.empty());
		if (annualLeaveGrant.isEmpty()) {
			// false：対象社員ではない - Not a target employee
			return false;
		}
		// INPUT．年休付与(List)を付与日でソートする
		lstHolidayGrant.stream().map(i -> i.getYmd()).sorted((a,b) -> a.compareTo(b)).collect(Collectors.toList());
		for (AnnualHolidayGrant grant : lstHolidayGrant) {
			// INPUT．前回年休付与日　≦　　INPUT．年休付与(i)．付与日
			if (lastGrantDate.beforeOrEquals(grant.getYmd())) {
				// 抽出条件_比較条件(A5_4)をチェックする
				// 以下
				if (exComparison == UNDER) {
					// 抽出条件_日数(A5_2)がゼロかチェックする - Check if the extraction condition_days (A5_2) is zero
					if(exConditionDays == 0) {
						//true：ゼロの場合
						//年休付与基準日＜次回年休付与日　かつ　(次回年休付与日－年休付与基準日)が１年以上かどうかチェックする
						//Check if the annual leave grant date <next annual leave grant date and (next annual leave grant date-annual leave grant reference date) is one year or more
						for (NextAnnualLeaveGrant nextAnnualLeave : annualLeaveGrant ) {
							if(annualLeaveEmpBasicInfo.get().getGrantRule().getGrantStandardDate().afterOrEquals(nextAnnualLeave.getGrantDate()) ||
									nextAnnualLeave.getGrantDate().differenceFrom(annualLeaveEmpBasicInfo.get().getGrantRule().getGrantStandardDate()).years() < 1) {
								return false;
							}
						}
					}
					// INPUT．年休付与(i)．付与数　≦　抽出条件_日数(A5_2)
					// INPUT. Annual leave granted (i). Number of grants ≤ Extraction condition_Days (A5_2)
					if (grant.getGrantDays() <= exConditionDays) {
						// 戻り値：trueで返す
						return true;
					}
				}
				// 以上
				if (exComparison == OVER) {
					// INPUT．年休付与(i)．付与数　≧　抽出条件_日数(A5_2)
					if (grant.getGrantDays() >= exConditionDays) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 過去月集計モードを判断する
	 */
	private boolean determinePastMonth(int periodOutput, YearMonth ym, Closure closure) {
		switch (periodOutput) {
		// 対象期間区分が現在の場合
		case NOW:
			return false;
		// 対象期間区分が１年経過時点の場合
		case AFTER_1_YEAR:
			return true;
		// 対象期間区分がnull or 過去の場合
		case PAST:
		default:
			return ym.lessThan(closure.getClosureMonth().getProcessingYm());
		}
	}

	@AllArgsConstructor
	public class RequireImpl implements GetAnnualLeaveUsedNumberFromRemDataService.RequireM3 {

		private final WorkingConditionItemRepository workingConditionItemRepository;

		private final AnnualPaidLeaveSettingRepository annualPaidLeaveSettingRepository;

		@Override
		public AnnualPaidLeaveSetting annualPaidLeaveSetting(String companyId) {
			return annualPaidLeaveSettingRepository.findByCompanyId(companyId);
		}

		@Override
		public Optional<WorkingConditionItem> workingConditionItem(String employeeId, GeneralDate baseDate) {
			return workingConditionItemRepository.getBySidAndStandardDate(employeeId, baseDate);
		}

	}
}
