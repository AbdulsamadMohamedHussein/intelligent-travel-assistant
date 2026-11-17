<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute();

</script>

<template>
  <router-view />

<van-tabbar v-if="['home', 'chat', 'profile'].includes(route.name)" route>
    <van-tabbar-item to="/" icon="home-o">Home</van-tabbar-item>
    <van-tabbar-item to="/chat" icon="chat-o">Chat</van-tabbar-item>
    <van-tabbar-item to="/profile" icon="user-o">Profile</van-tabbar-item>
  </van-tabbar>
</template>

<style scoped>
</style>
