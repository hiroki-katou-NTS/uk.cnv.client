/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

import characteristics = nts.uk.characteristics;
import IScheduleImport = nts.uk.at.view.kdl055.a.viewmodel.IScheduleImport;
import setShared = nts.uk.ui.windows.setShared;
import getShared = nts.uk.ui.windows.getShared;

module nts.uk.at.view.kdl055.b.viewmodel {

    @bean()
    export class KDL055BViewModel extends ko.ViewModel {
        overwrite: boolean = false;
        filename: KnockoutObservable<string> = ko.observable(null);
        gridOptions: any = { dataSource: [], columns: [], features: [], ntsControls: [] };
        data: CaptureDataOutput = null;
        errorList: any[] = [];
        isOpenKDL053: boolean = false;
        isEnableRegister: KnockoutObservable<boolean> = ko.observable(true);
        isEnableOpenKDL053: KnockoutObservable<boolean> = ko.observable(false);
        windows_lst: any = null;

        created(params: any) {
            const vm = this;

            characteristics.restore('ScheduleImport').then((obj: IScheduleImport) => {
                if (obj) {
                    vm.overwrite = obj.overwrite;
                    vm.filename(obj.mappingFile);
                }
            }).then(() => {
                let dataShare = getShared('dataShareDialogKDL055B');
                if (dataShare) {
                    setTimeout(() => {
                        vm.$blockui('grayout');
                        vm.$ajax(paths.getCaptureData, { data: dataShare, overwrite: vm.overwrite }).done((res: CaptureDataOutput) => {
                            if (res) {
                                console.log(res);
                                vm.data = res;
                            }
                        }).then(() => {
                            if (vm.data) {
                                vm.convertToGrid(vm.data);
                                vm.loadGrid();
                                vm.loadError(vm.data);
                            }
                        }).fail((err: any) => {
                            if (err) {
                                vm.$dialog.error({ messageId: err.messageId, messageParams: err.parameterIds });
                            }
                        })
                        .always(() => {vm.$blockui('hide'); console.log("END blockUI")});    
                    }, 150);
                } else if (params) {
                    setTimeout(() => {
                        vm.$blockui('grayout');
                        vm.$ajax(paths.getCaptureData, { data: params, overwrite: vm.overwrite }).done((res: CaptureDataOutput) => {
                            if (res) {
                                console.log(res);
                                vm.data = res;
                            }
                        }).then(() => {
                            if (vm.data) {
                                vm.convertToGrid(vm.data);
                                vm.loadGrid();
                                vm.loadError(vm.data);
                            }
                        }).fail((err: any) => {
                            if (err) {
                                vm.$dialog.error({ messageId: err.messageId, messageParams: err.parameterIds });
                            }
                        })
                        .always(() => {vm.$blockui('hide'); console.log("END blockUI")});
                    }, 150);
                }
            });
            

            setInterval(() => {
                if ($('#grid')) {
                    try {
                        let errors = $('#grid').mGrid('errors');
                        let dataSource: any[] = $("#grid").mGrid("dataSource");
                        let states = _.filter(vm.gridOptions.features, {'name': 'CellStyles'})[0].states.filter(x => x.columnKey != 'nameHeader').map(x => x.state);                  

                        if (errors.length > 0 || _.filter(states, x => !_.includes(x, 'mgrid-disable')).length === 0 || dataSource.length === 0) {
                            this.isEnableRegister(false);
                        } else {
                            this.isEnableRegister(true);
                        }
                        
                        if (vm.errorList.length > 0) {
                            this.isEnableOpenKDL053(true);
                        } else {
                            this.isEnableOpenKDL053(false);
                        }
                    } catch (error) {
                        // empty                        
                    }
                }
            }, 100);
        }
        
        mounted() {
            const vm = this;

            vm.windows_lst = nts.uk.ui.windows.container.windows;
        }

