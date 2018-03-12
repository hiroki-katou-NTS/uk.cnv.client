package nts.uk.ctx.at.function.ws.alarm;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.uk.ctx.at.function.app.find.alarm.AlarmPatternSettingFinder;
import nts.uk.ctx.at.function.app.find.alarm.CheckConditionTimeFinder;
import nts.uk.ctx.at.function.app.find.alarm.CodeNameAlarmDto;
import nts.uk.ctx.at.function.app.find.alarm.alarmlist.ExtractAlarmListFinder;
import nts.uk.ctx.at.function.app.find.alarm.alarmlist.ExtractAlarmQuery;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.ExtractedAlarmDto;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.CheckConditionTimeDto;

/**
 * @author dxthuong
 *
 * Web Service for function KAL001_アラームリスト
 */
@Path("at/function/alarm/kal/001")
@Produces("application/json")
public class KAL001WebService {

	@Inject
	private AlarmPatternSettingFinder alarmFinder;
	
	@Inject
	private CheckConditionTimeFinder checkConditionFinder;
	
	@Inject
	private ExtractAlarmListFinder extractAlarmFinder;
	
	@POST
	@Path("pattern/setting")
	public List<CodeNameAlarmDto> getAlarmByUser(){
		return alarmFinder.getCodeNameAlarm();
	}
	
	@POST
	@Path("check/condition/time")
	public List<CheckConditionTimeDto> getCheckConditionTime(String alarmCode){
		return checkConditionFinder.getCheckConditionTime(alarmCode);
	}
	
	@POST
	@Path("extract/alarm")
	public ExtractedAlarmDto extractAlarm(ExtractAlarmQuery query) {
		//extractAlarmFinder.extractAlarm(query);
		return new ExtractedAlarmDto(new ArrayList<>(), false, false);
	}
}
