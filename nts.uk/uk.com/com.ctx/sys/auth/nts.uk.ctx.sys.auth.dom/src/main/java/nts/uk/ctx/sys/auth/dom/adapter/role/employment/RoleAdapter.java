package nts.uk.ctx.sys.auth.dom.adapter.role.employment;

import java.util.List;

public interface RoleAdapter {
    List<RoleExport> getListRole(List<String> personIds);
}
