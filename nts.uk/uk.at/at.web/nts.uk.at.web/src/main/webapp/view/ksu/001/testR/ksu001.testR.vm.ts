module nts.uk.at.view.ksu001.testR {
    import setShare = nts.uk.ui.windows.setShared;
    import getShare = nts.uk.ui.windows.getShared;
    // Import
    export module viewmodel {
        export class ScreenModel {
            options: Option;
            currentScreen: any = null;
            enable: KnockoutObservable<boolean>;
            required: KnockoutObservable<boolean>;
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
            name: KnockoutObservable<string> = ko.observable('');
            stt: KnockoutObservable<string> = ko.observable('');
            constructor() {
                var self = this;
                self.enable = ko.observable(true);
                self.required = ko.observable(true);
                self.stt = ko.observable('');
                self.baseDate = ko.observable(new Date());
                self.selectedWorkplaceId = ko.observable('');
                self.startDateString = ko.observable("2020/08/01");
                self.endDateString = ko.observable("2020/08/31");
                self.dateValue = ko.observable({});
                self.workPlace.subscribe((x) => {
                    self.isWorkPlaceGroup(!x);
                    self.check1(!x)
                })
                self.workPlaceGroup.subscribe((x) => {
                    self.isWorkPlace(!x);
                    self.check(!x)
                })
                self.startDateString.subscribe(function(value) {
                    self.dateValue().startDate = value;
                    self.dateValue.valueHasMutated();
                });

                self.endDateString.subscribe(function(value) {
                    self.dateValue().endDate = value;
                    self.dateValue.valueHasMutated(); 
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
                self.currentIds.subscribe((x) => {
                    console.log(x);
                });
                self.selectedWorkplaceId.subscribe((x) => {
                    let data = {
                        workplaceId: self.selectedWorkplaceId(),
                        baseDate: moment().toISOString()
                    };

                    service.getWorkPlaceById(data).done(function(wkp) {
                        //self.name(wkp.workplaceName);

                    });
                });
            }
            
            openDialog(): void {
                let self = this;

                let target: any = {};
                let period: any = {};
                
                

                if (self.workPlace()) {
                    self.unit = '0';
                }
                if (self.workPlaceGroup()) {
                    self.unit = '1';
                }

                target.unit = self.unit;
                if (self.unit === '1') {
                    target.id = self.currentIds();
                } else {
                    target.id = self.selectedWorkplaceId();
                }

                if (self.unit == '' || self.unit == undefined || target.id == '' || target.id == undefined) {
                    alert('Please choose WorkPlace/WorkPlaceGroup');
                    return;
                }
                period.startDate = self.dateValue().startDate;
                period.endDate = self.dateValue().endDate;
                if (period.startDate == '' || period.startDate == undefined || period.endDate == '' || period.endDate == undefined) {
                    alert('Please input Start/End Date');
                    return;
                }
                setShare('targetR', target);
                setShare('periodR', period);
                let data = {
                    workplaceId: self.selectedWorkplaceId(),
                    baseDate: moment().toISOString()
                };

                service.getWorkPlaceById(data).done(function(wkp) {
                    if(!_.isNil(wkp)){
                        self.name(wkp.workplaceName);
                    }
                    
                    if (self.unit === '0') {
                        setShare('name', self.name());
                    }
                    else {
                        setShare('name', self.currentNames());
                    }
                    // self.currentScreen = nts.uk.ui.windows.sub.modeless("/view/ksu/001/r/index.xhtml");
                    self.currentScreen = nts.uk.ui.windows.sub.modal("/view/ksu/001/r/index.xhtml", { dialogClass: "no-close" }).onClosed(() => {

                        var sttData = nts.uk.ui.windows.getShared("EndStatus");
                        if (sttData !== undefined) {
                            self.stt(sttData);
                            nts.uk.ui.block.clear();
                        }
                        else {
                            self.stt = ko.observable("");
                            nts.uk.ui.block.clear();
                        }
                    });
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
}