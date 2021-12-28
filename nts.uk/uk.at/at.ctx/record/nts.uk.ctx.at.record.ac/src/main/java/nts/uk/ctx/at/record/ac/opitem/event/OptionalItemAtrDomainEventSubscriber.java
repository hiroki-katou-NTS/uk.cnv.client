package nts.uk.ctx.at.record.ac.opitem.event;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.dom.event.DomainEventSubscriber;
import nts.uk.ctx.at.shared.dom.scherec.event.OptionalItemAtrExport;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemUpdateDomainEvent;
import nts.uk.ctx.at.shared.dom.scherec.service.AttendanceAtrService;

@Stateless
public class OptionalItemAtrDomainEventSubscriber implements DomainEventSubscriber<OptionalItemUpdateDomainEvent> {

	@Inject
	private AttendanceAtrService attendanceAtrService;

	@Override
	public Class<OptionalItemUpdateDomainEvent> subscribedToEventType() {
		return OptionalItemUpdateDomainEvent.class;
	}

	@Override
	public void handle(OptionalItemUpdateDomainEvent domainEvent) {
//		OptionalItemAtrExport optItem = new OptionalItemAtrExport(
//				domainEvent.getPerformanceAtr().value,
//				domainEvent.getOptionalItemAtr().value,
//				domainEvent.getOptionalItemNo().v()
//		);
//		attendanceAtrService.updateAttendanceAtr(optItem);
	}

}
