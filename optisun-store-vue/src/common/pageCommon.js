export default {
	data() {
		return {
			pageStatus: 'loading'
		}
	},
	methods: {
		showPageContent(){
			this.pageStatus = 'content'
		},
		showPageError(error){
			this.pageStatus = 'error'
			if(this.$refs.container){
				if(error){
					this.$refs.container.showError(error)
				}
			}else{
				this.$uni.showError(error)
			}
		},
		showPageEmpty(){
			this.pageStatus = 'empty'
		},
		showPageLoading(){
			this.pageStatus = 'loading'
		}
	}
}
