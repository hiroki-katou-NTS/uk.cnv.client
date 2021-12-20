package nts.uk.screen.at.ws.kdp.kdp002.b;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.uk.screen.at.app.query.kdp.kdp002.a.CreateDailyParam;
import nts.uk.screen.at.app.query.kdp.kdp002.a.GetWorkManagementMultiple;
import nts.uk.screen.at.app.query.kdp.kdp002.a.SettingsStampCommon;
import nts.uk.screen.at.app.query.kdp.kdp002.a.SettingsStampCommonDto;
import nts.uk.screen.at.app.query.kdp.kdp002.b.ContentOfNotificationByStamp;
import nts.uk.screen.at.app.query.kdp.kdp002.b.ContentOfNotificationByStampDto;
import nts.uk.screen.at.app.query.kdp.kdp002.b.ContentOfNotificationByStampInput;
import nts.uk.screen.at.app.query.kdp.kdp002.b.GetSettingNoti;
import nts.uk.screen.at.app.query.kdp.kdp002.b.RegisterEmotionalStateCommand;
import nts.uk.screen.at.app.query.kdp.kdp002.b.RegisterEmotionalStateCommandhandler;
import nts.uk.screen.at.app.query.kdp.kdp002.b.SettingEmojiByStamp;
import nts.uk.screen.at.app.query.kdp.kdp002.l.GetEmployeeWorkByStamping;
import nts.uk.screen.at.app.query.kdp.kdp002.l.GetEmployeeWorkByStampingDto;
import nts.uk.screen.at.app.query.kdp.kdp002.l.GetEmployeeWorkByStampingInput;

/**
 * 
 * @author chungnt
 *
 */
@Path("at/record/stamp/")
@Produces("application/json")
public class Kdp002BWebService {

	@Inject
	private ContentOfNotificationByStamp notification;
	
	@Inject
	private SettingsStampCommon settingsStampCommon;
	
	@Inject
	private GetWorkManagementMultiple getWorkManagementMultiple;
	
	@Inject
	private SettingEmojiByStamp settingEmojiByStamp;
	
	@Inject
	private RegisterEmotionalStateCommandhandler registerEmotionalStateCommandhandler;
	
	@Inject
	private GetSettingNoti settingNoti;
	
	@Inject
	private GetEmployeeWorkByStamping getEmployeeWorkByStamping;
	
	@POST 
	@Path("notification_by_stamp")
	public ContentOfNotificationByStampDto notificationByStamp(ContentOfNotificationByStampInput param){
		return notification.get(param);
	}
	
	@POST 
	@Path("settings_stamp_common")
	public SettingsStampCommonDto settingsStampCommon(){
		return settingsStampCommon.getSettingCommonStamp();
	}
	
	@POST 
	@Path("work_management_multiple")
	public boolean getWorkManagementMultiple(){
		return getWorkManagementMultiple.getWorkManagementMultiple();
	}
	
	// 打刻入力でニコニコマークの表示設定を取得する
	@POST 
	@Path("setting_emoji_stamp")
	public boolean get(){
		return settingEmojiByStamp.getSettingEmoji();
	}
	
	//<command> 感情状態を登録する
	@POST
	@Path("regis_emotional_state")
	public void registerEmotionalState(RegisterEmotionalStateCommand param) {
		this.registerEmotionalStateCommandhandler.handle(param);
	}
	
	@POST
	@Path("settingNoti")
	public boolean settingNoti() {
		return this.settingNoti.getSetting();
	}
		
	@POST 
	@Path("employee_work_by_stamping")
	public GetEmployeeWorkByStampingDto getEmployeeWorkByStamping(GetEmployeeWorkByStampingInput param){
		return getEmployeeWorkByStamping.getEmployeeWorkByStamping(param);
	}

}
