import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Profile from '../views/Profile.vue'
import Chat from '../views/Chat.vue'
import Detail from '../views/Detail.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: Home,
  },
  {
    path: '/profile',
    name: 'profile',
    component: Profile,
  },  {
    path: '/chat',
    name: 'chat',
    component: Chat,
  },
  {
    path: '/detail',
    name: 'Detail',
    component: Detail,
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router
