<template>
  <div class="report-form-row">
    <div class="select-ids-label">
      <label class="report-form-label">Total instances selected: <span class="badge">{{ total }}</span></label>
      <div class="show-hide" @click="toggleShowHideLabel">{{ showHideLabel }}</div>
    </div>
    <textarea v-show="showQuery" readonly class="report-form-input" :value="query"></textarea>
  </div>
</template>

<script setup>
import {computed, defineProps, ref} from "vue";

defineProps({
  total: Number,
  query: String
})

const showHideLabel = ref("show")

function toggleShowHideLabel() {
  showHideLabel.value = showHideLabel.value === "hide" ? "show" : "hide"
}

const showQuery = computed(() => {
  return showHideLabel.value === "hide"
})

</script>

<style lang="scss">
.report-form-row .select-ids-label {
  display: flex;
  flex-direction: row;

  .report-form-label {
    flex: 1;
  }

  .show-hide {
    font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
    font-style: italic;
    color: #ccc;
    cursor: pointer;
  }
}

.report-form-row textarea {
  resize: vertical;
}
</style>
