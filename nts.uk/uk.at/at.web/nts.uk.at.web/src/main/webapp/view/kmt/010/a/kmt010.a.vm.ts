/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
module nts.uk.at.view.kmt010.a {

    const PATH = {
        init: "at/shared/scherec/taskmanagement/task/kmt010/init",
        getAllSetWkps: "at/shared/scherec/taskmanagement/task/kmt010/getAlreadySetWkps",
        getByWkpId: "at/shared/scherec/taskmanagement/task/kmt010/getlistbywpl",
        register: "at/shared/scherec/taskmanagement/task/kmt010/change",
        delete: "at/shared/scherec/taskmanagement/task/kmt010/delete",
        copy: "at/shared/scherec/taskmanagement/task/kmt010/copy"
    };

    @bean()
    class ViewModel extends ko.ViewModel {
        tabs: KnockoutObservableArray<any>;
        selectedTab: KnockoutObservable<string>;

        multiSelectedId: KnockoutObservable<any> = ko.observable(null);
        baseDate: KnockoutObservable<Date>;
        alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel> = ko.observableArray([]);
        treeGrid: any;

        workplaceName: KnockoutObservable<string> = ko.observable(null);
        workplaceDataSource: KnockoutObservableArray<UnitModel> = ko.observableArray([]);

        tabWorkSettings: KnockoutObservableArray<any>;

        displayGoback: KnockoutObservable<boolean>;
        updateMode: KnockoutObservable<boolean>;

        created(params: any) {
            const vm = this;
            vm.displayGoback = ko.observable(params && params.fromKMT011);
            vm.tabs = ko.observableArray([
                {id: 'tab-1', title: 'aa', content: '.tab-content-1', enable: ko.observable(false), visible: ko.observable(false)},
                {id: 'tab-2', title: 'bb', content: '.tab-content-2', enable: ko.observable(false), visible: ko.observable(false)},
                {id: 'tab-3', title: 'cc', content: '.tab-content-3', enable: ko.observable(false), visible: ko.observable(false)},
                {id: 'tab-4', title: 'dd', content: '.tab-content-4', enable: ko.observable(false), visible: ko.observable(false)},
                {id: 'tab-5', title: 'ee', content: '.tab-content-5', enable: ko.observable(false), visible: ko.observable(false)}
            ]);
            vm.selectedTab = ko.observable('tab-1');

            vm.tabWorkSettings = ko.observableArray([
                ko.observableArray([]),
                ko.observableArray([]),
                ko.observableArray([]),
                ko.observableArray([]),
                ko.observableArray([])
            ]);
            //-----------------------------------------

            vm.baseDate = ko.observable(new Date());
            vm.treeGrid = {
                isShowAlreadySet: true,
                isMultipleUse: false,
                isMultiSelect: false,
                startMode: StartMode.WORKPLACE,
                selectedId: vm.multiSelectedId,
                baseDate: vm.baseDate,
                selectType: SelectionType.SELECT_FIRST_ITEM,
                isShowSelectButton: true,
                isDialog: false,
                alreadySettingList: vm.alreadySettingList,
                maxRows: 15,
                tabindex: 5,
                systemType: SystemType.EMPLOYMENT //2
            };

            vm.$blockui("show");
            $.when(vm.$ajax(PATH.init), $('#kcp004').ntsTreeComponent(vm.treeGrid)).done((frameSettings: Array<any>, result) => {
                vm.tabs().forEach((t, i) => {
                    const s = _.find(frameSettings, d => d.frameNo == (i + 1));
                    if (s) {
                        t.title = s.frameName;
                        // $("a[href='#" + t.id + "']")[0].innerText = s.frameName;
                        t.enable(s.useAtr == 1);
                        t.visible(s.useAtr == 1);
                    }
                });
                vm.workplaceDataSource($('#kcp004').getDataList());
                vm.getAlreadySettingList();
            }).fail(error => {
                vm.$dialog.error(error).then(() => {
                    if (error.messageId == "Msg_2109") {
                        vm.$jump("/view/kmt/011/a/index.xhtml");
                    } else if (error.messageId == "Msg_2122" || error.messageId == "Msg_2114") {
                        nts.uk.request.jumpToTopPage();
                    }
                });
            }).always(() => {
                vm.$blockui("hide");
            });

            vm.updateMode = ko.computed(() => {
                return vm.alreadySettingList().map(i => i.workplaceId).indexOf(vm.multiSelectedId()) >= 0;
            });
        }

        mounted() {
            const vm = this;
            vm.multiSelectedId.subscribe((workplaceId) => {
                vm.getWorkPlaceDetails(workplaceId);
                vm.selectedTab('tab-1');
            });
        }

        saveRegistrationWork() {
            const vm = this;
            vm.$blockui("show").then(() => {
                const data = {
                    workPlaceId: vm.multiSelectedId(),
                    mapFrameAndCode: {}
                };
                vm.tabs().forEach((frame, index) => {
                    if (frame.visible()) {
                        const no: string = (index + 1).toString();
                        data.mapFrameAndCode[no] = vm.tabWorkSettings()[index]().map((i: any) => i.code);
                    }
                });
                return vm.$ajax(PATH.register, data);
            }).done(() => {
                vm.$dialog.info({messageId: "Msg_15"}).then(() => {
                    vm.getAlreadySettingList();
                });
            }).fail(error => {
                vm.$dialog.error(error);
            }).always(() => {
                $('.A6_1').focus();
                vm.$blockui("hide");
            });
        }

