package com.oneid.infrastructure.personalapi;

import com.oneid.application.personalapi.dto.PersonalApiTokenDTO;
import com.oneid.application.personalapi.dto.PersonalApiTokenDetailDTO;
import com.oneid.application.personalapi.dto.PersonalApiTokenIdDTO;
import com.oneid.application.personalapi.dto.UserInfoDTO;
import com.oneid.application.personalapi.vo.PersonalApiPermissionVO;
import com.oneid.application.personalapi.vo.PersonalApiTokenVO;
import com.oneid.common.constant.TableConstant;
import com.oneid.common.exception.CustomException;
import com.oneid.common.exception.ErrorCode;
import com.oneid.common.utils.CharacterUtil;
import com.oneid.common.utils.ResultUtil;
import com.oneid.infrastructure.dapr.DaprSqlActuator;
import com.oneid.infrastructure.personalapi.dataobject.PersonalApiPermissionDO;
import com.oneid.infrastructure.personalapi.dataobject.PersonalApiTokenDO;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonalApiMapper {

    private static final Logger log = LoggerFactory.getLogger(PersonalApiMapper.class);
    @Autowired
    private DaprSqlActuator daprSqlActuator;

    public ResponseEntity getAllPermissions() {
        String selectAllPermissionsSql = "select * from ?";
        List<String> params = new ArrayList<>();
        params.add(TableConstant.PERSONAL_API_PERMISSION_TABLE);
        List<PersonalApiPermissionDO> result = daprSqlActuator.query(selectAllPermissionsSql, params);
        return ResultUtil.result(HttpStatus.OK, "success", convertPermissionDoToPermissionVo(result));
    }

    public ResponseEntity getPersonalApiTokensByUserId(String userId) {
        String selectTokensByUserIdSql = "select * from ? where userId = ?";
        List<String> params = new ArrayList<>();
        params.add(TableConstant.PERSONAL_API_TOKEN_TABLE);
        params.add(userId);
        List<PersonalApiTokenDO> tokens = daprSqlActuator.query(selectTokensByUserIdSql, params);
        List<PersonalApiTokenVO> result = null;
        try {
            result = convertPersonalTokensDoToPersonalTokensVo(tokens);
        } catch (Exception e) {
            return ResultUtil.result(HttpStatus.BAD_REQUEST, "failed", e.getMessage());
        }
        return ResultUtil.result(HttpStatus.OK, "success", result);
    }

    public void deletePersonalApiToken(PersonalApiTokenIdDTO personalApiTokenIdDTO) {
        String deletePersonalApiTokenSql = "delete from ? where id = ?";
        List<String> params = new ArrayList<>();
        params.add(TableConstant.PERSONAL_API_TOKEN_TABLE);
        params.add(personalApiTokenIdDTO.getId());
        daprSqlActuator.exeSql(deletePersonalApiTokenSql, params);
        log.info("delete personal api token success, id: {}", personalApiTokenIdDTO.getId());
    }

    public void createPersonalApiToken(PersonalApiTokenDTO personalApiTokenDTO, UserInfoDTO userInfoDTO) {
        String createTokenSql = """
                INSERT INTO ? (
                    id, token, name, permission_ids, user_id, user_name, expire_at, create_at, update_at
                ) VALUES (
                    UUID(), ?, ?, ?, ?, ?, ?, NOW(), NOW()
                )
                """;
        List<String> params = new ArrayList<>();
        params.add(TableConstant.PERSONAL_API_TOKEN_TABLE);
        String token = CharacterUtil.generate16UUID();
        while (checkTokenExist(token)) {
            token = CharacterUtil.generate16UUID();
        }
        params.add(token);
        params.add(personalApiTokenDTO.getName());
        params.add(personalApiTokenDTO.getPermissionIds());
        params.add(userInfoDTO.getUserId());
        params.add(userInfoDTO.getUsername());
        Long expireTime = System.currentTimeMillis() + personalApiTokenDTO.getDayNum() * 24 * 60 * 60 * 1000;
        params.add(String.valueOf(expireTime));
        daprSqlActuator.exeSql(createTokenSql, params);
        log.info("create personal api token success, param: {}", params);
    }

    public void updatePersonalApiToken(PersonalApiTokenDetailDTO personalApiTokenDetailDTO) {
        Long expireTime = System.currentTimeMillis() + personalApiTokenDetailDTO.getDayNum() * 24 * 60 * 60 * 1000;
        String updatePersonalApiTokenSql = "update ? set name = ? and permission_ids = ? and expire_at = ? where id = ?";
        List<String> params = new ArrayList<>();
        params.add(TableConstant.PERSONAL_API_TOKEN_TABLE);
        params.add(personalApiTokenDetailDTO.getName());
        params.add(personalApiTokenDetailDTO.getPermissionIds());
        params.add(String.valueOf(expireTime));
        params.add(personalApiTokenDetailDTO.getId());
        daprSqlActuator.exeSql(updatePersonalApiTokenSql, params);
        log.info("update personal api token success, params:{}", params);
    }

    public void refreshPersonApiToken(PersonalApiTokenIdDTO personalApiTokenIdDTO) {
        String refreshTokenSql = "update ? set token = ? where id = ?";
        List<String> params = new ArrayList<>();
        params.add(TableConstant.PERSONAL_API_TOKEN_TABLE);
        String token = CharacterUtil.generate16UUID();
        while (checkTokenExist(token)) {
            token = CharacterUtil.generate16UUID();
        }
        params.add(token);
        params.add(personalApiTokenIdDTO.getId());
        daprSqlActuator.exeSql(refreshTokenSql, params);
        log.info("refresh personal api token success, param: {}", params);
    }

    public List<String> selectPermissionNameByPermissionIds(String permissionIds) {
        if (StringUtils.isBlank(permissionIds) || !checkPermissionIds(permissionIds)) {
            log.error("permissionNames error, permissions id:{}", permissionIds);
            throw new CustomException(ErrorCode.PERMISSION_ID_ERROR);
        }
        String selectPermissionNameSql = "select * from ? where id in ?";
        List<String> params = new ArrayList<>();
        params.add(TableConstant.PERSONAL_API_PERMISSION_TABLE);
        params.add(permissionIds);
        List<PersonalApiPermissionDO> personalApiPermissionDOs = daprSqlActuator.query(selectPermissionNameSql, params);
        return personalApiPermissionDOs.stream().map(PersonalApiPermissionDO::getName).collect(Collectors.toList());
    }

    public List<PersonalApiTokenDO> selectPersonalApiTokensByToken(String token) {
        String selectPersonalApiTokenSql = "select * from ? where token = ?";
        List<String> params = new ArrayList<>();
        params.add(TableConstant.PERSONAL_API_TOKEN_TABLE);
        params.add(token);
        return daprSqlActuator.query(selectPersonalApiTokenSql, params);
    }

    public boolean checkPermissionIds(String permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return false;
        }
        List<String> idList = CharacterUtil.csvToStringList(permissionIds);
        if (idList.isEmpty()) {
            return false;
        }
        String checkPermissionIdsSql = "SELECT COUNT(*) = ? AS all_ids_exist FROM ? WHERE id IN (?)";
        List<String> params = new ArrayList<>();
        params.add(String.valueOf(idList.size()));
        params.add(TableConstant.PERSONAL_API_PERMISSION_TABLE);
        params.add(permissionIds);
        List<Boolean> ids = daprSqlActuator.query(checkPermissionIdsSql, params);
        if (ids.isEmpty()) {
            return false;
        }
        return ids.get(0);
    }

    private boolean checkTokenExist(String token) {
        List<PersonalApiTokenDO> tokens = selectPersonalApiTokensByToken(token);
        return !tokens.isEmpty();
    }

    private List<PersonalApiTokenVO> convertPersonalTokensDoToPersonalTokensVo(List<PersonalApiTokenDO> tokens) {
        return tokens.stream()
                .map(this::convertPersonalTokenDoToPersonalTokenVo)
                .collect(Collectors.toList());
    }

    private PersonalApiTokenVO convertPersonalTokenDoToPersonalTokenVo(PersonalApiTokenDO token) {
        PersonalApiTokenVO vo = new PersonalApiTokenVO();
        vo.setId(token.getId());
        vo.setName(token.getName());
        vo.setUpdateAt(String.valueOf(token.getExpireAt()));
        vo.setPermissionIds(token.getPermissionIds());
        vo.setCreateAt(token.getCreateAt());
        vo.setUpdateAt(token.getUpdateAt());
        List<String> permissionNames = selectPermissionNameByPermissionIds(token.getPermissionIds());
        vo.setPermissionNames(CharacterUtil.listToCsv(permissionNames));
        return vo;
    }

    private List<PersonalApiPermissionVO> convertPermissionDoToPermissionVo(List<PersonalApiPermissionDO> permissions) {
        return permissions.stream()
                .map(this::convertPermissionDoToPermissionVo)
                .collect(Collectors.toList());
    }

    private PersonalApiPermissionVO convertPermissionDoToPermissionVo(PersonalApiPermissionDO permission) {
        PersonalApiPermissionVO vo = new PersonalApiPermissionVO();
        vo.setId(permission.getId());
        vo.setName(permission.getName());
        vo.setDescription(permission.getDescription());
        return vo;
    }
}
