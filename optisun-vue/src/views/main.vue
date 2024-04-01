<template>
  <div
    class="site-wrapper"
    :class="{ 'site-sidebar--fold': sidebarFold, 'site-sidebar--third': thirdMenu.name }"
    v-loading.fullscreen.lock="loading"
    element-loading-text="拼命加载中">
    <template v-if="!loading">
      <main-navbar ref="mainNavbar"/>
      <div class="site-content-wrap" :style="{ 'height': documentClientHeight + 'px'}">
        <main-sidebar/>
        <div v-if="thirdMenu.name" class="site-content__wrapper" style="width: 200px;background: #ffffff" :style="{ 'height': documentClientHeight + 'px'}">
          <main-side-third-menu :menu="thirdMenu"/>
        </div>
        <div class="site-content__wrapper"
             :style="{ 'min-height': documentClientHeight + 'px','width': sidebarFold ? 'calc(100% - 64px)': 'calc(100% - 200px)', 'padding-top': $route.meta.isTab ? '40px' : '0'}">
          <main-content v-if="!$store.state.common.contentIsNeedRefresh"/>
        </div>
      </div>
    </template>
  </div>
</template>
<style lang="scss" scoped>
  .clearfix:after {
    content: ".";
    display: block;
    height: 0;
    visibility: hidden;
    clear: both !important;
  }

  .site-content-wrap {
    position: relative;
    display: flex;
    margin-top: 64px;
    overflow: hidden;
  }

  .site-content__wrapper {
    margin: 0;
    overflow-y: auto;
  }
</style>
<script>
  import MainNavbar from './main-navbar'
  import MainSidebar from './main-sidebar'
  import MainContent from './main-content'
  import MainSideThirdMenu from './main-sidebar-third-menu'

  export default {
    provide() {
      return {
        // 刷新
        refresh() {
          this.$store.commit('common/updateContentIsNeedRefresh', true)
          this.$nextTick(() => {
            this.$store.commit('common/updateContentIsNeedRefresh', false)
          })
        }
      }
    },
    data() {
      return {
        loading: true
      }
    },
    components: {
      MainNavbar,
      MainSidebar,
      MainContent,
      MainSideThirdMenu
    },
    computed: {
      documentClientHeight: {
        get() {
          return this.$store.state.common.documentClientHeight
        },
        set(val) {
          this.$store.commit('common/updateDocumentClientHeight', val)
        }
      },
      sidebarFold: {
        get() {
          return this.$store.state.common.sidebarFold
        }
      },
      userId: {
        get() {
          return this.$store.state.user.id
        },
        set(val) {
          this.$store.commit('user/updateId', val)
        }
      },
      userName: {
        get() {
          return this.$store.state.user.name
        },
        set(val) {
          this.$store.commit('user/updateName', val)
        }
      },
      userInfo: {
        get() {
          return this.$store.state.user.userInfo
        },
        set(val) {
          this.$store.commit('user/updateUserInfo', val)
        }
      },
      orgInfo: {
        get() {
          return this.$store.state.user.orgInfo
        },
        set(val) {
          this.$store.commit('user/updateOrgInfo', val)
        }
      },
      menuList: {
        get () { return this.$store.state.common.menuList },
        set (val) { this.$store.commit('common/updateMenuList', val) }
      },
      thirdMenu: {
        get() {
          return this.$store.state.common.thirdMenu
        },
        set(val) {
          this.$store.commit('common/updateThirdMenu', val)
        }
      }
    },
    created() {
    },
    mounted() {
      this.resetDocumentClientHeight()
      this.getUserInfo()
    },
    methods: {
      // 重置窗口可视高度
      resetDocumentClientHeight() {
        this.documentClientHeight = document.documentElement['clientHeight'] - 64
        window.onresize = () => {
          this.documentClientHeight = document.documentElement['clientHeight'] - 64
        }
      },
      // 获取当前管理员信息
      getUserInfo() {
        this.$http({
          url: this.$http.adornUrl('/sys/user/info'),
          method: 'get',
          params: this.$http.adornParams()
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.loading = false
            this.userId = data.user.userId
            this.userName = data.user.username
            this.userInfo = data.user
            this.orgInfo = data.orgInfo
            let url = window.UEDITOR_CONFIG.serverUrl
            window.UEDITOR_CONFIG.serverUrl = url + "?orgId=" + data.user.orgId
            this.initWS(data.extInfo)
          }
        })
      },
      initWS(userInfo){
        this.$msgClient.connectWs()
        this.$msgClient.subscribe(1, '/exchange/crm.topic/notice', res => {
          let body = JSON.parse(res.body)
          let msg = JSON.parse(body)
          if(!msg.groupIds || msg.groupIds == ''){
            // 没有接收组
            return
          }
          let msgGroupId = msg.groupIds
          let needSend = false
          if(msgGroupId == '*' || msgGroupId == '[["*"]]'){
            // *代表所有人
            needSend = true
          }else {
            let idInMsgGroups = (msgGroupIds, id) => {
              for(let k = 0; k < msgGroupIds.length; k++){
                if(msgGroupIds[k].indexOf(id) != -1){
                  return true
                }
              }
              return false
            }
            let deptId = userInfo.deptId
            let msgGroupIds = JSON.parse(msgGroupId)
            needSend = idInMsgGroups(msgGroupIds, deptId)
          }
          if(needSend) {
            this.$notify({
              title: '系统公告',
              message: msg.contents,
              duration: 0
            })
          }
        })
      }
    }
  }
</script>
