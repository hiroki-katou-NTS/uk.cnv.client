module nts.uk.com.view.cmm050.a {
    export module service {
        /**
         * define path to service
         */
        var path: any = {
                find: "sys/env/mailserver/find",
                save: "sys/env/mailserver/save",
                checkDataChanged: "sys/env/mailserver/checkDataChanged",
            };
        
        /**
         * 
         */
        export function findMailServerSetting(): JQueryPromise<model.MailServerDto>{
            return nts.uk.request.ajax(path.find);
        }
        
        /**
         * 
         */
        export function registerMailServerSetting(data: model.MailServerDto): JQueryPromise<any> {
            return nts.uk.request.ajax(path.save, data);
        }

        export function checkDataChanged(data: model.MailServerDto): JQueryPromise<boolean> {
          return nts.uk.request.ajax(path.checkDataChanged, data);
        }
    }
    
    /**
     * Model define.
     */
    export module model {
        export class MailServerDto {
            useAuth: number;
            encryptionMethod: number;
            authMethod: number;
            emailAuthencation: string;
            password: string;
            smtpDto: SmtpInfoDto;
            popDto:  PopInfoDto;
            imapDto: ImapInfoDto;
            
            constructor(useAuth: number, encryptionMethod: number, authenticationMethod: number, emailAuthencation: string,
                        password: string, smtpDto?: SmtpInfoDto, popDto?:  PopInfoDto, imapDto?: ImapInfoDto){
                this.useAuth = useAuth;
                this.encryptionMethod = encryptionMethod;
                this.authMethod = authenticationMethod;
                this.emailAuthencation = emailAuthencation;
                this.password = password;
                this.smtpDto = smtpDto;
                this.popDto = popDto;
                this.imapDto = imapDto;
            }
        }

        export class SmtpInfoDto {
            smtpIpVersion: number;
            smtpServer: string;
            smtpTimeOut: number;
            smtpPort: number;
            
            constructor( smtpServer: string,
                        smtpPort: number ){
                this.smtpServer = smtpServer;
                this.smtpPort = smtpPort;
            }
        }

        export class PopInfoDto {
            popServer: string;
            popUseServer: number;
            popPort: number;
            
            constructor(popServer: string,
                        popUseServer: number,
                        popPort: number ){
                this.popServer = popServer;
                this.popUseServer = popUseServer;
                this.popPort = popPort;
            }
        }

        export class ImapInfoDto {
            imapServer: string;
            imapUseServer: number;
            imapPort: number;
            
            constructor( imapServer: string,
                        imapUseServer: number,
                        imapPort: number ){
                this.imapServer = imapServer;
                this.imapUseServer = imapUseServer;
                this.imapPort = imapPort;
            }
        }
    }
    
}