package nts.uk.ctx.at.function.infra.repository.attendancerecord.export;

import java.util.List;

import nts.uk.ctx.at.function.dom.attendancerecord.export.AttendanceRecordOuputItems;
import nts.uk.ctx.at.function.dom.attendancerecord.export.AttendanceRecordStandardSetting;
import nts.uk.ctx.at.function.dom.attendancerecord.export.setting.AttendanceRecordExportSetting;
import nts.uk.ctx.at.function.dom.attendancerecord.export.setting.ItemSelectionType;
import nts.uk.ctx.at.function.infra.entity.attendancerecord.export.setting.KfnmtRptWkAtdOut;

public class JpaAttendanceRecordOuputItemsGetMemento implements AttendanceRecordOuputItems.MementoGetter ,  AttendanceRecordStandardSetting.MementoGetter{

	private List<KfnmtRptWkAtdOut> kfnmtRptWkAtdOuts;
	
	@Override
	public int getiItemSelectionType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AttendanceRecordExportSetting> getAttendanceRecordExportSettings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEmployeeId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemSelectionType getItemSelectionType() {
		// TODO Auto-generated method stub
		return null;
	}

}
