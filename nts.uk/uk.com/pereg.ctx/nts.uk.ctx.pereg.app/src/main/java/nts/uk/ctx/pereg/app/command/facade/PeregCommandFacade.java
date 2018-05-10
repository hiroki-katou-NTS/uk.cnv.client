package nts.uk.ctx.pereg.app.command.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;

import lombok.val;
import nts.uk.ctx.pereg.app.find.processor.ItemDefFinder;
import nts.uk.shr.pereg.app.ItemValue;
import nts.uk.shr.pereg.app.command.ItemsByCategory;
import nts.uk.shr.pereg.app.command.PeregAddCommandHandler;
import nts.uk.shr.pereg.app.command.PeregCommandHandlerCollector;
import nts.uk.shr.pereg.app.command.PeregDeleteCommand;
import nts.uk.shr.pereg.app.command.PeregDeleteCommandHandler;
import nts.uk.shr.pereg.app.command.PeregInputContainer;
import nts.uk.shr.pereg.app.command.PeregUpdateCommandHandler;
import nts.uk.shr.pereg.app.command.userdef.PeregUserDefAddCommand;
import nts.uk.shr.pereg.app.command.userdef.PeregUserDefAddCommandHandler;
import nts.uk.shr.pereg.app.command.userdef.PeregUserDefDeleteCommand;
import nts.uk.shr.pereg.app.command.userdef.PeregUserDefDeleteCommandHandler;
import nts.uk.shr.pereg.app.command.userdef.PeregUserDefUpdateCommand;
import nts.uk.shr.pereg.app.command.userdef.PeregUserDefUpdateCommandHandler;
import nts.uk.shr.pereg.app.find.PeregQuery;

@ApplicationScoped
public class PeregCommandFacade {

	@Inject
	private PeregCommandHandlerCollector handlerCollector;

	/** Command handlers to add */
	private Map<String, PeregAddCommandHandler<?>> addHandlers;

	/** Command handlers to update */
	private Map<String, PeregUpdateCommandHandler<?>> updateHandlers;

	/** Command handlers to delete */
	private Map<String, PeregDeleteCommandHandler<?>> deleteHandlers;

	/** this handles command to add data defined by user. */
	@Inject
	private PeregUserDefAddCommandHandler userDefAdd;

	/** this handles command to update data defined by user. */
	@Inject
	private PeregUserDefUpdateCommandHandler userDefUpdate;

	/** this handles command to delete data defined by user. */
	@Inject
	private PeregUserDefDeleteCommandHandler userDefDelete;

	@Inject
	private ItemDefFinder itemDefFinder;

	/**
	 * Initializes.
	 */
	public void init(@Observes @Initialized(ApplicationScoped.class) Object event) {

		this.addHandlers = this.handlerCollector.collectAddHandlers().stream()
				.collect(Collectors.toMap(h -> h.targetCategoryCd(), h -> h));

		this.updateHandlers = this.handlerCollector.collectUpdateHandlers().stream()
				.collect(Collectors.toMap(h -> h.targetCategoryCd(), h -> h));

		this.deleteHandlers = this.handlerCollector.collectDeleteHandlers().stream()
				.collect(Collectors.toMap(h -> h.targetCategoryCd(), h -> h));

	}

	/**
	 * Handles add commands.
	 * 
	 * @param container
	 *            inputs
	 */
	@Transactional
	public String add(PeregInputContainer container) {
		return addNonTransaction(container);
	}

