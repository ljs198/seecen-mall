package com.seecen.mall.core.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seecen.mall.common.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 *
 * 继承 BaseMapper 之后，基础的 CRUD 都由 MyBatis-Plus 提供：
 *   - insert(user)        新增
 *   - updateById(user)    按 ID 修改
 *   - deleteById(id)      按 ID 删除（逻辑删除，由 @TableLogic 自动处理）
 *   - selectById(id)      按 ID 查询
 *   - selectList(...)     按条件查询列表
 *
 * 这里只需要继承 BaseMapper<SysUser> 即可，无需手写 SQL。
 */
@Mapper
public interface UserMapper extends BaseMapper<SysUser> {

}