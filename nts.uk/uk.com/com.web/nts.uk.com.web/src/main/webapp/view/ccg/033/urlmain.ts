__viewContext.ready(function() {
    debugger;
    var url_string = window.location.href;
    var urlID = _.split(url_string, '=')[1];
    var server_path = nts.uk.text.format("/ctx/sys/gateway/url/execution/{0}", urlID); 
    nts.uk.ui.block.invisible();
    nts.uk.request.ajax("com", server_path).done((success) => {
        //Doi ung password policy
        if(!nts.uk.util.isNullOrUndefined(success.changePw.successMsg)&&!nts.uk.util.isNullOrEmpty(success.changePw.successMsg)){
            nts.uk.ui.dialog.info({ messageId: success.successMsg }).then(()=>{
                loginDone(success);     
            });
        } else {
            loginDone(success);    
        }  
    }).fail((failure) => {
        if(!nts.uk.util.isNullOrEmpty(failure.messageId)){
            nts.uk.ui.dialog.alertError({ messageId: failure.messageId, messageParams: failure.parameterIds }).then(function() {
                nts.uk.request.jump("com", "/view/ccg/007/d/index.xhtml"); 
            });
        } else if(!nts.uk.util.isNullOrEmpty(failure.message)){
            nts.uk.ui.dialog.alertError(failure.message).then(function() {
                nts.uk.request.jump("com", "/view/ccg/007/d/index.xhtml"); 
            });
        } else {
            nts.uk.ui.dialog.alertError({ messageId: "unknown error" }).then(function() {
                nts.uk.request.jump("com", "/view/ccg/007/d/index.xhtml"); 
            });
        }
    });
});
function loginDone(success) {
    let changePw = success.changePw;
    if (!nts.uk.util.isNullOrEmpty(changePw.msgErrorId) && changePw.msgErrorId == 'Msg_1517') {
        //確認メッセージ（Msg_1517）を表示する{0}【残り何日】
        nts.uk.ui.dialog.confirm({ messageId: changePw.msgErrorId, messageParams: [changePw.spanDays]})
            .ifYes(() => {
                success.changePw.changePassReason = 'Msg_1523';
                openDialogCCG007E(success);
            }).ifNo(() => {
                routeData(success);
            });
    } else if (changePw.showChangePass) {
        openDialogCCG007E(success);
    }else{
        routeData(success);
    }
}
//open dialog CCG007E - change pass 
function openDialogCCG007E(success) {
    let changePw = success.changePw;
    //set LoginId to dialog
    nts.uk.ui.windows.setShared('parentCodes', {
        form1: false,
        contractCode: __viewContext.user.contractCode,
        employeeCode: __viewContext.user.employeeCode,
        companyCode: __viewContext.user.companyCode
    }, true);
    nts.uk.ui.windows.setShared("changePw", {
        reasonUpdatePw: changePw.changePassReason,
        spanDays: changePw.spanDays
    });
    nts.uk.ui.windows.sub.modal('/view/ccg/007/e/index.xhtml', {
        width: 520,
        height: 450
    }).onClosed(function(): any {
        var changePwDone = nts.uk.ui.windows.getShared('changePwDone');
        if(changePwDone){
           routeData(success); 
        }else{
            window.close();
        }
        
    })
}
function routeData(success){
    switch(success.programID){
        case "ccg007": {
            // forgot password screen
            nts.uk.request.jump("com", "/view/ccg/007/"+success.screenID+"/index.xhtml?id="+urlID);
            break;
        }
        case "kaf002": {
            nts.uk.request.jump("at", "/view/kaf/000/b/index.xhtml", { 
                'listAppMeta': [_.first(_.map(success.urlTaskValueList))], 
                'currentApp': _.first(_.map(success.urlTaskValueList)) });    
            break;
        }
        case "kaf004": {
            nts.uk.request.jump("at", "/view/kaf/000/b/index.xhtml", { 
                'listAppMeta': [_.first(_.map(success.urlTaskValueList))], 
                'currentApp': _.first(_.map(success.urlTaskValueList)) });   
            break;   
        }
        case "kaf005": {
            nts.uk.request.jump("at", "/view/kaf/000/b/index.xhtml", { 
                'listAppMeta': [_.first(_.map(success.urlTaskValueList))], 
                'currentApp': _.first(_.map(success.urlTaskValueList)) }); 
            break;     
        }
        case "kaf006": {
            nts.uk.request.jump("at", "/view/kaf/000/b/index.xhtml", { 
                'listAppMeta': [_.first(_.map(success.urlTaskValueList))], 
                'currentApp': _.first(_.map(success.urlTaskValueList)) });  
            break;  
        }
        case "kaf007": {
            nts.uk.request.jump("at", "/view/kaf/000/b/index.xhtml", { 
                'listAppMeta': [_.first(_.map(success.urlTaskValueList))], 
                'currentApp': _.first(_.map(success.urlTaskValueList)) });   
            break;   
        }
        case "kaf009": {
            nts.uk.request.jump("at", "/view/kaf/000/b/index.xhtml", { 
                'listAppMeta': [_.first(_.map(success.urlTaskValueList))], 
                'currentApp': _.first(_.map(success.urlTaskValueList)) });     
            break;
        }
        case "kaf010": {
            nts.uk.request.jump("at", "/view/kaf/000/b/index.xhtml", { 
                'listAppMeta': [_.first(_.map(success.urlTaskValueList))], 
                'currentApp': _.first(_.map(success.urlTaskValueList)) });  
            break;    
        }   
        case "kaf011": {
            nts.uk.request.jump("at", "/view/kaf/000/b/index.xhtml", { 
                'listAppMeta': [_.first(_.map(success.urlTaskValueList))], 
                'currentApp': _.first(_.map(success.urlTaskValueList)) });  
            break;    
        }   
        case "cmm045": {
            nts.uk.request.jump("at", "/view/cmm/045/a/index.xhtml?a=1");  
            break;   
        } 
        case "kdw003": {
            var initParam = {
                //画面モード
                screenMode: success.urlTaskValueList["screenMode"]=="normal" ? 0 : 1,
                //社員一覧
                lstEmployee: [success.sID],
                //エラー参照を起動する
                errorRefStartAtr: success.urlTaskValueList["errorRef"],
                //期間を変更する
                changePeriodAtr: success.urlTaskValueList["changePeriod"],
                //処理締め
                targetClosue: null,
                //Optional
                //打刻初期値
                initClock: null,
                //遷移先の画面
                transitionDesScreen: null,
            }
    
            var extractionParam = {
                //表示形式
                displayFormat: 0, // DPCorrectionDisplayFormat.INDIVIDUAl(個人別)
                //期間
                startDate: null,
                endDate: null,
                //抽出した社員一覧
                lstExtractedEmployee: [success.sID],
                //Optional
                //日付別で起動
                dateTarget: null,
                individualTarget: success.sID,
            }
            nts.uk.request.jump("at", "/view/kdw/003/a/index.xhtml", {initParam: initParam, extractionParam: extractionParam});    
            break;
        }
        case "kdw004": {
            nts.uk.request.jump("at", "/view/kdw/004/a/index.xhtml");  
            break;   
        } 
        default: nts.uk.request.jump("com", "/view/ccg/007/d/index.xhtml");
    }        
}

