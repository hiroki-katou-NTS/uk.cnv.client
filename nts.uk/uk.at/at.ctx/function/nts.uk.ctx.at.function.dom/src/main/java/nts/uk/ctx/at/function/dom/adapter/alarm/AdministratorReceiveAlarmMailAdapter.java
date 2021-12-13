package nts.uk.ctx.at.function.dom.adapter.alarm;

import java.util.List;
import java.util.Map;

/**
 * 職場IDから、アラームメールの受信を許可されている管理者を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.就業機能.Import.職場IDから、アラームメールの受信を許可されている管理者を取得する.職場IDから、アラームメールの受信を許可されている管理者を取得する
 */
public interface AdministratorReceiveAlarmMailAdapter {
    Map<String, List<String>> getAdminReceiveAlarmMailByWorkplaceIds(List<String> workplaceIds);
}
