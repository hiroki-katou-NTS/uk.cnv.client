package nts.uk.ctx.at.function.dom.alarm.sendemail;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.adapter.role.RoleSetExportDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 社員IDListから就業ロールIDを取得
 */
public class GetRoleWorkByEmployeeService {
    /**
     * 取得する
     *
     * @param sids 社員IDList
     * @param date 基準日
     * return Map<社員ID、ロールID>
     */
    public static Map<String, String> get(Require require, List<String> sids, GeneralDate date) {
        // 社員IDからユーザIDを取得する：　Map<社員ID、ユーザID>
        Map<String, String> userIds = sids.stream()
                .collect(Collectors.toMap(
                        x -> x,
                        x -> require.getUserIDByEmpID(x).orElse("")))
                .entrySet().stream().filter(x -> !x.getValue().equals(""))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // ユーザIDからロールセットを取得する：　Map<社員ID、ロールID>
        Map<String, String> roleMap = userIds.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        x -> require.getRoleSetFromUserId(x.getValue(), date)
                                .map(RoleSetExportDto::getEmploymentRoleId).orElse("")))
                .entrySet().stream().filter(x -> !x.getValue().equals(""))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return roleMap;
    }

    public static interface Require {
        Optional<String> getUserIDByEmpID(String employeeID);

        Optional<RoleSetExportDto> getRoleSetFromUserId(String userId, GeneralDate baseDate);
    }
}
