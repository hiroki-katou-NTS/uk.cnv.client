package nts.uk.ctx.at.schedule.infra.repository.displaysetting.authcontrol;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.uk.ctx.at.schedule.dom.displaysetting.authcontrol.ScheModifyFuncCommon;
import nts.uk.ctx.at.schedule.dom.displaysetting.authcontrol.ScheModifyFuncCommonRepository;
import nts.uk.ctx.at.schedule.infra.entity.displaysetting.authcontrol.KscmtFuncCommon;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;

/**
 * @author viet.tx
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JpaScheModifyFuncCommonRepository extends JpaRepository implements ScheModifyFuncCommonRepository {
    @Override
    public List<ScheModifyFuncCommon> getAll() {
        String sql = "SELECT * FROM KSCCT_FUNC_COMMON ORDER BY FUNCTION_NO ASC";

        return new NtsStatement(sql, this.jdbcProxy())
                .getList(x -> KscmtFuncCommon.MAPPER.toEntity(x).toDomain());
    }
}
