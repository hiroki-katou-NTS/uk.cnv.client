module nts.uk.com.view.cli001.a {

    export module service {
        var servicePath: any = {

            removeLockOutData: "ctx/sys/gateway/securitypolicy/lockoutdata/remove",
            findLockOutData: "ctx/sys/gateway/securitypolicy/lockoutdata/find",
            findByUserId: "ctx/sys/gateway/securitypolicy/lockoutdata/findByUserId/"

        }
        export function removeLockOutData(command: any): JQueryPromise<void> {
            return nts.uk.request.ajax(servicePath.removeLockOutData, command);
        }

        export function findAll(): JQueryPromise<Array<model.LockOutDataUserDto>> {
            return nts.uk.request.ajax(servicePath.findLockOutData);

        }
        export function findByUserId(userId: string): JQueryPromise<model.LockOutDataUserDto> {
            return nts.uk.request.ajax(servicePath.findByUserId + userId);
        }

        export module model {
            export interface LockOutDataUserDto {
                logType: number;
                loginId: string;
                userId: string;
                companyCode: string
                employeeCode: string
                userName: string
                lockOutDateTime: string;
            }
            export interface LockOutDataDto {
                lockType: number;
                contractCode: string;
                userId: string;
                loginMethod: number;
                lockoutDateTime: string;
            }

        }
    }
}