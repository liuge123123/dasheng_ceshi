<template>
  <div class="fzError">
    <el-submenu
      v-if="menu.list && menu.list.length >= 1 && level < 1"
      :index="menu.menuId + ''"
      :popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'">
      <template slot="title">
        <icon-svg :name="menu.icon || ''" class="site-sidebar__menu-icon" v-if="level == 0"></icon-svg>
        <span>{{ menu.name }}</span>
      </template>
      <sub-menu
        v-for="item in menu.list"
        :level="level + 1"
        :key="item.menuId"
        :menu="item"
        :dynamicMenuRoutes="dynamicMenuRoutes">
      </sub-menu>
    </el-submenu>
    <el-menu-item v-else :index="menu.menuId + ''" @click="gotoRouteHandle(menu)">
      <icon-svg :name="menu.icon || ''" class="site-sidebar__menu-icon" v-if="level == 0"></icon-svg>
      <span>{{ menu.name }}</span>
    </el-menu-item>
  </div>
</template>

<script>
  import SubMenu from './main-sidebar-sub-menu'
  export default {
    name: 'sub-menu',
    props: {
      menu: {
        type: Object,
        required: true
      },
      dynamicMenuRoutes: {
        type: Array,
        required: true
      },
      level: {
        type: Number,
        default: 0
      }
    },
    components: {
      SubMenu
    },
    computed: {
      sidebarLayoutSkin: {
        get () { return this.$store.state.common.sidebarLayoutSkin }
      }
    },
    created() {

    },
    methods: {
      // 通过menuId与动态(菜单)路由进行匹配跳转至指定路由
      gotoRouteHandle (menu) {
        let route = this.dynamicMenuRoutes.filter(item => item.meta.menuId === menu.menuId)
        if (route.length >= 1) {
          this.$router.push({ name: route[0].name })
          this.$store.commit('common/updateThirdMenu', {})
        }else{
          if(menu.list && menu.list.length > 0 && this.level == 1) {
            this.$store.commit('common/updateThirdMenu', menu)
            let thirdFirstMenu = this.dynamicMenuRoutes.filter(item => item.meta.menuId === menu.list[0].menuId)
            this.$router.push({ name: thirdFirstMenu[0].name })
          }else{
            this.$store.commit('common/updateThirdMenu', {})
          }
        }
      }
    }
  }
</script>
