module nts.uk.at.view.kdw001.b {
    import getText = nts.uk.resource.getText;
    import shareModel = nts.uk.at.view.kdw001.share.model;

    export module viewmodel {
        export class ScreenModel {
            params: shareModel.executionProcessingCommand = new shareModel.executionProcessingCommand();
          // closureID: any = __viewContext.transferred.value.closureID;
            //Declare import cScreenmodel, dScreenmodel
            cScreenmodel: any;
            dScreenmodel: any;

            //enable for checkBox area
            forciblyEnable: KnockoutObservable<boolean>;
            //Declare for checkBox 
            dailyCreatedCheck: KnockoutObservable<boolean>;
            dailyCalCheck: KnockoutObservable<boolean>;
            approvalResultCheck: KnockoutObservable<boolean>;
            forciblyCommitCheck: KnockoutObservable<boolean>;
            monthCountCheck: KnockoutObservable<boolean>;
            //Declare for radio button group
            // enable2: KnockoutObservable<boolean>;
            // selectedValue2: KnockoutObservable<any>;
            //selectedValue3: KnockoutObservable<any>;
            option1: KnockoutObservable<any>;
            option2: KnockoutObservable<any>;

            //Declare for help button properties
            enable1: KnockoutObservable<boolean>;

            //Declare for switch button properties
            roundingRules5: KnockoutObservableArray<any>;
            roundingRules4: KnockoutObservableArray<any>;
            roundingRules3: KnockoutObservableArray<any>;
            roundingRules1: KnockoutObservableArray<any>;
            selectedRuleCode: any;
            selectedCreatDivisionCode: KnockoutObservable<any> = ko.observable(0);
            selectedCalDivisionCode: KnockoutObservable<any> = ko.observable(0);
            selectedReflectClassCode: KnockoutObservable<any> = ko.observable(0);
            selectedAggregateClassCode: KnockoutObservable<any> = ko.observable(0);

            //Declare checkbox properties
            checked: KnockoutObservable<boolean>;
            enable: KnockoutObservable<boolean>;

            //Declare wizard properties
            stepList: Array<NtsWizardStep>;
            stepSelected: KnockoutObservable<NtsWizardStep>;
            activeStep: KnockoutObservable<number>;
            
            monthResoult :any;
            dataClosure :any;
            constructor() {
                var self = this;
               // self.params.setParamsScreenA({ closure: self.closureID });
                self.params.setParamsScreenA({ closureID: 1 });
                //import cScreenModel, dScreenModel
                self.cScreenmodel = new nts.uk.at.view.kdw001.c.viewmodel.ScreenModel();
                self.dScreenmodel = new nts.uk.at.view.kdw001.d.viewmodel.ScreenModel();

                //Init checkBox
                self.dailyCreatedCheck = ko.observable(true);
                self.dailyCalCheck = ko.observable(true);
                self.approvalResultCheck = ko.observable(true);
                self.forciblyCommitCheck = ko.observable(false);
                self.monthCountCheck = ko.observable(true);


                // init enable 
                self.forciblyEnable = ko.observable(false);

                //   self.option1 = ko.observable({ value: 1, text: getText('KDW001_56') });
                //   self.option2 = ko.observable({ value: 2, text: getText('KDW001_57') });

                //Init for help button
                self.enable1 = ko.observable(true);

                //Init for switch button
                self.roundingRules5 = ko.observableArray([
                    { code: 0, name: getText('KDW001_69') },
                    { code: 1, name: getText('KDW001_70') }
                ]);
                self.roundingRules4 = ko.observableArray([
                    { code: 0, name: getText('KDW001_66') },
                    { code: 1, name: getText('KDW001_67') }
                ]);
                self.roundingRules3 = ko.observableArray([
                    { code: 0, name: getText('KDW001_64') },
                    { code: 1, name: getText('KDW001_65') }
                ]);
                self.roundingRules1 = ko.observableArray([
                    { code: 0, name: getText('KDW001_54') },
                    { code: 1, name: getText('KDW001_55') }
                ]);
                self.selectedRuleCode = ko.observable(0);

                //*7

                self.approvalResultCheck.subscribe((x) => {
                    if (x == true && self.selectedReflectClassCode() == 1) {
                        self.forciblyEnable(true);
                    } else {
                        self.forciblyEnable(false);
                    }
                });

                self.selectedReflectClassCode.subscribe((x) => {
                    if (x == 1 && self.approvalResultCheck() == true) {
                        self.forciblyEnable(true);
                    } else {
                        self.forciblyEnable(false);
                    }
                });

                //Init checkbox properties
                self.checked = ko.observable(true);
                self.enable = ko.observable(true);

                //Init wizard
                self.stepList = [
                    { content: '.step-1' },
                    { content: '.step-2' },
                    { content: '.step-3' }
                ];
                self.activeStep = ko.observable(0);

                self.activeStep.subscribe(newVal => {
                    if (newVal == 0) {
                        $('#hor-scroll-button-hide').hide();
                        _.defer(() => {
                            $('#hor-scroll-button-hide').show();
                        });
                    }
                })
            }
            
