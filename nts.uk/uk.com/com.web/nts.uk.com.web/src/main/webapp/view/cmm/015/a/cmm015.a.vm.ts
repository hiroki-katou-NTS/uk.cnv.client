/// <reference path='../../../../lib/nittsu/viewcontext.d.ts' />
module cmm015.a.viewmodel {

    import getText = nts.uk.resource.getText;

    const API = {
        getEmpInWkp: 'com/screen/cmm015/getEmployeesInWorkplace', //職場の所属社員一覧を取得する
        getTransferOfDay: 'com/screen/cmm015/getTransferOfDay', //当日の異動を取得する
        regisJTC: 'com/screen/cmm015/regisJTC', //職位異動の登録をする
        createTransferList: 'com/screen/cmm015/createTransferList',//異動一覧を作成する
        addAWH: 'com/screen/cmm015/addAWH',
        transOnApproved: 'com/screen/cmm015/transOnApproved',
        cancelExecuteTransfer: 'com/screen/cmm015/cancelExecuteTransfer'
    };

    const MAX_DATE: string = '9999/12/31';
    const ERRORSCODE: string = '#FF0000';
    const COMMA: string = '、';

    @bean()
    export class ScreenModel extends ko.ViewModel {
        transferDate: KnockoutObservable<string> = ko.observable('');
        treeGridLeft: any;
        treeGridRight: any;

        selectedIdLeft: KnockoutObservable<string> = ko.observable('');
        selectedIdLeftClone: string = '';
        baseDateLeft: KnockoutObservable<Date> = ko.observable((new Date(moment.utc().subtract(1, 'd').toString())));
        listDataLeft: KnockoutObservableArray<any> = ko.observableArray([]);

        selectedIdRight: KnockoutObservable<string> = ko.observable('');
        selectedIdRightClone: string = '';
        baseDateRight: KnockoutObservable<Date> = ko.observable(new Date(moment.utc().toString()));
        listDataRight: KnockoutObservableArray<any> = ko.observableArray([]);

        incumbentLeft: KnockoutObservable<boolean> = ko.observable(true);
        closedLeft: KnockoutObservable<boolean> = ko.observable(false);
        leaveLeft: KnockoutObservable<boolean> = ko.observable(false);
        retireeLeft: KnockoutObservable<boolean> = ko.observable(false);

        incumbentRight: KnockoutObservable<boolean> = ko.observable(true);
        closedRight: KnockoutObservable<boolean> = ko.observable(false);
        leaveRight: KnockoutObservable<boolean> = ko.observable(false);
        retireeRight: KnockoutObservable<boolean> = ko.observable(false);

        columnsSource: KnockoutObservableArray<any>;
        tableDatasSource: KnockoutObservableArray<DataTable> = ko.observableArray([]);
        currentCodeListSource: KnockoutObservableArray<any> = ko.observableArray([]);

        columnsDest: KnockoutObservableArray<any>;
        tableDatasDest: KnockoutObservableArray<DataTable> = ko.observableArray([]);
        currentCodeListDest: KnockoutObservable<string> = ko.observable('');

        columnsHistory: KnockoutObservableArray<any>;
        tableDatasHistory: KnockoutObservableArray<DataTableHistory> = ko.observableArray([]);
        currentCodeListHistory: KnockoutObservableArray<any> = ko.observableArray([]);

        dateValue: KnockoutObservable<any> = ko.observable({
            startDate: moment.utc().format('YYYY/MM/DD'),
            endDate: MAX_DATE
        });
        startDateString: KnockoutObservable<string> = ko.observable('');
        endDateString: KnockoutObservable<string> = ko.observable('');

        isEnableA3: KnockoutObservable<boolean> = ko.observable(true);
        isEnableA6: KnockoutObservable<boolean> = ko.observable(true);
        isEnableA11: KnockoutObservable<boolean> = ko.observable(true);

        enableTransferDate: KnockoutObservable<boolean> = ko.observable(false);

        baseDateLeftText: KnockoutComputed<string>;
        baseDateRightText: KnockoutComputed<string>;

        transfereeOnApproved: KnockoutObservable<TransfereeOnApproved> = ko.observable({} as any);

        // Data SQ 異動一覧を作成する
        awhItems: any[];
        ajthItems: any[];
        empInfors: any[];
        wkpListInfo: any[];
        jtInfor: any[];

        isReloadKcp = false;

