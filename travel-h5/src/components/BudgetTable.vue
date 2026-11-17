<template>
  <div class="budget-table">
    <van-cell-group :border="false">
      <van-cell
        v-for="(value, key) in budgetItems"
        :key="key"
        :title="getLabel(key)"
        :value="`¥${value}`"
        :border="false"
      />
    </van-cell-group>
    <div class="budget-total">
      <span>Estimated total</span>
      <span class="total-amount">¥{{ estimated }}</span>
    </div>
    <div class="budget-line" v-if="total">
      <span>Your budget</span><span>¥{{ total }}</span>
    </div>
    <div class="budget-line" v-if="total" :class="remaining < 0 ? 'over' : 'left'">
      <span>{{ remaining < 0 ? 'Over budget' : 'Left over' }}</span><span>¥{{ Math.abs(remaining) }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  data: {
    type: Object,
    default: () => ({})
  },
  total: {
    type: [Number, String],
    default: 0
  }
})

const estimated = computed(() => Object.values(budgetItems.value).reduce((a, b) => a + Number(b), 0))
const remaining = computed(() => Number(props.total) - estimated.value)

const budgetItems = computed(() => {
  return {
    accommodation: props.data.accommodation || 0,
    food: props.data.food || 0,
    transportation: props.data.transportation || 0,
    tickets: props.data.tickets || 0,
    other: props.data.other || 0
  }
})

const labelMap = {
  accommodation: 'Accommodation',
  food: 'Food',
  transportation: 'Local transport',
  tickets: 'Tickets',
  other: 'Other (souvenirs, extras)'
}

const getLabel = (key) => {
  return labelMap[key] || key
}
</script>

<style scoped>
.budget-table {
  margin-top: 8px;
}

.budget-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f7f8fa;
  border-radius: 8px;
  margin-top: 8px;
  font-size: 16px;
  font-weight: 600;
}

.budget-line {
  display: flex;
  justify-content: space-between;
  padding: 8px 16px 0;
  font-size: 14px;
  color: #666;
}

.budget-line.over { color: #ee0a24; }
.budget-line.left { color: #07c160; }

.total-amount {
  color: #ee0a24;
  font-size: 18px;
}
</style>