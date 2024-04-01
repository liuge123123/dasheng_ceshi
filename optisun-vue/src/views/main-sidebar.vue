<template>
  <aside class="site-sidebar" :class="'site-sidebar--' + sidebarLayoutSkin">
    <div class="site-sidebar__inner">
      <el-menu
        :default-active="menuActiveName || 'home'"
        :collapse="sidebarFold"
        :collapseTransition="false"
        class="site-sidebar__menu">
        <el-menu-item index="home" @click="homeClick">
          <icon-svg name="home-filling" class="site-sidebar__menu-icon"></icon-svg>
          <span slot="title">首页</span>
        </el-menu-item>
        <el-menu-item index="home" @click="kefuClick">
          <icon-svg name="kefu" class="site-sidebar__menu-icon"></icon-svg>
          <span slot="title">客服设置</span>
        </el-menu-item>
        <sub-menu
          v-for="menu in leftMenuList"
          :level="0"
          :key="menu.menuId"
          :menu="menu"
          :dynamicMenuRoutes="dynamicMenuRoutes">
        </sub-menu>
      </el-menu>
    </div>
  </aside>
</template>

<script>
  import SubMenu from './main-sidebar-sub-menu'
  import { isURL } from '@/utils/validate'
  export default {
    data () {
      return {
        dynamicMenuRoutes: []
      }
    },
    components: {
      SubMenu
    },
    computed: {
      topMenu: {
        get () { return this.$store.state.common.topMenu },
        set (val) { this.$store.commit('common/updateTopMenu', val) }
      },
      sidebarLayoutSkin: {
        get () { return this.$store.state.common.sidebarLayoutSkin }
      },
      sidebarFold: {
        get () { return this.$store.state.common.sidebarFold }
      },
      leftMenuList: {
        get () {
          return this.topMenu.list
        }
      },
      menuList: {
        get () { return this.$store.state.common.menuList },
        set (val) { this.$store.commit('common/updateMenuList', val) }
      },
      menuActiveName: {
        get () { return this.$store.state.common.menuActiveName },
        set (val) { this.$store.commit('common/updateMenuActiveName', val) }
      },
      mainTabs: {
        get () { return this.$store.state.common.mainTabs },
        set (val) { this.$store.commit('common/updateMainTabs', val) }
      },
      mainTabsActiveName: {
        get () { return this.$store.state.common.mainTabsActiveName },
        set (val) { this.$store.commit('common/updateMainTabsActiveName', val) }
      }
    },
    watch: {
      $route: 'routeHandle'
    },
    created () {
      this.menuList = JSON.parse(sessionStorage.getItem('menuList') || '[]')
      this.dynamicMenuRoutes = JSON.parse(sessionStorage.getItem('dynamicMenuRoutes') || '[]')
      this.routeHandle(this.$route)
    },
    methods: {
      homeClick(){
        this.$router.push({ name: 'home' })
      },
      kefuClick(){
        this.$router.push({name: "kefusetting"})
      },
      // 路由操作
      routeHandle (route) {
        if (route.meta.isTab) {
          // tab选中, 不存在先添加
          var tab = this.mainTabs.filter(item => item.name === route.name)[0]
          if (!tab) {
            if (route.meta.isDynamic) {
              route = this.dynamicMenuRoutes.filter(item => item.name === route.name)[0]
              if (!route) {
                return console.error('未能找到可用标签页!')
              }
            }
            tab = {
              menuId: route.meta.menuId || route.name,
              name: route.name,
              title: route.meta.title,
              type: isURL(route.meta.iframeUrl) ? 'iframe' : 'module',
              iframeUrl: route.meta.iframeUrl || '',
              params: route.params,
              query: route.query
            }
            this.mainTabs = this.mainTabs.concat(tab)
          }
          this.mainTabsActiveName = tab.name
        }
        this.menuSet(route)
      },
      menuSet(route){
        if(route.name == 'home'){
          this.$store.commit('common/updateThirdMenu', {})
          return
        }
        // 菜单显示优化
        let currMenuId = route.meta.menuId;

        let resultList = []
        let indexOfMenu = (list, id) => {
          if(list && list.length > 0){
            for(let i = 0; i < list.length; i++){
              if(list[i].menuId == id){
                return list[i]
              }else if(list[i].list && list[i].list.length > 0){
                let res = indexOfMenu(list[i].list, id)
                if(res != null) {
                  return res
                }
              }
            }
          }
          return null
        }
        let func = (list, id) => {
          let result = indexOfMenu(list, id)
          resultList.push(result)
          if(result.parentId != 0){
            func(list, result.parentId)
          }
        }
        func(this.menuList, currMenuId)

        if(resultList.length == 4){
          this.$store.commit('common/updateThirdMenu', resultList[1])
        }else{
          this.$store.commit('common/updateThirdMenu', {})
        }

        this.$store.commit('common/updateTopMenu', resultList[resultList.length - 1])

      }
    }
  }
</script>
<style scoped>

  .el-menu--collapse .fzError /deep/ .el-menu-item span {
    height: 0;
    width: 0;
    overflow: hidden;
    visibility: hidden;
    display: inline-block;
  }
  .el-menu--collapse .fzError /deep/ .el-submenu__title span,
  .el-menu--collapse .fzError /deep/ .el-submenu__icon-arrow {
    display: none;
  }
</style>
