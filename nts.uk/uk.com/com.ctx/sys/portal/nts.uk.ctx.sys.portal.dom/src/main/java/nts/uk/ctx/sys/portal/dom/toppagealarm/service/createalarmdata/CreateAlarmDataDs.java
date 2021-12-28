package nts.uk.ctx.sys.portal.dom.toppagealarm.service.createalarmdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.uk.ctx.sys.portal.dom.toppagealarm.AlarmClassification;
import nts.uk.ctx.sys.portal.dom.toppagealarm.service.registeralarmdata.RegisterAlarmDataDs;
import nts.uk.ctx.sys.portal.dom.toppagealarm.service.registeralarmdata.RegisterAlarmDataDs.RegisterAlarmDataRequire;
import nts.uk.ctx.sys.portal.dom.toppagealarm.service.registeralarmdata.ToppageAlarmParam;
import nts.uk.ctx.sys.portal.dom.toppagealarm.service.updatealarmdata.UpdateAlarmDataDs;
import nts.uk.ctx.sys.portal.dom.toppagealarm.service.updatealarmdata.UpdateAlarmDataDs.UpdateAlarmDataRequire;
import nts.uk.ctx.sys.portal.dom.toppagealarm.service.updateautorunalarm.UpdateAutoRunAlarmDs;
import nts.uk.ctx.sys.portal.dom.toppagealarm.service.updateautorunalarm.UpdateAutoRunAlarmDs.UpdateAutoRunAlarmRequire;

/**
 * トップページアラームデータを作成する
 * 
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.ポータル.トップページアラーム（ver4～）.DomainService.トップページアラームデータを作成する.トップページアラームデータを作成する
 */
public class CreateAlarmDataDs {

	private CreateAlarmDataDs() {}
	
	public static void create(UpdateAutoRunAlarmRequire rq1, UpdateAlarmDataRequire rq2, RegisterAlarmDataRequire rq3, 
			String cid, List<ToppageAlarmParam> alarmInfos, Optional<DeleleteInfo> delInfo) {
		
		if (delInfo.isPresent()) {
			
			DeleleteInfo delExInfo = delInfo.get();
			
			if (delExInfo.getAlarmClassification() == AlarmClassification.AUTO_EXEC_BUSINESS_ERR
					|| delExInfo.getAlarmClassification() == AlarmClassification.AUTO_EXEC_OPERATION_ERR) {
				UpdateAutoRunAlarmDs.create(rq1, cid, delExInfo.getAlarmClassification(), delExInfo.getSids())
				.run();
			}
			
			if (delExInfo.getAlarmClassification() == AlarmClassification.ALARM_LIST) {
				UpdateAlarmDataDs.create(rq2, cid, delExInfo.getSids(), delExInfo.getAlarmListParttenCode().orElse(""), 
						new ArrayList<>(), delExInfo.getDisplayEmpClassfication().value)
				.run();
				
			}
		}
		
		alarmInfos.stream().forEach(item -> RegisterAlarmDataDs.register(rq3, cid, item));
	}
}
