package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import java.util.List;

/**
 * 作業マスタを取得するAdapter
 */
public interface WorkMasterAdapter {

    List<TaskImport> getTaskList(String cid, Integer taskFrameNo, List<String> codes);
}
