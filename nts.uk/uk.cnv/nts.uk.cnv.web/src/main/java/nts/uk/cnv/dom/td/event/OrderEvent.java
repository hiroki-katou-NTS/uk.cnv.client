package nts.uk.cnv.dom.td.event;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nts.arc.time.GeneralDateTime;
import nts.uk.cnv.dom.td.event.EventIdProvider.ProvideOrderIdRequire;

/**
 * 発注イベント
 * @author ai_muto
 *
 */
@Getter
@AllArgsConstructor
public class OrderEvent {
	private EventId eventId;
	private EventDetail detail;

	public static OrderEvent create(ProvideOrderIdRequire require, String eventName, String userName, List<String> alterationIds) {
		EventId id = EventIdProvider.provideOrderId(require);
		return new OrderEvent(
				id,
				new EventDetail(
					eventName,
					GeneralDateTime.now(),
					userName,
					alterationIds));
	}

	@RequiredArgsConstructor
	private class RequireImpl implements EventIdProvider.ProvideOrderIdRequire{
		private final OrderEventRepository repository;

		@Override
		public Optional<String> getNewestOrderId() {
			return repository.getNewestOrderId();
		}
	}
}
