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

package com.oneid.application.personalapi.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalApiTokenVO {
    /**
     * id.
     */
    private String id;

    /**
     * 令牌描述名.
     */
    private String name;

    /**
     * 权限列表.
     */
    private String permissionIds;

    /**
     * 权限列表.
     */
    private String permissionNames;

    /**
     * 过期时间.
     */
    private String expireAt;

    /**
     * 创建时间.
     */
    private String createAt;

    /**
     * 更新时间.
     */
    private String updateAt;
}
