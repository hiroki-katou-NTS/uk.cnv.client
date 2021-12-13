/**
 * 
 */
package nts.uk.ctx.pereg.app.find.layout.groupitem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.pereg.app.find.person.category.PerInfoCategoryFinder;
import nts.uk.ctx.pereg.app.find.person.category.PerInfoCtgFullDto;
import nts.uk.ctx.pereg.app.find.person.info.item.PerInfoItemDefDto;
import nts.uk.ctx.pereg.app.find.person.info.item.PerInfoItemDefFinder;
import nts.uk.ctx.pereg.app.find.person.info.item.SetItemDto;
import nts.uk.ctx.pereg.dom.person.groupitem.IPersonInfoItemGroupRepository;
import nts.uk.ctx.pereg.dom.person.groupitem.PersonInfoItemGroup;

@Stateless
public class PersonInfoItemGroupFinder {

	@Inject
	private IPersonInfoItemGroupRepository repo;

	@Inject
	PerInfoCategoryFinder categoryFinder;

	@Inject
	private PerInfoItemDefFinder itemDfFinder;

	/**
	 * Get All GroupItem
	 * 
	 * @return
	 */
	public List<PersonInfoItemGroupDto> getAllPersonInfoGroup() {
		return this.repo.getAll().stream().map(item -> {
			if (getAllItemDf(item.getPersonInfoItemGroupID()).size() > 0) {
				return PersonInfoItemGroupDto.fromDomain(item);
			}
			return null;
		}).filter(m -> m != null).collect(Collectors.toList());
	}

	/**
	 * 
	 * @param groupId
	 * @return
	 */
	public PersonInfoItemGroupDto getById(String groupId) {
		Optional<PersonInfoItemGroup> groupItem = this.repo.getById(groupId);

		if (!groupItem.isPresent()) {
			return null;
		}

		return PersonInfoItemGroupDto.fromDomain(groupItem.get());
	}

	/**
	 * Get All Item Difination
	 * 
	 * @return
	 */
	public List<PerInfoItemDefDto> getAllItemDf(String groupId) {
		List<String> listItemDfId = this.repo.getListItemIdByGrId(groupId);

		if (listItemDfId.isEmpty()) {
			return null;
		} else {
			List<PerInfoItemDefDto> items = itemDfFinder.getPerInfoItemDefByListId(listItemDfId);

			return items.stream().map(m -> {
				PerInfoCtgFullDto cat = categoryFinder.getPerInfoCtg(m.getPerInfoCtgId());
				if (cat.getIsAbolition() == 0 && m.getIsAbolition() == 0) {
					return m;
				}
				return null;
			}).filter(f -> f != null).map(m -> {
				int catDispOrder = categoryFinder.getDispOrder(m.getPerInfoCtgId());
				m.setDispOrder(catDispOrder * 100000 + m.getDispOrder());
				return m;
			}).sorted((o1, o2) -> o1.getDispOrder() - o2.getDispOrder()).collect(Collectors.toList());
		}
	}

