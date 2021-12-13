<template>
  <div class="cmms45c pt-0">
    <div class="modal-header">
      <a class="uk-text-white" v-on:click="back()">
        <i class="fas fa-arrow-circle-left"></i>
        {{ 'CMMS45_17' | i18n }}
      </a>
    </div>
    <div class="card invisible mt-n2" v-show="!(!isEmptyApp() && isFirstApp() && isLastApp())">
      <div class="card-body">
      </div>
    </div>
    <div class="row mb-2 fixed-top mt-5 border-bottom" v-show="!(!isEmptyApp() && isFirstApp() && isLastApp())">
      <div class="col-6 text-left uk-bg-silver mt-n2 pt-2 pb-2 pl-4">
        <a class="uk-text-blue" v-on:click="toPreviousApp" v-bind:class="{ 'd-none': isFirstApp() && !isEmptyApp() }">
          <i class="fas fa-arrow-circle-left"></i>
          {{'CMMS45_18' | i18n}}
        </a>
      </div>
      <div class="col-6 text-right uk-bg-silver mt-n2 pt-2 pb-2 pr-4">
        <a class="uk-text-blue" v-on:click="toNextApp" v-bind:class="{ 'd-none': isLastApp() && !isEmptyApp() }">
          {{'CMMS45_19' | i18n}}
          <i class="fas fa-arrow-circle-right"></i>
        </a>
      </div>
    </div>
    <div class="row bg-grey-200 border uk-border-gray border-right-0 border-left-0 pt-1">
      <div class="col-12">
        <div class="row p-2 pl-3 pr-3 ">
          <div class="mb-2 p-2 status-label align-top col-3" v-bind:class="appState.getClass">{{ appState.getName | i18n }}</div>
          <div class="col-9">
            <span class="ml-1">{{ appState.getNote | i18n }}</span>
            <br />
            <span class="uk-text-blue" v-on:click="reverseApproval">{{'CMMS45_20' | i18n}}
              <i class="uk-text-blue"
                v-bind:class="{ 'fas fa-chevron-down': !showApproval, 'fas fa-chevron-up': showApproval}"
              ></i>
            </span>
          </div>
        </div>
        <div v-if="showApproval">
          <approved v-model="selected" class="mt-2">
            <template v-slot:buttons>
              <!-- Khi một button bất kỳ được click, giá trị được emit ra là thứ tự của button -->
              <button v-for="(phase, phaseIndex) in phaseLst" v-bind:key="phaseIndex" v-bind:class="[phase.className, !phase.isExist ? 'corner-line' : '' ]" 
                v-bind:disabled="!phase.isExist" class="h-100 w-100 p-2">{{ phase.phaseText | i18n }}</button>
            </template>
            <template v-slot:popovers>
              <!-- duyệt theo thứ tự button ở đây -->
              <div v-for="(phase, phaseIndex) in phaseLst" v-bind:key="phaseIndex">
                <template v-if="selected == phaseIndex">
                  <div>{{phase.infoLabel | i18n(phase.phaseOrder.toString())}}</div>
                  <div>
                    <table class="table table-sm table-bordered m-0 bg-white">
                      <tbody>
                        <template v-if="phase.listApprovalFrame.length > 1">
                          <tr v-for="(frame, frameIndex) in phase.listApprovalFrame" v-bind:key="frameIndex">
                            <td v-bind:class="{ 'uk-bg-powder-blue' : frame.listApprover[0].approvalAtrValue==1, 
                                                'uk-bg-light-coral' : frame.listApprover[0].approvalAtrValue==2 }"
                              class="fixed-column-width align-middle text-center">
                              <span v-if="frame.listApprover[0].approvalAtrValue==1">
                                <i class="fa fa-check"></i>   
                              </span>
                              <span v-else-if="frame.listApprover[0].approvalAtrValue==2">
                                <i class="fa fa-times"></i>
                              </span>
                            </td>
                            <td>
                              <template v-if="frame.listApprover[0].approvalAtrValue==1 || frame.listApprover[0].approvalAtrValue==2">
                                <span class="text-break" v-if="frame.listApprover[0].agentID">
                                  <span>{{ frame.listApprover[0].agentName }}</span>
                                </span>
                                <span class="text-break" v-else>
                                  <span>{{ frame.listApprover[0].approverName }}</span>
                                </span>
                                <br/>
                                <p class="text-break child-font-size mb-0 pl-2" style="word-break: break-word">{{ frame.listApprover[0].approvalReason }}</p>
                              </template>
                              <template v-else>
                                <span class="text-break">
                                  <span>{{ frame.listApprover[0].approverName }}</span>
                                  <span v-if="frame.listApprover[0].representerName">({{ frame.listApprover[0].representerName }})</span>
                                </span>
                              </template>    
                            </td>
                          </tr>
                        </template>
                        <template v-else>
                          <tr v-for="(approver, approverIndex) in phase.listApprovalFrame[0].listApprover" v-bind:key="approverIndex">
                            <td v-bind:class="{ 'uk-bg-powder-blue' : approver.approvalAtrValue==1, 
                                                'uk-bg-light-coral' : approver.approvalAtrValue==2 }"
                              class="fixed-column-width align-middle text-center">
                              <span v-if="approver.approvalAtrValue==1">
                                <i class="fa fa-check"></i>   
                              </span>
                              <span v-else-if="approver.approvalAtrValue==2">
                                <i class="fa fa-times"></i>
                              </span>
                            </td>
                            <td>
                              <template v-if="approver.approvalAtrValue==1 || approver.approvalAtrValue==2">
                                <span class="text-break" v-if="approver.agentID"><span>{{ approver.agentName }}</span></span>
                                <span class="text-break" v-else><span>{{ approver.approverName }}</span></span>
                                <br/>
                                <p class="text-break child-font-size mb-0 pl-2" style="word-break: break-word">{{ approver.approvalReason }}</p>
                              </template>  
                              <template v-else>
                                <span class="text-break">
                                  <span>{{ approver.approverName }}</span>
                                  <span v-if="approver.representerName">({{ approver.representerName }})</span>
                                </span>
                              </template>    
                            </td>
                          </tr>
                        </template>
                      </tbody>
                    </table>
                  </div>
                </template>
              </div>
            </template>
          </approved>
        </div>
        <div class="row pl-3 pr-3 mb-2" v-if="reversionReason"> 
          <div class="col-12 uk-bg-light-coral">
            <div class="row ml-0 mr-0 pt-1 pb-1">{{ 'CMMS45_58' | i18n }}</div>
            <hr class="mt-0 mb-0"/>
            <div class="row ml-2 mr-2 pt-1 pb-1" style="word-break: break-word">{{ reversionReason || 'CMMS45_15' | i18n }}</div>
          </div>
        </div>
        <div class="pl-3 pr-3 mb-2 uk-bg-orange" v-if="!displayCancelButton">{{'CMMS45_93' | i18n}}</div>
      </div> 
    </div>
    <div class="row mt-1 content-div uk-bg-headline border-top uk-border-light-gray">{{'CMMS45_21' | i18n}}</div>
    <div class="row content-div border-top uk-border-light-gray text-break">
      <span>{{ applicant | i18n }}</span> 
      <span v-if="representerDisp" class="uk-text-dark-gray child-font-size">{{ 'CMMS45_22' | i18n(representer) }}</span>
    </div>
    <div v-if="appType!=10" class="row content-div uk-bg-headline border-top uk-border-light-gray">{{'CMMS45_23' | i18n}}</div>
    <div v-if="appType!=10" class="row content-div border-top uk-border-light-gray">
      <div class="col-12">
        <div v-if="opAppStartDate == opAppEndDate" class="row">{{ appDate | i18n }} {{ appTypeName }} {{'CMMS45_24' | i18n(prePost)}}</div>
        <div v-if="opAppStartDate != opAppEndDate" class="row">{{ opAppStartDate | | date('MM/DD (ddd)') }}～{{ opAppEndDate | | date('MM/DD (ddd)') }} {{ appTypeName }} {{'CMMS45_24' | i18n(prePost)}}</div>
        <div class="row uk-text-dark-gray child-font-size">{{'CMMS45_25' | i18n(inputDate)}}</div>
      </div>
    </div>
    <div>
      <div v-if="currentApp=='sample'">
        <appsample v-bind:params="appTransferData" @loading-complete='loadingComplete' />
      </div>
      <div v-if="currentApp!='sample'">
      <app2 v-if="appType==2" v-bind:params="appTransferData" @loading-complete='loadingComplete'/>
      <app3 v-if="appType==3" v-bind:params="appTransferData" @loading-complete='loadingComplete'/>
      <app4 v-if="appType==4" v-bind:params="appTransferData" @loading-complete='loadingComplete'/>
      <app7 v-if="appType==7" v-bind:params="appTransferData" @loading-complete='loadingComplete'/>
      <app8 v-if="appType==8" v-bind:params="appTransferData" @loading-complete='loadingComplete'/>
      <app9 v-if="appType==9" v-bind:params="appTransferData" @loading-complete='loadingComplete'/>
      <app15 v-if="appType==15" v-bind:params="appTransferData" @loading-complete='loadingComplete'/>
      <app0 v-if="appType==0" v-bind:params="appTransferData" @loading-complete='loadingComplete'/>
      <app1 v-if="appType==1" v-bind:params="appTransferData" @loading-complete='loadingComplete'/>
      <app10 v-if="appType==10" v-bind:params="appTransferData" @loading-complete='loadingComplete'/>
      <app6 v-if="appType==6" v-bind:params="appTransferData" @loading-complete='loadingComplete'/>
      </div>
    </div>
    <div v-if="comboReasonDisp || textReasonDisp" class="row content-div uk-bg-headline border-top uk-border-light-gray">{{'CMMS45_34' | i18n}}</div>
    <div v-if="comboReasonDisp || textReasonDisp" class="row content-div border-top uk-border-light-gray text-break">
      <div class="col-12">
        <div v-if="comboReasonDisp" class="row"><div class="col-12 pl-0">{{ comboReason | i18n }}</div></div> 
        <div v-if="textReasonDisp" class="row"><div class="col-12 pl-0" v-html="textReason"></div></div>
      </div>
    </div>
    <div v-if="appType == 0 && reasons != null">
      <!--B7_1-->
      <div class="card card-label" v-if="reasons[0].c1">
        <div class="card-header uk-bg-accordion mt-2 headerDiv">
            <span class="textPosition">{{'KAFS05_78' | i18n(reasons[0].title)}}</span>
        </div>
        <div v-if="reasons[0].c2" class="row mt-1 mb-1">
            <div class="col-1.5 pr-2 pl-3">
                {{reasons[0].code}}
            </div>
            <div class="col pl-0">
                {{reasons[0].name}}
            </div>
        </div>
        <div v-if="reasons[0].c3">{{reasons[0].content}}</div>
      </div>

      <!--B8_1-->
      <div class="card card-label" v-if="reasons[1].c1">
        <div class="card-header uk-bg-accordion mt-2 headerDiv">
            <span class="textPosition">{{'KAFS05_78' | i18n(reasons[1].title)}}</span>
        </div>
        <div v-if="reasons[1].c2" class="row mt-1 mb-1">
            <div class="col-1.5 pr-2 pl-3">
                {{reasons[1].code}}
            </div>
            <div class="col pl-0">
                {{reasons[1].name}}
            </div>
        </div>
        <div v-if="reasons[1].c3">{{reasons[1].content}}</div>
      </div>
    </div>
    <div v-if="appType == 6 && reasons != null">
      <!--B7_1-->
      <div class="card card-label" v-if="reasons[0].c1">
        <div class="card-header uk-bg-accordion mt-2 headerDiv">
            <span class="textPosition">{{'KAFS10_19' | i18n}}</span>
        </div>
        <div v-if="reasons[0].c2" class="row mt-1 mb-1">
            <div class="col-1.5 pr-2 pl-3">
                {{reasons[0].code}}
            </div>
            <div class="col pl-0">
                {{reasons[0].name}}
            </div>
        </div>
        <div v-if="reasons[0].c3">{{reasons[0].content}}</div>
      </div>
    </div>
    <div
      class="fixed-action-btn" v-show="displayEditFloat && isLoadingComplete && !appState.pastApp"
      v-float-action="{ icon: 'fas fa-pen', background: 'uk-bg-sea-green', forceground: 'uk-text-dark-gray' }"
    >
      <ul>
        <li class="uk-bg-white" v-on:click="cancelApp" v-show="displayCancelButton">
          <span class="uk-text-dark-gray">{{'CMMS45_92' | i18n}}</span>
          <i class="far fa-times-circle uk-text-dark-gray"></i>
        </li>
        <li class="uk-bg-white" v-on:click="deleteApp" v-show="displayDeleteButton">
          <span class="uk-text-dark-gray">{{'CMMS45_60' | i18n}}</span>
          <i class="fas fa-trash-alt uk-text-dark-gray"></i>
        </li>
        <li class="uk-bg-white" v-on:click="updateApp" v-show="displayUpdateButton">
          <span class="uk-text-dark-gray">{{'CMMS45_61' | i18n}}</span>
          <i class="fas fa-pen uk-text-dark-gray"></i>
        </li>
      </ul>
    </div>
  </div>
</template>