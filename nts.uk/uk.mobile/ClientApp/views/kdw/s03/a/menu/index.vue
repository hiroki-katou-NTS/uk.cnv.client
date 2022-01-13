<template>
    <div class="kdws03a uk-bg-white">
        <div class="modal-header rounded-0 d-block p-0">
            <div class="uk-bg-teal py-2">
                <h4 class="col-4 modal-title text-white" v-on:click="closeDialog">
                    <i class="fas fa-angle-left mr-1"></i>
                    <span>{{ 'KDWS03_75' | i18n }}</span>
                </h4>
            </div>
        </div>
        <ul class="list-group border-0">
            <li class="list-group-item border-0" v-if="params.allConfirmButtonDis && params.selfConfirm">
                <button type="button" class="btn btn-primary w-100 btn-lg btn-success" v-click:500="() => processConfirmAll('confirm')">{{'KDWS03_24' | i18n}}</button></li>
            <li class="list-group-item border-0" v-if="params.allConfirmButtonDis && params.selfConfirm">
                <button type="button" class="btn btn-primary w-100 btn-lg btn-warning" v-click:500="() => processConfirmAll('remove')">{{'KDWS03_25' | i18n}}</button></li>
            <div v-if="(params.displayFormat == 0) && params.allConfirmButtonDis && params.selfConfirm" class="w-2 mx-2" style="border: 1px solid #9ea5b0 !important"></div>
            <li class="list-group-item border-0" v-if="params.errorReferButtonDis">
                <button type="button" class="btn btn-primary w-100 btn-lg btn-secondary" v-click:500="openErrorList">{{'KDWS03_30' | i18n}}</button></li>
            <li class="list-group-item border-0" v-if="params.restReferButtonDis">
                <button type="button" class="btn btn-primary w-100 btn-lg btn-secondary" v-click:500="() => openKdws03g(true)">{{'KDWS03_31' | i18n}}</button></li>
            <li class="list-group-item border-0" v-if="params.monthActualReferButtonDis">
                <button type="button" class="btn btn-primary w-100 btn-lg btn-secondary" v-click:500="openKdws03f">{{'KDWS03_32' | i18n}}</button></li>  
            <li class="list-group-item border-0" v-if="params.timeExcessReferButtonDis">
                <button type="button" class="btn btn-primary w-100 btn-lg btn-secondary" v-click:500="() => openKdws03g(false)">{{'KDWS03_33' | i18n}}</button></li>
            <!--A3-15 -->
            <li class="list-group-item border-0" v-if="params.authoryView && params.functionNoView ">
                <button type="button" class="btn btn-primary w-100 btn-lg btn-secondary" v-click:500="openKdws03i">{{'KDWS03_12' | i18n}}</button></li>
        </ul>
    </div>
</template>