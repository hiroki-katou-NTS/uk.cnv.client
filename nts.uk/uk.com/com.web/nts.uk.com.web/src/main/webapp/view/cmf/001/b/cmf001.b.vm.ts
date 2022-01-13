/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
module nts.uk.com.view.cmf001.b.viewmodel {
	import ajax = nts.uk.request.ajax;
	import info = nts.uk.ui.dialog.info;
	import setShared = nts.uk.ui.windows.setShared;
	import getShared = nts.uk.ui.windows.getShared;

	function deleteButton(required, data) {
		if (required === "false") {
				return '<button type="button" class="delete-button" data-target="'+ data.itemNo +'">削除</button>';
		} else {
				return '';
		}
	}

	$(function() {
		$("#layout-list").on("click",".delete-button",function(){
			let vm = nts.uk.ui._viewModel.content;
			vm.removeItem($(this).data("target"));
		});
	})

	@bean()
	class ViewModel extends ko.ViewModel {
		isNewMode: KnockoutObservable<boolean> = ko.observable(true);

		settingList: KnockoutObservableArray<Setting> = ko.observableArray([]);

		settingCode: KnockoutObservable<string> = ko.observable();
		settingName: KnockoutObservable<string> = ko.observable();
		importDomain: KnockoutObservable<number> = ko.observable();
		importMode: KnockoutObservable<number> = ko.observable();
		itemNameRow: KnockoutObservable<number> = ko.observable();
		importStartRow: KnockoutObservable<number> = ko.observable();
		layoutItemNoList: KnockoutObservableArray<number> = ko.observableArray([]);
		
		csvFileId:string = "";

		importDomainOption: KnockoutObservableArray<any> = ko.observableArray(__viewContext.enums.ImportingDomainId);
		importModeOption: KnockoutObservableArray<any> = ko.observableArray(__viewContext.enums.ImportingMode);

		selectedCode: KnockoutObservable<string> = ko.observable();

		layout: KnockoutObservableArray<Layout> = ko.observableArray([]);
		selectedItem: KnockoutObservable<string> = ko.observable();

		canAddItem: KnockoutObservable<boolean> =
			ko.computed(() => !util.isNullOrEmpty(this.importDomain()));
	    canEditDetail: KnockoutObservable<boolean> =
			ko.computed(() => !util.isNullOrEmpty(this.selectedCode()));
	    
		settingListColumns: KnockoutObservableArray<any> = ko.observableArray([
			{ headerText: "コード", 				key: "code", 					width: 50 	},
			{ headerText: "名称", 					key: "name", 					width: 200 	},
		]);

		layoutListColumns: KnockoutObservableArray<any> = ko.observableArray([
			{ headerText: "削除", 					key: "required", 		width: 50 , 	formatter: deleteButton },
			{ headerText: "NO", 						key: "itemNo", 				width: 100 , 	hidden: true },
			{ headerText: "名称", 					key: "name", 					width: 300 		},
			{ headerText: "型", 						key: "type", 					width: 80 		},
			{ headerText: "受入元", 				key: "source", 				width: 80 		},
		]);

		constructor() {
			super();
			var self = this;

			self.startPage();

			// 受入設定の変更検知
			self.selectedCode.subscribe((value) => {
				if (value) {
					// 選択した場合、更新モードへ
					self.updateMode();
				} else {
					// 選択解除した場合、新規モードへ
					self.newMode();
				}
			})

			self.importDomain.extend({notify: 'always'})
			self.importDomain.subscribe((value) => {
				if (value) {
					let condition = {
						settingCode: self.settingCode(),
						importingDomainId: self.importDomain(),
						itemNoList: []};
					ajax("com", "screen/com/cmf/cmf001/b/get/layout", condition).done((itemNoList: number[]) => {
						self.layoutItemNoList(itemNoList);
					});
				}else{
					self.layoutItemNoList([]);
				}
			})
	
			self.layoutItemNoList.subscribe((value) => {
				self.setLayout(value);
			})
		}

		startPage(){
			var self = this;
			let dfd = $.Deferred();
			self.getListData().done(function() {
				if (self.settingList().length !== 0) {
					self.selectedCode(self.settingList()[0].code.toString());
				}
				dfd.resolve();
			});
			return dfd.promise();
		}

		reloadPage(){
			var self = this;
			let dfd = $.Deferred();
			self.getListData().done(function() {
				dfd.resolve();
			});
		}
	
