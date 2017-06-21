module nts.uk.com.view.ccg001.a {  

    import ListType = kcp.share.list.ListType;
    import SelectType = kcp.share.list.SelectType;
    import UnitModel = kcp.share.list.UnitModel;
    import PersonModel = nts.uk.com.view.ccg.share.ccg.PersonModel;
    export module viewmodel {
        export class ScreenModel {
           
            ccgcomponent: any;
            personinfo: any;
            selectedCode: KnockoutObservableArray<string>;
            visiblePersonInfo: KnockoutObservable<boolean>;
            personLoginInfo: PersonModel;
            constructor() {
                var self = this;
                self.selectedCode = ko.observableArray([]);
                self.personLoginInfo = new PersonModel();
                self.ccgcomponent = {
                    isMutipleCheck: true,
                    onSearchAllClicked: function(dataList: PersonModel[]) {
                        self.personinfo = {
                            isShowAlreadySet: false,
                            isMultiSelect: self.ccgcomponent.isMutipleCheck,
                            listType: ListType.EMPLOYEE,
                            employeeInputList: new nts.uk.com.view.ccg.share.ccg.ListGroupScreenModel().toUnitModelList(dataList),
                            selectType: SelectType.SELECT_BY_SELECTED_CODE,
                            selectedCode: self.selectedCode,
                            isDialog: false,
                            isShowNoSelectRow: false,
                        }
                        $('#personinfo').ntsListComponent(self.personinfo);
                    },
                    onSearchOnlyClicked: function(data: PersonModel){
                        self.personLoginInfo = data;
                        self.visiblePersonInfo(true);
                    }
                }
                self.visiblePersonInfo = ko.observable(false);
                $('#ccgcomponent').ntsGroupComponent(self.ccgcomponent);
               
                
            }

            public startPage(): JQueryPromise<any> {
                let dfd = $.Deferred<any>();
                dfd.resolve();
                return dfd.promise();
            }
        }
    }
}