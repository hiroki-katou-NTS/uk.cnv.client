package nts.uk.ctx.at.auth.infra.entity.employmentrole;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class KacmtRoleAttendancePK implements Serializable {

	private static final long serialVersionUID = 1893249814119833566L;

	@Column(name = "ROLE_ID")
	public String roleID;

}
