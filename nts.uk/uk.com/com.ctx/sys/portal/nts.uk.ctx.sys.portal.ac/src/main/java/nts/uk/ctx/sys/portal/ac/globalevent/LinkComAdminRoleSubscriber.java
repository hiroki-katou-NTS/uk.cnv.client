package nts.uk.ctx.sys.portal.ac.globalevent;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.dom.event.DomainEventSubscriber;
import nts.uk.ctx.sys.auth.pub.event.RoleByRoleTiesGlobalEvent;
import nts.uk.ctx.sys.portal.dom.webmenu.WebMenuCode;
import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.RoleByRoleTies;
import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.RoleByRoleTiesRepository;
import nts.uk.shr.com.constants.DefaultSettingKeys;
@Stateless
public class LinkComAdminRoleSubscriber implements DomainEventSubscriber<RoleByRoleTiesGlobalEvent> {
	@Inject
	private RoleByRoleTiesRepository roleTiesRepo;

	@Override
	public Class<RoleByRoleTiesGlobalEvent> subscribedToEventType() {
		return RoleByRoleTiesGlobalEvent.class;
	}

	@Override
	public void handle(RoleByRoleTiesGlobalEvent domainEvent) {
		val roleTies = new RoleByRoleTies(domainEvent.getRoleId(), DefaultSettingKeys.COMPANY_ID, new WebMenuCode("001"));
		this.roleTiesRepo.insertRoleByRoleTies(roleTies);
	}
}
	