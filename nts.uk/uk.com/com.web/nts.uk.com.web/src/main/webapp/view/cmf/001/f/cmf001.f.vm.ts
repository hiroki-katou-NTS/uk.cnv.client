/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
module nts.uk.com.view.cmf001.f.viewmodel {
	import ajax = nts.uk.request.ajax;
	import info = nts.uk.ui.dialog.info;
	import setShared = nts.uk.ui.windows.setShared;
	import getShared = nts.uk.ui.windows.getShared;

	function deleteButton(required, data) {
		if (required === false) {
				return '<button type="button" class="delete-button" data-target="'+ data.itemNo +'">削除</button>';
		} else {
				return '';
		}
	}

	$(function() {
		$(document).on("click",".delete-button",function(){
			let vm = nts.uk.ui._viewModel.content;
			vm.removeItem($(this).data("target"));
		});
	})

	@bean()
	class ViewModel extends ko.ViewModel {
        settingCode: string;
		
		itemNameRow: KnockoutObservable<number> = ko.observable();
		importStartRow: KnockoutObservable<number> = ko.observable();
		
		canEditDetail: KnockoutObservable<boolean> = ko.observable(false);
		
		//domain
		domainInfoList:KnockoutObservableArray<DomainInfo> = ko.observableArray([]);
		domainList: KnockoutObservableArray<ImportDomain> = ko.observableArray([]);
		layoutItemNoList: KnockoutObservableArray<number> = ko.observableArray([]);
		layout: KnockoutObservableArray<Layout> = ko.observableArray([]);

		importDomainOption: KnockoutObservableArray<any> = ko.observableArray(__viewContext.enums.ImportingDomainId);
		importDomain: KnockoutObservable<number> = ko.observable();
		selectedDomainId: KnockoutObservable<number> = ko.observable();
		
		//csv
		csvItemOption: KnockoutObservableArray<CsvItem> = ko.observableArray([]);

		selectedItem: KnockoutObservable<string> = ko.observable();
		
	    $grid!: JQuery;
		
		domainListColumns: KnockoutObservableArray<any> = ko.observableArray([
			{ headerText: "ID", 					key: "domainId", 		width: 50 , 	hidden: true },
			{ headerText: "受入ドメイン", 	key: "name", 			width: 280},
		]);

		constructor() {
			super();
			var self = this;
			
			var params = __viewContext.transferred.get();
			self.settingCode = params.settingCode;

			self.startPage();
			
			self.selectedDomainId.subscribe((value) => {
				if (value) {
					var info = $.grep(self.domainInfoList(), function (di) {
						return di.domainId == self.selectedDomainId();
					});
					if (info.length !== 0){
						self.setDomain(info[0]);
						self.canEditDetail(info[0].resistered);
						return;
					}
				}
				self.layoutItemNoList([]);
				self.canEditDetail(false);
			})
	
			self.layoutItemNoList.subscribe((value) => {
				self.setLayout(value);
			})
			
			if (params.domainId !== undefined){
				self.selectedDomainId(params.domainId);
			}
		}
		
		setDomain(info: DomainInfo) {
			let self = this;
			self.layoutItemNoList(info.itemNoList);
		}

		startPage(){
			var self = this;
			let dfd = $.Deferred();
						
			self.getListData().done(function() {
				if (self.domainList() !== null && self.domainList().length !== 0) {
					self.selectedDomainId(self.domainList()[0].domainId);
				}
			});
			
			self.$grid = $("#grid");
			self.initGrid();
		      
			return dfd.promise();
		}

		reloadPage(){
			var self = this;
			self.getListData().done(function() {
				self.initGrid();
			});
		}
		
		getListData() {
			var self = this;
			let dfd = $.Deferred();
			ajax("screen/com/cmf/cmf001/e/get/setting/" + self.settingCode)
			.done((res) => {
				let importDomains = $.map(res.domains, d => {
					let target = $.grep(__viewContext.enums.ImportingDomainId, (domain) => domain.value === d.domainId)[0];
					return new ImportDomain(d.domainId, target.name, true);
				});
				let importDomainInfoList = $.map(res.domains, d => {
					let target = $.grep(__viewContext.enums.ImportingDomainId, (domain) => domain.value === d.domainId)[0];
					return new DomainInfo(d.domainId, d.itemNoList, true);
				});
				self.domainList(importDomains);
				self.domainInfoList(importDomainInfoList);
				
				let csvItem = $.map(res.csvItems, function(value, index) {
					return new CsvItem(index + 1, value);
				});
				csvItem.unshift(new CsvItem(null, ''));
				self.csvItemOption=ko.observableArray(csvItem);

				dfd.resolve();
			}).fail(function(error) {
				dfd.reject();
				alert(error.message);
			})
			return dfd.promise();
		}
		
