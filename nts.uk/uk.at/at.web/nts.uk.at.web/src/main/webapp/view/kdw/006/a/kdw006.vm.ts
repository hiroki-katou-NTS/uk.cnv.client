module nts.uk.at.view.kdw006 {
    export module viewmodel {
        export class ScreenModel extends ko.ViewModel {

            ootsuka: KnockoutObservable<boolean>;
            formatPerformanceDto: KnockoutObservable<FormatPerformanceDto>;

            constructor(dataShare: any) {
                super();

                var self = this;
                self.ootsuka = ko.observable(false);

                self.formatPerformanceDto = ko.observable(new FormatPerformanceDto({
                    cid: '',
                    settingUnitType: 0,
                }));
            }

            startPage(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                self.$blockui("grayout");
                $.when(self.start(), self.getFormat()).done(() => {
                    dfd.resolve();
                }).always(() => {
                    nts.uk.ui.errors.clearAll();
                    self.$blockui("hide");
                });
                return dfd.promise();
            }

            start(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                service.start().done(function(data) {
                    if (data) {
                        self.ootsuka(data);
                        dfd.resolve();
                    } else {
                        dfd.resolve();
                    }
                });
                return dfd.promise();
            }

            getFormat(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                service.getFormat().done(function(data) {
                    if (data) {
                        self.formatPerformanceDto(new FormatPerformanceDto(data));
                        dfd.resolve();
                    } else {
                        dfd.resolve();
                    }
                });
                return dfd.promise();
            }

            //---common---
            openC() {
                nts.uk.request.jump("/view/kdw/006/c/index.xhtml");
            }

            openD() {
                nts.uk.request.jump("/view/kdw/006/d/index.xhtml");
            }

            openJ() {
                nts.uk.request.jump("/view/kdw/006/j/index.xhtml");
            }

            openK() {
                nts.uk.request.jump("/view/kdw/006/k/index.xhtml");
            }

            //---daily---
            open002Control() {
                let isDaily = true;
                nts.uk.request.jump("/view/kdw/002/a/index.xhtml", { ShareObject: isDaily  });
            }

            open002Setting() {
                let isDaily = true;
                nts.uk.request.jump("/view/kdw/002/c/index.xhtml", { ShareObject: isDaily  });
            }

            open008() {
                var self = this;
                
                let isDaily = true;
                if (self.formatPerformanceDto().settingUnitType() == SettingUnitType.AUTHORITY) {
                    nts.uk.request.jump("/view/kdw/008/a/index.xhtml", { ShareObject: isDaily  });
                } else {
                    nts.uk.request.jump("/view/kdw/008/b/index.xhtml", { ShareObject: isDaily  });
                }
            }

            openG() {
                nts.uk.request.jump("/view/kdw/006/g/index.xhtml");
            }

            open007() {
                let isDaily = true;
                nts.uk.request.jump("/view/kdw/007/a/index.xhtml", { ShareObject: isDaily });
            }

            openI() {
                nts.uk.request.jump("/view/kdw/006/i/index.xhtml");
            }

            //---monthly---
            open002ControlMonth() {
                let isDaily = false;
                nts.uk.request.jump("/view/kdw/002/a/index.xhtml", { ShareObject: isDaily  });
            }

            open002SettingMonth() {
                let isDaily = false;
                nts.uk.request.jump("/view/kdw/002/c/index.xhtml", { ShareObject: isDaily  });
            }

            open008Month() {
                var self = this;
                
                let isDaily = false;
                if (self.formatPerformanceDto().settingUnitType() == SettingUnitType.AUTHORITY) {
                    nts.uk.request.jump("/view/kdw/008/a/index.xhtml", { ShareObject: isDaily  });
                } else {
                    nts.uk.request.jump("/view/kdw/008/b/index.xhtml", { ShareObject: isDaily  });
                }
            }

            exportExcel(): void {
                var self = this;
                self.$blockui('grayout');
                let langId = "ja";
                service.saveAsExcelCommon(langId).done(function() {
                }).fail(function(error) {
                    self.$dialog.alert({ messageId: error.messageId });
                }).always(function() {
                    self.$blockui('hide');
                });
            }
        }

        enum SettingUnitType {
            AUTHORITY,
            BUSINESS_TYPE
        }

        interface IFormatPerformanceDto {
            cid: string;
            settingUnitType: number;
        }
        
        class FormatPerformanceDto {
            cid: KnockoutObservable<string>;
            settingUnitType: KnockoutObservable<number>;
    
            constructor(param: IFormatPerformanceDto) {
                let self = this;
                self.cid = ko.observable(param.cid);
                self.settingUnitType = ko.observable(param.settingUnitType);
            }
        }
    }
}
