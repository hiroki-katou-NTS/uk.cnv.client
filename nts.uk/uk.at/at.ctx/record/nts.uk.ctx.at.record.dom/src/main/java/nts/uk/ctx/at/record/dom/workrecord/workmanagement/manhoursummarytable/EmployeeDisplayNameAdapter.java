package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import nts.uk.ctx.at.record.dom.adapter.function.alarmworkplace.EmployeeInfoImport;

import java.util.List;

/**
 * 社員表示名を取得するAdapter
 */
public interface EmployeeDisplayNameAdapter {
    List<EmployeeInfoImport> getByListSID(List<String> sIds);

}
