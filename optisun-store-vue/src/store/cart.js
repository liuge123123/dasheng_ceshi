export default {
	namespaced: true,
	state: {
		goodsList: uni.getStorageSync("shop_cart") || []
	},
	mutations: {
		addGoods(state, goods) {
			let flag = false;
			for (var i = 0; i < state.goodsList.length; i++) {
				let goodsItem = state.goodsList[i]
				if(goodsItem.id == goods.id){
					goodsItem.num ++;
					flag = true;
					break;
				}
			}
			if(!flag){
				state.goodsList.push(goods);
			}
			uni.setStorageSync("shop_cart", state.goodsList)
		},
		subGoods(state, goods) {
			for (var i = 0; i < state.goodsList.length; i++) {
				let goodsItem = state.goodsList[i]
				if(goodsItem.id == goods.id){
					goodsItem.num --;
					break;
				}
			}
			uni.setStorageSync("shop_cart", state.goodsList)
		},
		removeGoods(state, id) {
			for (var i = 0; i < state.goodsList.length; i++) {
				let goods = state.goodsList[i];
				if (goods.id == id) {
					state.goodsList.splice(i, 1);
					break;
				}
			}
			uni.setStorageSync("shop_cart", state.goodsList)
		},
		removeGoodsList(state, ids) {
			for (var i = 0; i < ids.length; i++) {
				let id = ids[i]
				for (var i = 0; i < state.goodsList.length; i++) {
					let goods = state.goodsList[i];
					if (goods.id == id) {
						state.goodsList.splice(i, 1);
						break;
					}
				}
			}
			uni.setStorageSync("shop_cart", state.goodsList)
		}
	},
	getters: {
		goodsNum(state){
			let num = 0;
			state.goodsList.forEach(item => {
				num += item.num;
			})
			return num
		},
		goodsList(state){
			return state.goodsList
		}
	}
}