        register() {
            const vm = this;
            let targets: ScheduleRegisterTarget[] = [];

            let dataSource: any[] = $("#grid").mGrid("dataSource");
            let cellStates: any[] = _.filter(vm.gridOptions.features, {'name': 'CellStyles'})[0].states;

            _.forEach(dataSource, item => {
                _.forEach(vm.data.importableDates, date => {
                    let target: ScheduleRegisterTarget = {employeeId: item.employeeId, date: '', importCode: ''};
                    if (item[date]) {
                        target.date = date;
                        target.importCode = item[date];
                        
                        if (_.filter(cellStates, { 'columnKey': date, 'rowId': item.employeeId,  }).length > 0) {
                            let states = _.filter(cellStates, { 'columnKey': date, 'rowId': item.employeeId,  })[0].state;
                            if (!_.includes(states, 'mgrid-disable')) {
                                targets.push(target);
                            }
                        }
                    }
                })
            });

            let command = {
                targets: targets,
                overwrite: vm.overwrite
            }
            // close KDL053
            if (vm.windows_lst) {
                let selfId = nts.uk.ui.windows.selfId;
                for (let id in vm.windows_lst) {
                    if (nts.uk.ui.windows.container.windows[id].parent && nts.uk.ui.windows.container.windows[id].parent.id == selfId) {
                        nts.uk.ui.windows.close(id);
                        vm.isOpenKDL053 = false;
                    }
                }
            }

            vm.$blockui('grayout');

            // Async
            vm.$ajax(paths.register, command).done((res) => {
                let taskId = res.taskInfor.id;
                vm.checkStateAsyncTask(taskId);
                
            }).fail(function(error) {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError(error);
            });
            
            // Async

            // vm.$ajax(paths.register, command).done((res) => {
            //     if (res) {
            //         // reset list data fail
            //         vm.data.mappingErrorList = [];
            //         vm.errorList = [];

            //         if (res.length > 0) {
            //             let request: any = {};
            //             request.errorRegistrationList = [];
            //             _.forEach(res, errorItem => {
            //                 // bind to list mappingErrorList

            //                 // let empFilter = _.filter(vm.data.listPersonEmp, {'employeeCode': errorItem.employeeCode});
            //                 // let empId = empFilter.length > 0 ? empFilter[0].employeeId : '';

            //                 let error: MappingErrorOutput = {employeeCode: errorItem.employeeCode, employeeName: errorItem.employeename, date: errorItem.date, errorMessage: errorItem.errorMessage};
            //                 vm.data.mappingErrorList.push(error);
            //                 error.isErrorGrid = true;
            //                 vm.errorList.push(error);
            //                 vm.$blockui("hide");

            //             });

            //             // set error list
            //             let errors: any[] = [];
            //             let listPersonEmp = vm.sortListEmpInfo(vm.data.listPersonEmp, vm.data.importResult.orderOfEmployees);

            //             for(let j = 0; j < res.length; j++) {
            //                 let err: any = { columnKey: 'nameHeader', id: null, index: null, message: res[j].errorMessage, isErrorGrid: true };
                            
            //                 if (res[j].employeeCode) {
            //                     let empFilter = _.filter(listPersonEmp, {'employeeCode': res[j].employeeCode});
            //                     let empId = empFilter.length > 0 ? empFilter[0].employeeId : '';
            //                     err.id = empId;
            //                     for (let i = 0; i < listPersonEmp.length; i++) {
            //                         if (listPersonEmp[i].employeeCode === res[j].employeeCode) {
            //                             err.id = listPersonEmp[i].employeeId;
            //                             err.index = i;
            //                         }
            //                     }
            //                 }
            //                 if (res[j].date) {
            //                     err.columnKey = res[j].date;
            //                 }
                            
            //                 if (err.index != null) {
            //                     errors.push(err);
            //                 }
            //             }

            //             $("#grid").mGrid("setErrors", errors);

            //             // open KDL053
            //             request.employeeIds = _.map(vm.data.listPersonEmp, (item) => item.employeeId);
            //             let empList = vm.data.listPersonEmp;
            //             for (let i = 0; i < vm.errorList.length; i++) {
            //                 let empFilter = _.filter(empList, {'employeeCode': vm.errorList[i].employeeCode});
            //                 let empId = empFilter.length > 0 ? empFilter[0].employeeId : '';

            //                 let item: any = {id: i, sid: empId, scd: vm.errorList[i].employeeCode == null ? '' : vm.errorList[i].employeeCode, 
            //                     empName: vm.errorList[i].employeeName == null ? '' : vm.errorList[i].employeeName, 
            //                     date: vm.errorList[i].date == null ? '' : vm.errorList[i].date, attendanceItemId: null, errorMessage: vm.errorList[i].errorMessage};
            //                 request.errorRegistrationList.push(item);
            //                 if (!request.employeeIds.includes(item.sid)) {
            //                     request.employeeIds.push(item.sid);
            //                 }
            //             }
            //             request.isRegistered = 1;
            //             request.dispItemCol = true;
            //             if (!vm.isOpenKDL053) {
            //                 vm.$window.modeless('at', '/view/kdl/053/a/index.xhtml', request).then(() => {
            //                     vm.isOpenKDL053 = false;
            //                 });
            //                 vm.isOpenKDL053 = true;
            //             }
            //         } else {
            //             vm.$dialog.info({ messageId: "Msg_15"}).then(() => {
            //                 vm.$blockui("hide");
            //                 vm.close(true);
            //             });                        
            //         }
            //     }
            // }).fail((err) => {
            //     if (err) {
            //         vm.$dialog.error({ messageId: err.messageId, messageParams: err.parameterIds });
            //     }
            // }).always(() => {
            //     vm.$blockui("hide");
            // });
        }

