package nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.arc.time.calendar.period.DatePeriod;

@Stateless
public class SpecialLeaveGrantNextDateServiceImpl implements SpecialLeaveGrantNextDateService{
	@Inject
	private NotDepentSpecialLeaveOfEmployee notDepentSpeService;
	@Override
	public Optional<GrantDaysInfor> getNumberOfGrantDays(SpecialLeaveGrantNextDateInput param) {
		//次回特休付与計算開始日を計算する
		GeneralDate startDate = this.getStartDateOfSpecialNextDay(param.getClosureDate(), param.getInputDate());
		//アルゴリズム「次回特別休暇付与日を取得」を実行
		SpeGrantNextDateByGetInput inputParam = new SpeGrantNextDateByGetInput(param.getCid(),
				param.getSpecialCode(),
				new DatePeriod(startDate, GeneralDate.max()),
				param.getSpecialDate(),
				param.getSpecialSetting(),
				param.getSpeGrantDataCode(),
				param.getGranDays(),
				param.getInputDate(),
				param.getAnnualHolidayDate());
		return this.getGrantDataOfNextDay(inputParam);
	}

	@Override
	public GeneralDate getStartDateOfSpecialNextDay(GeneralDate closureDate, GeneralDate inputDate) {
		GeneralDate outputDate = GeneralDate.today();
		//パラメータ「入社年月日」とパラメータ「締め開始日」を比較
		if(closureDate.afterOrEquals(inputDate)) {
			//次回特休付与計算開始日　←　パラメータ「締め開始日」
			outputDate = closureDate;
		} else {
			//次回特休付与計算開始日　←　パラメータ「入社年月日」
			outputDate = inputDate;
		}
		//次回特休付与計算開始日 = 次回特休付与計算開始日.AddDays(1)
		outputDate = outputDate.addDays(1);
		return outputDate;
	}

	@Override
	public Optional<GrantDaysInfor> getGrantDataOfNextDay(SpeGrantNextDateByGetInput param) {
		//社員に依存しない特別休暇情報を取得する
		NotDepentSpecialLeaveOfEmployeeInputExtend inputData = new NotDepentSpecialLeaveOfEmployeeInputExtend(
				Optional.empty(),
				param.getCid(), 
				param.getDatePeriod(), 
				param.getSpecialCode(), 
				param.getSpecialDate(),
				param.getAnnualHolidayDate(),
				param.getInputDate(), 
				true, 
				param.getSpecialSetting(), 
				param.getGranDays(),
				param.getSpeGrantDataCode());
		InforSpecialLeaveOfEmployee speInfor = notDepentSpeService.getNotDepentInfoSpecialLeave(inputData);
		//Output「付与日数一覧」をチェックする
		if(speInfor.getSpeHolidayInfor().isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(speInfor.getSpeHolidayInfor().get(0).getGrantDaysInfor());
	}

	@Override
	public Map<String, GrantDaysInfor> getNumberOfGrantDays(List<SpecialLeaveGrantNextDateInputExtend> params) {
		//次回特休付与計算開始日を計算する
		List<SpeGrantNextDateByGetInputExtend>  inputParams = new ArrayList<>();
		params.stream().forEach(c ->{
			//次回特休付与計算開始日を計算する
			GeneralDate startDate = this.getStartDateOfSpecialNextDay(c.getClosureDate(), c.getInputDate());
			//アルゴリズム「次回特別休暇付与日を取得」を実行
			SpeGrantNextDateByGetInputExtend inputParam = new SpeGrantNextDateByGetInputExtend(
					c.getSid(),
					c.getCid(),
					c.getSpecialCode(),
					new DatePeriod(startDate, GeneralDate.max()),
					c.getSpecialDate(),
					c.getSpecialSetting(),
					c.getSpeGrantDataCode(),
					c.getGranDays(),
					c.getInputDate(),
					c.getAnnualHolidayDate());
			inputParams.add(inputParam);
		});
		if(inputParams.isEmpty()) return new HashMap<>();
		return this.getGrantDataOfNextDay(inputParams);
	}

	@Override
	public Map<String, GrantDaysInfor> getGrantDataOfNextDay(List<SpeGrantNextDateByGetInputExtend> params) {
		Map<String, GrantDaysInfor> result = new HashMap<>();
		//社員に依存しない特別休暇情報を取得する
		List<NotDepentSpecialLeaveOfEmployeeInputExtend> inputDatas = params.stream().map(param -> new NotDepentSpecialLeaveOfEmployeeInputExtend(
				Optional.of(param.getSid()),
				param.getCid(), 
				param.getDatePeriod(), 
				param.getSpecialCode(), 
				param.getSpecialDate(),
				param.getAnnualHolidayDate(),
				param.getInputDate(), 
				true, 
				param.getSpecialSetting(), 
				param.getGranDays(),
				param.getSpeGrantDataCode())).collect(Collectors.toList());
		Map<String, InforSpecialLeaveOfEmployee> speInforMap = notDepentSpeService.getNotDepentInfoSpecialLeave(inputDatas);
		if(speInforMap.isEmpty()) return new HashMap<>();
		speInforMap.entrySet().stream().filter(c -> c.getValue()!= null).forEach(c ->{
			//Output「付与日数一覧」をチェックする
			if(CollectionUtil.isEmpty(c.getValue().getSpeHolidayInfor())) {
				return;
			}
			result.put(c.getKey(), c.getValue().getSpeHolidayInfor().get(0).getGrantDaysInfor());
		});
		
		return result;
	}
}
