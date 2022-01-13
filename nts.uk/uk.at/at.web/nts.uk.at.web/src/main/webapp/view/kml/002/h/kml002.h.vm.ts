module nts.uk.at.view.kml002.k {
    import getText = nts.uk.resource.getText;
    import setShare = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    const Paths = {
        GET_ESTIMATE_INFO_BY_CID:"screen/at/kml002/h/init",
        GET_DEFAULT_INFO:"screen/at/kml002/k/init",
        GET_ESTIMATE_INFO_BY_CID_AND_CODE:"screen/at/kml002/k/getEstimatedInfo",
        REGISTER_SETTING_CMP:"ctx/at/schedule/budget/initCompanyInfo/register",
        REGISTER_SETTING_EMP:"ctx/at/schedule/budget/employmentsetting/register",
        DELETE_SETTING_EMP:"ctx/at/schedule/budget/employmentsetting/remove"

    };
    @bean()
    class Kml002hViewModel extends ko.ViewModel {
        name: KnockoutObservable<string> = ko.observable('');

        itemHandling: KnockoutObservable<ItemHandling> = ko.observable(new ItemHandling());
        itemMonthly: KnockoutObservable<ItemMonthly> = ko.observable(new ItemMonthly());
        itemAnnual: KnockoutObservable<ItemAnnual> = ko.observable(new ItemAnnual());


        itemHandlingScreenK: KnockoutObservable<ItemHandling> = ko.observable();
        itemMonthlyScreenK: KnockoutObservable<ItemMonthly> = ko.observable(new ItemMonthly());
        itemAnnualScreenK: KnockoutObservable<ItemAnnual> = ko.observable(new ItemAnnual());
        isReloadScreenK: KnockoutObservable<boolean> = ko.observable(false);
        isUseageEmployment: KnockoutObservable<boolean> = ko.observable(true);
        enableDeleteBtn: KnockoutObservable<boolean> = ko.observable(false);
        enableRegisterBtn: KnockoutObservable<boolean> = ko.observable(true);
        enableRegisterBtnScreenK: KnockoutObservable<boolean> = ko.observable(true);

        currentScreen: any = null;
        

        listComponentOption: any;
        selectedCode: KnockoutObservable<string> = ko.observable('1');
        isShowAlreadySet: KnockoutObservable<boolean> = ko.observable(true);
        alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel> = ko.observableArray([]);
        isDialog: KnockoutObservable<boolean> = ko.observable(false);
        isShowNoSelectRow: KnockoutObservable<boolean> = ko.observable(false);
        isMultiSelect: KnockoutObservable<boolean> = ko.observable(false);
        employmentList: KnockoutObservableArray<UnitModel> = ko.observableArray<UnitModel>([]);
        //Upgrade
        isDisplayClosureSelection: KnockoutObservable<boolean> = ko.observable(false);
        isDisplayFullClosureOption: KnockoutObservable<boolean> = ko.observable(false);
        closureSelectionType: KnockoutObservable<number>;
        selectClosureTypeList: KnockoutObservableArray<any>;
        listEmp: KnockoutObservableArray<EmpModel> = ko.observableArray<EmpModel>([]);
        
        constructor() {
            super();
            const self = this;      
            $('#screen-h').click(function(){
                self.clearErrorAnnual();
                self.clearErrorMonth();
                self.clearError();
                self.loadData();
                // self.$window.size(660, 1050);
             }); 
            $('#screen-k').click(function(){
                self.clearErrorAnnual();
                self.clearErrorMonth();   
                self.clearError();            
                self.loadDataScreenK();
            });       

            self.loadData();
           
            self.listComponentOption = {
                isShowAlreadySet: self.isShowAlreadySet(),
                isMultiSelect: self.isMultiSelect(),
                listType: ListType.EMPLOYMENT,
                selectType: SelectType.SELECT_BY_SELECTED_CODE,
                selectedCode: self.selectedCode,
                isDialog: self.isDialog(),
                isShowNoSelectRow: self.isShowNoSelectRow(),
                alreadySettingList: self.alreadySettingList,
                maxRows: 12
            };

            self.selectedCode.subscribe((code) => {
                self.clearErrorAnnual();
                self.clearErrorMonth();
                self.clearError();
                self.loadData();
                self.findDetail(code);
            }); 

            self.itemHandling.subscribe((val) => {     
                self.clearError();     
                self.enableRegisterBtn(true);                   
                val.backgroundColor1.subscribe((color) => {
                    self.clearError();
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtn(true);   
                    // $('#colorpicker1').ntsError('clear');
                    // self.enableRegisterBtn(true); 
                    // if (!color || color =='') {
                    //     $('#colorpicker1').ntsError('set', { messageId: 'MsgB_2', messageParams: [getText("KML002_159")]});    
                    //     self.enableRegisterBtn(false);
                    // }
                }) ;
                 val.backgroundColor2.subscribe((color) => {
                    self.clearError();
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtn(true);                    
                });

                val.backgroundColor3.subscribe((color) => {
                    self.clearError();
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtn(true);                    
                });
                val.backgroundColor4.subscribe((color) => {
                    self.clearError();
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtn(true);                    
                });
                val.backgroundColor5.subscribe((color) => {
                    self.clearError();
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtn(true);                   
                });     

            });   

            self.itemMonthly.subscribe((val) => {
                val.amount1.subscribe((amount) => {
                    self.clearError();
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtn(true); 
                });

                val.amount2.subscribe((amount) => {
                    self.clearError();
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtn(true); 
                });

                val.amount3.subscribe((amount) => {
                    self.clearError();
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtn(true); 
                });

                val.amount4.subscribe((amount) => {
                    self.clearError();
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtn(true); 
                });

                val.amount5.subscribe((amount) => {
                    self.clearError();
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtn(true); 
                });
            });

            self.itemAnnual.subscribe((val) => {
                val.amount1.subscribe((amount) => {
                    self.clearError();
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtn(true); 
                });

                val.amount2.subscribe((amount) => {
                    self.clearError();
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtn(true); 
                });

                val.amount3.subscribe((amount) => {
                    self.clearError();
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtn(true); 
                });

                val.amount4.subscribe((amount) => {
                    self.clearError();
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtn(true); 
                });

                val.amount5.subscribe((amount) => {
                    self.clearError();
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtn(true); 
                });                
            });

            self.itemMonthlyScreenK.subscribe((val) => {
                val.amount1.subscribe((amount) => {
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtnScreenK(true); 
                });

                val.amount2.subscribe((amount) => {
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtnScreenK(true); 
                });

                val.amount3.subscribe((amount) => {
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtnScreenK(true); 
                });

                val.amount4.subscribe((amount) => {
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtnScreenK(true); 
                });

                val.amount5.subscribe((amount) => {
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtnScreenK(true); 
                });
            });

            self.itemAnnualScreenK.subscribe((val) => {
                val.amount1.subscribe((amount) => {
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtnScreenK(true); 
                });

                val.amount2.subscribe((amount) => {
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtnScreenK(true); 
                });

                val.amount3.subscribe((amount) => {
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtnScreenK(true); 
                });

                val.amount4.subscribe((amount) => {
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtnScreenK(true); 
                });

                val.amount5.subscribe((amount) => {
                    self.clearErrorAnnual();
                    self.clearErrorMonth(); 
                    self.enableRegisterBtnScreenK(true); 
                });                
            });        
        }

        mounted(){
            $('#task-list').focus();
            setTimeout(function(){ 
                let g = $('[tabindex=0]');
                for (let i = 0; i< g.length; i++) 
                    {
                    $(g[i]).attr("tabindex" , -1);
                    }},200);
        }

        loadData(): void {
            const self = this;
            let dataHandling: Array<string> = [], dataMonthly: Array<string> = [], dataAnnual: Array<string> = [];
            let amountMonthly: string, amountAnnual: string, backgroundColor: string;
            self.$blockui("invisible");
            self.$ajax(Paths.GET_ESTIMATE_INFO_BY_CID).done((data: any) => {
                if (data) {
					data.esimatedInfoDto.months = _.sortBy(data.esimatedInfoDto.months, [function(o) { return o.frameNo; }]);
					data.esimatedInfoDto.annuals = _.sortBy(data.esimatedInfoDto.annuals, [function(o) { return o.frameNo; }]);
					data.esimatedInfoDto.listHandlingByNo = _.sortBy(data.esimatedInfoDto.listHandlingByNo, [function(o) { return o.frameNo; }]);
                    self.isUseageEmployment(data.useSetting);
                    for (let i = 1; i < 6; i++) {
                        if (data.esimatedInfoDto.months && 
                            i <= data.esimatedInfoDto.months.length && data.esimatedInfoDto.months[i - 1].frameNo == i) {
                            amountMonthly = data.esimatedInfoDto.months[i - 1].amount;
                        } else {
                            amountMonthly = '';
                        }
                        if (data.esimatedInfoDto.annuals && 
                            i <= data.esimatedInfoDto.annuals.length && data.esimatedInfoDto.annuals[i - 1].frameNo == i) {
                            amountAnnual = data.esimatedInfoDto.annuals[i - 1].amount;
                        } else {
                            amountAnnual = '';
                        }

                        if (data.esimatedInfoDto.listHandlingByNo && 
                            i <= data.esimatedInfoDto.listHandlingByNo.length && data.esimatedInfoDto.listHandlingByNo[i - 1].frameNo == i) {
                            backgroundColor = '#' + data.esimatedInfoDto.listHandlingByNo[i - 1].backgroundColor;
                        } else {
                            backgroundColor = '#FFFFFF';
                        }

                        dataHandling.push(backgroundColor);
                        dataMonthly.push(amountMonthly);
                        dataAnnual.push(amountAnnual);
                    }
                }
                self.itemHandling(new ItemHandling(dataHandling));
                self.itemMonthly(new ItemMonthly(dataMonthly));
                self.itemAnnual(new ItemAnnual(dataAnnual));
                $('#month1').focus();
            }).always(() => {
                self.$blockui("hide");
            });
        }
        
        registerMoneyCmp(): void {
            const self = this;
            let command: any = {}, handlings: Array<ItemHandlingModel> = [], months: Array<ItemAmountModel> = [],
                years: Array<ItemAmountModel> = [];

            handlings.push({ "frameNo": 1, "backgroundColor": self.itemHandling().backgroundColor1() });
            handlings.push({ "frameNo": 2, "backgroundColor": self.itemHandling().backgroundColor2() });
            handlings.push({ "frameNo": 3, "backgroundColor": self.itemHandling().backgroundColor3() });
            handlings.push({ "frameNo": 4, "backgroundColor": self.itemHandling().backgroundColor4() });
            handlings.push({ "frameNo": 5, "backgroundColor": self.itemHandling().backgroundColor5() });

            self.checkDataScreenH(handlings, self.itemMonthly(), self.itemAnnual());
            if (self.validateAll()) {
                return;
            }          
                
           
            if(!_.isNull(self.itemMonthly().amount1())){
                months.push({ "frameNo": 1, "amount": parseInt(self.itemMonthly().amount1()) });
            }
            if(self.itemMonthly().amount2()){
                months.push({ "frameNo": 2, "amount": parseInt(self.itemMonthly().amount2()) });
            }
            if(self.itemMonthly().amount3()){
                months.push({ "frameNo": 3, "amount": parseInt(self.itemMonthly().amount3()) });
            }
            if(self.itemMonthly().amount4()){
                months.push({ "frameNo": 4, "amount": parseInt(self.itemMonthly().amount4()) });
            }
            if(self.itemMonthly().amount5()){
                months.push({ "frameNo": 5, "amount": parseInt(self.itemMonthly().amount5()) });
            }

            if(!_.isNull(self.itemAnnual().amount1())){
                years.push({ "frameNo": 1, "amount": parseInt(self.itemAnnual().amount1()) });
            }

            if(self.itemAnnual().amount2()){
                years.push({ "frameNo": 2, "amount": parseInt(self.itemAnnual().amount2()) });
            }

            if(self.itemAnnual().amount3()){
                years.push({ "frameNo": 3, "amount": parseInt(self.itemAnnual().amount3()) });
            }

            if(self.itemAnnual().amount4()){
                years.push({ "frameNo": 4, "amount": parseInt(self.itemAnnual().amount4()) });
            }

            if(self.itemAnnual().amount5()){
                years.push({ "frameNo": 5, "amount": parseInt(self.itemAnnual().amount5()) });
            }

            command.months = months;
            command.years = years;
            command.handlings = handlings;
            self.$blockui("invisible");
            self.$ajax(Paths.REGISTER_SETTING_CMP, command).done(() => {
                self.$dialog.info({ messageId: 'Msg_15' }).then(() => {
                    self.loadData();
                    $('#month1').focus();
                });               
            }).fail((res) => {
                self.$dialog.alert({ messageId: res.messageId });
            }).always(() => {
                self.$blockui("hide");
            });
        }

        checkDataScreenH(itemHandling: Array<ItemHandlingModel>, itemMonthly: ItemMonthly, itemAnnual: ItemAnnual): void {
            let self = this;
            if(_.isNull(itemHandling[0].backgroundColor) || itemHandling[0].backgroundColor == ''){
                $('#colorpicker1').ntsError('set', { messageId: 'MsgB_2', messageParams: [getText("KML002_159")]});
                self.enableRegisterBtn(false);            }
            
            if(_.isNull(itemHandling[1].backgroundColor) || itemHandling[1].backgroundColor == ''){
                $('#colorpicker2').ntsError('set', { messageId: 'MsgB_2', messageParams: [getText("KML002_160")]}); 
                self.enableRegisterBtn(false);
            }
            if(_.isNull(itemHandling[2].backgroundColor) || itemHandling[2].backgroundColor == ''){
                $('#colorpicker3').ntsError('set', { messageId: 'MsgB_2', messageParams: [getText("KML002_161")]}); 
                self.enableRegisterBtn(false);
            }
            if(_.isNull(itemHandling[3].backgroundColor) || itemHandling[3].backgroundColor == ''){
                $('#colorpicker4').ntsError('set', { messageId: 'MsgB_2', messageParams: [getText("KML002_162")]}); 
                self.enableRegisterBtn(false);
            }
            if(_.isNull(itemHandling[4].backgroundColor) ||itemHandling[4].backgroundColor == ''){
                $('#colorpicker5').ntsError('set', { messageId: 'MsgB_2', messageParams: [getText("KML002_163")]}); 
                self.enableRegisterBtn(false);
            }
            if( (itemMonthly.amount2() == '' && (itemMonthly.amount3() != '' || itemMonthly.amount4() != '' || itemMonthly.amount5() != '')) ||
                                    (itemMonthly.amount2() != '' && parseInt(itemMonthly.amount1()) >= parseInt(itemMonthly.amount2()))) {
                $('#month2').ntsError('set', { messageId: 'MsgB_1', messageParams: [getText("KML002_153") + getText("KML002_140")] });
                self.enableRegisterBtn(false);
            }
            if( (itemMonthly.amount3() == '' && (itemMonthly.amount4() != '' || itemMonthly.amount5() != '')) ||
                                    (itemMonthly.amount3() != '' && parseInt(itemMonthly.amount2()) >= parseInt(itemMonthly.amount3()))) {
                $('#month3').ntsError('set', { messageId: 'MsgB_1', messageParams: [getText("KML002_153") + getText("KML002_141")] });
                self.enableRegisterBtn(false);
            }
            if( (itemMonthly.amount4() == '' && itemMonthly.amount5() != '') ||
                                    (itemMonthly.amount4() != '' && parseInt(itemMonthly.amount3()) >= parseInt(itemMonthly.amount4()))) {
                $('#month4').ntsError('set', { messageId: 'MsgB_1', messageParams: [getText("KML002_153") + getText("KML002_142")] });
                self.enableRegisterBtn(false);
            }
            if(itemMonthly.amount5() != '' && parseInt(itemMonthly.amount4()) >= parseInt(itemMonthly.amount5())) {
                $('#month5').ntsError('set', { messageId: 'MsgB_1', messageParams: [getText("KML002_153") + getText("KML002_143")] });
                self.enableRegisterBtn(false);
            }

            if( (itemAnnual.amount2() == '' && (itemAnnual.amount3() != '' || itemAnnual.amount4() != '' || itemAnnual.amount5() != '')) ||
                            (itemAnnual.amount2() != '' && parseInt(itemAnnual.amount1()) >= parseInt(itemAnnual.amount2()))) {
                $('#year2').ntsError('set', { messageId: 'MsgB_1', messageParams: [getText("KML002_153") + getText("KML002_146")] });
                self.enableRegisterBtn(false);
            }
            if( (itemAnnual.amount3() == '' && (itemAnnual.amount4() != '' || itemAnnual.amount5() != '')) ||
                            (itemAnnual.amount3() != '' && parseInt(itemAnnual.amount2()) >= parseInt(itemAnnual.amount3()))) {
                $('#year3').ntsError('set', { messageId: 'MsgB_1', messageParams: [getText("KML002_153") + getText("KML002_147")] });
                self.enableRegisterBtn(false);
            }
            if( (itemAnnual.amount4() == '' && itemAnnual.amount5() != '' ) ||
                          (itemAnnual.amount4() != '' && parseInt(itemAnnual.amount3()) >= parseInt(itemAnnual.amount4()))) {
                $('#year4').ntsError('set', { messageId: 'MsgB_1', messageParams: [getText("KML002_153") + getText("KML002_148")] });
                self.enableRegisterBtn(false);
            }
            if( itemAnnual.amount5() != '' && parseInt(itemAnnual.amount4()) >= parseInt(itemAnnual.amount5())) {
                $('#year5').ntsError('set', { messageId: 'MsgB_1', messageParams: [getText("KML002_153") + getText("KML002_149")] });
                self.enableRegisterBtn(false);
            }
        }

        loadDataScreenK(): void {
            const self = this;
            let dataHandling: Array<string> = [], alreadySettingList: Array<UnitAlreadySettingModel> = [], backgroundColor: string;
            
            self.$blockui("invisible");
            self.$ajax(Paths.GET_DEFAULT_INFO).done((data: any) => {
                if (data) {
                    self.listEmp(data.employments);
                    if(!self.isReloadScreenK()){
                        self.selectedCode(data.employments[0].code);                        
                        self.name(data.employments[0].name);
                    } else {                        
                        if(data.emmployments){
                            _.filter(data.emmployments, item => {
                                if(item.code == self.selectedCode() ){
                                    self.name(item.name);
                                }
                            });
                        }                        
                    }                  
                    self.findDetail(self.selectedCode());

                    _.each(data.employments, emp => {
                        _.each(data.employmentCodes, code =>{
                            if(emp.code === code){
                                alreadySettingList.push({code: emp.code, isAlreadySetting: true});
                            }                            
                        })                        
                    });
					data.listHandlingByNo  = _.sortBy(data.listHandlingByNo, [function(o) { return o.frameNo; }]);

                    for (let i = 1; i < 6; i++) {
                        if (i <= data.listHandlingByNo.length && data.listHandlingByNo[i - 1].frameNo == i) {
                            backgroundColor = '#' + data.listHandlingByNo[i - 1].backgroundColor;
                        } else {
                            backgroundColor = '#FFFFFF';
                        }
                        dataHandling.push(backgroundColor);
                    }
                    $('#empt-list-setting').ntsListComponent(self.listComponentOption).done(function(){
                        $('#empt-list-setting').focusComponent();
                    });;
                    self.alreadySettingList(alreadySettingList);
                    if(self.alreadySettingList().length > 0){
                        self.enableDeleteBtn(true);
                    } else {
                        self.enableDeleteBtn(false);
                    }
                    self.itemHandlingScreenK(new ItemHandling(dataHandling));
                    $('#month1screenk').focus();
                }
            }).always(() => {
                self.$blockui("hide");
            });
        }

        findDetail(code: string): void {
            const self = this;
            let dataMonthly: Array<string> = [], dataAnnual: Array<string> = [];
            let amountMonthly: string, amountAnnual: string;
            self.name(_.find(self.listEmp(), emp =>{return emp.code == code;}).name);
            self.$blockui("invisible");           
            self.$ajax(Paths.GET_ESTIMATE_INFO_BY_CID_AND_CODE + "/" + code).done((data: any) => {
                if (data) {
                    for (let i = 1; i < 6; i++) {
                        if(i <= data.months.length && data.months[i-1].frameNo == i){
                            amountMonthly = data.months[i-1].amount;
                        } else {
                            amountMonthly = '';
                        }
                        if(i <= data.years.length && data.years[i-1].frameNo == i){
                             amountAnnual = data.years[i-1].amount;
                        } else {
                            amountAnnual ='';
                        }
                        dataMonthly.push(amountMonthly);
                        dataAnnual.push(amountAnnual);
                    }
                    self.enableDeleteBtn(true);                   
                } else {
                    for (let i = 1; i < 6; i++) {
                        amountMonthly = '';
                        amountAnnual ='';
                        dataMonthly.push(amountMonthly);
                        dataAnnual.push(amountAnnual);
                    }
                    self.enableDeleteBtn(false);
                }
                self.itemMonthlyScreenK(new ItemMonthly(dataMonthly));
                self.itemAnnualScreenK(new ItemAnnual(dataAnnual));               
                $('#month1screenk').focus();
            }).always(() => {
                self.$blockui("hide");
            });
        }

        
        checkDataScreenK(itemMonthly: ItemMonthly, itemAnnual: ItemAnnual): void {
            let self = this;            
            if( (itemMonthly.amount2() == '' && (itemMonthly.amount3() != '' || itemMonthly.amount4() != '' || itemMonthly.amount5() != '')) ||
                                    (itemMonthly.amount2() != '' && parseInt(itemMonthly.amount1()) >= parseInt(itemMonthly.amount2()))) {
                $('#month2screenk').ntsError('set', { messageId: 'MsgB_1', messageParams: [getText("KML002_153") + getText("KML002_140")] });
                self.enableRegisterBtn(false);
            }
            if( (itemMonthly.amount3() == '' && (itemMonthly.amount4() != '' || itemMonthly.amount5() != '')) ||
                                    (itemMonthly.amount3() != '' && parseInt(itemMonthly.amount2()) >= parseInt(itemMonthly.amount3()))) {
                $('#month3screenk').ntsError('set', { messageId: 'MsgB_1', messageParams: [getText("KML002_153") + getText("KML002_141")] });
                self.enableRegisterBtn(false);
            }
            if( (itemMonthly.amount4() == '' && itemMonthly.amount5() != '') ||
                                    (itemMonthly.amount4() != '' && parseInt(itemMonthly.amount3()) >= parseInt(itemMonthly.amount4()))) {
                $('#month4screenk').ntsError('set', { messageId: 'MsgB_1', messageParams: [getText("KML002_153") + getText("KML002_142")] });
                self.enableRegisterBtn(false);
            }
            if(itemMonthly.amount5() != '' && parseInt(itemMonthly.amount4()) >= parseInt(itemMonthly.amount5())) {
                $('#month5screenk').ntsError('set', { messageId: 'MsgB_1', messageParams: [getText("KML002_153") + getText("KML002_143")] });
                self.enableRegisterBtn(false);
            }

            if( (itemAnnual.amount2() == '' && (itemAnnual.amount3() != '' || itemAnnual.amount4() != '' || itemAnnual.amount5() != '')) ||
                            (itemAnnual.amount2() != '' && parseInt(itemAnnual.amount1()) >= parseInt(itemAnnual.amount2()))) {
                $('#year2screenk').ntsError('set', { messageId: 'MsgB_1', messageParams: [getText("KML002_153") + getText("KML002_146")] });
                self.enableRegisterBtn(false);
            }
            if( (itemAnnual.amount3() == '' && (itemAnnual.amount4() != '' || itemAnnual.amount5() != '')) ||
                            (itemAnnual.amount3() != '' && parseInt(itemAnnual.amount2()) >= parseInt(itemAnnual.amount3()))) {
                $('#year3screenk').ntsError('set', { messageId: 'MsgB_1', messageParams: [getText("KML002_153") + getText("KML002_147")] });
                self.enableRegisterBtn(false);
            }
            if( (itemAnnual.amount4() == '' && itemAnnual.amount5() != '' ) ||
                          (itemAnnual.amount4() != '' && parseInt(itemAnnual.amount3()) >= parseInt(itemAnnual.amount4()))) {
                $('#year4screenk').ntsError('set', { messageId: 'MsgB_1', messageParams: [getText("KML002_153") + getText("KML002_148")] });
                self.enableRegisterBtn(false);
            }
            if( itemAnnual.amount5() != '' && parseInt(itemAnnual.amount4()) >= parseInt(itemAnnual.amount5())) {
                $('#year5screenk').ntsError('set', { messageId: 'MsgB_1', messageParams: [getText("KML002_153") + getText("KML002_149")] });
                self.enableRegisterBtn(false);
            }
        }


        registerMoneyEmp(): void {
            const self = this;            
            let command:any = {}, months: Array<ItemAmountModel> = [], 
            years: Array<ItemAmountModel> = [];

            self.checkDataScreenK(self.itemMonthlyScreenK() ,self.itemAnnualScreenK());

            if (self.validateAllScreenK()) {
                return;
            }

            if(!_.isNull(self.itemMonthlyScreenK().amount1())){
                months.push({ "frameNo": 1, "amount": parseInt(self.itemMonthlyScreenK().amount1()) });
            }
            if(self.itemMonthlyScreenK().amount2()){
                months.push({ "frameNo": 2, "amount": parseInt(self.itemMonthlyScreenK().amount2()) });
            }
            if(self.itemMonthlyScreenK().amount3()){
                months.push({ "frameNo": 3, "amount": parseInt(self.itemMonthlyScreenK().amount3()) });
            }
            if(self.itemMonthlyScreenK().amount4()){
                months.push({ "frameNo": 4, "amount": parseInt(self.itemMonthlyScreenK().amount4()) });
            }
            if(self.itemMonthlyScreenK().amount5()){
                months.push({ "frameNo": 5, "amount": parseInt(self.itemMonthlyScreenK().amount5()) });
            }

            if(!_.isNull(self.itemAnnualScreenK().amount1())){
                years.push({ "frameNo": 1, "amount": parseInt(self.itemAnnualScreenK().amount1()) });
            }

            if(self.itemAnnualScreenK().amount2()){
                years.push({ "frameNo": 2, "amount": parseInt(self.itemAnnualScreenK().amount2()) });
            }

            if(self.itemAnnualScreenK().amount3()){
                years.push({ "frameNo": 3, "amount": parseInt(self.itemAnnualScreenK().amount3()) });
            }

            if(self.itemAnnualScreenK().amount4()){
                years.push({ "frameNo": 4, "amount": parseInt(self.itemAnnualScreenK().amount4()) });
            }

            if(self.itemAnnualScreenK().amount5()){
                years.push({ "frameNo": 5, "amount": parseInt(self.itemAnnualScreenK().amount5()) });
            }

            command.employmentCode = self.selectedCode();
            command.months = months;
            command.years = years;
            self.$blockui("invisible");
            self.$ajax(Paths.REGISTER_SETTING_EMP, command).done(() => {
                self.$dialog.info({messageId: 'Msg_15'}).then(() => {
                    self.isReloadScreenK(true);
                    self.loadDataScreenK();
                    self.findDetail(self.selectedCode());   
                    $('#month1screenk').focus();  
                });
            }).fail((res) => {
                self.$dialog.alert({messageId: res.messageId});
            }).always(() => {
                self.$blockui("hide");
            });
        }

        public remove(): void {
            const self = this;           
            self.$dialog.confirm({messageId: "Msg_18"}).then((result: 'no' | 'yes') =>{
                self.$blockui("invisible");
                let command: any = {
                    employmentCode: self.selectedCode()
                }
                
                if (result === 'yes') {
                    self.$ajax(Paths.DELETE_SETTING_EMP, command).done(() => {
                        self.$dialog.info({ messageId: "Msg_16" }).then(() => {
                            for (let index = 0; index < self.alreadySettingList().length; index++) {
                                if (self.alreadySettingList()[index].code == self.selectedCode()) {
                                    self.alreadySettingList.splice(index, 1);
                                    break;
                                }
                            }
                            self.isReloadScreenK(true);
                            self.loadDataScreenK();
                        });
                    }).always(() => {
                        self.$blockui("hide");
                    });
                    self.$blockui("hide");
                }
                if(result === 'no'){
                    self.$blockui("hide");
                }
            });            
        }

        openDialogScreenL(): void {
            const self = this;
            self.currentScreen = nts.uk.ui.windows.sub.modal('/view/kml/002/l/index.xhtml').onClosed(() => {
                self.clearErrorAnnual();
                self.clearErrorMonth();
                self.clearError();
                self.loadData();
            });          
        }

        closeDialog(): void {
            const self = this;           
            self.$window.close();
        }      
        
        private validateAll(): boolean {
            const self = this;
            $('#colorpicker1').ntsEditor('validate');
            $('#colorpicker2').ntsEditor('validate');
            $('#colorpicker3').ntsEditor('validate');
            $('#colorpicker4').ntsEditor('validate');
            $('#colorpicker5').ntsEditor('validate');

            $('#month1').ntsEditor('validate');
            $('#month2').ntsEditor('validate');
            $('#month3').ntsEditor('validate');
            $('#month4').ntsEditor('validate');
            $('#month5').ntsEditor('validate');

            $('#year1').ntsEditor('validate');
            $('#year2').ntsEditor('validate');
            $('#year3').ntsEditor('validate');
            $('#year4').ntsEditor('validate');
            $('#year5').ntsEditor('validate');
            
            if (nts.uk.ui.errors.hasError()) {   
                self.enableRegisterBtn(false);                 
                return true;                
            }
            if(self.itemMonthly().amount1() !='' && parseInt(self.itemMonthly().amount1()) != 0
                && self.itemAnnual().amount1() != '' && parseInt(self.itemAnnual().amount1())!= 0){
                self.enableRegisterBtn(true);
            }            
            return false;
        }

        private validateAllScreenK(): boolean {
            const self = this;
            $('#month1screenk').ntsEditor('validate');
            $('#month2screenk').ntsEditor('validate');
            $('#month3screenk').ntsEditor('validate');
            $('#month4screenk').ntsEditor('validate');
            $('#month5screenk').ntsEditor('validate');

            $('#year1screenk').ntsEditor('validate');
            $('#year2screenk').ntsEditor('validate');
            $('#year3screenk').ntsEditor('validate');
            $('#year4screenk').ntsEditor('validate');
            $('#year5screenk').ntsEditor('validate');
            if (nts.uk.ui.errors.hasError()) {       
                self.enableRegisterBtnScreenK(false);
                return true;
            }

            if(self.itemMonthlyScreenK().amount1() !='' && parseInt(self.itemMonthlyScreenK().amount1()) != 0
                && self.itemAnnualScreenK().amount1() != '' && parseInt(self.itemAnnualScreenK().amount1())!= 0){
                self.enableRegisterBtnScreenK(true);
            }               
            return false;
        }

        
        private clearError(): void {
            $('#colorpicker1').ntsError('clear');
            $('#colorpicker2').ntsError('clear');
            $('#colorpicker3').ntsError('clear');
            $('#colorpicker4').ntsError('clear');
            $('#colorpicker5').ntsError('clear');            
        }

        private clearErrorMonth(): void {
            $('#month1').ntsError('clear');
            $('#month2').ntsError('clear');
            $('#month3').ntsError('clear');
            $('#month4').ntsError('clear');
            $('#month5').ntsError('clear');           
            $('#month1screenk').ntsError('clear');
            $('#month2screenk').ntsError('clear');
            $('#month3screenk').ntsError('clear');
            $('#month4screenk').ntsError('clear');
            $('#month5screenk').ntsError('clear');      
            
        }

        private clearErrorAnnual(): void {           
            $('#year1').ntsError('clear');
            $('#year2').ntsError('clear');
            $('#year3').ntsError('clear');
            $('#year4').ntsError('clear');
            $('#year5').ntsError('clear');  
            $('#year1screenk').ntsError('clear');          
            $('#year2screenk').ntsError('clear');
            $('#year3screenk').ntsError('clear');
            $('#year4screenk').ntsError('clear');
            $('#year5screenk').ntsError('clear');
        }
    }

    interface IEsimatedInfo {
        months: Array<ItemModel>;
        annuals: Array<ItemModel>;
        listHandlingByNo: Array<ItemModel>;
    }

    interface ItemModel {
        frameNo: number;
        data: string;
    }

    class EsimatedInfo {
        months: KnockoutObservableArray<ItemModel> = ko.observableArray([]);   
        annuals: KnockoutObservableArray<ItemModel> = ko.observableArray([]); 
        listHandlingByNo: KnockoutObservableArray<ItemModel> = ko.observableArray([]);    

        constructor(params ?: IEsimatedInfo) {
            const self = this;
            if(params){
                self.annuals(params.annuals);
                self.months(params.months);
                self.listHandlingByNo(params.listHandlingByNo);
            }
        }
    }

    class ItemHandling {
        backgroundColor1: KnockoutObservable<string> = ko.observable('');
        backgroundColor2: KnockoutObservable<string> = ko.observable('');
        backgroundColor3: KnockoutObservable<string> = ko.observable('');
        backgroundColor4: KnockoutObservable<string> = ko.observable('');
        backgroundColor5: KnockoutObservable<string> = ko.observable('');

        constructor(backgroundColors?: Array<string>) {
            const self = this;
            if(backgroundColors){
                self.backgroundColor1(backgroundColors[0]);
                self.backgroundColor2(backgroundColors[1]);
                self.backgroundColor3(backgroundColors[2]);
                self.backgroundColor4(backgroundColors[3]);
                self.backgroundColor5(backgroundColors[4]);
            } else {
                self.backgroundColor1('');
                self.backgroundColor2('');
                self.backgroundColor3('');
                self.backgroundColor4('');
                self.backgroundColor5('');
            }           
        }        
    }

    class ItemMonthly {
        amount1: KnockoutObservable<string> = ko.observable('');
        amount2: KnockoutObservable<string> = ko.observable('');
        amount3: KnockoutObservable<string> = ko.observable('');
        amount4: KnockoutObservable<string> = ko.observable('');
        amount5: KnockoutObservable<string> = ko.observable('');

        constructor(amounts?: Array<string>) {
            const self = this;
            if(amounts){
                self.amount1(amounts[0]);
                self.amount2(amounts[1]);
                self.amount3(amounts[2]);
                self.amount4(amounts[3]);
                self.amount5(amounts[4]);
            } else {
                self.amount1('');
                self.amount2('');
                self.amount3('');
                self.amount4('');
                self.amount5('');
            }           
        }
    }

    class ItemAnnual {
        amount1: KnockoutObservable<string> = ko.observable();
        amount2: KnockoutObservable<string> = ko.observable();
        amount3: KnockoutObservable<string> = ko.observable();
        amount4: KnockoutObservable<string> = ko.observable();
        amount5: KnockoutObservable<string> = ko.observable();

        constructor(amounts?: Array<string>) {
            const self = this;
            if(amounts){
                self.amount1(amounts[0]);
                self.amount2(amounts[1]);
                self.amount3(amounts[2]);
                self.amount4(amounts[3]);
                self.amount5(amounts[4]);
            } else {
                self.amount1('');
                self.amount2('');
                self.amount3('');
                self.amount4('');
                self.amount5('');
            }           
        }
    }

    class ItemHandlingModel {
        frameNo: number;
        backgroundColor: string;        
    }

    class ItemAmountModel {
        frameNo: number;
        amount: number;
    }

    class EmpModel {
        code: string;
        name: string;
    }
    
    export class ListType {
        static EMPLOYMENT = 1;
        static Classification = 2;
        static JOB_TITLE = 3;
        static EMPLOYEE = 4;
    }

    export interface UnitModel {
        code: string;
        name?: string;
        workplaceName?: string;
        isAlreadySetting?: boolean;
    }
    export class SelectType {
        static SELECT_BY_SELECTED_CODE = 1;
        static SELECT_ALL = 2;
        static SELECT_FIRST_ITEM = 3;
        static NO_SELECT = 4;
    }
    
    export interface UnitAlreadySettingModel {
        code: string;
        isAlreadySetting: boolean;
    }

}
