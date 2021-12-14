module nts.uk.at.view.kal002.b.viewmodel {
    import block = nts.uk.ui.block;
    export class ScreenModel {

        MailAutoAndNormalDto: MailAutoAndNormalDto;
        
        constructor() {
            var self = this;
        }

        startPage(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
            var MailSettingsDefault = ({subject : "", text : "", mailAddressCC : [], mailAddressBCC : [], mailRely : ""});
            block.grayout();
            new service.Service().getAllMailSet().done(function(data: MailAutoAndNormalDto) {
                //console.timeEnd("StartServer");
                //console.log(data);
                if(data){
                    data.mailSettingNormalDto.mailSettings = data.mailSettingNormalDto.mailSettings == null? MailSettingsDefault: data.mailSettingNormalDto.mailSettings;
                    data.mailSettingNormalDto.mailSettingAdmins = data.mailSettingNormalDto.mailSettingAdmins == null? MailSettingsDefault: data.mailSettingNormalDto.mailSettingAdmins;
                    data.mailSettingAutomaticDto.mailSettings = data.mailSettingAutomaticDto.mailSettings == null? MailSettingsDefault: data.mailSettingAutomaticDto.mailSettings;
                    data.mailSettingAutomaticDto.mailSettingAdmins = data.mailSettingAutomaticDto.mailSettingAdmins == null? MailSettingsDefault: data.mailSettingAutomaticDto.mailSettingAdmins;
                        
                    self.MailAutoAndNormalDto = data;
                }
                dfd.resolve();
                 
            }).always(() => {block.clear();});
            return dfd.promise();
        }

        setMailNormal(){
            var self = this;
            nts.uk.ui.windows.setShared("sendingAddressCheck", false);
            self.setPara();
            nts.uk.ui.windows.setShared("MailSettings", self.MailAutoAndNormalDto.mailSettingNormalDto.mailSettings);
            nts.uk.ui.windows.sub.modal("com","view/ccg/027/a/index.xhtml").onClosed(() => {
                let data = nts.uk.ui.windows.getShared("MailSettings");
                if (data != null) {
                    //console.log(data);
                    self.MailAutoAndNormalDto.mailSettingNormalDto.mailSettings = data;
                }
            });
        }
        
        setMailNormalAd(){
            var self = this;
            nts.uk.ui.windows.setShared("sendingAddressCheck", false);
            self.setPara();
            nts.uk.ui.windows.setShared("MailSettings", self.MailAutoAndNormalDto.mailSettingNormalDto.mailSettingAdmins);
            nts.uk.ui.windows.sub.modal("com","view/ccg/027/a/index.xhtml").onClosed(() => {
                let data = nts.uk.ui.windows.getShared("MailSettings");
                if (data != null) {
                    self.MailAutoAndNormalDto.mailSettingNormalDto.mailSettingAdmins = data;
                }
            });
        }
        
        setMailAuto(){
            var self = this;
            nts.uk.ui.windows.setShared("sendingAddressCheck", true);
            self.setPara();
            nts.uk.ui.windows.setShared("senderAddress", self.MailAutoAndNormalDto.mailSettingAutomaticDto.senderAddress)
            nts.uk.ui.windows.setShared("MailSettings", self.MailAutoAndNormalDto.mailSettingAutomaticDto.mailSettings);
            nts.uk.ui.windows.sub.modal("com","view/ccg/027/a/index.xhtml").onClosed(() => {
                let data = nts.uk.ui.windows.getShared("MailSettings");
                if (data != null) {
                    self.MailAutoAndNormalDto.mailSettingAutomaticDto.mailSettings = data;
                    self.MailAutoAndNormalDto.mailSettingAutomaticDto.senderAddress = nts.uk.ui.windows.getShared("senderAddress");
                }
            });
        }
        
        setMailAutoAd(){
            var self = this;
            nts.uk.ui.windows.setShared("sendingAddressCheck", true);
            self.setPara();
            nts.uk.ui.windows.setShared("senderAddress", self.MailAutoAndNormalDto.mailSettingAutomaticDto.senderAddress)
            nts.uk.ui.windows.setShared("MailSettings", self.MailAutoAndNormalDto.mailSettingAutomaticDto.mailSettingAdmins);
            nts.uk.ui.windows.sub.modal("com","view/ccg/027/a/index.xhtml").onClosed(() => {
                let data = nts.uk.ui.windows.getShared("MailSettings");
                if (data != null) {
                    self.MailAutoAndNormalDto.mailSettingAutomaticDto.mailSettingAdmins = data;
                    self.MailAutoAndNormalDto.mailSettingAutomaticDto.senderAddress = nts.uk.ui.windows.getShared("senderAddress");
                    //console.log(self.MailAutoAndNormalDto);
                }
            });
        }
        
        setPara(){
            nts.uk.ui.windows.setShared("SetCC", true);
            nts.uk.ui.windows.setShared("SetBCC", true);
            nts.uk.ui.windows.setShared("SetReply", true);
            nts.uk.ui.windows.setShared("SetSubject", true);
            nts.uk.ui.windows.setShared("SetBody", true);
            nts.uk.ui.windows.setShared("wording", "");
                    
        }
        
        Registration(){
            var self = this;
            var dfd = $.Deferred();
            var dto = self.MailAutoAndNormalDto;
            block.grayout();
            var mailSettingNormal = new MailSettingNormalCommand(new MailSettingsCommand(dto.mailSettingNormalDto.mailSettings), 
                new MailSettingsCommand(dto.mailSettingNormalDto.mailSettingAdmins));
            
            var mailSettingAutomatic = new MailSettingAutomaticCommand(new MailSettingsCommand(dto.mailSettingAutomaticDto.mailSettings), 
                new MailSettingsCommand(dto.mailSettingAutomaticDto.mailSettingAdmins),dto.mailSettingAutomaticDto.senderAddress);
            
            var command = new MailAutoAndNormalCommand(mailSettingAutomatic, mailSettingNormal);
            //console.log(command);
            new service.Service().addMailSet(command).done(function() {
                nts.uk.ui.dialog.info({ messageId: "Msg_15" });
            }).always(()=>{block.clear();});
            dfd.resolve(); 
            return dfd.promise();
        }
        
    }
    
    export interface MailSettingsDto {
        subject?: string;
        text?: string;
        mailAddressCC: Array<string>;
        mailAddressBCC: Array<string>;
        mailRely?: string;
    }
    export interface MailSettingNormalDto {
        mailSettings?: MailSettingsDto;
        mailSettingAdmins?: MailSettingsDto;
    }
    export interface MailSettingAutomaticDto {
        mailSettings?: MailSettingsDto;
        mailSettingAdmins?: MailSettingsDto;
        senderAddress?: string;
    }
    export interface MailAutoAndNormalDto {
        mailSettingAutomaticDto: MailSettingAutomaticDto;
        mailSettingNormalDto: MailSettingNormalDto;
    }
    
    class MailSettingsCommand {
        subject: string;
        text: string;
        mailAddressCC: Array<string>;
        mailAddressBCC: Array<string>;
        mailRely: string;
        constructor(mailSettingsDto : MailSettingsDto) {
            this.subject = mailSettingsDto.subject;
            this.text = mailSettingsDto.text;
            this.mailAddressCC = mailSettingsDto.mailAddressCC;
            this.mailAddressBCC = mailSettingsDto.mailAddressBCC;
            this.mailRely = mailSettingsDto.mailRely;
        }
    }
    
    class MailSettingNormalCommand {
        mailSettings: MailSettingsCommand;
        mailSettingAdmins: MailSettingsCommand;
       
        constructor(mailSettings : MailSettingsCommand, mailSettingAdmins: MailSettingsCommand) {
            this.mailSettings = mailSettings;
            this.mailSettingAdmins = mailSettingAdmins;
        }
    }
    
    class MailSettingAutomaticCommand {
        mailSettings: MailSettingsCommand;
        mailSettingAdmins: MailSettingsCommand;
        senderAddress: string;
       
        constructor(mailSettings : MailSettingsCommand, mailSettingAdmins: MailSettingsCommand, senderAddress: string) {
            this.mailSettings = mailSettings;
            this.mailSettingAdmins = mailSettingAdmins;
            this.senderAddress =  senderAddress;
        }
    }
    
    class MailAutoAndNormalCommand {
        mailSettingAutomatic: MailSettingAutomaticCommand;
        mailSettingNormal: MailSettingNormalCommand;
       
        constructor(mailSettingAutomatic: MailSettingAutomaticCommand, mailSettingNormal: MailSettingNormalCommand) {
            this.mailSettingAutomatic = mailSettingAutomatic;
            this.mailSettingNormal = mailSettingNormal;
        }
    }
    

}

