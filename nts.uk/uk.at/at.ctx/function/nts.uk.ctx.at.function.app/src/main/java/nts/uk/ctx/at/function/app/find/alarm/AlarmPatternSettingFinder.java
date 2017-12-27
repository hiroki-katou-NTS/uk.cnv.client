package nts.uk.ctx.at.function.app.find.alarm;

import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import nts.uk.ctx.at.function.dom.alarm.AlarmPatternSetting;
import nts.uk.ctx.at.function.dom.alarm.AlarmPatternSettingRepository;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckConditionByCategory;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckConditionByCategoryRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class AlarmPatternSettingFinder {

	@Inject
	private AlarmPatternSettingRepository alarmPatternRepo;

	@Inject
	private AlarmCheckConditionByCategoryRepository alarmCategoryRepo;

	public List<AlarmPatternSettingDto> findAllAlarmPattern() {
		String companyId = AppContexts.user().companyId();		
		return alarmPatternRepo.findByCompanyId(companyId).stream().map(domain -> convertToAlarmPatternDto(domain)).collect(Collectors.toList());
	}

	public List<AlarmCheckConditonCodeDto> findAllAlarmCheckCondition() {
		String companyId = AppContexts.user().companyId();
		return alarmCategoryRepo.findAll(companyId).stream().map(domain ->this.convertToCheckConditionCode(domain)).collect(Collectors.toList());
	}

	public AlarmPatternSettingDto convertToAlarmPatternDto(AlarmPatternSetting domain) {
		
		List<CheckConditionDto> checkConditionDtos = domain.getCheckConList().stream()
				.map(c -> new CheckConditionDto(c.getAlarmCategory().value, c.getCheckConditionList()))
				.collect(Collectors.toList());
		AlarmPermissionSettingDto alarmPerSet = new AlarmPermissionSettingDto(domain.getAlarmPerSet().isAuthSetting(), domain.getAlarmPerSet().getRoleIds());
		
		return new AlarmPatternSettingDto(domain.getAlarmPatternCD().v(), domain.getAlarmPatternName().v(), alarmPerSet, checkConditionDtos);
	}
	
	public AlarmCheckConditonCodeDto  convertToCheckConditionCode(AlarmCheckConditionByCategory domain) {		
		return new AlarmCheckConditonCodeDto(domain.getCategory().value, domain.getCode().v(), domain.getName().v(), domain.getListRoleId());
	}
}