        created() {
            const vm = this;
            vm.treeGridLeft = {
                isShowAlreadySet: false,
                isMultipleUse: true,
                isMultiSelect: false,
                startMode: 0,
                selectedId: vm.selectedIdLeft,
                baseDate: vm.baseDateLeft,
                selectType: 4,
                isShowSelectButton: false,
                isDialog: false,
                maxRows: 10,
                tabindex: 3,
                systemType : 2,
                listDataDisplay: vm.listDataLeft,
                hasPadding: false
            };

            vm.treeGridRight = {
                isShowAlreadySet: false,
                isMultipleUse: true,
                isMultiSelect: false,
                startMode: 0,
                selectedId: vm.selectedIdRight,
                baseDate: vm.baseDateRight,
                selectType: 4,
                isShowSelectButton: false,
                isDialog: false,
                maxRows: 10,
                tabindex: 5,
                systemType : 2,
                listDataDisplay: vm.listDataRight,
                hasPadding: false
            }

            vm.columnsSource = ko.observableArray([
                { headerText: '', key: 'id', hidden: true },
                { headerText: '', key: 'cssWKP', hidden: true },
                {
                    headerText: vm.$i18n('CMM015_21'), key: 'code', width: 115, headerCssClass: 'text-left',
                    template: '<div style="color: ${cssWKP}">${code}</div>'
                },
                {
                    headerText: vm.$i18n('CMM015_22'), key: 'name', width: 140, headerCssClass: 'text-left',
                    template: '<div style="color: ${cssWKP}">${name}</div>'
                },
                { headerText: vm.$i18n('CMM015_23'), key: 'jt', width: 80, headerCssClass: 'text-left' }
            ]);

            vm.columnsDest = ko.observableArray([
                { headerText: '', key: 'id', hidden: true },
                { headerText: '', key: 'jtID', hidden: true },
                { headerText: '', key: 'cssWKP', hidden: true },
                { headerText: '', key: 'cssJT', hidden: true },
                {
                    headerText: vm.$i18n('CMM015_21'), key: 'code', width: 115, headerCssClass: 'text-left',
                    template: '<div style="color: ${cssWKP}">${code}</div>'
                },
                {
                    headerText: vm.$i18n('CMM015_22'), key: 'name', width: 140, headerCssClass: 'text-left',
                    template: '<div style="color: ${cssWKP}">${name}</div>'
                },
                {
                    headerText: vm.$i18n('CMM015_23'), key: 'jt', width: 140, headerCssClass: 'text-left',
                    template: '<div style="color: ${cssJT}">${jt}</div>'
                },
                {
                    headerText: vm.$i18n('CMM015_34'), key: 'jtc', width: 100, headerCssClass: 'text-left',
                    template: '<a class="custom-link" jtid="${jtID}" emp="${id}" style="color: ${cssJT}">${jtc}</a>'
                }
            ]);

            vm.columnsHistory = ko.observableArray([
                { headerText: '', key: 'key', hidden: true },
                { headerText: '', key: 'bgPostWkp', hidden: true},
                { headerText: '', key: 'css', hidden: true},
                {
                    headerText: vm.$i18n('CMM015_45'), key: 'startDate', width: 100, headerCssClass: 'text-left',
                    template: '<div style="color: ${css};">${startDate}</div>'
                },
                {
                    headerText: vm.$i18n('CMM015_46'), key: 'sCD', width: 130, headerCssClass: 'text-left',
                    template: '<div style="color: ${css};">${sCD}</div>'
                },
                {
                    headerText: vm.$i18n('CMM015_47'), key: 'sName', width: 140, headerCssClass: 'text-left',
                    template: '<div style="color: ${css};">${sName}</div>'
                },
                { headerText: vm.$i18n('CMM015_48'), key: 'prevWkpName', width: 220, headerCssClass: 'text-left' },
                { headerText: vm.$i18n('CMM015_49'), key: 'prevPositionName', width: 135, headerCssClass: 'text-left' },
                {
                    headerText: vm.$i18n('CMM015_50'), key: 'postWkpName', width: 220, headerCssClass: 'text-left dest-wkp',
                    template: '<div style="background: ${bgPostWkp}; color: ${css};">${postWkpName}</div>'
                },
                {
                    headerText: vm.$i18n('CMM015_51'), key: 'postPositionName', width: 140, headerCssClass: 'text-left dest-wkp',
                    template: '<div style="color: ${css};">${postPositionName}</div>'
                }
            ]);

            vm.baseDateLeftText = ko.computed(() => {
                const date = moment.utc(vm.baseDateLeft()).format('YYYY/MM/DD');
                return (nts.uk.time as any).applyFormat('Long_YMD', [date]);
            });
            vm.baseDateRightText = ko.computed(() => {
                const date = moment.utc(vm.baseDateRight()).format('YYYY/MM/DD');
                return (nts.uk.time as any).applyFormat('Long_YMD', [date]);
            });

        }

