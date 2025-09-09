<template>
  <ReportForm class="report-container"
              :report="report"
              :on-close-request="onCloseRequest"
  />
</template>

<script setup>
import ReportForm from "@/components/ReportForm";
import {Report} from "@/model/Report";
import {defineProps} from "vue";

defineProps({
  report: Report,
  onCloseRequest: Function,
})
</script>