		initGrid(){
			var self = this;
			var comboColumns = [
				{ prop: "no",	length:2 },
				{ prop: "name", length:10}
			];
			if (self.$grid.data("igGrid")) {
				self.$grid.ntsGrid("destroy");
			}
			
			self.$grid.ntsGrid({
				height: '300px',
				dataSource: self.layout(),
		        primaryKey: 'itemNo',
		        rowVirtualization: true,
		        virtualization: true,
		        virtualizationMode: 'continuous',
		        columns: [
					{ headerText: "削除", 				key: "required", 			dataType: 'boolean',	width: 50, formatter: deleteButton},
					{ headerText: "NO", 					key: "itemNo", 				dataType: 'number',	width: 50, 	hidden: true },
					{ headerText: "名称", 				key: "name", 				dataType: 'string',		width: 250},
					{ headerText: "受入元", 				key: "isFixedValue",		dataType: 'number',	width: 130, ntsControl: 'SwitchButtons'},
					{ headerText: "CSVヘッダ名", 	key: "selectedCsvItemNo",	dataType: 'number',	width: 220, ntsControl: 'Combobox' },
					{ headerText: "サンプルデータ", 				key: "csvData", 				dataType: 'string',		width: 120	}
				],
		        features: [
		          {
		          },
		        ],
		        ntsControls: [
		          {
			            name: 'SwitchButtons',
			            options: [{ value:0, text: 'CSV' },{ value:1, text: '固定値' }],
                        optionsValue: 'value',
                        optionsText: 'text',
                        controlType: 'SwitchButtons',
                        enable: true 
		          },
		          {
		            name: 'Combobox',
		            options: self.csvItemOption(),
		            optionsValue: 'no',
		            optionsText: 'name',
		            columns: comboColumns,
		            controlType: 'ComboBox',
		            visibleItemsCount: 5,
		            dropDownAttachedToBody: false,
		        	selectFirstIfNull: false,
		            enable: true
		          }
		        ]
		      });
		}
		
		removeItem(target: number){
			let self = this;
			let index = self.layoutItemNoList().findIndex((item) => item === target);
			self.layoutItemNoList().splice(index, 1);
			self.setLayout(self.layoutItemNoList());
		}

		checkError(){
			nts.uk.ui.errors.clearAll()
			$('.check-target').ntsError('check');
		}

		setLayout(itemNoList: number[]){
			let self = this;
			if(itemNoList.length > 0){
				let condition = {
					settingCode: self.settingCode,
					importingDomainId: self.selectedDomainId(),
					itemNoList: itemNoList};
				ajax("screen/com/cmf/cmf001/f/get/layout/detail", condition).done((layoutItems: Array<viewmodel.Layout>) => {
					self.layout(layoutItems);
					self.initGrid();
				});
			}else{
				self.layout([]);
			}
		}

		canSave = ko.computed(() => !nts.uk.ui.errors.hasError() );
	
		save(){
			let self = this;
			self.checkError();
			if(!nts.uk.ui.errors.hasError()){
				let domains = $.map(self.layout(), l =>{
					return {
						itemNo: l.itemNo,
						isFixedValue: l.isFixedValue,
						csvItemNo: l.selectedCsvItemNo,
						fixedValue: l.fixedValue
					}
				});
				let saveContents = {
					code: self.settingCode,
					domainId: self.selectedDomainId(),
					items: domains
				};
				ajax("screen/com/cmf/cmf001/f/save", saveContents).done(() => {
					info(nts.uk.resource.getMessage("Msg_15", []));
					self.reloadPage();
	            });
			}
		}

		uploadCsv() {
			let self = this;
		}
		