        mounted() {
            const vm = this;
            vm.startDateString.subscribe(value => {
                vm.dateValue().startDate = value;
                vm.dateValue.valueHasMutated();        
            });
            
            vm.endDateString.subscribe(value => {
                vm.dateValue().endDate = value;   
                vm.dateValue.valueHasMutated();      
            });

            vm.selectedIdLeft.subscribe(value => {
                vm.selectedIdLeftClone = value;
                if (!value) {
                    return;
                }

                if (vm.isReloadKcp) {
                    return;
                }
                vm.loadDataWkp(Table.LEFT);
            });

            vm.selectedIdRight.subscribe(value => {
                vm.selectedIdRightClone = value;
                if (!value) {
                    return;
                }

                if (vm.isReloadKcp) {
                    vm.isReloadKcp = false;
                    return;
                }
                vm
                    .loadDataWkp(Table.RIGHT)
                    .then(() => {
                        if (!_.isEmpty(vm.tableDatasDest())) {
                            vm.getTransferOfDay(vm.baseDateRight());
                        }
                    });
            });

            vm.transferDate.subscribe(() => {
                vm
                    .$validate('#A1_2')
                    .then(valid => {
                        if (!valid) {
                            vm.enableTransferDate(false);
                        }
                    })
            });

            $('#A13').click((v) => { //「職位の選択」ダイアログで職位変更
                if (!v.target.classList.contains('custom-link')) {
                    return;
                }

                const employeeID = v.target.getAttribute('emp');

                nts.uk.ui.windows.setShared('inputCDL004', {
                    isMultiple: false,
                    isShowNoSelectRow: true,
                    selectedCodes: v.target.getAttribute('jtID'),
                    isShowBaseDate: false,
                    baseDate: moment(vm.baseDateRight()).utc().toISOString()
                });

                nts.uk.ui.windows.sub
                    .modal('com', '/view/cdl/004/a/index.xhtml')
                    .onClosed(() => {
                        const output = nts.uk.ui.windows.getShared('outputCDL004');
                        if (output) {
                            const index = _.findIndex(vm.tableDatasDest(), (item: DataTable) => item.id === employeeID);
                            const data: DataTable = vm.tableDatasDest()[index];
                            if (vm.isDuplicateId(data.jtID, output)) return;

                            vm.$blockui('grayout');
                            // 職位異動の登録をする
                            vm
                                .$ajax('com', API.regisJTC, {
                                    sid: data.id,
                                    jobTitleId: output,
                                    startDate: moment.utc(vm.baseDateRight()).toISOString(),
                                    endDate: moment.utc(new Date(MAX_DATE)).toISOString()
                                })
                                .then(() => {
                                    vm
                                        .loadDataWkp(Table.RIGHT)
                                        .then(() => vm.getTransferOfDay(vm.baseDateRight()));
                                    // UI処理[6]
                                    vm.currentCodeListDest('');
                                    vm.createTransferList();
                                    vm.$dialog.info({ messageId: 'Msg_15' });
                                })
                                .fail(({messageId}) => vm.$dialog.error({ messageId: messageId }));
                            vm.$blockui('clear');
                        }
                    });
            });
            
            vm.startPage();
            $('#A1_2').focus();
        }

        startPage() {
            const vm = this;
            vm.$blockui('grayout');
            $.when(
                $('#tree-grid-left').ntsTreeComponent(vm.treeGridLeft),
                $('#tree-grid-right').ntsTreeComponent(vm.treeGridRight)
            )
            .then(() => {
                const idLeft = $($('#tree-grid-left [data-id]')[0]).attr('data-id');
                const idRight = $($('#tree-grid-right [data-id]')[0]).attr('data-id');
                if (idLeft) {
                    vm.selectedIdLeft(idLeft);
                }

                if (idRight) {
                    vm.selectedIdRight(idRight);
                }
            });

            vm.createTransferList();
            vm.$blockui('clear');
        }

        clickMeans() {
            const vm = this;
            vm.$blockui('grayout');
            
            if (_.isEmpty(vm.transferDate())) {
                vm.$dialog.error({ messageId: 'Msg_2105' }).then(() => vm.$blockui('clear'));
                return;
            }
            vm.$validate('#A1_2').then(valid => {
                if (!valid) {
                    nts.uk.ui.errors.show();
                    vm.enableTransferDate(false);
                    vm.$blockui('clear');
                    return;
                }
                const date = ko.unwrap<string>(vm.transferDate);
                vm.baseDateRight(new Date(vm.transferDate()));
                vm.baseDateLeft(new Date(moment.utc(date).subtract(1, 'd').toString()));
                vm.enableTransferDate(true);
                vm
                    .reloadKcp()
                    .then(() => vm.loadDataWkp(Table.RIGHT))
                    .then(() => vm.getTransferOfDay(new Date(vm.transferDate())));
            });
            vm.$blockui('clear');
        }

        reloadKcp(): JQueryPromise<any> {
            const vm = this, dfd = $.Deferred();
            const $selectedLeft = $(`#tree-grid-left [data-id="${vm.selectedIdLeft()}"]`);
            const $selectedRight = $(`#tree-grid-right [data-id="${vm.selectedIdRight()}"]`);
            // get selected index
            let indexLeft = $selectedLeft.parent().children().index($selectedLeft);
            let indexRight = $selectedRight.parent().children().index($selectedRight);

            vm.isReloadKcp = true;
            $
                .when(
                    $('#tree-grid-left').ntsTreeComponent(vm.treeGridLeft),
                    $('#tree-grid-right').ntsTreeComponent(vm.treeGridRight)
                )
                .then(() => {
                    vm.setSelectedDataKcp(indexLeft, indexRight);
                    vm.loadDataWkp(Table.LEFT);
                    vm.loadDataWkp(Table.RIGHT);
                    $('#tree-grid-left [data-id]').click(() => {
                        if (_.isEmpty(vm.selectedIdLeftClone)) {
                            vm.selectedIdLeft.valueHasMutated();
                        }
                        vm.selectedIdLeftClone = '';
                    });
                    $('#tree-grid-right [data-id]').click(() => {
                        if (_.isEmpty(vm.selectedIdRightClone)) {
                            vm.selectedIdRight.valueHasMutated();
                        }
                        vm.selectedIdRightClone = '';
                    });
                    dfd.resolve();
                })
                .fail(err => {
                    vm.$dialog.error(err);
                    dfd.reject();
                })
            return dfd.promise();
        }

