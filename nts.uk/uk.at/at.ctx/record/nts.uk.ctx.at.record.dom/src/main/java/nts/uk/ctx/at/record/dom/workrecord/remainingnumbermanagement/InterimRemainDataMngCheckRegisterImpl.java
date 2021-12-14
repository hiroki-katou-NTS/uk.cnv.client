package nts.uk.ctx.at.record.dom.workrecord.remainingnumbermanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.childcare.ChildCareNurseRequireImplFactory;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata.ComplileInPeriodOfSpecialLeaveParam;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.export.SpecialLeaveManagementService;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.NumberCompensatoryLeavePeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.AbsRecMngInPeriodRefactParamInput;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.CompenLeaveAggrResult;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimAbsMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.ApplicationType;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.DailyInterimRemainMngData;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.EarchInterimRemainCheck;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimEachData;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainCheckInputParam;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainCreateDataInputPara;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainOffPeriodCreateData;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.RemainErrors;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.RemainInputParam;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.require.RemainNumberTempRequireService;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TempAnnualLeaveMngs;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.NumberRemainVacationLeaveRangeProcess;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.BreakDayOffRemainMngRefactParam;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.InterimRemain;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.CreateAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.RemainType;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.RequiredDay;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.UnOffsetDay;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.care.interimdata.TempCareManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.childcare.interimdata.TempChildCareManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.interim.TmpResereLeaveMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialholidaymng.interim.InterimSpecialHolidayMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service.SpecialLeaveError;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.CompanyHolidayMngSetting;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.service.AnnualLeaveErrorSharedImport;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.service.GetAnnLeaRemNumWithinPeriodSharedImport;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.service.ReserveLeaveErrorImport;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.breakinfo.FixedManagementDataMonth;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationRepository;
//import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata.ComplileInPeriodOfSpecialLeaveParam;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

@Stateless
public class InterimRemainDataMngCheckRegisterImpl implements InterimRemainDataMngCheckRegister {

	@Inject
	private GetAnnLeaRemNumWithinPeriodSharedImport annualService;
//	@Inject
//	private ComplileInPeriodOfSpecialLeaveSharedImport specialLeaveService;

	/** REQUIRE対応 */
	@Inject
	private RemainNumberTempRequireService requireService;
	
	@Inject
	private RecordDomRequireService recordDomrequireService;

	@Inject
	private NumberRemainVacationLeaveRangeProcess numberRemainVacationLeaveRangeProcess;
	
	@Inject
    private ChildCareNurseRequireImplFactory childCareNurseRequireImplFactory;
	
	@Inject
    private RecordDomRequireService recordDomRequireService;
	
	@Inject
	private RemainingNumberCheck remainingNumberCheck;
	
	@Inject
	private SpecialHolidayRepository specialHolidayRepository;
	
	@Inject
	private RemainChildCareCheck remainChildCareCheck;
	
	@Inject
	private RemainLongTermCareCheck remainLongTermCareCheck;

