module nts.uk.at.view.kaf006.shr.viewmodel {
    import modal = nts.uk.ui.windows.sub.modal;
    import getShared = nts.uk.ui.windows.getShared;
    import setShared = nts.uk.ui.windows.setShared;

    export class Kaf006ShrViewModel extends ko.ViewModel {
        create() {

        }

        mounted() {

        }

        public static openDialogKDL035(params: any, vm: any) {
            console.log("Open KDL035");

            let linkingDates: any[] = [];
            setShared("KDL035_PARAMS", params);
            modal("/view/kdl/035/a/index.xhtml").onClosed(() => {
                // get List<振休振出紐付け管理> from KDL035
                linkingDates = getShared('KDL035_RESULT');
                if (linkingDates) {
                    vm.payoutSubofHDManagements(linkingDates);
                } 
                // else {
                //     vm.payoutSubofHDManagements([]);
                // }
            });

        }

        public static openDialogKDL036(params: any, vm: any) {
            console.log("Open KDL036");

            let listParam: any[] = [];
            setShared("KDL036_PARAMS", params)
            modal("/view/kdl/036/a/index.xhtml").onClosed(() => {
            listParam = getShared("KDL036_RESULT");
            if (listParam) {
                vm.leaveComDayOffManas(listParam);
            } 
            // else {
            //     vm.leaveComDayOffManas([]);
            // }
          });

        }

        public static openKDL005(param: any) {
            setShared("KDL005_DATA", param.employeeIds);

			if (param.employeeIds.length > 1) {
                nts.uk.ui.windows.sub.modal("/view/kdl/005/a/index.xhtml", {  width: 1160, height: 640 });
            } else {
                nts.uk.ui.windows.sub.modal("/view/kdl/005/a/index.xhtml",{  width: 860, height: 640 });
            }
        }

        public static openKDL020(param: any) {
			setShared('KDL020_DATA', param.employeeIds);
			if (param.employeeIds.length > 1)
				nts.uk.ui.windows.sub.modal("/view/kdl/020/a/index.xhtml",{  width: 1040, height: 660 });
			else
				nts.uk.ui.windows.sub.modal("/view/kdl/020/a/index.xhtml",{  width: 730, height: 660 });
        }

        public static openKDL051(param: any) {
            setShared('KDL051A_PARAM', param.employeeIds);
            if(param.employeeIds.length > 1 ) {
                nts.uk.ui.windows.sub.modal("/view/kdl/051/a/index.xhtml",{width: 980, height: 570});
            } else {
                nts.uk.ui.windows.sub.modal("/view/kdl/051/a/index.xhtml",{width: 650, height: 530});
            }
        }

        public static openKDL052(param: any) {
            const vm = new ko.ViewModel();
            setShared('KDL052A_PARAM', param);            
            vm.$window.modal('/view/kdl/052/a/index.xhtml');
        }

        public static openKDL017(param: any) {
            setShared('KDL017_PARAM', param);
            if(param.employeeIds.length > 1) {
                modal("/view/kdl/017/a/multiple.xhtml");
            } else {
                modal("/view/kdl/017/a/single.xhtml");
            }
        }

        public static openKDL009(param: any) {
            setShared('KDL009_DATA', param.employeeIds);
            if(param.employeeIds.length > 1) {
                nts.uk.ui.windows.sub.modal("/view/kdl/009/a/index.xhtml",{width: 1100, height: 650});
            } else {
                nts.uk.ui.windows.sub.modal("/view/kdl/009/a/index.xhtml",{width: 770, height: 650});
            }  
        }

        public static openKDL029(param: any) {
			setShared('KDL029_DATA', param.employeeIds);
			if (param.employeeIds.length > 1)
				modal("/view/kdl/029/a/index.xhtml",{  width: 1060, height: 600 });
			else
				modal("/view/kdl/029/a/index.xhtml",{  width: 710, height: 600 });
        }
    }

    export class WorkType {
        workTypeCode: string;
        name: string;
        
        constructor(iWorkType: IWorkType) {
            this.workTypeCode = iWorkType.workTypeCode;
            this.name = iWorkType.name;
        }
    }

    export interface IWorkType {
        workTypeCode: string;
        name: string;
    }

    export class ApplicationDto {
        version: number;
        appId: string;
        prePostAtr: number;
        employeeID: string;
        appType: number;
        appDate: string;
        enteredPerson: string;
        inputDate: string;
        reflectionStatus;
        opStampRequestMode: number;
        opReversionReason: string;
        opAppStartDate: string;
        opAppEndDate: string;
        opAppReason: string;
        opAppStandardReasonCD: number;

        constructor(version, appId, prePostAtr, employeeID, appType, appDate, enteredPerson,
            inputDate, reflectionStatus, opStampRequestMode, opReversionReason, opAppStartDate,
            opAppEndDate, opAppReason, opAppStandardReasonCD) {
                this.version = version;
                this.appId = appId;
                this.prePostAtr = prePostAtr;
                this.employeeID = employeeID;
                this.appType = appType;
                this.appDate = appDate;
                this.enteredPerson = enteredPerson;
                this.inputDate = inputDate;
                this.reflectionStatus = reflectionStatus;
                this.opStampRequestMode = opStampRequestMode;
                this.opReversionReason = opReversionReason;
                this.opAppStartDate = opAppStartDate;
                this.opAppEndDate = opAppEndDate;
                this.opAppReason = opAppReason;
                this.opAppStandardReasonCD = opAppStandardReasonCD
        }
    }
}