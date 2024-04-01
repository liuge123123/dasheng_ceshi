<template>
  <div class="third-menu">
    <div class="top-menu">{{menu.name}}</div>
    <el-menu>
      <template v-for="item in menu.list">
        <el-submenu v-if="item.list && item.list.length >= 1" :index="item.menuId + ''">
          <template slot="title">
            {{ item.name }}
          </template>
          <el-menu-item v-for="item1 in item.list" :key="item1.menuId" :menu="item1" @click="menuClick(item1)">
            {{ item1.name }}
          </el-menu-item>
        </el-submenu>
        <el-menu-item v-else :index="item.menuId + ''"  @click="menuClick(item)">
          {{ item.name }}
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>

<script>
  export default {
    name: 'thid-menu',
    props: {
      menu: {
        type: Object,
        required: true
      }
    },
    components: {
    },
    computed: {

    },
    data(){
      return {
        dynamicMenuRoutes: []
      }
    },
    created() {
      this.dynamicMenuRoutes = JSON.parse(sessionStorage.getItem('dynamicMenuRoutes') || '[]')
    },
    methods: {
      menuClick(menu){
        let route = this.dynamicMenuRoutes.filter(item => item.meta.menuId === menu.menuId)
        this.$router.push({ name: route[0].name })
      }
    }
  }
</script>
<style scoped lang="scss">
  .third-menu {
    height: 100%;
    border-right: 1px solid #eeeeee;
    /deep/ .el-menu{
      border-right: none;
    }
    .top-menu {
      line-height: 56px;
      padding: 0 20px;
      border-bottom: 1px solid #eeeeee;
      font-weight: bold;
    }
  }
</style>
