package cumt.miirso.eduagentx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cumt.miirso.eduagentx.dto.req.ClassCreateReqDTO;
import cumt.miirso.eduagentx.entity.ClassDO;
import cumt.miirso.eduagentx.mapper.ClassMapper;
import cumt.miirso.eduagentx.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassMapper classMapper;

    @Override
    public void create(ClassCreateReqDTO requestParam) {
        // 检查班级名称是否已存在
        LambdaQueryWrapper<ClassDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClassDO::getName, requestParam.getName());
        
        if (classMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("班级名称已存在：" + requestParam.getName());
        }
        
        ClassDO classDO = new ClassDO();
        classDO.setName(requestParam.getName());
        classDO.setMajorId(requestParam.getMajorId());
        classMapper.insert(classDO);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createBatch(List<ClassCreateReqDTO> classDTOList) {
        if (classDTOList == null || classDTOList.isEmpty()) {
            return;
        }
        
        // 1. 收集所有班级名称，检查列表内部是否有重复
        Set<String> classNameSet = new HashSet<>();
        for (ClassCreateReqDTO dto : classDTOList) {
            if (!classNameSet.add(dto.getName())) {
                throw new RuntimeException("请求中存在重复的班级名称：" + dto.getName());
            }
        }
        
        // 2. 检查数据库中是否已存在这些班级名称
        LambdaQueryWrapper<ClassDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ClassDO::getName, classNameSet);
        List<ClassDO> existingClasses = classMapper.selectList(queryWrapper);
        
        if (!existingClasses.isEmpty()) {
            StringBuilder errorMsg = new StringBuilder("以下班级名称已存在：");
            for (ClassDO existingClass : existingClasses) {
                errorMsg.append(existingClass.getName()).append(", ");
            }
            throw new RuntimeException(errorMsg.substring(0, errorMsg.length() - 2));
        }
        
        // 3. 批量插入班级
        for (ClassCreateReqDTO dto : classDTOList) {
            ClassDO classDO = new ClassDO();
            classDO.setName(dto.getName());
            classDO.setMajorId(dto.getMajorId());
            classMapper.insert(classDO);
        }
    }
}
