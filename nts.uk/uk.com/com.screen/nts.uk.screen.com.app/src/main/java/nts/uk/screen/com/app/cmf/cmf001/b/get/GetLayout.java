package nts.uk.screen.com.app.cmf.cmf001.b.get;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItem;
import nts.uk.ctx.exio.dom.input.setting.DomainImportSetting;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.ctx.exio.dom.input.setting.assembly.mapping.ImportingItemMapping;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class GetLayout {
	
	@Inject
	private GetLayoutRequire require;
	
	public List<Integer> get(GetLayoutParam query) {
		val require = this.require.create();
		
		val settingOpt = require.getSetting(AppContexts.user().companyId(), query.getSettingCode());
		if (settingOpt.isPresent()) {
			val setting = settingOpt.get();
			Optional<DomainImportSetting> domainSetting = setting.getDomainSetting(query.getImportingDomainId());
			if (domainSetting.isPresent()) {
				// 設定されている項目
				return domainSetting.get().getAssembly().getMapping().getMappings().stream()
						.map(m -> m.getItemNo())
						.collect(Collectors.toList());
			}
		}
		// デフォルトで全項目
		val importableItems = require.getImportableItems(query.getImportingDomainId());
		return importableItems.stream()
				.map(i -> i.getItemNo())
				.collect(Collectors.toList());
	}
	
	public List<ExternalImportLayoutDto> getDetail(GetLayoutParam query) {
		val require = this.require.create();
		
		if (query.isAllItem()) {
			return getAll(require, query);
		} else {
			return getSpecified(require, query);
		}
	}
	
	// すべての項目のレイアウトを取得する
	private List<ExternalImportLayoutDto> getAll(GetLayout.Require require, GetLayoutParam query) {
		
		val settingOpt = require.getSetting(AppContexts.user().companyId(), query.getSettingCode());
		if (settingOpt.isPresent()) {
 			val setting = settingOpt.get();
			Optional<DomainImportSetting> domainSetting = setting.getDomainSetting(query.getImportingDomainId());
			return getSaved(require, query, domainSetting);
		}
		else {
			return getAllImportables(require, query);
		}
	}
	
	// 指定した項目のレイアウトを取得する
	private List<ExternalImportLayoutDto> getSpecified(GetLayout.Require require, GetLayoutParam query) {
		
		val results = new ArrayList<ExternalImportLayoutDto>();
		
		val settingOpt = require.getSetting(AppContexts.user().companyId(), query.getSettingCode());
		if (settingOpt.isPresent()) {
 			val setting = settingOpt.get();
			Optional<DomainImportSetting> domainSetting = setting.getDomainSetting(query.getImportingDomainId());
			results.addAll(getSaved(require, query, domainSetting).stream()
					.filter(s -> query.getItemNoList().contains(s.getItemNo()))
					.collect(toList()));
		}
		
		val savedItemNos = results.stream().map(l -> l.getItemNo()).collect(Collectors.toSet());
		
		// 指定した項目のうち未登録の項目	
		val unsavedItemNos = query.getItemNoList().stream()
				.filter(n -> !savedItemNos.contains(n))
				.collect(Collectors.toSet());
		
		val unsavedLayouts = getAllImportables(require, query).stream()
				.filter(l -> unsavedItemNos.contains(l.getItemNo()))
				.collect(toList());
		
		results.addAll(unsavedLayouts);
		
		return results;
	}
	
	private static List<ExternalImportLayoutDto> getAllImportables(GetLayout.Require require, GetLayoutParam query) {
		
		val importableItems = require.getImportableItems(query.getImportingDomainId());

		return importableItems.stream()
				.map(t -> ExternalImportLayoutDto.fromDomain(
						t,
						ImportingItemMapping.noSetting(t.getItemNo())))
				.collect(Collectors.toList());
	}
	
	private List<ExternalImportLayoutDto> getSaved(
			GetLayout.Require require,
			GetLayoutParam query,
			Optional<DomainImportSetting> setting) {
		List<ImportingItemMapping> mappings = setting.isPresent()
			? setting.get().getAssembly().getMapping().getMappings()
			: new ArrayList<>();
		return toLayouts(require, query, mappings);
	}
	
	private List<ExternalImportLayoutDto> toLayouts(GetLayout.Require require, GetLayoutParam query,
			List<ImportingItemMapping> mappings) {
		// 対象受入項目NO一覧
		val targetItemNoList = mappings.stream()
				.map(i -> i.getItemNo())
				.collect(Collectors.toList());
		// 対象受入項目の受入可能項目を取得
		val importableItems = require.getImportableItems(query.getImportingDomainId());
		val targetItems = importableItems.stream()
				.filter(i -> targetItemNoList.contains(i.getItemNo()))
				.collect(Collectors.toList());

		return targetItems.stream()
				.map(t -> ExternalImportLayoutDto.fromDomain(
						t,
						mappings.stream()
								.filter(m -> m.getItemNo() == t.getItemNo())
								.collect(Collectors.toList()).get(0)))
				.collect(Collectors.toList());
	}
	
	public static interface Require extends ExternalImportLayoutDto.Require {
		Optional<ExternalImportSetting> getSetting(String companyId, ExternalImportCode settingCode);
		
		List<ImportableItem> getImportableItems(ImportingDomainId domainId);
	}
}
