module nts.uk.com.view.cmm050.a {
    import MailServerFindDto = model.MailServerDto;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import blockUI = nts.uk.ui.block;
    import errors = nts.uk.ui.errors;
    
    export module viewmodel {
        export class ScreenModel {
            useServerArray: KnockoutObservableArray<any>;
            authMethodArray: KnockoutObservable<Array<any>>;
            encryptionMethodArray: KnockoutObservableArray<any>;
            
            authMethodEnable: KnockoutObservable<boolean>;
            
            havePopSetting: KnockoutObservable<boolean>;
            haveImapSetting: KnockoutObservable<boolean>;
            haveEncryptMethod: KnockoutObservable<boolean>;
            
            computedText: KnockoutComputed<string>;
            
            //common info
            emailAuth: KnockoutObservable<string>;
            authMethod: KnockoutObservable<number>;
            password: KnockoutObservable<string>;
            encryptionMethod: KnockoutObservable<number>;
            useSmtpAuthen: KnockoutObservable<boolean>
            
            //smtp info
            smtpPort: KnockoutObservable<number>;
            smtpServer: KnockoutObservable<string>;
                        
            //imap info
            imapPort: KnockoutObservable<number>;
            imapUseServer: KnockoutObservable<number>;
            imapServer: KnockoutObservable<string>;
            
            imapServerEnable: KnockoutObservable<boolean>;
            
            //pop info
            popPort: KnockoutObservable<number>;
            popUseServer: KnockoutObservable<number>;
            popServer: KnockoutObservable<string>;
            
            popServerEnable: KnockoutObservable<boolean>;
            
            isEnableButtonTest: KnockoutObservable<boolean>;

            mailServerData: MailServerFindDto;

            constructor(){
                let _self = this;

                _self.mailServerData = null;
                
                _self.isEnableButtonTest = ko.observable(false);
                
                _self.useServerArray = ko.observableArray([
                    { value: 1, name: nts.uk.resource.getText("CMM050_7") },
                    { value: 0, name: nts.uk.resource.getText("CMM050_8") }
                ]);
                _self.authMethodArray = ko.observable(__viewContext.enums.AuthenticationMethod);
                 _self.encryptionMethodArray = ko.observableArray(__viewContext.enums.EncryptionMethod);
                
                _self.authMethodEnable = ko.observable(false);
                
                _self.havePopSetting = ko.observable(false);
                _self.haveImapSetting = ko.observable(false);
                _self.haveEncryptMethod = ko.observable(false);
                
                _self.emailAuth = ko.observable("");
                _self.authMethod = ko.observable(0);
                _self.password = ko.observable("");
                _self.encryptionMethod = ko.observable(0);
                _self.useSmtpAuthen = ko.observable(false);
                
                _self.smtpPort = ko.observable(null);
                _self.smtpServer = ko.observable("");
                
                _self.imapPort = ko.observable(143);
                _self.imapUseServer = ko.observable(0);
                _self.imapServer = ko.observable("");
                
                _self.popPort = ko.observable(110);
                _self.popUseServer = ko.observable(0);
                _self.popServer = ko.observable("");
                
                _self.imapServerEnable = ko.observable(false);
                _self.popServerEnable = ko.observable(false);
                
                _self.computedText = ko.computed(function() {
                    if(_self.useSmtpAuthen()){
                       return nts.uk.resource.getText("CMM050_13", [587]);
                    }else{
                       return nts.uk.resource.getText("CMM050_13", [25]);
                    } 
                });
                
                //handle when value have been changed
                _self.useSmtpAuthen.subscribe(value => {
                    _self.hasError();
                    if(value){
                        _self.authMethodEnable(true);
//                        _self.smtpPort(25);
                        _self.fillUI(_self.authMethod());
                    }else{
//                       _self.smtpPort(587);
                       _self.authMethodEnable(false);
                       _self.havePopSetting(false);
                       _self.haveImapSetting(false);
                       _self.haveEncryptMethod(false);
                    }
                }); 
                
                //handle when value have been changed
                _self.authMethod.subscribe(function(authMethodChanged){
                     errors.clearAll();
                    _self.fillUI(authMethodChanged);
                });
                
                //handle when value have been changed
                _self.imapUseServer.subscribe(function(imapUseServerChanged){
                    errors.clearAll();
                    if(imapUseServerChanged == ImapUseServer.USE){
                       _self.imapServerEnable(true);
                    }else{
                       _self.imapServerEnable(false);
                    }
                });
                
                //handle when value have been changed
                _self.popUseServer.subscribe(function(popUseServerChanged){
                    errors.clearAll();
                    if(popUseServerChanged == PopUseServer.USE){
                       _self.popServerEnable(true);
                    }else{
                       _self.popServerEnable(false);
                    }
                });
                 
                //handle when value have been changed
                _self.emailAuth.subscribe(function(emailString){
                   if(emailString.trim().length <= 0){
                        _self.emailAuth(emailString.trim());
                    }
                });
                _self.password.subscribe(function(pass){
                   if(pass.trim().length <= 0){
                        _self.password(pass.trim());
                    }
                });
                
                //handle when value have been changed
                _self.popServer.subscribe(function(popServer){
                   if(popServer.trim().length <= 0){
                        _self.popServer(popServer.trim());
                    }
                });

                _self.imapServer.subscribe(function(imapServer){
                   if(imapServer.trim().length <= 0){
                        _self.imapServer(imapServer.trim());
                    }
                });
                
                //handle when value have been changed
                _self.smtpServer.subscribe(function(smtpServer){
                   if(smtpServer.trim().length <= 0){
                        _self.smtpServer(smtpServer.trim());
                    }
                });
            }
            
