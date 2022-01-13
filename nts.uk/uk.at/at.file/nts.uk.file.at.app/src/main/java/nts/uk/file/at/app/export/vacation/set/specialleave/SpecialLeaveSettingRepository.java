package nts.uk.file.at.app.export.vacation.set.specialleave;

import java.util.List;

import nts.uk.shr.infra.file.report.masterlist.data.MasterData;

public interface SpecialLeaveSettingRepository {

	List<MasterData> getWorkDaysNumberOnLeaveCount(String cid);
}
