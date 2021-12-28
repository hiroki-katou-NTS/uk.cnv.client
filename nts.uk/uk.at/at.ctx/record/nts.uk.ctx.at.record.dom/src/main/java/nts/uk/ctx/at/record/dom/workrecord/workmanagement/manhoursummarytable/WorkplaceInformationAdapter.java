package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.adapter.workplace.config.info.WorkplaceInfor;

import java.util.List;

/**
 * 職場情報を取得するAdapter
 */
public interface WorkplaceInformationAdapter {
    List<WorkplaceInfor> getWorkplaceInfor(String companyId, List<String> listWorkplaceId, GeneralDate baseDate);
}
