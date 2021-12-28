module nts.uk.com.view.cdl023.a.viewmodel {
    import getText = nts.uk.resource.getText;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import alertError = nts.uk.ui.dialog.alertError;

    export class ScreenModel {

        isFirtTime: KnockoutObservable<boolean>;

        code: KnockoutObservable<string>;
        name: KnockoutObservable<string>;
        totalSelectionDisp: KnockoutObservable<string>;
        isOverride: KnockoutObservable<boolean>;

        lstSelected: KnockoutObservableArray<string>;
        lstSelectedClone: any = [];

        targetType: number;
        paramMsg1540: string;
        itemListSetting: Array<string>;
        baseDate: Date;

        roleType: number;

        workFrameNoSelection: number;

        constructor() {
            let self = this;

            self.isFirtTime = ko.observable(true);

            self.code = ko.observable(null);
            self.name = ko.observable(null);

            self.lstSelected = ko.observableArray([]);

            // display number of destinations
            self.totalSelectionDisp = ko.computed(() => {
                return getText("CDL023_5", [self.lstSelected().length]);
            });
            self.isOverride = ko.observable(false);

            self.baseDate = null;
        }

        /**
         * startPage
         */
        public startPage(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();

            // get data shared
            let object: any = getShared("CDL023Input");
            self.code(object.code);
            self.name(object.name);
            self.targetType = object.targetType;
            self.itemListSetting = object.itemListSetting;
            self.baseDate = object.baseDate;
            self.roleType = object.roleType;
            self.workFrameNoSelection = !!object.workFrameNoSelection ? object.workFrameNoSelection : null; //ver6
            
            dfd.resolve();
            return dfd.promise();
        }

        /**
         * closeDialog
         */
        public closeDialog() {
            // respond data
            setShared("CDL023Cancel", true);

            // close dialog
            nts.uk.ui.windows.close();
        }

        /**
         * execution
         */
        public execution() {
            let self = this;

            // check empty selection
            if (self.lstSelected().length <= 0) {
                alertError({ messageId: "Msg_646" });
                return;
            }

            if (self.getSelectedItems().length <= 0) {
                alertError({ messageId: "Msg_1540", messageParams: [self.paramMsg1540] });
                self.lstSelected(self.lstSelectedClone);
                return;
            }

            // respond data
            setShared("CDL023Output", self.getSelectedItems());

            // close dialog
            nts.uk.ui.windows.close();
        }

        /**
         * getSelectedItems
         */
        private getSelectedItems(): Array<string> {
            let self = this;
            self.lstSelectedClone = _.clone(self.lstSelected());
            // if is override, return list selected
            if (self.isOverride()) {
                return self.lstSelected();
            }

            return self.lstSelected().filter(i => self.itemListSetting.indexOf(i) < 0);
        }

        /**
         * openDialog
         */
        public openDialog() {
            let self = this;

            let listToDialog = self.isFirtTime() ? self.itemListSetting : self.lstSelected();

            let screenUrl: string = null;

            // set parameters
            let shareData: any = {};
            shareData.isMultiple = true;

            // define key share
            let keyInput: string = null;
            let keyOutput: string = null;
            let keyCancel: string = null;

            switch (self.targetType) {
                case TargetType.EMPLOYMENT:
                    screenUrl = '/view/cdl/002/a/index.xhtml';
                    keyInput = 'CDL002Params';
                    keyOutput = 'CDL002Output';
                    keyCancel = 'CDL002Cancel';

                    // set parameter
                    self.paramMsg1540 = getText("Com_Employment");
                    shareData.showNoSelection = false;
                    shareData.selectedCodes = self.lstSelected();
                    break;
                case TargetType.CLASSIFICATION:
                    screenUrl = '/view/cdl/003/a/index.xhtml';
                    keyInput = 'inputCDL003';
                    keyOutput = 'outputCDL003';
                    keyCancel = 'CDL003Cancel';

                    // set parameter
                    self.paramMsg1540 = getText("Com_Class");
                    shareData.showNoSelection = false;
                    shareData.selectedCodes = self.lstSelected();
                    break;
                case TargetType.JOB_TITLE:
                    screenUrl = '/view/cdl/004/a/index.xhtml';
                    keyInput = 'inputCDL004';
                    keyOutput = 'outputCDL004';
                    keyCancel = 'CDL004Cancel';

                    // set data share
                    self.paramMsg1540 = getText("Com_Jobtitle");
                    shareData.baseDate = self.baseDate;
                    shareData.showNoSelection = false;
                    shareData.selectedCodes = self.lstSelected();
                    break;
                case TargetType.WORKPLACE:
                    screenUrl = '/view/cdl/008/a/index.xhtml';
                    keyInput = 'inputCDL008';
                    keyOutput = 'outputCDL008';
                    keyCancel = 'CDL008Cancel';

                    // set data share
                    self.paramMsg1540 = getText("Com_Workplace");
                    shareData.selectedSystemType = 2;
                    shareData.baseDate = self.baseDate;
                    shareData.selectedCodes = self.lstSelected();
                    break;
                case TargetType.DEPARTMENT:
                    self.paramMsg1540 = getText("Com_Department");
                    screenUrl = '/view/cdl/007/a/index.xhtml';
                    nts.uk.ui.dialog.alert("Not cover.");
                    return;
                //                    break;
                case TargetType.WORKPLACE_PERSONAL:
                    screenUrl = '/view/cdl/009/a/index.xhtml';
                    keyInput = 'CDL009Params';
                    keyOutput = 'CDL009Output';
                    keyCancel = 'CDL009Cancel';

                    // set data share
                    self.paramMsg1540 = getText("Com_Person");
                    shareData.target = TargetPersonalType.WORKPLACE;
                    shareData.baseDate = self.baseDate;
                    shareData.selectedIds = self.lstSelected();
                    break;
                case TargetType.DEPARTMENT_PERSONAL:
                    screenUrl = '/view/cdl/009/a/index.xhtml';
                    keyInput = 'CDL009Params';
                    keyOutput = 'CDL009Output';
                    keyCancel = 'CDL009Cancel';

                    // set data share
                    self.paramMsg1540 = getText("Com_Person");
                    shareData.target = TargetPersonalType.DEPARTMENT;
                    shareData.baseDate = self.baseDate;
                    shareData.selectedIds = self.lstSelected();
                    break;
                case TargetType.ROLE:
                    screenUrl = '/view/cdl/025/index.xhtml';
                    keyInput = 'paramCdl025';
                    keyOutput = 'dataCdl025';
                    keyCancel = 'CDL009Cancel';

                    // set data share
                    self.paramMsg1540 = getText("CDL023_9");
                    shareData.roleType = self.roleType;
                    shareData.multiple = true;
                    shareData.currentCode = self.lstSelected();
                    break;

                case TargetType.WORK_TYPE:
                    screenUrl = '/view/cdl/024/index.xhtml';
                    keyInput = 'CDL024';
                    keyOutput = 'currentCodeList';
                    keyCancel = 'CDL009Cancel';

                    // set data share
                    self.paramMsg1540 = '勤務種別';
                    shareData.codeList = listToDialog;
                    break;

                case TargetType.WORK:
                    screenUrl = '/view/kdl/012/index.xhtml';
                    keyInput = 'KDL012Params';
                    keyOutput = 'KDL012Output';
                    keyCancel = 'KDL012Cancel';
                    self.paramMsg1540 = getText("Com_Work");
                    // set data share
                    shareData.isMultiple = true; //選択モード single or multiple
                    shareData.showExpireDate = true; //表示モード	show/hide expire date
                    shareData.referenceDate = self.baseDate; //システム日付        
                    shareData.workFrameNoSelection = self.workFrameNoSelection;//作業枠NO選択        
                    shareData.selectionCodeList = self.lstSelected(); //初期選択コードリスト

                    break;
                default:
                    nts.uk.ui.dialog.alert("Target type not found.");
                    return;
            }

            // share data
            setShared(keyInput, shareData);

            let atOrcom = !!shareData.workFrameNoSelection ? 'at' : 'com';
							
            // open dialog
            nts.uk.ui.windows.sub.modal(atOrcom, screenUrl).onClosed(() => {

                // check close dialog
                if (getShared(keyCancel)) {
                    return;
                }

                // get value respond
                let selectedCode: any = getShared(keyOutput);
                if (!selectedCode || selectedCode.length <= 0) {
                    self.lstSelected([]);
                    return;
                }

                // set list selection
                if (Array.isArray(selectedCode)) {
                    self.lstSelected(selectedCode);
                } else {
                    self.lstSelected([selectedCode]);
                }

                // update flag
                self.isFirtTime(false);
            });
        }
    }

    /**
     * TargetType
     */
    class TargetType {

        // 雇用
        static EMPLOYMENT = 1;

        // 分類
        static CLASSIFICATION = 2;

        // 職位
        static JOB_TITLE = 3;

        // 職場
        static WORKPLACE = 4;

        // 部門
        static DEPARTMENT = 5;

        // 職場個人
        static WORKPLACE_PERSONAL = 6;

        // 部門個人
        static DEPARTMENT_PERSONAL = 7;

        // ロール
        static ROLE = 8;

        // 勤務種別
        static WORK_TYPE = 9;
        //作業
        static  WORK = 10; //ver 6
    }

    /**
     * TargetPersonalType
     */
    class TargetPersonalType {

        // 職場個人
        static WORKPLACE = 1;

        //  部門個人
        static DEPARTMENT = 2;
    }
}