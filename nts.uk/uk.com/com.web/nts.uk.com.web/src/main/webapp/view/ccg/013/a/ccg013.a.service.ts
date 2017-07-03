module ccg013.a.service {

    // Service paths.
    var servicePath = {
        find: "sys/portal/webmenu/find/{0}",
        findAllWebMenu: "sys/portal/webmenu/find",
        addWebMenu: "sys/portal/webmenu/add",
        updateWebMenu: "sys/portal/webmenu/update",
        findStandardMenuList: "sys/portal/standardmenu/findAll",
        deleteWebMenu: "sys/portal/webmenu/remove"
    }
    
    export function findWebMenu(webMenuCode: string): JQueryPromise<WebMenuDto> {
        var path = nts.uk.text.format(servicePath.find, webMenuCode);
        return nts.uk.request.ajax(path);
    }  
    
    export function loadWebMenu(): JQueryPromise<Array<any>> {
        var path = servicePath.findAllWebMenu;
        return nts.uk.request.ajax(path);
    }  
    
    export function addWebMenu( isCreated, webMenu: any): JQueryPromise<any> {
        var path = isCreated ? servicePath.addWebMenu : servicePath.updateWebMenu;
        return nts.uk.request.ajax("com",path, webMenu);
    }  
    
    export function deleteWebMenu(webMenuCd): JQueryPromise<any> {
        var path = servicePath.deleteWebMenu;
        debugger;
        var obj = {
                webMenuCd: webMenuCd 
            };
        return nts.uk.request.ajax("com",path, obj);
    } 
    
    export function findStandardMenuList(): JQueryPromise<Array<StandardMenuDto>> {
        return nts.uk.request.ajax(servicePath.findStandardMenuList);
    }
    
    export interface WebMenuDto {
        defaultMenu: number,
        menuBars: Array<any>,
        webMenuCode: string,
        webMenuName: string    
    }
    
    export interface StandardMenuDto {
        code: string,
        displayName: string,
        system: string,
        classification: string
    }
}