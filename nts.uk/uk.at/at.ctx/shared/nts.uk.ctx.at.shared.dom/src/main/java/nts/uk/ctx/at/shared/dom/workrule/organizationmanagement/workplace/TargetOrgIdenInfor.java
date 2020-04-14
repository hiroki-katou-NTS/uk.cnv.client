package nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace;

import java.util.Optional;

import lombok.Getter;
import lombok.Value;
import nts.arc.layer.dom.objecttype.DomainValue;

/**
 * VO : 対象組織識別情報
 * @author tutk
 *
 */
@Value
public class TargetOrgIdenInfor implements DomainValue {

	/**
	 * 単位
	 */
	@Getter
	private final TargetOrganizationUnit unit;
	
	/**
	 * 職場ID
	 */
	@Getter
	private final Optional<String> workplaceId;
	
	/**
	 * 職場グループID
	 */
	@Getter
	private final Optional<String> workplaceGroupId;

	public TargetOrgIdenInfor(TargetOrganizationUnit unit, String workplaceId, String workplaceGroupId) {
		super();
		this.unit = unit;
		this.workplaceId = Optional.ofNullable(workplaceId);
		this.workplaceGroupId = Optional.ofNullable(workplaceGroupId);
	}
	
	
	
}
