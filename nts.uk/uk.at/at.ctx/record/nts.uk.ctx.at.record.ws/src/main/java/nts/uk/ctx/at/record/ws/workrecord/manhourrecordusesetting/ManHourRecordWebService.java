package nts.uk.ctx.at.record.ws.workrecord.manhourrecordusesetting;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.record.app.command.workrecord.manhourrecordusesetting.ManHourRecordUseSettingCommand;
import nts.uk.ctx.at.record.app.command.workrecord.manhourrecordusesetting.UpdateManHourRecordUseSettingCommandHandler;
import nts.uk.ctx.at.record.app.find.manhourrecordusesetting.ManHourRecordReferenceSettingDto;
import nts.uk.ctx.at.record.app.find.manhourrecordusesetting.ManHourRecordUseSettingDto;
import nts.uk.ctx.at.record.app.find.manhourrecordusesetting.ManHourRecordUseSettingFinder;
import nts.uk.ctx.at.record.app.find.workrecord.erroralarm.ErrorAlarmWorkRecordDto;

/**
 * 
 * @author huylq
 *
 */
@Path("at/record/workrecord/manhourrecordusesetting/")
@Produces("application/json")
public class ManHourRecordWebService extends WebService {
	
	@Inject
	ManHourRecordUseSettingFinder finder;
	
	@Inject
	UpdateManHourRecordUseSettingCommandHandler updateHandler;
	
	@POST 
	@Path("start")
	public ManHourRecordUseSettingDto start() {
		//	日別実績のエラーアラームを取得する
		ErrorAlarmWorkRecordDto errorAlarmWorkRecord = finder.getErrorAlarmWorkRecord();
		//	工数実績参照設定を取得する
		ManHourRecordReferenceSettingDto manHourRecordReferenceSetting = finder.getManHourRecordReferenceSetting();
		
		return new ManHourRecordUseSettingDto(errorAlarmWorkRecord, manHourRecordReferenceSetting, finder.getArt());
	}
	
	@POST
	@Path("register")
	public void register(ManHourRecordUseSettingCommand command) {
		updateHandler.handle(command);
	}
}