            getMonthlyResult(closureId : number){
                let self = this;
                let dfd = $.Deferred<void>();
                service.findMonthlyResult(closureId).done(function(data) {
                    self.monthResoult = data;
                    dfd.resolve();
                });
                return dfd.promise();
            }
            
            getDataClosure(closureId: number) {
                let self = this;
                let dfd = $.Deferred<void>();
                service.findDataClosure(closureId).done(function(data) {
                    self.dataClosure = data;
                    dfd.resolve();
                });
                return dfd.promise();
            }                    
               opendScreenD() {
                var self = this;

                if (self.dailyCreatedCheck() == false && self.dailyCalCheck() == false && self.approvalResultCheck() == false && self.monthCountCheck() == false) {
                    nts.uk.ui.dialog.alertError({ messageId: "Msg_205" });
                    return;
                }
                let dfdGetDataClosure = self.getDataClosure(self.params.closureID);
                let dfdGetMonthlyResult = self.getMonthlyResult(self.params.closureID);
                $.when(dfdGetDataClosure, dfdGetMonthlyResult).done((dfdGetDataClosureData, dfdGetMonthlyResultData) => {

                    let periodStartDate = moment.utc(self.params.periodStartDate, "YYYY/MM/DD");
                    let periodEndDate = moment.utc(self.params.periodEndDate, "YYYY/MM/DD");

                    let startClosure = moment.utc(self.dataClosure.startCal, "YYYY/MM/DD");
                    let endClosure = moment.utc(self.dataClosure.endCal, "YYYY/MM/DD");

                    let isDisplay = false;

                    if (self.dailyCreatedCheck() == true || self.dailyCalCheck() == true
                        || self.approvalResultCheck() == true) {
                        if (self.dataClosure.lockDay == true) {
                            if (periodStartDate <= endClosure && periodEndDate >= startClosure) {
                                isDisplay = true;
                            }
                        }
                    }
                    //lock monthly
                    if (self.monthCountCheck() == true) {
                        if (self.dataClosure.lockMonthy == true) {
                            isDisplay = true;
                        }
                    }


                    self.dScreenmodel.closureId(self.params.closureID);
                    self.dScreenmodel.isDisplayCheckbox(isDisplay);
                    self.dScreenmodel.ifCallScreenBToD(true);



                
                if (self.selectedCreatDivisionCode() == 1 || 
                    self.selectedCalDivisionCode() == 1  || 
                    self.selectedAggregateClassCode() == 1 ) {
                   if( (self.selectedCalDivisionCode() == 1 && self.dailyCalCheck()==true) || 
                       (self.selectedAggregateClassCode() == 1 && self.monthCountCheck()==true)){
                        
                    
                        nts.uk.ui.dialog.confirm({ messageId: "Msg_575" }).ifYes(() => {
                            self.params.setParamsScreenB({
                                dailyCreation: self.dailyCreatedCheck(),
                                creationType: self.dailyCreatedCheck() == true ? self.selectedCreatDivisionCode() : null,
                                dailyCalClass: self.dailyCalCheck(),
                                calClass: self.dailyCalCheck() == true ? self.selectedCalDivisionCode() : null,
                                refApprovalresult: self.approvalResultCheck(),
                                refClass: self.approvalResultCheck() == true ? self.selectedReflectClassCode() : null,
                                alsoForciblyReflectEvenIfItIsConfirmed: self.forciblyEnable() == true ? self.forciblyCommitCheck() : null,
                                monthlyAggregation: self.monthCountCheck(),
                                summaryClass: self.monthCountCheck() == true ? self.selectedAggregateClassCode() : null
                            });
                            
                            
    
                            //view Init value screen D
                            self.dScreenmodel.periodDate(getText('KDW001_34', [self.params.periodStartDate, self.params.periodEndDate]));
                            self.dScreenmodel.numberEmployee(getText('KDW001_21', [self.params.lstEmployeeID.length]));
                            
                            if (self.dailyCreatedCheck() == false && self.dailyCalCheck() == false && self.approvalResultCheck() == false && self.monthCountCheck() == true) {
                                self.dScreenmodel.showPeriodTime(false);
                            } else {
                                self.dScreenmodel.showPeriodTime(true);
                            }
    
                            if (self.dailyCreatedCheck()) {
                                var dailyCreatedText = getText('KDW001_9');
                                self.dScreenmodel.dailyCreated(dailyCreatedText);
                                self.dScreenmodel.dailyCreatedVisible(true);
                                if (self.selectedCreatDivisionCode() == 1) {
                                    self.dScreenmodel.dailyCreated(dailyCreatedText += '(' + getText('KDW001_55') + ')');
                                }
                            } else {
                                self.dScreenmodel.dailyCreated('');
                                self.dScreenmodel.dailyCreatedVisible(false);
                            }
    
                            if (self.dailyCalCheck()) {
                                var dailyCalText = getText('KDW001_10');
                                self.dScreenmodel.dailyCal(dailyCalText);
                                self.dScreenmodel.dailyCalVisible(true);
                                if (self.selectedCalDivisionCode() == 1) {
                                    self.dScreenmodel.dailyCal(dailyCalText += '(' + getText('KDW001_65') + ')');
                                }
                            } else {
                                self.dScreenmodel.dailyCal('');
                                self.dScreenmodel.dailyCalVisible(false);
                            }
    
                            if (self.approvalResultCheck()) {
                                var approvalResultText = getText('KDW001_11');
                                self.dScreenmodel.approvalResult(approvalResultText);
                                self.dScreenmodel.approvalVisible(true);
                                if (self.selectedReflectClassCode() == 1) {
                                    self.dScreenmodel.approvalResult(approvalResultText += '(' + getText('KDW001_67') + ')');
                                }
                            } else {
                                self.dScreenmodel.approvalResult('');
                                self.dScreenmodel.approvalVisible(false);
                            }
    
                            if (self.monthCountCheck()) {
                                var monthCountText = getText('KDW001_12');
                                self.dScreenmodel.monthCount(monthCountText);
                                self.dScreenmodel.monthCountVisible(true);
                                if (self.selectedAggregateClassCode() == 1) {
                                    self.dScreenmodel.monthCount(monthCountText += '(' + getText('KDW001_70') + ')');
                                }
    
                            } else {
                                self.dScreenmodel.monthCount('');
                                self.dScreenmodel.monthCountVisible(false);
                            }
    
                            $("#wizard").ntsWizard("next");
                        });
                       }else{
                            self.params.setParamsScreenB({
                                dailyCreation: self.dailyCreatedCheck(),
                                creationType: self.dailyCreatedCheck() == true ? self.selectedCreatDivisionCode() : null,
                                dailyCalClass: self.dailyCalCheck(),
                                calClass: self.dailyCalCheck() == true ? self.selectedCalDivisionCode() : null,
                                refApprovalresult: self.approvalResultCheck(),
                                refClass: self.approvalResultCheck() == true ? self.selectedReflectClassCode() : null,
                                alsoForciblyReflectEvenIfItIsConfirmed: self.forciblyEnable() == true ? self.forciblyCommitCheck() : null,
                                monthlyAggregation: self.monthCountCheck(),
                                summaryClass: self.monthCountCheck() == true ? self.selectedAggregateClassCode() : null
                            });
                            
                            
    
                            //view Init value screen D
                            self.dScreenmodel.periodDate(getText('KDW001_34', [self.params.periodStartDate, self.params.periodEndDate]));
                            self.dScreenmodel.numberEmployee(getText('KDW001_21', [self.params.lstEmployeeID.length]));
                   
                            if (self.dailyCreatedCheck() == false && self.dailyCalCheck() == false && self.approvalResultCheck() == false && self.monthCountCheck() == true) {
                                self.dScreenmodel.showPeriodTime(false);
                            } else {
                                self.dScreenmodel.showPeriodTime(true);   
                            }
    
                            if (self.dailyCreatedCheck()) {
                                var dailyCreatedText = getText('KDW001_9');
                                self.dScreenmodel.dailyCreated(dailyCreatedText);
                                self.dScreenmodel.dailyCreatedVisible(true);
                                if (self.selectedCreatDivisionCode() == 1) {
                                    self.dScreenmodel.dailyCreated(dailyCreatedText += '(' + getText('KDW001_55') + ')');
                                }
                            } else {
                                self.dScreenmodel.dailyCreated('');
                                self.dScreenmodel.dailyCreatedVisible(false);
                            }
    
                            if (self.dailyCalCheck()) {
                                var dailyCalText = getText('KDW001_10');
                                self.dScreenmodel.dailyCal(dailyCalText);
                                self.dScreenmodel.dailyCalVisible(true);
                                if (self.selectedCalDivisionCode() == 1) {
                                    self.dScreenmodel.dailyCal(dailyCalText += '(' + getText('KDW001_65') + ')');
                                }
                            } else {
                                self.dScreenmodel.dailyCal('');
                                self.dScreenmodel.dailyCalVisible(false);
                            }
    
                            if (self.approvalResultCheck()) {
                                var approvalResultText = getText('KDW001_11');
                                self.dScreenmodel.approvalResult(approvalResultText);
                                self.dScreenmodel.approvalVisible(true);
                                if (self.selectedReflectClassCode() == 1) {
                                    self.dScreenmodel.approvalResult(approvalResultText += '(' + getText('KDW001_67') + ')');
                                }
                            } else {
                                self.dScreenmodel.approvalResult('');
                                self.dScreenmodel.approvalVisible(false);
                            }
    
                            if (self.monthCountCheck()) {
                                var monthCountText = getText('KDW001_12');
                                self.dScreenmodel.monthCount(monthCountText);
                                self.dScreenmodel.monthCountVisible(true);
                                if (self.selectedAggregateClassCode() == 1) {
                                    self.dScreenmodel.monthCount(monthCountText += '(' + getText('KDW001_70') + ')');
                                }
    
                            } else {
                                self.dScreenmodel.monthCount('');
                                self.dScreenmodel.monthCountVisible(false);
                            }
    
                            $("#wizard").ntsWizard("next");
                           
                   }
                } else {
                    self.params.setParamsScreenB({
                        dailyCreation: self.dailyCreatedCheck(),
                        creationType: self.dailyCreatedCheck() == true ? self.selectedCreatDivisionCode() : null,
                        dailyCalClass: self.dailyCalCheck(),
                        calClass: self.dailyCalCheck() == true ? self.selectedCalDivisionCode() : null,
                        refApprovalresult: self.approvalResultCheck(),
                        refClass: self.approvalResultCheck() == true ? self.selectedReflectClassCode() : null,
                        alsoForciblyReflectEvenIfItIsConfirmed: self.forciblyEnable() == true ? self.forciblyCommitCheck() : null,
                        monthlyAggregation: self.monthCountCheck(),
                        summaryClass: self.monthCountCheck() == true ? self.selectedAggregateClassCode() : null
                    });

                    //view Init value screen D
                    self.dScreenmodel.periodDate(getText('KDW001_34', [self.params.periodStartDate, self.params.periodEndDate]));
                    self.dScreenmodel.numberEmployee(getText('KDW001_21', [self.params.lstEmployeeID.length]));
                             
                    if (self.dailyCreatedCheck() == false && self.dailyCalCheck() == false && self.approvalResultCheck() == false && self.monthCountCheck() == true) {
                        self.dScreenmodel.showPeriodTime(false);
                    } else {
                       self.dScreenmodel.showPeriodTime(true); 
                    }
                    
                    if (self.dailyCreatedCheck()) {
                        var dailyCreatedText = getText('KDW001_9');
                        self.dScreenmodel.dailyCreated(dailyCreatedText);
                        self.dScreenmodel.dailyCreatedVisible(true);
                        if (self.selectedCreatDivisionCode() == 1) {
                            self.dScreenmodel.dailyCreated(dailyCreatedText += '(' + getText('KDW001_55') + ')');
                        }
                    } else {
                        self.dScreenmodel.dailyCreated('');
                        self.dScreenmodel.dailyCreatedVisible(false);
                    }

                    if (self.dailyCalCheck()) {
                        var dailyCalText = getText('KDW001_10');
                        self.dScreenmodel.dailyCal(dailyCalText);
                        self.dScreenmodel.dailyCalVisible(true);
                        if (self.selectedCalDivisionCode() == 1) {
                            self.dScreenmodel.dailyCal(dailyCalText += '(' + getText('KDW001_65') + ')');
                        }
                    } else {
                        self.dScreenmodel.dailyCal('');
                        self.dScreenmodel.dailyCalVisible(false);
                    }

                    if (self.approvalResultCheck()) {
                        var approvalResultText = getText('KDW001_11');
                        self.dScreenmodel.approvalResult(approvalResultText);
                        self.dScreenmodel.approvalVisible(true);
                        if (self.selectedReflectClassCode() == 1) {
                            self.dScreenmodel.approvalResult(approvalResultText += '(' + getText('KDW001_67') + ')');
                        }
                    } else {
                        self.dScreenmodel.approvalResult('');
                        self.dScreenmodel.approvalVisible(false);
                    }

                    if (self.monthCountCheck()) {
                        var monthCountText = getText('KDW001_12');
                        self.dScreenmodel.monthCount(monthCountText);
                        self.dScreenmodel.monthCountVisible(true);
                        if (self.selectedAggregateClassCode() == 1) {
                            self.dScreenmodel.monthCount(monthCountText += '(' + getText('KDW001_70') + ')');
                        }

                    } else {
                        self.dScreenmodel.monthCount('');
                        self.dScreenmodel.monthCountVisible(false);
                    }

                    if (!self.dailyCreatedCheck() && !self.dailyCalCheck() && !self.approvalResultCheck() && self.monthCountCheck()) {
                      self.dScreenmodel.isCreatingFutureDay(true);
                      $("#wizard").ntsWizard("next");
                      $('#button113').focus();
                    } else {
                      self.isCreatingFutureDay().then(result => {
                        self.dScreenmodel.isCreatingFutureDay(result);
                        $("#wizard").ntsWizard("next");
                        $('#button113').focus();
                      });
                    }
                }
                    self.params.startMonthResult = self.monthResoult.startMonth;
                    self.params.endMonthResult = self.monthResoult.endMonth;
                });


            }

            private isCreatingFutureDay(): JQueryPromise<any> {
              const vm = this;
              const param = {
                endDate: moment.utc(vm.params.periodEndDate, "YYYY/MM/DD")
              };
              return service.isCreatingFutureDay(param);
            }

            opendScreenC() {
                $("#wizard").ntsWizard("prev");
            }

            navigateView() {
                nts.uk.request.jump("/view/kdw/001/a/index.xhtml");
            }
        }
    }

    class RecreateDevisionModel {
        id: number;
        name: string;
        constructor(id, name) {
            var self = this;
            self.id = id;
            self.name = name;
        }
    }
}
