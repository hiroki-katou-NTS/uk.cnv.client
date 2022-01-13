module nts.uk.at.view.ksu001.k.c {
    import getText = nts.uk.resource.getText;
    import setShare = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;

    const Paths = {        
        COPY_SCHEDULE_TABLE_OUTPUT_SETTING:"ctx/at/schedule/scheduletable/copy"
    };

    @bean()
    class Ksu005cViewModel extends ko.ViewModel {
        currentScreen: any = null;
        copySourceCode: KnockoutObservable<string> = ko.observable('');
        copySourceName: KnockoutObservable<string> = ko.observable('');
        newCode:KnockoutObservable<string> = ko.observable('');
        newName:KnockoutObservable<string> = ko.observable('');
      
        constructor(params: any) {
            super();
            const self = this;            
            self.loadData(params);
        }

        mounted() {
            $('#outputSettingCopyCode').focus(); 
        }

        loadData(data: any): void {
            const self = this;
            self.copySourceCode(data.copySourceCode);
            self.copySourceName(data.copySourceName);
        }

        copy(): void {
            const self = this;                                
            if (self.validateAll()) {
                return;
            }

            let request: any = {
				copySourceCode : self.copySourceCode(),
                newCode : self.newCode(),
                newName: self.newName()
			}
            if(self.newCode() == self.copySourceCode()){
                self.$dialog.error({messageId: 'Msg_355'});
                return;
            }
            self.$blockui('invisible');  
            self.$ajax(Paths.COPY_SCHEDULE_TABLE_OUTPUT_SETTING, request).done(() => {
                self.$dialog.info({messageId: "Msg_15"}).then(function() {
                    self.$window.close(request.newCode);
                });  
            }).fail((res) => {
                if(res.messageId == 'Msg_3')                    
                    $('#outputSettingCopyCode').ntsError('set',{messageId: res.messageId});
            }).always(() => {
                self.$blockui('hide');
            });
            self.$blockui('hide');
        }

        private validateAll(): boolean {
            $('#outputSettingCopyCode').ntsEditor('validate');
            $('#outputSettingCopyName').ntsEditor('validate');         
            if (nts.uk.ui.errors.hasError()) {                    
                return true;
            }
            return false;
        }

        closeDialog(): void {
            const vm = this;
            vm.$window.close();
        }
    }
}