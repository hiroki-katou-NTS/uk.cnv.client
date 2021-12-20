/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
module nts.uk.com.view.cmf001.e.viewmodel {
	import ajax = nts.uk.request.ajax;
	import info = nts.uk.ui.dialog.info;
	import setShared = nts.uk.ui.windows.setShared;
	import getShared = nts.uk.ui.windows.getShared;

	@bean()
	class ViewModel extends ko.ViewModel {
		isNewMode: KnockoutObservable<boolean> = ko.observable(true);

		settingList: KnockoutObservableArray<Setting> = ko.observableArray([]);

		settingCode: KnockoutObservable<string> = ko.observable();
		settingName: KnockoutObservable<string> = ko.observable();
		
		itemNameRow: KnockoutObservable<number> = ko.observable();
		importStartRow: KnockoutObservable<number> = ko.observable();

		importDomainOption: KnockoutObservableArray<any> = ko.observableArray(__viewContext.enums.ImportingDomainId);

		selectedCode: KnockoutObservable<string> = ko.observable();
		
		selectedItem: KnockoutObservable<string> = ko.observable();

	    canEditDetail: KnockoutObservable<boolean> = ko.computed(() => !util.isNullOrEmpty(this.selectedCode()));
	    
	    // csv
		csvFileName: KnockoutObservable<string> = ko.observable();
		csvFileId: KnockoutObservable<string> = ko.observable();
	    
		settingListColumns: KnockoutObservableArray<any> = ko.observableArray([
			{ headerText: "コード", 				key: "code", 					width: 50 	},
			{ headerText: "名称", 					key: "name", 					width: 200 	},
		]);
		
		constructor() {
			super();
			var self = this;

			self.startPage();

			self.selectedCode.subscribe((value) => {
				if (value) {
					self.updateMode();
				} else {
					self.newMode();
				}
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
			ajax("screen/com/cmf/cmf001/e/get/settings/csvbase")
			.done((lstData: Array<viewmodel.Setting>) => {
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
			ajax("com", "screen/com/cmf/cmf001/e/get/setting/" + self.selectedCode())
			.done((infoData: viewmodel.SettingInfo) => {
				self.setInfo(infoData);
				self.isNewMode(false);
				self.checkError();

				ajax("com", "shr/infra/file/storage/infor/" + self.csvFileId())
				.done(res => {
					self.csvFileName(res.originalName);
				});
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
			self.itemNameRow(info.itemNameRow);
			self.importStartRow(info.importStartRow);
			self.csvFileId(info.csvFileId);
		}

		csvFileUploaded(fileInfo: any){
			let self = this;
			self.csvFileId(fileInfo.id);
		}
		
		canSave = ko.computed(() => !nts.uk.ui.errors.hasError() );
		save(){
			let self = this;
			self.checkError();
			if(!nts.uk.ui.errors.hasError()){
				let saveContents = {
					createMode: self.isNewMode(),
					baseType: 2,
					setting: new SettingInfo(
						__viewContext.user.companyId,
						self.settingCode(),
						self.settingName(),
						self.itemNameRow(),
						self.importStartRow(),
						self.csvFileId())
				};
				ajax("screen/com/cmf/cmf001/e/save", saveContents).done(() => {
					info(nts.uk.resource.getMessage("Msg_15", [])).then(() => {
				        ui.dialog.confirm("続けて受入レイアウトを登録しますか？").ifYes(() => {
				        	self.gotoDetailSetting();
				        });
					});
					self.reloadPage();
					self.selectedCode(self.settingCode());
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
		
		gotoDetailSetting() {
			request.jump("../f/index.xhtml", {
				settingCode: this.settingCode()
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
		csvFileId: string
		domains: ImportDomain[]
	
		constructor(companyId: string, code: string, name: string, itemNameRow: number, importStartRow: number, csvFileId: string) {
			this.companyId = companyId;
			this.code = code;
			this.name = name;
			this.itemNameRow = itemNameRow;
			this.importStartRow = importStartRow;
			this.csvFileId = csvFileId;
			this.domains = [];
		}
	
		static new(){
			return new SettingInfo(__viewContext.user.companyId, "", "", null, null, null, "")
		}
	}

	export class ImportDomain {
		domainId: int;
		itemNo: number[];
	
		constructor(domainId: int, itemNo: number[]) {
				this.domainId = domainId;
				this.itemNo = itemNo;
		}
	}
}