        checkStateAsyncTask(taskId: string) {
            const vm = this;

            nts.uk.deferred.repeat(conf => conf
                .task(() => {
                    return nts.uk.request.asyncTask.getInfo(taskId).done(function(res: any) {
                        if (res.succeeded || res.failed || res.cancelled) {
                            let arrayItems: any[] = [];
                            let dataResult: any = {};
                            dataResult.listErrorInfo = [];
                            dataResult.hasError = false;
                            dataResult.isRegistered = true; 
    
                            _.forEach(res.taskDatas, item => {
                                if (item.key == 'STATUS_REGISTER') {
                                    dataResult.isRegistered = item.valueAsBoolean;
                                } else if (item.key == 'STATUS_ERROR') {
                                    dataResult.hasError = item.valueAsBoolean;
                                } else {
                                    arrayItems.push(item);
                                }
                            });
    
                            if (arrayItems.length > 0) {
                                let listErrorInfo = _.map(arrayItems, obj2 => {
                                    return JSON.parse(obj2.valueAsString);
                                });
                                dataResult.listErrorInfo = listErrorInfo;
                            }
    
                            if (dataResult.hasError == false) {
                                vm.$blockui('hide');
                                nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                                                    vm.$blockui("hide");
                                                    vm.close(true);
                                                });;
                            } else {
                                // reset list data fail
                                vm.data.mappingErrorList = [];
                                vm.errorList = [];
                                let request: any = {};
                                request.errorRegistrationList = [];
                                _.forEach(dataResult.listErrorInfo, errorItem => {
                                    // bind to list mappingErrorList
    
                                    // let empFilter = _.filter(vm.data.listPersonEmp, {'employeeCode': errorItem.employeeCode});
                                    // let empId = empFilter.length > 0 ? empFilter[0].employeeId : '';
    
                                    let error: MappingErrorOutput = {employeeCode: errorItem.employeeCode, employeeName: errorItem.employeename, date: errorItem.date, errorMessage: errorItem.errorMessage};
                                    vm.data.mappingErrorList.push(error);
                                    error.isErrorGrid = true;
                                    vm.errorList.push(error);
                                    vm.$blockui("hide");
    
                                });
    
                                // set error list
                                let errors: any[] = [];
                                let listPersonEmp = vm.sortListEmpInfo(vm.data.listPersonEmp, vm.data.importResult.orderOfEmployees);
    
                                for(let j = 0; j < dataResult.listErrorInfo.length; j++) {
                                    let err: any = { columnKey: 'nameHeader', id: null, index: null, message: dataResult.listErrorInfo[j].errorMessage, isErrorGrid: true };
                                    
                                    if (dataResult.listErrorInfo[j].employeeCode) {
                                        let empFilter = _.filter(listPersonEmp, {'employeeCode': dataResult.listErrorInfo[j].employeeCode});
                                        let empId = empFilter.length > 0 ? empFilter[0].employeeId : '';
                                        err.id = empId;
                                        for (let i = 0; i < listPersonEmp.length; i++) {
                                            if (listPersonEmp[i].employeeCode === dataResult.listErrorInfo[j].employeeCode) {
                                                err.id = listPersonEmp[i].employeeId;
                                                err.index = i;
                                            }
                                        }
                                    }
                                    if (dataResult.listErrorInfo[j].date) {
                                        err.columnKey = dataResult.listErrorInfo[j].date;
                                    }
                                    
                                    if (err.index != null) {
                                        errors.push(err);
                                    }
                                }
    
                                $("#grid").mGrid("setErrors", errors);
    
                                // open KDL053
                                request.employeeIds = _.map(vm.data.listPersonEmp, (item) => item.employeeId);
                                let empList = vm.data.listPersonEmp;
                                for (let i = 0; i < vm.errorList.length; i++) {
                                    let empFilter = _.filter(empList, {'employeeCode': vm.errorList[i].employeeCode});
                                    let empId = empFilter.length > 0 ? empFilter[0].employeeId : '';
    
                                    let item: any = {id: i, sid: empId, scd: vm.errorList[i].employeeCode == null ? '' : vm.errorList[i].employeeCode, 
                                        empName: vm.errorList[i].employeeName == null ? '' : vm.errorList[i].employeeName, 
                                        date: vm.errorList[i].date == null ? '' : vm.errorList[i].date, attendanceItemId: null, errorMessage: vm.errorList[i].errorMessage};
                                    request.errorRegistrationList.push(item);
                                    if (!request.employeeIds.includes(item.sid)) {
                                        request.employeeIds.push(item.sid);
                                    }
                                }
                                request.isRegistered = dataResult.isRegistered;
                                request.dispItemCol = true;
                                if (!vm.isOpenKDL053) {
                                    vm.$window.modeless('at', '/view/kdl/053/a/index.xhtml', request).then(() => {
                                        vm.isOpenKDL053 = false;
                                    });
                                    vm.isOpenKDL053 = true;
                                }
                            }
                        }
                    });
                }).while(infor => {
                    return infor.pending || infor.running;
                }).pause(1000));
        }

        openKDL053() {
            const vm = this;

            let errorListGrid = $('#grid').mGrid('errors');
            let errorList = vm.errorList
                .map(err => {err.date = err.date ? err.date : ''; return err;})
                .map(err => {err.employeeCode = err.employeeCode ? err.employeeCode : ''; return err;})
                .map(err => {err.employeeName = err.employeeName ? err.employeeName : ''; return err;});
            let empList = vm.data.listPersonEmp;
            let empIds: any[] = [];
            let request: any = {};
            request.errorRegistrationList = [];

            // remove error from grid to errorList
            let errorListFilter = errorList.filter(x => x.isErrorGrid);
            errorListFilter.forEach(err => {
                err.employeeId = _.filter(vm.data.listPersonEmp, x => x.employeeCode === err.employeeCode)[0].employeeId;
                if (_.filter(errorListGrid, x => (x.rowId === err.employeeId && x.columnKey === err.date)).length === 0) {
                    errorList = _.filter(errorList, x => !(x.employeeId === err.employeeId && x.date === err.date));
                }
            });

            // add error from grid to errorList
            errorListGrid.forEach(err => {
                if (_.filter(errorListFilter, x => (x.employeeId === err.rowId && x.date === err.columnKey)).length === 0) {
                    let emp = _.filter(vm.data.listPersonEmp, x => x.employeeId === err.rowId)[0];
                    let item = {
                        date: err.columnKey ? err.columnKey : '', 
                        employeeCode: emp.employeeCode, 
                        employeeName: emp.businessName ? emp.businessName : '', 
                        errorMessage: err.message, 
                        isErrorGrid: true
                    }
                    errorList.push(item);
                }
            })

            vm.errorList = errorList;

            // add item from error list to param open KDL053
            for (let i = errorList.length - 1; i >= 0; i--) {
                let empCode = errorList[i].employeeCode;
                let empFilter = _.filter(empList, {'employeeCode': empCode});
                let empId = empFilter.length > 0 ? empFilter[0].employeeId : '';
                empIds.push(empId);
                let empname = empFilter.length > 0 ? empFilter[0].businessName : '';
                let item: any = {id: i, sid: empId, scd: empCode, empName: empname, 
                            date: errorList[i].date, attendanceItemId: null, errorMessage: errorList[i].errorMessage};
                request.errorRegistrationList.push(item);
                if (!empIds.includes(item.sid)) {
                    empIds.push(item.sid);
                }
            }
            request.isRegistered = 0;
            request.dispItemCol = false;
            request.employeeIds = empIds;

            if (!vm.isOpenKDL053) {
                vm.$window.modeless('at', '/view/kdl/053/a/index.xhtml', request).then(() => {
                    vm.isOpenKDL053 = false;
                });
                vm.isOpenKDL053 = true;
            }
        }
        
        closeDialog() {
            const vm = this;
            
            vm.close(false);
        }

        close(flag: boolean) {
            // false: close button
            // true: msg 15
            const vm = this;
            if (vm.windows_lst) {
                let selfId = nts.uk.ui.windows.selfId;
                for (let id in vm.windows_lst) {
                    if (nts.uk.ui.windows.container.windows[id].parent && nts.uk.ui.windows.container.windows[id].parent.id == selfId) {
                        nts.uk.ui.windows.close(id);
                        vm.isOpenKDL053 = false;
                    }
                }
            }

            let paramB = getShared('paramB');
            if (paramB) {
                let startDate = new Date(paramB.startDate);
                let endDate = new Date(paramB.endDate);

                let importableDates = _.map(vm.data.importableDates, date => new Date(date));
                let updateDates = _.filter(importableDates, (date) => {
                    if (date >= startDate && date <= endDate) {
                        return true;
                    }
                    return false;
                });
                if (flag) {
                    if (updateDates.length > 0) {
                        setShared('openA', false);
                        setShared('statusKDL055', 'UPDATE');
                    } else {
                        setShared('openA', false);
                        setShared('statusKDL055', 'CANCEL');
                    }
                } else {
                    setShared('openA', true);
                    setShared('statusKDL055', 'CANCEL');
                }
            } else {
                setShared('openA', false);
                setShared('statusKDL055', 'CANCEL');
            }

            this.$window.close();
        }

        loadGrid() {
            let vm = this;
            console.log('3');

            if (vm.gridOptions.columns.length > 1) {
                let columnFixing = { name: 'ColumnFixing', columnSettings: [{ columnKey: 'nameHeader', isFixed: true }]};
                vm.gridOptions.features.push(columnFixing);
            }

            if ($("#grid").data("mGrid")) $("#grid").mGrid("destroy");
            new nts.uk.ui.mgrid.MGrid($("#grid")[0], {
                width: '1200px',
                height: '600px',
                headerHeight: "45px",
                subHeight: "140px",
                subWidth: "100px",
                dataSource: vm.gridOptions.dataSource,
                columns: vm.gridOptions.columns,
                primaryKey: 'employeeId',
                virtualization: true,
                virtualizationMode: "continuous",
                enter: "right",
                autoFitWindow: false,
                features: vm.gridOptions.features,
                ntsControls: vm.gridOptions.ntsControls
            }).create();
            console.log('4');
        }

        loadError(param: CaptureDataOutput) {
            const vm = this;
            console.log('5');

            let mappingErrorList = param.mappingErrorList;
            vm.errorList = mappingErrorList;
            vm.errorList.map(error => error.isErrorGrid = false);
            let errors: any[] = [];
            let results = vm.data.importResult.results;
            let listPersonEmp = vm.sortListEmpInfo(param.listPersonEmp, param.importResult.orderOfEmployees);
            
            _.forEach(vm.errorList, (error: MappingErrorOutput) => {
                let err: any = { columnKey: 'nameHeader', id: null, index: null, message: error.errorMessage };
                
                if (error.employeeCode) {
                    let empFilter = _.filter(listPersonEmp, {'employeeCode': error.employeeCode});
                    let empId = empFilter.length > 0 ? empFilter[0].employeeId : '';
                    err.id = empId;
                    for (let i = 0; i < listPersonEmp.length; i++) {
                        if (listPersonEmp[i].employeeId === empId) {
                            err.id = listPersonEmp[i].employeeId;
                            err.index = i;
                        }
                    }
                }
                if (error.date) {
                    err.columnKey = error.date;
                }
                _.forEach(results, (result: ImportResultDetail) => {
                    if (result.employeeId === err.id && result.ymd === err.columnKey) {
                        if ([6, 7].includes(result.status) && err.index != null) {
                            errors.push(err);
                            error.isErrorGrid = true;
                        }
                    }
                });
            });

            $("#grid").mGrid("setErrors", errors);

            if (vm.errorList.length > 0) {
                let request: any = {};
                request.employeeIds = _.map(param.listPersonEmp, (item) => item.employeeId);
                request.errorRegistrationList = [];
                for (let i = 0; i < vm.errorList.length; i++) {
                    let empList = vm.data.listPersonEmp;
                    let empFilter = _.filter(empList, {'employeeCode': vm.errorList[i].employeeCode});
                    let empId = empFilter.length > 0 ? empFilter[0].employeeId : '';


                    let item: any = {id: i, sid: empId, scd: vm.errorList[i].employeeCode == null ? '' : vm.errorList[i].employeeCode, 
                        empName: vm.errorList[i].employeeName == null ? '' : vm.errorList[i].employeeName, 
                        date: vm.errorList[i].date == null ? '' : vm.errorList[i].date, attendanceItemId: null, errorMessage: vm.errorList[i].errorMessage};
                    request.errorRegistrationList.push(item);
                    if (!request.employeeIds.includes(item.sid)) {
                        request.employeeIds.push(item.sid);
                    }
                }
                request.isRegistered = 0;
                request.dispItemCol = false;

                if (request.errorRegistrationList.length > 0) {
                    vm.$window.modeless('at', '/view/kdl/053/a/index.xhtml', request).then(() => {
                        vm.isOpenKDL053 = false;
                    });
                }
                vm.isOpenKDL053 = true;
            } else {
                $('#register').focus();
            }
            console.log('6');
        }

        convertToGrid(data: CaptureDataOutput) {
            const vm = this;
            console.log('1');

            let headerStyle = { name: 'HeaderStyles', columns: [{ key: 'nameHeader', colors: ['weekday', 'align-center'] }] };
            let cellStates: any[] = [];

            // columns
            let importableDates: string[] = data.importableDates;

            vm.gridOptions.columns.push({ headerText: vm.$i18n('KDL055_26') , itemId: 'nameHeader', key: 'nameHeader', dataType: 'string', width: '187px', columnCssClass: 'halign-left limited-label', headerCssClass: 'halign-center valign-center', ntsControl: 'Label' });

            _.forEach(importableDates, (dateString: string) => {
                let item = { headerText: vm.convertDateHeader(dateString), itemId: dateString, key: dateString, dataType: 'string', width: '75px', columnCssClass: 'center-align', headerCssClass: 'center-align', constraint: {primitiveValue: 'ShiftMasterImportCode'} };
                if (_.filter(data.holidays, {'date': dateString}).length > 0 || new Date(dateString).getDay() === 0) {
                    headerStyle.columns.push({ key: dateString, colors: ['sunday', 'align-center'] });
                } else if (new Date(dateString).getDay() === 6) {
                    headerStyle.columns.push({ key: dateString, colors: ['saturday', 'align-center'] });
                } else {
                    headerStyle.columns.push({ key: dateString, colors: ['weekday', 'align-center'] });
                }

                vm.gridOptions.columns.push(item);
                // vm.gridOptions.ntsControls.push({name: dateString, constraint: {primitiveValue: 'ShiftMasterImportCode'}});
            });

            // dataSources
            let listPersonEmp = vm.sortListEmpInfo(data.listPersonEmp, data.importResult.orderOfEmployees);
            // let listPersonEmp = _.sortBy(data.listPersonEmp, ['employeeCode', 'businessName']);
            let results = data.importResult.results;

            _.forEach(listPersonEmp, (emp) => {
                let record: any = { employeeId: emp.employeeId, employeeCode: emp.employeeCode, employeeName: emp.businessName, nameHeader: emp.employeeCode + ' ' + emp.businessName };
                cellStates.push({rowId: emp.employeeId, columnKey: 'nameHeader', state: ['limited-label', 'padding-3']});
                _.forEach(results, (result: ImportResultDetail) => {
                    if (result.employeeId === emp.employeeId) {
                        // record[result.ymd] = result.importCode;
                        record[result.ymd] = result.importCode;
                        
                        if ([2, 3, 4, 5, 8].includes(result.status) || (result.status === 9 && !vm.overwrite)) {
                            cellStates.push({ rowId: emp.employeeId, columnKey: result.ymd, state: ["mgrid-disable", "align-center"]});
                        } else {
                            cellStates.push({ rowId: emp.employeeId, columnKey: result.ymd, state: ["align-center"]});
                        }
                    }
                });
                vm.gridOptions.dataSource.push(record);
            });

            _.forEach(listPersonEmp, (emp) => {
                _.forEach(importableDates, (date: string) => {
                    if (_.filter(results, { 'employeeId': emp.employeeId, 'ymd': date}).length == 0) {
                        cellStates.push({ rowId: emp.employeeId, columnKey: date, state: ["mgrid-disable", "align-center"]});
                    }
                });
            })

            // features
            vm.gridOptions.features = [{ name: 'Copy' }, { name: 'Tooltip', error: true }];
            // let columnFixing = { name: 'ColumnFixing', columnSettings: [{ columnKey: 'nameHeader', isFixed: true }]};

            // vm.gridOptions.features.push(columnFixing);
            vm.gridOptions.features.push(headerStyle);
            vm.gridOptions.features.push({ name: 'CellStyles', states: cellStates });
            console.log('2');
        }

        convertDateHeader(text: string): string {
            let date = new Date(text);

            return "<div>" + moment(date).format('M/D') + "<div><div>" + moment(date).format('dd') + "<div>"
        }

        sortListEmpInfo(emps: any[], orderList: any[]) {
            let temp = _.map(orderList, orderItem => {
                if (_.filter(emps, { 'employeeId': orderItem }).length > 0) {
                    return _.filter(emps, { 'employeeId': orderItem })[0];
                }
                return null;
            });

            return _.filter(temp, item => item);
        }
    }

    const paths = {
        getCaptureData: 'wpl/schedule/report/getCaptureData',
        register: 'at/schedule/workschedulestate/register'
    }

    export interface ColumnItem {
        headerText: string;

        itemName: string;

        key: string;

        dataType: string;

        width: string;
    }

    export interface Record {
        id: string;

        employeeCode: string;

        ymd: string;

        importCode: string;

        status: number;
    }

    export interface CapturedRawDataDto {
        /** 取り込み内容 **/
        contents: CapturedRawDataOfCellDto[];

        /** 社員の並び順(OrderdList) **/
        employeeCodes: string[];
    }

    export interface CapturedRawDataOfCellDto {
        /** 社員コード **/
        employeeCode: string;
        /** 年月日 **/
        ymd: string;
        /** 取り込みコード **/
        importCode: string;
    }

    export interface CaptureDataOutput {
        // 社員リスト　：OrderedList<社員ID, 社員コード, ビジネスネーム>
        listPersonEmp: PersonEmpBasicInfoImport[];
        // 年月日リスト：OrderedList<年月日, 曜日>
        importableDates: string[];
        // 祝日リスト　：List<祝日>
        holidays: PublicHoliday[];
        // 取り込み結果
        importResult: ImportResult;
        // エラーリスト：List<取り込みエラーDto>
        mappingErrorList: MappingErrorOutput[];
    }

    export interface PersonEmpBasicInfoImport {
        // 個人ID
        personId: string;

        // 社員ID
        employeeId: string;

        // ビジネスネーム
        businessName: string;

        // 性別
        gender: number;

        // 生年月日
        birthday: string;

        // 社員コード
        employeeCode: string;

        // 入社年月日
        jobEntryDate: string;

        // 退職年月日
        retirementDate: string;
    }

    export interface PublicHoliday {
        companyId: string;

        date: string;

        holidayName: string;
    }

    export interface ImportResult {
        /** 1件分の取り込み結果 **/
        results: ImportResultDetail[];
        /** 取り込み不可日 **/
        unimportableDates: string[];
        /** 存在しない社員 **/
        unexistsEmployees: string[];
        /** 社員の並び順(OrderdList) **/
        orderOfEmployees: string[];
    }

    export interface ImportResultDetail {
        /** 社員ID **/
        employeeId: string;
        /** 年月日 **/
        ymd: string;
        /** 取り込みコード **/
        importCode: string;
        /** 状態 **/
        status: number;
    }

    export interface MappingErrorOutput {
        // 社員コード
        employeeCode: string;
        // 社員名
        employeeName: string;
        // 年月日
        date: string;
        // エラーメッセージ
        errorMessage: string;
    }

    export enum ImportStatus {
        // 未チェック
        UNCHECKED = 0,
        // 取込可能
        IMPORTABLE = 1,
        // 参照範囲外
        OUT_OF_REFERENCE = 2,
        // 個人情報不備
        EMPLOYEEINFO_IS_INVALID = 3,
        // 在職していない
        EMPLOYEE_IS_NOT_ENROLLED = 4,
        // スケジュール管理しない 
        SCHEDULE_IS_NOTUSE = 5,
        // シフトが存在しない 
        SHIFTMASTER_IS_NOTFOUND = 6,
        // シフトが不正
        SHIFTMASTER_IS_ERROR = 7,
        // 確定済み
        SCHEDULE_IS_COMFIRMED = 8,
        // すでに勤務予定が存在する
        SCHEDULE_IS_EXISTS = 9
    }

    export interface CellState {
        rowId: string,

        columnKey: string,

        state: any[]
    }

    export interface ScheduleRegisterTarget {
        employeeId: string,

        date: string,

        importCode: string
    }
}