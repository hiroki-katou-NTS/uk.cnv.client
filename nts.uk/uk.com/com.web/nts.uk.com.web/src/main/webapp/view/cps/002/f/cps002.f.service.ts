module cps002.f.service{
    import ajax = nts.uk.request.ajax;
    import format = nts.uk.text.format;
    let basePathPerInfoCtg = "ctx/bs/person/info/category";
    let basePathPerInfoItem = "ctx/bs/person/info/ctgItem";
    let paths = {
        perInfoCtgHasItems:"/find/perInfoCtgHasItems",
        getPerInfoItemDef: "/findby/getPerInfoItemByCtgId",
        updatePerInfoCtgCopy: "/update/updatePerInfoCtgCopy",
        updatePerInfoItemCopy: "/update/updatePerInfoItemDefCopy"
    };
    
    export function getPerInfoCtgHasItems(ctgName){
        return ajax("com", basePathPerInfoCtg+paths.perInfoCtgHasItems, ctgName);
    }
    
    export function getPerInfoItemDef(categoryId){
        return ajax("com", basePathPerInfoItem + paths.getPerInfoItemDef, categoryId);
    }
    
    export function updatePerInfoCtgCopy(categoryId){
        return ajax("com", basePathPerInfoCtg + paths.updatePerInfoCtgCopy, categoryId);
    }
    
    export function updatePerInfoItemCopy(perInfoCtgId, perInfoItemDefIds){
        let command = {
            perInfoCtgId: perInfoCtgId,
            perInfoItemDefIds: perInfoItemDefIds
        };
        return ajax("com", basePathPerInfoItem + updatePerInfoItemCopy, command);
    }
    
}