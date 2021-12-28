module ccg018.b.viewmodel {
    import EmployeeSearchDto = nts.uk.com.view.ccg.share.ccg.service.model.EmployeeSearchDto;
    import Ccg001ReturnedData = nts.uk.com.view.ccg.share.ccg.service.model.Ccg001ReturnedData;
    import GroupOption = nts.uk.com.view.ccg.share.ccg.service.model.GroupOption;
    import blockUI = nts.uk.ui.block;

    export class ScreenModel extends base.viewModel.ScreenModelBase {
        items: KnockoutObservableArray<TopPagePersonSet>;
        selectedItem: KnockoutObservable<TopPagePersonSet>;
        currentCode: KnockoutObservable<any>;
        selectedItemAfterLogin: KnockoutObservable<string>;
        selectedItemAsTopPage: KnockoutObservable<string>;
        employeeCode: KnockoutObservable<string>;
        employeeName: KnockoutObservable<string>;
        isEnable: KnockoutObservable<boolean>;
        categorySet: KnockoutObservable<any>;
        alldata:KnockoutObservableArray<TopPagePersonSet> =  ko.observableArray();

        listSid: Array<any>;
        isSelectedFirst: KnockoutObservable<boolean>;

        isEmpty: KnockoutObservable<boolean>;

        //component
        ccgcomponent: GroupOption;
        showinfoSelectedEmployee: KnockoutObservable<boolean>;

        // Options
        baseDate: KnockoutObservable<Date>;
        selectedEmployee: KnockoutObservableArray<EmployeeSearchDto>;

        listSwitchDate: KnockoutObservableArray<number> = ko.observableArray();
        selectedSwitchDate: KnockoutObservable<number> = ko.observable(0);

        constructor(baseModel: base.result.BaseResultModel) {
            super(baseModel);
            let self = this;
            self.screenTemplateUrl("../b/index.xhtml");
            self.comboItemsAfterLogin(baseModel.comboItemsAfterLogin);
            self.comboItemsAsTopPage(baseModel.comboItemsAsTopPage);
            self.categorySet(baseModel.categorySet);
            self.items = ko.observableArray([]);
            self.selectedItem = ko.observable(null);
            self.listSid = [];
            self.currentCode = ko.observable();
            self.employeeCode = ko.observable('');
            self.employeeName = ko.observable('');
            self.selectedItemAfterLogin = ko.observable('');
            self.selectedItemAsTopPage = ko.observable('');

            self.isEnable = ko.observable(false);
            self.isSelectedFirst = ko.observable(true);

            self.currentCode.subscribe(function(codeChange: string) {
                if (codeChange && codeChange != "undefined") {
                    self.currentCode(codeChange);
                    self.employeeCode(codeChange);
                    self.selectedItem(_.find(self.items(), ['code', codeChange]));
                    self.employeeName(self.selectedItem().name);
                    self.selectedItemAfterLogin(self.selectedItem().uniqueCode());
                    self.selectedSwitchDate(self.selectedItem().switchingDate());
                    self.selectedItemAsTopPage(self.selectedItem().topPageCode());
                    self.isEnable(_.find(self.items(), ['code', self.currentCode()]).isAlreadySetting);
                } else {
                    self.currentCode(null);
                    self.employeeCode('');
                    self.employeeName('');
                    self.selectedItemAfterLogin('');
                    self.selectedItemAsTopPage('');
                    self.isEnable(false);
                }
            });

            //component
            self.selectedEmployee = ko.observableArray([]);
            self.showinfoSelectedEmployee = ko.observable(false);
            self.baseDate = ko.observable(new Date());

            self.selectedEmployee.subscribe(function() {
                self.listSid = [];
                _.each(self.selectedEmployee(), function(x) {
                    self.listSid.push(x.employeeId);
                });
                self.findTopPagePersonSet();
            });
            self.isEmpty = ko.computed(function() {
              return !nts.uk.ui.errors.hasError();
            });
            
            self.listSwitchDate(self.getSwitchDateLists());
        }

        initCCG001(): void {
            var self = this;
            // Component option
            self.ccgcomponent = {
                /** Common properties */  
                systemType: 1, // システム区分
                showEmployeeSelection: false, // 検索タイプ
                showQuickSearchTab: false, // クイック検索
                showAdvancedSearchTab: true, // 詳細検索
                showBaseDate: true, // 基準日利用
                showClosure: false, // 就業締め日利用
                showAllClosure: false, // 全締め表示
                showPeriod: false, // 対象期間利用
                periodFormatYM: false, // 対象期間精度

                /** Required parameter */
                baseDate: self.baseDate().toISOString(), // 基準日
                inService: true, // 在職区分
                leaveOfAbsence: false, // 休職区分
                closed: false, // 休業区分
                retirement: false, // 退職区分

                /** Quick search tab options */
                showAllReferableEmployee: false, // 参照可能な社員すべて
                showOnlyMe: false, // 自分だけ
                showSameWorkplace: false, // 同じ職場の社員
                showSameWorkplaceAndChild: false, // 同じ職場とその配下の社員

                /** Advanced search properties */
                showEmployment: true, // 雇用条件
                showWorkplace: true, // 職場条件
                showClassification: true, // 分類条件
                showJobTitle: true, // 職位条件
                showWorktype: true, // 勤種条件
                isMutipleCheck: true, // 選択モード
                tabindex: 4,

                /** Return data */
                returnDataFromCcg001: function(data: Ccg001ReturnedData) {
                    self.selectedEmployee(data.listEmployee);
                }
            }
            // Start component
            $('#ccgcomponent').ntsGroupComponent(self.ccgcomponent);
        }

        bindGrid(): any {
            let self = this;
            let listComponentOption: any = {
                isShowAlreadySet: true,
                alreadySettingList: self.items,
                isMultiSelect: false,
                listType: 4,
                isShowWorkPlaceName: true,
                selectedCode: self.currentCode,
                isShowNoSelectRow: false,
                isDialog: false,
                selectType: 4,
                isShowSelectAllButton: false,
                employeeInputList: self.items,
                isRemoveFilterWhenReload: false,
                tabindex: -1,
            };
            $('#sample-component').ntsListComponent(listComponentOption);
        }

        findTopPagePersonSet(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            service.findTopPagePersonSet(self.listSid).done(function(data) {
                    const arr: any = [];
                    _.each(self.selectedEmployee(), function(x) {
                        const topPagePersonSet: any = _.find(data, ['employeeId', x.employeeId]);
                        if (!!topPagePersonSet) {
                            arr.push(new TopPagePersonSet({
                                code: x.employeeCode,
                                name: x.employeeName,
                                affiliationName: x.affiliationName,
                                employeeId: x.employeeId,
                                topPageCode: topPagePersonSet.topMenuCode,
                                loginMenuCode: topPagePersonSet.loginMenuCode,
                                system: topPagePersonSet.system,
                                switchingDate: topPagePersonSet.switchingDate,
                                menuClassification: topPagePersonSet.menuClassification,
                                isAlreadySetting: true
                            }));
                        } else {
                            arr.push(new TopPagePersonSet({
                                code: x.employeeCode,
                                name: x.employeeName,
                                affiliationName: x.affiliationName,
                                employeeId: x.employeeId,
                                topPageCode: '',
                                loginMenuCode: '',
                                system: 0,
                                switchingDate: 0,
                                menuClassification: 0,
                                isAlreadySetting: false
                            }));
                        }
                    });
                    self.items(arr);
                    if (self.isSelectedFirst() && self.items().length > 0) {
                        self.currentCode(self.items()[0].code);
                    }
                    self.isSelectedFirst(true);
                    dfd.resolve();
                }).fail(function() {
                    dfd.reject();
                });
            return dfd.promise();
        }

        /**
         * Update/Insert data in to table TOPPAGE_PERSON_SET
         */
        save(): void {
          let self = this;
          if (!self.currentCode()) {
            return;
          }
          const obj: any = {
            employeeId: self.selectedItem().employeeId,
            switchingDate: self.selectedSwitchDate(),
            topMenuCode: self.selectedItemAsTopPage() ? self.selectedItemAsTopPage() : '',
            loginMenuCode: (self.selectedItemAfterLogin().length === 6 ? self.selectedItemAfterLogin().slice(0, 4) : ''),
            system: self.selectedItemAfterLogin().slice(-2, -1),
            menuClassification: self.selectedItemAfterLogin().slice(-1)
          };
          self.update(obj);
        }

        update(obj: any) {
          const vm = this;
          blockUI.invisible();
          ccg018.b.service.update(obj).then(() => {
            vm.isSelectedFirst(false);
            $.when(vm.findTopPagePersonSet()).then(() => {
              vm.currentCode(vm.selectedItem().code);
              vm.isEnable(true);
              nts.uk.ui.dialog.info({ messageId: "Msg_15" });
            });
          }).fail(() => {
            nts.uk.ui.dialog.caution({ messageId: "Msg_86" });
          }).always(() => blockUI.clear());
        }

        /**
         * remove data in to table TOPPAGE_PERSON_SET
         */
        removeData(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            if (!self.currentCode()) {
                return;
            }
            if (!!!self.currentCode()) {
//                nts.uk.ui.dialog.info(nts.uk.resource.getMessage('Msg_85'));
            nts.uk.ui.dialog.info({ messageId: "Msg_85" }).then(function() {
                    });
            } else {
                nts.uk.ui.dialog.confirm({ messageId: "Msg_18" }).ifYes(() => {
                    const obj = { employeeId: self.selectedItem().employeeId };
                    let keySearch = $('#sample-component .ntsSearchBox').val();
                    ccg018.b.service.remove(obj).done(function() {
                        self.isSelectedFirst(false);
                        $.when(self.findTopPagePersonSet()).done(function() {
                            if(keySearch != ""){
                                $('#sample-component .ntsSearchBox').val(keySearch);
                                $('#sample-component .search-btn').trigger('click');
                            }
                            self.isEnable(false);
                            self.selectedItemAfterLogin('');
                            self.selectedItemAsTopPage('');
                            self.selectedSwitchDate(0);
//                            nts.uk.ui.dialog.info(nts.uk.resource.getMessage('Msg_16'));
                            nts.uk.ui.dialog.info({ messageId: "Msg_16" }).then(function() {
                            });
                        });
                    }).fail(function() {
                        dfd.reject();
                    });
                }).ifNo(() => { });
            }
            dfd.resolve();
            return dfd.promise();
        }

        /**"トップページの設定を複写する (CDL023)"
        */
        copy() {
          const vm = this;
          const employee = _.find(vm.items(), ['employeeId', vm.selectedItem().employeeId]);
          if (!employee.code) {
            return;
          }
          service.findByCId()
            .then((data) => {
              vm.alldata(data);
              const lstSidSetting = _.map(vm.alldata(),(x) => x.employeeId)
              const object: any = {
                code: employee.code,
                name: employee.name,
                targetType: 6, // 職場個人
                itemListSetting: lstSidSetting,
                employeeId: employee.employeeId,
                baseDate: vm.baseDate().toISOString()
              };
              nts.uk.ui.windows.setShared("CDL023Input", object);
              nts.uk.ui.windows.sub.modal('/view/cdl/023/a/index.xhtml').onClosed(() => {
                blockUI.grayout();
                const lstSelection = nts.uk.ui.windows.getShared("CDL023Output");
                if (nts.uk.util.isNullOrEmpty(lstSelection)) {
                  blockUI.clear();
                  return;
                }
                const arrObj: any = [];
                _.forEach(lstSelection, id => {
                  const obj: any = {
                    employeeId: id,
                    switchingDate: vm.selectedSwitchDate(),
                    topMenuCode: vm.selectedItemAsTopPage() ? vm.selectedItemAsTopPage() : '',
                    loginMenuCode: (vm.selectedItemAfterLogin().length === 6 ? vm.selectedItemAfterLogin().slice(0, 4) : vm.selectedItemAsTopPage()),
                    system: vm.selectedItemAfterLogin().slice(-2, -1),
                    menuClassification: vm.selectedItemAfterLogin().slice(-1)
                  };
                  arrObj.push(obj);
                });
                ccg018.b.service.copy({ listTopPagePersonSetting: arrObj })
                  .then(() => {
                    vm.isSelectedFirst(false);
                    blockUI.clear();
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                    vm.findTopPagePersonSet();
                  })
                  .always(() => blockUI.clear());
                });
          })
        }

        showNote() {
          $('#popup-show-note').remove();
          const $table1 = $('#table-1');
          $('<div/>')
            .attr('id', 'popup-show-note')
            .appendTo($table1);
          const $popUpShowNote = $('#popup-show-note');
          $popUpShowNote.ntsPopup({
            showOnStart: false,
            dismissible: true,
            position: {
              my: 'left top',
              at: 'left bottom',
              of: '#B6_1'
            }
          });
          $('<div/>')
            .text(nts.uk.resource.getText('CCG018_52'))
            .appendTo($popUpShowNote);
          $popUpShowNote.ntsPopup('show');
        }

        private getSwitchDateLists() {
          const list: any = [];
          list.push({value: 0, text: nts.uk.resource.getText('CCG018_44')});
          _.range(1, 32).forEach(current => {
            list.push({value: current, text: current});
          });
          return list;
        }
    }

    interface ITopPagePersonSet {
        code: string,
        name: string,
        affiliationName: string,
        employeeId: string,
        topPageCode: string,
        loginMenuCode: string,
        system: number,
        menuClassification: number,
        isAlreadySetting: boolean,
        switchingDate: number
    }

    class TopPagePersonSet {
        code: string;
        name: string;
        affiliationName: string;
        employeeId: string;
        topPageCode: KnockoutObservable<string>;
        loginMenuCode: KnockoutObservable<string>;
        isAlreadySetting: boolean;
        system: KnockoutObservable<number>;
        menuClassification: KnockoutObservable<number>;
        switchingDate: KnockoutObservable<number>;
        //beacause there can exist same code, so create uniqueCode = loginMenuCd+ system+ menuClassification
        uniqueCode: KnockoutObservable<string> = ko.observable('');
        constructor(param: ITopPagePersonSet) {
            let self = this;
            self.code = param.code;
            self.name = param.name;
            self.affiliationName = param.affiliationName;
            self.employeeId = param.employeeId;
            self.topPageCode = ko.observable(param.topPageCode);
            self.switchingDate = ko.observable(param.switchingDate);
            self.loginMenuCode = ko.observable(param.loginMenuCode);
            self.isAlreadySetting = param.isAlreadySetting;
            self.system = ko.observable(param.system);
            self.menuClassification = ko.observable(param.menuClassification);
            self.uniqueCode(nts.uk.text.format("{0}{1}{2}", param.loginMenuCode, param.system, param.menuClassification));
        }
    }
}