        setSelectedDataKcp(indexLeft: number, indexRight: number) {
            const vm = this;
            const dataLengthLeft = $("#tree-grid-left [data-id]").length || 0;
            const dataLengthRight = $("#tree-grid-right [data-id]").length || 0;
            vm.selectedIdLeft('');
            vm.selectedIdRight('');
            if (indexLeft >= dataLengthLeft) {
                indexLeft = dataLengthLeft - 1;
            } else if (dataLengthLeft > 0 && indexLeft <0) {
                indexLeft = 0;
            }

            if (indexLeft >= 0) {
                const selectedId = $($("#tree-grid-left [data-id]")[indexLeft]).attr('data-id');
                vm.selectedIdLeft(selectedId);
            }

            if (indexRight >= dataLengthRight) {
                indexRight = dataLengthLeft - 1;
            } else if (dataLengthRight > 0 && indexRight < 0) {
                indexRight = 0;
            }

            if (indexRight >= 0) {
                const selectedId = $($("#tree-grid-right [data-id]")[indexRight]).attr('data-id');
                vm.selectedIdRight(selectedId);
            }

        }

        onClickTransfer() {
            const vm = this;
            vm
                .$blockui('grayout')
                .then(() => vm.createTransferList())
                .always(() => this.$blockui('clear'));
        }

        /**
         * 「異動登録ボタン」を押下する
         */
        onClickRegister() {
            const vm = this;
            if (vm.isEmptySelectedSource()) {
                return;
            }
            // UI処理[18]
            _.map(vm.tableDatasSource(), i => i.cssWKP = '');
            vm.tableDatasSource.valueHasMutated();

            const selectedSidsource: string[] = [];
            const param = _.map(vm.currentCodeListSource(), sid => {
                selectedSidsource.push(sid);
                return {
                    employeeId: sid,
                    workplaceId: vm.selectedIdRight(),
                    startDate: moment.utc(new Date(vm.transferDate())).toISOString(),
                    endDate: moment.utc(new Date(MAX_DATE)).toISOString()
                };
            });
            vm.$blockui('grayout');
            vm
                .$ajax('com', API.addAWH, param)
                .then((res: any[]) => {
                    // If has erros
                    const ifErrors = () => {
                        const errorList = _.map(res, i => i.errorLst[0]);
                        vm.$dialog
                            .error({ messageId: 'Msg_102' })
                            .then(() => ifNotErrors());
                        vm.changeColorWhenError(errorList);
                    }

                    // If not has errors
                    const ifNotErrors = () => {
                        if (res.length >= vm.currentCodeListSource().length) return;
                        vm
                            .loadDataWkp(Table.RIGHT)
                            .then(() => {
                                // UI処理[6]
                                vm.currentCodeListDest('');
                                // 当日の異動を取得する
                                return vm.getTransferOfDay(new Date(vm.transferDate()));
                            })
                            .then(() => vm.createTransferList())
                            .then(() => {
                                //UI処理[16]
                                vm.currentCodeListSource.removeAll();
                                // UI処理[15]
                                vm.$dialog
                                    .info({ messageId: 'Msg_15' })
                                    .then(() => vm.transOnApproved(selectedSidsource));
                            });
                    };
                    
                    if (_.isEmpty(res)) {
                        ifNotErrors();
                    } else {
                        ifErrors();
                    }
                })
                .fail(err => vm.$dialog.error({ messageId: err.messageId }));
            vm.$blockui('clear');
        }

