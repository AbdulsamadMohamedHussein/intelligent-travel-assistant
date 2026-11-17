<script setup>
import { onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { showToast } from 'vant';
import { getTravelRecommend, getTrip, saveTrip } from '../utils/api';
import SpotItem from '../components/SpotItem.vue';
import BudgetTable from '../components/BudgetTable.vue';

const loading = ref(false);
const saving = ref(false);
const saved = ref(false);
const errorMsg = ref('');
const router = useRouter();
const route = useRoute();
const tripData = ref(null);
const activeDays = ref([]);

const formData = reactive({
  city: '',
  budget: null,
  days: null
});

const goToChat = () => {
  router.push({ name: 'chat', query: { city: tripData.value ? tripData.value.city : '' } });
};

const goBack = () => {
  router.back();
};

onMounted(() => {
  formData.city = route.query.city;
  formData.budget = route.query.budget;
  formData.days = route.query.days;
  fetchRecommend();
});

// Two ways to open this page: a saved trip (?tripId=...) or new parameters (?city=&budget=&days=)
const fetchRecommend = async () => {
  loading.value = true;
  errorMsg.value = '';
  try {
    let res;
    if (route.query.tripId) {
      res = await getTrip(route.query.tripId);
      if (res.success && res.data) {
        res.data = res.data.plan;
        saved.value = true;
      }
    } else {
      res = await getTravelRecommend({
        city: formData.city,
        budget: Number(formData.budget),
        days: Number(formData.days)
      });
    }
    if (res && res.success && res.data) {
      tripData.value = res.data;
      activeDays.value = [1];
    } else {
      errorMsg.value = (res && res.message) || 'Failed to load trip plan.';
    }
  } catch (error) {
    console.error(error);
    errorMsg.value = 'Cannot reach the server. Please check that the backend is running.';
  } finally {
    loading.value = false;
  }
};

const handleSave = async () => {
  if (saved.value || saving.value) return;
  saving.value = true;
  try {
    const res = await saveTrip({
      city: tripData.value.city,
      budget: tripData.value.totalBudget,
      days: tripData.value.days
    });
    if (res.success) {
      saved.value = true;
      showToast('Saved to your profile');
    } else {
      showToast(res.message || 'Could not save');
    }
  } catch (e) {
    showToast('Could not save, please try again');
  } finally {
    saving.value = false;
  }
};
</script>

<template>
  <div class = "page-container">
    <van-nav-bar title="Detail" left-arrow @click-left="goBack" />   
    <div class="page-content">
      <div class="loading-container" v-if="loading" >
        <van-loading size="48" type="spinner" text="Loading..." style="margin-top: 20px;" />
      </div>
      <div class="empty-container" v-else-if="errorMsg">
        <van-empty :description="errorMsg" />
        <van-button type="primary" @click="fetchRecommend">Retry</van-button>
        <br><br>
        <van-button type="primary" @click="goBack">Back</van-button>
      </div>
      <template v-else-if="tripData">
        <div class="card overview-card">
          <div class="trip-header">
            <h2>{{ tripData.city }}  {{ tripData.days }} Days</h2>
            <div class="trip-budget">
              Budget:  {{ tripData.totalBudget }} RMB
            </div>
          </div>
          <div class="trip-sub">
            Estimated cost per person: {{ tripData.estimatedCost }} RMB &middot; Plan type: {{ tripData.strategy }}
          </div>
        </div>
        <van-collapse class="trip-collapse" v-model="activeDays">
          <van-collapse-item v-for = "day in tripData.dailyItinerary" :key="day.day" 
          :title="'Day '+day.day" :name="day.day">
            <div class="day-schedule">
              <div class="schedule-section">
                <div class="section-label morning">Morning</div>
                <SpotItem :data="day.morning" />
              </div>
              <div class="schedule-section">
                <div class="section-label afternoon">Afternoon</div>
                <SpotItem :data="day.afternoon" />
              </div>
              <div class="schedule-section">
                <div class="section-label evening">Evening</div>
                <SpotItem :data="day.evening" />
              </div>
            </div>
          </van-collapse-item>
        </van-collapse>
        <div class="card budget-card" v-if="tripData.budgetBreakdown">
          <div class="section-title">
            <h2> Budget Breakdown</h2>
            <BudgetTable :data="tripData.budgetBreakdown" :total="tripData.totalBudget" />
          </div>
        </div>
        <div class="card tips-card" v-if="tripData.tips&&tripData.tips.length">
          <div class="section-title">
            <h2> Kind Reminder</h2>
          </div>
          <ul class="tips-list">
            <li v-for="(tip,index) in tripData.tips" :key="index"> {{ tip }}</li>
          </ul>
        </div>
        <div class="card warnings-card" v-if="tripData.warnings&&tripData.warnings.length">
          <div class="section-title">
            <h2> Warnings</h2>
          </div>
          <ul class="warnings-list">
            <li v-for="(warning,index) in tripData.warnings" :key="index"> {{ warning }}</li>
          </ul>
        </div>
      </template>
    </div>
    <div class="detail-footer" v-if="tripData">
      <van-button plain type="primary" @click="handleSave" :loading="saving" :disabled="saved" class="footer-btn">
        {{ saved ? 'Saved' : 'Save Trip' }}
      </van-button>
      <van-button type="primary" @click="goToChat" class="footer-btn">Ask AI Assistant</van-button>
    </div>
  </div>
</template>

<style scoped>
.page-container {
    min-height: 100vh;
    background-color: #f5f5f5;
    padding-bottom: 70px;
}
.card {
    background-color: #fff;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.section-title {
    font-size: 18px;
    font-weight: 600;
    color: #323233;
    margin-bottom: 12px;
}
.page-content {
    padding: 16px;
}
.overview-card {
  margin-bottom: 16px;
}

.trip-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.trip-header h2 {
  font-size: 20px;
  color: #323233;
  margin: 0;
}

.trip-budget {
  font-size: 16px;
  color: #ee0a24;
  font-weight: 600;
}

.trip-collapse {
  margin-bottom: 16px;
}

.day-schedule {
  padding: 8px 0;
}

.schedule-section {
  margin-bottom: 16px;
}

.schedule-section:last-child {
  margin-bottom: 0;
}

.section-label {
  font-size: 14px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 4px;
  display: inline-block;
  margin-bottom: 8px;
}

.section-label.morning {
  background: #fff7e6;
  color: #fa8c16;
}

.section-label.afternoon {
  background: #e6f7ff;
  color: #1890ff;
}

.section-label.evening {
  background: #f6ffed;
  color: #52c41a;
}

.budget-card,
.tips-card,
.warnings-card {
  margin-bottom: 16px;
}

.tips-list,
.warnings-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tips-list li,
.warnings-list li {
  padding: 8px 0;
  color: #666;
  font-size: 14px;
  border-bottom: 1px solid #f5f5f5;
}

.tips-list li:last-child,
.warnings-list li:last-child {
  border-bottom: none;
}

.detail-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
  max-width: 750px;
  margin: 0 auto;
  display: flex;
  gap: 12px;
}

.footer-btn { flex: 1; }

.trip-sub {
  margin-top: 8px;
  font-size: 13px;
  color: #969799;
}

.error-card {
  text-align: center;
  padding: 40px 16px;
}
</style>