	@Override
	public EarchInterimRemainCheck checkRegister(InterimRemainCheckInputParam inputParam) {
		// 代休不足区分、振休不足区分、年休不足区分、積休不足区分、特休不足区分、公休不足区分、超休不足区分、子の看護不足区分、介護不足区分をfalseにする(初期化) 
		EarchInterimRemainCheck outputData = new EarchInterimRemainCheck(false, false, false, false, false, false,
				false, false, false);
		
		// 各残数をチェックするか判断する    #118506
		RemainNumberClassification remainNumberClassification = remainingNumberCheck.determineCheckRemain(inputParam.getCid(), inputParam.getWorkTypeCds(), inputParam.getTimeDigestionUsageInfor());
		
		// 暫定管理データをメモリ上で作成する
		Map<GeneralDate, DailyInterimRemainMngData> mapDataOutput = new HashMap<>();

		val require = recordDomrequireService.createRequire();
		CacheCarrier cacheCarrier = new CacheCarrier();

		inputParam.getAppData().stream().forEach(x -> {
			DatePeriod dateData = inputParam.getRegisterDate();
			if (x.getAppType() == ApplicationType.COMPLEMENT_LEAVE_APPLICATION) {
				dateData = new DatePeriod(x.getAppDate(), x.getAppDate());
			}
			InterimRemainCreateDataInputPara dataCreate = new InterimRemainCreateDataInputPara(inputParam.getCid(),
					inputParam.getSid(), dateData, inputParam.getRecordData(), inputParam.getScheData(),
					inputParam.getAppData());

			//inputの情報から、暫定データを差分作成する。
			Map<GeneralDate, DailyInterimRemainMngData> mapDataOutputTmp = InterimRemainOffPeriodCreateData
					.createInterimRemainByScheRecordApp(require, cacheCarrier, dataCreate);
			
			// 振休申請は取り消しになる時を対応します。
			if (x.getAppType() == ApplicationType.COMPLEMENT_LEAVE_APPLICATION
					&& mapDataOutputTmp.containsKey(x.getAppDate())) {
				DailyInterimRemainMngData data = mapDataOutputTmp.get(x.getAppDate());
				if (data.getRecAbsData().isEmpty()) {
					String mngId = IdentifierUtil.randomUniqueId();
					InterimAbsMng furikyuSinsei = new InterimAbsMng(mngId, inputParam.getSid(), x.getAppDate(),
							CreateAtr.APPBEFORE, RemainType.PAUSE, new RequiredDay(0.0), new UnOffsetDay(0.0));
					data.getRecAbsData().add(furikyuSinsei);
					data.setInterimAbsData(Optional.of(furikyuSinsei));
					mapDataOutputTmp.remove(x.getAppDate());
					mapDataOutputTmp.put(x.getAppDate(), data);
				}
			}
			mapDataOutputTmp.forEach((z, y) -> {
				if (!mapDataOutput.containsKey(z)) {
					mapDataOutput.put(z, y);
				}
			});

		});
		InterimEachData eachData = this.interimInfor(mapDataOutput);
		RemainInputParam inputParamForCheck = new RemainInputParam(inputParam.getCid(), inputParam.getSid(),
				inputParam.getDatePeriod(), inputParam.isMode(), inputParam.getBaseDate(), inputParam.getRegisterDate(),
				Optional.of(inputParam.getAppData().get(0).getPrePosAtr().value == 0 ? CreateAtr.APPBEFORE : CreateAtr.APPAFTER));
		RemainErrors errorCheck =  getError(inputParamForCheck, eachData, Optional.of(remainNumberClassification));
		// 代休チェック区分をチェックする
		if (!errorCheck.getDayOffErrors().isEmpty()) {
			outputData.setChkSubHoliday(true);
		}
		// 振休不足区分をチェックする
		if (!errorCheck.getPErrors().isEmpty()) {
				outputData.setChkPause(true);
		}
		// 特休チェック区分をチェックする
		if (!errorCheck.getSpecialLeaveErrors().stream().flatMap(x -> x.getRight().stream())
				.collect(Collectors.toList()).isEmpty()) {
			outputData.setChkSpecial(true);
		}
		
		// 年休チェック区分をチェックする
		if (!errorCheck.getAnnualErrors().isEmpty()) {
			outputData.setChkAnnual(true);
		}
			
		// 期間中の年休積休残数を取得
		if (!errorCheck.getReserveLeaveErrors().isEmpty()) {
					outputData.setChkFundingAnnual(true);
		}
		// 子の看護チェック区分をチェックする
		    if (!errorCheck.getChildCareErrors().isEmpty()) {
		        outputData.setChkChildNursing(true);
		}
		// 介護チェック区分をチェックする
		if (!errorCheck.getNurseErrors().isEmpty()) {
			outputData.setChkLongTermCare(true);
		}

		return outputData;
	}

