module nts.uk.com.view.cas013.b.service {
    var paths: any = {
        searchUser : "ctx/sys/auth/user/findByKey",
    }

    export function searchUser(key: string, Special: boolean, Multi: boolean, userId: Array<string>): JQueryPromise<any> {
        var userKeyDto = {
                    key: key,
                    Special: Special,
                    Multi: Multi,
                    userId: userId
                };
        return nts.uk.request.ajax("com", paths.searchUser, userKeyDto);
    }



}