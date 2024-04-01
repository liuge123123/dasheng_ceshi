<template>
	<view>
		<view v-html="content"></view>
	</view>
</template>

<script>
	export default {
		name: 'rich',
		props: {
			code: String
		},
		components: {},
		data() {
			return {
				content: ''
			}
		},
		mounted() {
			this.getData()
		},
		methods: {
			getData() {
				this.$uni.getContent(this.code).then(res => {
					this.content = res.content
				})
			}
		}
	}
</script>