package nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.repo;

import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.TimeRecordReqSetting;

public interface TimeRecordReqSettingSendWorkTypeRepository {

	void insert(TimeRecordReqSetting reqSetting);
	
	void update(TimeRecordReqSetting reqSetting);
	
	void delete(TimeRecordReqSetting reqSetting);
}