        /**
         * 職場の所属社員一覧を取得する
         * @param table LEFT OR RIGHT
         */
        loadDataWkp(table: Table): JQueryPromise<any> {
            const vm = this, dfd = $.Deferred();
            
            const paramLeft = {
                wkpIds: [vm.selectedIdLeft()],
                referDate: moment.utc(vm.baseDateLeft()),
                incumbent: vm.incumbentLeft(),
                closed: vm.closedLeft(),
                leave: vm.leaveLeft(),
                retiree: vm.retireeLeft()
            };

            const paramRight = {
                wkpIds: [vm.selectedIdRight()],
                referDate: moment.utc(vm.baseDateRight()),
                incumbent: vm.incumbentRight(),
                closed: vm.closedRight(),
                leave: vm.leaveRight(),
                retiree: vm.retireeRight()
            };

            const isLeft = table === Table.LEFT;

            const param = isLeft ? paramLeft : paramRight;
            
            vm
                .$ajax('com', API.getEmpInWkp, param)
                .then((res: EmployeesInWorkplace[]) => {
                    const tableDatas = _
                        .map(res, item => new DataTable(item, isLeft ? '' : vm.$i18n('CMM015_35')))
                        .sort((first, second) => {
                            let firstOrder = first.od;
                            let secondOrder = second.od;
                            if (_.isNil(firstOrder)) {
                                // if order is null, set data to the end of list
                                firstOrder = Number.MAX_VALUE;
                            }
                            if (_.isNil(secondOrder)) {
                                // if order is null, set data to the end of list
                                secondOrder = Number.MAX_VALUE;
                            }

                            return firstOrder - secondOrder || first.code.localeCompare(second.code);
                        });

                    if (isLeft) {
                        vm.currentCodeListSource.removeAll();
                        vm.tableDatasSource(tableDatas);
                        vm.tableDatasSource.valueHasMutated();
                    } else {
                        vm.currentCodeListDest('');
                        vm.tableDatasDest(tableDatas);
                        vm.tableDatasDest.valueHasMutated();
                    }
                    dfd.resolve();
                })
                .fail(err => {
                    vm.$dialog.error(err);
                    dfd.reject();
                })
            return dfd.promise();
        }

        showMessageApprove() {
            const vm = this;
            //List<承認ルート状況>
            const approvalRoutes = vm.transfereeOnApproved().approvalRoutes;
            //List<社員情報>
            const empInfors = vm.transfereeOnApproved().empInfors;
            //List<職場情報一覧>
            const wkpListInfo = vm.transfereeOnApproved().wkpListInfo;

            let msg2110 = '';
            let msg2111 = '';
            let msg2112 = '';

            _.map(approvalRoutes, i => {
                const employeeName = _.find(empInfors, ii => ii.employeeId === i.sid).businessName;
                //承認職場リスト
                if (!_.isEmpty(i.workplaceList)) {
                    const aprWkpName = _
                        .map(i.workplaceList, ii => _.find(wkpListInfo, iii => ii === iii.workplaceId).workplaceName)
                        .join(COMMA);
                    
                    msg2110 += `${vm.$i18n.message('Msg_2110', [employeeName, aprWkpName])}<br/>`;
                }
                
                //承認対象社員リスト
                if (!_.isEmpty(i.approveEmpList)) {
                    const aprEmpName = _.
                        map(i.approveEmpList, ii => _.find(empInfors, iii => ii === iii.employeeId).businessName)
                        .join(COMMA);

                    msg2111 += `${vm.$i18n.message('Msg_2111', [employeeName, aprEmpName])}<br/>`;
                }
                
                //指定者の承認者リスト
                if (!_.isEmpty(i.approverList)) {
                    const aprListName = _
                        .map(i.approverList, ii => _.find(empInfors, iii => ii === iii.employeeId).businessName)
                        .join(COMMA);

                    msg2112 += `${vm.$i18n.message('Msg_2112', [employeeName, aprListName])}<br/>`;
                }
                
            });

            const msg = `${msg2110}${msg2111}${msg2112}`;
            if (!_.isEmpty(msg)) {
                vm.$dialog.info(msg);
            }
        }

        isEmptySelectedSource(): boolean {
            const vm = this;
            if (_.isEmpty(vm.currentCodeListSource())) {
                vm.$dialog.error({ messageId: 'Msg_2107' });
                return true;
            }
            return false;
        }

        changeColorWhenError(errorList: string[]) {
            const vm = this;
            _.map(vm.tableDatasSource(), j => {
                if (_.includes(errorList, j.id)) {
                    j.cssWKP = `${ERRORSCODE} !important`;
                }
            });
            vm.tableDatasSource.valueHasMutated();
        }

        isDuplicateId(jtID: string, newID: string): boolean {
            const vm = this;
            if(jtID === newID) {
                vm.$dialog.error({ messageId: 'Msg_2120' });
                return true;
            }
            return false;
        }

        //当日の異動を取得する
        getTransferOfDay(date: Date): JQueryPromise<any> {
            const vm = this, dfd = $.Deferred();
            if (_.isEmpty(vm.tableDatasDest())) {
                dfd.resolve();
                return dfd.promise();
            }
            let dataTableDest = ko.unwrap<DataTable[]>(vm.tableDatasDest);
            const sids: any[] = _.map(dataTableDest, e => e.id);
            const baseDate = moment.utc(moment(date).format('YYYY-MM-DD')).toISOString();
            vm
                .$ajax('com', API.getTransferOfDay, {sids, baseDate})
                .then(({wkpEmployees, jtEmployees}: TransferOfDay) => {
                    const data = _.map(vm.tableDatasDest(), i => {
                        if (_.includes(wkpEmployees, i.id)) {
                            i.cssWKP = `${ERRORSCODE} !important`;
                        }

                        if (_.includes(jtEmployees, i.id)) {
                            i.cssJT = `${ERRORSCODE} !important`;
                        }
                        return i;
                    });

                    vm.tableDatasDest(data);
                    dfd.resolve();
                })
                .fail(err => {
                    vm.$dialog.error(err);
                    dfd.reject();
                });
            
            return dfd.promise();
        }

