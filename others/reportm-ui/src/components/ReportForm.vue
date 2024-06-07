<template>
  <ModalDialog class="report-form" @close="closeForm">

    <template #dialog-header>
      <div class="report-form-title">Report: {{ report.name }}</div>
      <div class="report-form-description">{{ report.description }}</div>
    </template>

    <template #dialog-body>
      <form method="post" action="#" class="report-form-body" @submit.prevent>
        <QueryInput v-if="report.reportQuery"
                    class="spacer"
                    :total="report.reportQuery.total"
                    :query="report.reportQuery.query"/>

        <div v-if="report.args.length >0"
             class="report-args-container">

          <div class="title">Arguments:</div>
          <component v-for="arg in report.args"
                     :is="ComponentTypeMap[arg.type]"
                     :key="arg"
                     :label="arg.name"
                     v-model:value="arg.value"
          />
        </div>

        <TextAreaInput v-if="allowSendingEmail"
                       class="report-input-emails"
                       label="Emails:"
                       v-model:value="emails"/>
      </form>
    </template>

    <template #dialog-footer>
      <div class="report-form-button-container">
        <button value="submit"
                class="report-form-button btn btn-small button-cancel-action"
                @click="closeForm">Close
        </button>

        <button value="submit"
                class="report-form-button btn btn-small btn-success"
                @click="downloadReport">Download
        </button>

        <button v-if="allowSendingEmail"
                value="submit"
                class="report-form-button btn btn-small btn-success"
                :disabled="!hasEmails"
                @click="sendReportByEmail">Send by Email
        </button>
      </div>
    </template>

  </ModalDialog>
</template>

<script setup>
import {Report} from "@/model/Report";
import {computed, defineProps, ref} from "vue";
import ModalDialog from "@/components/ModalDialog";
import TextInput from "@/components/TextInput";
import DateInput from "@/components/DateInput";
import {ArgumentType} from "@/model/Types";
import QueryInput from "@/components/QueryInput";
import TextAreaInput from "@/components/TextAreaInput";

const ComponentTypeMap = {
  [ArgumentType.TEXT]: TextInput,
  [ArgumentType.DATE]: DateInput,
}

const props = defineProps({
  report: Report,
  onCloseRequest: Function,
})

const allowSendingEmail = computed(() => props.report.actions.indexOf("Send Email") !== -1)
const emails = ref(props.report.emails || "")
const hasEmails = computed(() => emails.value.length)

function downloadReport() {
  props.report.generateAndDownloadReport()
  closeForm()
}

function sendReportByEmail() {
  props.report.generateAndSendEmail(emails.value)
  closeForm()
}

function closeForm() {
  props.onCloseRequest()
}
</script>

<style lang="scss">

.report-form {
  height: 100%;
  padding: 10px;

  .spacer {
    margin-bottom: 20px;
  }

  .report-form-title {
    margin-top: 10px;
    margin-bottom: 10px;
    font-size: 1.5em;
    font-weight: bold;
  }

  .report-form-description {
    margin-bottom: 20px;
    font-size: 1em;
    color: #777;
  }

  .report-form-body {
    display: flex;
    flex-direction: column;
    flex: 1;

    .report-args-container {
      overflow-y: auto;
      flex: 1;

      .title {
        margin-bottom: 5px;
        font-weight: bold;
        font-size: 14px;
      }

      .report-form-row:last-child {
        margin-bottom: 20px;
      }

      input[type='text'] {
        width: 239px !important;
        height: 23px !important;
      }
    }
  }

  .report-form-button-container {
    display: flex;
    flex-direction: row;
    gap: 10px;

    .report-form-button {
      width: 120px;
    }

    .button-cancel-action {
      margin-right: 50px;
    }
  }
}

.report-form-row {
  width: 100%;

  .report-form-label {
    font-size: 14px;
    line-height: 2;
    display: block;
  }

  input.report-form-input {
    width: 50%;
  }

  textarea.report-form-input {
    width: 100%;
  }

  input {
    box-sizing: border-box !important;
    height: unset !important;
  }
}
</style>
