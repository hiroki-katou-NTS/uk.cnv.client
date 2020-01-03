module nts.uk.com.view.cmm018.a.sub {
    import getText = nts.uk.resource.getText;
    import servicebase = cmm018.shr.servicebase;
    import vmbase = cmm018.shr.vmbase;
    import getShared = nts.uk.ui.windows.getShared;
    import setShared = nts.uk.ui.windows.setShared;
    import modal = nts.uk.ui.windows.sub.modal;
    import block = nts.uk.ui.block;
    import dialog = nts.uk.ui.dialog;
    //=========Mode A: まとめて登録モード==============
    export module viewmodelSubA {
        export class ScreenModel{
            items: KnockoutObservableArray<any> = ko.observableArray([]);
            lstData: KnockoutObservableArray<vmbase.CompanyAppRootADto> = ko.observableArray([]);
            constructor() {
            }
            reloadGridN(lstRoot: Array<vmbase.CompanyAppRootADto>, rootType: vmbase.RootType, mode: vmbase.MODE){
                let self = this;
                let gridName = '#grid_matome';
                if(rootType == vmbase.RootType.COMPANY){
                    gridName = mode == vmbase.MODE.MATOME ? '#grid_matome' : '#grid_matomeB';
                }else if(rootType == vmbase.RootType.WORKPLACE){
                    gridName = mode == vmbase.MODE.MATOME ? '#grid_matomeC' : '#grid_matomeD';
                }else{//PERSON
                    gridName = mode == vmbase.MODE.MATOME ? '#grid_matomeE' : '#grid_matomeF';
                }
                if($(gridName + '_container').length > 0){
                    $(gridName).ntsGrid("destroy");
                }
                self.items([]);
                self.lstData(lstRoot);
                _.each(lstRoot, function(root){
                    self.items.push(self.convertlistRoot(root));
                });
                let colorBackGr: any = self.fillColorbackGr();
              $(gridName).ntsGrid({
                width: 950,
                height: 530,
                dataSource: self.items(),
                primaryKey: 'typeRoot',
                rowVirtualization: true,
                virtualization: true,
                hidePrimaryKey: true,
                virtualizationMode: 'continuous',
                columns: [
                    { headerText: getText('CMM018_24'), key: 'appName', dataType: 'string', width: '100px'},
                    { headerText: getText('CMM018_28'), 
                        group:[{ headerText: getText('CMM018_30'), key: 'phase1', dataType: 'string', width: '100px' },
                               { headerText: getText('CMM018_31'), key: 'phase2', dataType: 'string', width: '100px' },
                               { headerText: getText('CMM018_32'), key: 'phase3', dataType: 'string', width: '100px' },
                               { headerText: getText('CMM018_33'), key: 'phase4', dataType: 'string', width: '100px' },
                               { headerText: getText('CMM018_34'), key: 'phase5', dataType: 'string', width: '100px' }]
                    },
                    { headerText: getText('CMM018_95'), key: 'deleteRoot', dataType: 'string', width: '75px'},
                    { headerText: 'ID', key: 'typeRoot', dataType: 'string', width: '0px', hidden: true }
                ],
                features: [
                    { name: "MultiColumnHeaders" },
                    { name: 'ColumnFixing', fixingDirection: 'left',
                                            showFixButtons: false,
                                            columnSettings: [{ columnKey: 'appName', isFixed: true } ]},
                ],
                 ntsFeatures:[
                    {
                        name: 'CellState',
                        rowId: 'rowId',
                        columnKey: 'columnKey',
                        state: 'state',
                        states: colorBackGr
                    },
                 ],
                ntsControls: [{ name: 'Button', controlType: 'Button', enable: true }],
            });
            $(gridName).on("click", ".button-delete", function(evt, ui) {
                let _this = $(this);
                let id = _this.parents('tr').data('id');
                let empRType = id.split("_")[0];
                let appType = id.split("_")[1];
                self.deleteRowSub(empRType, appType);
            });
            //Phase1
            $(gridName).on("click", ".openK_Phase1", function(evt, ui) {
                let _this = $(this);
                let id = _this.parents('tr').data('id');
                let empRType = id.split("_")[0];
                let appType = id.split("_")[1];
                self.openDialogKSub(1, empRType, appType);
            });
            $(gridName).on("click", ".openK_Phase2", function(evt, ui) {
                let _this = $(this);
                let id = _this.parents('tr').data('id');
                let empRType = id.split("_")[0];
                let appType = id.split("_")[1];
                self.openDialogKSub(2, empRType, appType);
            });
            $(gridName).on("click", ".openK_Phase3", function(evt, ui) {
                let _this = $(this);
                let id = _this.parents('tr').data('id');
                let empRType = id.split("_")[0];
                let appType = id.split("_")[1];
                self.openDialogKSub(3, empRType, appType);
            });
            $(gridName).on("click", ".openK_Phase4", function(evt, ui) {
                    console.log('Phase4');
                let _this = $(this);
                let id = _this.parents('tr').data('id');
                let empRType = id.split("_")[0];
                let appType = id.split("_")[1];
                self.openDialogKSub(4, empRType, appType);
            });
            $(gridName).on("click", ".openK_Phase5", function(evt, ui) {
                let _this = $(this);
                let id = _this.parents('tr').data('id');
                let empRType = id.split("_")[0];
                let appType = id.split("_")[1];
                self.openDialogKSub(5, empRType, appType);
            });
        }
        resize1(){
            let self = this;
            let a = __viewContext.viewModel.viewmodelSubA.items();
            let width = self.cal(a[0].phase1);
            $("#grid_matome").igGridResizing("resize", 'phase1', width);

        }
        resize2(){
            let self = this;
            let a = __viewContext.viewModel.viewmodelSubA.items();
            let width = self.cal(a[0].phase2);
            $("#grid_matome").igGridResizing("resize", 'phase2', width);

        }
        resize3(){
            let self = this;
            let a = __viewContext.viewModel.viewmodelSubA.items();
            let width = self.cal(a[0].phase3);
            $("#grid_matome").igGridResizing("resize", 'phase3', width);

        }
        cal1(){
            let self = this;
            let a = __viewContext.viewModel.viewmodelSubA.items();
            let width = self.cal(a[0].phase1);
            alert(width);
        }
        cal(inputText) {
            let font = "14px DroidSansMono, Meiryo"; 
            let canvas = document.createElement("canvas"); 
            let context = canvas.getContext("2d"); 
            context.font = font; 
            let width = context.measureText(inputText).width; 
            let textPixel = Math.ceil(width); 
            let halfPixel = nts.uk.text.countHalf(inputText)* 8
            console.log(inputText);
            console.log(textPixel);
            console.log(halfPixel);
            console.log((textPixel + halfPixel)/2);
            return (textPixel + halfPixel)/2 + 5; 
        }  
        convertlistRoot(root: vmbase.CompanyAppRootADto): vmbase.Root{
            let self = this;
            let typeRoot: string = root.employRootAtr +'_'+ root.appTypeValue;
            let appName:string = self.appNameHtml(root.appTypeName, root.employRootAtr);
            let phase1:string = self.phaseHtml(root.appPhase1, root.appTypeValue, root.employRootAtr,1);
            let phase2:string = self.phaseHtml(root.appPhase2, root.appTypeValue, root.employRootAtr,2);
            let phase3:string = self.phaseHtml(root.appPhase3, root.appTypeValue, root.employRootAtr,3);
            let phase4:string = self.phaseHtml(root.appPhase4, root.appTypeValue, root.employRootAtr,4);
            let phase5:string = self.phaseHtml(root.appPhase5, root.appTypeValue, root.employRootAtr,5);
            let deleteRoot: string = '<div class="td-delete"><button class="button-delete"></button></div>'; 
            return {typeRoot: typeRoot,
                            appName:appName,
                            phase1:phase1,
                            phase2:phase2,
                            phase3:phase3,
                            phase4:phase4,
                            phase5:phase5,
                            deleteRoot: deleteRoot};
        }
         
        phaseHtml(phase: vmbase.ApprovalPhaseDto, appTypeValue, employRootAtr, phaseOrder: number):any{
            let classPhase = 'openK_Phase' + phaseOrder;
            if(_.isEmpty(phase.approver) || phase.approver.length == 0){//phase chua setting
                return '<div class="hyperlink approver-line ' + classPhase + '">' + getText('CMM018_99') + '</div>';
            }
            let result = '<div class="approver">';
            _.each(phase.approver, function(approver){
                result += '<div class="hyperlink approver-line ' + classPhase + '">' + approver.name + approver.confirmName +'</div>';
            });
                result += '</div>'+ '<div class="from">' + '（' + phase.appFormName + '）' + '</div>';
           return result;
            
        }
        openDialogKSub(phaseOrder: number, empRType: any, appType: any){
            let self = this;
            let root: vmbase.CompanyAppRootADto = self.findRoot(empRType, appType);
            let phase = null;
            if(_.isEmpty(root)) return;
            if(phaseOrder == 1) phase = root.appPhase1;
            if(phaseOrder == 2) phase = root.appPhase2;
            if(phaseOrder == 3) phase = root.appPhase3;
            if(phaseOrder == 4) phase = root.appPhase4;
            if(phaseOrder == 5) phase = root.appPhase5;
            __viewContext.viewModel.viewmodelA.openDialogK(phase, root.approvalId, appType, empRType, phaseOrder);
        }
        findRoot(empRType: any, appType: any): vmbase.CompanyAppRootADto{
            let self = this;
            if(appType == 'null' && Number(empRType) == 0){//common
                return _.find(self.lstData(), function(root){
                    return root.employRootAtr == 0 && (root.appTypeValue == null || root.appTypeValue == 'null');
                });
            }
            return _.find(self.lstData(), function(root){
                return root.employRootAtr == Number(empRType) && root.appTypeValue == Number(appType);
            });
        }
        fillColorbackGr(): Array<vmbase.CellState>{
            let self = this;
            let result = [];
            _.each(__viewContext.viewModel.viewmodelA.cpA(), function(root) {
                let rowId = root.employRootAtr +'_'+ root.appTypeValue;
                result.push(new vmbase.CellState(rowId,'appName',['appNameBgColor']));
                result.push(new vmbase.CellState(rowId,'deleteRoot',['phaseBgColorNot']));
                if(!_.isEmpty(root.appPhase1.approver) && root.appPhase1.approver.length > 0){
                    result.push(new vmbase.CellState(rowId,'phase1',['phaseBgColor']));
                }else{
                    result.push(new vmbase.CellState(rowId,'phase1',['phaseBgColorNot']));
                }
                if(!_.isEmpty(root.appPhase2.approver) && root.appPhase2.approver.length > 0){
                    result.push(new vmbase.CellState(rowId,'phase2',['phaseBgColor']));
                }else{
                    result.push(new vmbase.CellState(rowId,'phase2',['phaseBgColorNot']));
                }
                if(!_.isEmpty(root.appPhase3.approver) && root.appPhase3.approver.length > 0){
                    result.push(new vmbase.CellState(rowId,'phase3',['phaseBgColor']));
                }else{
                    result.push(new vmbase.CellState(rowId,'phase3',['phaseBgColorNot']));
                }
                if(!_.isEmpty(root.appPhase4.approver) && root.appPhase4.approver.length > 0){
                    result.push(new vmbase.CellState(rowId,'phase4',['phaseBgColor']));
                }else{
                    result.push(new vmbase.CellState(rowId,'phase4',['phaseBgColorNot']));
                }
                if(!_.isEmpty(root.appPhase5.approver) && root.appPhase5.approver.length > 0){
                    result.push(new vmbase.CellState(rowId,'phase5',['phaseBgColor']));
                }else{
                    result.push(new vmbase.CellState(rowId,'phase5',['phaseBgColorNot']));
                }
            });
            return result;
        }
        
            appNameHtml(appName: string, empRType: number){
                if(empRType != 0) return appName;
                return appName + '<div style="display: inline-block;">'
                    + '<button data-bind="ntsHelpButton: ' + "{image: '../images/CMM018_001.png', position: 'right top',enable: true}" 
                    + '" >?</button></div>';
            }
            deleteRowSub(empRType: any, appType: any){
                let self = this;
                let root: vmbase.CompanyAppRootADto = self.findRoot(empRType, appType);
                if(_.isEmpty(root)) return;
                __viewContext.viewModel.viewmodelA.deleteRow(root.approvalId, empRType);
            }
        }
    }
}