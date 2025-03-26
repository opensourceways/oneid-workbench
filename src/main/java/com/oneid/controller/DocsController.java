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

package com.oneid.controller;

import com.oneid.common.utils.MarkdownUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "/oneid-workbench")
@Controller
public class DocsController {
    private static final String SECGEAR_FILE_PATH = "/static/docs/secGear.md";

    /**
     * 获取api文档.
     *
     * @param model 模型渲染
     * @return 接口信息
     */
    @GetMapping("/docs")
    public String showDoc(Model model) {
        try {
            String markdown = new String(
                    this.getClass().getResourceAsStream(SECGEAR_FILE_PATH)
                            .readAllBytes()
            );
            // 转换 Markdown 为 HTML
            String htmlContent = MarkdownUtil.parseToHtml(markdown);
            model.addAttribute("content", htmlContent);
            return "docTemplate";
        } catch (Exception e) {
            model.addAttribute("error", "get docs failed");
            return "error";
        }
    }
}
