package nts.uk.ctx.at.record.dom.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.dom.workrecord.monthlyprocess.export.monthlyactualerrors.CreateChildCareErrors;
import nts.uk.ctx.at.record.dom.workrecord.monthlyprocess.export.monthlyactualerrors.CreateChildCareErrorsParam;
import nts.uk.ctx.at.record.dom.workrecord.monthlyprocess.export.monthlyactualerrors.CreateLongTermCareErrors;
import nts.uk.ctx.at.record.dom.workrecord.monthlyprocess.export.monthlyactualerrors.CreateLongTermCareErrorsParam;
import nts.uk.ctx.at.record.dom.workrecord.remainingnumbermanagement.InterimRemainDataMngCheckRegister;
import nts.uk.ctx.at.record.dom.workrecord.remainingnumbermanagement.RemainNumberClassification;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.DailyInterimRemainMngData;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimEachData;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainCreateDataInputPara;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainOffPeriodCreateData;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.RecordRemainCreateInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.RemainErrors;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.RemainInputParam;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.SubsTransferProcessMode;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.CreateInterimAnnualMngData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TempAnnualLeaveMngs;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.InterimRemain;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.CreateAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.CreateWorkRecordScheduleRemain;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.export.pererror.CreatePerErrorsFromLeaveErrors;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.AnnualLeaveError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.EmployeeMonthlyPerError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.ReserveLeaveError;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

@Stateless
public class TimeOffRemainErrorInforImpl implements TimeOffRemainErrorInfor{
	@Inject
	private InterimRemainDataMngCheckRegister checkRegisterService;
	@Inject
	private RecordDomRequireService requireService;
	@Inject
	private InterimRemainDataMngCheckRegister InterimRemainCheckRegister;
	
	@Inject
    private CreateChildCareErrors createChildCareErrors;
	
	@Inject
	private CreateLongTermCareErrors createLongTermCareErrors;
	
	@Override
	public List<EmployeeMonthlyPerError> getErrorInfor(TimeOffRemainErrorInputParam param) {
		
		val require = requireService.createRequire();
		val cacheCarrier = new CacheCarrier();

		//残数作成元情報を作成する
		List<RecordRemainCreateInfor> recordInfor = CreateWorkRecordScheduleRemain.createRemain(require, param.getCid(), param.getLstDomainDaily(), SubsTransferProcessMode.DAILY);
		//指定期間の暫定残数管理データを作成する（差分のみ）
		InterimRemainCreateDataInputPara createInterimDataParam = new InterimRemainCreateDataInputPara(param.getCid(),
				param.getSid(),
				param.getObjDate(),
				recordInfor,
				Collections.emptyList(),
				Collections.emptyList());
		Map<GeneralDate, DailyInterimRemainMngData> interimRemainData = InterimRemainOffPeriodCreateData
				.createInterimRemainByScheRecordApp(require, cacheCarrier, createInterimDataParam);
		Optional<DailyInterimRemainMngData> optDaily = Optional.empty();

		/** 大塚モードかを確認する */
		if(param.getOptMonthlyData().isPresent() && AppContexts.optionLicense().customize().ootsuka()) {
			//月別実績(Work)から年休フレックス補填分の暫定年休管理データを作成する
			optDaily =  CreateInterimAnnualMngData.ofCompensFlex(param.getOptMonthlyData().get(), param.getObjDate().end());

		}
		
		InterimEachData eachData = checkRegisterService.interimInfor(interimRemainData);
		if(optDaily.isPresent()
				&& !optDaily.get().getAnnualHolidayData().isEmpty()) {
			TempAnnualLeaveMngs flexAnnual = optDaily.get().getAnnualHolidayData().stream().filter(c->c.getUsedNumber().isUseDay()).map(x-> x).findFirst().orElse(null);
			List<InterimRemain> lstAnnualMng = optDaily.get().getRecAbsData().stream()
					.filter(x -> x.getRemainManaID().equals(flexAnnual.getRemainManaID()))
					.collect(Collectors.toList());
			if(!lstAnnualMng.isEmpty()) {
				eachData.getAnnualMng().add(lstAnnualMng.get(0));
			}
		}
		
		RemainInputParam inputParam = new RemainInputParam(param.getCid(), param.getSid(), param.getAggDate(), false,
				param.getObjDate().end(), param.getObjDate(), Optional.of(CreateAtr.RECORD));
		Optional<RemainNumberClassification> remainNumberClassification = Optional.of(RemainNumberClassification.createTrue());
		RemainErrors lstError = InterimRemainCheckRegister.getError(inputParam, eachData, remainNumberClassification);
		
		List<EmployeeMonthlyPerError> outPutData = new ArrayList<>();
		
		outPutData.addAll(getAnnualErrors(param.getSid(), lstError));
		
		outPutData.addAll(getReserveLeaveErrors(param.getSid(), lstError));
		
		outPutData.addAll(getSpecialLeaveErrors(param.getSid(), lstError));
		
		outPutData.addAll(getPErrors(param.getSid(), lstError));
		
		outPutData.addAll(getDayOffErrors(param.getSid(), lstError));
		
		outPutData.addAll(getChildCareErrors(param.getSid(), lstError));

		outPutData.addAll(getNurseErrors(param.getSid(), lstError));
		
		return outPutData;
	}

