import { createRouter, createWebHistory } from "vue-router";
import Homepage from "../components/homepage/homepage.vue";
import Login from "../components/login/login.vue";
import Register from "../components/register/register.vue";
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "login",
      component: Login,
    },
    {
      path: "/register",
      name: "Register",
      component: Register,
    },
    {
      path: "/homepage",
      name: "homepage",
      component: Homepage,
    },
  ],
});
export default router;