            /**
             * Register Mail server setting
             */
            public registerMailSetting(): JQueryPromise<any> {
                blockUI.invisible();
                let _self = this;
                
                $(".nts-input").ntsError("validate");
                if (nts.uk.ui.errors.hasError()) {
                    blockUI.clear();
                    return;
                }
                // Validate
                if (_self.hasError()) {
                    _self.validateDomain();
                    blockUI.clear();
                    return;
                }
                
                var dfd = $.Deferred<void>();
                
                //prepare data
                if(!_.isNumber(_self.imapPort())){
                    _self.imapPort(_self.mailServerData.imapDto.imapPort)
                }
                if(!_.isNumber(_self.popPort())){
                    _self.popPort(_self.mailServerData.popDto.popPort)
                }
                
                var params = new model.MailServerDto(
                        _self.useSmtpAuthen() ? 1 : 0,
                        _self.encryptionMethod(),
                        _self.authMethod(),
                        _self.emailAuth(),
                        _self.password(),
                        new model.SmtpInfoDto( _self.smtpServer(), _self.smtpPort()),
                        new model.PopInfoDto(_self.popServer(), _self.popUseServer(), _self.popPort()),
                        new model.ImapInfoDto(_self.imapServer(), _self.imapUseServer(), _self.imapPort())
                    );
                blockUI.grayout();
                _self.saveMailServerSetting(params).done(function(){
                    
                    dfd.resolve();
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => { 
                        _self.startPage().done(function(){});
                        $('#A8_6').focus();
                    });
                }).fail(function(){
                    alert('error');    
                }).always(()=>blockUI.clear());
              
                return dfd.promise();
            }
            
            private checkToUseOlDataOrNewData() {
                
            }

            private validateDomain() {
              const _self = this;
              // validate input pop info
              if (_self.useSmtpAuthen() && _self.authMethod() == AuthenticationMethod.POP_BEFORE_SMTP) {
                if (nts.uk.text.isNullOrEmpty(_self.popPort())) {
                  nts.uk.ui.dialog.alertError({ messageId: "Msg_542" });
                }
                if (_self.popUseServer() == UseServer.USE && nts.uk.text.isNullOrEmpty(_self.popServer())) {
                  nts.uk.ui.dialog.alertError({ messageId: "Msg_543" });
                }
              }

              // validate input imap info
              if (_self.useSmtpAuthen() && _self.authMethod() == AuthenticationMethod.IMAP_BEFORE_SMTP) {
                if (nts.uk.text.isNullOrEmpty(_self.imapPort())) {
                  nts.uk.ui.dialog.alertError({ messageId: "Msg_544" });
                }
                if (_self.imapUseServer() == UseServer.USE && nts.uk.text.isNullOrEmpty(_self.imapServer())) {
                  nts.uk.ui.dialog.alertError({ messageId: "Msg_545" });
                }
              }
            }
            
