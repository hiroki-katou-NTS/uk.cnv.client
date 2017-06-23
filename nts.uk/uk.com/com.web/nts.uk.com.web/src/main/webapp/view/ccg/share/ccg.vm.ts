module nts.uk.com.view.ccg.share.ccg {

    import ListType = kcp.share.list.ListType;
    import TreeType = kcp.share.tree.TreeType;
    import SelectType = kcp.share.list.SelectType;
    import UnitModel = kcp.share.list.UnitModel;
    import PersonModel = service.model.PersonModel;
    import GroupOption = service.model.GroupOption;
    import ClassificationHistoryInDto = service.model.ClassificationHistoryInDto;
    import EmployeeSearchDto = service.model.EmployeeSearchDto;


    export module viewmodel {
        /**
        * Screen Model.
        */
        export class ListGroupScreenModel {
            isMultiple: boolean;
            tabs: KnockoutObservableArray<NtsTabPanelModel>;
            selectedTab: KnockoutObservable<string>;
            selectedCode: KnockoutObservableArray<string>;
            baseDate: KnockoutObservable<Date>;
            employments: any;
            classifications: any;
            jobtitles: any;
            workplaces: any;
            employeeinfo: any;
            onSearchAllClicked: (data: PersonModel[]) => void;
            onSearchOnlyClicked: (data: PersonModel) => void;


            constructor() {
                var self = this;
                self.isMultiple = false;
                self.selectedCode = ko.observableArray([]);
                self.baseDate = ko.observable(new Date());
                self.tabs = ko.observableArray([
                    {
                        id: 'tab-1',
                        title: nts.uk.resource.getText("CCG001_3"),
                        content: '.tab-content-1',
                        enable: ko.observable(true),
                        visible: ko.observable(true)
                    },
                    {
                        id: 'tab-2',
                        title: nts.uk.resource.getText("CCG001_4"),
                        content: '.tab-content-2',
                        enable: ko.observable(true),
                        visible: ko.observable(true)
                    }
                ]);
                self.selectedTab = ko.observable('tab-1');
                self.employments = {
                    isShowAlreadySet: false,
                    isMultiSelect: true,
                    listType: ListType.EMPLOYMENT,
                    selectedCode: self.selectedCode,
                    isDialog: true
                };

                self.classifications = {
                    isShowAlreadySet: false,
                    isMultiSelect: true,
                    listType: ListType.Classification,
                    selectedCode: self.selectedCode,
                    isDialog: true
                }


                self.jobtitles = {
                    isShowAlreadySet: false,
                    isMultiSelect: true,
                    listType: ListType.JOB_TITLE,
                    selectedCode: this.selectedCode,
                    isDialog: false,
                    baseDate: self.baseDate,
                }

                self.workplaces = {
                    isShowAlreadySet: false,
                    isMultiSelect: false,
                    treeType: TreeType.WORK_PLACE,
                    selectedCode: self.selectedCode,
                    baseDate: self.baseDate,
                    isDialog: false
                }



            }
            /**
             * Init component.
             */
            public init($input: JQuery, data: GroupOption): JQueryPromise<void> {
                var dfd = $.Deferred<void>();
                var self = this;
                self.isMultiple = data.isMutipleCheck;
                self.onSearchAllClicked = data.onSearchAllClicked;
                self.onSearchOnlyClicked = data.onSearchOnlyClicked;
                var webserviceLocator = nts.uk.request.location.siteRoot
                    .mergeRelativePath(nts.uk.request.WEB_APP_NAME["com"] + '/')
                    .mergeRelativePath('/view/ccg/share/ccg.xhtml').serialize();
                $input.load(webserviceLocator, function() {
                    ko.cleanNode($input[0]);
                    ko.applyBindings(self, $input[0]);
                    $('#employeeList').ntsListComponent(self.employments);
                    $('#classificationList').ntsListComponent(self.classifications);
                    $('#jobtitleList').ntsListComponent(self.jobtitles);
                    $(".accordion").accordion({
                        active: false,
                        collapsible: true
                    });
                    dfd.resolve();
                });

                return dfd.promise();
            }

            searchAllEmployee(): void {
                var self = this;
                service.findAllPerson().done(data => {
                    self.onSearchAllClicked(data);
                });
            }

            toClassficationDto(): ClassificationHistoryInDto {
                var self = this;
                var dto: ClassificationHistoryInDto = new ClassificationHistoryInDto();
                dto.baseDate = self.baseDate();
                var classificationData: UnitModel[] = $('#classificationList').getDataList();
                console.log(classificationData);
                var classificationCodes: string[] = [];
                for (var item: UnitModel of classificationData) {
                    classificationCodes.push(item.code);
                }
                dto.classificationCodes = classificationCodes;
                console.log(dto);
                return dto;
            }

            toEmployeeDto(): EmployeeSearchDto {
                var self = this;
                var dto: EmployeeSearchDto = new EmployeeSearchDto();
                dto.classificationHistory = self.toClassficationDto();
                return dto;
            }
            
            searchDataEmployee(): void {
                var self = this;

                service.searchModeEmployee(self.toEmployeeDto()).done(data => {
                    self.employeeinfo = {
                        isShowAlreadySet: false,
                        isMultiSelect: self.isMultiple,
                        listType: ListType.EMPLOYEE,
                        employeeInputList: self.toUnitModelList(data),
                        selectType: SelectType.SELECT_BY_SELECTED_CODE,
                        selectedCode: self.selectedCode,
                        isDialog: false,
                        isShowNoSelectRow: false,
                    }
                    $('#employeeinfo').ntsListComponent(self.employeeinfo);
                });

            }



            getEmployeeLogin(): void {
                var self = this;
                service.getPersonLogin().done(data => {
                    self.onSearchOnlyClicked(data);
                });
            }

            public toUnitModelList(dataList: PersonModel[]): KnockoutObservableArray<UnitModel> {
                var dataRes: UnitModel[] = [];

                for (var item: PersonModel of dataList) {
                    dataRes.push({
                        code: item.personId,
                        name: item.personName
                    });
                }
                return ko.observableArray(dataRes);
            }

            public toUnitModel(data: PersonModel): KnockoutObservable<UnitModel> {
                var dataRes: UnitModel = { code: data.personId, name: data.personName };
                return ko.observable(dataRes);
            }


        }





        /**
         * Defined Jquery interface.
        */
        interface JQuery {

            /**
             * Nts list component.
             * This Function used after apply binding only.
             */
            ntsGroupComponent(option: nts.uk.com.view.ccg.share.ccg.service.model.GroupOption): JQueryPromise<void>;
        }

        (function($: any) {
            $.fn.ntsGroupComponent = function(option: nts.uk.com.view.ccg.share.ccg.service.model.GroupOption): JQueryPromise<void> {

                // Return.
                return new nts.uk.com.view.ccg.share.ccg.viewmodel.ListGroupScreenModel().init(this, option);
            }

        } (jQuery));
    }
}
    