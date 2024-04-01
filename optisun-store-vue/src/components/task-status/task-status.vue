<template>
	<view>
		<text>{{status == 0 ? $t('team.no') : $t('team.yes')}}</text>
	</view>
</template>

<script>
	export default {
		name:"task-status",
		props: {
			id: {
				type: Number,
				default: 0
			}
		},
		data() {
			return {
				status: 0
			};
		},
		mounted() {
			this.getData()
		},
		methods: {
			getData(){
				if(this.id == 0){
					return
				}
				this.$get('/app/account/task/status', {
					custId: this.id 
				}).then(res => {
					if(res.code == 0){
						this.status = res.data
					}
				})
			}
		}
	}
</script>

<style>

</style>