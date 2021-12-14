module nts.uk.com.view.cas012.b.service {
    var paths: any = {
        searchUser : "ctx/sys/auth/user/searchUser",
    }

    export function searchUser(searchValue: string): JQueryPromise<any> {
        
        return nts.uk.request.ajax("com", paths.searchUser, searchValue);
    }



}