package nts.uk.ctx.at.shared.pub.workmanagement.workmaster;

import java.util.List;

/**
 * 作業マスタを取得するPublish
 */
public interface WorkMasterPub {
    List<TaskPubExport> getListTask(String cid, Integer taskFrameNo, List<String> codes);
}
