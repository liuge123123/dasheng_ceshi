// 表单校验
export default (that, rules) => {
	for (var key in that) {
	       if (that.hasOwnProperty(key) && rules.hasOwnProperty(key)){
	           // 校验是否为空
	           if (rules[key].required) {
					if(that[key].length == 0){
						// 弹框提示
						uni.showToast({
							title: rules[key].message,
							icon: 'none'
						});
						return false;
					}
	           }
	           if (rules[key].validator != null && rules[key].validator != '' && that[key].length > 0) {
					let reg =  new RegExp(rules[key].validator)
					console.log(reg)
					console.log(key)
					   // 校验 正则匹配
					   if (!reg.test(that[key])) {
							uni.showToast({
								title: rules[key].ValidateMsg,
								icon: 'none'
							});
							return false;
					   }
			  }
	   }
	}
	return true
}