            /**
             * Show dialog test
             */
            public showDialogTest() {
                let _self = this;
                // Validate
                if (nts.uk.text.isNullOrEmpty(_self.emailAuth())) {
                    nts.uk.ui.dialog.alert({ messageId: "Msg_533" });
                    return;
                }
                
                // Validate
                if (_self.hasError()) {
                    return;
                }

                //prepare data
                if(!_.isNumber(_self.imapPort())){
                  _self.imapPort(_self.mailServerData.imapDto.imapPort)
                }
                if(!_.isNumber(_self.popPort())){
                    _self.popPort(_self.mailServerData.popDto.popPort)
                }

                // テスト送信前チェック
                const param = new model.MailServerDto(
                  _self.useSmtpAuthen() ? 1 : 0,
                  _self.encryptionMethod(),
                  _self.authMethod(),
                  _self.emailAuth(),
                  _self.password(),
                  new model.SmtpInfoDto( _self.smtpServer(), _self.smtpPort()),
                  new model.PopInfoDto(_self.popServer(), _self.popUseServer(), _self.popPort()),
                  new model.ImapInfoDto(_self.imapServer(), _self.imapUseServer(), _self.imapPort())
                );
                service.checkDataChanged(param).then(result => {
                  // 変更された項目はない
                  if (!result) {
                    // 終了状態：処理実行
                    _self.proceedOpenDialogB();
                  } else {
                    // 確認メッセージ（ID：#Msg_2325）を表示
                    nts.uk.ui.dialog.confirm({ messageId: "Msg_2325" })
                    // 終了状態：処理中断
                    .ifNo(() => {})
                    .ifYes(() => {
                      // 「A：登録時チェック処理」を実行
                      _self.validateDomain();
                      if (!nts.uk.ui.errors.hasError()) {
                        // ドメインモデル「メールサーバ」を登録する
                        _self.saveMailServerSetting(param).then(() => {
                          nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => _self.proceedOpenDialogB());
                        });
                      }
                    });
                  }
                })
            }

            private proceedOpenDialogB() {
              const _self = this;
              setShared('CMM050Params', {
                emailAuth: _self.emailAuth(),
              }, true);
              nts.uk.ui.windows.sub.modal("/view/cmm/050/b/index.xhtml").onClosed(function() {
                  
              });
            }
            
            /**
             * Start Page
             */
            public startPage(): JQueryPromise<void> {
                var dfd = $.Deferred<void>();
                let _self = this;
                
                _self.loadMailServerSetting().done(function(data: MailServerFindDto){

                    _self.mailServerData = data;

                    //check visible
                    if (data.useAuth == UseServer.USE){
                        if (data.authMethod == AuthenticationMethod.POP_BEFORE_SMTP && data.popDto.popUseServer == PopUseServer.USE){
                              _self.popServerEnable(true);
                        }else if(data.authMethod == AuthenticationMethod.IMAP_BEFORE_SMTP && data.imapDto.imapUseServer == ImapUseServer.USE){
                              _self.imapServerEnable(true);
                        }
                        _self.fillUI(data.authMethod);
                    }else{
                        _self.fillUI(99); 
                    }          
                    dfd.resolve();
                });
                
                return dfd.promise();
            }
            
            
            /**
             * init UI
             */
            private fillUI(authMethodChanged): void {
                let _self = this;
                switch(authMethodChanged){
                    case AuthenticationMethod.POP_BEFORE_SMTP:
                        _self.haveEncryptMethod(false);
                        _self.havePopSetting(true);
                        _self.haveImapSetting(false);
                        break;
                    case AuthenticationMethod.IMAP_BEFORE_SMTP:
                        _self.haveEncryptMethod(false);
                        _self.havePopSetting(false);
                        _self.haveImapSetting(true);
                        break;
                    case AuthenticationMethod.SMTP_AUTH_LOGIN:
                        _self.haveEncryptMethod(true);
                        _self.havePopSetting(false);
                        _self.haveImapSetting(false);
                        break;
                    case AuthenticationMethod.SMTP_AUTH_PLAIN:
                        _self.haveEncryptMethod(true);
                        _self.havePopSetting(false);
                        _self.haveImapSetting(false);
                        break;
                    case AuthenticationMethod.SMTP_AUTH_CRAM_MD5:
                        _self.haveEncryptMethod(false);
                        _self.havePopSetting(false);
                        _self.haveImapSetting(false);
                        break;
                    default:
                        _self.haveEncryptMethod(false);
                        _self.havePopSetting(false);
                        _self.haveImapSetting(false);
                        break;
                }    
            } 
            
