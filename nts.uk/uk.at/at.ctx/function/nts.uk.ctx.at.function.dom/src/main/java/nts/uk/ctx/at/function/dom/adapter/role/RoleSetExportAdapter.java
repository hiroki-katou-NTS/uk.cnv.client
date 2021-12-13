package nts.uk.ctx.at.function.dom.adapter.role;

import nts.arc.time.GeneralDate;

import java.util.Optional;

public interface RoleSetExportAdapter {
    Optional<RoleSetExportDto> getRoleSetFromUserId(String userId, GeneralDate baseDate);
}
