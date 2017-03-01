module qrm001.a.viewmodel {
    export class ScreenModel {
        employeeInfo: KnockoutObservable<EmployeeInfo>;
        employeeList: KnockoutObservableArray<EmployeeInfo>;
        currentEmployee: KnockoutObservable<EmployeeInfo>;
        currentEmployeeName: KnockoutObservable<any>;
        currentEmployeeCode: KnockoutObservable<any>;
        previous: KnockoutObservable<any>;
        next: KnockoutObservable<any>;
        date: KnockoutObservable<any>;
        payDateList: KnockoutObservableArray<RetirementPaymentInfo>;
        retirementPaymentInfo: KnockoutObservable<RetirementPaymentInfo>;
        selectList10: KnockoutObservableArray<any>;
        fullPaytransfer: KnockoutObservableArray<BankDataSet>;
        currentPaytransfer: KnockoutObservableArray<BankDataSet>;
        fullSetSelect: KnockoutObservableArray<any>;
        currentSetSelect: KnockoutObservableArray<any>;
        isUpdate: any;
        value: KnockoutObservable<any>;
        constructor() {
            var self = this;
             self.employeeList = ko.observableArray([
                new EmployeeInfo('0001','A'),
                new EmployeeInfo('0002','B'),
                new EmployeeInfo('0003','C')
            ]);
            self.value = ko.observable(1);
            self.value.subscribe(function(value){
                console.log(value);
            });
            self.payDateList = ko.observableArray([]);
            self.isUpdate = false;
            self.date = ko.observable("2016/12/01");
            self.currentEmployeeCode = ko.observable(0);
            self.currentEmployeeName = ko.observable('0001');
            self.currentEmployee = ko.observable(self.employeeList()[self.currentEmployeeCode()]);
            self.previous = ko.observable(false);
            self.next = ko.observable(true);
            self.currentEmployeeCode.subscribe(function(value){
                self.previous((value === 0)?false:true);
                self.next((value === (self.employeeList().length-1))?false:true);
                self.currentEmployee(self.employeeList()[value]);
                self.currentEmployeeName(self.currentEmployee().personId());}
            );
            self.retirementPaymentInfo = ko.observable(
                new RetirementPaymentInfo('0001','2','3','4',5,'6','7','8','9','0','1',2 ,0,'4','5','6',7,8,'9','1')
            );
            self.fullSetSelect = ko.observableArray([
                { name: '使用しない' },
                { name: '支給1' },
                { name: '支給2' },
                { name: '支給3' },
                { name: '支給4' },
                { name: '支給5' }]);
            self.fullPaytransfer = ko.observableArray([
                new BankDataSet('支給1', 'bank1', 'branch1', '0001', '1'),
                new BankDataSet('使用しない', 'bank2', 'branch2', '0002', '2'),
                new BankDataSet('使用しない', 'bank3', 'branch3', '0003', '3'),
                new BankDataSet('使用しない', 'bank4', 'branch4', '0004', '4'),
                new BankDataSet('使用しない', 'bank5', 'branch5', '0005', '5')
            ]);
            self.currentPaytransfer = ko.observableArray([
                new BankDataSet('支給1', 'bank1', 'branch1', '0001', '1'),
                new BankDataSet('使用しない', 'bank2', 'branch2', '0002', '2'),
                new BankDataSet('使用しない', 'bank3', 'branch3', '0003', '3'),
                new BankDataSet('使用しない', 'bank4', 'branch4', '0004', '4'),
                new BankDataSet('使用しない', 'bank5', 'branch5', '0005', '5')
            ]);
            self.currentPaytransfer.subscribe(function(value){console.log(value);});
            self.selectList10 = ko.observableArray(self.refreshSelect(self.fullSetSelect, self.currentPaytransfer, 5));
            self.currentSetSelect = ko.observableArray(_.filter(self.fullSetSelect(), function(o) { 
                return o.name !== self.currentPaytransfer()[0].dataSetName() && 
                       o.name !== self.currentPaytransfer()[1].dataSetName() &&
                       o.name !== self.currentPaytransfer()[2].dataSetName() &&  
                       o.name !== self.currentPaytransfer()[3].dataSetName() ;
            }));
            self.currentPaytransfer().forEach(function(item){
                item.dataSetName.subscribe(function(value){
                    if((item.dataSetName()=='使用しない')){
                        self.setDataRow(item, self.fullPaytransfer, -1);
                        self.selectList10(self.refreshSelect(self.fullSetSelect, self.currentPaytransfer, 5));
                    } else {
                        let index = parseInt(item.dataSetName().substring(2, 3))-1;
                        self.setDataRow(item, self.fullPaytransfer, index);
                            self.selectList10(self.refreshSelect(self.fullSetSelect, self.currentPaytransfer, 5));
                    }
                });
                item.transferMoney.subscribe(function(value){
                    if(!(item.dataSetName()=='使用しない')){
                    self.fullPaytransfer()[parseInt(item.dataSetName().substring(2, 3))-1].transferMoney(value);}
                });
            });
            self.retirementPaymentInfo.subscribe(function(){
                if(!self.retirementPaymentInfo().taxCalculationMethod()){
                    self.autoCaculator();
                    $(".caculator").css('background-color', '#ffc000');
                } else $(".caculator").css('background-color', '#cee6ff');
            });
            self.retirementPaymentInfo().totalPaymentMoney.subscribe(function(value){
                console.log(value);    
            });
        }
        startPage(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
            let command = { personId: "1", dateTime: self.date() };
            qrm001.a.service.getRetirementPaymentList('A0001').done(function(data) {
                if(!data){ self.isUpdate = false; }
                else { 
                    self.payDateList.removeAll();
                    data.forEach(function(item) {
                        self.payDateList.push(new RetirementPaymentInfo(
                            item.companyCode,
                            item.personId,
                            item.payDate,
                            item.trialPeriodSet,
                            item.exclusionYears,
                            item.additionalBoardYears,
                            item.boardYears,
                            item.totalPaymentMoney,
                            item.deduction1Money,
                            item.deduction2Money,
                            item.deduction3Money,
                            item.retirementPayOption,
                            item.taxCalculationMethod,
                            item.incomeTaxMoney,
                            item.cityTaxMoney,
                            item.prefectureTaxMoney,
                            item.totalDeclarationMoney,
                            item.actualRecieveMoney,
                            item.withholdingMeno,
                            item.statementMemo));
                    });
                    self.date(_.first(self.payDateList()).payDate);
                    self.retirementPaymentInfo(_.first(self.payDateList()));
                    self.date.subscribe(function(value){ 
                        let index: any = $("#combo-box7").igCombo("activeIndex");
                        self.retirementPaymentInfo(self.payDateList()[index]); 
                    });
                    self.isUpdate = true; 
                }
                dfd.resolve();
            }).fail(function(res) {
                
            });
            return dfd.promise();
        }
        next_emp() {
            var self = this;
            self.currentEmployeeCode(self.currentEmployeeCode()+1);
        }
        previous_emp() {
            var self = this;
            self.currentEmployeeCode(self.currentEmployeeCode()-1);
        }
        loadData(src : KnockoutObservableArray<any>, dst : KnockoutObservableArray<any>): void {
            ko.utils.arrayForEach(src(), function(item){
                dst.push(item);
            });
        } 
        filter(dataset: KnockoutObservableArray<any>,currentSet: KnockoutObservableArray<BankDataSet>,index: number): any{
            var self = this;
            return _.filter(dataset(), function(o) { 
                    return  o.name == '使用しない' ||
                            o.name == currentSet()[index].dataSetName() ||
                            o.name !== currentSet()[0].dataSetName() && 
                            o.name !== currentSet()[1].dataSetName() &&
                            o.name !== currentSet()[2].dataSetName() &&  
                            o.name !== currentSet()[3].dataSetName() && 
                            o.name !== currentSet()[4].dataSetName();
            });
        }
        refreshSelect(source1: KnockoutObservableArray<any>, source2: KnockoutObservableArray<BankDataSet>, length: number): any{
            var self = this;
            let array = [];
            for(let i=0;i<length;i++){
                array.push(self.filter(source1, source2, i));    
            }
            return array;        
        }
        setDataRow(item: BankDataSet, dataSet: KnockoutObservableArray<BankDataSet>, index: number) {
            if(0<=index){
                item.bankName(dataSet()[index].bankName()); 
                item.branchName(dataSet()[index].branchName()); 
                item.accountNumber(dataSet()[index].accountNumber()); 
                item.transferMoney(dataSet()[index].transferMoney());
            } else {
                item.bankName(null); 
                item.branchName(null); 
                item.accountNumber(null); 
                item.transferMoney(null);    
            }       
        }
        setPaytransfer(variable: KnockoutObservableArray<BankDataSet>, dataSet: any ) {
            for(let i=0; i<5;i++){
                variable.push(new BankDataSet(
                                dataSet["dataSetName"+1],
                                dataSet["bankName"+1],
                                dataSet["branchName"+1],
                                dataSet["accountNumber"+1],
                                dataSet["transferMoney"+1]));    
            }    
        }
        manualCaculator(){
            var self = this;
            let totalPaymentMoney = parseInt(self.retirementPaymentInfo().totalPaymentMoney());
            let deduction1 = parseInt(self.retirementPaymentInfo().deduction1Money());
            let deduction2 = parseInt(self.retirementPaymentInfo().deduction2Money())
            let deduction3 = parseInt(self.retirementPaymentInfo().deduction3Money());
            let incomeTax = parseInt(self.retirementPaymentInfo().incomeTaxMoney());
            let cityTax = parseInt(self.retirementPaymentInfo().cityTaxMoney());
            let prefectureTax = parseInt(self.retirementPaymentInfo().prefectureTaxMoney());
            let totalDeclarationMoney =
                (deduction1?deduction1:0)+
                (deduction2?deduction2:0)+
                (deduction3?deduction3:0)+
                (incomeTax?incomeTax:0)+
                (cityTax?cityTax:0)+
                (prefectureTax?prefectureTax:0);
            self.retirementPaymentInfo().totalDeclarationMoney(totalDeclarationMoney);
            self.retirementPaymentInfo().actualRecieveMoney(totalPaymentMoney - totalDeclarationMoney); 
        }
        autoCaculator(){
            var self = this;
            let year; // LBL003
            let reduction; // Khau tru
            console.log(self.retirementPaymentInfo);
            let totalPaymentMoney = parseInt(self.retirementPaymentInfo().totalPaymentMoney());
            let rs = (totalPaymentMoney-reduction)/2;
            let tax = (rs<0)?rs:0; // Thue nghi viec
            let payOption = self.retirementPaymentInfo().retirementPayOption();
            let deduction1 = parseInt(self.retirementPaymentInfo().deduction1Money());
            let deduction2 = parseInt(self.retirementPaymentInfo().deduction2Money())
            let deduction3 = parseInt(self.retirementPaymentInfo().deduction3Money());
            let incomeTax = parseInt(self.retirementPaymentInfo().incomeTaxMoney());
            if(payOption==2){
                incomeTax = totalPaymentMoney*20.42/100;
            } else if(payOption) {
                        // ==1    
            } else { 
                        // ==0
            };
            let cityTax = (tax*6/100)*90/100;
            let prefectureTax = (tax*4/100)*90/100;
            let totalDeclarationMoney =
                (deduction1?deduction1:0)+
                (deduction2?deduction2:0)+
                (deduction3?deduction3:0)+
                (incomeTax?incomeTax:0)+
                (cityTax?cityTax:0)+
                (prefectureTax?prefectureTax:0);
            self.retirementPaymentInfo().incomeTaxMoney(incomeTax);
            self.retirementPaymentInfo().cityTaxMoney(cityTax);
            self.retirementPaymentInfo().prefectureTaxMoney(prefectureTax);
            self.retirementPaymentInfo().totalDeclarationMoney(totalDeclarationMoney);
            self.retirementPaymentInfo().actualRecieveMoney(totalPaymentMoney - totalDeclarationMoney); 
        }
        openDialog(){
            nts.uk.ui.windows.sub.modal('/view/qrm/001/b/index.xhtml', {title: '入力欄の背景色について', dialogClass: "no-close"});
        }
        saveData(isUpdate){
            var self = this;
            var dfd = $.Deferred();
            var command = ko.mapping.toJS(self.retirementPaymentInfo());
            if(isUpdate) {
                nts.uk.ui.dialog.confirm("Do you want to Update ?").ifYes(function(){
                    qrm001.a.service.updateRetirementPaymentInfo(command).done(function(data){
                        nts.uk.ui.dialog.alert("Update Success");
                        dfd.resolve();
                    }).fail(function(res){
                        nts.uk.ui.dialog.alert("Update Fail");
                        dfd.resolve();    
                    });   
                }).ifNo(function(){});
            } else {
                nts.uk.ui.dialog.confirm("Do you want to Register ?").ifYes(function(){
                    qrm001.a.service.registerRetirementPaymentInfo(command).done(function(data){
                        nts.uk.ui.dialog.alert("Register Success");
                        dfd.resolve(); 
                    }).fail(function(res){
                        nts.uk.ui.dialog.alert("Register Fail");
                        dfd.resolve();     
                    });  
                }).ifNo(function(){});
            }
        }
        deleteData(){
            var self = this;
            var dfd = $.Deferred();
            var command = ko.mapping.toJS(self.retirementPaymentInfo());
            nts.uk.ui.dialog.confirm("Do you want to Remove ?").ifYes(function(){
                qrm001.a.service.removeRetirementPaymentInfo(command).done(function(data){
                    nts.uk.ui.dialog.alert("Remove Success");
                    dfd.resolve(); 
                }).fail(function(res){
                    nts.uk.ui.dialog.alert("Remove Success");
                    dfd.resolve();
                });  
            }).ifCancel(function(){});     
        }
        checkEmpty(){
            var self = this;
            self.retirementPaymentInfo    
        }
    }

    class BankDataSet{
        dataSetName:  KnockoutObservable<any>;
        bankName: KnockoutObservable<any>;
        branchName: KnockoutObservable<any>;
        accountNumber: KnockoutObservable<any>;
        transferMoney: KnockoutObservable<any>;
        constructor(dataSetName: any, bankName: any, branchName: any, accountNumber: any, transferMoney: any){
            this.dataSetName = ko.observable(dataSetName);
            this.bankName = ko.observable(bankName);
            this.branchName = ko.observable(branchName);
            this.accountNumber = ko.observable(accountNumber);
            this.transferMoney = ko.observable(transferMoney);
        }  
    }

    class RetirementPaymentInfo {
        companyCode: KnockoutObservable<any>;
        personId: KnockoutObservable<any>;
        payDate: any;
        trialPeriodSet: KnockoutObservable<any>;
        exclusionYears: KnockoutObservable<any>;
        additionalBoardYears: KnockoutObservable<any>;
        boardYears: KnockoutObservable<any>;
        totalPaymentMoney: KnockoutObservable<any>;
        deduction1Money: KnockoutObservable<any>;
        deduction2Money: KnockoutObservable<any>;
        deduction3Money: KnockoutObservable<any>;
        retirementPayOption: KnockoutObservable<any>;
        taxCalculationMethod: KnockoutObservable<any>;
        incomeTaxMoney: KnockoutObservable<any>;
        cityTaxMoney: KnockoutObservable<any>;
        prefectureTaxMoney: KnockoutObservable<any>;
        totalDeclarationMoney: KnockoutObservable<any>;
        actualRecieveMoney: KnockoutObservable<any>;
        withholdingMeno: KnockoutObservable<any>;
        statementMemo: KnockoutObservable<any>;
        constructor(
            companyCode: any,
            personId: any,
            payDate: any,
            trialPeriodSet: any,
            exclusionYears: any,
            additionalBoardYears: any,
            boardYears: any,
            totalPaymentMoney: any,
            deduction1Money: any,
            deduction2Money: any,
            deduction3Money: any,
            retirementPayOption: any,
            taxCalculationMethod: any,
            incomeTaxMoney: any,
            cityTaxMoney: any,
            prefectureTaxMoney: any,
            totalDeclarationMoney: any,
            actualRecieveMoney: any,
            withholdingMeno: any,
            statementMemo: any ){
                var self = this;
                self.companyCode = ko.observable(companyCode);
                self.personId = ko.observable(personId);
                self.payDate = payDate;
                self.trialPeriodSet = ko.observable(trialPeriodSet);
                self.exclusionYears = ko.observable(exclusionYears);
                self.additionalBoardYears = ko.observable(additionalBoardYears);
                self.boardYears = ko.observable(boardYears);
                self.totalPaymentMoney = ko.observable(totalPaymentMoney);
                self.deduction1Money = ko.observable(deduction1Money);
                self.deduction2Money = ko.observable(deduction2Money);
                self.deduction3Money = ko.observable(deduction3Money);
                self.retirementPayOption = ko.observable(retirementPayOption);
                self.taxCalculationMethod = ko.observable(taxCalculationMethod);
                self.incomeTaxMoney = ko.observable(incomeTaxMoney);
                self.cityTaxMoney = ko.observable(cityTaxMoney);
                self.prefectureTaxMoney = ko.observable(prefectureTaxMoney);
                self.totalDeclarationMoney = ko.observable(totalDeclarationMoney);
                self.actualRecieveMoney = ko.observable(actualRecieveMoney);
                self.withholdingMeno = ko.observable(withholdingMeno);
                self.statementMemo = ko.observable(statementMemo);
                self.totalPaymentMoney.subscribe(function(value){
                    console.log(value);
                });
        }
    }
    class EmployeeInfo {
        personId: KnockoutObservable<any>;
        name: KnockoutObservable<any>;
        constructor(personId: any, name: any){
            this.personId = ko.observable(personId);
            this.name = ko.observable(name);    
        }   
    }
}
