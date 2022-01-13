package nts.uk.ctx.exio.app.command.exo.authset;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.shr.com.permit.RestoreAvailabilityPermission;

@NoArgsConstructor
@Getter
public class ExOutCtgAvailabilityPermissionImpl implements RestoreAvailabilityPermission {
    String cid;

    String roleId;

    int functionNo;

    boolean available;

    public ExOutCtgAvailabilityPermissionImpl(String cid, String roleId, int functionNo, boolean available) {
        this.cid = cid;
        this.roleId = roleId;
        this.functionNo = functionNo;
        this.available = available;
    }

    @Override
    public String companyId() {
        return this.cid;
    }

    @Override
    public String roleId() {
        return this.roleId;
    }

    @Override
    public int functionNo() {
        return this.functionNo;
    }

    @Override
    public boolean isAvailable() {
        return this.available;
    }
}
