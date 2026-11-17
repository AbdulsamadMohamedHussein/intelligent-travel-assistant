<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog, showToast } from 'vant'
import { deleteTrip, getTrips } from '../utils/api'
import { getUserId } from '../utils/request'

const router = useRouter()
const trips = ref([])
const loading = ref(false)
const errorMsg = ref('')
const userId = getUserId()

const totalDays = computed(() => trips.value.reduce((sum, t) => sum + t.plan.days, 0))

const loadTrips = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await getTrips()
    if (res && res.success) {
      trips.value = res.data || []
    } else {
      errorMsg.value = (res && res.message) || 'Failed to load saved trips.'
    }
  } catch (e) {
    errorMsg.value = 'Cannot reach the server. Please check that the backend is running.'
  } finally {
    loading.value = false
  }
}

const openTrip = (trip) => {
  router.push({ name: 'Detail', query: { tripId: trip.id } })
}

const removeTrip = async (trip) => {
  try {
    await showConfirmDialog({ title: 'Delete trip', message: `Delete your ${trip.plan.city} trip?` })
  } catch (e) {
    return // cancelled
  }
  const res = await deleteTrip(trip.id)
  if (res && res.success) {
    trips.value = trips.value.filter(t => t.id !== trip.id)
    showToast('Deleted')
  } else {
    showToast((res && res.message) || 'Could not delete')
  }
}

const formatDate = (iso) => new Date(iso).toLocaleDateString()

onMounted(loadTrips)
</script>

<template>
  <div class="page-container">
    <van-nav-bar title="My Profile" />
    <div class="page-content">
      <div class="card profile-card">
        <van-icon name="user-circle-o" size="48" color="#1989fa" />
        <div class="profile-info">
          <div class="profile-name">Traveller</div>
          <div class="profile-id">ID: {{ userId.slice(0, 12) }}</div>
        </div>
        <div class="profile-stats">
          <div><b>{{ trips.length }}</b><span>Trips</span></div>
          <div><b>{{ totalDays }}</b><span>Days</span></div>
        </div>
      </div>

      <div class="section-title">Saved Trips</div>
      <van-loading v-if="loading" type="spinner" class="center" />
      <div v-else-if="errorMsg" class="center">
        <van-empty :description="errorMsg" />
        <van-button type="primary" size="small" @click="loadTrips">Retry</van-button>
      </div>
      <van-empty v-else-if="!trips.length" description="No saved trips yet. Plan one on the Home page and tap Save Trip." />
      <template v-else>
      <van-swipe-cell v-for="trip in trips" :key="trip.id">
        <van-cell
          :title="`${trip.plan.city} - ${trip.plan.days} days`"
          :label="`Estimated ¥${trip.plan.estimatedCost} per person | Budget ¥${trip.plan.totalBudget} | ${formatDate(trip.createdAt)}`"
          is-link
          @click="openTrip(trip)" />
        <template #right>
          <van-button square type="danger" text="Delete" class="delete-btn" @click="removeTrip(trip)" />
        </template>
      </van-swipe-cell>
      </template>
    </div>
  </div>
</template>

<style scoped>
.page-container { min-height: 100vh; background-color: #f5f5f5; padding-bottom: 70px; }
.page-content { padding: 16px; }
.card { background: #fff; border-radius: 8px; padding: 16px; margin-bottom: 16px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05); }
.profile-card { display: flex; align-items: center; gap: 12px; }
.profile-info { flex: 1; }
.profile-name { font-size: 18px; font-weight: 600; }
.profile-id { font-size: 12px; color: #969799; margin-top: 4px; }
.profile-stats { display: flex; gap: 20px; text-align: center; }
.profile-stats b { display: block; font-size: 20px; color: #1989fa; }
.profile-stats span { font-size: 12px; color: #969799; }
.section-title { font-size: 16px; font-weight: bold; margin-bottom: 12px; }
.center { text-align: center; padding: 24px 0; }
.delete-btn { height: 100%; }
</style>
