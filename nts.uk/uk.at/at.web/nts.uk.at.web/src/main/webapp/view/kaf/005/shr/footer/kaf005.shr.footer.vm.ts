module nts.uk.at.view.kaf005.shr.footer.viewmodel {
	const template = `
<div id="kaf005-footer" data-bind="with: $parent">
	<div class="table" style="margin-bottom: 5px;" data-bind="if: true">
		<div style="width: 120px" class="cell col-1">
			<div class="cell valign-center" data-bind="ntsFormLabel: {}, text: $i18n('KAF005_90', [messageInfos()[0].titleDrop()])"></div>
		</div>
		<div class="cell valign-center">
			<div id="combo-box" data-bind="ntsComboBox: {
					options: messageInfos()[0].listDrop,
					optionsValue: 'code',
					value: messageInfos()[0].selectedCode,
					optionsText: 'name',
					editable: true,
					enable: true,
					required: true,
					columns: [
						{ prop: 'code', length: 4 },
						{ prop: 'name', length: 10 },
					]}">
			</div>
		</div>
	</div>
	
	<div class="table" style="margin-bottom: 5px;" data-bind="if: true">
		<div style="width: 120px" class="cell col-1">
			<div class="cell valign-center" data-bind="ntsFormLabel: {}, text: $i18n('KAF005_92')"></div>
		</div>
		<div class="cell valign-center">
			<input style="width: 390px" data-bind="ntsTextEditor: {value: ko.observable('')}" />
		</div>
	</div>
	
	
	<div class="table" style="margin-bottom: 5px;" data-bind="if: true">
		<div style="width: 120px" class="cell col-1">
			<div class="cell valign-center" data-bind="ntsFormLabel: {}, text: $i18n('KAF005_90')"></div>
		</div>
		<div class="cell valign-center">
			<div id="combo-box" data-bind="ntsComboBox: {
					options: messageInfos()[1].listDrop,
					optionsValue: 'code',
					value: messageInfos()[1].selectedCode,
					optionsText: 'name',
					editable: true,
					enable: true,
					required: true,
					columns: [
						{ prop: 'code', length: 4 },
						{ prop: 'name', length: 10 },
					]}">
			</div>
		</div>
	</div>
	
	<div class="table" style="margin-bottom: 5px;" data-bind="if: true">
		<div style="width: 120px" class="cell col-1">
			<div class="cell valign-center" data-bind="ntsFormLabel: {}, text: $i18n('KAF005_92')"></div>
		</div>
		<div class="cell valign-center">
			<input style="width: 390px" data-bind="ntsTextEditor: {value: ko.observable('')}" />
		</div>
	</div>
	
	
</div>

	`
	@component({
		name: 'kaf005-share-footer',
		template: template
	})
	class KAF005ShrFooterModel extends ko.ViewModel {
		itemList: KnockoutObservableArray<ItemModel>;
		selectedCode: KnockoutObservable<String>;
		created() {
			const self = this;
			
		}
		
		mounted() {
			const self = this;
		}
	}
	export class ItemModel {
	    code: string;
	    name: string;
	
	    constructor(code: string, name: string) {
	        this.code = code;
	        this.name = name;
	    }
	}
	
	export interface MessageInfo {
		selectedCode: KnockoutObservable<String>;
		titleDrop: KnockoutObservable<String>;
		listDrop: KnockoutObservableArray<ItemModel>;
		titleInput: KnockoutObservable<String>;
		valueInput: KnockoutObservable<String>;
	}
	
	
	
	
	
	
	
	
	
	
}