	@Override
	public InterimEachData interimInfor(Map<GeneralDate, DailyInterimRemainMngData> mapDataOutput) {
		/**
		 * 振休か振出の暫定残数管理
		 */
		List<InterimRemain> interimMngAbsRec = new ArrayList<>();
		/**
		 * 休出か代休の暫定残数管理
		 */
		List<InterimRemain> interimMngBreakDayOff = new ArrayList<>();
		/**
		 * 特別休暇の暫定残数管理
		 */
		List<InterimRemain> interimSpecial = new ArrayList<>();
		/**
		 * 年休の暫定残数管理
		 */
		List<InterimRemain> annualMng = new ArrayList<>();
		/**
		 * 積立年休の暫定残数管理
		 */
		List<InterimRemain> resereMng = new ArrayList<>();
		/**
	     * 暫定子の看護休暇データ
	     */
	    List<InterimRemain> childCareData = new ArrayList<>();
	    /**
	     * 暫定介護休暇データ
	     */
	    List<InterimRemain> careData = new ArrayList<>();
		mapDataOutput.forEach((x, y) -> {
			// 積立年休
			y.getResereData().ifPresent(z -> {
					resereMng.add(z);

			});
			// 年休
			y.getAnnualHolidayData().forEach(c->annualMng.add(c));
			// 特別休暇
			y.getSpecialHolidayData().forEach(c->interimSpecial.add(c));
			// 休出
			y.getBreakData().ifPresent(z -> {
				interimMngBreakDayOff.add(z);
			});
			//代休
			interimMngBreakDayOff.addAll(y.getDayOffData());
			
			// 振出
			y.getRecData().ifPresent(b -> {
				interimMngAbsRec.add(b);
			});
			//振休
			y.getInterimAbsData().ifPresent(c -> {
				interimMngAbsRec.add(c);
			});
			y.getChildCareData().forEach(b -> childCareData.add(b));
			y.getCareData().forEach(b -> careData.add(b));
		});
		return new InterimEachData(interimMngAbsRec, interimMngBreakDayOff,
				interimSpecial, annualMng, resereMng, childCareData, careData);
	}

