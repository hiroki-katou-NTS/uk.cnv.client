package nts.uk.ctx.exio.dom.exo.authset;

import nts.uk.shr.com.permit.AvailabilityPermissionRepositoryBase;

import java.util.List;

/**
 * 外部出力カテゴリ利用権限の設定
 */
public interface ExOutCtgAuthSetRepository extends AvailabilityPermissionRepositoryBase<ExOutCtgAuthSet> {
    List<ExOutCtgAuthSet> findByCidAndRoleId(String cid, String roleId);
    void delete(String cid, String roleId);
}
