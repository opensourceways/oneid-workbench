/* Copyright (c) 2025 openEuler Community
 oneid-workbench is licensed under the Mulan PSL v2.
 You can use this software according to the terms and conditions of the Mulan PSL v2.
 You may obtain a copy of Mulan PSL v2 at:
     http://license.coscl.org.cn/MulanPSL2
 THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 See the Mulan PSL v2 for more details.
*/

package com.oneid.application.personalapi;


import com.oneid.application.personalapi.dto.UserInfoDTO;

public interface AccountApiService {
    /**
     * 通过ygToken和utToken在账号服务中获取用户信息
     * @param utToken _U_T_
     * @param ygToken _Y_G_
     * @return 用户信息结构体
     */
    UserInfoDTO getUserInfo(String utToken, String ygToken);

    void checkLogin(String utToken, String ygToken);
}
