package com.oneid.infrastructure.personalapi;

import com.alibaba.fastjson2.JSONObject;
import com.oneid.application.personalapi.dto.*;
import com.oneid.application.personalapi.vo.PersonalApiPermissionVO;
import com.oneid.application.personalapi.vo.PersonalApiTokenVO;
import com.oneid.common.constant.TableConstant;
import com.oneid.common.exception.CustomException;
import com.oneid.common.exception.ErrorCode;
import com.oneid.common.utils.*;
import com.oneid.infrastructure.dapr.DaprRedisActuator;
import com.oneid.infrastructure.dapr.DaprSqlActuator;
import com.oneid.infrastructure.personalapi.dataobject.*;
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
    @Autowired
    private DaprRedisActuator daprRedisActuator;
    @Autowired
    private EncryptionService encryptionService;

    public ResponseEntity getAllPermissions() {
        String selectAllPermissionsSql = "select * from " + TableConstant.PERSONAL_API_PERMISSION_TABLE + " where 1 = ?";
        List<String> params = new ArrayList<>();
        params.add("1");
        List<PersonalApiPermissionDO> result = daprSqlActuator.query(selectAllPermissionsSql, params, PersonalApiPermissionDO.class);
        log.info("getAllPermissions size:{}, list:{}", result.size(), result.toString());
        return ResultUtil.result(HttpStatus.OK, "success", convertPermissionDoToPermissionVo(result));
    }

    public ResponseEntity getPersonalApiTokensByUserId(String userId) {
        String selectTokensByUserIdSql = "select * from " + TableConstant.PERSONAL_API_TOKEN_TABLE + " where user_id = ?";
        List<String> params = new ArrayList<>();
        params.add(userId);
        List<PersonalApiTokenDO> tokens = daprSqlActuator.query(selectTokensByUserIdSql, params, PersonalApiTokenDO.class);
        List<PersonalApiTokenVO> result = null;
        try {
            result = convertPersonalTokensDoToPersonalTokensVo(tokens);
        } catch (Exception e) {
            return ResultUtil.result(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, null, null);
        }
        return ResultUtil.result(HttpStatus.OK, "success", result);
    }

    public void deletePersonalApiToken(PersonalApiTokenIdDTO personalApiTokenIdDTO, UserInfoDTO userInfoDTO) {
        if (!checkEmailToken(personalApiTokenIdDTO.getEmailToken(), userInfoDTO.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_TOKEN_ERROR, "emailToken invalid");
        }
        String deletePersonalApiTokenSql = "delete from " + TableConstant.PERSONAL_API_TOKEN_TABLE + " where id = ?";
        List<String> params = new ArrayList<>();
        params.add(personalApiTokenIdDTO.getId());
        boolean result = daprSqlActuator.exeSql(deletePersonalApiTokenSql, params);
        if (!result) {
            throw new CustomException(ErrorCode.SQL_OPT_ERROR);
        }
        log.info("delete personal api token success, id: {}", personalApiTokenIdDTO.getId());
    }

    public String createPersonalApiToken(PersonalApiTokenDTO personalApiTokenDTO, UserInfoDTO userInfoDTO) {
        if (!checkEmailToken(personalApiTokenDTO.getEmailToken(), userInfoDTO.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_TOKEN_ERROR, "emailToken invalid");
        }
        if (!checkPersonalTokenNameLegal(personalApiTokenDTO.getName(), userInfoDTO.getUserId())) {
            throw new CustomException(ErrorCode.TOKEN_NAME_REPEATED);
        }
        if (!countUserTokens(userInfoDTO.getUserId())) {
            throw new CustomException(ErrorCode.TOTAL_TOKEN_EXCEED);
        }
        String createTokenSql = "INSERT INTO " + TableConstant.PERSONAL_API_TOKEN_TABLE + """
                 (
                    id, token, name, permission_ids, user_id, user_name, expire_at, create_at, update_at
                ) VALUES (
                    UUID(), ?, ?, ?, ?, ?, ?, NOW(), NOW()
                )
                """;
        List<String> params = new ArrayList<>();
        EncryptTokenDTO encryptTokenDTO = generateUniqueEncryptedToken();
        String token = encryptTokenDTO.getToken();
        String encryptToken = encryptTokenDTO.getEncryptedToken();
        params.add(encryptToken);
        params.add(personalApiTokenDTO.getName());
        params.add(personalApiTokenDTO.getPermissionIds());
        params.add(userInfoDTO.getUserId());
        params.add(userInfoDTO.getUsername());
        Long expireTime = System.currentTimeMillis() + personalApiTokenDTO.getDayNum() * 24 * 60 * 60 * 1000;
        params.add(String.valueOf(expireTime));
        boolean result = daprSqlActuator.exeSql(createTokenSql, params);
        if (!result) {
            throw new CustomException(ErrorCode.SQL_OPT_ERROR);
        }
        log.info("create personal api token success, param: {}", params);
        return token;
    }

    private boolean countUserTokens(String userId) {
        String countUserTokensSql = "select count(*) as total from " + TableConstant.PERSONAL_API_TOKEN_TABLE + " where user_id = ?";
        // 准备参数列表
        List<String> params = new ArrayList<>();
        params.add(userId);

        // 执行查询
        List<CountApiTokenDO> result = daprSqlActuator.query(countUserTokensSql, params, CountApiTokenDO.class);

        // 处理结果
        if (result != null && !result.isEmpty()) {
            return result.get(0).getTotal() <= 20; // 获取第一个(也是唯一一个)结果
        }
        return true;
    }

    private boolean checkPersonalTokenNameLegal(String tokenName, String userId) {
        String selectPersonalTokenByNameSql = "select * from " + TableConstant.PERSONAL_API_TOKEN_TABLE
                + " where user_id = ? and name = ?";
        List<String> params = new ArrayList<>();
        params.add(userId);
        params.add(tokenName);
        List<PersonalApiTokenDO> tokens = daprSqlActuator.query(selectPersonalTokenByNameSql, params, PersonalApiTokenDO.class);
        return tokens == null || tokens.isEmpty();
    }

    public void updatePersonalApiToken(PersonalApiTokenDetailDTO personalApiTokenDetailDTO, UserInfoDTO userInfoDTO) {
        if (!checkEmailToken(personalApiTokenDetailDTO.getEmailToken(), userInfoDTO.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_TOKEN_ERROR, "emailToken invalid");
        }
        if (personalApiTokenDetailDTO.getDayNum() == null) {
            updatePersonalApiTokenWithoutDayNum(personalApiTokenDetailDTO);
        } else {
            updatePersonalApiTokenWithDayNum(personalApiTokenDetailDTO);
        }
    }

    private void updatePersonalApiTokenWithoutDayNum(PersonalApiTokenDetailDTO personalApiTokenDetailDTO) {
        String updatePersonalApiTokenSql = "update " + TableConstant.PERSONAL_API_TOKEN_TABLE +
                " set name = ? and permission_ids = ? where id = ?";
        List<String> params = new ArrayList<>();
        params.add(personalApiTokenDetailDTO.getName());
        params.add(personalApiTokenDetailDTO.getPermissionIds());
        params.add(personalApiTokenDetailDTO.getId());
        boolean result = daprSqlActuator.exeSql(updatePersonalApiTokenSql, params);
        if (!result) {
            throw new CustomException(ErrorCode.SQL_OPT_ERROR);
        }
        log.info("update personal api token  success, params:{}", params);
    }

    private void updatePersonalApiTokenWithDayNum(PersonalApiTokenDetailDTO personalApiTokenDetailDTO) {
        Long expireTime = System.currentTimeMillis() + personalApiTokenDetailDTO.getDayNum() * 24 * 60 * 60 * 1000;
        String updatePersonalApiTokenSql = "update " + TableConstant.PERSONAL_API_TOKEN_TABLE +
                " set name = ? and permission_ids = ? and expire_at = ? where id = ?";
        List<String> params = new ArrayList<>();
        params.add(personalApiTokenDetailDTO.getName());
        params.add(personalApiTokenDetailDTO.getPermissionIds());
        params.add(String.valueOf(expireTime));
        params.add(personalApiTokenDetailDTO.getId());
        boolean result = daprSqlActuator.exeSql(updatePersonalApiTokenSql, params);
        if (!result) {
            throw new CustomException(ErrorCode.SQL_OPT_ERROR);
        }
        log.info("update personal api token success, params:{}", params);
    }

    public String refreshPersonApiToken(PersonalApiTokenIdDTO personalApiTokenIdDTO, UserInfoDTO userInfoDTO) {
        if (!checkEmailToken(personalApiTokenIdDTO.getEmailToken(), userInfoDTO.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_TOKEN_ERROR, "emailToken invalid");
        }
        String refreshTokenSql = "update " + TableConstant.PERSONAL_API_TOKEN_TABLE
                + " set token = ? where id = ?";
        List<String> params = new ArrayList<>();
        EncryptTokenDTO encryptTokenDTO = generateUniqueEncryptedToken();
        String token = encryptTokenDTO.getToken();
        String encryptToken = encryptTokenDTO.getEncryptedToken();
        params.add(encryptToken);
        params.add(personalApiTokenIdDTO.getId());
        boolean result = daprSqlActuator.exeSql(refreshTokenSql, params);
        if (!result) {
            throw new CustomException(ErrorCode.SQL_OPT_ERROR);
        }
        log.info("refresh personal api token success, param: {}", params);
        return token;
    }

    public List<String> selectPermissionNameByPermissionIds(String permissionIds) {
        if (StringUtils.isBlank(permissionIds) || !checkPermissionIds(permissionIds)) {
            log.error("permissionNames error, permissions id:{}", permissionIds);
            throw new CustomException(ErrorCode.PERMISSION_ID_ERROR);
        }
        String selectPermissionNameSql = "select * from " + TableConstant.PERSONAL_API_PERMISSION_TABLE
                + " where id in (?)";
        List<String> params = new ArrayList<>();
        params.add(permissionIds);
        List<PersonalApiPermissionDO> personalApiPermissionDOs = daprSqlActuator.query(selectPermissionNameSql, params, PersonalApiPermissionDO.class);
        if (personalApiPermissionDOs == null || personalApiPermissionDOs.isEmpty()) {
            return new ArrayList<>();
        } else {
            return personalApiPermissionDOs.stream().map(PersonalApiPermissionDO::getName).collect(Collectors.toList());
        }
    }

    public List<PersonalApiTokenDO> selectPersonalApiTokensByToken(String encryptToken) {
        String selectPersonalApiTokenSql = "select * from " + TableConstant.PERSONAL_API_TOKEN_TABLE
                + " where token = ? ";
        List<String> params = new ArrayList<>();
        params.add(encryptToken);
        return daprSqlActuator.query(selectPersonalApiTokenSql, params, PersonalApiTokenDO.class);
    }

    public boolean checkPermissionIds(String permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return false;
        }
        List<String> idList = CharacterUtil.csvToStringList(permissionIds);
        if (idList.isEmpty()) {
            return false;
        }
        String checkPermissionIdsSql = "SELECT COUNT(*) = ? AS all_ids_exist FROM " +
                TableConstant.PERSONAL_API_PERMISSION_TABLE + " WHERE id IN (?) ";
        List<String> params = new ArrayList<>();
        params.add(String.valueOf(idList.size()));
        params.add(permissionIds);
        List<CheckPermissionDO> ids = daprSqlActuator.query(checkPermissionIdsSql, params, CheckPermissionDO.class);
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        return ids.get(0).getAllIdsExist();
    }

    public ResponseEntity checkToken(CheckTokenDTO checkTokenDTO) {
        String encryptToken = null;
        try {
            encryptToken = encryptionService.encrypt(checkTokenDTO.getToken());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.ENCRYPT_TOKEN_ERROR);
        }
        List<PersonalApiTokenDO> tokens = selectPersonalApiTokensByToken(encryptToken);
        if (tokens == null || tokens.isEmpty()) {
            throw new CustomException(ErrorCode.TOKEN_ERROR);
        }
        PersonalApiTokenDO personalApiTokenDO = tokens.get(0);
        List<String> permissionIds = CharacterUtil.csvToStringList(personalApiTokenDO.getPermissionIds());
        boolean permissionCheckPass = permissionIds.stream()
                .map(this::selectPersonalApiUrlsByPermissionId)
                .filter(urls -> urls != null && !urls.isEmpty())  // 同时检查非null和非空
                .flatMap(List::stream)
                .anyMatch(url -> url.getUri().equals(checkTokenDTO.getUrl()));
        if (permissionCheckPass) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", personalApiTokenDO.getUserId());
            return ResultUtil.result(HttpStatus.OK, "success", jsonObject);
        } else {
            throw new CustomException(ErrorCode.TOKEN_ERROR);
        }
    }

    public List<PersonalApiUrlDO> selectPersonalApiUrlsByPermissionId(String permissionId) {
        String selectPersonalApiUrlSql = "select * from " + TableConstant.PERSONAL_API_URL_TABLE
                + " where permission_id = ?";
        List<String> params = new ArrayList<>();
        params.add(permissionId);
        return daprSqlActuator.query(selectPersonalApiUrlSql, params, PersonalApiUrlDO.class);
    }

    private boolean checkTokenExist(String token) {
        List<PersonalApiTokenDO> tokens = selectPersonalApiTokensByToken(token);
        return tokens != null && !tokens.isEmpty();
    }

    private List<PersonalApiTokenVO> convertPersonalTokensDoToPersonalTokensVo(List<PersonalApiTokenDO> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return new ArrayList<>();
        }
        return tokens.stream()
                .map(this::convertPersonalTokenDoToPersonalTokenVo)
                .collect(Collectors.toList());
    }

    private PersonalApiTokenVO convertPersonalTokenDoToPersonalTokenVo(PersonalApiTokenDO token) {
        PersonalApiTokenVO vo = new PersonalApiTokenVO();
        vo.setId(token.getId());
        vo.setName(token.getName());
        vo.setExpireAt(DateUtil.convertTimestampToSlashFormat(token.getExpireAt()));
        vo.setPermissionIds(token.getPermissionIds());
        vo.setCreateAt(DateUtil.convertISO8601ToSlashFormat(token.getCreateAt()));
        vo.setUpdateAt(DateUtil.convertISO8601ToSlashFormat(token.getUpdateAt()));
        List<String> permissionNames = selectPermissionNameByPermissionIds(token.getPermissionIds());
        vo.setPermissionNames(CharacterUtil.listToCsv(permissionNames));
        return vo;
    }

    private List<PersonalApiPermissionVO> convertPermissionDoToPermissionVo(List<PersonalApiPermissionDO> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return new ArrayList<>();
        }
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

    private Boolean checkEmailToken(String emailToken, String email) {
        return false;
//        String emailTokenKey = RedisUtil.getEmailTokenRedisKey(email.toLowerCase());
//        String rightToken = daprRedisActuator.getState(emailTokenKey);
//        if (rightToken == null || !rightToken.equals(emailToken)) {
//            return false;
//        } else {
//            daprRedisActuator.deleteState(emailTokenKey);
//            return true;
//        }
    }

    public EncryptTokenDTO generateUniqueEncryptedToken() {
        int maxAttempts = 10; // 防止无限循环
        int attempts = 0;

        while (attempts < maxAttempts) {
            String token = CharacterUtil.generate32UUID();
            try {
                String encryptToken = encryptionService.encrypt(token);
                if (!checkTokenExist(encryptToken)) {
                    return new EncryptTokenDTO(token, encryptToken);
                }
            } catch (Exception e) {
                throw new CustomException(ErrorCode.ENCRYPT_TOKEN_ERROR);
            }
            attempts++;
        }

        throw new CustomException(ErrorCode.ENCRYPT_TOKEN_ERROR);
    }
}
