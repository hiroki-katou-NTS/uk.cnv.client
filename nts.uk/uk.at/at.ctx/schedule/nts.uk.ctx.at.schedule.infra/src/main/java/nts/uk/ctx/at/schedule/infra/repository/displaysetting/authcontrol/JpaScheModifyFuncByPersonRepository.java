package nts.uk.ctx.at.schedule.infra.repository.displaysetting.authcontrol;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.uk.ctx.at.schedule.dom.displaysetting.authcontrol.ScheModifyFuncByPerson;
import nts.uk.ctx.at.schedule.dom.displaysetting.authcontrol.ScheModifyFuncByPersonRepository;
import nts.uk.ctx.at.schedule.infra.entity.displaysetting.authcontrol.KscmtFuncByperson;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;

/**
 * @author viet.tx
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JpaScheModifyFuncByPersonRepository extends JpaRepository implements ScheModifyFuncByPersonRepository {
    @Override
    public List<ScheModifyFuncByPerson> getAll() {
        String sql = "SELECT * FROM KSCCT_FUNC_BYPERSON ORDER BY FUNCTION_NO ASC";

        return new NtsStatement(sql, this.jdbcProxy())
                .getList(x -> KscmtFuncByperson.MAPPER.toEntity(x).toDomain());
    }
}