            /**
             * Check Errors all input.
             */
            private hasError(): boolean {
                let _self = this;
                _self.clearErrors();
                $('#A2_2').ntsEditor("validate");
                if (_self.useSmtpAuthen()){
                    $('#A5_2').ntsEditor("validate");
                }
                $('#A8_3').ntsEditor("validate");
                $('#A8_6').ntsEditor("validate");
                 // validate input pop info
                if(_self.useSmtpAuthen() && _self.authMethod() == AuthenticationMethod.POP_BEFORE_SMTP){
                     $('#A9_3').ntsEditor("validate");
                   
                    if(_self.popUseServer() == UseServer.USE && nts.uk.text.isNullOrEmpty(_self.popServer())){
                         $('#A9_10').ntsEditor("validate");
                    }
                }
                
                // validate input imap info
                if(_self.useSmtpAuthen() && _self.authMethod() == AuthenticationMethod.IMAP_BEFORE_SMTP){
                    $('#A10_3').ntsEditor("validate");
                    if(_self.imapUseServer() == UseServer.USE && nts.uk.text.isNullOrEmpty(_self.imapServer())){
                        $('#A10_10').ntsEditor("validate");
                    }
                }
                if ($('.nts-input').ntsError('hasError')) {
                    return true;
                }
                return false;
            }

            /**
             * Clear Errors
             */
            private clearErrors(): void {
    
                 // Clear errors
                $('#email_auth').ntsEditor("clear");
                $('#password').ntsEditor("clear");
                $('#smtp_port').ntsEditor("clear");
                $('#smtp_server').ntsEditor("clear");
                $('#imap_port').ntsEditor("clear");
                $('#imap_server').ntsEditor("clear");
                $('#pop_port').ntsEditor("clear");
                $('#pop_server').ntsEditor("clear");
                // Clear error inputs
                $('.nts-input').ntsError('clear');
            }
            
            /**
             * Get mail server setting
             */
            private loadMailServerSetting(): JQueryPromise<any>{
                var dfd = $.Deferred<MailServerFindDto>();
                var _self = this;
                
                service.findMailServerSetting().done(function(data: MailServerFindDto){
                    if (data === undefined){
                         _self.isEnableButtonTest(false);
                         let data = new model.MailServerDto(
                                        _self.useSmtpAuthen() ? 1 : 0,
                                        _self.encryptionMethod(),
                                        _self.authMethod(),
                                        _self.emailAuth(),
                                        _self.password(),
                                        new model.SmtpInfoDto(_self.smtpServer(), _self.smtpPort()),
                                        new model.PopInfoDto(_self.popServer(), _self.popUseServer(), _self.popPort()),
                                        new model.ImapInfoDto(_self.imapServer(), _self.imapUseServer(), _self.imapPort())
                                        );
                        dfd.resolve(data);
                    }else {
                        //set common mail server setting data
                        _self.isEnableButtonTest(true);
                        _self.emailAuth(data.emailAuthencation);
                        _self.useSmtpAuthen(data.useAuth === 1);
                        _self.authMethod(data.authMethod);
                        _self.password(data.password);
                        _self.encryptionMethod(data.encryptionMethod);
                        
                        // set smtp info data
                        _self.smtpPort(data.smtpDto.smtpPort);
                        _self.smtpServer(data.smtpDto.smtpServer);
                        
                        // set pop info data
                        _self.popPort(data.popDto.popPort);
                        _self.popUseServer(data.popDto.popUseServer);
                        _self.popServer(data.popDto.popServer);
                        
                        // set imap info data
                        _self.imapPort(data.imapDto.imapPort);
                        _self.imapUseServer(data.imapDto.imapUseServer);
                        _self.imapServer(data.imapDto.imapServer);
                        
                        dfd.resolve(data);    
                    }
                     
                }).fail(function(){
                    alert("error")
                });
                
                return dfd.promise();
            }
            
            private saveMailServerSetting(params: MailServerFindDto): JQueryPromise<any>{
                var dfd = $.Deferred<MailServerFindDto>();
                let _self = this;
                
                service.registerMailServerSetting(params).done(function(){
                    dfd.resolve();
                }).fail(function(){
                    alert("error")
                });
                
                return dfd.promise();
            }
        }
        
        /**
         * Define constant Authentication Method
         */
        enum AuthenticationMethod {
            POP_BEFORE_SMTP = 0,
            IMAP_BEFORE_SMTP = 1,
            SMTP_AUTH_LOGIN = 2,
            SMTP_AUTH_PLAIN = 3,
            SMTP_AUTH_CRAM_MD5 = 4
        }
        
        /**
         * Define constant Use Server
         */
        enum UseServer {
            NOT_USE = 0,
            USE = 1
        }
        
         /**
         * Define constant Pop Use Server
         */
        enum PopUseServer {
            NOT_USE = 0,
            USE = 1
        }
        
         /**
         * Define constant Imap Use Server
         */
        enum ImapUseServer {
            NOT_USE = 0,
            USE = 1
        }
        
         /**
         * Define constant Encrypt Method
         */
        enum EncryptMethod {
            None = 0,
            SSL = 1,
            TLS = 2
        }
    }
}