        //異動一覧を作成する
        createTransferList(): JQueryPromise<any> {
            const vm = this, dfd = $.Deferred();
            const startDate = moment.utc(new Date(vm.dateValue().startDate)).toISOString();
            const endDate = moment.utc(new Date(vm.dateValue().endDate)).toISOString();
            vm
                .$ajax('com', API.createTransferList, { startDate, endDate })
                .then(({awhItems, ajthItems, empInfors, wkpListInfo, jtInfor}: TransferList) => {
                    // 職場履歴リスト
                    vm.awhItems = awhItems;
                    // 職位履歴リスト
                    vm.ajthItems = ajthItems;
                    // List<社員一覧情報>
                    vm.empInfors = empInfors;
                    // List<職場一覧情報>
                    vm.wkpListInfo = wkpListInfo;
                    // List<職位一覧情報>
                    vm.jtInfor = jtInfor;

                    vm.uiProcess25();
                    dfd.resolve();
                })
                .fail(err => {
                    vm.tableDatasHistory.removeAll();
                    vm.$dialog.error(err);
                    dfd.reject();
                    vm.$blockui('clear');
                })

            return dfd.promise();
        }

        //異動者が承認ルートにいるか確認する
        transOnApproved(sids: string[]): JQueryPromise<any> {
            const vm = this, dfd = $.Deferred();
            const baseDate = moment.utc(new Date(vm.transferDate())).toISOString();
            vm
                .$ajax('com', API.transOnApproved, { sids, baseDate })
                .then((res:TransfereeOnApproved) => {
                    vm.transfereeOnApproved(res);
                    vm.showMessageApprove();
                    dfd.resolve();
                })
                .fail(err => {
                    vm.$dialog.error({ messageId: err.messageId });
                    dfd.reject();
                });

            return dfd.promise();
        }

        uiProcess25() {
            const vm = this;

            const condition = (date: string): boolean => {
                const startDate = moment.utc(new Date(vm.dateValue().startDate));
                const endDate = moment.utc(new Date(vm.dateValue().endDate));
                return moment.utc(new Date(date)).isSameOrAfter(startDate)
                    && moment.utc(new Date(date)).isSameOrBefore(endDate);
            };

            const compare = (firstDate: string, secondDate: string): number => {
                return moment(new Date(firstDate)).isSameOrAfter(moment(new Date(secondDate))) ? 1 : -1;
            };

            vm.awhItems.sort((first, second) => compare(first.startDate, second.startDate));
            vm.ajthItems.sort((first, second) => compare(first.startDate, second.startDate));
            
            // Remove start date outside display period
            const awhItems = _.filter(vm.awhItems, i => condition(i.startDate))
                .map(i => {
                    i.wkpHID = i.historyId;
                    i.historyId = '';
                    return i;
                });

            const ajthItems = _.filter(vm.ajthItems, i => condition(i.startDate))
                .map(i => {
                    i.jtHID = i.historyId;
                    i.historyId = '';
                    return i;
                });
            
            // summarize in 1 table
            const arr: HistoryItem[] = [...awhItems, ...ajthItems];

            // Sort by date
            arr.sort((first, second) => compare(first.startDate, second.startDate));

            // Summarize the same employees and the same start date
            let summarizeArr: HistoryItem[] = [];
            _.forEach(arr, (i, index) => {
                let item = i;

                const summarize =_.find(arr, (j, jindex) =>
                    i.employeeId === j.employeeId && index !== jindex &&
                    moment.utc(new Date(i.startDate)).isSame(moment.utc(new Date(j.startDate)))
                );
                
                if (summarize) {
                    if (moment(new Date(item.endDate)).isBefore(moment(new Date(summarize.endDate)))) {
                        summarize.endDate = item.endDate;
                    } else {
                        item.endDate = summarize.endDate;
                    }
                    item = $.extend(item, summarize);
                }
                summarizeArr.push(item);
            });
            // Remove duplicate item
            summarizeArr = _.uniqBy(summarizeArr, i => `${i.employeeId} ${i.startDate}`);
            const displayData: DataTableHistory[] = _.map(summarizeArr, i => {
                const post = i;
                const compareDate = moment.utc(new Date(i.startDate)).subtract(1, 'd');

                // Find previous item
                const prevAWH = _.findLast(vm.awhItems, j =>
                    moment.utc(new Date(j.startDate)).isSameOrBefore(compareDate)
                    && j.employeeId === i.employeeId);
                const prevJTH = _.findLast(vm.ajthItems, j =>
                    moment.utc(new Date(j.startDate)).isSameOrBefore(compareDate)
                    && j.employeeId === i.employeeId);
                return new DataTableHistory(prevAWH, prevJTH, post, vm.empInfors, vm.wkpListInfo, vm.jtInfor);
            });

            displayData.sort((first, second) => {
                return moment(new Date(first.startDate)).diff(moment(new Date(second.startDate)))
                    || first.postPositionOrder - second.postPositionOrder
                    || first.sCD.localeCompare(second.sCD);
            });

            vm.tableDatasHistory(displayData);
        }