	private List<EmployeeMonthlyPerError> getAnnualErrors(String sid, RemainErrors lstError) {
		if (lstError.getAnnualErrors().isEmpty()) {
			return new ArrayList<>();
		}
		// 年休エラーから月別実績エラー一覧を作成する
		return CreatePerErrorsFromLeaveErrors.fromAnnualLeave(sid, YearMonth.of(999912),
				ClosureId.RegularEmployee, new ClosureDate(1, false), lstError.getAnnualErrors().stream()
						.map(x -> EnumAdaptor.valueOf(x.value, AnnualLeaveError.class)).collect(Collectors.toList()));
	}

	private List<EmployeeMonthlyPerError> getReserveLeaveErrors(String sid, RemainErrors lstError) {
		if (lstError.getReserveLeaveErrors().isEmpty()) {
			return new ArrayList<>();
		}
		// 積立年休エラーから月別実績エラー一覧を作成する
		return CreatePerErrorsFromLeaveErrors.fromReserveLeave(sid,
				YearMonth.of(999912), ClosureId.RegularEmployee, new ClosureDate(1, false),
				lstError.getReserveLeaveErrors().stream()
						.map(x -> EnumAdaptor.valueOf(x.value, ReserveLeaveError.class)).collect(Collectors.toList()));
	}

	private List<EmployeeMonthlyPerError> getSpecialLeaveErrors(String sid, RemainErrors lstError) {
		List<EmployeeMonthlyPerError> outPutData = new ArrayList<>();
		lstError.getSpecialLeaveErrors().stream().filter(x -> !x.getRight().isEmpty()).forEach(x -> {
			outPutData.addAll(CreatePerErrorsFromLeaveErrors.fromSpecialLeave(
				sid,
				YearMonth.of(999912),
				ClosureId.RegularEmployee,
				new ClosureDate(1, false),
				x.getLeft(),
				x.getRight()));
		});
		return outPutData;
	}

	private List<EmployeeMonthlyPerError> getPErrors(String sid, RemainErrors lstError) {
		if (lstError.getPErrors().isEmpty()) {
			return new ArrayList<>();
		}
		return CreatePerErrorsFromLeaveErrors.fromPause(sid,
					YearMonth.of(999912), ClosureId.RegularEmployee, new ClosureDate(1, false), lstError.getPErrors());
	}

	private List<EmployeeMonthlyPerError> getDayOffErrors(String sid, RemainErrors lstError) {

		if (lstError.getDayOffErrors().isEmpty()) {
			return new ArrayList<>();
		}
		return CreatePerErrorsFromLeaveErrors.fromDayOff(sid, YearMonth.of(999912), ClosureId.RegularEmployee,
				new ClosureDate(1, false), lstError.getDayOffErrors());
	}

	private List<EmployeeMonthlyPerError> getChildCareErrors(String sid, RemainErrors lstError) {
		if (lstError.getChildCareErrors().isEmpty()) {
			return new ArrayList<>();
		}

		CreateChildCareErrorsParam createChildCareErrorsParam = new CreateChildCareErrorsParam(sid,
				GeneralDate.max().yearMonth(), ClosureId.RegularEmployee, new ClosureDate(1, false),
				lstError.getChildCareErrors());
		// 子の看護エラーから月別実績エラー一覧を作成する
		return createChildCareErrors.createChildCareErrors(createChildCareErrorsParam);
	}

	private List<EmployeeMonthlyPerError> getNurseErrors(String sid, RemainErrors lstError) {
		if (lstError.getNurseErrors().isEmpty()) {
			return new ArrayList<>();
		}
		CreateLongTermCareErrorsParam createChildCareErrorsParam = new CreateLongTermCareErrorsParam(sid,
				GeneralDate.max().yearMonth(), ClosureId.RegularEmployee, new ClosureDate(1, false),
				lstError.getNurseErrors());
		// 介護エラーから月別実績エラー一覧を作成する
		return createLongTermCareErrors.createLongTermCareErrors(createChildCareErrorsParam);
	}
}
