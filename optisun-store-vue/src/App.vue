<script>
	export default {
		globalData: {
			siteInfo: null
		},
		onLaunch: function() {},
		onShow: function() {},
		onHide: function() {}
	}
</script>

<style>
	/*每个页面公共css */
	@import url("static/css/iconfont.css");

	* {
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial,
			'Noto Sans', sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol',
			'Noto Color Emoji';
	}

	

	body {
		max-width: 750px;
		margin: 0 auto;
	}

	page {
		background: #f8f8f8;
		height: 100%;
		position: relative;
	}

	img {
		max-width: 100%;
	}
	
	::-webkit-scrollbar {
		display: none;
		width: 0 !important;
		height: 0 !important;
		-webkit-appearance: none;
		background: transparent;
		color: transparent;
	}
</style>
