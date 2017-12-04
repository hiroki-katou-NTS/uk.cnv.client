package nts.uk.ctx.at.auth.infra.entity.wkpmanager;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class KacmtWorkplaceManagerPK implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Column(name = "WKP_MANAGER_ID")
	public String workplaceManagerId;
}
