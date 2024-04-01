<template>
  <nav class="site-navbar" :class="'site-navbar--' + navbarLayoutType">
    <div class="site-navbar__header">
      <h1 class="site-navbar__brand" @click="$router.push({ name: 'home' })">
        <a class="site-navbar__brand-lg" href="javascript:;">
          <img :src="orgInfo.sysLogo" /> {{orgInfo.sysTitle}}
        </a>
        <a class="site-navbar__brand-mini" href="javascript:;">
          <img :src="orgInfo.sysLogo" />
        </a>
      </h1>
    </div>
    <div class="site-navbar__body clearfix">
      <el-menu class="site-navbar__menu top-menu" mode="horizontal" default-active="1" >
        <el-menu-item class="site-navbar__switch" index="0" @click="sidebarFold = !sidebarFold">
          <i class="el-icon-s-fold" v-show="sidebarFold"></i>
          <i class="el-icon-s-unfold" v-show="!sidebarFold"></i>
        </el-menu-item>
        <el-menu-item v-for="(item, index) in menuList" :key="index" :index="(index + 1 + '')" @click="menuItemClick(item, index)">
          <icon-svg class="icon" :name="item.icon || ''"></icon-svg> {{item.name}}
        </el-menu-item>
      </el-menu>
      <el-menu class="site-navbar__menu site-navbar__menu--right" mode="horizontal">
        <el-menu-item class="site-navbar__avatar" index="3">
          <el-dropdown placement="bottom">
            <div class="avatar-group">
              <img class="avatar" v-if="avatar" :src="avatar" :alt="userName" />
              <img class="avatar"  v-else src="~@/assets/img/boy.png"/>
              <div class="userInfo">
                <span class="name">{{ userName }}</span>
              </div>
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item @click.native="updatePasswordHandle()">
                <span class="el-dropdown-link drop-item">修改密码</span>
              </el-dropdown-item>
              <el-dropdown-item @click.native="userInfoGet()">
                <span class="el-dropdown-link drop-item">个人信息</span>
              </el-dropdown-item>
              <el-dropdown-item @click.native="logoutHandle()">
                <span class="el-dropdown-link drop-item">退出</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-menu-item>
      </el-menu>
      <!-- 弹窗, 修改密码 -->
      <update-password v-if="updatePassowrdVisible" ref="updatePassowrd"></update-password>
      <!--个人信息-->
      <user-info ref="userInfo"></user-info>
    </div>
  </nav>
</template>

<script>
  import UpdatePassword from "./main-navbar-update-password";
  import { clearLoginInfo } from "@/utils";
  import userInfo from "./modules/sys/user-info";
  export default {
    data() {
      return {
        updatePassowrdVisible: false,
      };
    },
    components: {
      UpdatePassword,
      userInfo
    },
    computed: {
      menuList: {
        get () { return this.$store.state.common.menuList },
        set (val) { this.$store.commit('common/updateMenuList', val) }
      },
      navbarLayoutType: {
        get() {
          return this.$store.state.common.navbarLayoutType;
        },
      },
      sidebarFold: {
        get() {
          return this.$store.state.common.sidebarFold;
        },
        set(val) {
          this.$store.commit("common/updateSidebarFold", val);
        },
      },
      mainTabs: {
        get() {
          return this.$store.state.common.mainTabs;
        },
        set(val) {
          this.$store.commit("common/updateMainTabs", val);
        },
      },
      userInfo() {
        return this.$store.state.user.userInfo;
      },
      userName: {
        get() {
          return this.userInfo.name;
        },
      },
      avatar: {
        get() {
          return this.userInfo.avatar;
        },
      },
      orgInfo() {
        return this.$store.state.user.orgInfo;
      },
      topMenu: {
        get () { return this.$store.state.common.topMenu },
        set (val) { this.$store.commit('common/updateTopMenu', val) }
      }
    },

    mounted() {
      this.topMenu = this.menuList[0]
    },

    methods: {
      menuItemClick(item, index){
        this.topMenu = item
      },
      // 修改密码
      updatePasswordHandle() {
        this.updatePassowrdVisible = true;
        this.$nextTick(() => {
          this.$refs.updatePassowrd.init();
        });
      },
      //个人信息
      userInfoGet(){
        this.$nextTick(() => {
          this.$refs.userInfo.init();
        });
      },
      // 退出
      logoutHandle() {
        this.$confirm(`确定进行[退出]操作?`, "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        })
          .then(() => {
            this.$http({
              url: this.$http.adornUrl("/sys/logout"),
              method: "post",
              data: this.$http.adornData(),
            }).then(({ data }) => {
              if (data && data.code === 0) {
                clearLoginInfo();
                this.$router.push({ name: "login" });
                this.$ws.close();
              }
            });
          })
          .catch(() => {});
      }
    },
  };
</script>

<style scoped lang="scss">


  .avatar-group {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 64px;
    line-height: 64px;

    .avatar {
      width: 36px;
      height: 36px;
      object-fit: cover;
      border-radius: 2px;
    }

    .userInfo {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 36px;
      margin-left: 10px;

      .name {
        text-align: center;
        line-height: 18px;
        font-size: 14px;
        color: #1f2329;
      }
    }
  }

  .site-navbar__body{
    /deep/ .el-menu.el-menu--horizontal{
      border: none;
      .el-menu-item{
        border: none!important;
      }
    }
  }

  .top-menu {
    .el-menu-item {
      &.is-active {
        color: $--color-primary;
      }
    }
    /deep/ .el-menu-item{
      display: flex;
      align-items: center;
      .icon {
        width: 16px;
        height: 16px;
        margin-right: 3px;
      }
    }
  }
</style>
