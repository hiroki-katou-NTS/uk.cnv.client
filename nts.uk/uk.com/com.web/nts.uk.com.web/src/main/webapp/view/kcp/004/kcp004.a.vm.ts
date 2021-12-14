module kcp004.a.viewmodel {
    import UnitModel = kcp.share.tree.UnitModel;
    import TreeComponentOption = kcp.share.tree.TreeComponentOption;
    import StartMode = kcp.share.tree.StartMode;
    import SelectType = kcp.share.tree.SelectionType;
    import UnitAlreadySettingModel = kcp.share.tree.UnitAlreadySettingModel; 
    import SystemType = kcp.share.tree.SystemType;
    
    
    export class ScreenModel {
        
        // Control common
        listTreeType: KnockoutObservableArray<any>;
        selectedTreeType: KnockoutObservable<number>;

        listSelectionType: KnockoutObservableArray<any>;
        selectedSelectionType: KnockoutObservable<number>;

        isMultipleUse: KnockoutObservable<boolean>;
        isMultipleTreeGrid: KnockoutObservable<boolean>;
        isShowAlreadySet: KnockoutObservable<boolean>;
        isDialog: KnockoutObservable<boolean>;
        isShowSelectButton: KnockoutObservable<boolean>;
        enableShowSelectButton: KnockoutObservable<boolean>;
        restrictionOfReferenceRange: KnockoutObservable<boolean>;


        listSystemType: KnockoutObservableArray<any>;
        selectedSystemType: KnockoutObservable<number>;

        // Control component
        selectedWorkplaceId: KnockoutObservable<string>;
        baseDate: KnockoutObservable<Date>;
        multiSelectedWorkplaceId: KnockoutObservable<any>;
        alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel>;
        treeGrid: TreeComponentOption;

        jsonData: KnockoutObservable<string>;
        rowSelected: KnockoutObservable<RowSelection>;
        isBindingTreeGrid: KnockoutObservable<boolean>;
        enable: KnockoutObservable<boolean>;

        listStartMode: KnockoutObservableArray<any>;
        selectedStartMode: KnockoutObservable<StartMode>;

        constructor() {
            let self = this;

            // Control common
            self.isMultipleUse =  ko.observable(false);

            self.listTreeType = ko.observableArray([
                {code : 0, name: 'Single tree select grid'},
                {code : 1, name: 'Multiple tree select grid'}
            ]);
            self.selectedTreeType = ko.observable(1);
            self.isMultipleTreeGrid = ko.computed(function () {
                return self.selectedTreeType() == 1;
            });
            self.isDialog = ko.observable(false);
            self.isShowAlreadySet = ko.observable(true);
            self.restrictionOfReferenceRange = ko.observable(true);
            self.isShowSelectButton = ko.observable(true);
            self.enableShowSelectButton = ko.computed(function() {
                return self.isMultipleTreeGrid();
            });

            // Control component
            self.baseDate = ko.observable(new Date());
            self.selectedWorkplaceId = ko.observable('');
            self.multiSelectedWorkplaceId = ko.observableArray([]);
            self.enable = ko.observable(true);
            self.listSelectionType = ko.observableArray([
                {code : 1, name: 'Select by selected code', enable: self.enable},
                {code : 2, name: 'Select all', enable: self.isMultipleTreeGrid},
                {code : 3, name: 'Select first item', enable: self.enable},
                {code : 4, name: 'No select', enable: self.enable}
            ]);
            self.selectedSelectionType = ko.observable(3);

            self.listStartMode = ko.observableArray([
                {code : StartMode.WORKPLACE, name: 'Workplace'},
                {code : StartMode.DEPARTMENT, name: 'Department'}
            ]);
            self.selectedStartMode = ko.observable(StartMode.WORKPLACE);

            self.listSystemType = ko.observableArray([
                {code : 1, name: '個人情報', enable: self.enable},
                {code : 2, name: '就業', enable: self.enable},
                {code : 3, name: '給与', enable: self.enable},
                {code : 4, name: '人事', enable: self.enable},
                {code : 5, name: '管理者', enable: self.enable}
            ]);
            self.selectedSystemType = ko.observable(5);

            self.alreadySettingList = ko.observableArray([]);
            self.treeGrid = {
                isShowAlreadySet: self.isShowAlreadySet(),
                isMultipleUse: self.isMultipleUse(),
                isMultiSelect: self.isMultipleTreeGrid(),
                startMode: self.selectedStartMode(),
                selectedId: self.getSelectedWorkplaceId(),
                baseDate: self.baseDate,
                selectType: self.selectedSelectionType(),
                isShowSelectButton: self.isShowSelectButton(),
                isDialog: self.isDialog(),
                alreadySettingList: self.alreadySettingList,
                systemType : self.selectedSystemType(),
                restrictionOfReferenceRange: self.restrictionOfReferenceRange()

            };

            self.jsonData = ko.observable('');
            self.rowSelected = ko.observable(new RowSelection('', ''));
            self.isBindingTreeGrid = ko.observable(false);

            // Subscribe
            self.isMultipleUse.subscribe(function(code) {
                self.resetSelectedWorkplace();
                self.reloadTreeGrid().done(() => {
                    self.getSelectedData();
                });
            });
            self.selectedTreeType.subscribe(function(code) {
                // single tree grid
                if (code == 0 && self.selectedSelectionType() == 2) {
                    self.selectedSelectionType(1);
                }
                self.resetSelectedWorkplace();
                self.reloadTreeGrid().done(() => {
                    self.getSelectedData();
                });
            });
            self.selectedStartMode.subscribe(code => {
                self.reloadTreeGrid().done(() => {
                    self.getSelectedData();
                });
            });
            self.isDialog.subscribe(function(value) {
                self.reloadTreeGrid();
            });
            self.isShowAlreadySet.subscribe(function() {
                self.reloadTreeGrid();
            });
            self.restrictionOfReferenceRange.subscribe(() => self.reloadTreeGrid());
//            self.alreadySettingList.subscribe(function() {
//                self.reloadTreeGrid();
//            });
            self.isShowSelectButton.subscribe(function() {
                self.reloadTreeGrid();
            });
            self.selectedSelectionType.subscribe((code) => {
                if (code == 1) {
                       self.selectedWorkplaceId();
                       self.multiSelectedWorkplaceId();
                }

                if (code == 4) {
                    self.resetSelectedWorkplace();
                }

                self.reloadTreeGrid().done(function() {
                    self.getSelectedData();
                });
            });

            self.selectedSystemType.subscribe((code) => {
                // check in case NoSelect button active
                if (self.selectedSelectionType() == 4) {
                    self.resetSelectedWorkplace();
                }
                self.reloadTreeGrid().done(function() {
                    self.getSelectedData();
                });
            });


            self.selectedWorkplaceId.subscribe(() => {
                self.getSelectedData();
            });
            self.multiSelectedWorkplaceId.subscribe(() => {
                self.getSelectedData();
            });
        }

        private copy() {
            // issue #84074
            nts.uk.ui.dialog.info('複写時の処理については現状対象外となります。');
        }

        private register() {
            let self = this;
            if (self.isMultipleTreeGrid()) {
                for (let workplaceId of self.multiSelectedWorkplaceId()) {
                    self.alreadySettingList.push({ workplaceId: workplaceId, isAlreadySetting: true});
                }
            } else {
                self.alreadySettingList.push({ workplaceId: self.selectedWorkplaceId(), isAlreadySetting: true});
            }
        }

        private remove() {
            let self = this;
            let data = $('#tree-grid').getRowSelected();
            let selecetdWorkplaceId = data.map(item => item.workplaceId).join(", ");

            // remove setting by delete element in list.
            self.alreadySettingList(self.alreadySettingList().filter((item) => {
                return selecetdWorkplaceId.indexOf(item.workplaceId) < 0;
            }));

            // remove setting by update alreadySetting = false;
//            self.alreadySettingList().forEach((item) => {
//                if (selecetdWorkplaceId.indexOf(item.workplaceId) > -1) {
//                    item.isAlreadySetting = false;
//                }
//            });
//            self.alreadySettingList.valueHasMutated();
        }

        private getSelectedWorkplaceId() : any {
            let self = this;
            if (self.isMultipleTreeGrid()) {
                return self.multiSelectedWorkplaceId;
            }
            return self.selectedWorkplaceId;
        }

        public getSelectedData() {
            let self = this;
            if (!self.isBindingTreeGrid()) {
                return;
            }
            let data = $('#tree-grid').getRowSelected();
            self.rowSelected().workplaceId(data.length > 0 ? data.map(item => item.id).join(", ") : '');
            self.rowSelected().workplaceCode(data.length > 0 ? data.map(item => item.code).join(", ") : '');
        }

        private setTreeData() {
            let self = this;
            self.treeGrid = {
                isShowAlreadySet: self.isShowAlreadySet(),
                isMultipleUse: self.isMultipleUse(),
                isMultiSelect: self.isMultipleTreeGrid(),
                startMode: self.selectedStartMode(),
                selectedId: self.getSelectedWorkplaceId(),
                baseDate: self.baseDate,
                selectType: self.selectedSelectionType(),
                isShowSelectButton: self.isShowSelectButton(),
                isDialog: self.isDialog(),
                alreadySettingList: self.alreadySettingList,
                systemType: self.selectedSystemType(),
                restrictionOfReferenceRange: self.restrictionOfReferenceRange()
            };
        }
        
        private reloadTreeGrid() : JQueryPromise<void>  {
            let self = this;
            let dfd = $.Deferred<void>();
            self.setTreeData();
            $('#tree-grid').ntsTreeComponent(self.treeGrid).done(() => {
                $('#tree-grid').focusTreeGridComponent();
                dfd.resolve();
            });
            return dfd.promise();
        }
        
        private resetSelectedWorkplace() {
            let self = this;
            self.selectedWorkplaceId('');
            self.multiSelectedWorkplaceId([]);
        }
    }
    
    /**
     * Class Row Selection
     */
    export class RowSelection {
        workplaceId: KnockoutObservable<string>;
        workplaceCode: KnockoutObservable<string>;
        
        constructor(workplaceId: string, workplaceCode: string) {
            let self = this;
            self.workplaceId = ko.observable(workplaceId);
            self.workplaceCode = ko.observable(workplaceCode);
        }
    }
}   