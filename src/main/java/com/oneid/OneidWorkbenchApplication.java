/* Copyright (c) 2024 openEuler Community
 EasySoftware is licensed under the Mulan PSL v2.
 You can use this software according to the terms and conditions of the Mulan PSL v2.
 You may obtain a copy of Mulan PSL v2 at:
     http://license.coscl.org.cn/MulanPSL2
 THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 See the Mulan PSL v2 for more details.
*/

package com.easysoftware;

import com.baomidou.mybatisplus.autoconfigure.DdlApplicationRunner;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.easysoftware.*"})
@MapperScan("com.easysoftware.infrastructure.mapper")
public class EasysoftwareApplication {

    /**
     * Main method for the Java application.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EasysoftwareApplication.class, args);
    }

    /**
     * Bean definition for DdlApplicationRunner.
     *
     * @param ddlList List of DDLs (Data Definition Language)
     * @return An instance of DdlApplicationRunner
     */
    @Bean
    public DdlApplicationRunner ddlApplicationRunner(@Autowired(required = false) final List ddlList) {
        return new DdlApplicationRunner(ddlList);
    }
}
