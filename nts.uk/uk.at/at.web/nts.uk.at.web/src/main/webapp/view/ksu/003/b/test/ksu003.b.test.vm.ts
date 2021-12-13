module nts.uk.at.view.ksu003.b.test {
	import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
	// Import
	export module viewmodel {
		export class ScreenModel {
			options: Option;
			currentScreen: any = null;
			enable: KnockoutObservable<boolean>;
			required: KnockoutObservable<boolean>;
			date: KnockoutObservable<string>;		
			dateValue: KnockoutObservable<any>;
			startDateString: KnockoutObservable<string>;
			endDateString: KnockoutObservable<string>;	
			workplaceGroupList: KnockoutObservable<any> = ko.observable([]);
			currentIds: KnockoutObservable<any> = ko.observable(null);
			currentCodes: KnockoutObservable<any> = ko.observable([]);
			currentNames: KnockoutObservable<any> = ko.observable([]);
			treeGrid: TreeComponentOption;
			selectedWorkplaceId: KnockoutObservable<string>;
			baseDate: KnockoutObservable<Date>;
			unit: string = '';
			workPlace: KnockoutObservable<boolean> = ko.observable(false);
			workPlaceGroup: KnockoutObservable<boolean> = ko.observable(false);
			isWorkPlace: KnockoutObservable<boolean> = ko.observable(true);
			isWorkPlaceGroup: KnockoutObservable<boolean> = ko.observable(true);
			check: KnockoutObservable<boolean> = ko.observable(true);
			check1: KnockoutObservable<boolean> = ko.observable(true);

            itemList: KnockoutObservableArray<ItemModel>;
            selectedPage: KnockoutObservable<number>;
            isEnable: KnockoutObservable<boolean>;
            isEditable: KnockoutObservable<boolean>;
			pageNo: KnockoutObservable<string> = ko.observable('');	
			constructor() {
				var self = this;
                
                self.itemList = ko.observableArray([
                    new ItemModel(1),
                    new ItemModel(2),
                    new ItemModel(3),
                    new ItemModel(4),
                    new ItemModel(5)
                ]);             
        
                self.selectedPage = ko.observable(1);
                self.isEnable = ko.observable(true);
                self.isEditable = ko.observable(true);

				self.enable = ko.observable(true);
				self.required = ko.observable(true);
				self.baseDate = ko.observable(new Date());
				self.selectedWorkplaceId = ko.observable('');				
				self.date = ko.observable(new Date().toString());

				
				self.dateValue = ko.observable({});
				self.workPlace.subscribe((x) => {
					self.isWorkPlaceGroup(!x);
					self.check1(!x)
				});
				self.workPlaceGroup.subscribe((x) => {
					self.isWorkPlace(!x);
					self.check(!x)
				});
			
				self.treeGrid = {
					isMultipleUse: true,
					isMultiSelect: false,
					treeType: 1,
					selectedId: self.selectedWorkplaceId,
					baseDate: self.baseDate,
					selectType: 4,
					isShowSelectButton: false,
					isDialog: false,
					maxRows: 15,
					tabindex: 1,
					systemType: 2

				};

				$('#tree-grid').ntsTreeComponent(self.treeGrid);
				self.options = {
					itemList: self.workplaceGroupList,
					currentCodes: self.currentCodes,
					currentNames: self.currentNames,
					currentIds: self.currentIds,
					multiple: false,
					tabindex: 2,
					isAlreadySetting: false,
					showEmptyItem: false,
					reloadData: ko.observable(''),
					height: 373,
					selectedMode: 1
				};			
			}
			openDialog(): void {
				let self = this;
				let request: any = {};			

				if (self.workPlace()) {
					self.unit = '0';
				}
				if (self.workPlaceGroup()) {
					self.unit = '1';
				}

                request.page = self.selectedPage();
				request.targetUnit = self.unit;
				if (self.unit === '1') {
					request.targetId = self.currentIds();
				} else {
					request.targetId = self.selectedWorkplaceId();
				}

                if (self.unit == '' || self.unit == undefined || request.targetId =='' || request.targetId == undefined) {
                    alert('Please choose target is WorkPlace or WorkPlaceGroup ?'); 
                    return;
                }				
				request.referenceDate = moment(self.date()).format('YYYY/MM/DD');
				setShared('dataShareKsu003b', request);		
				self.currentScreen = nts.uk.ui.windows.sub.modal("/view/ksu/003/b/index.xhtml").onClosed(() => {
                    let data = getShared('dataShareFromKsu003b');
					self.pageNo(getShared('dataShareFromKsu003b'));
                });
			}
			public startPage(): JQueryPromise<any> {
				let self = this,
					dfd = $.Deferred();

				dfd.resolve();
				return dfd.promise();
			}
		}
	}
    class ItemModel {
        page: number;        
        constructor(page: number) {
            this.page = page;
           
        }
    }
                                    
}