		addImportDomain() {
			let self = this;
			
			let index = self.domainInfoList().findIndex((di) => di.domainId === self.importDomain());
			if(index != -1){
				info("既に追加されています");
				return;
			}

			let selected = $.grep(__viewContext.enums.ImportingDomainId, (domain) => domain.value === self.importDomain())[0];
			self.domainList.push(new ImportDomain(selected.value, selected.name, true ));
			
			let condition = {
				settingCode: self.settingCode,
				importingDomainId: self.importDomain(),
				itemNoList: []};
			ajax("com", "screen/com/cmf/cmf001/b/get/layout", condition)
			.done((itemNoList: number[]) => {
				self.domainInfoList.push(new DomainInfo(self.importDomain(), itemNoList), false);
				self.selectedDomainId(self.importDomain());
				self.importDomain(null);

			});
		}
		
		deleteImportDomain() {
			let self = this;

	        ui.dialog.confirm({ messageId: "Msg_18" }).ifYes(() => {
				ajax("screen/com/cmf/cmf001/f/delete", {
					code: self.settingCode,
					domainId: self.selectedDomainId()
				}).done(() => {
					info(nts.uk.resource.getMessage("Msg_16", []));
					
					let index = self.domainInfoList().findIndex((di) => di.domainId === self.selectedDomainId());
					self.domainInfoList.splice(index, 1);
					self.domainList.splice(index, 1);
					self.selectedDomainId(null);
					
					self.reloadPage();
	            });
	        });
		}

		selectLayout() {
			let self = this;
			setShared('CMF001DParams', {
					domainId: self.selectedDomainId(),
					selectedItems: self.layoutItemNoList()
			}, true);
	
			nts.uk.ui.windows.sub.modal("/view/cmf/001/d/index.xhtml").onClosed(function() {
				// ダイアログを閉じたときの処理
				if(!getShared('CMF001DCancel')){
					let ItemNoList: string[] = getShared('CMF001DOutput')
					console.log("closed: " + ItemNoList)
					ko.utils.arrayPushAll(self.layoutItemNoList, ItemNoList.map(n => Number(n)));
				}
			});
		}

		gotoDetailSetting() {
			let self = this;
			request.jump("../c/index.xhtml", {
				settingCode: self.settingCode,
				domainId: self.selectedDomainId(),
				screenId: 'cmf001f'
			});
		}
	}

	export class Setting {
		code: string;
		name: string;
	
		constructor(code: string, name: string) {
				this.code = code;
				this.name = name;
		}
	}
	
	export class SettingInfo {
		companyId: string;
		code: string;
		name: string;
		itemNameRow: number;
		importStartRow: number;
	
		constructor(companyId: string, code: string, name: string, itemNameRow: number, importStartRow: number) {
			this.companyId = companyId;
			this.code = code;
			this.name = name;
			this.itemNameRow = itemNameRow;
			this.importStartRow = importStartRow;
		}
	
		static new(){
			return new SettingInfo(__viewContext.user.companyId, "", "", null, null, null)
		}
	}

	export class ImportDomain {
		domainId: number;
		name: string;
		deletable: boolean;
	
		constructor(domainId: number, name: string, deletable: boolean) {
				this.domainId = domainId;
				this.name = name;
				this.deletable = deletable;
		}
	}
	
	export class DomainInfo {
		domainId: number;
		itemNoList: Array<number>;
		resistered: boolean;
		
		constructor(domainId: number, itemNoList: Array<number>, resistered: boolean) {
			this.domainId = domainId;
			this.itemNoList = itemNoList;
			this.resistered = resistered;
		}
	
		static new(){
			return new DomainInfo(null, [], false)
		}
	}
	
	export class Layout {
		csvData: string;
		fixedValue: string;
		itemNo: number;
		name: string;
		required: boolean;
		selectedCsvItemNo: number;
		isFixedValue: number;
	
		constructor(itemNo: number, name: string, required: boolean, selectedCsvItemNo: number, fixedValue: string, csvData: string, isFixedValue: number) {
			this.itemNo = itemNo;
			this.name = name;
			this.required = required;
			this.selectedCsvItemNo = selectedCsvItemNo;
			this.fixedValue = fixedValue;
			this.csvData = csvData;
			this.isFixedValue = isFixedValue;
		}
	}
	
	export class CsvItem {
	    no: number;
	    name: string;
	
	    constructor(no: number, name: string) {
	        this.no = no;
	        this.name = name;
	    }
	}
}