        cancelExecuteTransfer() {
            const vm = this;
            vm.$blockui('grayout');
            // Clear red color in table history
            _.map(vm.tableDatasHistory(), i => i.css = '');
            vm.tableDatasHistory.valueHasMutated();
            if (_.isEmpty(vm.currentCodeListHistory())) {
                vm.$dialog.error({ messageId: 'Msg_2115' });
                vm.$blockui('clear');
                return;
            }

            const affWorkplaces: DeleteAffWorkplaceHistoryCommand[] = [];
            const affJobTitles: DeleteAffJobTitleMainCommand[] = [];
            const errList: string[] = [];
            _.forEach(vm.currentCodeListHistory(), i => {
                const selectedData = _.find(vm.tableDatasHistory(), item => item.key === i);
                if (!selectedData) {
                    return;
                }

                if (selectedData.endDate !== MAX_DATE) {
                    errList.push(selectedData.key);
                    return;
                }

                if (selectedData.wkpHID) {
                    affWorkplaces.push(new DeleteAffWorkplaceHistoryCommand({
                        employeeId: selectedData.sID,
                        historyId: selectedData.wkpHID
                    }));
                }
                
                if (selectedData.postPositionHID) {
                    affJobTitles.push(new DeleteAffJobTitleMainCommand({
                        employeeId: selectedData.sID,
                        histId: selectedData.postPositionHID
                    }));
                }
            });

            const errFnc = (callback?: Function) => {
                if (!_.isEmpty(errList)) {
                    vm.$dialog
                        .error({ messageId: 'Msg_2203' })
                        .then(() => {
                            _.map(vm.tableDatasHistory(), i => {
                                if (_.includes(errList, i.key)) {
                                    i.css = `${ERRORSCODE} !important`;
                                }
                            });
                            if (callback) {
                                callback();
                            }
                            vm.tableDatasHistory.valueHasMutated();
                        });
                } else if (callback) {
                    callback();
                    vm.tableDatasHistory.valueHasMutated();
                }
            };
            if (errList.length === vm.currentCodeListHistory().length) {
                errFnc();
                vm.$blockui('clear');
                return;
            }
            
            const param = { affWorkplaces, affJobTitles };
            let affWorkplaceErr: any[] = [];
            let affJobTitleErr: any[] = [];
            
            vm
                .$ajax('com', API.cancelExecuteTransfer, param)
                .then((res: CancelExecuteTransferResult) => {
                    affWorkplaceErr = res.affWorkplaceErr;
                    affJobTitleErr = res.affJobTitleErr;
                    vm.createTransferList();
                    vm.loadDataWkp(Table.LEFT);
                    return vm.loadDataWkp(Table.RIGHT);
                })
                .then(() => vm.getTransferOfDay(vm.baseDateRight()))
                .then(() => {
                    // Merge 2 list error
                    const mergeErr = [...affWorkplaceErr, ...affJobTitleErr];
                    // Get list error id
                    const errIds = _.map(mergeErr, i => i.errorLst[0]);
                    const mergeErrMsg = _.uniq(_.map(mergeErr, i => `${vm.$i18n.message(i.messageId)}<br/>`));
                    const success = () => {
                        _.map(vm.tableDatasHistory(), i => {
                            // If id data equals error id => change color to red
                            if (_.includes(errIds, `${i.sID} ${i.wkpHID}`) || _.includes(errIds, `${i.sID} ${i.postPositionHID}`)) {
                                i.css = `${ERRORSCODE} !important`;
                            }
                        });

                        if (mergeErr.length === affWorkplaces.length + affJobTitles.length) {
                            vm.tableDatasHistory.valueHasMutated();
                            vm.$blockui('clear');
                            return;
                        }
                        vm.$dialog.info({ messageId: 'Msg_16' });
                    };
                    if (!_.isEmpty(mergeErr)) {
                        errFnc(() => vm.$dialog
                            .error(mergeErrMsg.join(''))
                            .then(() => success()));
                    } else {
                        errFnc(() => success());
                    }
                    
                });
            vm.$blockui('clear');
        }

    }

    enum Table { LEFT, RIGHT }

    class DeleteAffWorkplaceHistoryCommand {
	    employeeId: string = '';
	    historyId: string = '';

        constructor(init: DeleteAffWorkplaceHistoryCommand) {
            $.extend(this, init);
        }
    }

    class DeleteAffJobTitleMainCommand {
	    histId: string = '';
	    employeeId: string = '';

        constructor(init: DeleteAffJobTitleMainCommand) {
            $.extend(this, init);
        }
    }

    interface CancelExecuteTransferResult {
        affWorkplaceErr: any[];
        affJobTitleErr: any[];
    }

    interface EmployeesInWorkplace {
        employeeCD: string;
        employeeID: string;
        employeeName: string;
        jobtitle: string;
        jobtitleID: string;
        order: number;
    }

    interface TransferOfDay {
        wkpEmployees: string[];
        jtEmployees: string[];
    }

    interface TransferList {
        awhItems: any[];
        ajthItems: any[];
        empInfors: any[];
        wkpListInfo: any[];
        jtInfor: any[];
    }