	/**
	 * Get All Item Difination
	 * 
	 * @return
	 */
	public List<PerInfoItemDefDto> getAllItemDfTest(String groupId) {
		List<String> listItemDfId = this.repo.getListItemIdByGrId(groupId);

		if (listItemDfId.isEmpty()) {
			return null;
		} else {
			List<PerInfoItemDefDto> items = itemDfFinder.getPerInfoItemDefByListId(listItemDfId);

			List<PerInfoItemDefDto> itemDfChild = new ArrayList<>();
			List<String> idsChild = new ArrayList<>();
			List<String> idsChildInChild = new ArrayList<>();
			List<PerInfoItemDefDto> itemDfChildinChild = new ArrayList<>();

			List<PerInfoItemDefDto> result = items.stream().map(m -> {

				if (m.getItemTypeState().getItemType() == 1
						&& (((SetItemDto) m.getItemTypeState()).getItems() != null)) {

					idsChild.addAll(((SetItemDto) m.getItemTypeState()).getItems());
				}

				PerInfoCtgFullDto cat = categoryFinder.getPerInfoCtg(m.getPerInfoCtgId());

				if (cat.getIsAbolition() == 0 && m.getIsAbolition() == 0) {
					return m;
				}
				return null;
			}).filter(f -> f != null).map(m -> {
				int catDispOrder = categoryFinder.getDispOrder(m.getPerInfoCtgId());
				m.setDispOrder(catDispOrder * 100000 + m.getDispOrder());
				return m;
			}).sorted((o1, o2) -> o1.getDispOrder() - o2.getDispOrder()).collect(Collectors.toList());

			if (!idsChild.isEmpty()) {

				// Lay List ItemDf theo idsChild
				itemDfChild = itemDfFinder.getPerInfoItemDefByListId(idsChild);

				// List ItemDf sau khi check Abolition va sap xep
				List<PerInfoItemDefDto> lstItemDfDto = itemDfChild.stream().map(m -> {
					
					if (m.getItemTypeState().getItemType() == 1
							&& (((SetItemDto) m.getItemTypeState()).getItems() != null)) {

						idsChildInChild.addAll(((SetItemDto) m.getItemTypeState()).getItems());
					}

					PerInfoCtgFullDto cat = categoryFinder.getPerInfoCtg(m.getPerInfoCtgId());

					if (cat.getIsAbolition() == 0 && m.getIsAbolition() == 0) {
						return m;
					}
					return null;
				}).filter(f -> f != null).map(m -> {
					int catDispOrder = categoryFinder.getDispOrder(m.getPerInfoCtgId());
					m.setDispOrder(catDispOrder * 100000 + m.getDispOrder());
					return m;
				}).sorted((o1, o2) -> o1.getDispOrder() - o2.getDispOrder()).collect(Collectors.toList());

				result.addAll(lstItemDfDto);

			}
			
			if (!idsChildInChild.isEmpty()) {

				// Lay List ItemDf theo idsChild of Child
				itemDfChildinChild = itemDfFinder.getPerInfoItemDefByListId(idsChildInChild);

				// List ItemDf sau khi check Abolition va sap xep
				List<PerInfoItemDefDto> lstItemDfDtoChild = itemDfChildinChild.stream().map(m -> {
					
					if (m.getItemTypeState().getItemType() == 1
							&& (((SetItemDto) m.getItemTypeState()).getItems() != null)) {

						idsChildInChild.addAll(((SetItemDto) m.getItemTypeState()).getItems());
					}

					PerInfoCtgFullDto cat = categoryFinder.getPerInfoCtg(m.getPerInfoCtgId());

					if (cat.getIsAbolition() == 0 && m.getIsAbolition() == 0) {
						return m;
					}
					return null;
				}).filter(f -> f != null).map(m -> {
					int catDispOrder = categoryFinder.getDispOrder(m.getPerInfoCtgId());
					m.setDispOrder(catDispOrder * 100000 + m.getDispOrder());
					return m;
				}).sorted((o1, o2) -> o1.getDispOrder() - o2.getDispOrder()).collect(Collectors.toList());

				result.addAll(lstItemDfDtoChild);
			}

			return result;

		}
	}

	public List<PerInfoItemDefDto> getAllItemDfFromListGroup(List<String> groupIds) {

		if (groupIds.isEmpty()) {
			return null;
		}

		List<PerInfoItemDefDto> result = new ArrayList<PerInfoItemDefDto>();

		groupIds.stream().forEach(f -> {
			List<PerInfoItemDefDto> lstItemDfDto = getAllItemDfTest(f);
			if (lstItemDfDto != null) {
				Optional<PersonInfoItemGroup> groupOp = repo.getById(f);
			
				if (groupOp.isPresent()) {
					lstItemDfDto.forEach(item -> {
						item.setPersonInfoItemGroupId(f);
						item.setFieldGroupName(groupOp.get().getFieldGroupName().v());
					});
				}
				result.addAll(lstItemDfDto);
			}
		});

		return result;
	}
}
