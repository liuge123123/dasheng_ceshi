let config = {}

if (process.env.NODE_ENV === 'development') {
	let baseUrl = 'http://localhost:8099/api/v1'
	let baseWs = 'wss://localhost:8099/ws'
	let imgUrl = 'http://192.168.0.107'
	config = {
		baseUrl: baseUrl,
		imgUrl:imgUrl,
		websocketUrl: baseWs
	}
} else {
	let baseUrl = 'https://admin.datarobotnew.com/api/v1'
	let baseWs = 'wss://localhost:8099/ws'
	let imgUrl = 'http://localhost:8099/api/v1'
	config = {
		baseUrl: baseUrl,
		imgUrl:imgUrl,
		websocketUrl: baseWs
	}
}

export default config