    class DataTable {
        code: string;
        id: string;
        name: string;
        jt: string;
        jtID: string;
        od: number;
        jtc: string;
        cssJT: string;
        cssWKP: string;

        constructor(data: EmployeesInWorkplace, jtc: string) {
            const vm = this;
            vm.id = data.employeeID;
            vm.code = data.employeeCD;
            vm.name = data.employeeName;
            vm.jt = data.jobtitle;
            vm.jtID = data.jobtitleID;
            vm.od = data.order;
            vm.jtc = jtc;
        }
    }

    class DataTableHistory {
        key: string = ''; 
        dayBefore: string = ''; //前日
        startDate: string = ''; //開始日
        endDate: string = ''; //終了日
        sID: string = ''; //社員ID
        sCD: string = ''; //社員CD
        sName: string = ''; //社員名称
        prevWkpID: string = ''; //前職場ID
        prevWkpName: string = ''; //前職場名称
        prevWkpHID: string = ''; //前職場履歴ID
        postWkpID: string = ''; //後職場ID
        postWkpName: string = ''; //後職場名称
        wkpHID: string = ''; //後職場履歴ID
        prevPositionID: string = ''; //前職位ID
        prevPositionName: string = ''; //前職位名称
        prevPositionOrder: number = 999; //前職位並び順
        prevPositionHID: string = ''; //前職位履歴ID
        positionId: string = ''; //後職位ID
        postPositionName: string = ''; //後職位名称
        postPositionOrder: number = 999; //後職位並び順
        postPositionHID: string = ''; //後職位履歴ID
        bgPostWkp: string = '';
        css: string = '';

        constructor(prevAWH: HistoryItem, prevAJH: HistoryItem, post: HistoryItem, empInfors: any[], wkpListInfo: any[], jtInfor: any[]) {
            const self = this;
            self.endDate = MAX_DATE;
            
            if (prevAWH) {
                self.prevWkpID = prevAWH.workplaceId;
                self.prevWkpName = _.isNil(prevAWH.workplaceId) || _.isEmpty(prevAWH.workplaceId)
                    ? ''
                    : _.find(wkpListInfo, i => i.workplaceId === prevAWH.workplaceId).workplaceName;
                self.prevWkpHID = prevAWH.wkpHID;
            }

            if (prevAJH) {
                self.prevPositionID = prevAJH.jobTitleId;
                const prevPosition = _.find(jtInfor, i => i.jobTitleId === prevAJH.jobTitleId);
                self.prevPositionName = prevPosition.jobTitleName;
                self.prevPositionOrder = prevPosition.order;
                self.prevPositionHID = prevAJH.jtHID;
            }

            if (post) {
                self.dayBefore = moment.utc(new Date(post.startDate)).subtract(1, 'd').toString();
                self.startDate = post.startDate;
                self.sID = post.employeeId;
                const { employeeCode, businessName } = _.find(empInfors, i => i.employeeId === post.employeeId);
                self.sCD = employeeCode;
                self.sName = businessName;
                self.postWkpID = post.workplaceId;
                if (_.isNil(post.workplaceId) || _.isEmpty(post.workplaceId)) {
                    self.postWkpName = getText('CMM015_52');
                    self.bgPostWkp = '#FFFFCC';
                } else {
                    self.postWkpName = _.find(wkpListInfo, i => i.workplaceId === post.workplaceId).workplaceName;
                }
                self.wkpHID = post.wkpHID;

                self.positionId = post.jobTitleId;
                const postPosition = _.find(jtInfor, i => i.jobTitleId === post.jobTitleId);
                if (postPosition) {
                    self.postPositionName = postPosition.jobTitleName;
                    self.postPositionOrder = postPosition.order;
                }

                if (_.isEmpty(self.postPositionName) && _.isEmpty(self.postPositionOrder)) {
                    self.postPositionName = self.prevPositionName;
                    self.postPositionOrder = self.prevPositionOrder;
                }
                self.postPositionHID = post.jtHID;
                self.endDate = post.endDate;

            }

            self.key = `${self.sID} ${self.startDate}`;
        }
    }

    class HistoryItem {
        startDate: string;
        endDate: string;
        employeeId: string;
        workplaceId: string;
        normalWorkplaceId: string;
        jobTitleId: string;
        wkpHID: string;
        jtHID: string;
        order: number;

        constructor(init: HistoryItem) {
            $.extend(this, init);
        }
    }

    interface TransfereeOnApproved {
        approvalRoutes: ApprovalRouteSpStatus[];
        
        empInfors: EmployeeInformation[];
        
        wkpListInfo: WorkplaceInfor[];
    }

    interface ApprovalRouteSpStatus {
        sid: string;
        workplaceList: string[];
        approveEmpList: string[];
        approverList: string[];
        workplace: WorkplaceInfor;
    }

    interface EmployeeInformation {
        employeeId: string;
        employeeCode: string;
        businessName: string;
    }

    interface WorkplaceInfor {
        workplaceId: string;
        workplaceCode: string;
        workplaceName: string;
    }
}