	@Override
	public RemainErrors getError(RemainInputParam inputParam, InterimEachData remainData, Optional<RemainNumberClassification> remainNumberClassification) {
		val require = requireService.createRequire();
		List<InterimRemain> interimMngAbsRec = remainData.getInterimMngAbsRec();
		List<InterimRemain> interimMngBreakDayOff = remainData.getInterimMngBreakDayOff();
		List<InterimRemain> interimSpecial = remainData.getInterimSpecial();
		List<InterimRemain> childCareData = remainData.getChildCareData();
		List<InterimRemain> careData = remainData.getCareData();
		RemainErrors outputData = new RemainErrors();
		CacheCarrier cache = new CacheCarrier();

		// 代休チェック区分をチェックする
		if (remainNumberClassification.map(x -> x.isChkSubHoliday()).orElse(true)) {

			// 期間内の休出代休残数を取得する
			BreakDayOffRemainMngRefactParam inputParamBreak =  new BreakDayOffRemainMngRefactParam(
					inputParam.getCid(), inputParam.getSid(),
					inputParam.getDatePeriod(),
					inputParam.isMode(),
					inputParam.getBaseDate(),
					true,
					interimMngBreakDayOff,
					Optional.of(CreateAtr.APPBEFORE),
					Optional.of(inputParam.getRegisterDate()),
					Optional.empty(), new FixedManagementDataMonth());
			val remainMng = numberRemainVacationLeaveRangeProcess.getBreakDayOffMngInPeriod(inputParamBreak);
			outputData.setDayOffErrors(remainMng.getDayOffErrors());
		}
		// 振休不足区分をチェックする
		if (remainNumberClassification.map(x -> x.isChkPause()).orElse(true)) {
			// 振出振休残数を取得する
			val mngParam = new AbsRecMngInPeriodRefactParamInput(inputParam.getCid(),
					inputParam.getSid(), inputParam.getDatePeriod(), inputParam.getBaseDate(), inputParam.isMode(),
					true, interimMngAbsRec,
					Optional.empty(),
					Optional.of(CreateAtr.APPBEFORE),
					Optional.of(inputParam.getRegisterDate()),
					new FixedManagementDataMonth());
			CompenLeaveAggrResult remainMng = NumberCompensatoryLeavePeriodQuery.process(require, mngParam);
			outputData.setPErrors(remainMng.getPError());
		}
		// 特休チェック区分をチェックする
		if (remainNumberClassification.map(x -> x.isChkSpecial()).orElse(true) && !interimSpecial.isEmpty()) {
			List<SpecialHoliday> lstSpecialHoliday = specialHolidayRepository.findByCompanyIdWithTargetItem(inputParam.getCid());
			// 暫定残数管理データ(output)に「特別休暇暫定データ」が存在するかチェックする
			 List<Pair<Integer, List<SpecialLeaveError>>> specialLeaveErrorAll = new ArrayList<>();
			for (SpecialHoliday a : lstSpecialHoliday) {
				List<InterimSpecialHolidayMng> specInterimRemain = interimSpecial.stream().map(x -> (InterimSpecialHolidayMng)x)
						.filter(x -> x.getSpecialHolidayCode() == a.getSpecialHolidayCode().v().intValue()).collect(Collectors.toList());
				val specialLeaveErrors = SpecialLeaveManagementService.complileInPeriodOfSpecialLeave(recordDomRequireService.createRequire(), cache, 
				        new ComplileInPeriodOfSpecialLeaveParam(
				                inputParam.getCid(), 
				                inputParam.getSid(), 
				                inputParam.getDatePeriod(), 
				                false, 
				                inputParam.getBaseDate(), 
				                a.getSpecialHolidayCode().v(), 
				                false, 
				                true, 
				                specInterimRemain, 
				                Optional.of(inputParam.getRegisterDate()))).getSpecialLeaveErrors();
				if(!specialLeaveErrors.isEmpty()) {
					specialLeaveErrorAll.add(Pair.of( a.getSpecialHolidayCode().v(), specialLeaveErrors));
				}
			}
			outputData.setSpecialLeaveErrors(specialLeaveErrorAll);
		}
		// 年休チェック区分をチェックする
		if (remainNumberClassification.map(x -> x.isChkAnnual()).orElse(true)) {
			List<TempAnnualLeaveMngs> mngWork = remainData.getAnnualMng().stream().map(x -> ((TempAnnualLeaveMngs)x)).collect(Collectors.toList());
			List<AnnualLeaveErrorSharedImport> lstError = annualService.annualLeaveErrors(inputParam.getCid(),
					inputParam.getSid(), inputParam.getDatePeriod(), inputParam.isMode(), inputParam.getBaseDate(),
					false, false, Optional.of(true), Optional.of(mngWork), Optional.empty(), inputParam.getRegisterDate());
			outputData.setAnnualErrors(lstError.stream().filter(errorcheck -> {
				if (errorcheck == AnnualLeaveErrorSharedImport.SHORTAGE_AL_OF_UNIT_DAY_AFT_GRANT
						|| errorcheck == AnnualLeaveErrorSharedImport.SHORTAGE_AL_OF_UNIT_DAY_BFR_GRANT
						|| errorcheck == AnnualLeaveErrorSharedImport.EXCESS_MAX_HALFDAY_AFTER_GRANT
						|| errorcheck == AnnualLeaveErrorSharedImport.EXCESS_MAX_HALFDAY_BEFORE_GRANT
						|| errorcheck == AnnualLeaveErrorSharedImport.EXCESS_MAX_TIMEAL_AFTER_GRANT
						|| errorcheck == AnnualLeaveErrorSharedImport.EXCESS_MAX_TIMEAL_BEFORE_GRANT) {
					return true;
				}else {
					return false;
				}
			}).collect(Collectors.toList()));
		 }
		// 期間中の年休積休残数を取得
		if (remainNumberClassification.map(x -> x.isChkFundingAnnual()).orElse(true)) {
			List<TempAnnualLeaveMngs> mngWork = remainData.getAnnualMng().stream().map(x -> ((TempAnnualLeaveMngs)x)).collect(Collectors.toList());
			List<TmpResereLeaveMng> lstReserve = remainData.getResereMng().stream().map(x -> ((TmpResereLeaveMng)x)).collect(Collectors.toList());
			List<ReserveLeaveErrorImport> reserveLeaveErrors = annualService.reserveLeaveErrors(inputParam.getCid(),
					inputParam.getSid(), inputParam.getDatePeriod(), inputParam.isMode(), inputParam.getBaseDate(),
					false, false, Optional.of(true), Optional.of(mngWork), Optional.of(lstReserve), Optional.empty(),
					Optional.empty(), inputParam.getRegisterDate());
			outputData.setReserveLeaveErrors(reserveLeaveErrors.stream().filter(errorCheck -> {
				if (errorCheck == ReserveLeaveErrorImport.SHORTAGE_RSVLEA_AFTER_GRANT
						|| errorCheck == ReserveLeaveErrorImport.SHORTAGE_RSVLEA_BEFORE_GRANT) {
					return true;
				}else {
					return false;
				}
			}).collect(Collectors.toList()));
		}
		// 子の看護チェック区分をチェックする
		if (remainNumberClassification.map(x -> x.isChkChildNursing()).orElse(true)) {
		    // [NO.206]期間中の子の看護休暇残数を取得
//		    AggrResultOfChildCareNurse resultChildcare =
//		            GetRemainingNumberChildCareService.getChildCareRemNumWithinPeriod(
//	                        inputParam.getCid(), 
//	                        inputParam.getSid(), 
//	                        inputParam.getDatePeriod(), 
//	                        InterimRemainMngMode.of(inputParam.isMode()), 
//	                        inputParam.getBaseDate(),
//	                        Optional.of(true), 
//	                        childCareData.stream().map(x -> ((TempChildCareManagement)x)).collect(Collectors.toList()),
//	                        Optional.empty(), 
//	                        inputParam.getCreateAtr(), 
//	                        Optional.of(inputParam.getRegisterDate()),
//	                        cache, 
//	                        childCareNurseRequireImplFactory.createRequireImpl());
		    RemainChildCareCheckParam param = new RemainChildCareCheckParam(
		                    inputParam.getCid(), 
                            inputParam.getSid(), 
                            YearMonth.of(9999, 12), 
                            inputParam.getDatePeriod(), 
                            ClosureId.RegularEmployee, 
                            new ClosureDate(1, false), 
                            inputParam.isMode(), 
                            true,
                            childCareData.stream().map(x -> ((TempChildCareManagement)x)).collect(Collectors.toList()), 
                            inputParam.getCreateAtr(), 
                            Optional.of(inputParam.getRegisterDate()));
		    
		        outputData.setChildCareErrors(remainChildCareCheck.checkRemainChildCare(param));
		}
		
		// 介護チェック区分をチェックする
		if (remainNumberClassification.map(x -> x.isChkLongTermCare()).orElse(true)) {
		    // [NO.207]期間中の介護休暇残数を取得
//		    AggrResultOfChildCareNurse resultNursing = GetRemainingNumberCareService.getCareRemNumWithinPeriod(
//		            inputParam.getCid(), 
//                    inputParam.getSid(), 
//                    inputParam.getDatePeriod(),
//                    InterimRemainMngMode.of(inputParam.isMode()), 
//                    inputParam.getBaseDate(),
//                    Optional.of(true), 
//	                careData.stream().map(x -> (TempCareManagement)x).collect(Collectors.toList()), 
//	                Optional.empty(),
//	                inputParam.getCreateAtr(), 
//	                Optional.of(inputParam.getRegisterDate()),
//                    cache, 
//                    childCareNurseRequireImplFactory.createRequireImpl());
		    RemainLongTermCareCheckParam param = new RemainLongTermCareCheckParam(
		            inputParam.getCid(), 
                    inputParam.getSid(), 
                    YearMonth.of(9999, 12), 
                    inputParam.getDatePeriod(), 
                    ClosureId.RegularEmployee, 
                    new ClosureDate(1, false), 
                    inputParam.isMode(), 
                    true,
                    careData.stream().map(x -> (TempCareManagement)x).collect(Collectors.toList()),
                    inputParam.getCreateAtr(), 
                    Optional.of(inputParam.getRegisterDate()));
		       outputData.setNurseErrors(remainLongTermCareCheck.checkRemainLongTermCare(param));
		}
		return outputData;
	}
	

}
