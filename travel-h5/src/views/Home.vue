<script setup>
import { onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { showToast } from 'vant';
import { getCities } from '../utils/api';

const router = useRouter();

const goPage = (path) => {
  router.push(path);
}

const handleSubmit = () => {
  console.log(formData);
  if(!formData.city ){
    showToast('Please choose your destination city.');
    return;
  }
  if(!formData.budget || Number(formData.budget) <= 0){
    showToast('Please input a budget greater than 0.');
    return;
  }
  const days = Number(formData.days);
  if(!Number.isInteger(days) || days < 1 || days > 7){
    showToast('Days must be a whole number from 1 to 7.');
    return;
  }
  router.push({
    name:'Detail',
    query:{
      city:formData.city,
      budget:formData.budget,
      days:formData.days
    }
  });
}


const formData = reactive({
    "city": '',
    "budget": null,
    "days": null
});
const showCityPicker = ref(false);

// Cities come from the backend; this list is only a fallback if the backend is not reachable
const hotCities = ref(['Beijing', 'Shanghai', 'Guangzhou', 'Shenzhen', 'Chengdu', 'Hangzhou', "Xi'an", 'Chongqing'])
const cityColumns = ref([])
const refreshColumns = () => {
  cityColumns.value = hotCities.value.map(city => ({ text: city, value: city }))
}
refreshColumns()

onMounted(async () => {
  try {
    const res = await getCities()
    if (res && res.success && Array.isArray(res.data) && res.data.length) {
      hotCities.value = res.data
      refreshColumns()
    }
  } catch (e) {
    console.warn('Could not load cities from backend, using defaults')
  }
})

const handleConfirm = ({selectedOptions}) => {
  console.log(selectedOptions);
  formData.city = selectedOptions[0].value;
  showCityPicker.value = false;
}
const handleCancel = () => {
  showCityPicker.value = false;
}








</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <van-nav-bar title="Intelligent Travel Assistant" />
    </div>
    <van-notice-bar text="Help you plan your travel." left-icon="volume-o" />
    <div class="page-content">
      <div class="card">
        <div class="section-title">Plan Your Travel</div>
        <van-field
          v-model="formData.city"
          readonly
          is-link
          @click="showCityPicker = true"
          label="Destination"
          placeholder="Please choose your destination city." />
        <van-field v-model="formData.budget" type="number" label="Budget (RMB)" placeholder="Total budget per person, e.g. 3000" />
        <van-field v-model="formData.days" type="digit" label="Days" placeholder="1 to 7 days" />
        <div class="submit-btn">
          <van-button type="primary" round block @click="handleSubmit">Start Planning</van-button>
        </div>
      </div>
      <div class="card">
        <div class="section-title">Shortcut</div>
        <div class="shortcut-grid">
          <div class="shortcut-item" @click="goPage('/chat')">
            <van-icon name="chat-o" size="28" />
            <span>AI Chat</span>
          </div>
          <div class="shortcut-item" @click="goPage('/profile')">
            <van-icon name="user-o" size="28" />
            <span>Profile</span>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="section-title">Hot Destinations</div>
        <div class="city-tags">
          <div
            v-for="city in hotCities"
            :key="city"
            class="city-tag"
            :class="{'active': formData.city === city}"
            @click="formData.city = city">
            {{ city }}
          </div>
        </div>
      </div>
    </div>
  </div>
  <van-popup round v-model:show="showCityPicker" position="bottom" >
    <van-picker 
    title="Choose City"
    :columns="cityColumns"
    @confirm="handleConfirm" 
    @cancel="handleCancel" />
  </van-popup>
</template>

<style scoped>
.page-container{
    min-height: 100vh;
    background-color: #f5f5f5;
    padding-bottom: 70px;
}
.page-header{
    position: sticky;
    top: 0;
    z-index: 100;
    background-color: #fff;
}
.page-content{
    padding: 16px;
}
.card{
    background-color: #fff;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.section-title{
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 12px;
}
.submit-btn{
    margin-top: 16px;
}
.shortcut-grid{
    display: flex;
    gap: 12px;
}
.shortcut-item{
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    padding: 16px 0;
    background-color: #f5f5f5;
    border-radius: 8px;
    font-size: 14px;
    color: #333;
}
.city-tags{
    display: flex;
    flex-wrap: wrap;
    gap: 10px;

}
.city-tag{
    padding: 8px 16px;
    background-color: #f5f5f5;
    border-radius: 20px;
    font-size: 14px;
    color: #333;
    cursor: pointer;
}
.city-tag.active{
    background-color: #007AFF;
    color: #fff;
}
</style>