        deleteRegistrationWork() {
            const vm = this;
            vm.$dialog.confirm({messageId: 'Msg_18'}).then((result) => {
                if (result === 'yes') {
                    vm.$blockui('show');
                    vm.$ajax(PATH.delete, {workPlaceId: vm.multiSelectedId()}).done(() => {
                        vm.$dialog.info({messageId: 'Msg_16'}).then(() => {
                            vm.getAlreadySettingList();
                            vm.multiSelectedId.valueHasMutated();
                        });
                    }).fail((error) => {
                        vm.$dialog.error(error);
                    }).always(() => {
                        $('.A6_1').focus();
                        vm.$blockui('hide');
                    });
                }
            });
        }

        openDialogCDL023() {
            const vm = this;
            const wkp = vm.findWorkPlaceById(vm.multiSelectedId());
            let params: any = {
                code: wkp.code,
                name: vm.workplaceName(),
                targetType: 4, // 職場
                itemListSetting: vm.alreadySettingList().map(w => w.workplaceId),
                baseDate: vm.baseDate()
            };

            nts.uk.ui.windows.setShared("CDL023Input", params);
            // open dialog
            nts.uk.ui.windows.sub.modal('com', 'view/cdl/023/a/index.xhtml').onClosed(() => {
                let lstSelection: Array<string> = nts.uk.ui.windows.getShared("CDL023Output");
                if (!_.isEmpty(lstSelection)) {
                    vm.$blockui("show");
                    vm.$ajax(PATH.copy, {copySourceWplId: vm.multiSelectedId(), copyDestinationWplId: lstSelection}).done(() => {
                        vm.getAlreadySettingList();
                    }).fail((error) => {
                        vm.$dialog.error(error);
                    }).always(() => {
                        $('.A6_1').focus();
                        vm.$blockui('hide');
                    });
                }
            });
        }

        findElement(listItems: Array<any>, wpId: string) {
            const vm = this;
            _.forEach(listItems, (x) => {
                if (wpId === x.id) {
                    vm.workplaceName(x.name);
                } else if (x.children.length > 0) {
                    vm.findElement(x.children, wpId);
                }
            });
        }

        getWorkPlaceDetails(workplaceId: string) {
            const vm = this;
            vm.$blockui('show');
            vm.$ajax(PATH.getByWkpId, {workPlaceId: workplaceId}).done(data => {
                vm.tabWorkSettings().forEach((v, i) => {
                    const frameNo = (i + 1).toString();
                    const settings = data[frameNo] || [];
                    v(settings.map((s: any) => new WorkItem(
                        s.code,
                        s.displayInfo.taskName,
                        s.expirationStartDate,
                        s.expirationEndDate
                    )));
                });
            }).fail(error => {
                vm.$dialog.error(error);
            }).always(() => {
                vm.findElement(vm.workplaceDataSource(), vm.multiSelectedId());
                $('.A6_1').focus();
                vm.$blockui("hide");
            });
        }

        getAlreadySettingList() {
            const vm = this;
            vm.$blockui("show");
            vm.$ajax(PATH.getAllSetWkps).done((data: Array<string>) => {
                vm.alreadySettingList(data.map(id => ({workplaceId: id, isAlreadySetting: true})));
            }).fail(error => {
                vm.$dialog.error(error);
            }).always(() => {
                vm.$blockui("hide");
            });
        }

        goback() {
            const vm = this;
            vm.$jump("/view/kmt/011/a/index.xhtml", {screen: "KMT010"});
        }

        findWorkPlaceById(id: string): UnitModel {
          const vm = this;
          const workplaces = vm.flatMap(vm.workplaceDataSource());
          return _.find(workplaces, { id: id });
        }

        flatMap(workplaces: UnitModel[]) {
          const vm = this;
          return _.reduce(workplaces, (acc, wkp) => {
            if (!_.isEmpty(wkp.children)) {
              acc = acc.concat(vm.flatMap(wkp.children));
            }
            acc.push(wkp);
            return acc;
          }, []);
        }
    }

    export enum StartMode {
        WORKPLACE = 0,
        DEPARTMENT = 1
    }

    export enum SelectionType {
        SELECT_BY_SELECTED_CODE = 1,
        SELECT_ALL = 2,
        SELECT_FIRST_ITEM = 3,
        NO_SELECT = 4
    }

    export enum SystemType {
        // 個人情報
        PERSONAL_INFORMATION = 1,
        // 就業
        EMPLOYMENT = 2,
        // 給与
        SALARY = 3,
        // 人事
        HUMAN_RESOURCES = 4,
        // 管理者
        ADMINISTRATOR = 5,
    }

    export interface UnitAlreadySettingModel {
        workplaceId: string;
        isAlreadySetting: boolean;
    }

    export interface UnitModel {
        id: string;
        code: string;
        name: string;
        nodeText?: string;
        level: number;
        heirarchyCode: string;
        isAlreadySetting?: boolean;
        children: Array<UnitModel>;
    }

    export interface RowSelection {
        id: string;
        code: string;
    }

    export class WorkItem {
        code: string;
        name: string;
        startDate: string;
        endDate: string;
        displayCodeName: string;
        displayExpiration: string;

        constructor(code?: string,
                    name?: string,
                    startDate?: string,
                    endDate?: string) {

            this.code = code;
            this.name = name;
            this.startDate = startDate;
            this.endDate = endDate;
            this.displayCodeName = code + " " + name;
            this.displayExpiration = startDate + ' ～ ' + endDate;
        }
    }
}