package nts.uk.ctx.at.schedule.infra.repository.displaysetting.authcontrol;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.uk.ctx.at.schedule.dom.displaysetting.authcontrol.ScheModifyFuncByWorkplace;
import nts.uk.ctx.at.schedule.dom.displaysetting.authcontrol.ScheModifyFuncByWorkplaceRepository;
import nts.uk.ctx.at.schedule.infra.entity.displaysetting.authcontrol.KscmtFuncBywkp;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;

/**
 * @author viet.tx
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JpaScheModifyFuncByWorkplaceRepository extends JpaRepository implements ScheModifyFuncByWorkplaceRepository {
    @Override
    public List<ScheModifyFuncByWorkplace> getAll() {
        String sql = "SELECT * FROM KSCCT_FUNC_BYWKP ORDER BY FUNCTION_NO ASC";

        return new NtsStatement(sql, this.jdbcProxy())
                .getList(x -> KscmtFuncBywkp.MAPPER.toEntity(x).toDomain());
    }
}
