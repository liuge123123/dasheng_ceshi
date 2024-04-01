
package com.tcc.modules.exercise.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.modules.exercise.entity.SignInEntity;

/**
 * 签到信息
 *
 * @author
 * @email
 * @date 2022-08-06 08:55:20
 */
public interface SignInService extends IService<SignInEntity> {

    void excute(Long custId);

}