	private String addNonTransaction(PeregInputContainer container) {
		// Filter input category
		List<ItemsByCategory> addInputs = container.getInputs().stream()
				.filter(p -> StringUtils.isEmpty(p.getRecordId())).collect(Collectors.toList());
		PeregInputContainer addPeregInputContainer = new PeregInputContainer(container.getPersonId(),
				container.getEmployeeId(), addInputs);
		List<String> recordIds = new ArrayList<String>();

		addPeregInputContainer.getInputs().forEach(itemsByCategory -> {
			val handler = this.addHandlers.get(itemsByCategory.getCategoryCd());
			// In case of optional category fix category doesn't exist
			String recordId = null;
			if (handler != null && itemsByCategory.getItems().stream().anyMatch(i -> i.itemCode().charAt(1) == 'S')) {
				val result = handler.handlePeregCommand(container.getPersonId(), container.getEmployeeId(),
						itemsByCategory);
				// pass new record ID that was generated by add domain command
				recordId = result.getAddedRecordId();
			}
			// In case of add more optional item when fixed items are already registed
			ItemsByCategory sameCategoryItem = container.getInputs().stream()
					.filter(c -> c.getCategoryCd().equals(itemsByCategory.getCategoryCd())
							&& StringUtils.isNotEmpty(c.getRecordId()))
					.findFirst().orElse(null);
			if (sameCategoryItem != null) {
				// Get recored id of fixed category are registed already
				recordId = sameCategoryItem.getRecordId();
			}
			// pass new record ID that was generated by add domain command
			// handler
			val commandForUserDef = new PeregUserDefAddCommand(container.getPersonId(), container.getEmployeeId(),
					recordId, itemsByCategory);

			this.userDefAdd.handle(commandForUserDef);

			// Keep record id to focus in UI
			recordIds.add(recordId);
		});

		if (recordIds.size() == 1) {
			return recordIds.get(0);
		}
		return null;
	}

	/**
	 * Handles update commands.
	 * 
	 * @param container
	 *            inputs
	 */
	@Transactional
	public void update(PeregInputContainer container) {
		updateNonTransaction(container);
	}

	private void updateNonTransaction(PeregInputContainer container) {

		List<ItemsByCategory> updateInputs = container.getInputs().stream()
				.filter(p -> !StringUtils.isEmpty(p.getRecordId())).collect(Collectors.toList());

		if (updateInputs != null && !updateInputs.isEmpty()) {
			// Add item invisible to list
			for (ItemsByCategory itemByCategory : updateInputs) {

				PeregQuery query = new PeregQuery(itemByCategory.getRecordId(), itemByCategory.getCategoryCd(),
						container.getEmployeeId(), container.getPersonId());

				List<ItemValue> fullItems = itemDefFinder.getFullListItemDef(query);
				List<String> visibleItemCodes = itemByCategory.getItems().stream().map(ItemValue::itemCode)
						.collect(Collectors.toList());

				// List item invisible
				List<ItemValue> itemInvisible = fullItems.stream().filter(i -> {
					return i.itemCode().indexOf("O") == -1 && !visibleItemCodes.contains(i.itemCode());
				}).collect(Collectors.toList());

				itemByCategory.getItems().addAll(itemInvisible);
			}

		}

		updateInputs.forEach(itemsByCategory -> {
			val handler = this.updateHandlers.get(itemsByCategory.getCategoryCd());
			// In case of optional category fix category doesn't exist
			if (handler != null) {
				handler.handlePeregCommand(container.getPersonId(), container.getEmployeeId(), itemsByCategory);
			}
			val commandForUserDef = new PeregUserDefUpdateCommand(container.getPersonId(), container.getEmployeeId(),
					itemsByCategory);
			this.userDefUpdate.handle(commandForUserDef);
		});
	}

	@Transactional
	public Object register(PeregInputContainer inputContainer) {
		
		// ADD COMMAND
		String recordId = this.add(inputContainer);

		// UPDATE COMMAND
		this.update(inputContainer);

		return new Object[] { recordId };
	}

	/**
	 * Handles delete command.
	 * 
	 * @param command
	 *            command
	 */
	@Transactional
	public void delete(PeregDeleteCommand command) {

		val handler = this.deleteHandlers.get(command.getCategoryId());
		if (handler != null) {
			handler.handlePeregCommand(command);
		}
		val commandForUserDef = new PeregUserDefDeleteCommand(command);
		this.userDefDelete.handle(commandForUserDef);
	}

	/**
	 * return List Category Code
	 * 
	 */
	public List<String> getAddCategoryCodeList() {

		List<String> ctgCodeList = new ArrayList<String>();
		this.addHandlers.forEach((k, v) -> {
			ctgCodeList.add(k);
		});
		return ctgCodeList;

	}
}
