package nts.uk.file.at.app.schedule.filemanagement;

import nts.uk.ctx.at.schedule.dom.importschedule.CapturedRawData;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.setting.code.EmployeeCodeEditSettingExport;

public interface CheckFileService {

    public CapturedRawData processingFile(WorkPlaceScheCheckFileParam checkFileParam, EmployeeCodeEditSettingExport setting) throws Exception;
}