		getListData(){
			var self = this;
			let dfd = $.Deferred();
			ajax("screen/com/cmf/cmf001/b/get/settings/domainbase").done((lstData: Array<viewmodel.Setting>) => {
				let sortedData = _.orderBy(lstData, ['code'], ['asc']);
				self.settingList(sortedData);
				dfd.resolve();
			}).fail(function(error) {
				dfd.reject();
				alert(error.message);
			})
			return dfd.promise();
		}

		newMode() {
			let self = this;
			self.selectedCode("");
			self.setInfo(SettingInfo.new());
			self.isNewMode(true);
		}
	
		updateMode(){
			let self = this;
			ajax("com", "screen/com/cmf/cmf001/b/get/setting/" + self.selectedCode()).done((infoData: viewmodel.SettingInfo) => {
				self.setInfo(infoData);
				self.isNewMode(false);
				self.checkError();
			});
		}

		checkError(){
			nts.uk.ui.errors.clearAll()
			$('.check-target').ntsError('check');
		}
	
		setInfo(info: SettingInfo){
			let self = this;
			self.settingCode(info.code);
			self.settingName(info.name);
			self.importMode(info.mode);
			self.itemNameRow(info.itemNameRow);
			self.importStartRow(info.importStartRow);
			self.csvFileId=info.csvFileId;

			self.importDomain(info.domains[0].domainId);
		}

		setLayout(itemNoList: number[]){
			let self = this;
			if(itemNoList.length > 0){
				let condition = {
					settingCode: self.settingCode(),
					importingDomainId: self.importDomain(),
					itemNoList: itemNoList};
				ajax("screen/com/cmf/cmf001/b/get/layout/detail", condition).done((layoutItems: Array<viewmodel.Layout>) => {
					self.layout(layoutItems);
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
				let saveContents = {
					createMode: self.isNewMode(),
					baseType: 1,
					setting: new SettingInfo(
						__viewContext.user.companyId,
						self.settingCode(),
						self.settingName(),
						self.importDomain(),
						self.importMode(),
						self.itemNameRow(),
						self.importStartRow(),
						self.csvFileId,
						self.layoutItemNoList()),
				};
				ajax("screen/com/cmf/cmf001/b/save", saveContents).done(() => {
					info(nts.uk.resource.getMessage("Msg_15", []));
					self.reloadPage();
					self.selectedCode(self.settingCode());
	            }).fail(function(error) {
					nts.uk.ui.dialog.alert({ messageId: error.messageId })
				});
			}
		}

		canRemove = ko.computed(() => !util.isNullOrEmpty(this.selectedCode()));
	
		remove(){
			let self = this;
			let target = {code: self.selectedCode()};
	
			
	
	        ui.dialog.confirm({ messageId: "Msg_18" }).ifYes(() => {
	            this.$ajax("exio/input/setting/remove", target).done(() => {
					info(nts.uk.resource.getMessage("Msg_16", []));
					self.reloadPage();
					self.selectedCode("");
	            });
	        });
		}

		selectLayout() {
			let self = this;
			setShared('CMF001DParams', {
					domainId: self.importDomain(),
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
			request.jump("../c/index.xhtml", {
				settingCode: this.settingCode(),
				domainId: this.importDomain(),
				screenId: 'cmf001b'
			});
		}
	
		removeItem(target){
			let self = this;
			self.layoutItemNoList(self.layoutItemNoList().filter(function(itemNo){
				return itemNo !== target;
			}))
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
		mode: number;
		itemNameRow: number;
		importStartRow: number;
		csvFileId: string;
		domains:[];
	
		constructor(companyId: string, code: string, name: string, domain: number, mode: number, itemNameRow: number, importStartRow: number, csvFileId: string, itemNoList: Array<number>) {
			this.companyId = companyId;
			this.code = code;
			this.name = name;
			this.mode = mode;
			this.itemNameRow = itemNameRow;
			this.importStartRow = importStartRow;
			this.csvFileId = csvFileId;
			
			this.domains = [
				{
					domainId:domain,
					itemNoList:itemNoList
				}
			];
		}
	
		static new(){
			return new SettingInfo(__viewContext.user.companyId, "", "", null, null, null, [])
		}
	}
	
	export class Layout {
		itemNo: number;
		name: string;
		required: boolean;
		type: string;
		source: string;
	
		constructor(itemNo: number,　name: string, required: boolean, type: string, source: string) {
			this.itemNo = itemNo;
			this.name = name;
			this.required = required;
			this.type = type;
			this.source = source;
		}
	}
}