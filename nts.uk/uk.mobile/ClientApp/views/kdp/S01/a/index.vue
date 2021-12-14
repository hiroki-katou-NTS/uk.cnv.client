<template>
  <div class="mx-n3">
    <div
      class="pl-5 text-break"
      id="textComment"
      v-bind:style="{
        color: setting.stampPageComment.commentColor
      }"
      v-html="textComment"
    ></div>
    <div class="pl-5 pt-2 pr-2" style="position: relative;" >
      <div class="clearfix" v-bind:style="{color: setting.displaySettingsStampScreen.settingDateTimeColor.textColor}">
        <span id="date">
          {{ $dt.now | date("YYYY/MM/DD（ddd）") }}
        </span>
        <span class="text-time">Time</span>
      </div>
    </div>
     <div class="pl-4 lh-1" v-bind:style="{color: setting.displaySettingsStampScreen.settingDateTimeColor.textColor}">
          <span id="time">{{ $dt.now | date("HH:mm") }}</span>
          <span class="second">:{{ $dt.now | date("ss") }}</span>
      </div>
        <div
          v-on:click="openDialogS"
          type="text"
          class="pr-4 blue-link"
        >
          {{ "KDPS01_25" | i18n }}
        </div>

    <div class="px-3">
      <div class="row">
        <div
          class="col-6 mb-2 px-2 div-btn-stamp"
          v-for="button in setting.buttons"
          v-bind:key="button.buttonPositionNo"
        >
          <button
            type="button"
            v-if="button.buttonDisSet.backGroundColor !== ''"
            v-bind:style="{
              color: button.buttonDisSet.buttonNameSet.textColor,
              'background-color': button.buttonDisSet.displayBackGroundColor
            }"
            v-click:500="() => stampClick(button)"
            class="btn-block btn-stamp btn btn-secondary "
          >
            <img v-bind:src="'/nts.uk.mobile.web/dist/resources/' + button.icon" height="50" width="50"/> 
            <br>
            <div
               v-bind:style="{
              'font-size': button.buttonDisSet.buttonNameSet.buttonName.length > 8 ? '15px' : '20px',
              'height': button.buttonDisSet.buttonNameSet.buttonName.length > 8 ? '29px': 'unset'
             
            }"
            >{{ button.buttonDisSet.buttonNameSet.buttonName }}</div>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
