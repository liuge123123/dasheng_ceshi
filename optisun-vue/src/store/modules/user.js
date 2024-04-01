export default {
  namespaced: true,
  state: {
    id: 0,
    name: '',
    userInfo: {},
    orgInfo:{},
  },
  mutations: {
    updateId (state, id) {
      state.id = id
    },
    updateName (state, name) {
      state.name = name
    },
    updateUserInfo (state, userInfo) {
      state.userInfo = userInfo
    },
    updateOrgInfo (state, orgInfo) {
      state.orgInfo = orgInfo
    }
  }
}
