module nts.uk.at.view.kml002.l {
    import setShare = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    const Paths = {
        GET_EMPLOYMENT_USEAGE_SETTING:"ctx/at/schedule/budget/employmentsetting/getUsage",
        REGISTER_EMPLOYMENT_USEAGE_SETTING:"ctx/at/schedule/budget/employmentsetting/registerUsage"
    };
   
    @bean()
    class Kml002lViewModel extends ko.ViewModel {
        isUseageEmployment: KnockoutObservable<boolean> = ko.observable(true);
        constructor() {
            super();
            const self = this;      
            self.loadData();            
        }

        loadData(): void {
            const self = this;            
            self.$blockui("invisible");
            self.$ajax(Paths.GET_EMPLOYMENT_USEAGE_SETTING).done((data: any) => {
                if(data){
                    data.employmentUse == 1 ? self.isUseageEmployment(true) : self.isUseageEmployment(false);      
                }  
                $('[type=checkbox]:first').focus();
            }).always(() => {
                self.$blockui("hide");
            });
        }       

        public register(): void {
            const self = this;
            self.$blockui("invisible");

            let command: any = {
                employmentUse: self.isUseageEmployment() ? 1 : 0
            }
            self.$ajax(Paths.REGISTER_EMPLOYMENT_USEAGE_SETTING, command).done(() => {
                self.$dialog.info({ messageId: 'Msg_15' }).then(() => {
                    self.closeDialog();
                });
            }).fail((res) => {
                if (res.messageId == 'Msg_3' || res.messageId == 'Msg_1971') {
                }
            }).always(() => {
                self.$blockui("hide");
            });
        }

        closeDialog(): void {
            const self = this;           
            self.$window.close();
        }        
    }  
}
